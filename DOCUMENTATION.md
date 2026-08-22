# 📚 Prince Gupt - Full Stack Developer Portfolio - Complete Documentation

## Table of Contents
1. [Overview](#overview)
2. [Quick Start (5 Minutes)](#quick-start)
3. [Setup Guide](#setup-guide)
4. [Features](#features)
5. [API Documentation](#api-documentation)
6. [Testing](#testing)
7. [Admin Guide](#admin-guide)
8. [Troubleshooting](#troubleshooting)
9. [Configuration](#configuration)
10. [Customization](#customization)
11. [Deployment](#deployment)

---

## Overview

A modern, professional portfolio website built with **Next.js**, **TypeScript**, and **Tailwind CSS**. Includes:

✅ **Resume Download System** - Secure email-based approval workflow
✅ **Contact Form** - Email integration for inquiries
✅ **Email Service** - Complete email system using Nodemailer
✅ **Modern UI** - Animations, dark/light mode, responsive design
✅ **Error Handling** - Robust error handling with helpful messages

### Tech Stack
- **Framework**: Next.js 16 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Animations**: Framer Motion
- **Email**: Nodemailer
- **Icons**: Lucide React, react-icons
- **Theme**: next-themes

### Project Structure
```
portfolio/
├── app/
│   ├── api/
│   │   ├── resume/
│   │   │   ├── request/route.ts          # Submit resume request
│   │   │   ├── approve/route.ts          # Admin approval
│   │   │   └── download/route.ts         # Secure download
│   │   ├── contact/route.ts              # Contact form
│   │   └── diagnostics/route.ts          # Config checker
│   ├── resume-approved/page.tsx          # Success page
│   ├── resume-error/page.tsx             # Error page
│   ├── layout.tsx
│   ├── page.tsx
│   └── globals.css
├── components/
│   ├── Hero.tsx                          # With resume modal
│   ├── ResumeModal.tsx                   # Email input modal
│   ├── Contact.tsx                       # Contact form
│   ├── Navbar.tsx
│   ├── About.tsx
│   ├── Skills.tsx
│   ├── Projects.tsx
│   ├── Experience.tsx
│   ├── Footer.tsx
│   └── Providers.tsx
├── lib/
│   ├── email.ts                          # Email service
│   ├── db.ts                             # Request storage
│   └── ...
├── public/
│   ├── resume.pdf                        # Your resume
│   └── ...
├── .env.local                            # Configuration (gitignored)
├── .env.local.example                    # Template
├── package.json
└── tsconfig.json
```

---

## Quick Start

### 5-Minute Setup

#### Step 1: Configure Email (2 minutes)

**For Gmail Users:**
1. Go to https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Go to https://myaccount.google.com/apppasswords
4. Select "Mail" → "Windows Computer"
5. Copy 16-character password

**Create `.env.local` file in project root:**
```env
EMAIL_SERVICE=gmail
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-16-char-app-password
ADMIN_EMAIL=princegupt3052@gmail.com
NEXT_PUBLIC_BASE_URL=http://localhost:3000
```

#### Step 2: Add Your Resume (1 minute)
```bash
cp /path/to/your/resume.pdf public/resume.pdf
```

Or manually: Copy your resume PDF to `public/resume.pdf`

#### Step 3: Restart Dev Server (1 minute)
```bash
npm run dev
```

#### Step 4: Test (1 minute)
1. Open http://localhost:3000
2. Click "Download Resume" button
3. Enter email → Submit
4. Check your ADMIN_EMAIL inbox
5. Click "Approve Request"
6. Check test email for download link

#### Verify Setup
```bash
curl http://localhost:3000/api/diagnostics
```

Should show: `"status": "✓ System Ready"`

---

## Setup Guide

### Environment Configuration

Create `.env.local` in project root with:
```env
EMAIL_SERVICE=gmail
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-app-password
ADMIN_EMAIL=princegupt3052@gmail.com
NEXT_PUBLIC_BASE_URL=http://localhost:3000
```

**File Location:** `portfolio/.env.local` (same level as `package.json`)

### Get Gmail App Password

1. **Enable 2FA** (if not already):
   - Visit https://myaccount.google.com/security
   - Enable "2-Step Verification"

2. **Get App Password**:
   - Visit https://myaccount.google.com/apppasswords
   - Select "Mail" → "Windows Computer"
   - Copy 16-character password
   - Paste into `EMAIL_PASSWORD` in `.env.local`

### Add Your Resume

Place your resume PDF at:
```
public/resume.pdf
```

### Prerequisites

- [ ] `.env.local` created with all 5 variables
- [ ] All variables have values (not blank)
- [ ] GMail App Password obtained (if using Gmail)
- [ ] `public/resume.pdf` exists
- [ ] Node.js and npm installed

### Installation Steps

```bash
# 1. Install dependencies
npm install

# 2. Create .env.local with credentials (see above)

# 3. Add public/resume.pdf

# 4. Start dev server
npm run dev

# 5. Verify configuration
curl http://localhost:3000/api/diagnostics

# 6. Open in browser
# http://localhost:3000
```

---

## Features

### 1. Resume Download System

**User Flow:**
```
User clicks "Download Resume"
        ↓
Modal appears (enter email)
        ↓
User submits email
        ↓
Admin receives approval request email
        ↓
Admin clicks "Approve Request"
        ↓
User receives download link (24-hour valid)
        ↓
User downloads resume
```

**Security:**
- 🔐 Secure token-based requests (32-byte cryptographic tokens)
- ⏰ Download links valid for 24 hours only
- ✅ Request tracking (pending → approved → downloaded)
- 🙅 No direct downloads (requires approval)

**Files:**
- `components/Hero.tsx` - Download button
- `components/ResumeModal.tsx` - Email modal
- `app/api/resume/request/route.ts` - Submit request
- `app/api/resume/approve/route.ts` - Admin approval
- `app/api/resume/download/route.ts` - Secure download
- `lib/db.ts` - Request storage
- `lib/email.ts` - Email sending

### 2. Contact Form System

**User Flow:**
```
User fills contact form
        ↓
Submits (name, email, subject, message)
        ↓
Form validates all fields
        ↓
Email sent to ADMIN_EMAIL
        ↓
Confirmation email sent to user
        ↓
Success message shown
```

**Features:**
- 📧 Form validation (client + server)
- ✉️ Admin receives message with sender info
- 📨 User receives confirmation
- 🎯 Professional email templates

**Files:**
- `components/Contact.tsx` - Contact form component
- `app/api/contact/route.ts` - Contact API endpoint

### 3. Email Service

**Powered by Nodemailer** with support for:
- Gmail (easiest)
- Outlook/Office365
- Yahoo
- Custom SMTP servers

**Configuration:**
```env
EMAIL_SERVICE=gmail                    # Service provider
EMAIL_USER=princegupt3052@gmail.com        # Sender email
EMAIL_PASSWORD=app-password            # App password
ADMIN_EMAIL=princegupt3052@gmail.com       # Admin inbox
```

---

## API Documentation

### Resume System APIs

#### 1. POST `/api/resume/request`
**Submit resume download request**

Request:
```json
{
  "userEmail": "user@example.com"
}
```

Response (201):
```json
{
  "success": true,
  "message": "Resume request submitted...",
  "requestId": "uuid"
}
```

#### 2. GET/POST `/api/resume/approve`
**Admin approval endpoint**

GET Query Params:
```
token=approval-token&requestId=request-id
```

POST Request:
```json
{
  "requestId": "uuid",
  "approvalToken": "token"
}
```

Response (200):
```json
{
  "success": true,
  "message": "Request approved. Download link sent to user."
}
```

#### 3. GET `/api/resume/download`
**Download secure PDF**

Query Params:
```
token=download-token&requestId=request-id
```

Response: PDF file (binary)

### Contact Form API

#### POST `/api/contact`
**Submit contact message**

Request:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "subject": "Project Inquiry",
  "message": "Message content"
}
```

Response (200):
```json
{
  "success": true,
  "message": "Message sent successfully! I'll get back to you soon."
}
```

### Diagnostics API

#### GET `/api/diagnostics`
**Check configuration status**

Response:
```json
{
  "status": "✓ System Ready",
  "environment": {
    "EMAIL_SERVICE": "✓ Configured",
    "EMAIL_USER": "✓ Configured",
    "EMAIL_PASSWORD": "✓ Configured",
    "ADMIN_EMAIL": "✓ Configured"
  }
}
```

---

## Testing

### Test Resume System

1. Open http://localhost:3000
2. Click "Download Resume" button
3. Enter your test email
4. Click "Submit Request"
5. Check ADMIN_EMAIL inbox (within 5 seconds)
6. Click "Approve Request" in email
7. Check test email inbox
8. Click download link to get PDF

### Test Contact Form

1. Open http://localhost:3000
2. Scroll to "Get In Touch"
3. Fill form (name, email, subject, message)
4. Click "Send Message"
5. Check ADMIN_EMAIL inbox
6. Verify form cleared and success message shown

### Test Configuration

```bash
# Check all environment variables are set
curl http://localhost:3000/api/diagnostics
```

Should show: `"status": "✓ System Ready"`

### Test API Endpoints

```bash
# Test resume request API
curl -X POST http://localhost:3000/api/resume/request \
  -H "Content-Type: application/json" \
  -d '{"userEmail":"test@example.com"}'

# Test contact API
curl -X POST http://localhost:3000/api/contact \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","subject":"Test","message":"Test message"}'
```

---

## Admin Guide

### Resume Approval Process

#### What You Receive
Email with subject: `New Resume Download Request from user@email.com`

Email contains:
- Sender's email address
- "Approve Request" button
- Request ID and token

#### What To Do
1. **Receive email** from user request
2. **Check spam/promotions** folder if not in inbox
3. **Click "Approve Request"** button
4. See **"Request Approved!"** confirmation page
5. **Done** - user automatically receives download link

#### After Approval
✅ User receives email with subject: "Your Resume Download Link - Prince Gupt"
✅ Download link is valid for 24 hours
✅ User can download your resume
✅ Request status marked as "downloaded" after download

#### Checking Request History

Access: `.data/resume_requests.json`

```json
[
  {
    "id": "unique-id",
    "userEmail": "user@example.com",
    "status": "pending|approved|downloaded",
    "createdAt": "2024-12-15T15:30:00Z",
    "approvedAt": "2024-12-15T15:31:00Z",
    "downloadedAt": "2024-12-15T15:32:00Z"
  }
]
```

#### Security Notes
- ✅ All requests require your approval
- ✅ Links have unique tokens
- ✅ Download links expire after 24 hours
- ✅ Invalid tokens show error to user
- ✅ All actions are timestamped

#### Troubleshooting

**Q: I didn't receive approval email**
- Check spam/promotions folder
- Verify ADMIN_EMAIL in `.env.local` is correct
- Check credentials in `.env.local`

**Q: User didn't get download link**
- Verify you clicked approve
- Check `.data/resume_requests.json` for status
- Make sure ADMIN_EMAIL credentials work

**Q: Download link expired**
- Valid for 24 hours from approval
- User must request again if expired

---

## Troubleshooting

### Error: "Unexpected token '<', '<!DOCTYPE' is not valid JSON"

**Cause:** API returning HTML instead of JSON

**Fix:**
1. Check `.env.local` has all 5 variables
2. Verify credentials are correct
3. Run: `curl http://localhost:3000/api/diagnostics`
4. Restart dev server: `npm run dev`

### Error: "Network error. Please check your connection and try again"

**Cause:** Can't reach API endpoint or missing `.env.local`

**Fix:**
1. Verify `.env.local` exists and is configured
2. Restart dev server: `npm run dev`
3. Check server logs for errors
4. Verify file is named `.env.local` (with dot at start)

### Error: "Hydration mismatch" or "server rendered HTML didn't match"

**Fix:** (Already fixed in current version)
1. Clear cache: `rm -rf .next`
2. Restart server: `npm run dev`

### Email Not Sending

**Debug Steps:**
1. Run: `curl http://localhost:3000/api/diagnostics`
2. Check all variables show "✓ Configured"
3. Verify Gmail App Password (16 characters)
4. Check spam/promotions folder
5. Look at server logs for error messages

**Solutions by Email Provider:**

**Gmail:**
- Enable 2FA: https://myaccount.google.com/security
- Get App Password: https://myaccount.google.com/apppasswords
- Use 16-char password as EMAIL_PASSWORD

**Outlook/Office365:**
```env
EMAIL_SERVICE=outlook
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-password
```

**Yahoo:**
```env
EMAIL_SERVICE=yahoo
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-app-password
```

### "public/resume.pdf" Not Found

**Fix:**
```bash
cp /path/to/your/resume.pdf public/resume.pdf
```

Or manually copy file to `public/resume.pdf`

### Modal or Buttons Not Appearing

**Fix:**
1. Clear browser cache: Ctrl+Shift+Delete
2. Clear Next.js cache: `rm -rf .next`
3. Restart server: `npm run dev`
4. Try different browser

### Cannot resolve module at path /lib/db

**Fix:**
1. Run: `npm install`
2. Clear cache: `rm -rf .next`
3. Restart server: `npm run dev`

---

## Configuration

### Environment Variables

| Variable | Required | Value | Example |
|----------|----------|-------|---------|
| EMAIL_SERVICE | Yes | Email provider | `gmail` |
| EMAIL_USER | Yes | Sender email | `princegupt3052@gmail.com` |
| EMAIL_PASSWORD | Yes | App password | `abcd efgh ijkl mnop` |
| ADMIN_EMAIL | Yes | Approval inbox | `princegupt3052@gmail.com` |
| NEXT_PUBLIC_BASE_URL | Yes | Site URL | `http://localhost:3000` |

### Email Providers

**Gmail (Recommended):**
```env
EMAIL_SERVICE=gmail
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-16-char-app-password
```

**Outlook:**
```env
EMAIL_SERVICE=outlook
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-password
```

**Yahoo:**
```env
EMAIL_SERVICE=yahoo
EMAIL_USER=princegupt3052@gmail.com
EMAIL_PASSWORD=your-app-password
```

### Data Storage

**Resume Requests:** `.data/resume_requests.json`
- Auto-created on first request
- Gitignored (not committed to version control)
- Contains: request IDs, emails, tokens, status, timestamps

---

## Customization

### Change Admin Email

Edit `.env.local`:
```env
ADMIN_EMAIL=new-email@example.com
```

### Change Resume Filename

Edit `app/api/resume/download/route.ts`:
```ts
'attachment; filename="your-custom-name.pdf"'
```

### Change Email Templates

Edit `lib/email.ts`:
- `sendAdminApprovalEmail()` - Admin email template
- `sendUserDownloadEmail()` - User email template

Edit `app/api/contact/route.ts`:
- Admin email template (lines ~50-85)
- User confirmation template (lines ~106-145)

### Change Modal Styling

Edit `components/ResumeModal.tsx` - All Tailwind CSS classes

### Change Button Text

Edit `components/Hero.tsx` - Button text and labels

### Add Custom Validation

Edit `components/ResumeModal.tsx` or `components/Contact.tsx`:
- Add/remove validation checks
- Change error messages

### Change Token Expiration

Edit `lib/db.ts` (~line 95):
```ts
// Change 24 to desired hours
request.expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString();
```

---

## Deployment

### Vercel (Recommended)

1. Push code to GitHub
2. Connect to Vercel project
3. Add environment variables in Vercel dashboard:
   - `EMAIL_SERVICE`
   - `EMAIL_USER`
   - `EMAIL_PASSWORD`
   - `ADMIN_EMAIL`
   - `NEXT_PUBLIC_BASE_URL` (your domain)
4. Deploy

### Build

```bash
npm run build
```

### Production Start

```bash
npm start
```

### Other Platforms

Works with any platform supporting Next.js:
- Railway
- Netlify
- DigitalOcean App Platform

**Important:** Set `.env` variables in platform's environment configuration (not `.env.local`)

---

## All Issues Fixed ✅

### 1. ✅ React Hydration Error
**Fixed:** Floating animations now generate client-side only

### 2. ✅ JSON Parse Error
**Fixed:** All APIs return proper JSON with explicit error handling

### 3. ✅ Contact Form Network Error
**Fixed:** Created complete `/api/contact` endpoint

---

## Files Created/Modified

### API Routes (Backend)
- `app/api/resume/request/route.ts` - Resume request
- `app/api/resume/approve/route.ts` - Admin approval
- `app/api/resume/download/route.ts` - Secure download
- `app/api/contact/route.ts` - Contact form
- `app/api/diagnostics/route.ts` - Config checker

### Components (Frontend)
- `components/Hero.tsx` - Updated with modal
- `components/ResumeModal.tsx` - NEW: Email modal
- `components/Contact.tsx` - Updated with validation

### Core Libraries
- `lib/email.ts` - Email service
- `lib/db.ts` - Request storage

### Pages
- `app/resume-approved/page.tsx` - Success page
- `app/resume-error/page.tsx` - Error page

---

## Tech Stack Details

```json
{
  "dependencies": {
    "framer-motion": "^12.38.0",
    "lucide-react": "^1.7.0",
    "next": "16.2.2",
    "next-themes": "^0.4.6",
    "nodemailer": "^6.9.13",
    "react": "19.2.4",
    "react-dom": "19.2.4",
    "react-icons": "^5.6.0",
    "react-type-animation": "^3.2.0"
  },
  "devDependencies": {
    "@tailwindcss/postcss": "^4",
    "@types/node": "^20",
    "@types/react": "^19",
    "@types/react-dom": "^19",
    "@types/nodemailer": "^6.4.14",
    "eslint": "^9",
    "eslint-config-next": "16.2.2",
    "tailwindcss": "^4",
    "typescript": "^5"
  }
}
```

---

## Contact & Support

**Prince Gupt**
- Email: princegupt3052@gmail.com
- Phone: +91 7084315271
- LinkedIn: [Prince Gupt](https://www.linkedin.com/in/prince-gupt-175289322)
- Location: Lucknow, Uttar Pradesh, India

---

## License

This project is open source and available under the MIT License.

---

**Status:** ✅ All Systems Operational
**Portfolio:** Ready for Production
**Documentation:** Complete
**Time to Launch:** 🚀
