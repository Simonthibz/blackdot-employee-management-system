# Blackdot Employee Management System - Windows Deployment Script
# ================================================================
# PowerShell script for Windows deployment

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("deploy", "start", "stop", "restart", "status", "logs", "backup", "cleanup", "build")]
    [string]$Action,
    
    [Parameter(Mandatory=$false)]
    [string]$Service = ""
)

# Configuration
$ProjectName = "blackdot-ems"
$DockerComposeFile = "docker-compose.yml"
$EnvFile = ".env"

# Color output functions
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Check-Prerequisites {
    Write-Info "Checking prerequisites..."
    
    # Check Docker
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Error "Docker is not installed or not in PATH"
        exit 1
    }
    
    # Check Docker Compose
    if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        Write-Error "Docker Compose is not installed or not in PATH"
        exit 1
    }
    
    # Check if Docker daemon is running
    try {
        docker info *> $null
    }
    catch {
        Write-Error "Docker daemon is not running"
        exit 1
    }
    
    Write-Success "Prerequisites check passed"
}

function Setup-Environment {
    Write-Info "Setting up environment..."
    
    # Copy environment file if it doesn't exist
    if (-not (Test-Path $EnvFile)) {
        if (Test-Path ".env.example") {
            Copy-Item ".env.example" $EnvFile
            Write-Warning "Created $EnvFile from .env.example. Please review and modify as needed."
        }
        else {
            Write-Error ".env.example file not found"
            exit 1
        }
    }
    
    # Create logs directory
    if (-not (Test-Path "logs")) {
        New-Item -ItemType Directory -Path "logs" | Out-Null
    }
    
    Write-Success "Environment setup completed"
}

function Build-Application {
    Write-Info "Building application..."
    
    # Build Docker images
    docker-compose build --no-cache
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Application build failed"
        exit 1
    }
    
    Write-Success "Application build completed"
}

function Start-Services {
    Write-Info "Starting services..."
    
    # Start services
    docker-compose up -d
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to start services"
        exit 1
    }
    
    # Wait for services to be healthy
    Write-Info "Waiting for services to be ready..."
    
    # Wait for database
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            docker-compose exec -T postgres pg_isready -U postgres -d blackdot_ems *> $null
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Database is ready"
                break
            }
        }
        catch { }
        
        if ($attempt -eq $maxAttempts) {
            Write-Error "Database failed to start within timeout"
            docker-compose logs postgres
            exit 1
        }
        
        Write-Info "Waiting for database... (attempt $attempt/$maxAttempts)"
        Start-Sleep -Seconds 5
        $attempt++
    }
    
    # Wait for application
    $attempt = 1
    $maxAttempts = 20
    
    while ($attempt -le $maxAttempts) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Success "Application is ready"
                break
            }
        }
        catch { }
        
        if ($attempt -eq $maxAttempts) {
            Write-Error "Application failed to start within timeout"
            docker-compose logs app
            exit 1
        }
        
        Write-Info "Waiting for application... (attempt $attempt/$maxAttempts)"
        Start-Sleep -Seconds 10
        $attempt++
    }
    
    Write-Success "All services are running"
}

function Show-Status {
    Write-Info "Service Status:"
    docker-compose ps
    
    Write-Host ""
    Write-Info "Application URLs:"
    Write-Host "  • Main Application: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "  • Health Check: http://localhost:8080/actuator/health" -ForegroundColor Cyan
    Write-Host "  • Database: localhost:5432" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Info "Default Credentials:"
    Write-Host "  • Admin: admin@blackdot.com / admin123" -ForegroundColor Cyan
    Write-Host "  • HR Manager: hr@blackdot.com / hr123" -ForegroundColor Cyan
}

function Stop-Services {
    Write-Info "Stopping services..."
    docker-compose down
    Write-Success "Services stopped"
}

function Restart-Services {
    Stop-Services
    Start-Sleep -Seconds 2
    docker-compose up -d
    Show-Status
}

function Show-Logs {
    param([string]$ServiceName)
    
    if ($ServiceName) {
        docker-compose logs -f $ServiceName
    }
    else {
        docker-compose logs -f
    }
}

function Backup-Data {
    $backupDir = "backups\$(Get-Date -Format 'yyyyMMdd_HHmmss')"
    New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
    
    Write-Info "Creating backup in $backupDir..."
    
    # Backup database
    docker-compose exec -T postgres pg_dump -U postgres blackdot_ems > "$backupDir\database.sql"
    
    # Backup application logs
    docker-compose logs > "$backupDir\application.log" 2>&1
    
    Write-Success "Backup created in $backupDir"
}

function Cleanup-Environment {
    Write-Info "Cleaning up..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    Write-Success "Cleanup completed"
}

# Main script logic
switch ($Action) {
    "deploy" {
        Write-Info "Starting deployment..."
        Check-Prerequisites
        Setup-Environment
        Build-Application
        Start-Services
        Show-Status
    }
    "start" {
        Write-Info "Starting services..."
        docker-compose up -d
        Show-Status
    }
    "stop" {
        Stop-Services
    }
    "restart" {
        Restart-Services
    }
    "status" {
        Show-Status
    }
    "logs" {
        Show-Logs -ServiceName $Service
    }
    "backup" {
        Backup-Data
    }
    "cleanup" {
        Cleanup-Environment
    }
    "build" {
        Build-Application
    }
}

Write-Host ""
Write-Info "Operation completed successfully!"