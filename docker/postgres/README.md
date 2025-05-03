# PostgreSQL Docker Setup

This directory contains a Docker Compose setup for PostgreSQL and pgAdmin, along with a convenient script to manage the containers.

## Quick Start

The `pg` script provides a simple interface to manage the PostgreSQL and pgAdmin containers:

```bash
# Start PostgreSQL and pgAdmin
./pg start

# Stop the containers
./pg stop

# Stop the containers and remove volumes (clean up)
./pg clean
```

## Script Commands

The `pg` script supports the following commands:

- `start`: Starts the PostgreSQL and pgAdmin containers in detached mode and displays their status.
- `stop`: Stops the running containers.
- `clean`: Stops the containers and removes all associated volumes, effectively cleaning up all data.

## Services

### PostgreSQL

- **Image**: postgres:17
- **Port**: 15432 (mapped to container port 5432)
- **Credentials**:
  - Username: postgres
  - Password: password
- **Data**: Stored in a Docker volume named `postgres`
- **Initialization**: The database is initialized with the SQL script at `./db/docker_postgres_init.sql`

### pgAdmin

- **Image**: dpage/pgadmin4:latest
- **Port**: 15433 (mapped to container port 80)
- **Web Interface**: http://localhost:15433
- **Credentials**:
  - Email: admin@example.com
  - Password: admin
- **Data**: Stored in a Docker volume named `pgadmin`
- **Configuration**: Pre-configured with server connection details in `./db/docker_pgadmin_servers.json`

## Connecting to PostgreSQL

### From Host Machine

You can connect to PostgreSQL using any PostgreSQL client with these details:
- Host: localhost
- Port: 15432
- Username: postgres
- Password: password

### Using pgAdmin

1. Start the containers with `./pg start`
2. Open http://localhost:15433 in your browser to access pgAdmin
3. Log in with email `admin@example.com` and password `admin`
4. The PostgreSQL server should be pre-configured and available in the server list

### From Docker Containers or Applications

If you're connecting from another Docker container in the same network, use:
- Host: postgres
- Port: 5432
- Username: postgres
- Password: password

## Notes

- The containers are configured to restart automatically unless explicitly stopped.
- To completely reset the database and pgAdmin, use the `clean` command.
