"""
MySQL database connection for ${projectName}
Uses SQLAlchemy for ORM and pymysql for MySQL driver
"""

import logging
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, declarative_base
from sqlalchemy.exc import SQLAlchemyError
from config.config import Config

logger = logging.getLogger(__name__)

# SQLAlchemy setup
Base = declarative_base()
engine = None
SessionLocal = None


def init_db(app=None):
    """Initialize database connection"""
    global engine, SessionLocal

    config = Config()

    try:
        # Create engine
        engine = create_engine(
            config.database_url,
            echo=config.debug,
            pool_pre_ping=True,  # Verify connections before using
            pool_size=10,
            max_overflow=20
        )

        # Create session factory
        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

        # Test connection
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
            logger.info("MySQL connection established successfully")

        # Create tables (if you have models)
        # Base.metadata.create_all(bind=engine)

    except SQLAlchemyError as e:
        logger.error(f"Failed to connect to MySQL: {e}")
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
        logger.info("MySQL connection closed")


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
