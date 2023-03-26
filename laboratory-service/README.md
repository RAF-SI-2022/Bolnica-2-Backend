# Laboratory Service - Servis za Laboratoriju

## O servisu:

Servis za laboratoriju

### Pre pokretanja servisa, skinuti i instalirati docker i pokrenuti sledeću komandu:

```bash
docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d postgres
```

```
docker network create local_network
docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d --network local_network postgres
docker run -d -p 8083:8083 --network local_network laboratory
```

