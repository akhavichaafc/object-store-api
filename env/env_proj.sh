# env_proj.sh
export APP='object_store_api'

export APP_DB="dina_db"
export APP_DB_DATA="/var/lib/postgresql/data/pgdata"
export APP_DB_MOUNT_PATH="/var/lib/postgresql/data"
export APP_DB_PASSWORD="changeme"
export APP_DB_SCHEMA="dina_sch"
export APP_DB_URL="jdbc:postgresql://database/${APP_DB}?currentSchema=${APP_DB_SCHEMA}"
export APP_DB_USER="app_user"

export APP_PORT_INT=8080
export APP_PORT_EXT=8080

export AUTH_DB="keycloak"
export AUTH_HOST="database"
export AUTH_DB_PASSWORD="password"
export AUTH_DB_USER="keycloak"
export AUTH_DB_VENDOR="POSTGRES"
export AUTH_IMG
export AUTH_PASSWORD="admin"
export AUTH_USER="admin"

export LIQUIBASE_CONTEXTS="schema-change"
export LIQUIBASE_DB_PASSWORD="changeme2"
export LIQUIBASE_DB_USER="migr_role"

export MINIO_HOST="migr_role"

export OC_CLIENT_ID="auth0_webapp"
export OC_SECRET="484317a9-bf9d-4c29-ba4e-1e45d2ae65ce"
export ISSUER_BASE_URL="http://localhost:8080/auth/realms/master/protocol/openid-connect/token"
