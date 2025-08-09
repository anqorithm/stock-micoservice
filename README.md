# Stock Market Microservice

> A Spring Boot microservice for stock market data management with comprehensive CRUD operations, advanced search capabilities, and robust testing coverage.

## Project Status

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Coverage](https://img.shields.io/badge/Coverage-99%25-brightgreen?style=flat-square&logo=codecov)
![Tests](https://img.shields.io/badge/Tests-240%20passing-brightgreen?style=flat-square&logo=junit5)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square&logo=github-actions)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

## Test Coverage & Quality

| Metric | Covered | Total | Percent |
|--------|---------|-------|---------|
| Instructions | 2,376 | 2,400 | 99.0% |
| Branches | 110 | 120 | 91.7% |
| Lines | 527 | 534 | 98.7% |
| Methods | 173 | 173 | 100% |
| Classes | 11 | 11 | 100% |

**Test Suite Breakdown:**
- **Unit Tests**: 240 tests across all layers
- **Entity Tests**: 19 tests (validation & business logic)
- **Repository Tests**: 40 tests (JPA operations with H2)
- **Service Tests**: 42 tests (business logic with Mockito)
- **Controller Tests**: 71 tests (REST API with MockMvc)
- **Exception Tests**: 67 tests (error handling scenarios)
- **Config Tests**: 1 test (cache configuration)

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

## Example Request

```bash
$ curl -X POST http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
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
- **Database**: PostgreSQL 15
- **Data Access**: JPA/Hibernate + JDBC Template
- **Testing**: JUnit 5, Mockito, MockMvc
- **Coverage**: JaCoCo
- **Build Tool**: Maven
- **Connection Pool**: HikariCP

## Key Features

- **CRUD Operations**: Complete stock management functionality
- **Advanced Search**: Filter by sector, industry, price range, performance
- **Dual Data Access**: JPA for writes, JDBC for optimized reads
- **Input Validation**: Comprehensive validation with error handling
- **High Test Coverage**: 99% instruction coverage with 240 tests
- **RESTful Design**: Clean API following REST principles
- **Health Monitoring**: Built-in health checks and metrics

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.