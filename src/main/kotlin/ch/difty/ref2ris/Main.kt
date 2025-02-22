@file:Suppress("MatchingDeclarationName")

package ch.difty.ref2ris

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

@ExperimentalCoroutinesApi
class Process : CliktCommand("process references") {
    private val path: String
        by option("--path", help = "The file or folder containing the files to process").default("data")

    private val debug: Boolean
        by option("-d", help = "Print parsed lines").flag("--debug", default = false)

    override fun help(context: Context) = "Process the references in the files in the specified folder"

    override fun run() = runBlocking(Dispatchers.IO) {
        Settings.debug = debug
        Settings.folder = path
        @Suppress("SpreadOperator")
        doProcess(*RawFiles.entries.toTypedArray())
    }

    private suspend fun doProcess(vararg processors: RawFileProcessor) {
        val dataFolder = Paths.get(path)
        processors.asIterable().forEach {
            it.processAllLines(dataFolder).also {
                log.info { it }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun main(args: Array<String>) = Process().main(args)

object Settings {
    var debug: Boolean = false
    var folder: String = "data"
}
