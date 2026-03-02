# 🚀 GENESIS

**Python Project Initializr** - Generate production-ready Python projects in seconds

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Production%20Ready-success.svg)]()
</div>

---

## 📖 Overview

**Genesis** is a modern, NASA-themed web application that generates complete, production-ready Python projects with your choice of frameworks, databases, security, and more. Think of it as **Spring Initializr, but for Python** - select your components through a beautiful mission-control interface, and Genesis generates a fully-configured project with starter code, documentation, and one-command setup scripts.

### Why Genesis?

Setting up a new Python project with the right tools, configurations, and boilerplate code can take hours. Genesis reduces this to **seconds**, giving you:

- ✨ **Zero-config setup** - Working projects with one command
- 🎯 **Best practices built-in** - Professional code structure
- 📚 **Comprehensive documentation** - Every component fully documented
- 🔧 **Enterprise-grade code** - Clean, maintainable, production-ready
- ⚡ **Instant gratification** - From idea to running app in 60 seconds

---

## ✨ Features

### 🎨 **NASA Mission-Control UI**
- Beautiful dark-themed interface inspired by NASA mission control
- Real-time component preview and validation
- Intuitive drag-free selection with visual feedback
- Responsive design (desktop, tablet, mobile)

### 🧩 **18 Components Across 6 Categories**
- **5 Databases:** PostgreSQL, MySQL, MongoDB, SQLite, Redis
- **3 Web Frameworks:** Flask, FastAPI, Django
- **4 Security Options:** JWT, OAuth2, Basic Auth, API Key
- **3 GUI Frameworks:** Tkinter, PyQt5, Streamlit
- **2 Testing Frameworks:** pytest, unittest
- **Docker Support:** Dockerfile + docker-compose with all services

### 🧠 **Smart Validation**
- Prevents invalid component combinations
- Real-time conflict detection
- Automatic option disabling for incompatible choices
- Helpful error messages with suggestions

### 🎯 **Project Templates (One-Click Presets)**
- **🌐 REST API** - FastAPI + PostgreSQL + JWT + Docker
- **📊 Data Dashboard** - Streamlit + PostgreSQL + Redis
- **🤖 ML Pipeline** - PostgreSQL + pytest for data science
- **💻 Desktop App** - PyQt5 + SQLite + unittest

### ⚡ **One-Command Setup**
Generated projects include automated scripts:
- `./setup_venv.sh` - Creates environment + installs dependencies
- `./run_dev.sh` - Starts your application instantly
- Cross-platform (Linux, macOS, Windows)

### 💾 **Save/Load Configurations**
- Export your selections as JSON
- Share configurations with team
- Reuse for similar projects
- Version control your project setup

### 📦 **Advanced Features**
- Multiple database support (PostgreSQL + Redis, etc.)
- YAML-based component system (easy to extend)
- Template caching for performance
- Comprehensive logging and metrics
- Graceful error handling
- 50+ unit and integration tests

---

## 🎬 Quick Start

### Prerequisites
- Java 21 (required for Lombok compatibility)
- Maven 3.6+

### Running Genesis

```bash
# 1. Clone the repository
git clone https://github.com/ByteSizedLaw/Genesis.git
cd genesis

# 2. Set Java 21 (if not default)
export JAVA_HOME=/path/to/jdk-21  # Linux/macOS
set JAVA_HOME=C:\Program Files\Java\jdk-21  # Windows

# 3. Start Genesis
./mvnw spring-boot:run

# 4. Open your browser
# Navigate to: http://localhost:8080
```

### Generating Your First Project

1. **Enter project details** (name, description, Python version)
2. **Select components** (databases, web framework, security, etc.)
   - Or click a **project template** for instant configuration
3. **Click "INITIATE GENERATION"**
4. **Download** your project ZIP automatically

### Using Your Generated Project

```bash
# Extract and enter directory
unzip my_awesome_project.zip
cd my_awesome_project

# One-command setup (Linux/macOS)
./setup_venv.sh

# Or Windows
setup_venv.bat

# Configure environment
cp .env.template .env
# Edit .env with your settings

# Run!
./run_dev.sh  # Linux/macOS
run_dev.bat   # Windows
```

**Your app is now running!** 🎉

---

## 🏗️ Architecture

### Technology Stack

**Backend:**
- **Spring Boot 3.2.0** - RESTful API framework
- **Java 21** - Modern Java with records and pattern matching
- **Apache FreeMarker** - Powerful template engine for code generation
- **Apache Commons Compress** - ZIP file generation
- **SnakeYAML** - Component metadata configuration
- **Caffeine** - High-performance caching
- **Lombok** - Reduced boilerplate code

