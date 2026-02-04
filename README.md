# CampusAura Backend

CampusAura is an interactive web-based application that allows students to learn about future events, buy tickets, fund charitable efforts, browse products, and sell or buy items created by students - all in one combined system.

This is the **Spring Boot backend** with Firebase Authentication, role-based access control, and Firestore integration.

## 🚀 Features

- ✅ **Firebase Authentication Integration** - Secure JWT token validation
- ✅ **Role-Based Access Control** - 4 roles (ADMIN, COORDINATOR, STUDENT, EXTERNAL_USER)
- ✅ **Email Domain Validation** - Automatic role assignment based on university email
- ✅ **Marketplace Access Control** - Business rule enforcement (EXTERNAL_USER cannot sell)
- ✅ **Firestore Integration** - User data stored in Firestore
- ✅ **Automatic User Sync** - Users created on first login
- ✅ **RESTful API** - Clean, well-documented endpoints
- ✅ **CORS Enabled** - Frontend integration ready

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Firebase project with Authentication and Firestore enabled
- Firebase service account credentials

## 🛠️ Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd CampusAura-backend
```

### 2. Configure Firebase
1. Place your `firebase-service-account.json` in `src/main/resources/`
2. Update `application.properties`:
```properties
firebase.service-account-key=classpath:firebase-service-account.json
firebase.database-url=https://your-project.firebaseio.com
```

### 3. Build and Run
```bash
# Using Maven wrapper (recommended)
./mvnw.cmd spring-boot:run

# Or using Maven
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [AUTHENTICATION_GUIDE.md](docs/AUTHENTICATION_GUIDE.md) | Complete authentication flow and integration guide |
| [API_REFERENCE.md](docs/API_REFERENCE.md) | API endpoints quick reference |
| [AUTHENTICATION_FLOW_DIAGRAM.md](docs/AUTHENTICATION_FLOW_DIAGRAM.md) | Visual authentication flow diagrams |
| [IMPLEMENTATION_SUMMARY.md](docs/IMPLEMENTATION_SUMMARY.md) | Implementation details and checklist |
| [FIREBASE_AUTH_GUIDE.md](docs/FIREBASE_AUTH_GUIDE.md) | Firebase setup guide |

## 🔐 Authentication

### How It Works

CampusAura uses **Firebase Authentication** on the frontend with backend token validation:

1. **Frontend** (React/Angular) handles registration/login via Firebase SDK
2. **Backend** validates tokens and manages roles/permissions
3. Users are automatically created in Firestore on first authenticated request
4. Roles are assigned based on email domain

### Quick Example

```javascript
// Frontend - Register & Login
import { auth } from './firebaseConfig';
import { createUserWithEmailAndPassword } from 'firebase/auth';

// Register
const userCredential = await createUserWithEmailAndPassword(auth, email, password);
const token = await userCredential.user.getIdToken();

// Make API request
fetch('http://localhost:8080/api/user/profile', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

## 🎯 Roles & Permissions

| Role | Email Requirement | Can Sell Items | Access Level |
|------|------------------|----------------|--------------|
| **EXTERNAL_USER** | Any email | ❌ No | Limited - browse only |
| **STUDENT** | @std.uwu.ac.lk | ✅ Yes | Full student features |
| **COORDINATOR** | @std.uwu.ac.lk | ✅ Yes | Event management |
| **ADMIN** | @std.uwu.ac.lk | ✅ Yes | Full system access |

### Automatic Role Assignment

```
student@std.uwu.ac.lk → STUDENT role (can sell)
john@gmail.com        → EXTERNAL_USER role (cannot sell)
```

## 🌐 API Endpoints

### Public (No Authentication)
```http
POST /api/auth/validate-email
GET  /api/auth/registration-info
```

### Authentication Required
```http
GET  /api/auth/session
GET  /api/auth/verify
POST /api/auth/logout

GET  /api/user/profile
GET  /api/user/me
GET  /api/user/dashboard

GET  /api/marketplace/items
GET  /api/marketplace/can-sell
POST /api/marketplace/sell        # STUDENT, COORDINATOR, ADMIN only
POST /api/marketplace/buy

