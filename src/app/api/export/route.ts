import { exportAll } from "@/server/data";
import { errorResponse } from "@/server/http";

export const dynamic = "force-dynamic";

const esc = (s: string) =>
  s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");

export async function GET(req: Request) {
  try {
    const format = new URL(req.url).searchParams.get("format") === "html" ? "html" : "json";
    const data = await exportAll();
    const stamp = new Date().toISOString().slice(0, 10);
    if (format === "json") {
      return new Response(JSON.stringify(data, null, 2), {
        headers: {
          "content-type": "application/json; charset=utf-8",
          "content-disposition": `attachment; filename="pixelmarks-${stamp}.json"`,
        },
      });
    }
    const line = (b: (typeof data.bookmarks)[number], indent: string) => {
      const add = Math.floor(new Date(b.createdAt).getTime() / 1000);
      const tags = b.tags.length ? ` TAGS="${esc(b.tags.join(","))}"` : "";
      const desc = b.description ? `\n${indent}<DD>${esc(b.description)}` : "";
      return `${indent}<DT><A HREF="${esc(b.url)}" ADD_DATE="${add}"${tags}>${esc(b.title)}</A>${desc}`;
    };
    const parts: string[] = [
      "<!DOCTYPE NETSCAPE-Bookmark-file-1>",
      '<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">',
      "<TITLE>Bookmarks</TITLE>",
      "<H1>Pixelmarks</H1>",
      "<DL><p>",
    ];
    for (const c of data.collections) {
      parts.push(`    <DT><H3>${esc(c.name)}</H3>`, "    <DL><p>");
      for (const b of data.bookmarks.filter((x) => x.collectionId === c.id)) parts.push(line(b, "        "));
      parts.push("    </DL><p>");
    }
    for (const b of data.bookmarks.filter((x) => x.collectionId === null)) parts.push(line(b, "    "));
    parts.push("</DL><p>");
    return new Response(parts.join("\n"), {
      headers: {
        "content-type": "text/html; charset=utf-8",
        "content-disposition": `attachment; filename="pixelmarks-${stamp}.html"`,
      },
    });
  } catch (err) {
    return errorResponse(err);
  }
}