**Frontend:**
- **Vanilla JavaScript (ES6+)** - No framework needed, full control
- **Custom CSS** - NASA-themed design system
- **Fetch API** - Modern HTTP client

**Testing:**
- **JUnit 5** - Unit testing framework
- **MockMvc** - Integration testing
- **AssertJ** - Fluent assertions
- **50+ test cases** with 85%+ coverage

### Architecture Highlights

```
┌─────────────────────────────────────────────────────────────┐
│                     NASA-Themed Frontend                    │
│  (HTML + CSS + JavaScript - Mission Control Interface)      │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API (JSON)
┌────────────────────────┴────────────────────────────────────┐
│                   Spring Boot Backend                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ Controllers  │→ │  Services    │→ │  Metadata    │       │
│  │ (REST API)   │  │ (Business    │  │  (YAML)      │       │
│  └──────────────┘  │  Logic)      │  └──────────────┘       │
│                    └──────┬───────┘                         │
│                           │                                 │
│                    ┌──────▼───────┐                         │
│                    │  FreeMarker  │                         │
│                    │  Templates   │                         │
│                    └──────┬───────┘                         │
│                           │                                 │
│                    ┌──────▼───────┐                         │
│                    │ ZIP Service  │                         │
│                    └──────────────┘                         │
└────────────────────────┬────────────────────────────────────┘
                         │ Download
                    ┌────▼─────┐
                    │   ZIP    │
                    │  Project │
                    └──────────┘
```

**Key Design Principles:**
- **Separation of Concerns** - Clear boundaries between layers
- **Template-Based Generation** - FreeMarker for flexible code generation
- **YAML Configuration** - Easy to add new components without code changes
- **Validation First** - Prevent errors before generation
- **Performance Optimized** - Template caching, efficient ZIP generation

---

## 📦 What Genesis Generates

Every generated project includes:

### Base Structure
```
my_project/
├── main.py                    # Application entry point
├── requirements.txt           # All dependencies (auto-generated)
├── README.md                  # Complete setup guide
├── .gitignore                 # Python best practices
├── .env.template              # Environment variables
├── config/
│   ├── config.py             # Configuration management
│   └── db_connection_*.py    # Database connections (if selected)
├── setup_venv.sh             # Automated setup (Linux/macOS)
├── setup_venv.bat            # Automated setup (Windows)
├── run_dev.sh                # Run script (Linux/macOS)
└── run_dev.bat               # Run script (Windows)
```

### Plus Component-Specific Files

**Web Frameworks:**
- Flask: `app.py`, `routes.py` with CORS, blueprints
- FastAPI: `main.py`, `routers.py` with Swagger docs
- Django: `manage.py`, `settings.py`, `urls.py` with admin

**Databases:**
- Connection modules with SQLAlchemy/pymongo/redis
- Comprehensive usage documentation
- Connection pooling and error handling
- Example CRUD operations

**Security:**
- JWT: Token creation, validation, route protection
- OAuth2: Google/GitHub integration with Authlib
- Basic Auth: HTTP authentication decorators
- API Key: Key validation and management

**GUI:**
- Tkinter: Cross-platform desktop app with widgets
- PyQt5: Professional Qt-based application
- Streamlit: Interactive data dashboard

**Docker:**
- Multi-stage Dockerfile
- docker-compose.yml with database services
- .dockerignore for optimized builds

---

## 🎯 Use Cases

### For Individual Developers
- **Rapid Prototyping** - Go from idea to working app in minutes
- **Learning** - See best practices for any stack combination
- **Side Projects** - Professional structure without the setup time

### For Teams
- **Standardization** - Consistent project structure across team
- **Onboarding** - New developers get working environment instantly
- **Microservices** - Generate multiple services quickly
- **Templates** - Save team's preferred configurations

### For Educators
- **Teaching** - Students start coding immediately, not configuring
- **Workshops** - Everyone has identical setup
- **Demonstrations** - Quick project creation for examples

### For Enterprises
- **Productivity** - Developers spend time on features, not boilerplate
- **Standards Compliance** - Enforced best practices
- **Documentation** - Every project well-documented
- **Quality** - Enterprise-grade code from day one

---

## 🔥 Example Scenarios

### Scenario 1: REST API with Authentication
**Need:** Build a REST API with JWT authentication and PostgreSQL

**Genesis Configuration:**
- Web Framework: FastAPI
- Database: PostgreSQL
- Security: JWT
- Testing: pytest
- Docker: Enabled

