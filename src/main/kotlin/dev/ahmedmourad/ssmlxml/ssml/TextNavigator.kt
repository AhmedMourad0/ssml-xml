package dev.ahmedmourad.ssmlxml.ssml

class TextNavigator(private val source: String) {

    private var index = 0

    fun current() = source[index]

    fun advanceTo(vararg expected: Char): String = buildString {
        while (!isAtEnd()) {
            val current = source[index]
            if (expected.any { it == current }) {
                break
            } else {
                append(current)
                ++index
            }
        }
    }

    fun advancePast(vararg expected: Char): String {
        return advanceTo(*expected).also { advance() }
    }

    fun advance() {
        ++index
    }

    fun peek(skipWhitespace: Boolean, includeCurrent: Boolean = true): Char? {
        var peekIndex = if (includeCurrent) index else index + 1
        if (isAtEnd()) return null
        if (!skipWhitespace) return source[peekIndex]
        while (peekIndex <= source.lastIndex) {
            if (source[peekIndex] == ' ') {
                ++peekIndex
            } else {
                return source[peekIndex]
            }
        }
        return null
    }

    fun isAtEnd(): Boolean {
        return index >= source.lastIndex
    }
}
