SELECT count(usename) from pg_catalog.pg_user where usename in ('$POSTGRES_USER', '$spring.datasource.username', '$spring.liquibase.user');