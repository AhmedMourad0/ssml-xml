package dev.ahmedmourad.ssmlxml.ssml

val ParserCandidates = listOf(
    ParserCandidate(null, Plain.Companion::from),
    ParserCandidate("speak", Speak.Companion::from),
    ParserCandidate("break", Break.Companion::from),
    ParserCandidate("p", Paragraph.Companion::from),
    ParserCandidate("s", Sentence.Companion::from)
)

data class ParserCandidate(
    val name: String?,
    val parser: (SsmlTag) -> SsmlParseResult
)

private data class ParserTag(
    val name: String,
    val elements: MutableList<SsmlElement> = mutableListOf(),
    val attributes: MutableList<SsmlLexeme.Attribute> = mutableListOf()
)

fun parse(
    ssml: String,
    candidates: List<ParserCandidate> = ParserCandidates
): List<SsmlElement> {
    val errors = mutableListOf<SsmlParseError>()
    val tagsStack = mutableListOf<ParserTag>()
    val roots = mutableListOf<SsmlElement>()
    lexemesFrom(ssml).forEach { lexeme ->
        when (lexeme) {
            is SsmlLexeme.StartTag -> tagsStack.add(ParserTag(lexeme.name))
            is SsmlLexeme.Attribute -> tagsStack.lastOrNull()?.attributes?.add(lexeme)
            is SsmlLexeme.Text -> {
                val candidate = candidates.firstOrNull { it.name == null } ?: return@forEach
                val result = candidate.parser(
                    SsmlTag(
                    name = lexeme.value,
                    attributes = emptyList(),
                    elements = emptyList()
                )
                )
                tagsStack.lastOrNull()?.elements?.add(result.element)
                errors.addAll(result.errors)
            }
            SsmlLexeme.EndTag -> {
                val tag = tagsStack.removeLastOrNull() ?: return@forEach
                val candidate = candidates.firstOrNull { it.name == tag.name } ?: return@forEach
                val result = candidate.parser(
                    SsmlTag(
                    name = tag.name,
                    attributes = tag.attributes,
                    elements = tag.elements
                )
                )
                errors.addAll(result.errors)
                if (tagsStack.isEmpty()) {
                    roots.add(result.element)
                } else {
                    tagsStack.lastOrNull()?.elements?.add(result.element)
                }
            }
        }
    }
    return roots
}
