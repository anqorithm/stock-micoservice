# Stock Market Microservice

> A Spring Boot microservice for stock market data management with **JWT authentication**, comprehensive CRUD operations, advanced search capabilities, and robust testing coverage.

## Project Status

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Coverage](https://img.shields.io/badge/Coverage-94%25-brightgreen?style=flat-square&logo=codecov)
![Tests](https://img.shields.io/badge/Tests-251%20passing-brightgreen?style=flat-square&logo=junit5)
![Security](https://img.shields.io/badge/Security-JWT%20Auth-blue?style=flat-square&logo=jsonwebtokens)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square&logo=github-actions)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

## Test Coverage & Quality

| Metric | Covered | Total | Percent |
|--------|---------|-------|---------|
| Instructions | 3,374 | 3,583 | 94.2% |
| Branches | 145 | 166 | 87.3% |
| Lines | 762 | 812 | 93.8% |
| Methods | 264 | 276 | 95.7% |
| Classes | 22 | 22 | 100% |

**Test Suite Breakdown:**
- **Total Tests**: 251 tests across all layers
- **Entity Tests**: 20 tests (User & Stock validation)
- **Repository Tests**: 55 tests (JPA operations with H2)
- **Service Tests**: 89 tests (business logic with Mockito)
- **Controller Tests**: 14 tests (Authentication API)
- **Security Tests**: 29 tests (JWT & Authentication)
- **Exception Tests**: 37 tests (error handling scenarios)
- **Config Tests**: 11 tests (cache & security configuration)

## Quick Start

### Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **Maven 3.6+**

### Installation & Setup
```bash
$ git clone https://github.com/anqorithm/stock-micoservice.git
$ cd stock-micoservice
$ chmod +x ./mvnw
$ make run
```

**API Available at**: `http://localhost:8080`

### Database Management
- **PostgreSQL Database**: Available at `localhost:5432`
- **pgAdmin Interface**: `http://localhost:5050`
  - Email: `admin@admin.com`
  - Password: `admin`
  - Server: `postgres` (host), Port: `5432`
  - Database: `stocks`
  - Username: `postgres`
  - Password: `password`

### Using Makefile Commands
```bash
$ make help
$ make dev
$ make build
$ make test
$ make coverage
$ make run
$ make start-db
$ make stop-db
$ make health
$ make clean
```

### Manual Setup
```bash
$ docker-compose up -d
$ ./mvnw spring-boot:run
$ ./mvnw clean test jacoco:report
```

## API Endpoints

### Authentication (JWT)
| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/auth/register` | Register new user | `{"username", "email", "password", "firstName", "lastName"}` | 201 Created |
| POST | `/auth/login` | User login | `{"username", "password"}` | JWT token |
| POST | `/auth/validate` | Validate JWT token | Header: `Authorization: Bearer {token}` | Token validity |
| GET | `/auth/me` | Get current user | Header: `Authorization: Bearer {token}` | User details |

> **Note**: All `/stocks/**` endpoints require valid JWT token in Authorization header

### Stock Management
| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/stocks` | Get all stocks (paginated) | 200 OK |
| GET | `/stocks/{id}` | Get stock by ID | 200 OK / 404 Not Found |
| GET | `/stocks/symbol/{symbol}` | Get stock by symbol | 200 OK / 404 Not Found |
| POST | `/stocks` | Create new stock | 201 Created / 400 Bad Request |
| PUT | `/stocks/{id}` | Update stock | 200 OK / 404 Not Found |
| DELETE | `/stocks/{id}` | Delete stock by ID | 200 OK / 404 Not Found |
| DELETE | `/stocks/symbol/{symbol}` | Delete stock by symbol | 200 OK / 404 Not Found |

### Stock Operations
| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| PATCH | `/stocks/{symbol}/price` | Update stock price | 200 OK / 404 Not Found |
| PATCH | `/stocks/{symbol}/volume` | Update stock volume | 200 OK / 404 Not Found |

### Search & Filter
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|-------------|
| GET | `/stocks/search` | Search stocks by name or symbol | `name`, `symbol`, `page`, `size` |
| GET | `/stocks/sector/{sector}` | Get stocks by sector | `page`, `size` |
| GET | `/stocks/industry/{industry}` | Get stocks by industry | `page`, `size` |
| GET | `/stocks/price-range` | Get stocks in price range | `minPrice`, `maxPrice`, `page`, `size` |

### Analytics
| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|-------------|
| GET | `/stocks/top/market-cap` | Top stocks by market cap | `limit` (default: 10) |
| GET | `/stocks/top/volume` | Top stocks by volume | `limit` (default: 10) |
| GET | `/stocks/performers/high` | High performing stocks | `limit` (default: 10) |
| GET | `/stocks/performers/low` | Low performing stocks | `limit` (default: 10) |
| GET | `/stocks/value-stocks` | Value stocks (low P/E) | `maxPeRatio`, `limit` |
| GET | `/stocks/dividend-stocks` | Dividend paying stocks | `minDividendYield`, `limit` |

### Metadata
| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/stocks/sectors` | Get all sectors | Array of sectors |
| GET | `/stocks/industries` | Get all industries | Array of industries |
| GET | `/stocks/stats/count` | Get stock statistics | Statistics object |
| GET | `/stocks/exists/symbol/{symbol}` | Check if stock exists | Boolean response |

### Batch Operations
| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/stocks/batch` | Create multiple stocks | Array of stock objects |

### Health Check
| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/stocks/health` | API health status | Health status object |

## Example Requests

### Authentication Flow
```bash
# Register a new user
$ curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "email": "user@example.com", 
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Login to get JWT token
$ curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "password123"
  }'
# Response: {"token":"eyJhbGc...", "username":"user123", "email":"user@example.com", "role":"USER"}
```

### Using Protected Endpoints
```bash
# Create stock (requires JWT token)
$ curl -X POST http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -d '{
    "symbol": "AAPL",
    "name": "Apple Inc.",
    "currentPrice": 150.00,
    "sector": "Technology",
    "industry": "Consumer Electronics"
  }'
```

## Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Security**: Spring Security 6 + JWT Authentication
- **Database**: PostgreSQL 15 + H2 (testing)
- **Data Access**: JPA/Hibernate + JDBC Template
- **Authentication**: JSON Web Tokens (JWT) with JJWT library
- **Testing**: JUnit 5, Mockito, MockMvc, DataJpaTest
- **Coverage**: JaCoCo (94% instruction coverage)
- **Build Tool**: Maven
- **Connection Pool**: HikariCP

## Key Features

- **JWT Authentication**: Secure user registration, login, and token-based auth
- **CRUD Operations**: Complete stock management functionality
- **Advanced Search**: Filter by sector, industry, price range, performance
- **Dual Data Access**: JPA for writes, JDBC for optimized reads
- **Input Validation**: Comprehensive validation with error handling
- **High Test Coverage**: 94% instruction coverage with 251 tests
- **RESTful Design**: Clean API following REST principles
- **User Management**: Role-based access control (USER/ADMIN)
- **Security Features**: Password encryption, token validation, protected endpoints
- **Health Monitoring**: Built-in health checks and metrics

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.