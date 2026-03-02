"""
MongoDB database connection for ${projectName}
Uses pymongo for MongoDB operations
"""

import logging
from pymongo import MongoClient
from pymongo.errors import ConnectionFailure, ServerSelectionTimeoutError
from config.config import Config

logger = logging.getLogger(__name__)

# MongoDB client
client = None
database = None


def init_db(app=None):
    """Initialize MongoDB connection"""
    global client, database

    config = Config()

    try:
        # Create MongoDB client
        client = MongoClient(
            config.mongo_uri,
            serverSelectionTimeoutMS=5000  # 5 second timeout
        )

        # Test connection
        client.admin.command('ping')
        logger.info("MongoDB connection established successfully")

        # Get database
        database = client.get_database()

    except (ConnectionFailure, ServerSelectionTimeoutError) as e:
        logger.error(f"Failed to connect to MongoDB: {e}")
        raise


def get_db_client():
    """Get MongoDB client"""
    if client is None:
        init_db()
    return client


def get_database():
    """Get MongoDB database"""
    if database is None:
        init_db()
    return database


def get_collection(collection_name: str):
    """Get a specific collection"""
    db = get_database()
    return db[collection_name]


def close_db():
    """Close MongoDB connection"""
    global client
    if client:
        client.close()
        logger.info("MongoDB connection closed")


# Example usage:
#
# # Get database
# db = get_database()
#
# # Get collection
# users = get_collection('users')
#
# # Insert document
# user_id = users.insert_one({
#     'username': 'john_doe',
#     'email': 'john@example.com'
# }).inserted_id
#
# # Find documents
# for user in users.find():
#     print(user)
#
# # Update document
# users.update_one(
#     {'username': 'john_doe'},
#     {'$set': {'email': 'newemail@example.com'}}
# )
#
# # Delete document
# users.delete_one({'username': 'john_doe'})
