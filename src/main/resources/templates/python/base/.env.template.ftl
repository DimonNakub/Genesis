# ${projectName} - Environment Configuration
# Copy this file to .env and update with your settings

# Application Settings
ENVIRONMENT=development
DEBUG=True
HOST=0.0.0.0
PORT=<#if has_flask??>5000<#elseif has_fastapi??>8000<#else>8000</#if>

<#if hasDatabase>
# Database Settings
<#if has_postgresql??>
DATABASE_URL=postgresql://postgres:password@localhost:5432/${projectName}
DB_HOST=localhost
DB_PORT=5432
DB_NAME=${projectName}
DB_USER=postgres
DB_PASSWORD=your_password_here
<#elseif has_mysql??>
DATABASE_URL=mysql+pymysql://root:password@localhost:3306/${projectName}
DB_HOST=localhost
DB_PORT=3306
DB_NAME=${projectName}
DB_USER=root
DB_PASSWORD=your_password_here
<#elseif has_mongodb??>
MONGO_URI=mongodb://localhost:27017/${projectName}
<#elseif has_sqlite??>
DB_FILE=data/${projectName}.db
<#elseif has_redis??>
REDIS_URI=redis://localhost:6379/0
</#if>

</#if>
<#if hasSecurity>
# Security Settings
SECRET_KEY=your-secret-key-here-change-in-production
<#if has_jwt??>
JWT_SECRET_KEY=your-jwt-secret-here-change-in-production
JWT_ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30
</#if>
<#if has_oauth2??>
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
</#if>

</#if>
# Add your custom environment variables below
