# PostgreSQL Docker Setup

This directory contains a Docker Compose setup for PostgreSQL and pgAdmin, along with a convenient script to manage the containers.

## Quick Start

The `pg` script provides a simple interface to manage the PostgreSQL and pgAdmin containers:

```bash
# Start PostgreSQL and pgAdmin
./pg start

# Check the status of containers and display connection information
./pg status

# Stop the containers
./pg stop

# Stop the containers and remove volumes (clean up)
./pg clean

# Fix port conflicts by shutting down conflicting containers
./pg fix
```

## Script Commands

- `start`: Starts the PostgreSQL and pgAdmin containers.
- `status`: Shows container status and connection information.
- `stop`: Stops the running containers.
- `clean`: Stops the containers and removes all associated volumes.
- `fix`: Detects and resolves port conflicts automatically.

## Services

### PostgreSQL
- Port: 15432
- Username: postgres
- Password: password

### pgAdmin
- URL: http://localhost:15433
- Email: admin@example.com
- Password: admin

## Connecting to PostgreSQL

### From Host Machine
- JDBC URL: `jdbc:postgresql://localhost:15432/postgres`
- PSQL: `psql -h localhost -p 15432 -U postgres -d postgres`

### From Docker Containers
- Host: postgres
- Port: 5432
- Username: postgres
- Password: password

## Notes
- Run `./pg status` to view all connection details.
- The `fix` command helps resolve port conflicts automatically.
- All configuration values are hardcoded in the docker-compose.yaml file.
