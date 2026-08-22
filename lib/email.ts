import nodemailer from "nodemailer";

let transporter: any = null;

function getTransporter() {
  if (transporter) return transporter;

  const emailService = process.env.EMAIL_SERVICE || "gmail";
  const emailUser = process.env.EMAIL_USER;
  const emailPassword = process.env.EMAIL_PASSWORD;

  if (!emailUser || !emailPassword) {
    console.warn(
      "⚠️ Email credentials not configured. Set EMAIL_USER and EMAIL_PASSWORD in .env.local"
    );
    throw new Error(
      "Email service not configured. Please set EMAIL_USER, EMAIL_PASSWORD, and EMAIL_SERVICE in .env.local"
    );
  }

  transporter = nodemailer.createTransport({
    service: emailService,
    auth: {
      user: emailUser,
      pass: emailPassword,
    },
  });

  // Verify connection
  transporter.verify((error: any) => {
    if (error) {
      console.error("❌ Email service verification failed:", error.message);
      console.warn(
        "Make sure your email credentials are correct in .env.local"
      );
    } else {
      console.log("✓ Email service ready");
    }
  });

  return transporter;
}

interface EmailParams {
  to: string;
  subject: string;
  html: string;
}

export async function sendEmail({ to, subject, html }: EmailParams) {
  try {
    const emailTransport = getTransporter();
    const info = await emailTransport.sendMail({
      from: process.env.EMAIL_FROM || process.env.EMAIL_USER,
      to,
      subject,
      html,
    });
    console.log("✓ Email sent:", info.messageId);
    return { success: true, messageId: info.messageId };
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : "Unknown error";
    console.error("❌ Error sending email:", errorMessage);
    throw new Error(`Failed to send email: ${errorMessage}`);
  }
}

/**
 * Generate and send admin approval email
 */
export async function sendAdminApprovalEmail(
  adminEmail: string,
  userEmail: string,
  requestId: string,
  approvalToken: string
) {
  const approvalUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/api/resume/approve?token=${approvalToken}&requestId=${requestId}`;

  const htmlContent = `
    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f5f5f5;">
      <div style="background: white; border-radius: 8px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
        <h2 style="color: #1a3a52; margin-bottom: 20px; border-bottom: 3px solid #3b82f6; padding-bottom: 10px;">Resume Download Request</h2>
        
        <p style="color: #4b5563; font-size: 16px; line-height: 1.6;">
          You have received a resume download request from:
        </p>
        
        <div style="background: #f0f7ff; border-left: 4px solid #3b82f6; padding: 15px; margin: 20px 0; border-radius: 4px;">
          <p style="color: #1a3a52; font-size: 18px; font-weight: bold; margin: 0;">
            ${userEmail}
          </p>
        </div>
        
        <p style="color: #4b5563; font-size: 16px; line-height: 1.6; margin-top: 20px;">
          Click the button below to approve the request. The user will receive a secure download link via email.
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
          <a href="${approvalUrl}" style="display: inline-block; background: #3b82f6; color: white; text-decoration: none; padding: 12px 30px; border-radius: 6px; font-weight: bold; font-size: 16px; transition: background 0.3s;">
            ✓ Approve Request
          </a>
        </div>

        <p style="color: #7a8693; font-size: 12px; margin-top: 30px; border-top: 1px solid #e5e7eb; padding-top: 15px;">
          Request ID: ${requestId}
        </p>
      </div>
    </div>
  `;

  return sendEmail({
    to: adminEmail,
    subject: `New Resume Download Request from ${userEmail}`,
    html: htmlContent,
  });
}

/**
 * Generate and send user download link email
 */
export async function sendUserDownloadEmail(
  userEmail: string,
  downloadToken: string,
  requestId: string
) {
  const downloadUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/api/resume/download?token=${downloadToken}&requestId=${requestId}`;

  const htmlContent = `
    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f5f5f5;">
      <div style="background: white; border-radius: 8px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
        <h2 style="color: #1a3a52; margin-bottom: 20px; border-bottom: 3px solid #10b981; padding-bottom: 10px;">Your Resume is Ready!</h2>
        
        <p style="color: #4b5563; font-size: 16px; line-height: 1.6;">
          Great news! Your request to download the resume has been approved. Click the button below to download.
        </p>
        
        <div style="text-align: center; margin: 30px 0;">
          <a href="${downloadUrl}" style="display: inline-block; background: #10b981; color: white; text-decoration: none; padding: 12px 30px; border-radius: 6px; font-weight: bold; font-size: 16px; transition: background 0.3s;">
            📥 Download Resume
          </a>
        </div>

        <p style="color: #7a8693; font-size: 13px; margin-top: 20px;">
          This link will be valid for 24 hours. If it expires, you can request a new one.
        </p>

        <p style="color: #7a8693; font-size: 12px; margin-top: 30px; border-top: 1px solid #e5e7eb; padding-top: 15px;">
          Request ID: ${requestId}
        </p>
      </div>
    </div>
  `;

  return sendEmail({
    to: userEmail,
    subject: "Your Resume Download Link - Prince Gupt",
    html: htmlContent,
  });
}
