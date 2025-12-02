const { createClient } = require('@supabase/supabase-js');
require('dotenv').config();

// Supabase configuration
const supabaseUrl = process.env.SUPABASE_URL;
const supabaseServiceKey = process.env.SUPABASE_SERVICE_KEY;
const supabaseAnonKey = process.env.SUPABASE_ANON_KEY;

if (!supabaseUrl || !supabaseServiceKey) {
    console.error('❌ Missing Supabase configuration. Please check your .env file.');
    process.exit(1);
}

// Create Supabase client with service role key for backend operations
const supabase = createClient(supabaseUrl, supabaseServiceKey, {
    auth: {
        autoRefreshToken: false,
        persistSession: false
    }
});

// Create Supabase client with anon key for client-side operations
const supabaseAnon = createClient(supabaseUrl, supabaseAnonKey);

// Test connection
async function testConnection() {
    try {
        const { data, error } = await supabase
            .from('users')
            .select('count')
            .limit(1);

        if (error && error.code !== 'PGRST116') { // PGRST116 = table doesn't exist yet
            throw error;
        }

        console.log('✅ Supabase connected successfully');
        return true;
    } catch (err) {
        console.error('❌ Supabase connection failed:', err.message);
        console.error('Please ensure your Supabase project is set up and credentials are correct.');
        return false;
    }
}

// Run connection test
testConnection();

module.exports = {
    supabase,
    supabaseAnon,
    testConnection
};
