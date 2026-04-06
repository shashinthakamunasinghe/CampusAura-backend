const admin = require('firebase-admin');
const fetch = require('node-fetch');

// Initialize Firebase Admin
const serviceAccount = require('./src/main/resources/firebase-service-account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Admin credentials
const adminEmail = 'admin@university.edu';
const adminPassword = 'Admin@123456';

// Get your Firebase Web API Key from Firebase Console
// Go to: Firebase Console > Project Settings > General > Web API Key
const FIREBASE_API_KEY = 'AIzaSyDKnWIHEwjFoEmOUeantbDcNOtHYR4gMAs'; // Replace with your actual API key

async function getAdminToken() {
  try {
    console.log('🔄 Authenticating admin user...');
    
    // Sign in with email and password using Firebase REST API
    const response = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${FIREBASE_API_KEY}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: adminEmail,
          password: adminPassword,
          returnSecureToken: true
        })
      }
    );

    const data = await response.json();

    if (data.error) {
      console.error('❌ Authentication failed:', data.error.message);
      console.log('\n💡 Make sure you:');
      console.log('1. Updated FIREBASE_API_KEY in this script');
      console.log('2. Have run "node create-admin-account.js" first');
      console.log('3. The email and password are correct');
      process.exit(1);
    }

    const idToken = data.idToken;
    
    // Verify the token has admin claims
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    
    console.log('\n' + '='.repeat(70));
    console.log('✅ ADMIN TOKEN GENERATED SUCCESSFULLY!');
    console.log('='.repeat(70));
    console.log('\n📋 TOKEN (Copy this to Postman):');
    console.log('\n' + idToken + '\n');
    console.log('='.repeat(70));
    
    console.log('\n👤 User Info:');
    console.log('Email:', decodedToken.email);
    console.log('UID:', decodedToken.uid);
    console.log('Admin Role:', decodedToken.admin || false);
    console.log('Role:', decodedToken.role || 'none');
    
    if (!decodedToken.admin) {
      console.log('\n⚠️  WARNING: This user does NOT have admin privileges!');
      console.log('Run: node create-admin-account.js');
    }
    
    console.log('\n⏰ Token Expiry:');
    const expiryDate = new Date(decodedToken.exp * 1000);
    console.log('Expires at:', expiryDate.toLocaleString());
    console.log('Valid for: ~1 hour from now');
    
    console.log('\n📝 HOW TO USE IN POSTMAN:');
    console.log('1. Open your Postman collection');
    console.log('2. Go to Variables tab');
    console.log('3. Update "token" variable with the token above');
    console.log('4. Save and test your API requests!');
    console.log('\n' + '='.repeat(70) + '\n');
    
    process.exit(0);
  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}

// Instructions to get Firebase API Key
if (FIREBASE_API_KEY === 'YOUR_FIREBASE_WEB_API_KEY') {
  console.log('\n' + '='.repeat(70));
  console.log('⚠️  SETUP REQUIRED: Firebase Web API Key');
  console.log('='.repeat(70));
  console.log('\n📍 How to get your Firebase Web API Key:');
  console.log('\n1. Go to Firebase Console: https://console.firebase.google.com/');
  console.log('2. Select your project: campusaura-12c16');
  console.log('3. Click the ⚙️  gear icon (Project Settings)');
  console.log('4. Scroll down to "Your apps" section');
  console.log('5. Copy the "Web API Key"');
  console.log('6. Replace "YOUR_FIREBASE_WEB_API_KEY" in this file (line 14)');
  console.log('\n💡 The API Key looks like: AIzaSyD...(39 characters)');
  console.log('\n' + '='.repeat(70) + '\n');
  process.exit(1);
}

getAdminToken();
