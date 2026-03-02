# Docker Compose configuration for ${projectName}

version: '3.8'

services:
  app:
    build: .
    container_name: ${projectName}
    ports:
<#if has_flask??>
      - "5000:5000"
<#elseif has_fastapi??>
      - "8000:8000"
<#elseif has_django??>
      - "8000:8000"
<#elseif has_streamlit??>
      - "8501:8501"
<#else>
      - "8000:8000"
</#if>
    environment:
      - ENVIRONMENT=production
      - DEBUG=False
<#if has_postgresql??>
      - DATABASE_URL=postgresql://postgres:password@postgres:5432/${projectName}
<#elseif has_mysql??>
      - DATABASE_URL=mysql+pymysql://root:password@mysql:3306/${projectName}
<#elseif has_mongodb??>
      - MONGO_URI=mongodb://mongodb:27017/${projectName}
<#elseif has_redis??>
      - REDIS_URI=redis://redis:6379/0
</#if>
    volumes:
      - .:/app
<#if hasDatabase && !has_redis??>
    depends_on:
<#if has_postgresql??>
      - postgres
<#elseif has_mysql??>
      - mysql
<#elseif has_mongodb??>
      - mongodb
</#if>
</#if>
    restart: unless-stopped

<#if has_postgresql??>
  postgres:
    image: postgres:16-alpine
    container_name: ${projectName}_postgres
    environment:
      - POSTGRES_DB=${projectName}
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

</#if>
<#if has_mysql??>
  mysql:
    image: mysql:8.0
    container_name: ${projectName}_mysql
    environment:
      - MYSQL_DATABASE=${projectName}
      - MYSQL_ROOT_PASSWORD=password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    restart: unless-stopped

</#if>
<#if has_mongodb??>
  mongodb:
    image: mongo:7
    container_name: ${projectName}_mongodb
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    restart: unless-stopped

</#if>
<#if has_redis??>
  redis:
    image: redis:7-alpine
    container_name: ${projectName}_redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    restart: unless-stopped

</#if>
<#if hasDatabase>
volumes:
<#if has_postgresql??>
  postgres_data:
<#elseif has_mysql??>
  mysql_data:
<#elseif has_mongodb??>
  mongodb_data:
<#elseif has_redis??>
  redis_data:
</#if>
</#if>
