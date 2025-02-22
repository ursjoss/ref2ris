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
    val journal: String,
    val volumeNumber: String?,
    val articleNumber: String?,
    val doi: String?,
) {

    companion object {
        private val dateRegex = Regex("""\(\d{4}, ([^)]+)\)""")
        fun fromTextLine(textLine: String): RawReference {
            val authors = textLine.substringBefore(" (")
            val year = textLine.drop(authors.length + 2).substringBefore(", ")
            val date = dateRegex.find(textLine)?.groupValues?.get(1)
            val afterYearAndDate = textLine.split("). ").drop(1).joinToString("). ")
            val partsAfterYearAndDate = afterYearAndDate.split(". ")
            val (title, journalWithVolumeNumberAndArticleNumber, _) = partsAfterYearAndDate
            val journalParts = journalWithVolumeNumberAndArticleNumber.split(", ")
            val journal = journalParts.first()
            val volumeNumber = journalParts.getOrNull(1)
            val articleNumber = journalParts.getOrNull(2)
            val indexOfJournal = afterYearAndDate.indexOf(journalWithVolumeNumberAndArticleNumber)
            val afterJournal = afterYearAndDate
                .drop(indexOfJournal + journalWithVolumeNumberAndArticleNumber.length + 2)
            val doi = afterJournal.takeIf { "doi" in it }
            return RawReference(textLine, authors, year, date, title, journal, volumeNumber, articleNumber, doi)
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
    val allAuthors = rr.authors.dropLast(1).replace(" & ", " ").split("., ").map { "$it." }
    return RisRecord().apply {
        type = RisType.JOUR
        userDefinable1 = rr.textLine
        authors.addAll(allAuthors)
        firstAuthors.add(allAuthors.first())
        publicationYear = rr.publicationYear
        date = rr.date
        title = rr.title
        periodicalNameFullFormatJO = rr.journal
        volumeNumber = rr.volumeNumber
        reviewedItem = rr.articleNumber
        doi = rr.doi
    }
}
