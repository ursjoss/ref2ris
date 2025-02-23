package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.toRisLines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolutePathString

@ExperimentalCoroutinesApi
internal suspend fun processAllLines(inputPath: Path, outputPath: Path): ProcessorResult =
    resolveFiles(inputPath)
        .processInto(outputPath)
        .processResult()

/** Flow of a single file path or paths of all files in a specified directory */
private fun resolveFiles(path: Path): Flow<Path> =
    if (path.toFile().isDirectory)
        path.toFile().listFiles()
            .filter { it.isFile }
            .map { it.toPath() }
            .asFlow()
    else path.toFile().takeIf { it.isFile }?.let { flowOf(path) } ?: error("Unable to process file $path")

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
private fun Flow<Path>.processInto(outputPath: Path): Flow<Int> =
    readTextLines()
        .tokenized()
        .toRisRecord()
        .unique()
        .toRisLines()
        .writeTo(outputPath)

private fun Flow<TokenizedReference>.toRisRecord(): Flow<RisRecord> = flow {
    collect { tr -> emit(tr.asRisRecord()) }
}

private fun Flow<RisRecord>.unique(): Flow<RisRecord> = flow {
    val set = mutableSetOf<RisRecord>()
    collect { risRecord ->
        if (set.add(risRecord)) emit(risRecord)
    }
}

@ExperimentalCoroutinesApi
private fun Flow<String>.writeTo(outputPath: Path): Flow<Int> =
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