PUT  /api/user/admin/users/{uid}/role  # ADMIN only
```

See [API_REFERENCE.md](docs/API_REFERENCE.md) for complete API documentation.

## 🏗️ Project Structure

```
src/main/java/com/example/campusaura/
├── config/
│   ├── FirebaseConfig.java           # Firebase initialization
│   └── SecurityConfig.java           # Spring Security config
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   ├── UserController.java           # User management
│   └── MarketplaceController.java    # Marketplace logic
├── model/entity/
│   └── User.java                     # User entity
├── repository/
│   ├── UserRepository.java
│   └── impl/FirestoreUserRepository.java
├── security/
│   ├── FirebaseAuthFilter.java       # Token validation filter
│   ├── FirebasePrincipal.java        # Custom principal
│   └── Roles.java                    # Role constants
├── service/
│   ├── UserService.java
│   └── impl/UserServiceImpl.java     # Role assignment logic
└── util/
    └── EmailValidator.java            # Email domain validation
```

## 🧪 Testing

### Test with cURL
```bash
# Validate email (public endpoint)
curl -X POST http://localhost:8080/api/auth/validate-email \
  -H "Content-Type: application/json" \
  -d '{"email": "student@std.uwu.ac.lk"}'

# Get user profile (requires token)
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer <your-firebase-token>"
```

### Test Marketplace Access
```bash
# STUDENT can sell (succeeds)
curl -X POST http://localhost:8080/api/marketplace/sell \
  -H "Authorization: Bearer <student-token>" \
  -H "Content-Type: application/json" \
  -d '{"itemName": "Laptop", "price": 50000}'

# EXTERNAL_USER cannot sell (fails with 403)
curl -X POST http://localhost:8080/api/marketplace/sell \
  -H "Authorization: Bearer <external-token>" \
  -H "Content-Type: application/json" \
  -d '{"itemName": "Laptop", "price": 50000}'
```

## 🔒 Security Features

- ✅ Firebase JWT token validation on every request
- ✅ Role-based access control with `@PreAuthorize`
- ✅ Email domain validation for protected roles
- ✅ Defense-in-depth (multiple security layers)
- ✅ CORS configuration for frontend
- ✅ Stateless authentication (no server-side sessions)

## 🎓 Email Domain Rules

### University Email Required
- **ADMIN** - University email required
- **COORDINATOR** - University email required
- **STUDENT** - University email required (auto-assigned)

### Any Email Allowed
- **EXTERNAL_USER** - Any valid email (auto-assigned for non-university emails)

### Validation
```java
// Implemented in EmailValidator.java
@std.uwu.ac.lk → Valid university email
@gmail.com     → External email (valid but limited access)
```

## 🛒 Marketplace Business Rules

### Selling Items
- ❌ **EXTERNAL_USER** - Cannot sell items
- ✅ **STUDENT** - Can sell items
- ✅ **COORDINATOR** - Can sell items
- ✅ **ADMIN** - Can sell items

### Buying & Browsing
- ✅ All authenticated users can browse and buy items

## 🚀 Deployment

### Production Configuration
1. Update CORS origins in `SecurityConfig.java`
2. Use environment variables for Firebase credentials
3. Enable HTTPS
4. Configure production database URL

```properties
# application-prod.properties
firebase.service-account-key=${FIREBASE_CREDENTIALS}
firebase.database-url=${FIREBASE_DATABASE_URL}
```

## 🐛 Troubleshooting

### Issue: "Unauthorized" error
**Solution**: Ensure token is valid and included in Authorization header

### Issue: CORS errors
**Solution**: Add your frontend URL to `SecurityConfig.java` CORS configuration

### Issue: Role not updating
**Solution**: User needs to refresh token (re-login) after role change

### Issue: EXTERNAL_USER can't access anything
**Solution**: EXTERNAL_USER can browse and buy, just cannot sell items

## 📞 Support

For questions and support, please refer to:
- [AUTHENTICATION_GUIDE.md](docs/AUTHENTICATION_GUIDE.md) - Complete authentication documentation
- [API_REFERENCE.md](docs/API_REFERENCE.md) - API endpoint reference
- [FIREBASE_AUTH_GUIDE.md](docs/FIREBASE_AUTH_GUIDE.md) - Firebase setup

## 📝 License

[Add your license here]

## ✨ Contributors

[Add contributors here]

---

**Built with Spring Boot, Firebase, and ❤️**
