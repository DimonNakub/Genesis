"""
Flask application factory for ${projectName}
"""

from flask import Flask, jsonify
from flask_cors import CORS
import logging

logger = logging.getLogger(__name__)


def create_app():
    """Create and configure Flask application"""
    app = Flask(__name__)

    # Load configuration
    app.config.from_object('config.config.Config')

    # Enable CORS
    CORS(app)

<#if hasDatabase>
    # Database connections available:
<#if has_postgresql??>
    # from config.db_connection_postgresql import init_db as init_postgres
    # with app.app_context():
    #     init_postgres(app)
</#if>
<#if has_mysql??>
    # from config.db_connection_mysql import init_db as init_mysql
    # with app.app_context():
    #     init_mysql(app)
</#if>
<#if has_mongodb??>
    # from config.db_connection_mongodb import init_db as init_mongo
    # with app.app_context():
    #     init_mongo(app)
</#if>
<#if has_sqlite??>
    # from config.db_connection_sqlite import init_db as init_sqlite
    # with app.app_context():
    #     init_sqlite(app)
</#if>
<#if has_redis??>
    # from config.db_connection_redis import init_redis
    # with app.app_context():
    #     init_redis(app)
</#if>
    logger.info("Database modules available")

</#if>
    # Register routes
    from routes import register_routes
    register_routes(app)

    # Health check endpoint
    @app.route('/health')
    def health():
        return jsonify({
            'status': 'healthy',
            'application': '${projectName}',
            'version': '1.0.0'
        })

    logger.info("Flask application created successfully")
    return app
