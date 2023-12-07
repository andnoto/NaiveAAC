package com.sampietro.NaiveAAC.activities.Grammar

import android.content.Context
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAll
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.util.*

/**
 * <h1>GrammarHelper</h1>
 *
 *
 * **GrammarHelper** utility class for Italian grammar.
 *
 * @version     4.0, 09/09/2023
 */
object GrammarHelper {
    /**
     * splits a sentence into an array of words
     *
     * @param eText string containing the sentence to split
     */
    fun splitString(eText: String): Array<String> {
        return eText.split(" ".toRegex()).toTypedArray()
    }
    // data la keyword Arasaac , il genere e la categoria singolare/plurale
    // restituisce l'articolo
    // individuato approssimativamente nel seguente modo :
    // se singolare e la keyword inizia per vocale -> l'
    // se singolare maschile e la keyword inizia per consonante -> il
    // se singolare femminile e la keyword inizia per consonante -> la
    // se plurale maschile e la keyword inizia per vocale -> gli
    // se plurale maschile e la keyword inizia per consonante  -> i
    // se plurale femminile  -> le
    /**
     * given the keyword Arasaac, the gender and the singular / plural category returns the article
     * identified approximately as follows:
     * if singular and the keyword starts with a vowel -> l'
     * if masculine singular and the keyword starts with a consonant -> il
     * if feminine singular and the keyword starts with a consonant -> la
     * if masculine plural and the keyword starts with a vowel -> gli
     * if masculine plural and the keyword starts with a consonant -> i
     * if plural feminine -> le
     *
     * @param kKeyword string containing the keyword Arasaac
     * @param gender string containing the gender masculine (gender = M) or feminine (gender = F) of the keyword Arasaac
     * @param plural string containing the singular (plural = N) / plural (plural = S) category of the keyword Arasaac
     * @return string article
     */
    @Deprecated("replaced by {@link #searchArticle(String,String,String,String,Realm)}")
    fun searchArticle(kKeyword: String, gender: String, plural: String): String {
        var articleToSearch = ""
        val keywordFirstChar = kKeyword.substring(0, 1)
        if (plural == "N" &&
            (keywordFirstChar == "a" || keywordFirstChar == "e" ||
                    keywordFirstChar == "i" || keywordFirstChar == "o" ||
                    keywordFirstChar == "u")
        ) articleToSearch = "l'"
        if (plural == "N" && gender == "M" &&
            !(keywordFirstChar == "a" || keywordFirstChar == "e" ||
                    keywordFirstChar == "i" || keywordFirstChar == "o" ||
                    keywordFirstChar == "u")
        ) articleToSearch = "il "
        if (plural == "N" && gender == "F" &&
            !(keywordFirstChar == "a" || keywordFirstChar == "e" ||
                    keywordFirstChar == "i" || keywordFirstChar == "o" ||
                    keywordFirstChar == "u")
        ) articleToSearch = "la "
        if (plural == "Y" && gender == "M" &&
            (keywordFirstChar == "a" || keywordFirstChar == "e" ||
                    keywordFirstChar == "i" || keywordFirstChar == "o" ||
                    keywordFirstChar == "u")
        ) articleToSearch = "gli "
        if (plural == "Y" && gender == "M" &&
            !(keywordFirstChar == "a" || keywordFirstChar == "e" ||
                    keywordFirstChar == "i" || keywordFirstChar == "o" ||
                    keywordFirstChar == "u")
        ) articleToSearch = "i "
        if (plural == "Y" && gender == "F") articleToSearch = "le "
        return articleToSearch
    }
    // data la keyword Arasaac , il genere , la categoria singolare/plurale
    // e il tipo dell'eventuale verbo di movimento
    // restituisce l'articolo o il complemento di moto
    // individuato approssimativamente nel seguente modo :
    // se singolare e la keyword inizia per vocale -> l'
    // se singolare maschile , la keyword inizia per consonante s seguita da consonante o inizia per consonante z -> lo
    // se singolare maschile e la keyword inizia per consonante negli altri casi -> il
    // se singolare femminile e la keyword inizia per consonante -> la
    // se plurale maschile e la keyword inizia per vocale -> gli
    // se plurale maschile e la keyword inizia per consonante  -> i
    // se plurale femminile  -> le
    // previo controllo di eventuali eccezioni
    /**
     * given the keyword Arasaac, the gender, the singular / plural category and the type of the
     * eventual motion verb returns the article or the complement of motion identified approximately as follows:
     * if singular and the keyword starts with a vowel -> the
     * if masculine singular, the keyword starts with consonant s followed by consonant or starts with consonant z -> lo
     * if masculine singular and the keyword begins with a consonant in the other cases -> the
     * if feminine singular and the keyword starts with a consonant -> la
     * if masculine plural and the keyword starts with a vowel -> gli
     * if masculine plural and the keyword starts with a consonant -> i
     * if plural feminine -> le
     * after checking for any exceptions
     *
     * @param context context
     * @param kKeyword string containing the keyword Arasaac
     * @param gender string containing the gender masculine (gender = M) or feminine (gender = F) of the keyword Arasaac
     * @param plural string containing the singular (plural = N) / plural (plural = S) category of the keyword Arasaac
     * @param verbOfMovement string which indicates whether the keyword Arasaac is preceded by a verb of motion
     * @param realm realm
     * @return string the article or the complement of motion
     * @see GrammaticalExceptions
     */
    fun searchArticle(
        context: Context,
        kKeyword: String, gender: String, plural: String,
        verbOfMovement: String?, realm: Realm
    ): String {
        var articleToSearch = ""
        //
        val results = realm.where(
            GrammaticalExceptions::class.java
        )
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), kKeyword)
            .equalTo(context.getString(R.string.exceptiontype),
                context.getString(R.string.article_uppercase))
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                articleToSearch = when (verbOfMovement) {
                    context.getString(R.string.is_a_verb_of_movement_to_place) -> result.exception3 + " "
                    context.getString(R.string.is_a_verb_of_movement_from_place) -> result.exception2 + " "
                    else -> result.exception1 + " "
                }
                return articleToSearch
            }
        }
        //
        val keywordFirstChar = kKeyword.substring(0, 1)
        val keywordSecondChar = kKeyword.substring(1, 1)
        val keywordFirstCharVocale = keywordFirstChar == "a" || keywordFirstChar == "e" ||
                keywordFirstChar == "i" || keywordFirstChar == "o" ||
                keywordFirstChar == "u"
        val keywordSecondCharVocale = keywordSecondChar == "a" || keywordSecondChar == "e" ||
                keywordSecondChar == "i" || keywordSecondChar == "o" ||
                keywordSecondChar == "u"
        //
        if (plural == "N" &&
            keywordFirstCharVocale
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "all'"
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dall'"
                else -> "l'"
            }
            return articleToSearch
        }
        //
        if (plural == "N" && gender == "M" &&
            (keywordFirstChar == "z" ||
                    keywordFirstChar == "s" && !keywordSecondCharVocale)
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "allo "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dallo "
                else -> "lo "
            }
            return articleToSearch
        }
        //
        if (plural == "N" && gender == "M" &&
            !keywordFirstCharVocale
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "al "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dal "
                else -> "il "
            }
            return articleToSearch
        }
        //
        if (plural == "N" && gender == "F" &&
            !keywordFirstCharVocale
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "alla "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dalla "
                else -> "la "
            }
            return articleToSearch
        }
        //
        if (plural == "Y" && gender == "M" &&
            keywordFirstCharVocale
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "agli "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dagli "
                else -> "gli "
            }
            return articleToSearch
        }
        //
        if (plural == "Y" && gender == "M" &&
            !keywordFirstCharVocale
        ) {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "ai "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dai "
                else -> "i "
            }
            return articleToSearch
        }
        //
        if (plural == "Y" && gender == "F") {
            articleToSearch = when (verbOfMovement) {
                context.getString(R.string.is_a_verb_of_movement_to_place) -> "alle "
                context.getString(R.string.is_a_verb_of_movement_from_place) -> "dalle "
                else -> "le "
            }
            return articleToSearch
        }
        //
        return articleToSearch
    }
    // data la keyword Arasaac restituisce
    // se l'argomento si riferisce al genere maschile o femminile
    // individuato approssimativamente nel seguente modo :
    // se il plurale finisce in "i" -> genere maschile;
    // se il singolare finisce in "a" e il plurale in "e" -> genere femminile
    //
    /**
     * given the keyword Arasaac returns whether the argument refers to the male or female gender
     * identified approximately as follows:
     * if the plural ends in "i" -> masculine gender;
     * if the singular ends in "a" and the plural in "e" -> feminine gender
     *
     * @param context context
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string the gender
     * @see PictogramsAll
     */
    fun searchGender(   context: Context,
                        kKeyword: String,
                        realm: Realm): String {
        var genderToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(PictogramsAll::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), kKeyword)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val keywordLastChar = kKeyword.substring(kKeyword.length - 1)
                val plural = result.keywordPlural
                val pluralLastChar = plural!!.substring(plural.length - 1)
                if (pluralLastChar == "i") {
                    genderToSearchRealm = "M"
                } else {
                    if (pluralLastChar == "e" && keywordLastChar == "a") {
                        genderToSearchRealm = "F"
                    }
                }
            }
        }
        return genderToSearchRealm
    }
    // data la keyword Arasaac restituisce
    // se l'argomento si riferisce ad un plurale
    /**
     * given the keyword Arasaac returns if the argument refers to a plural
     *
     * @param context context
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string plural
     * @see PictogramsAll
     */
    fun searchPlural(   context: Context,
                        kKeyword: String?,
                        realm: Realm): String {
        var pluralToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(PictogramsAll::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), kKeyword)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                pluralToSearchRealm = result.plural!!
            }
        }
        return pluralToSearchRealm
    }
    // dato l'identificativo e la keyword Arasaac restituisce
    // se gli argomenti si riferiscono ad un plurale
    /**
     * given the identifier and the keyword Arasaac returns if the arguments refer to a plural
     *
     * @param context context
     * @param k_Id string containing the identifier Arasaac
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string plural
     * @see PictogramsAll
     */
    fun searchPlural(   context: Context,
                        k_Id: String?,
                        kKeyword: String?,
                        realm: Realm): String {
        var pluralToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(PictogramsAll::class.java)
            .beginGroup()
            .equalTo("_id", k_Id)
            .and()
            .equalTo(context.getString(R.string.keyword), kKeyword)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                pluralToSearchRealm = result.plural!!
            }
        }
        return pluralToSearchRealm
    }
    // dato l'identificativo e la keyword Arasaac restituisce
    // l'eventuale plurale corrispondente
    /**
     * given the identifier and the keyword Arasaac returns any corresponding plural
     *
     * @param context context
     * @param k_Id string containing the identifier Arasaac
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string corresponding plural
     * @see PictogramsAll
     */
    fun searchKeywordPlural(context: Context,
                            k_Id: String?,
                            kKeyword: String?,
                            realm: Realm): String {
        var pluralToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(PictogramsAll::class.java)
            .beginGroup()
            .equalTo("_id", k_Id)
            .and()
            .equalTo(context.getString(R.string.keyword), kKeyword)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                pluralToSearchRealm = result.keywordPlural!!
            }
        }
        return pluralToSearchRealm
    }
    // dato l'identificativo Arasaac restituisce il tipo di parola Arasaac
    // type = 1 sostantivi senza plurale
    // type = 2 sostantivi (o frasi) con possibile plurale
    // type = 3 verbi
    // type = 4 numeri (tra l'altro)
    // type = 6 articoli, preposizioni , avverbi di luogo (tra l'altro)
    /**
     * given the identifier Arasaac, it returns the type of word Arasaac
     * type = 1 nouns without plural
     * type = 2 nouns (or phrases) with possible plural
     * type = 3 verbs
     * type = 4 numbers (by the way)
     * type = 6 articles, prepositions, adverbs of place (among others)
     *
     * @param context context
     * @param k string containing the identifier Arasaac
     * @param realm realm
     * @return string with the type of word Arasaac
     * @see PictogramsAll
     */
    fun searchType(context: Context,
                   k: String?,
                   realm: Realm): String {
        var typeToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(PictogramsAll::class.java).equalTo(context.getString(R.string.keyword), k).findAll()
        val count = results.size
        if (count != 0) {
            //
            val result = results[0]
            if (result != null) {
                typeToSearchRealm = result.type!!
            }
        }
        return typeToSearchRealm
    }
    // data una coniugazione restituisce l'infinito
    /**
     * given a conjugation returns the infinitive
     *
     * @param context context
     * @param k string containing the conjugation
     * @param realm realm
     * @return string with the infinitive
     * @see Verbs
     */
    fun searchVerb(context: Context,
                   k: String?,
                   realm: Realm): String {
        var verbToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(Verbs::class.java).equalTo(context.getString(R.string.conjugation), k).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                verbToSearchRealm = result.infinitive!!
            }
        }
        return verbToSearchRealm
    }
    // dato l'infinito , la forma 1a, 2a o 3a persona singolare o plurale
    // restituisce la coniugazione all'indicativo presente
    /**
     * given the infinitive, the 1st, 2nd or 3rd person singular or plural form
     * returns the conjugation to the present indicative
     *
     *
     * form=s1 first person singular
     *
     *
     * form=s2 second person singular
     *
     *
     * form=s3 third person singular
     *
     *
     * form=p1 first person plural
     *
     *
     * form=p2 second person plural
     *
     *
     * form=p3 third person plural
     *
     * @param context context
     * @param k string containing the infinitive
     * @param form string containing the form
     * @param realm realm
     * @return string with the conjugation to the present indicative
     * @see Verbs
     */
    @JvmStatic
    fun searchVerb(context: Context,
                   k: String?,
                   form: String?,
                   realm: Realm): String {
        var verbToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(Verbs::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.infinitive), k)
            .equalTo(context.getString(R.string.group),
                context.getString(R.string.indicative_present))
            .equalTo(context.getString(R.string.form), form)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                verbToSearchRealm = result.conjugation!!
            }
        }
        return verbToSearchRealm
    }
    // dato l'infinito restituisce se si tratta di un verbo di movimento
    /**
     * given the infinitive, it returns if it is a verb of movement
     *
     * @param context context
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is a verb of movement
     * @see GrammaticalExceptions
     */
    fun searchVerbsOfMovement(context: Context,
                              k: String?,
                              realm: Realm): String {
        var verbToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(
            GrammaticalExceptions::class.java
        )
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), k)
            .equalTo(context.getString(R.string.exceptiontype), context.getString(R.string.is_a_verb_of_movement_to_place))
            .endGroup()
            .or()
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), k)
            .equalTo(context.getString(R.string.exceptiontype), context.getString(R.string.is_a_verb_of_movement_from_place))
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                verbToSearchRealm = result.exceptionType!!
            }
        }
        return verbToSearchRealm
    }
    // dato l'infinito restituisce se si tratta di un verbo ausiliare
    /**
     * given the infinitive, it returns if it is an auxiliary verb
     *
     * @param context context
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is an auxiliary verb
     * @see GrammaticalExceptions
     */
    fun searchAuxiliaryVerbs(context: Context,
                             k: String?,
                             realm: Realm): String {
        var verbToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(
            GrammaticalExceptions::class.java
        )
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), k)
            .equalTo(context.getString(R.string.exceptiontype), context.getString(R.string.is_an_auxiliary_verb))
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                verbToSearchRealm = result.exceptionType!!
            }
        }
        return verbToSearchRealm
    }
    // dato l'infinito restituisce se si tratta di un verbo servile
    /**
     * given the infinitive, it returns if it is a servile verb
     *
     * @param context context
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is a servile verb
     * @see GrammaticalExceptions
     */
    fun searchServileVerbs(context: Context,
                           k: String?,
                           realm: Realm): String {
        var verbToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(
            GrammaticalExceptions::class.java
        )
            .beginGroup()
            .equalTo(context.getString(R.string.keyword), k)
            .equalTo(context.getString(R.string.exceptiontype), context.getString(R.string.is_a_servile_verb))
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                verbToSearchRealm = result.exceptionType!!
            }
        }
        return verbToSearchRealm
    }
    // data una stringa restituisce se contiene un avverbio di negazione
    /**
     * given a string, if it contains a negation adverb
     * it returns description of the corresponding image in the images class
     *
     * @param context context
     * @param k string
     * @param realm realm
     * @return string with if it contains a negation adverb
     * @see GrammaticalExceptions
     */
    fun searchNegationAdverb(context: Context,
                             k: String,
                             realm: Realm): String {
        var NegationAdverb = context.getString(R.string.non_trovato)
        //
        // decomposes k
        val arrWords = splitString(k)
        //
        val arrWordsLength = arrWords.size
        var i = 0
        while (i < arrWordsLength) {
            val results = realm.where(
                GrammaticalExceptions::class.java
            )
                .beginGroup()
                .equalTo(context.getString(R.string.keyword), arrWords[i])
                .equalTo(context.getString(R.string.exceptiontype),
                    context.getString(R.string.is_a_negation_adverb))
                .endGroup()
                .findAll()
            val count = results.size
            if (count != 0) {
                val result = results[0]
                if (result != null) {
                    NegationAdverb = result.exception1!!
                    break
                }
            }
            i++
        }
        return NegationAdverb
    }
    // data una parola controlla se si tratta di un complemento al nome
    /**
     * given a word, check if it is a complement to the noun
     *
     * @param context context
     * @param k string containing the word
     * @param realm realm
     * @return string with if it is a complement to the noun
     * @see ComplementsOfTheName
     */
    fun searchComplement(context: Context,
                         k: String?,
                         realm: Realm): String {
        var complementToSearchRealm = context.getString(R.string.non_trovato)
        //
        val results = realm.where(
            ComplementsOfTheName::class.java
        ).equalTo(context.getString(R.string.keyword), k).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                complementToSearchRealm = result.keyword!!
            }
        }
        return complementToSearchRealm
    }

    /**
     * in game2 it creates a response phrase to the input received via speech recognizer
     *
     * @param context context
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with a response phrase
     * @see lookForTheLastPieceOfTheSentence
     *
     * @see verifyIfLastWordOfTheSentenceIsPlural
     *
     * @see Phrases
     */
    fun lookForTheAnswerToLastPieceOfTheSentence(
        context: Context,
        sharedLastPhraseNumber: Int,
        realm: Realm
    ): String {
        var answerToLastPieceOfTheSentence =
            lookForTheLastPieceOfTheSentence(context, sharedLastPhraseNumber, realm)
        //
        val lastWordOfTheSentencePlural =
            verifyIfLastWordOfTheSentenceIsPlural(context, sharedLastPhraseNumber, realm)
        //
        val phraseToSearch: Phrases?
        phraseToSearch = if (lastWordOfTheSentencePlural == "Y") {
            realm.where(Phrases::class.java)
                .equalTo(context.getString(R.string.tipo), context.getString(R.string.reminder_phrase_plural))
                .findFirst()
        } else {
            realm.where(Phrases::class.java)
                .equalTo(context.getString(R.string.tipo), context.getString(R.string.reminder_phrase))
                .findFirst()
        }
        answerToLastPieceOfTheSentence = if (phraseToSearch != null) {
            (phraseToSearch.descrizione
                    + " " + answerToLastPieceOfTheSentence)
        } else {
            " "
        }
        return answerToLastPieceOfTheSentence
    }

    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence to get the last part of the sentence
     *
     * @param context context
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with the last part of the sentence
     * @see lookForTheAnswerToLastPieceOfTheSentence
     *
     * @see History
     *
     * @see SearchLastWordOfTheSentence
     *
     * @see splitString
     *
     * @see searchComplement
     */
    fun lookForTheLastPieceOfTheSentence(context: Context,
                                         sharedLastPhraseNumber: Int,
                                         realm: Realm): String {
        // READS REALM HISTORY
        // considering on history the whole sentence instead of the single words
        var lastPieceOfTheSentence = " "
        val results = realm.where(
            History::class.java
        ).equalTo(context.getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            var sentence = result!!.word
            // search for the last noun in the sentence
            val lastWordOfTheSentence = SearchLastWordOfTheSentence(context, sharedLastPhraseNumber, realm)
            // from the last noun of the sentence it proceeds backwards including all the complements to the noun
            sentence = sentence!!.lowercase(Locale.getDefault())
            //
            val arrSentence = splitString(sentence)
            val arrSentenceLength = arrSentence.size
            var i = arrSentenceLength - 1
            //
            while (i >= 0) {
                if (arrSentence[i] != lastWordOfTheSentence) {
                    lastPieceOfTheSentence = arrSentence[i] + lastPieceOfTheSentence
                } else {
                    lastPieceOfTheSentence = arrSentence[i] + lastPieceOfTheSentence
                    var ii = i - 1
                    var complement: String?
                    while (ii >= 0) {
                        complement = searchComplement(context, arrSentence[ii], realm)
                        lastPieceOfTheSentence = if (complement == context.getString(R.string.non_trovato)) {
                            break
                        } else {
                            arrSentence[ii] + lastPieceOfTheSentence
                        }
                        ii--
                    }
                    break
                }
                i--
            }
        }
        return lastPieceOfTheSentence
    }

    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence to verify if last word of the sentence is plural
     *
     * @param context context
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with if last word of the sentence is plural
     * @see lookForTheAnswerToLastPieceOfTheSentence
     *
     * @see History
     */
    fun verifyIfLastWordOfTheSentenceIsPlural(context: Context,
                                              sharedLastPhraseNumber: Int,
                                              realm: Realm): String {
        // scroll the sentence backwards excluding verbs (type = 3)
        var lastWordOfTheSentencePlural = " "
        val results = realm.where(
            History::class.java
        ).equalTo(context.getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        if (count != 0) {
            var i = count - 1
            while (i > 0) {
                val result = results[i]!!
                val type = result.type
                if (type == "1" || type == "2") {
                    // non mette il plurale nello storico
                    lastWordOfTheSentencePlural = result.plural!!
                    break
                }
                i--
            }
        }
        return lastWordOfTheSentencePlural
    }

    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence via lookForTheLastPieceOfTheSentence to
     * search last word of the sentence
     *
     * @param context context
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with the last word of the sentence
     * @see lookForTheAnswerToLastPieceOfTheSentence
     *
     * @see History
     */
    fun SearchLastWordOfTheSentence(context: Context,
                                    sharedLastPhraseNumber: Int,
                                    realm: Realm): String {
        // scroll the sentence backwards excluding verbs (type = 3)
        var lastWordOfTheSentence = " "
        val results = realm.where(
            History::class.java
        ).equalTo(context.getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        if (count != 0) {
            var i = count - 1
            while (i > 0) {
                val result = results[i]!!
                val type = result.type
                if (type == "1" || type == "2") {
                    lastWordOfTheSentence = result.word!!
                    break
                }
                i--
            }
        }
        return lastWordOfTheSentence
    }

    /**
     * check if there is a correspondence between two sentences considering a margin of allowed errors
     *
     * @param phrase1 string containing the first sentence
     * @param phrase2 string containing the second sentence
     * @param percentualeErroriAmmessa  int containing the percentage of errors allowed
     * @return boolean true if the two sentences match
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    fun thereIsACorrespondenceWithAnAllowedMarginOfError(
        phrase1: String,
        phrase2: String,
        percentualeErroriAmmessa: Int
    ): Boolean {
        val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz"
        val alphabetLengt = alphabet.length
        //
        val phrase1Array = phrase1.toCharArray()
        val phrase1countArray = IntArray(36)
        for (x in phrase1Array) {
            for (i in 0 until alphabetLengt) {
                if (alphabet[i] == x) {
                    phrase1countArray[i]++
                }
            }
        }
        //
        val phrase2Array = phrase2.toCharArray()
        val phrase2countArray = IntArray(36)
        for (x in phrase2Array) {
            for (i in 0 until alphabetLengt) {
                if (alphabet[i] == x) {
                    phrase2countArray[i]++
                }
            }
        }
        //
        var differenze = 0
        for (i in 0 until alphabetLengt) {
            differenze = if (phrase1countArray[i] > phrase2countArray[i]) {
                differenze + (phrase1countArray[i] - phrase2countArray[i])
            } else {
                differenze + (phrase2countArray[i] - phrase1countArray[i])
            }
        }
        //
        val percentualedifferenze =
            differenze * 100 / 2 / phrase2.replace("\\s+".toRegex(), "").length
        //
        return percentualedifferenze <= percentualeErroriAmmessa
    }
}