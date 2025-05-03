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
- `status`: Displays detailed container status in a tabular format and provides comprehensive connection information including ports, credentials, JDBC connection URL, and psql command for PostgreSQL and pgAdmin.
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

## Notes

- The containers are configured to restart automatically unless explicitly stopped.
- To completely reset the database and pgAdmin, use the `clean` command.

## Port Conflict Detection

The `pg` script includes built-in port conflict detection when starting containers. If ports 15432 (PostgreSQL) or 15433 (pgAdmin) are already in use by other containers, the script will:

1. Detect the conflict and provide information about the conflicting containers
2. Show which Docker Compose project and file is using the conflicting ports (if available)
3. Provide direct commands to resolve the conflict without changing directories
4. Suggest using the `pg fix` command to automatically resolve the conflict

Example output when a port conflict is detected:

```
== Starting PostgreSQL Containers ==

> Running docker compose up...
✗ Failed to start PostgreSQL containers

-- Port Conflict Detection --

> Checking for port conflicts...
! Port conflict detected: Port 15432 is already in use by container: ai_postgres

-- PostgreSQL Container Details --

CONTAINER ID   NAMES        IMAGE                  PORTS
1e61ee68f163   ai_postgres  pgvector/pgvector:pg16 0.0.0.0:15432->5432/tcp
> This container belongs to Docker Compose project: pgvector
> Docker Compose file: /Users/adib/dev/asaikali/spring-ai-zero-to-hero/docker/pgvector/docker-compose.yaml

! Port conflict detected: Port 15433 is already in use by container: ai_pgadmin

-- pgAdmin Container Details --

CONTAINER ID   NAMES        IMAGE                   PORTS
afd6e1e5033c   ai_pgadmin   dpage/pgadmin4:latest   443/tcp, 0.0.0.0:15433->80/tcp
> This container belongs to Docker Compose project: pgvector
> Docker Compose file: /Users/adib/dev/asaikali/spring-ai-zero-to-hero/docker/pgvector/docker-compose.yaml

-- Resolution Steps --

> To resolve this conflict, you can either:
  1. Stop the conflicting containers using 'docker stop <container_name>'
  2. Modify the docker-compose.yaml file to use different ports

-- Docker Compose Shutdown Commands --

> To shut down the conflicting PostgreSQL container using Docker Compose:
  docker compose -f /Users/adib/dev/asaikali/spring-ai-zero-to-hero/docker/pgvector/docker-compose.yaml down
> Or simply run: pg fix
```

### Using the Fix Command

The easiest way to resolve port conflicts is to use the `pg fix` command:

```bash
./pg fix
```

This command will:
1. Automatically detect containers causing port conflicts
2. Find their associated Docker Compose files
3. Shut them down directly without requiring you to change directories
4. Provide feedback on the actions taken

After running `pg fix`, you can then run `pg start` to start your PostgreSQL containers.
