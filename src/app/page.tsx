import { cookies } from "next/headers";
import { BookmarkApp } from "@/components/app/BookmarkApp";
import { parseThemeCookie, THEME_COOKIE, VIEW_COOKIE } from "@/lib/color";
import { getStats, listBookmarks, listCollections } from "@/server/data";
import { ensureSeeded } from "@/server/seed";

export const dynamic = "force-dynamic";

export default async function HomePage() {
  await ensureSeeded();
  const [collections, stats, bookmarks, store] = await Promise.all([
    listCollections(),
    getStats(),
    listBookmarks({ view: "all", sort: "recent" }),
    cookies(),
  ]);
  const theme = parseThemeCookie(store.get(THEME_COOKIE)?.value);
  const viewMode = store.get(VIEW_COOKIE)?.value === "list" ? "list" : "grid";

  return (
    <BookmarkApp
      initialCollections={collections}
      initialStats={stats}
      initialBookmarks={bookmarks}
      initialTheme={theme}
      initialViewMode={viewMode}
    />
  );
}
