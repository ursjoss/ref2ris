package ch.difty.ref2ris

import ch.difty.kris.domain.RisRecord
import ch.difty.kris.domain.RisType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** String tokens of the reference line */
internal data class TokenizedReference(
    val textLine: String,
    val authors: String,
    val publicationYear: String,
    val date: String?,
    val title: String,
    val journal: String,
    val volumeNumber: String?,
    val issue: String?,
    val firstPage: String?,
    val lastPage: String?,
    val doi: String?,
) {

    companion object {
        private val yearDateRegex = Regex("""(\d{4})(?:, ([^)]+))?\)""")
        private val dateRegex = Regex("""(\d{4})/(\d{2})/(\d{2})/""")

        /** Accepts the reference as full string, returning a class with tokenized reference strings */
        fun fromTextLine(textLine: TextLine): TokenizedReference {
            val authors = textLine.authors()
            val (year, dateOrNull) = textLine.tokenizedYearAndDate(authors)
            val (title, doiOrNull, journalWithVolumeAndPages) =
                textLine.titleAndJournalStringAndOptionalDoi()
            val jt = tokenizeJournalFrom(journalWithVolumeAndPages)
            return TokenizedReference(
                textLine.line,
                authors,
                year,
                dateOrNull,
                title,
                jt.journal,
                jt.volume,
                jt.issue,
                jt.firstPage,
                jt.lastPage,
                doiOrNull,
            )
        }

        private fun TextLine.authors(): String = line.substringBefore(" (")

        private fun TextLine.tokenizedYearAndDate(authors: String): Pair<String, String?> {
            val startingWithYear = line.drop(authors.length + 2)
            val yearAndOptionallyDate = yearDateRegex.find(startingWithYear)
            val year = yearAndOptionallyDate?.groupValues?.get(1) ?: error("Unable to get year from $line")
            val date = yearAndOptionallyDate.groupValues[2].takeIf { it.isNotBlank() }?.let {
                val dateMatch = dateRegex.find(it)
                if (dateMatch != null) {
                    val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
                    var i = 0
                    val ld = LocalDate.of(
                        dateMatch.groupValues[++i].toInt(),
                        dateMatch.groupValues[++i].toInt(),
                        dateMatch.groupValues[++i].toInt()
                    )
                    ld.format(formatter)
                }
                else it
            }
            return Pair(year, date)
        }

        private fun TextLine.titleAndJournalStringAndOptionalDoi(): Triple<String, String?, String> {
            val afterYearAndDate = line.split("). ").drop(1).joinToString("). ")
            val (title, journalWithVolumeNumberAndArticleNumber) = afterYearAndDate.split(". ")
            val indexOfJournal = afterYearAndDate.indexOf(journalWithVolumeNumberAndArticleNumber)
            val afterJournal = afterYearAndDate
                .drop(indexOfJournal + journalWithVolumeNumberAndArticleNumber.length + 2)
            val doiOrNull = afterJournal.takeIf { "doi" in it }
                ?.substringAfter("https://doi.org/")
                ?.substringAfter("https://doi.org/")
            return Triple(title, doiOrNull, journalWithVolumeNumberAndArticleNumber)
        }

        private fun tokenizeJournalFrom(journalWithVolumeAndPages: String): JournalToken {
            val journalParts = journalWithVolumeAndPages.split(", ")
            val journal = journalParts.first()
            val volume = journalParts.getOrNull(1)?.substringBefore('(')
            val issue = journalParts.getOrNull(1)?.substringAfter("(")?.substringBefore(")")
            val pages = journalParts.getOrNull(2)
            return JournalToken(
                journal,
                volume,
                issue,
                pages?.substringBefore('-'),
                pages?.takeIf{ it.contains('-')}?.substringAfter('-'),
            )
        }

        private data class JournalToken(
            val journal: String,
            val volume: String?,
            val issue: String?,
            val firstPage: String?,
            val lastPage: String?,
        )
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
        issue = rr.issue
        startPage = rr.firstPage
        endPage = rr.lastPage
        doi = rr.doi
    }
}
