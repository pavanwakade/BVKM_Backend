@echo off
REM Deployment script for PMS Backend to Render

echo.
echo ========================================
echo  PMS Backend - Global Deployment Script
echo ========================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed. Please install Docker Desktop first.
    exit /b 1
)

REM Step 1: Build Docker image
echo [1/5] Building Docker image...
docker build -t pms-backend:latest .
if errorlevel 1 (
    echo [ERROR] Docker build failed
    exit /b 1
)
echo [OK] Docker image built successfully

REM Step 2: Test Docker image locally
echo.
echo [2/5] Testing Docker image locally...
docker run --rm -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=BVKM_PMS pms-backend:latest &
timeout /t 5 >nul
echo [OK] Docker image test started (check http://localhost:8080)

REM Step 3: Login to Docker Hub
echo.
echo [3/5] Preparing for Docker Hub push...
echo Please enter your Docker Hub credentials when prompted
docker login

REM Step 4: Push to Docker Hub
echo.
echo [4/5] Pushing image to Docker Hub...
echo Note: Replace YOUR_DOCKERHUB_USERNAME with your actual username
set /p DOCKER_USERNAME="Enter your Docker Hub username: "
docker tag pms-backend:latest %DOCKER_USERNAME%/pms-backend:latest
docker push %DOCKER_USERNAME%/pms-backend:latest
if errorlevel 1 (
    echo [ERROR] Docker push failed
    exit /b 1
)
echo [OK] Image pushed to Docker Hub

REM Step 5: Instructions
echo.
echo [5/5] Next Steps:
echo ========================================
echo 1. Go to https://render.com
echo 2. Sign in or create account
echo 3. Click "New" > "Web Service"
echo 4. Select "Deploy from container image"
echo 5. Enter: %DOCKER_USERNAME%/pms-backend:latest
echo 6. Add environment variables:
echo    - SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres
echo    - SPRING_DATASOURCE_USERNAME=postgres
echo    - SPRING_DATASOURCE_PASSWORD=BVKM_PMS
echo    - SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo    - SERVER_PORT=8080
echo 7. Click "Create Web Service"
echo.
echo Your app will be available at: https://pms-backend.onrender.com
echo ========================================
echo.
echo Deployment preparation complete!
pause
