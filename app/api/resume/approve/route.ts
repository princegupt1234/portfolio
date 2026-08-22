import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";

/**
 * GET endpoint for clicking approval link from email
 */
export async function GET(req: NextRequest) {
  try {
    const token = req.nextUrl.searchParams.get("token");
    const requestId = req.nextUrl.searchParams.get("requestId");

    if (!token || !requestId) {
      return NextResponse.redirect(new URL("/resume-error", req.url));
    }

    // Dynamic imports
    const { getRequestByApprovalToken, approveResumeRequest } = await import("@/lib/db");
    const { sendUserDownloadEmail } = await import("@/lib/email");

    // Verify the token
    const request = getRequestByApprovalToken(token);
    if (!request || request.id !== requestId) {
      return NextResponse.redirect(new URL("/resume-error", req.url));
    }

    // Approve the request
    const approvedRequest = approveResumeRequest(requestId);
    if (!approvedRequest) {
      return NextResponse.redirect(new URL("/resume-error", req.url));
    }

    // Send download link to user
    try {
      await sendUserDownloadEmail(
        approvedRequest.userEmail,
        approvedRequest.downloadToken || "",
        requestId
      );
    } catch (emailError) {
      console.error("Failed to send user email:", emailError);
      return NextResponse.redirect(new URL("/resume-error", req.url));
    }

    return NextResponse.redirect(new URL("/resume-approved", req.url));
  } catch (error) {
    console.error("Error in approval link:", error);
    return NextResponse.redirect(new URL("/resume-error", req.url));
  }
}

/**
 * POST endpoint for programmatic approval
 */
export async function POST(req: NextRequest) {
  try {
    const { requestId, approvalToken } = await req.json();

    // Validate inputs
    if (!requestId || !approvalToken) {
      return NextResponse.json(
        { error: "Missing required fields" },
        { status: 400 }
      );
    }

    // Dynamic imports
    const { getRequestByApprovalToken, approveResumeRequest } = await import("@/lib/db");
    const { sendUserDownloadEmail } = await import("@/lib/email");

    // Verify approval token
    const request = await getRequestByApprovalToken(approvalToken);
    if (!request || request.id !== requestId) {
      return NextResponse.json(
        { error: "Invalid approval token" },
        { status: 401 }
      );
    }

    // Approve the request
    const approvedRequest = approveResumeRequest(requestId);
    if (!approvedRequest) {
      return NextResponse.json(
        { error: "Request not found" },
        { status: 404 }
      );
    }

    // Send download link to user
    try {
      await sendUserDownloadEmail(
        approvedRequest.userEmail,
        approvedRequest.downloadToken || "",
        requestId
      );
    } catch (emailError) {
      console.error("Failed to send user email:", emailError);
      return NextResponse.json(
        { error: "Failed to send download link email" },
        { status: 500 }
      );
    }

    return NextResponse.json(
      {
        success: true,
        message: "Request approved. Download link sent to user.",
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Error approving request:", error);
    return NextResponse.json(
      { error: "Failed to approve request" },
      { status: 500 }
    );
  }
}