**Result:** Complete FastAPI project with:
- JWT authentication handlers
- PostgreSQL connection with SQLAlchemy
- Interactive API docs at `/docs`
- Docker deployment ready
- Comprehensive tests
- **Ready to deploy in production!**

---

### Scenario 2: Data Dashboard
**Need:** Create an analytics dashboard with caching

**Genesis Configuration:**
- GUI: Streamlit
- Databases: PostgreSQL + Redis
- Testing: pytest

**Result:** Streamlit dashboard with:
- PostgreSQL for data storage
- Redis for caching and sessions
- Interactive visualizations
- Database connection examples
- **Deploy with `streamlit run`**

---

### Scenario 3: Desktop Application
**Need:** Build a cross-platform desktop app

**Genesis Configuration:**
- GUI: PyQt5
- Database: SQLite
- Testing: unittest

**Result:** Professional desktop app with:
- Modern Qt interface
- Embedded SQLite database
- Example widgets and layouts
- Built-in testing
- **Distribute as executable**

---

## 🛠️ Development

### Project Structure

```
genesis/
├── src/main/java/com/stephen_oosthuizen/genesis/
│   ├── config/          # Spring configuration (cache, web, FreeMarker)
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Request/response objects
│   ├── exception/       # Custom exceptions & global handler
│   ├── metadata/        # Component registry & YAML loader
│   ├── model/           # Domain models (Component, ProjectConfig, etc.)
│   └── service/         # Business logic (generation, templates, ZIP)
│
├── src/main/resources/
│   ├── templates/python/
│   │   ├── metadata/components.yaml     # All component definitions
│   │   ├── base/        # Base project templates
│   │   ├── database/    # Database connection templates (5 types)
│   │   ├── web/         # Web framework templates (3 types)
│   │   ├── security/    # Security templates (4 types)
│   │   ├── gui/         # GUI framework templates (3 types)
│   │   ├── docker/      # Docker support templates
│   │   └── scripts/     # Setup and run scripts
│   │
│   ├── static/          # Frontend assets
│   │   ├── index.html
│   │   ├── css/         # NASA-themed design system
│   │   └── js/          # Application logic + API client
│   │
│   └── application.yml  # Spring Boot configuration
│
├── src/test/java/       # Comprehensive test suite (50+ tests)
│   ├── service/         # Unit tests for services
│   ├── metadata/        # Tests for YAML loading
│   └── integration/     # Full API integration tests
│
└── pom.xml              # Maven dependencies
```

### Adding New Components

Thanks to the YAML-based system, adding new components is simple:

1. **Add to `components.yaml`:**
```yaml
newframework:
  id: newframework
  name: New Framework
  description: An awesome new framework
  type: WEB_FRAMEWORK
  dependencies:
    - newframework==1.0.0
  templatePaths:
    - web/newframework/app.py.ftl
  conflicts:
    - flask
```

2. **Create templates** in `src/main/resources/templates/python/web/newframework/`

3. **Restart Genesis** - New component appears automatically!

No Java code changes required! 🎉

---

## 🧪 Testing

Genesis has a comprehensive test suite with 50+ tests:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TemplateServiceTest

