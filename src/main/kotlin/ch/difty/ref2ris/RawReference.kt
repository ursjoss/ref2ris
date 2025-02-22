package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.domain.RisType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class RawReference(val textLine: String, val authors: String) {

    companion object {
        fun fromTextLine(textLine: String): RawReference {
            val authors = textLine.substringBefore(" (")
            return RawReference(textLine, authors)
        }
    }
}

internal fun Flow<TextLine>.toRawReference(): Flow<RawReference> = flow {
    collect { textLine ->
        emit(RawReference.fromTextLine(textLine.line))
    }
}

internal fun RawReference.toRisRecord(): RisRecord {
    val rr = this
    val allAuthors = rr.authors.dropLast(1).replace(" & ", " ").split("., ").map{ "$it."}
    return RisRecord().apply {
        type = RisType.JOUR
        userDefinable1 = rr.textLine
        authors.addAll(allAuthors)
        firstAuthors.add(allAuthors.first())
    }
}
