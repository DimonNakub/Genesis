"""
JWT Authentication Handler for ${projectName}
Provides token creation, validation, and user authentication
"""

import jwt
from datetime import datetime, timedelta
from typing import Optional, Dict
from config.config import Config

config = Config()


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    """
    Create a JWT access token

    Args:
        data: Dictionary containing claims (e.g., {'sub': 'user@example.com'})
        expires_delta: Optional expiration time delta

    Returns:
        Encoded JWT token string
    """
    to_encode = data.copy()

    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=config.ACCESS_TOKEN_EXPIRE_MINUTES)

    to_encode.update({"exp": expire, "iat": datetime.utcnow()})

    encoded_jwt = jwt.encode(
        to_encode,
        config.JWT_SECRET_KEY,
        algorithm=config.JWT_ALGORITHM
    )

    return encoded_jwt


def verify_token(token: str) -> Optional[Dict]:
    """
    Verify and decode a JWT token

    Args:
        token: JWT token string

    Returns:
        Decoded token payload or None if invalid
    """
    try:
        payload = jwt.decode(
            token,
            config.JWT_SECRET_KEY,
            algorithms=[config.JWT_ALGORITHM]
        )
        return payload
    except jwt.ExpiredSignatureError:
        # Token has expired
        return None
    except jwt.InvalidTokenError:
        # Token is invalid
        return None


def get_user_from_token(token: str) -> Optional[str]:
    """
    Extract user identifier from token

    Args:
        token: JWT token string

    Returns:
        User identifier (usually email or username) or None
    """
    payload = verify_token(token)
    if payload:
        return payload.get("sub")
    return None


<#if has_flask??>
# Flask-specific decorators and helpers
from functools import wraps
from flask import request, jsonify


def token_required(f):
    """
    Decorator to protect Flask routes with JWT authentication

    Usage:
        @app.route('/protected')
        @token_required
        def protected_route(current_user):
            return jsonify({'message': f'Hello {current_user}'})
    """
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        # Check for token in Authorization header
        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            try:
                token = auth_header.split(" ")[1]  # Bearer <token>
            except IndexError:
                return jsonify({'message': 'Token format invalid'}), 401

        if not token:
            return jsonify({'message': 'Token is missing'}), 401

        # Verify token
        current_user = get_user_from_token(token)
        if not current_user:
            return jsonify({'message': 'Token is invalid or expired'}), 401

        return f(current_user, *args, **kwargs)

    return decorated


</#if>
<#if has_fastapi??>
# FastAPI-specific dependencies and helpers
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

security = HTTPBearer()


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
) -> str:
    """
    FastAPI dependency to get current user from JWT token

    Usage:
        @app.get("/protected")
        async def protected_route(current_user: str = Depends(get_current_user)):
            return {"message": f"Hello {current_user}"}
    """
    token = credentials.credentials
    user = get_user_from_token(token)

    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        )

    return user


</#if>
# Example usage:
#
# # Create token
# token = create_access_token(data={"sub": "user@example.com"})
#
# # Verify token
# payload = verify_token(token)
# if payload:
#     user = payload['sub']
