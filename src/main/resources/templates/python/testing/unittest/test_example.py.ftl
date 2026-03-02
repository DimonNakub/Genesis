"""
Unit tests for ${projectName}
Using Python's built-in unittest framework
"""

import unittest
import sys
import os

# Add parent directory to path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))


class TestExample(unittest.TestCase):
    """Example test case"""

    def setUp(self):
        """Set up test fixtures before each test"""
        self.test_data = "Hello World"

    def tearDown(self):
        """Clean up after each test"""
        pass

    def test_example(self):
        """Example test method"""
        self.assertEqual(1 + 1, 2)

    def test_string_operations(self):
        """Test string operations"""
        self.assertTrue(self.test_data.startswith("Hello"))
        self.assertIn("World", self.test_data)

    def test_list_operations(self):
        """Test list operations"""
        test_list = [1, 2, 3, 4, 5]
        self.assertEqual(len(test_list), 5)
        self.assertIn(3, test_list)


<#if hasWebFramework>
<#if has_flask??>
class TestFlaskApp(unittest.TestCase):
    """Test Flask application"""

    def setUp(self):
        """Set up Flask test client"""
        from app import create_app
        self.app = create_app()
        self.client = self.app.test_client()

    def test_health_endpoint(self):
        """Test health endpoint"""
        response = self.client.get('/health')
        self.assertEqual(response.status_code, 200)
        data = response.get_json()
        self.assertEqual(data['status'], 'healthy')

    def test_api_root(self):
        """Test API root endpoint"""
        response = self.client.get('/api/')
        self.assertEqual(response.status_code, 200)


</#if>
</#if>
if __name__ == '__main__':
    unittest.main()
