import { NextResponse } from "next/server";

export const runtime = "nodejs";

export async function GET() {
  const diagnostics = {
    environment: {
      NODE_ENV: process.env.NODE_ENV || "not set",
      EMAIL_SERVICE: process.env.EMAIL_SERVICE ? "✓ Configured" : "✗ Missing",
      EMAIL_USER: process.env.EMAIL_USER ? "✓ Configured" : "✗ Missing",
      EMAIL_PASSWORD: process.env.EMAIL_PASSWORD ? "✓ Configured" : "✗ Missing",
      ADMIN_EMAIL: process.env.ADMIN_EMAIL ? "✓ Configured" : "✗ Missing",
      NEXT_PUBLIC_BASE_URL: process.env.NEXT_PUBLIC_BASE_URL || "not set",
    },
    checks: {
      emailConfigured:
        !!(
          process.env.EMAIL_SERVICE &&
          process.env.EMAIL_USER &&
          process.env.EMAIL_PASSWORD
        ),
      adminEmailSet: !!process.env.ADMIN_EMAIL,
      baseUrlSet: !!process.env.NEXT_PUBLIC_BASE_URL,
    },
    status:
      process.env.EMAIL_SERVICE &&
      process.env.EMAIL_USER &&
      process.env.EMAIL_PASSWORD &&
      process.env.ADMIN_EMAIL
        ? "✓ System Ready"
        : "✗ Configuration Incomplete",
    requiredSteps: [
      !process.env.EMAIL_SERVICE &&
        "1. Set EMAIL_SERVICE in .env.local (e.g., 'gmail')",
      !process.env.EMAIL_USER &&
        "2. Set EMAIL_USER in .env.local (your email address)",
      !process.env.EMAIL_PASSWORD &&
        "3. Set EMAIL_PASSWORD in .env.local (Gmail App Password)",
      !process.env.ADMIN_EMAIL &&
        "4. Set ADMIN_EMAIL in .env.local (where approvals go)",
    ].filter(Boolean),
  };

  return NextResponse.json(diagnostics);
}
