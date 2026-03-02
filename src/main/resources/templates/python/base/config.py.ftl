"""
Configuration module for ${projectName}
Loads settings from environment variables
"""

import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()


class Config:
    """Application configuration"""

    # Application Settings
    APP_NAME = "${projectName}"
    ENVIRONMENT = os.getenv("ENVIRONMENT", "development")
    DEBUG = os.getenv("DEBUG", "True").lower() == "true"
    HOST = os.getenv("HOST", "0.0.0.0")
    PORT = int(os.getenv("PORT", <#if has_flask??>"5000"<#elseif has_fastapi??>"8000"<#else>"8000"</#if>))

<#if hasDatabase>
    # Database Settings
<#if has_mongodb??>
    # MongoDB specific settings
    MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/${projectName}")
<#elseif has_redis??>
    # Redis specific settings
    REDIS_URI = os.getenv("REDIS_URI", "redis://localhost:6379/0")
<#else>
    DATABASE_URL = os.getenv("DATABASE_URL")
<#if has_postgresql??>
    # PostgreSQL specific settings
    DB_HOST = os.getenv("DB_HOST", "localhost")
    DB_PORT = int(os.getenv("DB_PORT", "5432"))
    DB_NAME = os.getenv("DB_NAME", "${projectName}")
    DB_USER = os.getenv("DB_USER", "postgres")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "")
<#elseif has_mysql??>
    # MySQL specific settings
    DB_HOST = os.getenv("DB_HOST", "localhost")
    DB_PORT = int(os.getenv("DB_PORT", "3306"))
    DB_NAME = os.getenv("DB_NAME", "${projectName}")
    DB_USER = os.getenv("DB_USER", "root")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "")
<#elseif has_sqlite??>
    # SQLite specific settings (file-based)
    DB_FILE = os.getenv("DB_FILE", "data/${projectName}.db")
</#if>
</#if>

</#if>
<#if hasSecurity>
    # Security Settings
    SECRET_KEY = os.getenv("SECRET_KEY", "change-this-in-production")
<#if has_jwt??>
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "change-this-in-production")
    JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
    ACCESS_TOKEN_EXPIRE_MINUTES = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", "30"))
</#if>
<#if has_oauth2??>
    GOOGLE_CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID", "")
    GOOGLE_CLIENT_SECRET = os.getenv("GOOGLE_CLIENT_SECRET", "")
    GITHUB_CLIENT_ID = os.getenv("GITHUB_CLIENT_ID", "")
    GITHUB_CLIENT_SECRET = os.getenv("GITHUB_CLIENT_SECRET", "")
</#if>

</#if>
    @property
    def environment(self):
        """Get current environment"""
        return self.ENVIRONMENT

    @property
    def debug(self):
        """Check if debug mode is enabled"""
        return self.DEBUG

    @property
    def host(self):
        """Get application host"""
        return self.HOST

    @property
    def port(self):
        """Get application port"""
        return self.PORT

<#if hasDatabase>
<#if has_mongodb??>
    @property
    def mongo_uri(self):
        """Get MongoDB connection URI"""
        return self.MONGO_URI
<#elseif has_redis??>
    @property
    def redis_uri(self):
        """Get Redis connection URI"""
        return self.REDIS_URI
<#else>
    @property
    def database_url(self):
        """Get database connection URL"""
        if self.DATABASE_URL:
            return self.DATABASE_URL
<#if has_postgresql??>
        return f"postgresql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"
<#elseif has_mysql??>
        return f"mysql+pymysql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"
<#elseif has_sqlite??>
        return f"sqlite:///{self.DB_FILE}"
<#else>
        return None
</#if>
</#if>
</#if>


# Global config instance
config = Config()
