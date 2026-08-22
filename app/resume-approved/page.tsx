import { CheckCircle, Home } from "lucide-react";
import Link from "next/link";

export default function ResumeApproved() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-gray-900 to-zinc-900">
      <div className="text-center px-4 max-w-md">
        {/* Success Icon */}
        <div className="flex justify-center mb-6">
          <div className="relative">
            <div className="absolute inset-0 bg-green-500/20 rounded-full blur-lg animate-pulse"></div>
            <div className="relative bg-green-500/10 border border-green-500/30 rounded-full p-4">
              <CheckCircle className="w-12 h-12 text-green-400" />
            </div>
          </div>
        </div>

        {/* Title */}
        <h1 className="text-3xl md:text-4xl font-bold text-white mb-4">
          Request Approved!
        </h1>

        {/* Message */}
        <p className="text-gray-300 text-lg mb-6">
          Thank you! Your resume request has been approved. A download link has been sent to your email address.
        </p>

        {/* Details */}
        <div className="bg-green-500/10 border border-green-500/20 rounded-lg p-4 mb-8">
          <p className="text-green-400 text-sm">
            📧 Check your inbox for the secure download link. The link will be valid for 24 hours.
          </p>
        </div>

        {/* Action Buttons */}
        <div className="space-y-3">
          <Link
            href="/"
            className="inline-flex items-center justify-center gap-2 w-full px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 transition-all font-semibold"
          >
            <Home className="w-5 h-5" />
            Back to Home
          </Link>
        </div>

        {/* Footer */}
        <p className="text-gray-500 text-xs mt-8">
          If you don't see the email, check your spam folder or request again.
        </p>
      </div>
    </div>
  );
}
