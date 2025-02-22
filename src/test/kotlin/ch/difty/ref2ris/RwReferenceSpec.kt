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
            title shouldBeEqualTo "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
            "Adults in China: A Prospective Cohort Study"
            journal shouldBeEqualTo "J Gerontol A Biol Sci Med Sci"
        }

        rawRef.toRisRecord().run {
            type shouldBe RisType.JOUR
            userDefinable1 shouldBeEqualTo ref
            authors shouldContainAll listOf("Zhu, A.", "Kan, H.", "Shi, X.", "Zeng, Y.", "Ji, J. S.")
            firstAuthors shouldContainAll listOf("Zhu, A.")
            publicationYear shouldBeEqualTo "2024"
            date shouldBeEqualTo "Dec 24"
            title shouldBeEqualTo "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
                "Adults in China: A Prospective Cohort Study"
            periodicalNameFullFormatJO shouldBeEqualTo "J Gerontol A Biol Sci Med Sci"
        }
    }


    test("case 2") {
        val ref = "Ahn, S., Yun, H., Oh, J., Kim, S., Jang, H., Kim, Y., Kang, C., Ahn, S., Kim, A., " +
            "Kwon, D., Park, J., Song, I., Moon, J., Kim, E., Min, J., Kim, H., & Lee, W. (2025, 2025/03/05/). " +
            "Short-term exposure to warm-season ozone, cardiovascular mortality, and novel high-risk populations: " +
            "A nationwide time-stratified case-crossover study. Atmospheric Environment, 345, 121031. " +
            "https://doi.org/https://doi.org/10.1016/j.atmosenv.2025.121031"

        RawReference.fromTextLine(ref).toRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldContainAll listOf("Ahn, S.", "Yun, H.", "Oh, J.", "Kim, S.", "Jang, H.",
                "Kim, Y.", "Kang, C.", "Ahn, S.", "Kim, A.", "Kwon, D.", "Park, J.", "Song, I.",
                "Moon, J.", "Kim, E.", "Min, J.", "Kim, H.", "Lee, W.")
            firstAuthors shouldContainAll listOf("Ahn, S.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "2025/03/05/"
            title shouldBeEqualTo "Short-term exposure to warm-season ozone, cardiovascular mortality, " +
                "and novel high-risk populations: A nationwide time-stratified case-crossover study"
            periodicalNameFullFormatJO shouldBeEqualTo "Atmospheric Environment"
            volumeNumber shouldBeEqualTo "345"
            reviewedItem shouldBeEqualTo "121031"
            doi shouldBeEqualTo "https://doi.org/https://doi.org/10.1016/j.atmosenv.2025.121031"
        }
    }

    test("case 3") {
        val ref = "Kang, H., Oh, E., & Choi, Y.-H. (2025, 2025/02/15/). Exposure-crossover observations " +
            "of air pollution after large-scale fireworks in two Korean megacities, Seoul and Busan: Empirical " +
            "evidence toward sustainable festivals. Science of The Total Environment, 965, 178640." +
            " https://doi.org/https://doi.org/10.1016/j.scitotenv.2025.178640"
        RawReference.fromTextLine(ref).toRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldContainAll listOf("Kang, H.", "Oh, E.", "Choi, Y.-H.")
            firstAuthors shouldContainAll listOf("Kang, H.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "2025/02/15/"
            title shouldBeEqualTo "Exposure-crossover observations of air pollution after large-scale fireworks " +
                "in two Korean megacities, Seoul and Busan: Empirical evidence toward sustainable festivals"
            periodicalNameFullFormatJO shouldBeEqualTo "Science of The Total Environment"
            volumeNumber shouldBeEqualTo "965"
            reviewedItem shouldBeEqualTo "178640"
            doi shouldBeEqualTo "https://doi.org/https://doi.org/10.1016/j.scitotenv.2025.178640"
        }
    }
})
