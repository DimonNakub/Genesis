# Redis Usage Instructions

This document explains how to use Redis with ${projectName}.

## Configuration

Redis settings are configured in `.env` file:

```env
REDIS_URI=redis://localhost:6379/0
```

## Prerequisites

1. **Install Redis:**
   - Ubuntu/Debian: `sudo apt-get install redis-server`
   - macOS: `brew install redis`
   - Windows: Download from https://redis.io/download or use WSL

2. **Start Redis service:**
   ```bash
   # Linux
   sudo service redis-server start

   # macOS
   brew services start redis

   # Manual start
   redis-server
   ```

## Usage

### Basic Connection

```python
from config.db_connection import get_redis_client

# Get Redis client
redis_client = get_redis_client()

# Test connection
redis_client.ping()
```

### Simple Key-Value Operations

```python
from config.db_connection import cache_get, cache_set, cache_delete, cache_exists

# Set a value
cache_set('username', 'john_doe')

# Get a value
username = cache_get('username')
print(username)  # 'john_doe'

# Set with expiration (1 hour = 3600 seconds)
cache_set('session:abc123', 'user_data', expire=3600)

# Check if key exists
if cache_exists('username'):
    print('Key exists')

# Delete a key
cache_delete('username')
```

### Advanced Operations

```python
from config.db_connection import get_redis_client

redis_client = get_redis_client()

# Increment/Decrement
redis_client.incr('page_views')  # Increment by 1
redis_client.incrby('page_views', 10)  # Increment by 10
redis_client.decr('inventory')  # Decrement by 1

# Lists
redis_client.lpush('tasks', 'task1', 'task2')  # Push to list
task = redis_client.rpop('tasks')  # Pop from list
tasks = redis_client.lrange('tasks', 0, -1)  # Get all items

# Sets
redis_client.sadd('users:online', 'user1', 'user2')  # Add to set
redis_client.sismember('users:online', 'user1')  # Check membership
members = redis_client.smembers('users:online')  # Get all members

# Hashes (like Python dict)
redis_client.hset('user:123', mapping={
    'username': 'john_doe',
    'email': 'john@example.com'
})
redis_client.hget('user:123', 'username')  # Get single field
user_data = redis_client.hgetall('user:123')  # Get all fields

# Sorted Sets
redis_client.zadd('leaderboard', {'player1': 100, 'player2': 200})
top_players = redis_client.zrevrange('leaderboard', 0, 9, withscores=True)
```

### Caching Example

```python
from config.db_connection import get_redis_client
import json

redis_client = get_redis_client()

def get_user_data(user_id: int):
    """Get user data with caching"""
    cache_key = f'user:{user_id}'

    # Try to get from cache
    cached_data = redis_client.get(cache_key)
    if cached_data:
        return json.loads(cached_data)

    # If not in cache, get from database
    user_data = fetch_user_from_database(user_id)

    # Store in cache for 1 hour
    redis_client.setex(
        cache_key,
        3600,
        json.dumps(user_data)
    )

    return user_data
```

### Session Management Example

```python
from config.db_connection import get_redis_client
import uuid

redis_client = get_redis_client()

def create_session(user_id: int):
    """Create user session"""
    session_id = str(uuid.uuid4())
    redis_client.setex(
        f'session:{session_id}',
        3600,  # 1 hour
        user_id
    )
    return session_id

def get_session(session_id: str):
    """Get user ID from session"""
    return redis_client.get(f'session:{session_id}')

def destroy_session(session_id: str):
    """Destroy session"""
    redis_client.delete(f'session:{session_id}')
```

### Pub/Sub Example

```python
from config.db_connection import get_redis_client

redis_client = get_redis_client()

# Publisher
def send_notification(channel: str, message: str):
    redis_client.publish(channel, message)

# Subscriber
def listen_for_notifications(channel: str):
    pubsub = redis_client.pubsub()
    pubsub.subscribe(channel)

    for message in pubsub.listen():
        if message['type'] == 'message':
            print(f"Received: {message['data']}")
```

## Common Use Cases

### 1. Caching Database Queries
```python
# Cache expensive query results
redis_client.setex('popular_posts', 300, json.dumps(posts))
```

### 2. Rate Limiting
```python
def check_rate_limit(user_id: int, max_requests: int = 100):
    key = f'rate_limit:{user_id}'
    current = redis_client.incr(key)

    if current == 1:
        redis_client.expire(key, 3600)  # 1 hour window

    return current <= max_requests
```

### 3. Distributed Locks
```python
def acquire_lock(resource: str, timeout: int = 10):
    lock_key = f'lock:{resource}'
    return redis_client.set(lock_key, '1', nx=True, ex=timeout)
```

## Troubleshooting

### Connection refused
- Ensure Redis is running: `redis-cli ping` (should return PONG)
- Check Redis port: `sudo netstat -plnt | grep 6379`

### Out of memory
- Check Redis memory usage: `redis-cli INFO memory`
- Configure maxmemory in redis.conf
- Set eviction policy: `redis-cli CONFIG SET maxmemory-policy allkeys-lru`

### Slow performance
- Use pipelining for bulk operations
- Monitor slow queries: `redis-cli SLOWLOG GET`

## Best Practices

1. **Use appropriate data structures** - Choose the right type for your use case
2. **Set expiration times** - Prevent memory bloat
3. **Use connection pooling** (redis-py handles this automatically)
4. **Namespace your keys** - Use prefixes like `user:123`, `session:abc`
5. **Monitor memory usage** - Redis is in-memory

## Resources

- Redis Documentation: https://redis.io/documentation
- redis-py Documentation: https://redis-py.readthedocs.io/
- Redis Commands: https://redis.io/commands

---

Generated by Genesis - ${currentDate}
