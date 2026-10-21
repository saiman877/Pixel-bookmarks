import { importData, InputError } from "@/server/data";
import { errorResponse, json, readJson } from "@/server/http";
import type { ImportPayload } from "@/lib/types";

export const dynamic = "force-dynamic";

export async function POST(req: Request) {
  try {
    const body = await readJson<ImportPayload>(req);
    if (!Array.isArray(body.bookmarks)) throw new InputError("Nothing to import");
    const result = await importData({
      collections: Array.isArray(body.collections) ? body.collections : [],
      bookmarks: body.bookmarks,
    });
    return json(result);
  } catch (err) {
    return errorResponse(err);
  }
}
