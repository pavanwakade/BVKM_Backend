# Deploy BVKM Backend to Render (Recommended)

Your local Docker environment cannot reach Supabase because it resolves to an **IPv6-only address** and Windows Docker (WSL2) lacks working IPv6 routing. **Render** (a cloud platform) fully supports IPv6 and will solve this connectivity issue immediately.

## Option 1: Deploy from GitHub (Recommended)

### Step 1: Push Your Code to GitHub

```powershell
# Initialize git (if not already done)
git init
git add .
git commit -m "Add Docker configuration for BVKM Backend"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/BVKM_Backend.git
git push -u origin main
```

### Step 2: Create Render Service

1. Go to [Render.com](https://render.com) and sign up/log in
2. Click **"New +"** → **"Web Service"**
3. Select **"Deploy an existing repository"** → Authorize GitHub → Select `BVKM_Backend`
4. Fill in service details:
   - **Name**: `pms-backend`
   - **Environment**: `Docker`
   - **Region**: `Oregon` (or nearest to you)
   - **Plan**: `Free` (or Starter for production)
   - **Branch**: `main`

### Step 3: Configure Environment Variables

In Render dashboard, go to **Environment** and add:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db.ldxukptpjddrtzcvvcpm.supabase.co:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=BVKM_PMS
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SERVER_PORT=10000
```

**Note**: Render dynamically assigns `PORT` via `$PORT` env var. The Dockerfile already supports this with `${PORT:-8080}`.

### Step 4: Deploy

Click **"Create Web Service"** → Render auto-builds and deploys → Get your URL like `https://pms-backend-xxxxx.onrender.com`

---

## Option 2: Deploy from Docker Hub

If you prefer not to push code to GitHub, push the Docker image instead:

### Step 1: Push Image to Docker Hub

```powershell
# Log in to Docker Hub
docker login

# Tag image
docker tag bvkm_backend:latest YOUR_USERNAME/bvkm_backend:latest

# Push
docker push YOUR_USERNAME/bvkm_backend:latest
```

### Step 2: Create Render Service from Image

1. In Render, click **"New +"** → **"Web Service"**
2. Select **"Public Registry"**
3. Enter: `YOUR_USERNAME/bvkm_backend:latest`
4. Fill in service details (same as above)
5. Set environment variables
6. Click **"Create"**

---

## Testing Your Deployment

Once deployed (5-10 min), test the health endpoint:

```powershell
# Replace with your Render URL
curl https://pms-backend-xxxxx.onrender.com/actuator/health

# Expected response:
# {"status":"UP"}
```

---

## Production Checklist

- [ ] Move database credentials to Render **Secrets** (not environment variables)
- [ ] Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` (not `update`) in production
- [ ] Enable HTTPS (Render provides free SSL/TLS)
- [ ] Monitor logs: Render → **Logs** tab in dashboard
- [ ] Test API endpoints after deployment

---

## Troubleshooting

**Build fails?**
- Check Render logs: Dashboard → Service → **Logs** tab
- Ensure `Dockerfile` and `pom.xml` are in root of repo

**App crashes after startup?**
- Same DB connection issue → Logs show `Network unreachable`
- Contact Render support or try a different deployment region

**Slow first request?**
- Render free tier spins down after 15 min of inactivity → first request takes ~30s to wake up
- Upgrade to Starter plan to avoid this

---

## Why Render Over Local Docker?

| Aspect | Local Docker | Render |
|--------|--------------|--------|
| **IPv6 Support** | ❌ No (Windows WSL2 limitation) | ✅ Full IPv6 routing |
| **Supabase Connectivity** | ❌ Fails with "Network unreachable" | ✅ Works out-of-box |
| **SSL/TLS** | Manual setup | ✅ Automatic with domain |
| **Auto-scaling** | ❌ No | ✅ Yes (paid plans) |
| **Cost** | Free (local) | Free tier available |

