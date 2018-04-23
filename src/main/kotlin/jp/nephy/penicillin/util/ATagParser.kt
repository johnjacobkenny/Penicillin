package jp.nephy.penicillin.util

class ATagParser(html: String) {
    private val tagPattern = "^<a (.+?)>(.+?)</a>$".toRegex()
    private val attributePattern = "^(.+?)=\"(.+?)\"$".toRegex()
    private val matches = tagPattern.matchEntire(html)

    val attributes: Map<String, String>
        get() = if (matches != null && matches.groupValues.size == 3) {
            matches.groupValues[1].split(" ").map {
                val (key, value) = attributePattern.matchEntire(it) !!.destructured
                key to value
            }.toMap()
        } else {
            emptyMap()
        }

    val value: String
        get() = if (matches != null && matches.groupValues.size == 3) {
            matches.groupValues[2]
        } else {
            ""
        }
}
