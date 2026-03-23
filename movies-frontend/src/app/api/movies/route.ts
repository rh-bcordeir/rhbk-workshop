import { auth } from "@/auth";
import { NextRequest, NextResponse } from "next/server";

const MS_MOVIES_URL = process.env.MS_MOVIES_URL || "http://localhost:8080";

export async function GET() {
  const session = await auth();

  if (!session?.accessToken) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const res = await fetch(`${MS_MOVIES_URL}/movies`, {
    headers: {
      Authorization: `Bearer ${session.accessToken}`,
      Accept: "application/json",
    },
    cache: "no-store",
  });

  if (!res.ok) {
    return NextResponse.json(
      { error: "Failed to fetch movies" },
      { status: res.status }
    );
  }

  const movies = await res.json();
  return NextResponse.json(movies);
}

export async function POST(request: NextRequest) {
  const session = await auth();

  if (!session?.accessToken) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  const body = await request.json();
  console.log("\n", session.accessToken);

  const res = await fetch(`${MS_MOVIES_URL}/movies`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${session.accessToken}`,
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const errorMessages: Record<number, string> = {
      403: "You don't have permission to create movies.",
      401: "Your session has expired. Please sign in again.",
    };
    const error =
      errorMessages[res.status] ||
      `Failed to create movie (status ${res.status})`;
    return NextResponse.json({ error }, { status: res.status });
  }

  const movie = await res.json();
  return NextResponse.json(movie, { status: 201 });
}
