# Dockerfile for ${projectName}
# Multi-stage build for optimal image size

FROM python:${pythonVersion}-slim as builder

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir --user -r requirements.txt

# Final stage
FROM python:${pythonVersion}-slim

WORKDIR /app

# Copy dependencies from builder
COPY --from=builder /root/.local /root/.local

# Copy application code
COPY . .

# Make sure scripts are executable
RUN chmod +x main.py

# Add local bin to PATH
ENV PATH=/root/.local/bin:$PATH

# Expose port
EXPOSE <#if has_flask??>5000<#elseif has_fastapi??>8000<#elseif has_django??>8000<#elseif has_streamlit??>8501<#else>8000</#if>

# Run application
<#if has_flask??>
CMD ["python", "main.py"]
<#elseif has_fastapi??>
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
<#elseif has_django??>
CMD ["python", "manage.py", "runserver", "0.0.0.0:8000"]
<#elseif has_streamlit??>
CMD ["streamlit", "run", "streamlit_app.py", "--server.port=8501", "--server.address=0.0.0.0"]
<#elseif has_tkinter?? || has_pyqt5??>
CMD ["python", "gui_app.py"]
<#else>
CMD ["python", "main.py"]
</#if>
