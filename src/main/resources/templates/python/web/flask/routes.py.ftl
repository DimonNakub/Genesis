"""
Flask routes for ${projectName}
"""

from flask import Blueprint, jsonify, request
import logging

<#if hasDatabase>
from config.db_connection import get_db_session
</#if>

logger = logging.getLogger(__name__)

# Create blueprints
api_bp = Blueprint('api', __name__, url_prefix='/api')


@api_bp.route('/', methods=['GET'])
def index():
    """API root endpoint"""
    return jsonify({
        'message': 'Welcome to ${projectName} API',
        'version': '1.0.0',
        'endpoints': {
            'health': '/health',
            'api': '/api'
        }
    })


@api_bp.route('/example', methods=['GET'])
def example():
    """Example API endpoint"""
    return jsonify({
        'message': 'This is an example endpoint',
        'data': {
            'status': 'success'
        }
    })


<#if hasDatabase>
@api_bp.route('/db-test', methods=['GET'])
def db_test():
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
        results['postgresql'] = f'error: {e}'
</#if>
<#if has_mysql??>
    try:
        from config.db_connection_mysql import get_db_session
        session = get_db_session()
        session.close()
        results['mysql'] = 'connected'
    except Exception as e:
        results['mysql'] = f'error: {e}'
</#if>
<#if has_mongodb??>
    try:
        from config.db_connection_mongodb import get_database
        db = get_database()
        results['mongodb'] = 'connected'
    except Exception as e:
        results['mongodb'] = f'error: {e}'
</#if>
<#if has_sqlite??>
    try:
        from config.db_connection_sqlite import get_db_session
        session = get_db_session()
        session.close()
        results['sqlite'] = 'connected'
    except Exception as e:
        results['sqlite'] = f'error: {e}'
</#if>
<#if has_redis??>
    try:
        from config.db_connection_redis import get_redis_client
        redis = get_redis_client()
        redis.ping()
        results['redis'] = 'connected'
    except Exception as e:
        results['redis'] = f'error: {e}'
</#if>

    return jsonify({
        'message': 'Database connection test complete',
        'results': results
    })


</#if>
@api_bp.route('/example', methods=['POST'])
def create_example():
    """Example POST endpoint"""
    data = request.get_json()

    # Process your data here
    logger.info(f"Received data: {data}")

    return jsonify({
        'message': 'Data received successfully',
        'data': data
    }), 201


def register_routes(app):
    """Register all route blueprints"""
    app.register_blueprint(api_bp)
    logger.info("Routes registered successfully")
