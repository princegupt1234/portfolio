import fs from "fs";
import path from "path";
import crypto from "crypto";

interface ResumeRequest {
  id: string;
  userEmail: string;
  status: "pending" | "approved" | "downloaded";
  createdAt: string;
  approvedAt?: string;
  downloadedAt?: string;
  approvalToken?: string;
  downloadToken?: string;
  expiresAt?: string;
}

const dbPath = process.env.NODE_ENV === 'production' ? '/tmp' : path.join(process.cwd(), ".data");
const dbFile = path.join(dbPath, "resume_requests.json");

// Ensure .data directory exists
function ensureDbDirectory() {
  if (!fs.existsSync(dbPath)) {
    fs.mkdirSync(dbPath, { recursive: true });
  }
}

// Read all requests
function readRequests(): ResumeRequest[] {
  ensureDbDirectory();
  if (!fs.existsSync(dbFile)) {
    return [];
  }
  try {
    const data = fs.readFileSync(dbFile, "utf-8");
    return JSON.parse(data) || [];
  } catch {
    return [];
  }
}

// Write requests
function writeRequests(requests: ResumeRequest[]) {
  ensureDbDirectory();
  fs.writeFileSync(dbFile, JSON.stringify(requests, null, 2), "utf-8");
}

// Create new request
export function createResumeRequest(userEmail: string): ResumeRequest {
  const requests = readRequests();
  const id = crypto.randomUUID();
  const approvalToken = crypto.randomBytes(32).toString("hex");

  const newRequest: ResumeRequest = {
    id,
    userEmail,
    status: "pending",
    createdAt: new Date().toISOString(),
    approvalToken,
  };

  requests.push(newRequest);
  writeRequests(requests);

  return newRequest;
}

// Get request by ID
export function getResumeRequest(id: string): ResumeRequest | null {
  const requests = readRequests();
  return requests.find((r) => r.id === id) || null;
}

// Get request by approval token
export function getRequestByApprovalToken(token: string): ResumeRequest | null {
  const requests = readRequests();
  return requests.find((r) => r.approvalToken === token) || null;
}

// Get request by download token
export function getRequestByDownloadToken(token: string): ResumeRequest | null {
  const requests = readRequests();
  return requests.find((r) => r.downloadToken === token) || null;
}

// Approve request
export function approveResumeRequest(id: string): ResumeRequest | null {
  const requests = readRequests();
  const request = requests.find((r) => r.id === id);

  if (!request) return null;

  request.status = "approved";
  request.approvedAt = new Date().toISOString();
  request.downloadToken = crypto.randomBytes(32).toString("hex");
  request.expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(); // 24 hours

  writeRequests(requests);
  return request;
}

// Mark as downloaded
export function markAsDownloaded(id: string): ResumeRequest | null {
  const requests = readRequests();
  const request = requests.find((r) => r.id === id);

  if (!request) return null;

  request.status = "downloaded";
  request.downloadedAt = new Date().toISOString();

  writeRequests(requests);
  return request;
}

// Check if token is valid (not expired)
export function isTokenValid(expiresAt?: string): boolean {
  if (!expiresAt) return false;
  return new Date() < new Date(expiresAt);
}
