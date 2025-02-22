package ch.difty.ref2ris

import ch.difty.kris.domain.RisType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.amshove.kluent.shouldContainAll

object RwReferenceSpec : FunSpec({

    test("case 1") {
        val ref = "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S. (2024, Dec 24). " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
            "Adults in China: A Prospective Cohort Study. J Gerontol A Biol Sci Med Sci. " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged Adults " +
            "in China: A Prospective Cohort Study"

        val rawRef = RawReference.fromTextLine(ref)
        rawRef.textLine shouldBe ref
        rawRef.authors shouldBe "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S."

        val risRecord = rawRef.toRisRecord()

        risRecord.type shouldBe RisType.JOUR
        risRecord.userDefinable1 shouldBe ref
        risRecord.authors shouldContainAll listOf( "Zhu, A.", "Kan, H.", "Shi, X.", "Zeng, Y.", "Ji, J. S.")
        risRecord.firstAuthors shouldContainAll listOf("Zhu, A.")
    }
})
