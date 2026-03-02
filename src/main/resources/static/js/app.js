/**
 * GENESIS - Main Application
 * Coordinates UI, state management, and API calls
 */

import { ApiClient } from './api/ApiClient.js';

class GenesisApp {
    constructor() {
        this.api = new ApiClient();
        this.state = {
            projectName: '',
            description: '',
            pythonVersion: '3.11',
            authorName: '',
            authorEmail: '',
            databases: [],
            webFramework: 'none',
            security: 'none',
            gui: 'none',
            pytest: false,
            unittest: false,
            docker: false,
            components: []
        };

        this.init();
    }

    async init() {
        console.log('🚀 Initializing GENESIS...');

        // Load metadata from backend
        try {
            await this.loadMetadata();
        } catch (error) {
            this.showError('Failed to connect to server. Please ensure the backend is running.');
        }

        // Setup event listeners
        this.setupEventListeners();

        // Initial UI update
        this.updateUI();

        console.log('✓ GENESIS initialized successfully');
    }

    async loadMetadata() {
        try {
            const metadata = await this.api.getMetadata();
            console.log('Loaded metadata:', metadata);
            this.metadata = metadata;
        } catch (error) {
            console.error('Failed to load metadata:', error);
            throw error;
        }
    }

    setupEventListeners() {
        // Project name input
        const projectNameInput = document.getElementById('projectName');
        projectNameInput.addEventListener('input', (e) => {
            this.state.projectName = e.target.value;
            this.validateProjectName();
            this.updateProjectNamePreview();
            this.validateForm(); // Enable/disable generate button
        });

        // Other form inputs
        document.getElementById('description').addEventListener('input', (e) => {
            this.state.description = e.target.value;
        });

        document.getElementById('pythonVersion').addEventListener('change', (e) => {
            this.state.pythonVersion = e.target.value;
        });

        document.getElementById('authorName').addEventListener('input', (e) => {
            this.state.authorName = e.target.value;
        });

        document.getElementById('authorEmail').addEventListener('input', (e) => {
            this.state.authorEmail = e.target.value;
        });

        // Database selection (multiple allowed)
        const databaseCheckboxes = document.querySelectorAll('input[name="database"]');
        databaseCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', (e) => {
                if (e.target.checked) {
                    this.state.databases.push(e.target.value);
                } else {
                    this.state.databases = this.state.databases.filter(db => db !== e.target.value);
                }
                this.updateUI();
            });
        });

        // Web framework selection
        const webFrameworkRadios = document.querySelectorAll('input[name="webFramework"]');
        webFrameworkRadios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.state.webFramework = e.target.value;
                this.updateUI();
            });
        });

        // Security selection
        const securityRadios = document.querySelectorAll('input[name="security"]');
        securityRadios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.state.security = e.target.value;
                this.updateUI();
            });
        });

        // GUI framework selection
        const guiRadios = document.querySelectorAll('input[name="gui"]');
        guiRadios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.state.gui = e.target.value;
                this.updateUI();
            });
        });

        // pytest checkbox
        const pytestCheckbox = document.getElementById('pytestCheckbox');
        if (pytestCheckbox) {
            pytestCheckbox.addEventListener('change', (e) => {
                this.state.pytest = e.target.checked;
                this.updateUI();
            });
        }

        // unittest checkbox
        const unittestCheckbox = document.getElementById('unittestCheckbox');
        if (unittestCheckbox) {
            unittestCheckbox.addEventListener('change', (e) => {
                this.state.unittest = e.target.checked;
                this.updateUI();
            });
        }

        // Docker checkbox
        const dockerCheckbox = document.getElementById('dockerCheckbox');
        if (dockerCheckbox) {
            dockerCheckbox.addEventListener('change', (e) => {
                this.state.docker = e.target.checked;
                this.updateUI();
            });
        }

        // Generate button
        document.getElementById('generateBtn').addEventListener('click', () => {
            this.generateProject();
        });

        // Modal close buttons
        document.getElementById('modalClose').addEventListener('click', () => {
            this.hideModal();
        });

        document.getElementById('modalOkBtn').addEventListener('click', () => {
            this.hideModal();
        });

        // Project template buttons
        const templateButtons = document.querySelectorAll('.template-btn');
        templateButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const template = e.currentTarget.dataset.template;
                this.applyTemplate(template);
            });
        });

        // Save/Load config buttons
        document.getElementById('saveConfigBtn').addEventListener('click', () => {
            this.saveConfiguration();
        });

        document.getElementById('loadConfigBtn').addEventListener('click', () => {
            this.loadConfiguration();
        });
    }

    applyTemplate(templateName) {
        console.log(`Applying template: ${templateName}`);

        // Clear current selections
        this.clearSelections();

        // Apply template-specific configurations
        switch (templateName) {
            case 'rest-api':
                // FastAPI + PostgreSQL + JWT + pytest + Docker
                this.state.databases = ['postgresql'];
                this.state.webFramework = 'fastapi';
                this.state.security = 'jwt';
                this.state.pytest = true;
                this.state.docker = true;
                break;

            case 'data-dashboard':
                // Streamlit + PostgreSQL + Redis + pytest
                this.state.databases = ['postgresql', 'redis'];
                this.state.gui = 'streamlit';
                this.state.pytest = true;
                break;

            case 'ml-pipeline':
                // Python script + PostgreSQL + pytest
                this.state.databases = ['postgresql'];
                this.state.pytest = true;
                break;

            case 'desktop-app':
                // PyQt5 + SQLite + unittest
                this.state.databases = ['sqlite'];
                this.state.gui = 'pyqt5';
                this.state.unittest = true;
                break;
        }

        // Update UI checkboxes/radios
        this.syncStateToUI();
        this.updateUI();

        // Show feedback
        this.showSuccess(`Template "${this.formatTemplateName(templateName)}" applied!`);
    }

    clearSelections() {
        this.state.databases = [];
        this.state.webFramework = 'none';
        this.state.security = 'none';
        this.state.gui = 'none';
        this.state.pytest = false;
        this.state.unittest = false;
        this.state.docker = false;
    }

    syncStateToUI() {
        // Update database checkboxes
        document.querySelectorAll('input[name="database"]').forEach(checkbox => {
            checkbox.checked = this.state.databases.includes(checkbox.value);
        });

        // Update web framework radio
        const webFrameworkRadio = document.querySelector(`input[name="webFramework"][value="${this.state.webFramework}"]`);
        if (webFrameworkRadio) webFrameworkRadio.checked = true;

        // Update security radio
        const securityRadio = document.querySelector(`input[name="security"][value="${this.state.security}"]`);
        if (securityRadio) securityRadio.checked = true;

        // Update GUI radio
        const guiRadio = document.querySelector(`input[name="gui"][value="${this.state.gui}"]`);
        if (guiRadio) guiRadio.checked = true;

        // Update testing checkboxes
        const pytestCheckbox = document.getElementById('pytestCheckbox');
        if (pytestCheckbox) pytestCheckbox.checked = this.state.pytest;

        const unittestCheckbox = document.getElementById('unittestCheckbox');
        if (unittestCheckbox) unittestCheckbox.checked = this.state.unittest;

        // Update Docker checkbox
        const dockerCheckbox = document.getElementById('dockerCheckbox');
        if (dockerCheckbox) dockerCheckbox.checked = this.state.docker;
    }

    formatTemplateName(template) {
        return template.split('-').map(word =>
            word.charAt(0).toUpperCase() + word.slice(1)
        ).join(' ');
    }

    saveConfiguration() {
        const config = {
            version: '1.0',
            projectName: this.state.projectName,
            description: this.state.description,
            pythonVersion: this.state.pythonVersion,
            authorName: this.state.authorName,
            authorEmail: this.state.authorEmail,
            databases: this.state.databases,
            webFramework: this.state.webFramework,
            security: this.state.security,
            gui: this.state.gui,
            pytest: this.state.pytest,
            unittest: this.state.unittest,
            docker: this.state.docker,
            savedAt: new Date().toISOString()
        };

        // Download as JSON
        const blob = new Blob([JSON.stringify(config, null, 2)], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `genesis-config-${this.state.projectName || 'untitled'}.json`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        this.showSuccess('Configuration saved!');
    }

    loadConfiguration() {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'application/json';

        input.onchange = async (e) => {
            const file = e.target.files[0];
            if (!file) return;

            try {
                const text = await file.text();
                const config = JSON.parse(text);

                // Apply configuration
                this.state.projectName = config.projectName || '';
                this.state.description = config.description || '';
                this.state.pythonVersion = config.pythonVersion || '3.11';
                this.state.authorName = config.authorName || '';
                this.state.authorEmail = config.authorEmail || '';
                this.state.databases = config.databases || [];
                this.state.webFramework = config.webFramework || 'none';
                this.state.security = config.security || 'none';
                this.state.gui = config.gui || 'none';
                this.state.pytest = config.pytest || false;
                this.state.unittest = config.unittest || false;
                this.state.docker = config.docker || false;

                // Update form inputs
                document.getElementById('projectName').value = this.state.projectName;
                document.getElementById('description').value = this.state.description;
                document.getElementById('pythonVersion').value = this.state.pythonVersion;
                document.getElementById('authorName').value = this.state.authorName;
                document.getElementById('authorEmail').value = this.state.authorEmail;

                // Sync to UI
                this.syncStateToUI();
                this.updateUI();

                this.showSuccess('Configuration loaded successfully!');
            } catch (error) {
                console.error('Failed to load configuration:', error);
                this.showError('Failed to load configuration file. Please check the file format.');
            }
        };

        input.click();
    }

    validateProjectName() {
        const input = document.getElementById('projectName');
        const errorElement = document.getElementById('projectNameError');
        const pattern = /^[a-z][a-z0-9_]*$/;

        if (!this.state.projectName) {
            errorElement.textContent = '';
            return false;
        }

        if (!pattern.test(this.state.projectName)) {
            errorElement.textContent = 'Must start with lowercase letter and contain only lowercase, numbers, and underscores';
            input.classList.add('invalid');
            return false;
        }

        errorElement.textContent = '';
        input.classList.remove('invalid');
        return true;
    }

    updateProjectNamePreview() {
        const preview = document.getElementById('projectNamePreview');
        preview.textContent = this.state.projectName || 'my_project';
    }

    updateUI() {
        this.updateSelectedComponents();
        this.updateFileTree();
        this.updateBadges();
        this.validateComponentCompatibility();
        this.validateForm();
    }

    validateComponentCompatibility() {
        const hasWebFramework = this.state.webFramework !== 'none';
        const hasGui = this.state.gui !== 'none';
        const hasSecurity = this.state.security !== 'none';

        // Disable GUI options if web framework selected (except Streamlit)
        const tkinterRadio = document.querySelector('input[name="gui"][value="tkinter"]');
        const pyqt5Radio = document.querySelector('input[name="gui"][value="pyqt5"]');

        if (hasWebFramework) {
            if (tkinterRadio) {
                tkinterRadio.disabled = true;
                tkinterRadio.parentElement.style.opacity = '0.5';
            }
            if (pyqt5Radio) {
                pyqt5Radio.disabled = true;
                pyqt5Radio.parentElement.style.opacity = '0.5';
            }
        } else {
            if (tkinterRadio) {
                tkinterRadio.disabled = false;
                tkinterRadio.parentElement.style.opacity = '1';
            }
            if (pyqt5Radio) {
                pyqt5Radio.disabled = false;
                pyqt5Radio.parentElement.style.opacity = '1';
            }
        }

        // Disable web frameworks if desktop GUI selected
        if (this.state.gui === 'tkinter' || this.state.gui === 'pyqt5') {
            const webFrameworkRadios = document.querySelectorAll('input[name="webFramework"]:not([value="none"])');
            webFrameworkRadios.forEach(radio => {
                radio.disabled = true;
                radio.parentElement.style.opacity = '0.5';
            });
        } else {
            const webFrameworkRadios = document.querySelectorAll('input[name="webFramework"]');
            webFrameworkRadios.forEach(radio => {
                radio.disabled = false;
                radio.parentElement.style.opacity = '1';
            });
        }

        // Disable security if no web framework
        if (!hasWebFramework) {
            const securityRadios = document.querySelectorAll('input[name="security"]:not([value="none"])');
            securityRadios.forEach(radio => {
                radio.disabled = true;
                radio.parentElement.style.opacity = '0.5';
            });

            // Reset security selection if web framework removed
            if (hasSecurity) {
                this.state.security = 'none';
                document.querySelector('input[name="security"][value="none"]').checked = true;
            }
        } else {
            const securityRadios = document.querySelectorAll('input[name="security"]');
            securityRadios.forEach(radio => {
                radio.disabled = false;
                radio.parentElement.style.opacity = '1';
            });
        }

        // Show warnings
        this.showValidationWarnings();
    }

    showValidationWarnings() {
        const statusElement = document.getElementById('generationStatus');
        const warnings = [];

        // Check if security selected without web framework
        if (this.state.security !== 'none' && this.state.webFramework === 'none') {
            warnings.push('Security requires a web framework');
        }

        // Check if no testing framework
        if (!this.state.pytest && !this.state.unittest) {
            warnings.push('No testing framework selected (recommended to add pytest or unittest)');
        }

        // Display warnings
        if (warnings.length > 0) {
            statusElement.innerHTML = warnings.map(w => `⚠️ ${w}`).join('<br>');
            statusElement.className = 'generation-status warning';
        } else if (statusElement.classList.contains('warning')) {
            statusElement.innerHTML = '';
            statusElement.className = 'generation-status';
        }
    }

    updateSelectedComponents() {
        const container = document.getElementById('selectedComponentsList');
        const components = [];

        // Add databases (can be multiple)
        this.state.databases.forEach(db => {
            components.push({
                id: db,
                name: db.charAt(0).toUpperCase() + db.slice(1),
                type: 'Database'
            });
        });

        // Add web framework if selected
        if (this.state.webFramework !== 'none') {
            components.push({
                id: this.state.webFramework,
                name: this.state.webFramework.charAt(0).toUpperCase() + this.state.webFramework.slice(1),
                type: 'Web Framework'
            });
        }

        // Add security if selected
        if (this.state.security !== 'none') {
            const securityNames = {
                'jwt': 'JWT',
                'oauth2': 'OAuth2',
                'basicauth': 'Basic Auth',
                'apikey': 'API Key'
            };
            components.push({
                id: this.state.security,
                name: securityNames[this.state.security] || this.state.security,
                type: 'Security'
            });
        }

        // Add GUI framework if selected
        if (this.state.gui !== 'none') {
            const guiNames = {
                'tkinter': 'Tkinter',
                'pyqt5': 'PyQt5',
                'streamlit': 'Streamlit'
            };
            components.push({
                id: this.state.gui,
                name: guiNames[this.state.gui] || this.state.gui,
                type: 'GUI'
            });
        }

        // Add pytest if selected
        if (this.state.pytest) {
            components.push({
                id: 'pytest',
                name: 'pytest',
                type: 'Testing'
            });
        }

        // Add unittest if selected
        if (this.state.unittest) {
            components.push({
                id: 'unittest',
                name: 'unittest',
                type: 'Testing'
            });
        }

        // Add Docker if selected
        if (this.state.docker) {
            components.push({
                id: 'docker',
                name: 'Docker',
                type: 'Container'
            });
        }

        // Render component chips
        if (components.length === 0) {
            container.innerHTML = '<div class="text-muted" style="padding: 1rem; text-align: center;">No components selected</div>';
        } else {
            container.innerHTML = components.map(comp => `
                <div class="component-chip fade-in">
                    <span class="chip-icon">✓</span>
                    <span class="chip-text">${comp.name}</span>
                    <span class="chip-type">${comp.type}</span>
                </div>
            `).join('');
        }
    }

    updateFileTree() {
        const container = document.getElementById('fileTreeContent');
        const files = [];

        // Base files (always included)
        files.push('main.py', 'requirements.txt', 'README.md', '.gitignore', '.env.template');

        // Config directory
        if (this.state.databases.length > 0 || this.state.webFramework !== 'none' || this.state.security !== 'none') {
            files.push('config/');
            files.push('config/config.py');

            // Add db_connection.py for each database
            this.state.databases.forEach(db => {
                files.push(`config/db_connection_${db}.py`);
            });
            if (this.state.security !== 'none') {
                const securityFiles = {
                    'jwt': 'jwt_handler.py',
                    'oauth2': 'oauth_handler.py',
                    'basicauth': 'auth_handler.py',
                    'apikey': 'auth_handler.py'
                };
                if (securityFiles[this.state.security]) {
                    files.push('config/' + securityFiles[this.state.security]);
                }
            }
        }

        // Web framework specific files
        if (this.state.webFramework === 'flask') {
            files.push('app.py');
            files.push('routes.py');
        } else if (this.state.webFramework === 'fastapi') {
            files.push('routers.py');
        } else if (this.state.webFramework === 'django') {
            files.push('manage.py');
            files.push('${projectName}/');
            files.push('${projectName}/settings.py');
            files.push('${projectName}/urls.py');
        }

        // GUI framework specific files
        if (this.state.gui === 'tkinter' || this.state.gui === 'pyqt5') {
            files.push('gui_app.py');
        } else if (this.state.gui === 'streamlit') {
            files.push('streamlit_app.py');
        }

        // Testing files
        if (this.state.pytest || this.state.unittest) {
            files.push('tests/');
            if (this.state.unittest) {
                files.push('tests/test_example.py');
            }
        }

        // Docker files
        if (this.state.docker) {
            files.push('Dockerfile');
            files.push('docker-compose.yml');
            files.push('.dockerignore');
        }

        // Render file tree
        container.innerHTML = files.map(file => {
            const isDir = file.endsWith('/');
            const icon = isDir ? '📁' : '📄';
            const displayName = file.includes('${projectName}') ?
                file.replace('${projectName}', this.state.projectName || 'my_project') :
                file;
            return `
                <div class="tree-item fade-in">
                    <span class="tree-icon">${icon}</span>
                    <span class="tree-name">${displayName}</span>
                </div>
            `;
        }).join('');
    }

    updateBadges() {
        // Update database badge
        const databaseBadge = document.getElementById('databaseBadge');
        if (this.state.databases.length === 0) {
            databaseBadge.textContent = 'NONE';
            databaseBadge.classList.remove('active');
        } else if (this.state.databases.length === 1) {
            databaseBadge.textContent = this.state.databases[0].toUpperCase();
            databaseBadge.classList.add('active');
        } else {
            databaseBadge.textContent = `${this.state.databases.length} SELECTED`;
            databaseBadge.classList.add('active');
        }

        // Update web framework badge
        const webFrameworkBadge = document.getElementById('webFrameworkBadge');
        if (this.state.webFramework === 'none') {
            webFrameworkBadge.textContent = 'NONE';
            webFrameworkBadge.classList.remove('active');
        } else {
            webFrameworkBadge.textContent = this.state.webFramework.toUpperCase();
            webFrameworkBadge.classList.add('active');
        }

        // Update security badge
        const securityBadge = document.getElementById('securityBadge');
        if (this.state.security === 'none') {
            securityBadge.textContent = 'NONE';
            securityBadge.classList.remove('active');
        } else {
            securityBadge.textContent = this.state.security.toUpperCase();
            securityBadge.classList.add('active');
        }

        // Update GUI badge
        const guiBadge = document.getElementById('guiBadge');
        if (this.state.gui === 'none') {
            guiBadge.textContent = 'NONE';
            guiBadge.classList.remove('active');
        } else {
            guiBadge.textContent = this.state.gui.toUpperCase();
            guiBadge.classList.add('active');
        }

        // Update Testing badge
        const testingBadge = document.getElementById('testingBadge');
        const testingComponents = [];
        if (this.state.pytest) testingComponents.push('PYTEST');
        if (this.state.unittest) testingComponents.push('UNITTEST');

        if (testingComponents.length === 0) {
            testingBadge.textContent = 'NONE';
            testingBadge.classList.remove('active');
        } else {
            testingBadge.textContent = testingComponents.join(' + ');
            testingBadge.classList.add('active');
        }

        // Update Docker badge
        const dockerBadge = document.getElementById('dockerBadge');
        if (this.state.docker) {
            dockerBadge.textContent = 'ENABLED';
            dockerBadge.classList.add('active');
        } else {
            dockerBadge.textContent = 'NONE';
            dockerBadge.classList.remove('active');
        }
    }

    validateForm() {
        const generateBtn = document.getElementById('generateBtn');
        const isValid = this.state.projectName && this.validateProjectName();
        generateBtn.disabled = !isValid;
    }

    async generateProject() {
        if (!this.validateProjectName()) {
            this.showError('Please fix validation errors before generating.');
            return;
        }

        // Build component list
        const components = [];

        // Add all selected databases
        components.push(...this.state.databases);

        // Add web framework
        if (this.state.webFramework !== 'none') {
            components.push(this.state.webFramework);
        }

        // Add security
        if (this.state.security !== 'none') {
            components.push(this.state.security);
        }

        // Add GUI
        if (this.state.gui !== 'none') {
            components.push(this.state.gui);
        }

        // Add testing frameworks
        if (this.state.pytest) {
            components.push('pytest');
        }
        if (this.state.unittest) {
            components.push('unittest');
        }

        // Add Docker
        if (this.state.docker) {
            components.push('docker');
        }

        // Build request
        const request = {
            projectName: this.state.projectName,
            description: this.state.description || `A Python project generated by Genesis`,
            pythonVersion: this.state.pythonVersion,
            authorName: this.state.authorName || undefined,
            authorEmail: this.state.authorEmail || undefined,
            components: components
        };

        console.log('Generating project with config:', request);

        // Show loading overlay
        this.showLoading();

        try {
            // Call API
            const blob = await this.api.generateProject(request);

            // Download the ZIP file
            this.downloadZip(blob, `${this.state.projectName}.zip`);

            // Show success message
            this.showSuccess('Project generated successfully!');

            console.log('✓ Project generated successfully');
        } catch (error) {
            console.error('Generation failed:', error);
            this.showError(error.message || 'Failed to generate project. Please try again.');
        } finally {
            this.hideLoading();
        }
    }

    downloadZip(blob, filename) {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    }

    showLoading() {
        const overlay = document.getElementById('loadingOverlay');
        overlay.classList.add('active');
    }

    hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        overlay.classList.remove('active');
    }

    showSuccess(message) {
        const statusElement = document.getElementById('generationStatus');
        statusElement.textContent = message;
        statusElement.className = 'generation-status success';

        setTimeout(() => {
            statusElement.textContent = '';
            statusElement.className = 'generation-status';
        }, 5000);
    }

    showError(message) {
        const modal = document.getElementById('errorModal');
        const errorMessage = document.getElementById('errorMessage');

        errorMessage.textContent = message;
        modal.classList.add('active');
    }

    hideModal() {
        const modal = document.getElementById('errorModal');
        modal.classList.remove('active');
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.genesisApp = new GenesisApp();
});
