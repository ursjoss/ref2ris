package ch.difty.ref2ris

import ch.difty.kris.domain.RisType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object RwReferenceSpec : FunSpec({

    test("case 1") {
        val ref = "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S. (2024, Dec 24). " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
            "Adults in China: A Prospective Cohort Study. J Gerontol A Biol Sci Med Sci. " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged Adults " +
            "in China: A Prospective Cohort Study"

        val risRecord = RawReference.fromTextLine(ref).toRisRecord()

        risRecord.type shouldBe RisType.JOUR
        risRecord.userDefinable1 shouldBe ref
    }
})
