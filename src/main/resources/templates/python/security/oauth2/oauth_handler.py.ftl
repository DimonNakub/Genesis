"""
OAuth2 Authentication Handler for ${projectName}
Provides OAuth2 integration with popular providers (Google, GitHub, etc.)
"""

from authlib.integrations.requests_client import OAuth2Session
from config.config import Config
import logging

logger = logging.getLogger(__name__)
config = Config()


class OAuth2Provider:
    """OAuth2 provider configuration"""

    def __init__(self, name: str, client_id: str, client_secret: str,
                 authorize_url: str, token_url: str, userinfo_url: str):
        self.name = name
        self.client_id = client_id
        self.client_secret = client_secret
        self.authorize_url = authorize_url
        self.token_url = token_url
        self.userinfo_url = userinfo_url


# Example provider configurations
PROVIDERS = {
    'google': OAuth2Provider(
        name='Google',
        client_id=config.GOOGLE_CLIENT_ID if hasattr(config, 'GOOGLE_CLIENT_ID') else '',
        client_secret=config.GOOGLE_CLIENT_SECRET if hasattr(config, 'GOOGLE_CLIENT_SECRET') else '',
        authorize_url='https://accounts.google.com/o/oauth2/v2/auth',
        token_url='https://oauth2.googleapis.com/token',
        userinfo_url='https://www.googleapis.com/oauth2/v1/userinfo'
    ),
    'github': OAuth2Provider(
        name='GitHub',
        client_id=config.GITHUB_CLIENT_ID if hasattr(config, 'GITHUB_CLIENT_ID') else '',
        client_secret=config.GITHUB_CLIENT_SECRET if hasattr(config, 'GITHUB_CLIENT_SECRET') else '',
        authorize_url='https://github.com/login/oauth/authorize',
        token_url='https://github.com/login/oauth/access_token',
        userinfo_url='https://api.github.com/user'
    )
}


def get_oauth_session(provider_name: str, redirect_uri: str) -> OAuth2Session:
    """
    Create OAuth2 session for a provider

    Args:
        provider_name: Provider name ('google', 'github', etc.)
        redirect_uri: Callback URL after authentication

    Returns:
        OAuth2Session object
    """
    provider = PROVIDERS.get(provider_name)
    if not provider:
        raise ValueError(f"Unknown provider: {provider_name}")

    return OAuth2Session(
        client_id=provider.client_id,
        client_secret=provider.client_secret,
        redirect_uri=redirect_uri
    )


def get_authorization_url(provider_name: str, redirect_uri: str) -> tuple:
    """
    Get authorization URL for OAuth flow

    Returns:
        Tuple of (authorization_url, state)
    """
    provider = PROVIDERS.get(provider_name)
    session = get_oauth_session(provider_name, redirect_uri)

    authorization_url, state = session.create_authorization_url(provider.authorize_url)
    return authorization_url, state


def get_access_token(provider_name: str, redirect_uri: str, authorization_response: str) -> dict:
    """
    Exchange authorization code for access token

    Args:
        provider_name: Provider name
        redirect_uri: Callback URL
        authorization_response: Full callback URL with code

    Returns:
        Token dictionary
    """
    provider = PROVIDERS.get(provider_name)
    session = get_oauth_session(provider_name, redirect_uri)

    token = session.fetch_token(
        provider.token_url,
        authorization_response=authorization_response
    )
    return token


def get_user_info(provider_name: str, token: dict) -> dict:
    """
    Get user information from provider

    Args:
        provider_name: Provider name
        token: Access token dictionary

    Returns:
        User info dictionary
    """
    provider = PROVIDERS.get(provider_name)
    session = OAuth2Session(
        client_id=provider.client_id,
        token=token
    )

    response = session.get(provider.userinfo_url)
    return response.json()


# Example usage:
#
# # 1. Get authorization URL
# auth_url, state = get_authorization_url('google', 'http://localhost:5000/callback')
#
# # 2. Redirect user to auth_url
# # User authorizes and is redirected back
#
# # 3. Exchange code for token
# token = get_access_token('google', 'http://localhost:5000/callback', callback_url)
#
# # 4. Get user info
# user_info = get_user_info('google', token)
