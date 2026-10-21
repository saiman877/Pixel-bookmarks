package com.pixelmarks.app.data.remote

import com.pixelmarks.app.data.model.WebsiteMetadata
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import java.net.URLEncoder

class MetadataDetector {

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }

    suspend fun detect(rawUrl: String): WebsiteMetadata = withContext(Dispatchers.IO) {
        val normalizedUrl = normalizeUrl(rawUrl)
        val domain = extractDomain(normalizedUrl)
        val screenshotUrl = "https://s0.wp.com/mshots/v1/${URLEncoder.encode(normalizedUrl, "UTF-8")}?w=960"
        val faviconUrl = "https://www.google.com/s2/favicons?domain=${URLEncoder.encode(domain, "UTF-8")}&sz=128"

        // 1. YouTube specialized adapter
        val ytMatch = Regex("""(?:youtube\.com/(?:watch\?.*?v=|shorts/|embed/)|youtu\.be/)([a-zA-Z0-9_-]{11})""", RegexOption.IGNORE_CASE)
            .find(normalizedUrl)
        if (ytMatch != null) {
            val videoId = ytMatch.groupValues[1]
            val maxThumb = "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
            try {
                val oembedStr = client.get("https://www.youtube.com/oembed?url=${URLEncoder.encode(normalizedUrl, "UTF-8")}&format=json").bodyAsText()
                val json = JSONObject(oembedStr)
                return@withContext WebsiteMetadata(
                    url = normalizedUrl,
                    domain = domain,
                    title = json.optString("title", "YouTube Video"),
                    description = "Video by " + json.optString("author_name", "YouTube"),
                    imageUrl = json.optString("thumbnail_url", maxThumb),
                    screenshotUrl = screenshotUrl,
                    faviconUrl = "https://www.youtube.com/s/desktop/f1725b82/img/favicon.ico",
                    siteName = "YouTube"
                )
            } catch (e: Exception) {
                return@withContext WebsiteMetadata(
                    url = normalizedUrl,
                    domain = domain,
                    title = "YouTube Video",
                    description = "Watch on YouTube",
                    imageUrl = maxThumb,
                    screenshotUrl = screenshotUrl,
                    faviconUrl = faviconUrl,
                    siteName = "YouTube"
                )
            }
        }

        // 2. Wikipedia specialized adapter
        val wikiMatch = Regex("""^https?://([a-z0-9_-]{2,})\.wikipedia\.org/wiki/(.+)""", RegexOption.IGNORE_CASE)
            .find(normalizedUrl)
        if (wikiMatch != null) {
            val lang = wikiMatch.groupValues[1]
            val slug = wikiMatch.groupValues[2].split("#")[0]
            try {
                val summaryStr = client.get("https://$lang.wikipedia.org/api/rest_v1/page/summary/${URLEncoder.encode(slug, "UTF-8")}") {
                    header("User-Agent", "Pixelmarks/1.0 (Android App)")
                }.bodyAsText()
                val json = JSONObject(summaryStr)
                val original = json.optJSONObject("originalimage")?.optString("source")
                val thumb = json.optJSONObject("thumbnail")?.optString("source")
                return@withContext WebsiteMetadata(
                    url = normalizedUrl,
                    domain = domain,
                    title = json.optString("title", slug.replace("_", " ")),
                    description = json.optString("extract", json.optString("description", "")),
                    imageUrl = original ?: thumb ?: screenshotUrl,
                    screenshotUrl = screenshotUrl,
                    faviconUrl = "https://$lang.wikipedia.org/static/favicon/wikipedia.ico",
                    siteName = "Wikipedia"
                )
            } catch (e: Exception) {
                // fall through
            }
        }

        // 3. GitHub specialized adapter
        val ghMatch = Regex("""^https?://github\.com/([a-zA-Z0-9_.-]+)(?:/([a-zA-Z0-9_.-]+))?""", RegexOption.IGNORE_CASE)
            .find(normalizedUrl)
        if (ghMatch != null) {
            val owner = ghMatch.groupValues[1]
            val repo = ghMatch.groupValues.getOrNull(2)
            if (!repo.isNullOrBlank()) {
                return@withContext WebsiteMetadata(
                    url = normalizedUrl,
                    domain = domain,
                    title = "$owner/$repo",
                    description = "GitHub repository",
                    imageUrl = "https://opengraph.githubassets.com/1/$owner/$repo",
                    screenshotUrl = screenshotUrl,
                    faviconUrl = "https://github.githubassets.com/favicons/favicon.svg",
                    siteName = "GitHub"
                )
            }
        }

        // 4. General HTML page scraper
        try {
            val response = client.get(normalizedUrl) {
                header("User-Agent", "Mozilla/5.0 (Linux; Android 15; Pixel 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36")
                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            }
            val html = response.bodyAsText()

            // OpenGraph / Twitter title
            val ogTitle = extractMeta(html, "property", "og:title")
                ?: extractMeta(html, "name", "twitter:title")
                ?: extractTag(html, "title")
                ?: domain

            // OpenGraph / Twitter description
            val ogDesc = extractMeta(html, "property", "og:description")
                ?: extractMeta(html, "name", "twitter:description")
                ?: extractMeta(html, "name", "description")
                ?: ""

            // OpenGraph / Twitter image
            val ogImage = extractMeta(html, "property", "og:image:secure_url")
                ?: extractMeta(html, "property", "og:image")
                ?: extractMeta(html, "name", "twitter:image")
                ?: extractMeta(html, "name", "thumbnail")
                ?: screenshotUrl

            val siteName = extractMeta(html, "property", "og:site_name")
                ?: domain

            WebsiteMetadata(
                url = normalizedUrl,
                domain = domain,
                title = cleanEntities(ogTitle).trim(),
                description = cleanEntities(ogDesc).trim(),
                imageUrl = ogImage,
                screenshotUrl = screenshotUrl,
                faviconUrl = faviconUrl,
                siteName = siteName
            )
        } catch (e: Exception) {
            WebsiteMetadata(
                url = normalizedUrl,
                domain = domain,
                title = domain,
                description = "",
                imageUrl = screenshotUrl,
                screenshotUrl = screenshotUrl,
                faviconUrl = faviconUrl,
                siteName = domain
            )
        }
    }

    private fun extractMeta(html: String, attrName: String, attrValue: String): String? {
        val regex = Regex("""<meta\b[^>]*$attrName=["']$attrValue["'][^>]*content=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
        val match = regex.find(html)
        if (match != null) return match.groupValues[1]

        val regexReverse = Regex("""<meta\b[^>]*content=["']([^"']+)["'][^>]*$attrName=["']$attrValue["']""", RegexOption.IGNORE_CASE)
        return regexReverse.find(html)?.groupValues?.get(1)
    }

    private fun extractTag(html: String, tag: String): String? {
        val regex = Regex("""<$tag[^>]*>([\s\S]*?)</$tag>""", RegexOption.IGNORE_CASE)
        return regex.find(html)?.groupValues?.get(1)
    }

    private fun cleanEntities(str: String): String {
        return str.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace(Regex("""\s+"""), " ")
    }

    private fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    private fun extractDomain(url: String): String {
        return try {
            val uri = URI(url)
            uri.host?.removePrefix("www.") ?: url
        } catch (e: Exception) {
            url
        }
    }
}
