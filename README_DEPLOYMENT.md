# Global Deployment Guide - PMS Backend on Render

## Prerequisites
1. Docker Desktop installed
2. Render account (free at https://render.com)
3. GitHub account with repository access

## Step 1: Push to GitHub

```powershell
cd "d:\Desktop\b v mm\apps\backend"

# Initialize git if not already done
git init

# Add all files
git add .

# Commit
git commit -m "Initial backend deployment setup"

# Add remote repository
git remote add origin https://github.com/YOUR_USERNAME/pms-backend.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Step 2: Deploy on Render

### Option A: Using GitHub Integration (Recommended)

1. Go to https://render.com and sign in
2. Click **"New"** → **"Web Service"**
3. Connect your GitHub account
4. Select your `pms-backend` repository
5. Configure the service:
   - **Name**: pms-backend
   - **Region**: Choose closest to your users (US, EU, Asia)
   - **Branch**: main
   - **Runtime**: Docker
   - **Build Command**: Leave empty (uses Dockerfile)
   - **Start Command**: Leave empty (uses Dockerfile ENTRYPOINT)

6. Add Environment Variables:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=BVKM_PMS
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
   SERVER_PORT=8080
   ```

7. Click **"Create Web Service"**

### Option B: Manual Docker Image Upload

1. Build Docker image locally:
```powershell
cd "d:\Desktop\b v mm\apps\backend"
docker build -t pms-backend:latest .
```

2. Push to Docker Hub:
```powershell
# Login to Docker Hub
docker login

# Tag image
docker tag pms-backend:latest YOUR_DOCKERHUB_USERNAME/pms-backend:latest

# Push
docker push YOUR_DOCKERHUB_USERNAME/pms-backend:latest
```

3. On Render:
   - Go to https://render.com
   - Click **"New"** → **"Web Service"**
   - Select **"Deploy from container image"**
   - Enter: `YOUR_DOCKERHUB_USERNAME/pms-backend:latest`
   - Continue with environment variables setup

## Step 3: Configure Custom Domain (Optional)

1. In Render dashboard, go to your service
2. Click **"Settings"**
3. Under **"Custom Domain"**, add your domain
4. Follow DNS configuration instructions

## Step 4: Monitor & Logs

- View logs: Dashboard → Service → Logs
- Check health: Dashboard → Metrics
- View deployment status: Dashboard → Events

## Accessing Your Global API

After deployment, your API will be available at:
```
https://pms-backend.onrender.com
```

Or with custom domain:
```
https://yourdomain.com
```

## Environment Variables for Different Regions

If you want to deploy in multiple regions on Render:

**US Region (Oregon)**
```yaml
region: oregon
```

**EU Region**
```yaml
region: frankfurt
```

**Asia Region (Singapore)**
```yaml
region: singapore
```

## Auto-Deployment

Enable auto-deployment:
1. Go to Service Settings
2. Enable **"Auto-Deploy"**
3. Choose branch (main)
4. Every push to main will trigger deployment

## Pricing

- **Free Tier**: Up to 750 hours/month (enough for small apps)
- **Paid Plans**: Starting $7/month
- Database: Use your existing Supabase (no extra cost)

## Troubleshooting

### Build fails
- Check logs in Render dashboard
- Ensure pom.xml and Dockerfile are correct
- Verify Java 17 compatibility

### Connection timeout
- Check Supabase credentials
- Ensure Supabase allows connections from Render IPs
- Verify SPRING_DATASOURCE_URL is correct

### Service won't start
- Check environment variables
- View startup logs in dashboard
- Ensure health check endpoint exists: `/actuator/health`

## Scaling Up

If you need better performance:
1. Upgrade plan in Render (Pro, Business)
2. Enable auto-scaling
3. Add CDN for static content

## Backup & Security

1. Regularly backup Supabase database
2. Use strong passwords
3. Never commit secrets to GitHub
4. Use Render's secret management for sensitive data

## CI/CD Pipeline

For automated testing before deployment:

Create `.github/workflows/deploy.yml`:
```yaml
name: Deploy to Render

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
      - name: Deploy to Render
        run: |
          curl -X POST https://api.render.com/deploy/${{ secrets.RENDER_SERVICE_ID }} \
            -H "Authorization: Bearer ${{ secrets.RENDER_API_KEY }}"
```
