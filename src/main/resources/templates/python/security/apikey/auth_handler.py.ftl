"""
API Key Authentication Handler for ${projectName}
Simple API key-based authentication
"""

import secrets
from typing import Optional
import logging

logger = logging.getLogger(__name__)


def generate_api_key() -> str:
    """
    Generate a secure random API key

    Returns:
        Random API key string
    """
    return secrets.token_urlsafe(32)


def verify_api_key(api_key: str) -> bool:
    """
    Verify API key

    Args:
        api_key: API key to verify

    Returns:
        True if valid, False otherwise

    TODO: Replace with actual database lookup
    """
    # EXAMPLE ONLY - Store API keys in database in production
    valid_keys = {
        'demo_key_123': 'user1',
        'test_key_456': 'user2'
    }

    return api_key in valid_keys


def get_user_from_api_key(api_key: str) -> Optional[str]:
    """
    Get user identifier from API key

    TODO: Replace with actual database lookup
    """
    key_to_user = {
        'demo_key_123': 'user1',
        'test_key_456': 'user2'
    }

    return key_to_user.get(api_key)


<#if has_flask??>
# Flask-specific decorator
from functools import wraps
from flask import request, jsonify


def api_key_required(f):
    """
    Decorator to protect Flask routes with API Key

    Usage:
        @app.route('/protected')
        @api_key_required
        def protected_route(user_id):
            return jsonify({'message': f'Hello {user_id}'})
    """
    @wraps(f)
    def decorated(*args, **kwargs):
        # Check for API key in header or query param
        api_key = request.headers.get('X-API-Key') or request.args.get('api_key')

        if not api_key:
            return jsonify({'message': 'API key missing'}), 401

        if not verify_api_key(api_key):
            return jsonify({'message': 'Invalid API key'}), 401

        user_id = get_user_from_api_key(api_key)
        return f(user_id, *args, **kwargs)

    return decorated


</#if>
<#if has_fastapi??>
# FastAPI-specific dependencies
from fastapi import Depends, HTTPException, status, Security
from fastapi.security import APIKeyHeader

api_key_header = APIKeyHeader(name="X-API-Key", auto_error=False)


async def get_api_key(api_key: str = Security(api_key_header)) -> str:
    """
    FastAPI dependency for API Key authentication

    Usage:
        @app.get("/protected")
        async def protected_route(user_id: str = Depends(get_current_user_from_key)):
            return {"message": f"Hello {user_id}"}
    """
    if not api_key or not verify_api_key(api_key):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing API key"
        )

    return api_key


async def get_current_user_from_key(api_key: str = Depends(get_api_key)) -> str:
    """Get user ID from API key"""
    user_id = get_user_from_api_key(api_key)
    if not user_id:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid API key"
        )
    return user_id


</#if>
# Example usage:
#
# # Generate new API key
# new_key = generate_api_key()
# print(f"Your API key: {new_key}")
#
# # Verify API key
# if verify_api_key(api_key):
#     user = get_user_from_api_key(api_key)
