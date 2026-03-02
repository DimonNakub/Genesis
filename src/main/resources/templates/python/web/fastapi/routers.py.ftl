"""
FastAPI routers for ${projectName}
"""

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import logging

<#if hasDatabase>
<#if has_mongodb??>
from config.db_connection import get_collection
<#else>
from config.db_connection import get_db_session
from sqlalchemy import text
</#if>
</#if>

logger = logging.getLogger(__name__)

# Create router
router = APIRouter()


# Example Pydantic models
class ExampleRequest(BaseModel):
    name: str
    value: int


class ExampleResponse(BaseModel):
    message: str
    data: dict


@router.get("/")
async def api_root():
    """API root endpoint"""
    return {
        "message": "Welcome to ${projectName} API",
        "endpoints": {
            "health": "/health",
            "docs": "/docs",
            "example": "/api/example"
        }
    }


@router.get("/example")
async def get_example():
    """Example GET endpoint"""
    return {
        "message": "This is an example endpoint",
        "data": {
            "status": "success"
        }
    }


@router.post("/example", response_model=ExampleResponse)
async def create_example(request: ExampleRequest):
    """Example POST endpoint"""
    logger.info(f"Received data: {request}")

    return ExampleResponse(
        message="Data received successfully",
        data=request.dict()
    )


<#if hasDatabase>
@router.get("/db-test")
async def db_test():
    """Test database connections"""
    results = {}

    # Test available database connections
<#if has_postgresql??>
    try:
        from config.db_connection_postgresql import get_db_session
        session = get_db_session()
        session.close()
        results['postgresql'] = 'connected'
    except Exception as e:
        results['postgresql'] = f'error: {str(e)}'
</#if>
<#if has_mysql??>
    try:
        from config.db_connection_mysql import get_db_session
        session = get_db_session()
        session.close()
        results['mysql'] = 'connected'
    except Exception as e:
        results['mysql'] = f'error: {str(e)}'
</#if>
<#if has_mongodb??>
    try:
        from config.db_connection_mongodb import get_database
        db = get_database()
        results['mongodb'] = 'connected'
    except Exception as e:
        results['mongodb'] = f'error: {str(e)}'
</#if>
<#if has_sqlite??>
    try:
        from config.db_connection_sqlite import get_db_session
        session = get_db_session()
        session.close()
        results['sqlite'] = 'connected'
    except Exception as e:
        results['sqlite'] = f'error: {str(e)}'
</#if>
<#if has_redis??>
    try:
        from config.db_connection_redis import get_redis_client
        redis = get_redis_client()
        redis.ping()
        results['redis'] = 'connected'
    except Exception as e:
        results['redis'] = f'error: {str(e)}'
</#if>

    return {
        "message": "Database connection test complete",
        "results": results
    }


</#if>
