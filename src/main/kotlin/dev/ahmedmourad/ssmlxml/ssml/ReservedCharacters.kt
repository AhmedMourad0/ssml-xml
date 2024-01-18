package dev.ahmedmourad.ssmlxml.ssml

private val ReservedCharacters = mapOf(
    '\"' to "&quot;",
    '&' to "&amp;",
    '\'' to "&apos;",
    '<' to "&lt;",
    '>' to "&gt;"
)

fun encodeReservedCharacters(ssml: String): String {
    return ssml.flatMap { char ->
        val reservedReplacement = ReservedCharacters[char]
        when {
            reservedReplacement != null -> reservedReplacement.toList()
            char == '\n' -> emptyList()
            else -> listOf(char)
        }
    }.joinToString(separator = "")
}

fun decodeReservedCharacters(ssml: String): String {
    val replaceMap = Regex("&(quot|amp|apos|lt|gt);").findAll(ssml).map {
        it.range to it.value
    }.sortedByDescending { (range, _) -> range.first }
    return replaceMap.fold(ssml) { acc, pair ->
        acc.replaceRange(
            pair.first,
            ReservedCharacters.entries.first { it.value == pair.second }.key.toString()
        )
    }.trim().replace("\\s+".toRegex(), " ")
}
