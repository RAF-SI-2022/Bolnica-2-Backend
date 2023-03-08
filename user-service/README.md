# User Service - Korisnički servis

## O servisu:

Korisnički servis za autorizaciju, autentifikaciju i upravljanje korisnicima (CRUD operacije),.

### Pre pokretanja servisa, skinuti i instalirati docker i pokrenuti sledeću komandu:

docker run --name postgresDb -p 5432:5432 -e POSTGRES_USER=student -e POSTGRES_PASSWORD=student -e POSTGRES_DB=postgresDB -d postgres
