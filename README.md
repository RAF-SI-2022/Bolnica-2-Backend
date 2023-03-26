# Bolnica 2

init commit
## Pokretanje backenda :
```bash
docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d postgres
docker compose up
```
