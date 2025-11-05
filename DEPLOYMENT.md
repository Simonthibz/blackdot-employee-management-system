# Blackdot Employee Management System - Deployment Guide

# ====================================================

## Overview

This guide provides comprehensive instructions for deploying the Blackdot Employee Management System in your internal environment.

## Prerequisites

### Software Requirements

- **Docker**: Version 20.x or higher
- **Docker Compose**: Version 2.x or higher
- **Git**: For source code management
- **Java 17** (optional, for development)
- **Maven 3.6+** (optional, for development)

### Hardware Requirements

- **Minimum**: 2 CPU cores, 4GB RAM, 20GB disk space
- **Recommended**: 4 CPU cores, 8GB RAM, 50GB disk space
- **Operating System**: Linux, Windows, or macOS with Docker support

## Quick Start

### 1. Clone and Setup

```bash
# Clone the repository
git clone <repository-url>
cd blackdot-employee-management-system

# Copy environment configuration
cp .env.example .env

# Edit environment variables as needed
nano .env  # or use your preferred editor
```

### 2. Deploy Application

#### Using Linux/macOS:

```bash
# Make script executable
chmod +x scripts/deploy.sh

# Deploy the application
./scripts/deploy.sh deploy
```

#### Using Windows PowerShell:

```powershell
# Run deployment script
.\scripts\deploy.ps1 deploy
```

### 3. Access Application

- **Main Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Database**: localhost:5432

### 4. Default Login Credentials

- **Admin**: admin@blackdot.com / admin123
- **HR Manager**: hr@blackdot.com / hr123

## Development with IntelliJ IDEA

For development and debugging, IntelliJ IDEA provides the best experience:

### 1. Import Project

1. Open IntelliJ IDEA
2. Select "Open" and navigate to the project folder
3. Select the `pom.xml` file and click "Open as Project"
4. Wait for Maven to download dependencies

### 2. Configure Database

Ensure PostgreSQL is running locally:

```sql
-- Create database and user
CREATE DATABASE blackdot_ems;
CREATE USER blackdot_user WITH PASSWORD 'blackdot_password';
GRANT ALL PRIVILEGES ON DATABASE blackdot_ems TO blackdot_user;
```

### 3. Run Application in IntelliJ

1. Navigate to `src/main/java/com/blackdot/ems/EmployeeManagementSystemApplication.java`
2. Right-click and select "Run 'EmployeeManagementSystemApplication'"
3. Or click the green play button in the gutter

### 4. View Console Output

Watch the console for:

- Data initialization logs showing default users being created
- Any error messages during startup
- Database connection confirmations

### 5. Access Application

- **Web Interface**: http://localhost:8080
- **Login with**: admin@blackdot.com / admin123

### 6. Debug Tips

- Set breakpoints in `DataInitializer.java` to verify user creation
- Check database tables in IntelliJ's Database tool window
- Use the terminal within IntelliJ for Maven commands

## Detailed Configuration

### Environment Variables

Edit the `.env` file to customize your deployment:

```bash
# Database Configuration
DATABASE_PASSWORD=your_secure_password
DATABASE_PORT=5432

# Application Configuration
APP_PORT=8080
LOG_LEVEL=INFO

# Security Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key
JWT_EXPIRATION=86400000

# Performance Configuration
DB_POOL_SIZE=20
DB_MIN_IDLE=5
JVM_HEAP_SIZE=1024m
```

### SSL/TLS Configuration (Optional)

To enable HTTPS with Nginx reverse proxy:

1. Create SSL certificates:

```bash
mkdir -p nginx/ssl
# Copy your SSL certificates to nginx/ssl/
# - certificate.crt
# - private.key
```

2. Enable Nginx profile:

```bash
docker-compose --profile with-nginx up -d
```

## Management Commands

### Service Management

```bash
# Start services
./scripts/deploy.sh start

# Stop services
./scripts/deploy.sh stop

# Restart services
./scripts/deploy.sh restart

# View status
./scripts/deploy.sh status
```

### Monitoring and Logs

```bash
# View all logs
./scripts/deploy.sh logs

# View specific service logs
./scripts/deploy.sh logs app
./scripts/deploy.sh logs postgres

# Follow logs in real-time
docker-compose logs -f app
```

### Backup and Recovery

```bash
# Create backup
./scripts/deploy.sh backup

# Manual database backup
docker-compose exec postgres pg_dump -U postgres blackdot_ems > backup.sql

# Restore database
docker-compose exec -T postgres psql -U postgres blackdot_ems < backup.sql
```

## Production Deployment

### Security Considerations

