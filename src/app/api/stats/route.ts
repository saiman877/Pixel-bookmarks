import { getStats } from "@/server/data";
import { errorResponse, json } from "@/server/http";

export const dynamic = "force-dynamic";

export async function GET() {
  try {
    return json(await getStats());
  } catch (err) {
    return errorResponse(err);
  }
}
