require('dotenv').config();
const { Client } = require('pg');

const regions = [
    'aws-0-ap-southeast-1.pooler.supabase.com', // Singapore
    'aws-0-us-east-1.pooler.supabase.com',      // US East N. Virginia
    'aws-0-eu-central-1.pooler.supabase.com',   // Frankfurt
    'aws-0-eu-west-2.pooler.supabase.com'       // London
];

async function testRegion(host) {
    const connectionString = `postgres://postgres.ldxukptpjddrtzcvvcpm:BVKM_PMS@${host}:6543/postgres`;
    const client = new Client({
        connectionString: connectionString,
        ssl: { rejectUnauthorized: false },
        connectionTimeoutMillis: 5000
    });

    try {
        console.log(`Testing ${host}...`);
        await client.connect();
        console.log(`✅ SUCCESS! Found region: ${host}`);
        await client.end();
        return host;
    } catch (err) {
        console.log(`❌ Failed ${host}: ${err.message}`);
        await client.end();
        return null;
    }
}

async function findRegion() {
    for (const host of regions) {
        const result = await testRegion(host);
        if (result) {
            console.log(`\n🎉 CORRECT POOLER HOST: ${result}`);
            return;
        }
    }
    console.log('\n❌ Could not guess the region. Please provide the connection string.');
}

findRegion();
