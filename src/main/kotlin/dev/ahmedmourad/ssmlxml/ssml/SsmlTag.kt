package dev.ahmedmourad.ssmlxml.ssml

data class SsmlParseResult(
    val element: SsmlElement,
    val errors: List<SsmlParseError>
)

data class SsmlParseError(
    val attribute: String?,
    val description: String
)

data class SsmlTag(
    val name: String,
    val attributes: List<SsmlLexeme.Attribute>,
    val elements: List<SsmlElement>
)

class SsmlTagContext(private val tag: SsmlTag) {
    val errors = mutableListOf<SsmlParseError>()
    val name: String get() = tag.name
    val elements: List<SsmlElement> get() = tag.elements
    fun <T> attribute(name: String, parse: (String) -> T): T? {
        return tag.attributes.firstOrNull { it.name == name }?.value?.let(parse)
    }
    fun <T> error(attribute: String? = null, description: String, fallback: () -> T): T {
        errors.add(
            SsmlParseError(
            attribute = attribute,
            description = description
        )
        )
        return fallback()
    }
}

fun <T : SsmlElement> fromTag(tag: SsmlTag, parse: SsmlTagContext.() -> T): SsmlParseResult {
    val context = SsmlTagContext(tag)
    return SsmlParseResult(
        element = parse(context),
        errors = context.errors
    )
}
