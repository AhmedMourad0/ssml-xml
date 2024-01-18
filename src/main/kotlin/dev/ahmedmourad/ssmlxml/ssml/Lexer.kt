package dev.ahmedmourad.ssmlxml.ssml

sealed interface SsmlLexeme {
    data class StartTag(val name: String) : SsmlLexeme
    data class Attribute(val name: String, val value: String) : SsmlLexeme
    data class Text(val value: String) : SsmlLexeme
    data object EndTag : SsmlLexeme
}

fun lexemesFrom(source: String): Sequence<SsmlLexeme> {
    val navigator = TextNavigator(source.trim().replace("\n", ""))
    return sequence {
        while (!navigator.isAtEnd()) {
            if (navigator.current() == '<') {
                navigator.advance()
                if (navigator.peek(skipWhitespace = true) == '/') {
                    //</something>
                    navigator.advancePast('>')
                    yield(SsmlLexeme.EndTag)
                } else {
                    //<something...
                    yield(SsmlLexeme.StartTag(name = navigator.advanceTo(' ', '/', '>')))
                    if (navigator.peek(skipWhitespace = true) == '/') {
                        //<something />
                        navigator.advancePast('>')
                        yield(SsmlLexeme.EndTag)
                    } else if (navigator.peek(skipWhitespace = true) == '>') {
                        //<something >
                        navigator.advancePast('>')
                    } else {
                        //<something ..attributes..
                        yieldAll(attributes(navigator))
                        if (navigator.current() == '/') {
                            //<something ..attributes.. />
                            yield(SsmlLexeme.EndTag)
                        }
                        navigator.advancePast('>')
                    }
                }
            } else {
                //text or whitespace
                val text = navigator.advanceTo('<')
                if (text.isNotBlank()) {
                    //text
                    yield(SsmlLexeme.Text(decodeReservedCharacters(text)))
                }
            }
        }
    }
}

private fun attributes(navigator: TextNavigator): List<SsmlLexeme.Attribute> = buildList {
    var key: String? = null
    while (!navigator.isAtEnd()) {
        when (navigator.current()) {
            '\"' -> {
                navigator.advance()
                add(
                    SsmlLexeme.Attribute(
                        name = key ?: continue,
                        value = navigator.advancePast('\"')
                    )
                )
                key = null
            }
            ' ' -> navigator.advance()
            '/', '>' -> break
            else -> key = navigator.advancePast('=')
        }
    }
}
