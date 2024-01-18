package dev.ahmedmourad.ssmlxml.ssml

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

sealed interface SsmlElement

data class Speak(val elements: List<SsmlElement>): SsmlElement {
    companion object {
        fun from(tag: SsmlTag): SsmlParseResult = fromTag(tag) {
            Speak(elements = elements)
        }
    }
}

data class Plain(val value: String) : SsmlElement {
    companion object {
        fun from(tag: SsmlTag): SsmlParseResult = fromTag(tag) {
            Plain(value = name)
        }
    }
}

data class Break(
    val time: Duration?,
    val strength: BreakStrength?
) : SsmlElement {
    companion object {
        fun from(tag: SsmlTag): SsmlParseResult = fromTag(tag) {
            Break(time = attribute("time") { value ->
                when {
                     value.endsWith("ms") -> value.dropLast(2).toLong().milliseconds
                     value.endsWith('s') -> value.dropLast(1).toLong().seconds
                     else -> error(attribute = "time", "Invalid time: $value") { null }
                 }
            }, strength = attribute("strength") { value ->
                BreakStrength.entries.firstOrNull {
                    it.raw == value
                } ?: error(attribute = "strength", "Invalid strength: $value") { null }
            })
        }
    }
}

data class Paragraph(val elements: List<SsmlElement>) : SsmlElement {
    companion object {
        fun from(tag: SsmlTag): SsmlParseResult = fromTag(tag) {
            Paragraph(elements = elements)
        }
    }
}

data class Sentence(val elements: List<SsmlElement>) : SsmlElement {
    companion object {
        fun from(tag: SsmlTag): SsmlParseResult = fromTag(tag) {
            Sentence(elements = elements)
        }
    }
}

enum class BreakStrength(val raw: String) {
    None("none"),
    XWeak("x-weak"),
    Weak("weak"),
    Medium("medium"),
    Strong("strong"),
    XStrong("x-strong")
}