# Run with coverage report
./mvnw test jacoco:report
```

**Test Coverage:**
- ✅ Template rendering and variable substitution
- ✅ ZIP file generation and structure
- ✅ Component conflict detection
- ✅ YAML metadata loading
- ✅ Complete API integration tests
- ✅ All 18 components validated
- ✅ Error handling and edge cases

**Expected:** 85%+ code coverage

---

## 🌟 Key Technical Features

### Backend
- **RESTful API** - Clean REST endpoints (`/api/v1/generate`, `/api/v1/metadata`)
- **FreeMarker Templates** - Powerful template engine for code generation
- **YAML Configuration** - Metadata-driven component system
- **Validation Layer** - DependencyResolver prevents invalid combinations
- **Caching** - Caffeine cache for template performance
- **Error Handling** - Global exception handler with helpful messages
- **Logging** - Comprehensive logging with request timing

### Frontend
- **Vanilla JavaScript** - No framework overhead, ES6 modules
- **Custom CSS** - NASA mission-control aesthetic
- **Real-time Validation** - Disables incompatible options instantly
- **State Management** - Clean application state handling
- **Progressive Enhancement** - Works without JavaScript for basic functionality

### Code Quality
- **Clean Architecture** - Separation of concerns, SOLID principles
- **Enterprise Patterns** - Service layer, DTOs, builder pattern
- **Comprehensive Tests** - Unit + integration tests
- **Documentation** - JavaDoc + comprehensive guides
- **Type Safety** - Proper typing throughout

---

## 📊 Generated Project Features

Projects generated by Genesis include:

### Professional Structure
- Clean, organized directory layout
- Proper separation of concerns (config, routes, models)
- Industry-standard naming conventions

### Complete Documentation
- **README.md** with quickstart guide
- **Component-specific documentation** (DB_USAGE_INSTRUCTIONS.md, SECURITY_README.md)
- Code comments explaining key concepts
- Troubleshooting sections
- Best practices guides

### Production-Ready Code
- Environment variable configuration
- Logging setup
- Error handling
- Connection pooling (databases)
- CORS configuration (web frameworks)
- Security middleware integration

### Developer Experience
- **One-command setup** - `./setup_venv.sh` does everything
- **One-command run** - `./run_dev.sh` starts your app
- **Pre-configured** - Environment templates, gitignore, etc.
- **Testing ready** - Test structure and examples included

---

## 🎨 Screenshots

### Mission Control Interface
The NASA-themed UI features:
- Dark space backgrounds (#0B0D17) with mission-blue accents (#0E7BC0)
- Clean aerospace typography (Rajdhani + Inter fonts)
- Modular grid layout with left panel (configuration) and right panel (preview)
- Real-time component badges showing selections
- Animated component selection with smooth transitions
- Live file tree preview showing expected project structure

### Features in Action
- **Smart Validation:** Select Flask → Tkinter becomes disabled (incompatible)
- **Project Templates:** One-click to configure "REST API" preset
- **Multi-Select:** Check PostgreSQL + Redis for dual database setup
- **Live Preview:** See your project structure update in real-time
- **Generate Button:** Beautiful gradient button with "INITIATE GENERATION"

## Screenshots of GUI:
UI Info demo:
<image src="https://github.com/ByteSizedLaw/Genesis/blob/609c08d14b2f713ad8ece906e1909572f6cd896d/media/Genesis%20Python%20Initializr%20UI%20demo.png"></image>
<br>
Component selection demo:
<image src="https://github.com/ByteSizedLaw/Genesis/blob/609c08d14b2f713ad8ece906e1909572f6cd896d/media/Genesis%20Python%20Initializr%20UI%20component%20demo.png"></image>
<br>
For video demonstrations, please see the [media folder](https://github.com/ByteSizedLaw/Genesis/tree/main/media) inside this project. 
<br>
I have included demo videos of how to select components and how the system generates code, etc.
---

## 💡 Technical Decisions

### Why Spring Boot for a Python Generator?
- **Mature ecosystem** - Robust, battle-tested framework
- **Easy deployment** - Package as JAR, deploy anywhere
- **Excellent tooling** - IDE support, debugging, testing
- **Performance** - Fast template rendering, efficient ZIP generation
- **Scalability** - Handle concurrent requests easily

### Why FreeMarker?
- **Code generation specialist** - Better than Thymeleaf for text templates
- **Powerful directives** - Conditionals, loops, macros, includes
- **Template inheritance** - DRY principle for templates
- **Type-safe** - Compile-time template checking

### Why Vanilla JavaScript?
- **Full control** - Custom NASA aesthetic without framework constraints
- **Lightweight** - No build process, instant page load
- **Simple** - Easy to understand and modify
- **Sufficient** - UI complexity doesn't warrant React/Vue

### Why YAML for Metadata?
- **Easy to edit** - Human-readable, simple syntax
- **Version control** - Git-friendly, clear diffs
- **No DB required** - Components change infrequently
- **Type-safe parsing** - SnakeYAML with validation

---

## 🤝 Contributing

I am not accepting contributions to this repository, but you are welcome to change it within your own environment.
<br>
Here's how you would usually collaborate with other software engineers in your respective environment:

### Adding New Components

1. Fork the repository
2. Add component to `src/main/resources/templates/python/metadata/components.yaml`
3. Create templates in appropriate directory
4. Add tests
5. Submit pull request

### Improving Templates

- Enhance existing templates with better practices
- Add more comprehensive documentation
- Improve error handling in generated code
- Add more examples

### UI Enhancements

- Improved themes
- Add more project templates
- Enhance validation messages
- Add keyboard shortcuts

### Testing

- Add more test cases
- Improve test coverage
- Add performance benchmarks
- Add browser automation tests

---

## 📈 Roadmap

### Current Version: 1.0.0 ✅
- [x] 18 components across 6 categories
- [x] NASA-themed UI
- [x] Smart validation
- [x] Project templates
- [x] YAML-based components
- [x] Comprehensive tests

### Future Enhancements
- [ ] More components (FastAPI WebSockets, Celery, GraphQL)
- [ ] More project templates (Microservice, CLI Tool, API Gateway)
- [ ] Component search/filter
- [ ] CLI version of Genesis
- [ ] VS Code extension
- [ ] Multi-language support (Node.js, Go, Rust)
- [ ] Custom component definitions (user-defined templates)

---

## 📄 API Documentation

### Endpoints

#### `GET /api/v1/metadata`
Returns all available components and configuration options.

**Response:**
```json
{
  "version": "1.0.0",
  "defaultPythonVersion": "3.11",
  "supportedPythonVersions": ["3.9", "3.10", "3.11", "3.12"],
  "components": [
    {
      "id": "flask",
      "name": "Flask",
      "description": "Lightweight WSGI web framework",
      "type": "WEB_FRAMEWORK",
      "conflicts": ["django", "fastapi"]
    }
  ]
}
```

#### `POST /api/v1/generate`
Generates a Python project based on selections.

**Request:**
```json
{
  "projectName": "my_awesome_project",
  "description": "My awesome application",
  "pythonVersion": "3.11",
  "authorName": "Your Name",
  "authorEmail": "you@example.com",
  "components": ["fastapi", "postgresql", "jwt", "docker"]
}
```

**Response:**
- Content-Type: `application/octet-stream`
- Binary ZIP file download

#### `GET /api/v1/health`
Health check endpoint.

**Response:**
```json
{
  "status": "healthy",
  "application": "Genesis Python Initializr",
  "version": "1.0.0",
  "componentsAvailable": 18,
  "timestamp": "2026-03-02T10:30:00"
}
```

---

## 🎓 Learning Resources

Genesis serves as an excellent learning resource for:

### Backend Development
- RESTful API design with Spring Boot
- Template engine integration (FreeMarker)
- File generation and ZIP streaming
- YAML configuration parsing
- Validation patterns
- Exception handling strategies

### Frontend Development
- Vanilla JavaScript state management
- CSS custom properties and theming
- Fetch API and file downloads
- Form validation
- Real-time UI updates

### Software Engineering
- Clean architecture
- SOLID principles
- Design patterns (Builder, Factory, Template Method)
- Test-driven development
- Documentation best practices

---

## 🐛 Troubleshooting

### Issue: Compilation fails with Lombok error
**Solution:** You're not using Java 21. Genesis requires Java 21, because we use project Lombok to reduce boilerplate code.
```bash
set JAVA_HOME=C:\Program Files\Java\jdk-21
java -version  # Verify shows 21.x.x
./mvnw clean compile
```

### Issue: Port 8080 already in use
**Solution:** Change port in `application.yml` or kill the process:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill
```

