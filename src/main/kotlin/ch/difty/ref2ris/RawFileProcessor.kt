package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.toRisLines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toSet
import java.io.BufferedWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolutePathString

/**
 * [RawFileProcessor] interface configuring processing pipelines for the different file imports.
 */
internal interface RawFileProcessor {
    suspend fun Flow<Path>.processAllLines(intputPath: Path, outputPath: Path): Flow<Int>
}

internal enum class RawFiles : RawFileProcessor {
    References {
        @Suppress("MagicNumber")
        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        override suspend fun Flow<Path>.processAllLines(inputPath: Path, outputPath: Path): Flow<Int> =
            toTextLines()
                .toRawReference()
                .toRisRecord { it.toRisRecord() }
                .toRisLines()
                .toSet().toList().sorted().asFlow()
                .writeCleanFileLineTo(outputPath)

    },
}

@Suppress("unused")
@ExperimentalCoroutinesApi
internal fun Flow<String>.printToConsole(): Flow<Int> = flow {
    var lineCount = 0
    println()
    collect { risLine ->
        print(risLine.toString())
        lineCount++
    }
    emit(lineCount)
    println()
}

/**
 * Processes the lines according to the [RawFileProcessor] as receiver.
 */
@ExperimentalCoroutinesApi
internal suspend fun RawFileProcessor.processAllLines(inputPath: Path, outputPath: Path): ProcessorResult {
    fun resolveFiles(path: Path): Flow<Path> =
        if (path.toFile().isDirectory)
            path.toFile().walk().filter { it.isFile }.map { it.toPath() }.asFlow()
        else path
            .filterNot {
                it.toFile().isDirectory
            }.asFlow()
    return resolveFiles(inputPath)
        .processAllLines(inputPath, outputPath)
        .processResult()
}

internal fun Flow<RawReference>.toRisRecord(f: (RawReference) -> RisRecord): Flow<RisRecord> = flow {
    collect { rawReference -> emit(f(rawReference)) }
}

@ExperimentalCoroutinesApi
private fun Flow<String>.writeCleanFileLineTo(outputPath: Path): Flow<Int> =
    flow {
        var lines = 0
        openCleanWriter(outputPath.absolutePathString()).use { writer ->
            collect { line ->
                writer.append(line)
                lines++
            }
        }
        emit(lines)
    }.flowOn(Dispatchers.IO)

private fun openCleanWriter(targetFileName: String): BufferedWriter {
    fun Path.openBufferedWriter(charset: Charset = Charsets.UTF_8): BufferedWriter =
        Files.newBufferedWriter(this, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    val file = Path.of(targetFileName)
    return file.openBufferedWriter()
}
