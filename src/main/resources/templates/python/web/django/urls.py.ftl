"""
URL Configuration for ${projectName}
"""

from django.contrib import admin
from django.urls import path, include
from django.http import JsonResponse


def health(request):
    """Health check endpoint"""
    return JsonResponse({
        'status': 'healthy',
        'application': '${projectName}',
        'version': '1.0.0'
    })


urlpatterns = [
    path('admin/', admin.site.urls),
    path('health/', health),
    # Add your app URLs here
    # path('api/', include('your_app.urls')),
]