### Issue: Frontend shows "Failed to load metadata"
**Solution:** Backend isn't running. Start with `./mvnw spring-boot:run`

### Issue: My Generated project won't run
**Solution:** Check the generated project's README.md for setup instructions. Ensure:
- Virtual environment is activated
- Dependencies are installed
- `.env` file is configured
- Required database servers are running (If applicable)

---

## 📜 License

MIT License
<br>
Copyright (c) 2026 Stephen Oosthuizen

---

## 👨‍💻 Author

**Stephen Oosthuizen**

- GitHub: [ByteSizedLaw](https://github.com/ByteSizedLaw)
- Project: [Genesis](https://github.com/ByteSizedLaw/Genesis)

---

## 🙏 Acknowledgments

- **Spring Boot Team** - Excellent web framework
- **FreeMarker Team** - Powerful template engine
- **Apache Commons** - Reliable utilities
- **Spring Initializr** - Inspiration for this project

---

## 📊 Project Stats

- **Lines of Code:** ~6,000+ (actual code) & 1,500+ (unit tests)
- **Components Supported:** 18
- **Templates Created:** 40+
- **Test Cases:** 50+
- **Documentation Files:** 10+
- **Possible Component Combinations:** 1,000+
- **Specialty:** Optimized for rapid project generation
- **First Release:** 2026-03-02

---

## 🚀 Get Started

```bash
git clone https://github.com/ByteSizedLaw/Genesis.git
cd genesis
./mvnw spring-boot:run
# Visit http://localhost:8080
```

**Generate your first Python project in 60 seconds!** ⚡

---

<div align="center">

**Built with ❤️ using Spring Boot, FreeMarker, and NASA-inspired design**

⭐ **Star this repo if you find it useful!** ⭐

</div>

---
