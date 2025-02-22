package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.domain.RisType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class RawReference(
    val textLine: String,
    val authors: String,
    val publicationYear: String,
    val date: String?,
    val title: String,
) {

    companion object {
        private val dateRegex = Regex("""\(\d{4}, (.+)\)""")
        fun fromTextLine(textLine: String): RawReference {
            val authors = textLine.substringBefore(" (")
            val year = textLine.drop(authors.length + 2).substringBefore(", ")
            val date = dateRegex.find(textLine)?.groupValues?.get(1)
            val remainder = textLine.split("). ").drop(1).joinToString("). ")
            val parts = remainder.split(". ")
            val title = parts.first()
            return RawReference(textLine, authors, year, date, title)
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
        publicationYear = rr.publicationYear
        date = rr.date
        title = rr.title
    }
}
