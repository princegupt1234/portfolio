import type { Metadata } from "next";
import { Inter, Poppins } from "next/font/google";
import { Providers } from "../components/Providers";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["300", "400", "500", "600", "700"],
  variable: "--font-poppins",
});

export const metadata: Metadata = {
  title: "Prince Gupt - Full Stack Developer",
  description: "Full Stack Developer specializing in building scalable, responsive, and user-centric web applications using modern technologies like React, Node.js, and SQL.",
  keywords: ["Full Stack Developer", "React", "Node.js", "Web Development", "Portfolio"],
  authors: [{ name: "Prince Gupt" }],
  openGraph: {
    title: "Prince Gupt - Full Stack Developer",
    description: "Building scalable and user-focused web applications",
    type: "website",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${inter.variable} ${poppins.variable} h-full antialiased`}
      suppressHydrationWarning
    >
      <body className="min-h-full flex flex-col font-sans" suppressHydrationWarning>
        <Providers>
          {children}
        </Providers>
      </body>
    </html>
  );
}
