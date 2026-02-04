# Google OAuth 2.0 Setup Guide

## Problem: Error [28444] - Developer Console Not Set Up Correctly

The error "[28444] Developer console is not set up correctly" occurs when:
- OAuth 2.0 consent screen is not configured
- Redirect URIs are missing or incorrect
- The app credentials are incomplete
- The app is not verified for production use

## Solution: Complete Google Cloud Console Configuration

### Step 1: Configure OAuth Consent Screen

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Select your project
3. Navigate to **APIs & Services** → **OAuth consent screen**
4. Choose **User Type**: Select "External" (unless you're using Google Workspace)
5. Click **Create**
6. Fill in the required fields:
   - **App name**: SafetyApp
   - **User support email**: your-email@example.com
   - **Developer contact**: your-email@example.com
7. Click **Save and Continue**

### Step 2: Add Required Scopes

1. On the **Scopes** page, add these scopes:
   - `openid` - OpenID authentication
   - `email` - User email address
   - `profile` - User profile information
2. Click **Save and Continue**

### Step 3: Add Test Users (For Development)

1. On the **Test users** page, add your email address
2. This allows you to test OAuth before publishing

### Step 4: Verify OAuth Credentials

1. Go to **APIs & Services** → **Credentials**
2. Find your OAuth 2.0 Client ID: `727557341501-t7jdlqukns4etesst2400shndiqdsji8.apps.googleusercontent.com`
3. Click on it to edit
4. Ensure **Authorized redirect URIs** includes:
   - `com.guerramath.safetyapp://oauth` (for Android)
   - If using web backend: `https://your-api-domain.com/auth/oauth/callback`

### Step 5: Enable Google+ API

1. Go to **APIs & Services** → **Library**
2. Search for **Google+ API**
3. Click **Enable**

### Step 6: Android Configuration

The app will use the redirect scheme `com.guerramath.safetyapp://oauth` automatically handled by Google Play Services.

**⚠️ Important Security Note**: You've shared your OAuth Client ID publicly. While Client IDs can be public (they're used in OAuth flows), you should:

1. Rotate the Client ID if this is a production app
2. Restrict it to your Android app package only:
   - Go to Credentials → Your OAuth Client ID
   - Under "Android" section, add your app's SHA-1 certificate fingerprint
   - This ensures only YOUR app can use this OAuth credential

### Step 7: Backend Implementation

Your backend needs to implement an endpoint to exchange the Google ID Token for your app's session token.

**Endpoint**: `POST /auth/oauth/login`

**Request Body**:
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ...",
  "provider": "google"
}
```

**Response Body** (same as regular login):
```json
{
  "token": "your-session-token",
  "refreshToken": "your-refresh-token",
  "user": {
    "id": 1,
    "name": "User Name",
    "email": "user@example.com",
    "avatarUrl": null,
    "role": "user",
    "emailVerified": true,
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

**Backend Implementation Steps**:

1. **Verify the Google ID Token**:
   ```python
   # Example using google-auth library
   from google.auth.transport import requests
   from google.oauth2 import id_token

   try:
       idinfo = id_token.verify_oauth2_token(
           idToken,
           requests.Request(),
           client_id="727557341501-t7jdlqukns4etesst2400shndiqdsji8.apps.googleusercontent.com"
       )
       email = idinfo.get('email')
       name = idinfo.get('name')
   except ValueError:
       # Token is invalid
       return error_response
   ```

2. **Find or Create User**:
   - Check if a user exists with the given email
   - If not, create a new user with the name and email from the token

3. **Generate Session Tokens**:
   - Create your own JWT tokens (not Google's)
   - Return `token` (short-lived) and `refreshToken` (long-lived)

4. **Save Google Account Link** (optional):
   - You may want to store that this user logged in via Google
   - This allows users to link multiple OAuth providers later

## Testing the OAuth Flow

### In the App:

1. Open SafetyApp
2. On the login screen, click **"Continuar com Google"** (Continue with Google)
3. Google Sign-In dialog appears
4. Select a test account (if configured as test user)
5. Authorize the app
6. App receives ID Token and sends it to your backend
7. Backend verifies token and returns session credentials
8. User is logged in

### Troubleshooting:

| Error | Cause | Solution |
|-------|-------|----------|
| [28444] Developer console not set up | Missing OAuth consent screen config | Follow Step 1-3 above |
| Sign-in fails silently | Wrong redirect URI | Check Android package name and SHA-1 |
| "This app isn't verified" dialog appears | App not whitelisted as test user | Add your email in Step 3 |
| Token verification fails on backend | Invalid ID token or wrong client ID in verification | Ensure you're using the correct Client ID when verifying |

## Security Checklist

- [ ] OAuth 2.0 Consent Screen is configured
- [ ] Scopes are minimized (only request what you need)
- [ ] Android app package name and SHA-1 are registered
- [ ] Backend verifies Google ID Tokens correctly
- [ ] Backend creates its own session tokens (doesn't use Google's)
- [ ] OAuth Client ID is restricted to your app (if production)
- [ ] HTTPS is used for all backend calls
- [ ] Never log or store raw ID Tokens

## Next Steps

1. **Configure Google Cloud Console** following Steps 1-6
2. **Implement Backend Endpoint** following Step 7
3. **Get SHA-1 Certificate Fingerprint**:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
4. **Register Android App** in Google Cloud Console with your SHA-1
5. **Test the flow** using the steps in Testing section

## References

- [Google OAuth 2.0 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [Android Google Sign-In Documentation](https://developers.google.com/identity/sign-in/android)
- [Verify Google ID Tokens](https://developers.google.com/identity/sign-in/web/backend-auth)
