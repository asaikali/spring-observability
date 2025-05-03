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

The `pg` script supports the following commands:

- `start`: Starts the PostgreSQL and pgAdmin containers in detached mode and displays their status.
- `status`: Displays container status in a tabular format and provides compact connection information including ports, credentials, JDBC connection URL, and psql command for PostgreSQL and pgAdmin.
- `stop`: Stops the running containers.
- `clean`: Stops the containers and removes all associated volumes, effectively cleaning up all data.
- `fix`: Detects port conflicts and automatically shuts down conflicting containers, allowing you to start your PostgreSQL containers without changing directories.

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
- Database: postgres

For Java applications using JDBC, use the following connection URL:
```
jdbc:postgresql://localhost:15432/postgres
```

For command-line access using psql, use:
```
psql -h localhost -p 15432 -U postgres -d postgres
```

You can also run `./pg status` to view all connection details including the JDBC URL and psql command.

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

## Configuration

The PostgreSQL setup is configured with hardcoded values in the docker-compose.yaml file:

- PostgreSQL image: postgres:17
- PostgreSQL port: 15432
- pgAdmin image: dpage/pgadmin4:9.3
- pgAdmin port: 15433

All other configuration values (usernames, passwords, etc.) are also hardcoded in the Docker Compose file.

If you need to customize the configuration, you can edit the docker-compose.yaml file directly.

## Notes

- The containers are configured to restart automatically unless explicitly stopped.
- To completely reset the database and pgAdmin, use the `clean` command.
- All configuration values are hardcoded in the docker-compose.yaml file for simplicity and clarity.

## Port Conflict Detection

The `pg` script includes built-in port conflict detection when starting containers. If ports 15432 (PostgreSQL) or 15433 (pgAdmin) are already in use by other containers, the script will:

1. Detect the conflict and display a concise error message
2. Show which containers are using the conflicting ports
3. Provide a simple command to fix the issue (`pg fix`)
4. Offer a direct Docker Compose command as an alternative

Example output when a port conflict is detected:

```
== Starting PostgreSQL Containers ==

> Running docker compose up...
✗ Failed to start PostgreSQL containers - Port conflict detected
! Port 15432 in use by: ai_postgres
! Port 15433 in use by: ai_pgadmin
> To fix, run: pg fix
> Or run: docker compose -f /Users/adib/dev/asaikali/spring-ai-zero-to-hero/docker/pgvector/docker-compose.yaml down
```

### Using the Fix Command

The easiest way to resolve port conflicts is to use the `pg fix` command:

```bash
./pg fix
```

Example output:

```
== Fixing Port Conflicts ==
! Port 15432 in use by: ai_postgres
! Port 15433 in use by: ai_pgadmin
> Shutting down: ai_postgres
> Shutting down: ai_pgadmin
✓ Port conflicts resolved. Run 'pg start' to start PostgreSQL
```

This command automatically detects and shuts down conflicting containers without requiring you to change directories.
