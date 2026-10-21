import { InputError } from "@/server/data";
import { errorResponse, json } from "@/server/http";
import { fetchMetadata } from "@/server/metadata";
import { normalizeUrl } from "@/lib/utils";

export const dynamic = "force-dynamic";

export async function GET(req: Request) {
  try {
    const raw = new URL(req.url).searchParams.get("url") ?? "";
    if (!normalizeUrl(raw)) throw new InputError("Please enter a valid http(s) link");
    return json(await fetchMetadata(raw));
  } catch (err) {
    return errorResponse(err);
  }
}
