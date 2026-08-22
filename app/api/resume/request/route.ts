import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";

export async function POST(req: NextRequest) {
  try {
    // Parse request body
    let userEmail;
    try {
      const body = await req.json();
      userEmail = body.userEmail;
    } catch (parseError) {
      console.error("Error parsing request body:", parseError);
      return NextResponse.json(
        { error: "Invalid request format" },
        { status: 400 }
      );
    }

    // Validate email
    if (!userEmail || typeof userEmail !== "string" || !userEmail.includes("@")) {
      return NextResponse.json(
        { error: "Invalid email address" },
        { status: 400 }
      );
    }

    // Dynamic imports to avoid build-time issues
    const { createResumeRequest } = await import("@/lib/db");
    const { sendAdminApprovalEmail } = await import("@/lib/email");

    // Create resume request
    let request;
    try {
      request = createResumeRequest(userEmail);
    } catch (dbError) {
      console.error("Database error:", dbError);
      return NextResponse.json(
        { error: "Failed to create request" },
        { status: 500 }
      );
    }

    // Send admin approval email
    const adminEmail = process.env.ADMIN_EMAIL;
    if (!adminEmail) {
      console.warn("ADMIN_EMAIL not configured - request created but email not sent");
      // Don't fail here - request is still created
    } else {
      try {
        await sendAdminApprovalEmail(
          adminEmail,
          userEmail,
          request.id,
          request.approvalToken || ""
        );
      } catch (emailError) {
        console.error("Failed to send admin email:", emailError);
        // Continue anyway - request was created
      }
    }

    return NextResponse.json(
      {
        success: true,
        message:
          "Resume request submitted. Check your email for further instructions.",
        requestId: request.id,
      },
      { status: 201 }
    );
  } catch (error) {
    console.error("Unhandled error in resume request:", error);
    return NextResponse.json(
      {
        error: "Failed to process request",
        details: error instanceof Error ? error.message : "Unknown error",
      },
      { status: 500 }
    );
  }
}
