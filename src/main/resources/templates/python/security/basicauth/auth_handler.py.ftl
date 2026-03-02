"""
Basic Authentication Handler for ${projectName}
Simple username/password authentication
"""

import base64
from typing import Optional, Tuple
import logging

logger = logging.getLogger(__name__)


def verify_basic_auth(username: str, password: str) -> bool:
    """
    Verify username and password

    Args:
        username: Username
        password: Password

    Returns:
        True if valid, False otherwise

    TODO: Replace with actual database lookup
    """
    # EXAMPLE ONLY - Replace with actual database verification
    valid_users = {
        'admin': 'admin123',  # CHANGE THIS!
        'user': 'password'
    }

    return valid_users.get(username) == password


def decode_basic_auth_header(auth_header: str) -> Optional[Tuple[str, str]]:
    """
    Decode Basic Auth header

    Args:
        auth_header: Authorization header value (e.g., "Basic base64string")

    Returns:
        Tuple of (username, password) or None if invalid
    """
    try:
        scheme, credentials = auth_header.split(' ', 1)
        if scheme.lower() != 'basic':
            return None

        decoded = base64.b64decode(credentials).decode('utf-8')
        username, password = decoded.split(':', 1)
        return username, password
    except Exception as e:
        logger.error(f"Failed to decode Basic Auth header: {e}")
        return None


<#if has_flask??>
# Flask-specific decorator
from functools import wraps
from flask import request, jsonify


def basic_auth_required(f):
    """
    Decorator to protect Flask routes with Basic Auth

    Usage:
        @app.route('/protected')
        @basic_auth_required
        def protected_route(username):
            return jsonify({'message': f'Hello {username}'})
    """
    @wraps(f)
    def decorated(*args, **kwargs):
        auth_header = request.headers.get('Authorization')

        if not auth_header:
            return jsonify({'message': 'Authorization header missing'}), 401

        credentials = decode_basic_auth_header(auth_header)
        if not credentials:
            return jsonify({'message': 'Invalid authorization header'}), 401

        username, password = credentials
        if not verify_basic_auth(username, password):
            return jsonify({'message': 'Invalid credentials'}), 401

        return f(username, *args, **kwargs)

    return decorated


</#if>
<#if has_fastapi??>
# FastAPI-specific dependencies
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBasic, HTTPBasicCredentials

security = HTTPBasic()


async def get_current_username(
    credentials: HTTPBasicCredentials = Depends(security)
) -> str:
    """
    FastAPI dependency for Basic Auth

    Usage:
        @app.get("/protected")
        async def protected_route(username: str = Depends(get_current_username)):
            return {"message": f"Hello {username}"}
    """
    if not verify_basic_auth(credentials.username, credentials.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials",
            headers={"WWW-Authenticate": "Basic"},
        )

    return credentials.username


</#if>
