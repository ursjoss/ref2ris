package ch.difty.ref2ris

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

private val log = KotlinLogging.logger {}

/** data class for a single relevant line of text from the parsed file */
data class TextLine(val line: String, val fileName: String = "")

@ExperimentalCoroutinesApi
@FlowPreview
internal fun Flow<Path>.readTextLines(): Flow<TextLine> =
    flatMapConcat { filePath ->
        log.info { "Processing file ${filePath.fileName}..." }
        filePath.lineFlow(Charset.defaultCharset())
    }

@ExperimentalCoroutinesApi
private fun Path.lineFlow(charset: Charset = Charsets.UTF_8): Flow<TextLine> =
    lineFlow(fileName.toString()) { openBufferedReader(charset) }


private fun Path.openBufferedReader(charset: Charset = Charsets.UTF_8): BufferedReader =
    Files.newBufferedReader(this, charset)

@ExperimentalCoroutinesApi
private fun lineFlow(fileName: String, openReader: () -> Reader): Flow<TextLine> {
    val lines = flow {
        openReader().useLines { lines ->
            lines.forEach { emit(TextLine(it, fileName)) }
        }
    }

    @Suppress("MagicNumber")
    return lines.flowOn(Dispatchers.IO).buffer(10_000)
}
