# Stock Market Microservice - Makefile
# Common development tasks

.PHONY: help build test clean run stop start-db stop-db coverage verify docker-build docker-run

# Default target
help:
	@echo "Stock Market Microservice - Available Commands:"
	@echo ""
	@echo "Development:"
	@echo "  make build         - Build the application"
	@echo "  make test          - Run all tests"
	@echo "  make coverage      - Run tests with coverage report"
	@echo "  make verify        - Run tests with coverage validation"
	@echo "  make run           - Run the application"
	@echo "  make clean         - Clean build artifacts"
	@echo ""
	@echo "Database:"
	@echo "  make start-db      - Start PostgreSQL database"
	@echo "  make stop-db       - Stop PostgreSQL database"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build  - Build Docker image"
	@echo "  make docker-run    - Run application in Docker"
	@echo ""
	@echo "Utilities:"
	@echo "  make logs          - View application logs"
	@echo "  make health        - Check application health"

# Build the application
build:
	@echo "Building Stock Market Microservice..."
	./mvnw clean compile

# Run all tests
test:
	@echo "Running tests..."
	./mvnw test

# Run tests with coverage report
coverage:
	@echo "Running tests with coverage..."
	./mvnw clean test jacoco:report
	@echo "Coverage report available at: target/site/jacoco/index.html"

# Run tests with coverage validation
verify:
	@echo "Running tests with coverage validation..."
	./mvnw verify

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	./mvnw clean

# Run the application
run: start-db
	@echo "Starting Stock Market Microservice..."
	@echo "Application will be available at http://localhost:8080"
	./mvnw spring-boot:run

# Start PostgreSQL database
start-db:
	@echo "Starting PostgreSQL database..."
	docker-compose up -d
	@echo "Waiting for database to be ready..."
	@sleep 5

# Stop PostgreSQL database
stop-db:
	@echo "Stopping PostgreSQL database..."
	docker-compose down

# Build Docker image
docker-build: build
	@echo "Building Docker image..."
	docker build -t stock-market-microservice .

# Run application in Docker
docker-run: docker-build
	@echo "Running application in Docker..."
	docker-compose -f docker-compose.yml -f docker-compose.app.yml up

# View application logs
logs:
	@echo "Viewing application logs..."
	docker-compose logs -f app

# Check application health
health:
	@echo "Checking application health..."
	@curl -s http://localhost:8080/stocks/health | grep -q "UP" && echo "✅ Application is healthy" || echo "❌ Application is not responding"

# Quick development setup
setup: start-db build test
	@echo "✅ Development environment is ready!"
	@echo "Run 'make run' to start the application"

# Full CI pipeline
ci: clean build test coverage verify
	@echo "✅ CI pipeline completed successfully"

# Development workflow
dev: clean start-db build test run

# Package application
package:
	@echo "Packaging application..."
	./mvnw clean package -DskipTests

# Install dependencies
install:
	@echo "Installing dependencies..."
	./mvnw dependency:resolve

# Format code (if spotless is added)
format:
	@echo "Formatting code..."
	./mvnw spotless:apply || echo "Spotless not configured"

# Security check (if OWASP dependency check is added)
security:
	@echo "Running security checks..."
	./mvnw dependency-check:check || echo "OWASP dependency check not configured"

# Performance test endpoint
perf-test:
	@echo "Running basic performance test..."
	@for i in {1..10}; do \
		curl -s -o /dev/null -w "Request $$i: %{time_total}s\n" http://localhost:8080/stocks/health; \
	done