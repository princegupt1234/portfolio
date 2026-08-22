"use client";

import { useState } from "react";
import { X, Download, AlertCircle } from "lucide-react";

interface ResumeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ResumeModal = ({ isOpen, onClose }: ResumeModalProps) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleDownload = async () => {
    setError("");
    setLoading(true);

    try {
      const response = await fetch("/api/resume/download");

      if (!response.ok) {
        throw new Error("Failed to download resume");
      }

      // Get the blob from the response
      const blob = await response.blob();

      // Create a temporary URL and trigger download
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "Resume_Prince_Gupt.pdf";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      // Close modal after successful download
      setTimeout(() => {
        onClose();
      }, 500);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "An error occurred";
      console.error("Download error:", message);
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 backdrop-blur-sm z-40"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-50 w-full max-w-md mx-4">
        <div className="bg-gray-900 border border-gray-700 rounded-2xl shadow-2xl p-8 relative">
          {/* Close Button */}
          <button
            onClick={onClose}
            className="absolute top-4 right-4 text-gray-400 hover:text-white transition-colors"
          >
            <X className="w-6 h-6" />
          </button>

          {/* Content */}
          <div className="text-center">
            <div className="flex justify-center mb-4">
              <div className="bg-blue-500/20 p-3 rounded-full">
                <Download className="w-6 h-6 text-blue-400" />
              </div>
            </div>

            <h2 className="text-2xl font-bold text-white mb-2">
              Download Resume
            </h2>
            <p className="text-gray-400 mb-6">
              Click the button below to download my resume in PDF format.
            </p>

            {/* Error Message */}
            {error && (
              <div className="mb-4 p-3 bg-red-500/10 border border-red-500/30 rounded-lg flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-red-400" />
                <span className="text-red-400 text-sm">{error}</span>
              </div>
            )}

            {/* Download Button */}
            <button
              onClick={handleDownload}
              disabled={loading}
              className="w-full px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white rounded-lg font-semibold flex items-center justify-center gap-2 transition-colors"
            >
              <Download className="w-5 h-5" />
              {loading ? "Downloading..." : "Download Resume"}
            </button>

            {/* Close Button */}
            <button
              onClick={onClose}
              className="w-full mt-3 px-6 py-3 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded-lg font-semibold transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </>
  );
};
