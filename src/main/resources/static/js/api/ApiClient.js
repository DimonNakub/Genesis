/**
 * API Client for Genesis Backend
 * Handles all HTTP communication with Spring Boot REST API
 */

export class ApiClient {
    constructor() {
        this.baseUrl = '/api/v1';
    }

    /**
     * Get available components metadata
     * @returns {Promise<Object>} Metadata containing available components
     */
    async getMetadata() {
        try {
            const response = await fetch(`${this.baseUrl}/metadata`);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Failed to fetch metadata:', error);
            throw new Error('Failed to load component metadata. Please check if the server is running.');
        }
    }

    /**
     * Generate a Python project
     * @param {Object} config Project configuration
     * @returns {Promise<Blob>} ZIP file as blob
     */
    async generateProject(config) {
        try {
            const response = await fetch(`${this.baseUrl}/generate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(config)
            });

            if (!response.ok) {
                // Try to parse error response
                const errorData = await response.json().catch(() => null);

                if (errorData && errorData.message) {
                    throw new Error(errorData.message);
                }

                throw new Error(`Generation failed: ${response.statusText}`);
            }

            return await response.blob();
        } catch (error) {
            console.error('Project generation failed:', error);
            throw error;
        }
    }

    /**
     * Check server health
     * @returns {Promise<Object>} Health status
     */
    async checkHealth() {
        try {
            const response = await fetch(`${this.baseUrl}/health`);

            if (!response.ok) {
                throw new Error('Server unhealthy');
            }

            return await response.json();
        } catch (error) {
            console.error('Health check failed:', error);
            throw error;
        }
    }
}
