package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.domain.RisType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** String tokens of the reference line */
internal data class TokenizedReference(
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
        private val dateRegex = Regex("""(\d{4})(?:, ([^)]+))?\)""")

        /** Accepts the reference as full string, returning a class with tokenized reference strings */
        fun fromTextLine(textLine: TextLine): TokenizedReference {
            val authors = textLine.authors()
            val (year, dateOrNull) = textLine.tokenizedYearAndDate(authors)
            val (title, doiOrNull, journalWithVolumeAndArticleNumbers) =
                textLine.titleAndJournalStringAndOptionalDoi()
            val (journal, volumeNumberOrNull, articleNumberOrNull) =
                tokenizeJournalFrom(journalWithVolumeAndArticleNumbers)
            return TokenizedReference(
                textLine.line,
                authors,
                year,
                dateOrNull,
                title,
                journal,
                volumeNumberOrNull,
                articleNumberOrNull,
                doiOrNull,
            )
        }

        private fun TextLine.authors(): String = line.substringBefore(" (")

        private fun TextLine.tokenizedYearAndDate(authors: String): Pair<String, String?> {
            val startingWithYear = line.drop(authors.length + 2)
            val yearAndOptionallyDate = dateRegex.find(startingWithYear)
            val year = yearAndOptionallyDate?.groupValues?.get(1) ?: error("Unable to get year from $line")
            val date = yearAndOptionallyDate.groupValues[2].takeIf { it.isNotBlank() }
            return Pair(year, date)
        }

        private fun TextLine.titleAndJournalStringAndOptionalDoi(): Triple<String, String?, String> {
            val afterYearAndDate = line.split("). ").drop(1).joinToString("). ")
            val (title, journalWithVolumeNumberAndArticleNumber) = afterYearAndDate.split(". ")
            val indexOfJournal = afterYearAndDate.indexOf(journalWithVolumeNumberAndArticleNumber)
            val afterJournal = afterYearAndDate
                .drop(indexOfJournal + journalWithVolumeNumberAndArticleNumber.length + 2)
            val doiOrNull = afterJournal.takeIf { "doi" in it }
            return Triple(title, doiOrNull, journalWithVolumeNumberAndArticleNumber)
        }

        private fun tokenizeJournalFrom(journalWithVolumeAndArticleNumbers: String): Triple<String, String?, String?> {
            val journalParts = journalWithVolumeAndArticleNumbers.split(", ")
            val journal = journalParts.first()
            val volumeNumber = journalParts.getOrNull(1)
            val articleNumber = journalParts.getOrNull(2)
            return Triple(journal, volumeNumber, articleNumber)
        }
    }
}

internal fun Flow<TextLine>.tokenized(): Flow<TokenizedReference> = flow {
    collect { textLine ->
        emit(TokenizedReference.fromTextLine(textLine))
    }
}

internal fun TokenizedReference.asRisRecord(): RisRecord {
    val rr = this
    val allAuthors = rr.authors.dropLast(1).replace(" & ", " ").split("., ").map { "$it." }
    return RisRecord().apply {
        type = RisType.JOUR
        userDefinable1 = rr.textLine
        authors.addAll(allAuthors)
        publicationYear = rr.publicationYear
        date = rr.date
        title = rr.title
        periodicalNameFullFormatJO = rr.journal
        volumeNumber = rr.volumeNumber
        reviewedItem = rr.articleNumber
        doi = rr.doi
    }
}
