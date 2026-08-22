import { NextResponse } from "next/server";

export const runtime = "nodejs";

export async function GET() {
  try {
    // Fetch resume file from public URL
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || 'https://princegportfolio.netlify.app';
    const fileUrl = `${baseUrl}/Resumeprince%201.pdf`;
    
    const fileResponse = await fetch(fileUrl);
    
    if (!fileResponse.ok) {
      console.error("Failed to fetch resume file:", fileResponse.status);
      return NextResponse.json(
        { error: "Resume file not available" },
        { status: 404 }
      );
    }

    const fileBuffer = await fileResponse.arrayBuffer();

    // Return file with download headers
    return new NextResponse(fileBuffer, {
      headers: {
        "Content-Type": "application/pdf",
        "Content-Disposition": 'attachment; filename="Resume_Prince_Gupt.pdf"',
        "Cache-Control": "no-cache, no-store, must-revalidate",
      },
    });
  } catch (error) {
    console.error("Error downloading resume:", error);
    return NextResponse.json(
      { error: "Failed to download resume" },
      { status: 500 }
    );
  }
}
