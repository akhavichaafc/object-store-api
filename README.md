# object-store-api

AAFC DINA object-store implementation.

See DINA object-store [specification](https://github.com/DINA-Web/object-store-specs).

## Database
This project requires a PostgreSQL database to run and to run integration tests.

## Minio
A [Minio](https://min.io/) service is also required to run the project (not required for testing).

## To Run

For testing purpose or local development a [Docker Compose](https://docs.docker.com/compose/) example file is available in the `local` folder.
Please note that the jar running in the container will be the jar currently available in the `target` folder.

Create a new docker-compose file from the example:

```
cp local/docker-compose.yml.example docker-compose.yml
```

Start the app (default port is 8081):

```
docker-compose up
```


## Testing
For testing purpose or local development a [Docker Compose](https://docs.docker.com/compose/) file can be used:



To run the integration tests:

```
 mvn verify -Dspring.datasource.url=jdbc:postgresql://localhost/object_store_test -Dspring.datasource.username=test -Dspring.datasource.password=test
```

## IDE

`object-store-api` requires [Project Lombok](https://projectlombok.org/) to be setup in your IDE.

Setup documentation for Eclipse: <https://projectlombok.org/setup/eclipse>

## Additional License
`CycleAvoidingMappingContext` class is licensed under the Apache License, Version 2.0. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0