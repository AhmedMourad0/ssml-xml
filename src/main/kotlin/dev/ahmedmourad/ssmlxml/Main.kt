package dev.ahmedmourad.ssmlxml

import dev.ahmedmourad.ssmlxml.ssml.lexemesFrom
import dev.ahmedmourad.ssmlxml.ssml.parse

fun main(args: Array<String>) {
    val ssml = """
        <speak>
          Here are <say-as interpret-as="characters">SSML</say-as> samples.
          I can pause <break time="3s"/>.
          I can play a sound
          <audio src="https://www.example.com/MY_MP3_FILE.mp3">didn't get your MP3 audio file</audio>.
          I can speak in cardinals. Your number is <say-as interpret-as="cardinal">10</say-as>.
          Or I can speak in ordinals. You are <say-as interpret-as="ordinal">10</say-as> in line.
          Or I can even speak in digits. The digits for ten are <say-as interpret-as="characters">10</say-as>.
          I can also substitute phrases, like the <sub alias="World Wide Web Consortium">W3C</sub>.
          Finally, I can speak a paragraph with two sentences.
          <p><s>This is sentence one.</s><s>This is sentence two.</s></p>
        </speak>
    """.trimIndent()

    val ssml1 = """
        <speak>
            Hello, <break time="500ms"/> this is a complex SSML example.
            <p>
                <s>
                    Here is some text with reserved characters: &lt;, &gt;, &amp;, &quot;.
                </s>
                <s>
                    Let's add a break with strength "strong" <break strength="strong"/>.
                </s>
                <s>
                    And another sentence with a longer break duration <break time="2s"/>.
                </s>
            </p>
            <break time="500ms"/>
            Thank you for listening!
        </speak>
    """.trimIndent()
    lexemesFrom(ssml1)//.forEach { println(it.toString()) }
    println(parse(ssml))
}
