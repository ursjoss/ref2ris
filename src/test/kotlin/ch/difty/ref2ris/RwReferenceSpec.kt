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
        val ref = "Renzetti, S., Volta, M., van Thriel, C., Lucchini, R. G., Smith, D. R., Patrono, " +
            "A., Cagna, G., Invernizzi, A., Rechtman, E., Ongaro, E., De Angelis, E., Calza, S., Rota, M., " +
            "Wright, R. O., Claus Henn, B., Horton, M. K., & Placidi, D. (2025, 2025/02/01/). " +
            "Association between environmental air pollution and olfactory functioning among Italian adolescents " +
            "and young adults in the province of Brescia, Italy. Atmospheric Pollution Research, 16(2), 102391. " +
            "https://doi.org/https://doi.org/10.1016/j.apr.2024.102391"
        RawReference.fromTextLine(ref).toRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Renzetti, S.", "Volta, M.", "van Thriel, C.", "Lucchini, R. G.",
                "Smith, D. R.", "Patrono, A.", "Cagna, G.", "Invernizzi, A.", "Rechtman, E.", "Ongaro, E.",
                "De Angelis, E.", "Calza, S.", "Rota, M.", "Wright, R. O.", "Claus Henn, B.", "Horton, M. K.",
                "Placidi, D.")
            firstAuthors shouldContainAll listOf("Renzetti, S.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "2025/02/01/"
            title shouldBeEqualTo "Association between environmental air pollution and olfactory functioning " +
                "among Italian adolescents and young adults in the province of Brescia, Italy"
            periodicalNameFullFormatJO shouldBeEqualTo "Atmospheric Pollution Research"
            volumeNumber shouldBeEqualTo "16(2)"
            reviewedItem shouldBeEqualTo "102391"
            doi shouldBeEqualTo "https://doi.org/https://doi.org/10.1016/j.apr.2024.102391"
        }
    }
})
