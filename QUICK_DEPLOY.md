# Quick Deploy to Render - Commands

## 1️⃣ Build Docker Image Locally

```powershell
cd "d:\Desktop\b v mm\apps\backend"
docker build -t pms-backend:latest .
```

## 2️⃣ Test Locally (Optional)

```powershell
docker run -d `
  --name pms-test `
  -p 8080:8080 `
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres `
  -e SPRING_DATASOURCE_USERNAME=postgres `
  -e SPRING_DATASOURCE_PASSWORD=BVKM_PMS `
  pms-backend:latest

# Check if running
curl http://localhost:8080/actuator/health

# Stop test container
docker stop pms-test
docker rm pms-test
```

## 3️⃣ Push to Docker Hub

```powershell
# Login (first time only)
docker login

# Tag image with your Docker Hub username
docker tag pms-backend:latest YOUR_USERNAME/pms-backend:latest

# Push
docker push YOUR_USERNAME/pms-backend:latest
```

## 4️⃣ Deploy on Render

### Via GitHub (Easiest)

1. Push code to GitHub
2. Go to https://render.com
3. Click "New" → "Web Service"
4. Connect GitHub & select repo
5. Set Runtime to "Docker"
6. Add environment variables (see below)
7. Deploy!

### Via Docker Hub Image

1. Go to https://render.com
2. Click "New" → "Web Service"
3. Select "Deploy from container image"
4. Enter: `YOUR_USERNAME/pms-backend:latest`
5. Add environment variables (see below)
6. Deploy!

## 5️⃣ Environment Variables for Render

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=BVKM_PMS
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SERVER_PORT=8080
```

## 6️⃣ Access Your API

After deployment completes (usually 5-10 minutes):

```
https://pms-backend.onrender.com
```

## 7️⃣ Monitor & Logs

In Render dashboard:
- Go to your service
- Click "Logs" tab to view real-time logs
- Check "Metrics" for performance

## 8️⃣ Auto-Deploy from GitHub

1. In Render, go to Service Settings
2. Enable "Auto-Deploy"
3. Now every push to main branch auto-deploys!

## Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| Build fails | Check logs in Render dashboard, ensure pom.xml is valid |
| Can't connect to Supabase | Verify IP whitelist in Supabase settings |
| Service won't start | Check environment variables, review startup logs |
| Free tier runs out of hours | Upgrade plan or wait for monthly reset |

## Useful Links

- Render Dashboard: https://dashboard.render.com
- Docker Hub: https://hub.docker.com
- GitHub: https://github.com
- Supabase: https://supabase.com

## Cost Breakdown

- **Render**: Free tier = 750 hrs/month (sufficient for small apps)
- **Supabase**: Free tier includes 500MB storage
- **Docker Hub**: Free tier for public images
- **Total Cost**: $0 (for free tier)

## Scale Up

When you grow:
1. Upgrade Render plan (Pro: $7/month)
2. Upgrade Supabase plan (Pro: $25/month)
3. Add custom domain ($10/month optional)

---

Need help? Check logs in Render dashboard or contact support!
