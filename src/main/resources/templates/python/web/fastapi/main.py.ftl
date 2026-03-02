"""
FastAPI application for ${projectName}
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import logging

logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(
    title="${projectName}",
    description="${description}",
    version="1.0.0"
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

<#if hasDatabase>
@app.on_event("startup")
async def startup_event():
    """Initialize databases on startup"""
    # Initialize your database connections here:
<#if has_postgresql??>
    # from config.db_connection_postgresql import init_db as init_postgres
    # init_postgres()
</#if>
<#if has_mysql??>
    # from config.db_connection_mysql import init_db as init_mysql
    # init_mysql()
</#if>
<#if has_mongodb??>
    # from config.db_connection_mongodb import init_db as init_mongo
    # init_mongo()
</#if>
<#if has_sqlite??>
    # from config.db_connection_sqlite import init_db as init_sqlite
    # init_sqlite()
</#if>
<#if has_redis??>
    # from config.db_connection_redis import init_redis
    # init_redis()
</#if>
    logger.info("Database modules available")

</#if>

# Import routers
from routers import router as api_router
app.include_router(api_router, prefix="/api")


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Welcome to ${projectName} API",
        "version": "1.0.0",
        "docs": "/docs"
    }


@app.get("/health")
async def health():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "application": "${projectName}",
        "version": "1.0.0"
    }
