package ch.difty.ref2ris

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

internal data class ProcessorResult(val processed: Int) {
    override fun toString(): String = "Successfully processed $processed lines"
}

@ExperimentalCoroutinesApi
internal suspend fun Flow<Int>.processResult(): ProcessorResult =
    ProcessorResult(fold(0) { sum, value -> sum + value })
