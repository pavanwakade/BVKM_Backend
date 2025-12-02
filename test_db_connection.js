require('dotenv').config();
const { Client } = require('pg');

const connectionString = 'postgres://postgres.ldxukptpjddrtzcvvcpm:BVKM_PMS@aws-0-ap-south-1.pooler.supabase.com:6543/postgres';
const client = new Client({
    connectionString: connectionString,
    ssl: { rejectUnauthorized: false }
});

async function testConnection() {
    console.log('Testing connection to:', connectionString);
    try {
        await client.connect();
        console.log('✅ Connection successful!');
        const res = await client.query('SELECT NOW()');
        console.log('Database time:', res.rows[0].now);
    } catch (err) {
        console.error('❌ Connection failed:', err.message);
        if (err.message.includes('password authentication failed')) {
            console.error('Hint: Check your password.');
        } else if (err.message.includes('getaddrinfo')) {
            console.error('Hint: Check the hostname. Is the database paused?');
        }
    } finally {
        await client.end();
    }
}

testConnection();
