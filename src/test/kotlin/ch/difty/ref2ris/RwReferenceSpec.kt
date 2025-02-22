package ch.difty.ref2ris

import ch.difty.kris.domain.RisType
import io.kotest.core.spec.style.FunSpec
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll

object RwReferenceSpec : FunSpec({

    test("1. simple case") {
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


    test("2. with DOI") {
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

    test("3. with volume with parenthesis") {
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

    test("4. with no date") {
        val ref = "Zhang, X., Xi, Z., Yang, M., Zhang, X., Wu, R., Li, S., Pan, L., Fang, Y., Lv, P., Ma, Y., Duan, " +
            "H., Wang, B., & Lv, K. (2025). Short-term effects of combined environmental factors on respiratory " +
            "disease mortality in Qingdao city: A time-series investigation. PLoS One, 20(1), e0318250. " +
            "https://doi.org/10.1371/journal.pone.0318250"
        RawReference.fromTextLine(ref).toRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Zhang, X.", "Xi, Z.", "Yang, M.", "Zhang, X.", "Wu, R.", "Li, S.",
                "Pan, L.", "Fang, Y.", "Lv, P.", "Ma, Y.", "Duan, H.", "Wang, B.", "Lv, K.")
            firstAuthors shouldContainAll listOf("Zhang, X.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo ""
            title shouldBeEqualTo "Short-term effects of combined environmental factors on respiratory " +
                "disease mortality in Qingdao city: A time-series investigation"
            periodicalNameFullFormatJO shouldBeEqualTo "PLoS One"
            volumeNumber shouldBeEqualTo "20(1)"
            reviewedItem shouldBeEqualTo "e0318250"
            doi shouldBeEqualTo "https://doi.org/10.1371/journal.pone.0318250"
        }
    }
})
