const admin = require('firebase-admin');

// Initialize Firebase Admin with your service account
const serviceAccount = require('./src/main/resources/firebase-service-account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Admin account details - CHANGE THESE VALUES
const adminEmail = 'admin@university.edu';
const adminPassword = 'Admin@123456';  // Change this to a secure password
const adminFirstName = 'Admin';
const adminLastName = 'User';

async function createAdminAccount() {
  try {
    console.log('🔄 Creating admin account...');
    
    // Step 1: Create Firebase Authentication user
    let userRecord;
    try {
      userRecord = await admin.auth().createUser({
        email: adminEmail,
        password: adminPassword,
        displayName: `${adminFirstName} ${adminLastName}`,
        emailVerified: true  // Set to true for admin
      });
      console.log('✅ Firebase Auth user created:', userRecord.uid);
    } catch (error) {
      if (error.code === 'auth/email-already-exists') {
        console.log('⚠️  User already exists, fetching existing user...');
        userRecord = await admin.auth().getUserByEmail(adminEmail);
        console.log('✅ Found existing user:', userRecord.uid);
      } else {
        throw error;
      }
    }

    // Step 2: Set admin custom claims
    await admin.auth().setCustomUserClaims(userRecord.uid, {
      admin: true,
      role: 'admin'
    });
    console.log('✅ Admin custom claims set successfully');

    // Step 3: Create admin user document in Firestore
    const db = admin.firestore();
    const userDoc = {
      uid: userRecord.uid,
      email: adminEmail,
      firstName: adminFirstName,
      lastName: adminLastName,
      userType: 'ADMIN',
      role: 'admin',
      active: true,
      verificationStatus: 'VERIFIED',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    await db.collection('users').doc(userRecord.uid).set(userDoc, { merge: true });
    console.log('✅ Admin user document created in Firestore');

    // Step 4: Display login credentials
    console.log('\n' + '='.repeat(60));
    console.log('🎉 ADMIN ACCOUNT READY!');
    console.log('='.repeat(60));
    console.log('Email:', adminEmail);
    console.log('Password:', adminPassword);
    console.log('UID:', userRecord.uid);
    console.log('Role: admin');
    console.log('='.repeat(60));
    console.log('\n📝 NEXT STEPS:');
    console.log('1. Login with these credentials in your app');
    console.log('2. Get the Firebase ID token (see instructions below)');
    console.log('3. Use the token in Postman for admin API requests');
    console.log('\n💡 GET TOKEN IN BROWSER CONSOLE:');
    console.log('   firebase.auth().currentUser.getIdToken().then(token => console.log(token));');
    console.log('\n⚠️  IMPORTANT: Store this password securely!\n');

    process.exit(0);
  } catch (error) {
    console.error('❌ Error creating admin account:', error);
    process.exit(1);
  }
}

createAdminAccount();
