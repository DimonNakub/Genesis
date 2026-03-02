# PostgreSQL Database Usage Instructions

This document explains how to use PostgreSQL with ${projectName}.

## Configuration

Database settings are configured in `.env` file:

```env
DATABASE_URL=postgresql://postgres:password@localhost:5432/${projectName}
DB_HOST=localhost
DB_PORT=5432
DB_NAME=${projectName}
DB_USER=postgres
DB_PASSWORD=your_password_here
```

## Prerequisites

1. **Install PostgreSQL:**
   - Ubuntu/Debian: `sudo apt-get install postgresql postgresql-contrib`
   - macOS: `brew install postgresql`
   - Windows: Download from https://www.postgresql.org/download/windows/

2. **Start PostgreSQL service:**
   ```bash
   # Linux
   sudo service postgresql start

   # macOS
   brew services start postgresql

   # Windows
   # PostgreSQL service starts automatically
   ```

3. **Create database:**
   ```bash
   # Login to PostgreSQL
   psql -U postgres

   # Create database
   CREATE DATABASE ${projectName};

   # Create user (optional)
   CREATE USER ${projectName}_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE ${projectName} TO ${projectName}_user;

   # Exit
   \q
   ```

## Usage

### Basic Connection

```python
from config.db_connection import get_db_connection, get_db_session

# Get engine
engine = get_db_connection()

# Get session
session = get_db_session()
```

### Using Context Manager

```python
from config.db_connection import DatabaseSession
from sqlalchemy import text

# Execute query with automatic session management
with DatabaseSession() as session:
    result = session.execute(text("SELECT version()"))
    version = result.fetchone()
    print(f"PostgreSQL version: {version[0]}")
```

### Creating Models

```python
from config.db_connection import Base
from sqlalchemy import Column, Integer, String, DateTime
from datetime import datetime

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, nullable=False)
    email = Column(String, unique=True, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

# Create tables
from config.db_connection import engine
Base.metadata.create_all(bind=engine)
```

### CRUD Operations

```python
from config.db_connection import DatabaseSession

# Create
with DatabaseSession() as session:
    new_user = User(username="john_doe", email="john@example.com")
    session.add(new_user)
    session.commit()
    session.refresh(new_user)
    print(f"Created user with ID: {new_user.id}")

# Read
with DatabaseSession() as session:
    users = session.query(User).all()
    for user in users:
        print(f"User: {user.username} ({user.email})")

# Update
with DatabaseSession() as session:
    user = session.query(User).filter_by(username="john_doe").first()
    if user:
        user.email = "newemail@example.com"
        session.commit()

# Delete
with DatabaseSession() as session:
    user = session.query(User).filter_by(username="john_doe").first()
    if user:
        session.delete(user)
        session.commit()
```

### Raw SQL Queries

```python
from sqlalchemy import text
from config.db_connection import DatabaseSession

with DatabaseSession() as session:
    # Execute raw SQL
    result = session.execute(
        text("SELECT * FROM users WHERE username = :username"),
        {"username": "john_doe"}
    )
    user = result.fetchone()
```

## Migrations

For database migrations, consider using Alembic:

```bash
# Install Alembic
pip install alembic

# Initialize Alembic
alembic init alembic

# Create migration
alembic revision --autogenerate -m "Create users table"

# Apply migration
alembic upgrade head
```

## Connection Pooling

The database connection uses connection pooling with these settings:

- **pool_size:** 10 connections
- **max_overflow:** 20 additional connections
- **pool_pre_ping:** True (verifies connections before use)

## Troubleshooting

### Connection refused
- Ensure PostgreSQL is running: `sudo service postgresql status`
- Check if PostgreSQL is listening on the correct port: `sudo netstat -plnt | grep 5432`
- Verify firewall settings

### Authentication failed
- Check username and password in `.env`
- Verify user permissions in PostgreSQL
- Check `pg_hba.conf` for authentication method

### Database does not exist
- Create the database: `createdb ${projectName}`
- Or use `CREATE DATABASE ${projectName};` in psql

### SSL connection error
If you need SSL, update the DATABASE_URL:
```
DATABASE_URL=postgresql://user:pass@host:5432/dbname?sslmode=require
```

## Best Practices

1. **Always use context managers** (DatabaseSession) for automatic session cleanup
2. **Never commit credentials** to version control
3. **Use connection pooling** for better performance
4. **Enable query logging** in development (set echo=True in engine)
5. **Use parameterized queries** to prevent SQL injection
6. **Close sessions properly** to avoid connection leaks

## Resources

- SQLAlchemy Documentation: https://docs.sqlalchemy.org/
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- psycopg2 Documentation: https://www.psycopg.org/docs/

---

Generated by Genesis - ${currentDate}
