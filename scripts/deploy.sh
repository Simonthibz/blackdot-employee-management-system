#!/bin/bash

# Blackdot Employee Management System - Deployment Script
# ========================================================
# This script handles the deployment of the application

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="blackdot-ems"
DOCKER_COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed or not in PATH"
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        log_error "Docker daemon is not running"
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

setup_environment() {
    log_info "Setting up environment..."
    
    # Copy environment file if it doesn't exist
    if [ ! -f "$ENV_FILE" ]; then
        if [ -f ".env.example" ]; then
            cp .env.example $ENV_FILE
            log_warning "Created $ENV_FILE from .env.example. Please review and modify as needed."
        else
            log_error ".env.example file not found"
            exit 1
        fi
    fi
    
    # Create logs directory
    mkdir -p logs
    chmod 755 logs
    
    log_success "Environment setup completed"
}

build_application() {
    log_info "Building application..."
    
    # Build Docker images
    docker-compose build --no-cache
    
    log_success "Application build completed"
}

start_services() {
    log_info "Starting services..."
    
    # Start services
    docker-compose up -d
    
    # Wait for services to be healthy
    log_info "Waiting for services to be ready..."
    
    # Wait for database
    local max_attempts=30
    local attempt=1
    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T postgres pg_isready -U postgres -d blackdot_ems &> /dev/null; then
            log_success "Database is ready"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "Database failed to start within timeout"
            docker-compose logs postgres
            exit 1
        fi
        
        log_info "Waiting for database... (attempt $attempt/$max_attempts)"
        sleep 5
        ((attempt++))
    done
    
    # Wait for application
    attempt=1
    max_attempts=20
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            log_success "Application is ready"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "Application failed to start within timeout"
            docker-compose logs app
            exit 1
        fi
        
        log_info "Waiting for application... (attempt $attempt/$max_attempts)"
        sleep 10
        ((attempt++))
    done
    
    log_success "All services are running"
}

show_status() {
    log_info "Service Status:"
    docker-compose ps
    
    echo ""
    log_info "Application URLs:"
    echo "  • Main Application: http://localhost:8080"
    echo "  • Health Check: http://localhost:8080/actuator/health"
    echo "  • Database: localhost:5432"
    
    echo ""
    log_info "Default Credentials:"
    echo "  • Admin: admin@blackdot.com / admin123"
    echo "  • HR Manager: hr@blackdot.com / hr123"
}

stop_services() {
    log_info "Stopping services..."
    docker-compose down
    log_success "Services stopped"
}

cleanup() {
    log_info "Cleaning up..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    log_success "Cleanup completed"
}

backup_data() {
    local backup_dir="backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$backup_dir"
    
    log_info "Creating backup in $backup_dir..."
    
    # Backup database
    docker-compose exec -T postgres pg_dump -U postgres blackdot_ems > "$backup_dir/database.sql"
    
    # Backup application logs
    docker-compose logs > "$backup_dir/application.log" 2>&1
    
    log_success "Backup created in $backup_dir"
}

show_logs() {
    local service=${1:-""}
    
    if [ -n "$service" ]; then
        docker-compose logs -f "$service"
    else
        docker-compose logs -f
    fi
}

# Main script logic
case "$1" in
    "deploy")
        log_info "Starting deployment..."
        check_prerequisites
        setup_environment
        build_application
        start_services
        show_status
        ;;
    "start")
        log_info "Starting services..."
        docker-compose up -d
        show_status
        ;;
    "stop")
        stop_services
        ;;
    "restart")
        stop_services
        sleep 2
        docker-compose up -d
        show_status
        ;;
    "status")
        show_status
        ;;
    "logs")
        show_logs "$2"
        ;;
    "backup")
        backup_data
        ;;
    "cleanup")
        cleanup
        ;;
    "build")
        build_application
        ;;
    *)
        echo "Blackdot Employee Management System - Deployment Script"
        echo ""
        echo "Usage: $0 {deploy|start|stop|restart|status|logs|backup|cleanup|build}"
        echo ""
        echo "Commands:"
        echo "  deploy   - Full deployment (build, start, and setup)"
        echo "  start    - Start existing services"
        echo "  stop     - Stop all services"
        echo "  restart  - Restart all services"
        echo "  status   - Show service status and URLs"
        echo "  logs     - Show logs (optionally specify service name)"
        echo "  backup   - Create database and logs backup"
        echo "  cleanup  - Stop services and clean up containers/volumes"
        echo "  build    - Build Docker images only"
        echo ""
        echo "Examples:"
        echo "  $0 deploy          # Full deployment"
        echo "  $0 logs app        # Show application logs"
        echo "  $0 logs postgres   # Show database logs"
        exit 1
        ;;
esac