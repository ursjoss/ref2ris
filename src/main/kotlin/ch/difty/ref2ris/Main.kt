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
    private val input: String
        by option("--input", help = "The file or folder containing the files to process").default("data")

    private val output: String
        by option("--output", help = "The output file with the resulting RIS records").default("output.ris")

    private val debug: Boolean
        by option("-d", help = "Print parsed lines").flag("--debug", default = false)

    override fun help(context: Context) = "Process the references in the files in the specified folder"

    override fun run() = runBlocking(Dispatchers.IO) {
        Settings.debug = debug
        Settings.input = input
        Settings.output = output
        @Suppress("SpreadOperator")
        doProcess(*RawFiles.entries.toTypedArray())
    }

    private suspend fun doProcess(vararg processors: RawFileProcessor) {
        val inputPath = Paths.get(input)
        val outputPath = Paths.get(output)
        processors.asIterable().forEach {
            it.processAllLines(inputPath, outputPath).also {
                log.info { it }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun main(args: Array<String>) = Process().main(args)

object Settings {
    var debug: Boolean = false
    var input: String = "data"
    var output: String = "output.ris"
}