1. **Change Default Passwords**: Update all default passwords in `.env`
2. **JWT Secret**: Generate a strong, unique JWT secret key
3. **Database Security**: Use strong database passwords
4. **Network Security**: Configure firewall rules
5. **SSL/TLS**: Enable HTTPS for production use

### Performance Tuning

1. **Database Connection Pool**: Adjust `DB_POOL_SIZE` based on load
2. **JVM Heap**: Increase `JVM_HEAP_SIZE` for higher loads
3. **Database Resources**: Allocate appropriate CPU/memory to PostgreSQL

### Monitoring Setup

1. **Health Checks**: Monitor `/actuator/health` endpoint
2. **Log Aggregation**: Set up centralized logging
3. **Metrics**: Monitor application and database metrics
4. **Alerts**: Configure alerts for system failures

## Troubleshooting

### Common Issues

#### Application Won't Start

```bash
# Check service status
docker-compose ps

# Check application logs
docker-compose logs app

# Check database connectivity
docker-compose exec app curl -f http://localhost:8080/actuator/health
```

#### Database Connection Issues

```bash
# Check database status
docker-compose logs postgres

# Test database connection
docker-compose exec postgres psql -U postgres -d blackdot_ems -c "\dt"

# Reset database
docker-compose down -v
docker-compose up -d
```

#### Performance Issues

```bash
# Check resource usage
docker stats

# Increase JVM heap size in .env
JVM_HEAP_SIZE=2048m

# Restart application
docker-compose restart app
```

#### Spring Data Repository Issues

If you encounter errors like `No property 'created' found for type 'Question'`, check repository method names:

```java
// Correct method name (use exact entity property names)
List<Question> findByAssessmentIdOrderByCreatedAtAsc(Long assessmentId);

// Incorrect - would cause the error above
List<Question> findByAssessmentIdOrderByCreatedAsc(Long assessmentId);
```

This error typically occurs when Spring Data JPA cannot map the method name to entity properties. Always use exact property names from your entity classes.

#### Controller Mapping Conflicts

If you encounter errors like `Ambiguous mapping. Cannot map 'webController' method`, this indicates multiple controllers are mapped to the same URL:

```java
// Problem: Both controllers mapping to the same path
@GetMapping("/")
public String home() { ... }  // in WebController

@GetMapping("/")
public String home() { ... }  // in LoginController - CONFLICT!
```

**Solution**: Remove duplicate mappings or use different paths for each controller method.

### Log Locations

- **Application Logs**: `logs/blackdot-ems.log`
- **Container Logs**: `docker-compose logs`
- **Database Logs**: PostgreSQL container logs

## Maintenance

### Regular Tasks

1. **Daily**: Monitor application health and logs
2. **Weekly**: Create database backups
3. **Monthly**: Update security patches
4. **Quarterly**: Review and rotate credentials

### Updates and Upgrades

```bash
# Pull latest changes
git pull origin main

# Rebuild and deploy
./scripts/deploy.sh build
./scripts/deploy.sh restart
```

### Scaling Considerations

- **Horizontal Scaling**: Deploy multiple app instances behind load balancer
- **Database Scaling**: Consider read replicas for high-read workloads
- **Cache Layer**: Add Redis for session management and caching

## API Documentation

The system provides REST API endpoints for integration:

- **API Documentation**: Available at runtime
- **Health Endpoint**: `/actuator/health`
- **Metrics Endpoint**: `/actuator/metrics`

## Support and Contact

For technical support and questions:

- **System Administrator**: admin@blackdot.com
- **Documentation**: Check README.md and API_DOCUMENTATION.md
- **Logs**: Check application logs for detailed error information

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx Proxy   │    │   Application   │    │   PostgreSQL    │
│   (Optional)    │────│   Spring Boot   │────│    Database     │
│   Port: 80/443  │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Security Best Practices

1. Use strong, unique passwords for all accounts
2. Enable firewall rules to restrict access
3. Regularly update the system and dependencies
4. Monitor logs for suspicious activities
5. Implement backup and disaster recovery procedures
6. Use HTTPS in production environments
7. Regularly rotate JWT secrets and database passwords

## Performance Monitoring

- Monitor CPU, memory, and disk usage
- Track database connection pool metrics
- Monitor application response times
- Set up alerts for critical failures
- Regularly review and analyze logs

## Backup Strategy

- **Automated Backups**: Configure daily database backups
- **Retention Policy**: Keep backups for 30 days minimum
- **Testing**: Regularly test backup restoration procedures
- **Off-site Storage**: Store backups in secure, off-site location

This deployment guide ensures a secure, reliable, and maintainable installation of the Blackdot Employee Management System.
