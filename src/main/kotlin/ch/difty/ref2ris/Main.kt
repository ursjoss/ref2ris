@file:Suppress("MatchingDeclarationName")

package ch.difty.ref2ris

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
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

    override fun help(context: Context) = "Process the references in the files in the specified folder"

    override fun run() = runBlocking(Dispatchers.IO) {
        doProcess()
    }

    private suspend fun doProcess() {
        processAllLines(Paths.get(input), Paths.get(output)).also {
            log.info { it }
        }
    }
}

@ExperimentalCoroutinesApi
fun main(args: Array<String>) = Process().main(args)
