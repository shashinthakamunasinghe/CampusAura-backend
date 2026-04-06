const admin = require('firebase-admin');

// Initialize Firebase Admin with your service account
const serviceAccount = require('./src/main/resources/firebase-service-account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Replace with your user's UID
const userId = 'Jj8Em9OTpSeSz41GVZ7HLFMG8Mv2';

admin.auth().setCustomUserClaims(userId, { 
  admin: true, 
  role: 'admin' 
})
.then(() => {
  console.log('✅ Admin role set successfully for user:', userId);
  process.exit(0);
})
.catch(error => {
  console.error('❌ Error setting admin role:', error);
  process.exit(1);
});