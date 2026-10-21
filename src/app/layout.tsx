import type { Metadata, Viewport } from "next";
import { Google_Sans_Flex } from "next/font/google";
import localFont from "next/font/local";
import { cookies } from "next/headers";
import type { ReactNode } from "react";
import { parseThemeCookie, THEME_COOKIE, themeCss } from "@/lib/color";
import "./globals.css";

const sans = Google_Sans_Flex({
  subsets: ["latin"],
  variable: "--font-flex",
  axes: ["ROND", "wdth", "opsz"],
  display: "swap",
  adjustFontFallback: false,
});

const symbols = localFont({
  src: "./fonts/material-symbols-rounded.woff2",
  variable: "--font-symbols",
  weight: "100 700",
  display: "block",
  preload: true,
  adjustFontFallback: false,
});

export const metadata: Metadata = {
  title: "Pixelmarks — Expressive bookmarks",
  description:
    "A Material 3 Expressive bookmark manager inspired by Pixel Bookmarks. Save, organise and rediscover links with collections, tags and reminders.",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  viewportFit: "cover",
};

export default async function RootLayout({ children }: { children: ReactNode }) {
  const store = await cookies();
  const theme = parseThemeCookie(store.get(THEME_COOKIE)?.value);
  const modeClass = theme.mode === "dark" ? "dark" : theme.mode === "light" ? "light" : "";
  return (
    <html
      lang="en"
      className={`${sans.variable} ${symbols.variable} ${modeClass}`}
      suppressHydrationWarning
    >
      <head>
        <style id="theme-vars" dangerouslySetInnerHTML={{ __html: themeCss(theme) }} />
      </head>
      <body className="antialiased">{children}</body>
    </html>
  );
}
