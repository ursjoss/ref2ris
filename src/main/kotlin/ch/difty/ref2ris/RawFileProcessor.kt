package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import java.nio.file.Path

/**
 * [RawFileProcessor] interface configuring processing pipelines for the different file imports.
 */
internal interface RawFileProcessor {
    val outputPath: String
    suspend fun Flow<Path>.processAllLines(folder: Path): Flow<Int>
}

internal enum class RawFiles : RawFileProcessor {
    References {
        override val outputPath: String
            get() = "output.ris"

        @Suppress("MagicNumber")
        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        override suspend fun Flow<Path>.processAllLines(folder: Path): Flow<Int> =
            toTextLines()
                .toRawReference()
                .toRisRecord { it.toRisRecord() }
                .printToConsole()
    },
}

@ExperimentalCoroutinesApi
internal fun Flow<RisRecord>.printToConsole(): Flow<Int> = flow {
    var lineCount = 0
    println()
    collect { risLine ->
        println(risLine.toString())
        lineCount++
    }
    emit(lineCount)
    println()
}

/**
 * Processes the lines according to the [RawFileProcessor] as receiver.
 */
@ExperimentalCoroutinesApi
internal suspend fun RawFileProcessor.processAllLines(path: Path): ProcessorResult {
    fun resolveFiles(path: Path): Flow<Path> =
        if (path.toFile().isDirectory)
            path.toFile().walk().filter { it.isFile }.map { it.toPath() }.asFlow()
        else path
            .filterNot {
                it.toFile().isDirectory
            }.asFlow()
    return resolveFiles(path)
        .processAllLines(path)
        .processResult()
}

internal fun Flow<RawReference>.toRisRecord(f: (RawReference) -> RisRecord): Flow<RisRecord> = flow {
    collect { rawReference -> emit(f(rawReference)) }
}

//@ExperimentalCoroutinesApi
//private fun <T : CleanFileLine> Flow<T>.writeCleanFileLineToJson(path: Path, outputPath: String): Flow<Int> =
//    flow {
//        var lines = 0
////        openCleanWriter(monthRoot, outputPath).use { writer ->
////            collect { line ->
////                val json = Gson().toJson(line)
////                writer.appendLine(json)
////                lines++
////            }
////        }
//        emit(lines)
//    }.flowOn(Dispatchers.IO)

//private fun openCleanWriter(monthRoot: Path, targetFileName: String): BufferedWriter {
//    fun Path.openBufferedWriter(charset: Charset = Charsets.UTF_8): BufferedWriter =
//        Files.newBufferedWriter(this, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
//
//    val file = monthRoot.resolve(targetFileName)
//    Files.createDirectories(file.parent)
//    return file.openBufferedWriter()
//}
