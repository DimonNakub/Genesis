"""
Redis connection for ${projectName}
Uses redis-py for Redis operations
"""

import logging
import redis
from redis.exceptions import RedisError, ConnectionError
from config.config import Config

logger = logging.getLogger(__name__)

# Redis client
redis_client = None


def init_redis(app=None):
    """Initialize Redis connection"""
    global redis_client

    config = Config()

    try:
        # Parse Redis URI
        redis_client = redis.from_url(
            config.redis_uri,
            decode_responses=True,  # Decode responses to strings
            socket_timeout=5,
            socket_connect_timeout=5
        )

        # Test connection
        redis_client.ping()
        logger.info("Redis connection established successfully")

    except (RedisError, ConnectionError) as e:
        logger.error(f"Failed to connect to Redis: {e}")
        raise


def get_redis_client():
    """Get Redis client"""
    if redis_client is None:
        init_redis()
    return redis_client


def close_redis():
    """Close Redis connection"""
    global redis_client
    if redis_client:
        redis_client.close()
        logger.info("Redis connection closed")


# Helper functions for common operations

def cache_get(key: str):
    """Get value from cache"""
    client = get_redis_client()
    return client.get(key)


def cache_set(key: str, value: str, expire: int = None):
    """Set value in cache with optional expiration (seconds)"""
    client = get_redis_client()
    if expire:
        client.setex(key, expire, value)
    else:
        client.set(key, value)


def cache_delete(key: str):
    """Delete key from cache"""
    client = get_redis_client()
    client.delete(key)


def cache_exists(key: str) -> bool:
    """Check if key exists"""
    client = get_redis_client()
    return bool(client.exists(key))


# Example usage:
#
# from config.db_connection import cache_get, cache_set
#
# # Set a value with 1 hour expiration
# cache_set('user:123', 'john_doe', expire=3600)
#
# # Get a value
# username = cache_get('user:123')
#
# # Delete a value
# cache_delete('user:123')
