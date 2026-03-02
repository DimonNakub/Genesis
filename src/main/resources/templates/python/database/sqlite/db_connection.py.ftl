"""
SQLite database connection for ${projectName}
Uses SQLAlchemy for ORM and built-in sqlite3 driver
"""

import logging
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, declarative_base
from sqlalchemy.exc import SQLAlchemyError
from pathlib import Path

logger = logging.getLogger(__name__)

# SQLAlchemy setup
Base = declarative_base()
engine = None
SessionLocal = None

# Database file path
DB_FILE = Path(__file__).parent.parent / 'data' / '${projectName}.db'


def init_db(app=None):
    """Initialize SQLite database connection"""
    global engine, SessionLocal

    try:
        # Create data directory if it doesn't exist
        DB_FILE.parent.mkdir(parents=True, exist_ok=True)

        # Create engine
        engine = create_engine(
            f'sqlite:///{DB_FILE}',
            echo=False,  # Set to True for query logging
            connect_args={'check_same_thread': False}  # Allow multi-threading
        )

        # Create session factory
        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

        # Test connection
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
            logger.info(f"SQLite connection established: {DB_FILE}")

        # Create tables (if you have models)
        # Base.metadata.create_all(bind=engine)

    except SQLAlchemyError as e:
        logger.error(f"Failed to connect to SQLite: {e}")
        raise


def get_db_connection():
    """Get database engine"""
    if engine is None:
        init_db()
    return engine


def get_db_session():
    """Get database session"""
    if SessionLocal is None:
        init_db()
    return SessionLocal()


def close_db():
    """Close database connection"""
    global engine
    if engine:
        engine.dispose()
        logger.info("SQLite connection closed")


# Context manager for database sessions
class DatabaseSession:
    """Context manager for database sessions"""

    def __enter__(self):
        self.session = get_db_session()
        return self.session

    def __exit__(self, exc_type, exc_val, exc_tb):
        if exc_type is not None:
            self.session.rollback()
        self.session.close()


# Example usage:
# with DatabaseSession() as session:
#     results = session.execute(text("SELECT * FROM users")).fetchall()
