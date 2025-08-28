# CrediYa - Entorno local (PostgreSQL)

Este entorno levanta **PostgreSQL 16** (y opcionalmente **pgAdmin**) para desarrollo local del microservicio **ms-autenticacion**.

## Requisitos
- Docker / Docker Compose

## Archivos
- `docker-compose.yml`: orquesta los contenedores.
- `.env`: variables (DB, credenciales, pgAdmin).
- `sql/01-auth-schema.sql`: script de esquema (roles/usuarios).

> Los scripts en `sql/` se aplican **automáticamente la primera vez** que arranca el contenedor.

## Levantar entorno
```bash
cd local_enviroment
docker compose up -d
