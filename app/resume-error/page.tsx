import { AlertCircle, Home } from "lucide-react";
import Link from "next/link";

export default function ResumeError() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-gray-900 to-zinc-900">
      <div className="text-center px-4 max-w-md">
        {/* Error Icon */}
        <div className="flex justify-center mb-6">
          <div className="relative">
            <div className="absolute inset-0 bg-red-500/20 rounded-full blur-lg animate-pulse"></div>
            <div className="relative bg-red-500/10 border border-red-500/30 rounded-full p-4">
              <AlertCircle className="w-12 h-12 text-red-400" />
            </div>
          </div>
        </div>

        {/* Title */}
        <h1 className="text-3xl md:text-4xl font-bold text-white mb-4">
          Unauthorized Access
        </h1>

        {/* Message */}
        <p className="text-gray-300 text-lg mb-6">
          The download link is invalid or has expired. Please request a new copy of the resume.
        </p>

        {/* Details */}
        <div className="bg-red-500/10 border border-red-500/20 rounded-lg p-4 mb-8 text-left">
          <p className="text-red-400 text-sm">
            <strong>Possible reasons:</strong>
          </p>
          <ul className="text-red-400 text-sm mt-2 space-y-1">
            <li>• The link has expired (valid for 24 hours)</li>
            <li>• The request was not approved</li>
            <li>• The link is incorrect or malformed</li>
          </ul>
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
          Go back to the portfolio and click "Download Resume" to submit a new request.
        </p>
      </div>
    </div>
  );
}
