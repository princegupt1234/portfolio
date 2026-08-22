import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";

export async function POST(req: NextRequest) {
  try {
    // Parse request body
    let data;
    try {
      data = await req.json();
    } catch (parseError) {
      console.error("Error parsing request body:", parseError);
      return NextResponse.json(
        { error: "Invalid request format" },
        { status: 400 }
      );
    }

    const { name, email, subject, message } = data;

    // Validate required fields
    if (!name || !email || !subject || !message) {
      return NextResponse.json(
        { error: "All fields are required" },
        { status: 400 }
      );
    }

    // Validate email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return NextResponse.json(
        { error: "Invalid email address" },
        { status: 400 }
      );
    }

    // Dynamic imports
    const { sendEmail } = await import("@/lib/email");

    // Get admin email
    const adminEmail = process.env.ADMIN_EMAIL;
    if (!adminEmail) {
      console.error("ADMIN_EMAIL not configured for contact form");
      return NextResponse.json(
        { error: "Server configuration error - contact form not available" },
        { status: 500 }
      );
    }

    // Create email content
    const htmlContent = `
      <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f5f5f5;">
        <div style="background: white; border-radius: 8px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
          <h2 style="color: #1a3a52; margin-bottom: 20px; border-bottom: 3px solid #3b82f6; padding-bottom: 10px;">
            New Contact Form Submission
          </h2>

          <div style="background: #f0f7ff; border-left: 4px solid #3b82f6; padding: 15px; margin: 20px 0; border-radius: 4px;">
            <p style="color: #1a3a52; margin: 0;"><strong>From:</strong> ${name}</p>
            <p style="color: #1a3a52; margin: 10px 0 0 0;"><strong>Email:</strong> ${email}</p>
          </div>

          <div style="margin: 20px 0;">
            <p style="color: #4b5563; font-size: 14px; font-weight: bold; margin: 0 0 10px 0;">
              Subject: ${subject}
            </p>
          </div>

          <div style="background: #f9f9f9; padding: 15px; border-left: 4px solid #6b7280; margin: 20px 0; border-radius: 4px;">
            <p style="color: #4b5563; font-size: 14px; line-height: 1.6; white-space: pre-wrap; margin: 0;">
              ${message}
            </p>
          </div>

          <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #e5e7eb;">
            <p style="color: #7a8693; font-size: 12px; margin: 0;">
              This message was sent from your portfolio contact form.
              <br>
              Reply directly to ${email}
            </p>
          </div>
        </div>
      </div>
    `;

    // Send email to admin
    try {
      await sendEmail({
        to: adminEmail,
        subject: `New Contact: ${subject} - from ${name}`,
        html: htmlContent,
      });
    } catch (emailError) {
      console.error("Failed to send contact email:", emailError);
      return NextResponse.json(
        {
          error: "Failed to send message. Email service error. Please try again later.",
        },
        { status: 500 }
      );
    }

    // Send confirmation email to user
    const confirmationHtml = `
      <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f5f5f5;">
        <div style="background: white; border-radius: 8px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
          <h2 style="color: #1a3a52; margin-bottom: 20px; border-bottom: 3px solid #10b981; padding-bottom: 10px;">
            Message Received! ✓
          </h2>

          <p style="color: #4b5563; font-size: 16px; line-height: 1.6;">
            Hi ${name},
            <br><br>
            Thank you for reaching out! I've received your message and will get back to you as soon as possible.
            <br><br>
            <strong>Your Message Details:</strong>
          </p>

          <div style="background: #f0f7ff; border-left: 4px solid #3b82f6; padding: 15px; margin: 20px 0; border-radius: 4px;">
            <p style="color: #1a3a52; margin: 0;"><strong>Subject:</strong> ${subject}</p>
          </div>

          <p style="color: #4b5563; font-size: 14px; line-height: 1.6; margin-top: 20px;">
            I'll review your message and respond to ${email} within 24-48 hours.
          </p>

          <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #e5e7eb;">
            <p style="color: #7a8693; font-size: 12px; margin: 0;">
              Best regards,
              <br>
              Prince Gupt
            </p>
          </div>
        </div>
      </div>
    `;

    try {
      await sendEmail({
        to: email,
        subject: `Thank you for contacting me - ${subject}`,
        html: confirmationHtml,
      });
    } catch (confirmError) {
      // Don't fail if confirmation email fails - main message was sent
      console.error("Failed to send confirmation email:", confirmError);
    }

    return NextResponse.json(
      {
        success: true,
        message: "Message sent successfully! I'll get back to you soon.",
      },
      { status: 200 }
    );
  } catch (error) {
    console.error("Unhandled error in contact route:", error);
    return NextResponse.json(
      {
        error: "Failed to process your request. Please try again later.",
      },
      { status: 500 }
    );
  }
}
