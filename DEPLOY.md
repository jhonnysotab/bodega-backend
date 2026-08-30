# Desplegar BodegaPOS gratis (Oracle Cloud + DuckDNS + Vercel/Cloudflare Pages)

Guía paso a paso para poner en producción, sin pagar nada:
- **Backend + MySQL**: en una VM "Always Free" de Oracle Cloud, con Docker.
- **HTTPS**: Caddy (certificado automático de Let's Encrypt) + un subdominio gratis de DuckDNS.
- **Frontend**: Vercel o Cloudflare Pages (elige uno).

Todo lo que menciona este documento (Dockerfile, docker-compose, schema SQL, seed, Caddyfile)
ya está en el repo, listo para usar. Lo único que falta es que tú ejecutes estos pasos en tu
cuenta de Oracle Cloud, porque requieren tu tarjeta/identidad y no puedo hacerlo por ti.

---

## 1. Crear la cuenta y la VM en Oracle Cloud

1. Entra a https://signup.oraclecloud.com/ y crea una cuenta "Always Free". Te van a pedir
   tarjeta para verificar identidad — **no te cobra** mientras te quedes en los recursos "Always
   Free" (los que vamos a usar acá).
2. Una vez dentro de la consola, ve a **Compute → Instances → Create Instance**.
3. Configura:
   - **Image**: Canonical Ubuntu (la versión LTS más reciente que ofrezca, ej. 22.04 o 24.04).
   - **Shape**: cambia a "Ampere" (ARM) y elige `VM.Standard.A1.Flex` — es la que da hasta 4
     OCPU / 24 GB RAM **gratis para siempre**. Dejar 2 OCPU / 12 GB alcanza de sobra para esto.
   - **Networking**: deja la VCN por defecto, y marca **"Assign a public IPv4 address"**.
   - **SSH keys**: deja que Oracle genere un par de llaves y **descarga la privada** (o pega tu
     llave pública si ya tienes una). La vas a necesitar para conectarte.
4. Crea la instancia y espera a que quede "Running". Anota la **IP pública**.
5. **Reserva esa IP como estática** (Networking → IP Management → hazla "Reserved" en vez de
   efímera) — así no cambia si reinicias la VM, y DuckDNS solo hay que configurarlo una vez.

### Abrir los puertos 80 y 443 (paso que casi todos olvidan)

Hay **dos** firewalls que bloquean el tráfico por defecto en Oracle Cloud, hay que abrir ambos:

1. **Security List de la VCN** (en la consola web): Networking → Virtual Cloud Networks → tu VCN
   → Security Lists → Default Security List → Add Ingress Rules:
   - Source CIDR `0.0.0.0/0`, protocolo TCP, destination port `80`
   - Source CIDR `0.0.0.0/0`, protocolo TCP, destination port `443`
2. **iptables dentro de la VM** (las imágenes Ubuntu de Oracle vienen con reglas que bloquean
   todo menos el puerto 22). Conéctate por SSH y corre:
   ```bash
   sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
   sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
   sudo netfilter-persistent save   # si no existe el comando: sudo apt install iptables-persistent -y
   ```

---

## 2. Conectarte por SSH e instalar Docker

```bash
ssh -i /ruta/a/tu-llave-privada.key ubuntu@<IP_PUBLICA>

# Instalar Docker + el plugin de compose
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker   # o cierra y vuelve a abrir la sesión SSH

docker --version
docker compose version
```

---

## 3. DuckDNS (dominio gratis para el backend)

1. Entra a https://www.duckdns.org/ y entra con tu cuenta de GitHub/Google (gratis, sin tarjeta).
2. Crea un subdominio, por ejemplo `mi-bodega` → te queda `mi-bodega.duckdns.org`.
3. En "current ip", pega la **IP pública reservada** de tu VM (paso 1) y dale "update ip".
   Como la IP es estática, con esto ya queda — no hace falta el script de auto-actualización de
   DuckDNS.
4. Confirma que resuelve: `ping mi-bodega.duckdns.org` desde tu compu debería responder la IP
   de la VM.

---

## 4. Clonar el repo y configurar

```bash
git clone <URL-de-tu-repo-bodegas> bodegas
cd bodegas/deploy
cp .env.example .env
nano .env   # rellena DB_PASSWORD, JWT_SECRET, DUCKDNS_DOMAIN, CORS_ALLOWED_ORIGINS
```

- `DB_PASSWORD` / `JWT_SECRET`: invéntate valores largos y random (ej. `openssl rand -hex 32`).
- `DUCKDNS_DOMAIN`: el que creaste arriba, ej. `mi-bodega.duckdns.org` (sin `https://`).
- `CORS_ALLOWED_ORIGINS`: la URL que te va a dar Vercel/Cloudflare Pages en el paso 6 (por ahora
  puedes dejar un valor cualquiera y volver a editarlo después — solo hay que reiniciar el
  contenedor `backend` cuando lo cambies).
- `ANTHROPIC_API_KEY`: déjalo vacío por ahora si no la tienes (la importación de productos por
  foto de factura simplemente no va a funcionar hasta que la pongas).

## 5. Levantar todo

```bash
docker compose up -d --build
```

La primera vez, MySQL corre automáticamente `schema_completo.sql` y `seed_inicial.sql` (crea
todas las tablas + una empresa "Mi Bodega" + un usuario admin). Esto solo pasa si el volumen de
MySQL está vacío — en despliegues siguientes no se vuelve a ejecutar.

Espera ~30–60 segundos y verifica:

```bash
docker compose ps                      # los 3 servicios deben decir "Up" / "healthy"
docker compose logs -f caddy           # busca que haya obtenido el certificado sin errores
curl https://mi-bodega.duckdns.org/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"bodega123"}'
```

Si responde con un `token`, el backend + base de datos + HTTPS ya están andando.

**Cambia la contraseña del admin de inmediato** (usuario `admin` / contraseña `bodega123` es
solo para el primer login) — inicia sesión en el frontend una vez desplegado y usa
Usuarios → editar → cambiar contraseña.

---

## 6. Desplegar el frontend

Antes de compilar, edita `bodega-frontend/src/environments/environment.prod.ts` y pon tu dominio
real:

```ts
export const environment = {
  production: true,
  apiUrl: 'https://mi-bodega.duckdns.org'
};
```

### Opción A: Vercel

1. Sube el repo del frontend a GitHub (si no lo está ya).
2. Entra a https://vercel.com, "Add New Project", importa el repo.
3. Vercel debería detectar `vercel.json` (ya está en el repo) y usar:
   - Build command: `npm run build -- --configuration=production`
   - Output directory: `dist/bodega-frontend/browser`
4. Deploy. Te da una URL tipo `https://tu-proyecto.vercel.app`.

### Opción B: Cloudflare Pages

1. Sube el repo a GitHub.
2. Cloudflare dashboard → Workers & Pages → Create → Pages → conecta el repo.
3. Build command: `npm run build -- --configuration=production` — Output directory:
   `dist/bodega-frontend/browser`.
4. Deploy. El `public/_redirects` ya incluido en el repo hace que las rutas de Angular (ej.
   `/productos` recargando la página) no den 404.

### Último paso: cerrar el círculo de CORS

Copia la URL que te dio Vercel/Cloudflare Pages, vuelve a la VM y actualiza
`deploy/.env` → `CORS_ALLOWED_ORIGINS=https://tu-url-real.vercel.app`, luego:

```bash
docker compose up -d backend   # recrea solo el backend con el nuevo CORS
```

Abre la URL del frontend, inicia sesión con `admin` / `bodega123`, cambia la contraseña, y ya.

---

## Mantenimiento

- **Ver logs**: `docker compose logs -f backend` (o `mysql`, `caddy`).
- **Actualizar el backend** tras un cambio de código: `git pull && docker compose up -d --build backend`.
- **Backups de la base de datos**: el volumen `mysql_data` persiste entre reinicios, pero no es
  un backup. Para un dump manual:
  ```bash
  docker compose exec mysql mysqldump -uroot -p"$DB_PASSWORD" pos_bodegas > backup_$(date +%F).sql
  ```
- **Espacio en disco**: las Always Free VMs de Oracle traen 50–200GB de disco de bloque gratis
  (según la forma elegida) — de sobra para una bodega chica, pero vigila `df -h` de vez en cuando.

## Notas / limitaciones conocidas

- No hay endpoint de registro de empresas — `seed_inicial.sql` crea la única empresa y el único
  usuario admin iniciales. Los demás usuarios se crean desde la pantalla de Usuarios ya logueado.
- La importación de productos por foto de factura necesita `ANTHROPIC_API_KEY` configurada (ver
  sección "Pendientes" en `CLAUDE.md`) — sin eso, el resto de la app funciona normal.
- Se detectó (pero no se corrigió, por estar fuera de esta tarea de despliegue) que el login
  responde el campo `nombreEmpresa` mientras el frontend espera `empresaNombre` — el nombre de la
  empresa en el ticket impreso siempre cae al valor por defecto "BodegaPOS" en vez del real.
