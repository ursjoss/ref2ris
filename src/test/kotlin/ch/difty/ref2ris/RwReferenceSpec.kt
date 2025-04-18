package ch.difty.ref2ris

import ch.difty.kris.domain.RisType
import io.kotest.core.spec.style.FunSpec
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainAll

object RwReferenceSpec : FunSpec({

    test("1. simple case") {
        val ref = "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S. (2024, Dec 24). " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
            "Adults in China: A Prospective Cohort Study. J Gerontol A Biol Sci Med Sci. " +
            "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged Adults " +
            "in China: A Prospective Cohort Study"

        val rawRef = TokenizedReference.fromTextLine(TextLine(ref)).apply {
            textLine shouldBeEqualTo ref
            authors shouldBeEqualTo "Zhu, A., Kan, H., Shi, X., Zeng, Y., & Ji, J. S."
            publicationYear shouldBeEqualTo "2024"
            date shouldBeEqualTo "Dec 24"
            title shouldBeEqualTo "Black Carbon Air Pollution and Incident Mortality among the Advance-Aged " +
                "Adults in China: A Prospective Cohort Study"
            journal shouldBeEqualTo "J Gerontol A Biol Sci Med Sci"
        }

        rawRef.asRisRecord().run {
            type shouldBe RisType.JOUR
            userDefinable1 shouldBeEqualTo ref
            authors shouldContainAll listOf("Zhu, A.", "Kan, H.", "Shi, X.", "Zeng, Y.", "Ji, J. S.")
            firstAuthors.shouldBeEmpty()
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

        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldContainAll listOf("Ahn, S.", "Yun, H.", "Oh, J.", "Kim, S.", "Jang, H.",
                "Kim, Y.", "Kang, C.", "Ahn, S.", "Kim, A.", "Kwon, D.", "Park, J.", "Song, I.",
                "Moon, J.", "Kim, E.", "Min, J.", "Kim, H.", "Lee, W.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "Mar 5"
            title shouldBeEqualTo "Short-term exposure to warm-season ozone, cardiovascular mortality, " +
                "and novel high-risk populations: A nationwide time-stratified case-crossover study"
            periodicalNameFullFormatJO shouldBeEqualTo "Atmospheric Environment"
            volumeNumber shouldBeEqualTo "345"
            startPage shouldBeEqualTo "121031"
            endPage.shouldBeNull()
            doi shouldBeEqualTo "10.1016/j.atmosenv.2025.121031"
        }
    }

    test("3. with volume with parenthesis") {
        val ref = "Renzetti, S., Volta, M., van Thriel, C., Lucchini, R. G., Smith, D. R., Patrono, " +
            "A., Cagna, G., Invernizzi, A., Rechtman, E., Ongaro, E., De Angelis, E., Calza, S., Rota, M., " +
            "Wright, R. O., Claus Henn, B., Horton, M. K., & Placidi, D. (2025, 2025/02/01/). " +
            "Association between environmental air pollution and olfactory functioning among Italian adolescents " +
            "and young adults in the province of Brescia, Italy. Atmospheric Pollution Research, 16(2), 102391. " +
            "https://doi.org/10.1016/j.apr.2024.102391"
        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Renzetti, S.", "Volta, M.", "van Thriel, C.", "Lucchini, R. G.",
                "Smith, D. R.", "Patrono, A.", "Cagna, G.", "Invernizzi, A.", "Rechtman, E.", "Ongaro, E.",
                "De Angelis, E.", "Calza, S.", "Rota, M.", "Wright, R. O.", "Claus Henn, B.", "Horton, M. K.",
                "Placidi, D.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "Feb 1"
            title shouldBeEqualTo "Association between environmental air pollution and olfactory functioning " +
                "among Italian adolescents and young adults in the province of Brescia, Italy"
            periodicalNameFullFormatJO shouldBeEqualTo "Atmospheric Pollution Research"
            volumeNumber shouldBeEqualTo "16"
            issue shouldBeEqualTo "2"
            startPage shouldBeEqualTo "102391"
            endPage.shouldBeNull()
            doi shouldBeEqualTo "10.1016/j.apr.2024.102391"
        }
    }

    test("4. with no date") {
        val ref = "Zhang, X., Xi, Z., Yang, M., Zhang, X., Wu, R., Li, S., Pan, L., Fang, Y., Lv, P., Ma, Y., Duan, " +
            "H., Wang, B., & Lv, K. (2025). Short-term effects of combined environmental factors on respiratory " +
            "disease mortality in Qingdao city: A time-series investigation. PLoS One, 20(1), e0318250. " +
            "https://doi.org/10.1371/journal.pone.0318250"
        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Zhang, X.", "Xi, Z.", "Yang, M.", "Zhang, X.", "Wu, R.", "Li, S.",
                "Pan, L.", "Fang, Y.", "Lv, P.", "Ma, Y.", "Duan, H.", "Wang, B.", "Lv, K.")
            publicationYear shouldBeEqualTo "2025"
            date.shouldBeNull()
            title shouldBeEqualTo "Short-term effects of combined environmental factors on respiratory " +
                "disease mortality in Qingdao city: A time-series investigation"
            periodicalNameFullFormatJO shouldBeEqualTo "PLoS One"
            volumeNumber shouldBeEqualTo "20"
            issue shouldBeEqualTo "1"
            startPage shouldBeEqualTo "e0318250"
            endPage.shouldBeNull()
            doi shouldBeEqualTo "10.1371/journal.pone.0318250"
        }
    }

    test("5. no volume and review item") {
        val ref = "Kurasz, A., Lip, G. Y. H., Święczkowski, M., Tomaszuk-Kazberuk, A., Dobrzycki, S., & Kuźma, Ł. " +
            "(2025, Jan 15). Air quality and the risk of acute atrial fibrillation (EP-PARTICLES study): " +
            "A nationwide study in Poland. Eur J Prev Cardiol. https://doi.org/10.1093/eurjpc/zwaf016"
        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Kurasz, A.", "Lip, G. Y. H.", "Święczkowski, M.", "Tomaszuk-Kazberuk, A.",
                "Dobrzycki, S.", "Kuźma, Ł.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "Jan 15"
            title shouldBeEqualTo "Air quality and the risk of acute atrial fibrillation (EP-PARTICLES study): " +
                "A nationwide study in Poland"
            periodicalNameFullFormatJO shouldBeEqualTo "Eur J Prev Cardiol"
            volumeNumber.shouldBeNull()
            startPage.shouldBeNull()
            endPage.shouldBeNull()
            doi shouldBeEqualTo "10.1093/eurjpc/zwaf016"
        }
    }

    test("6. with start and end page") {
        val ref = "Aretz, B., Doblhammer, G., & Heneka, M. T. (2024, Dec). The role of leukocytes in cognitive " +
            "impairment due to long-term exposure to fine particulate matter: A large population-based mediation " +
            "analysis. Alzheimers Dement, 20(12), 8715-8727. https://doi.org/10.1002/alz.14320"
        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Aretz, B.", "Doblhammer, G.", "Heneka, M. T.")
            publicationYear shouldBeEqualTo "2024"
            date shouldBeEqualTo "Dec"
            title shouldBeEqualTo "The role of leukocytes in cognitive impairment due to long-term exposure " +
                "to fine particulate matter: A large population-based mediation analysis"
            periodicalNameFullFormatJO shouldBeEqualTo "Alzheimers Dement"
            volumeNumber shouldBeEqualTo "20"
            issue shouldBeEqualTo "12"
            startPage shouldBeEqualTo "8715"
            endPage shouldBeEqualTo "8727"
            doi shouldBeEqualTo "10.1002/alz.14320"
        }
    }

    test("7. with no volume but pages") {
        val ref = "Chauhan, R., Dande, S., Hood, D. B., Chirwa, S. S., Langston, M. A., Grady, " +
            "S. K., Dojcsak, L., Tabatabai, M., Wilus, D., Valdez, R. B., Al-Hamdan, M. Z., Im, W., " +
            "McCallister, M., Alcendor, D. J., Mouton, C. P., & Ramesh, A. (2025, Jan 18). " +
            "Particulate matter 2.5 (PM(2.5)) - associated cognitive impairment and morbidity in humans " +
            "and animal models: a systematic review. J Toxicol Environ Health B Crit Rev, 1-31. " +
            "https://doi.org/10.1080/10937404.2025.2450354"
        TokenizedReference.fromTextLine(TextLine(ref)).asRisRecord().run {
            type shouldBe RisType.JOUR
            authors shouldBeEqualTo listOf("Chauhan, R.", "Dande, S.", "Hood, D. B.", "Chirwa, S. S.",
                "Langston, M. A.", "Grady, S. K.", "Dojcsak, L.", "Tabatabai, M.", "Wilus, D.", "Valdez, R. B.",
                "Al-Hamdan, M. Z.", "Im, W.", "McCallister, M.", "Alcendor, D. J.", "Mouton, C. P.", "Ramesh, A.")
            publicationYear shouldBeEqualTo "2025"
            date shouldBeEqualTo "Jan 18"
            title shouldBeEqualTo "Particulate matter 2.5 (PM(2.5)) - associated cognitive impairment and morbidity " +
                "in humans and animal models: a systematic review"
            periodicalNameFullFormatJO shouldBeEqualTo "J Toxicol Environ Health B Crit Rev"
            volumeNumber.shouldBeNull()
            issue.shouldBeNull()
            startPage shouldBeEqualTo "1"
            endPage shouldBeEqualTo "31"
            doi shouldBeEqualTo "10.1080/10937404.2025.2450354"
        }
    }
})
