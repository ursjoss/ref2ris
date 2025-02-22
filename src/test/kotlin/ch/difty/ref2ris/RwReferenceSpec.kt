package ch.difty.ref2ris

import ch.difty.kris.domain.RisType
import io.kotest.core.spec.style.FunSpec
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo

object RwReferenceSpec : FunSpec({

    test("case 1") {
        val ref = "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S. (2024, Dec 24). " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
            "Adults in China: A Prospective Cohort Study. J Gerontol A Biol Sci Med Sci. " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged Adults " +
            "in China: A Prospective Cohort Study"

        val rawRef = RawReference.fromTextLine(ref).apply {
            textLine shouldBeEqualTo ref
            authors shouldBeEqualTo "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S."
            publicationYear shouldBeEqualTo "2024"
            date shouldBeEqualTo "Dec 24"
        }

        rawRef.toRisRecord().run {
            type shouldBe RisType.JOUR
            userDefinable1 shouldBeEqualTo ref
            authors shouldContainAll listOf("Zhu, A.", "Kan, H.", "Shi, X.", "Zeng, Y.", "Ji, J. S.")
            firstAuthors shouldContainAll listOf("Zhu, A.")
            publicationYear shouldBeEqualTo "2024"
            date shouldBeEqualTo "Dec 24"
        }
    }
})
