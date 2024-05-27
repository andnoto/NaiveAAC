package com.sampietro.NaiveAAC.activities.Grammar

import android.content.Context
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAll
import com.sampietro.NaiveAAC.activities.Game.Game1.GetResultsWordPairsList
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames.Companion.checksWhetherWordRepresentsAClass
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames.Companion.listOfMembers
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames.Companion.searchesForThePossibleClassToWhichAWordBelongs
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.util.*

/**
 * <h1>GrammarHelper</h1>
 *
 *
 * **GrammarHelper** utility class for Italian grammar.
 *
 * @version     5.0, 01/04/2024
 */
object GrammarHelper {
    /**
     * splits a sentence into an array of words
     *
     * @param eText string containing the sentence to split
     */
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    /**
     * composes a sentence consisting of subject, verb, complement based on three lists and
     * the number of words chosen by the user.
     * Then it returns the three lists and the number of words chosen by normalizing them
     *
     * @param context context
     * @param realm realm
     * @param numberOfWordsChosen int
     * @param chosenColumn String
     * @param leftColumnContent String
     * @param middleColumnContent  String
     * @param rightColumnContent  String
     * @param listOfWordsLeft Array<String
     * @param listOfWordsCenter  Array<String
     * @param listOfWordsRight  Array<String
     * @param sharedLastPlayer String
     * @return ComposesASentenceResults
     * @see checksWhetherWordRepresentsAClass
     * @see listOfMembers
     * @see theFirstWordOfTheSentenceWasChosen
     * @see grammaticalArrangement
     * @see searchType
     * @see GetResultsWordPairsList.getResultsWordPairsList
     * @see refineSearchWordPairs
     */
    @JvmStatic
    fun composesASentence(
        context: Context,
        realm: Realm,
        numberOfWordsChosen: Int,
        chosenColumn: String,
        leftColumnContent: String,
        middleColumnContent: String,
        rightColumnContent: String,
        listOfWordsLeft: ArrayList<String>,
        listOfWordsCenter: ArrayList<String>,
        listOfWordsRight: ArrayList<String>,
        sharedLastPlayer: String
    ): ComposesASentenceResults {
        var numberOfWordsChosenToReturn: Int
        var leftColumnContentToReturn: String
        var middleColumnContentToReturn: String
        var rightColumnContentToReturn: String
        var listOfWordsLeftToReturn: MutableList<String>
        var listOfWordsCenterToReturn: MutableList<String>
        var listOfWordsRightToReturn: MutableList<String>
        //
        numberOfWordsChosenToReturn = numberOfWordsChosen
        leftColumnContentToReturn = leftColumnContent
        middleColumnContentToReturn = middleColumnContent
        rightColumnContentToReturn = rightColumnContent
        listOfWordsLeftToReturn = listOfWordsLeft.toMutableList()
        listOfWordsCenterToReturn = listOfWordsCenter.toMutableList()
        listOfWordsRightToReturn = listOfWordsRight.toMutableList()
        //
        when (numberOfWordsChosen) {
            0 -> {
                var previouslySearchedClassName = context.getString(R.string.nessuno)
                while (listOfWordsCenterToReturn.size == 1
                    && checksWhetherWordRepresentsAClass(context, listOfWordsCenterToReturn[0] ,realm))
                {
                    // if it is the name of a class I look for the list of its members
                    // to avoid an infinite loop I exit the loop if the previously searched class name
                    // is the same as the current class name to search for
                    if (previouslySearchedClassName != listOfWordsCenterToReturn[0])
                    {
                        previouslySearchedClassName = listOfWordsCenterToReturn[0]
                        val listOfMembers = listOfMembers(context, listOfWordsCenterToReturn[0] ,realm)
                        listOfWordsCenterToReturn = listOfMembers
                    }
                    else { break }
                }
                if (listOfWordsCenterToReturn.size == 1)
                // the first word of the sentence was chosen
                    // (in the case of noun and verb only possible choice,
                    // the second word of the sentence is also considered to be chosen -
                    // in the case of verb and nouns the only possible choice is considered
                    // also chosen the second or second and third word of the sentence)
                {
                    val composesASentenceResultsToReturn =
                        theFirstWordOfTheSentenceWasChosen(context, realm, numberOfWordsChosen+1,
                            leftColumnContent, listOfWordsCenterToReturn[0], rightColumnContent,
                            listOfWordsLeft, listOfWordsCenter, listOfWordsRight )
                    numberOfWordsChosenToReturn = composesASentenceResultsToReturn.numberOfWordsChosen
                    leftColumnContentToReturn = composesASentenceResultsToReturn.leftColumnContent
                    middleColumnContentToReturn = composesASentenceResultsToReturn.middleColumnContent
                    rightColumnContentToReturn = composesASentenceResultsToReturn.rightColumnContent
                    listOfWordsLeftToReturn = composesASentenceResultsToReturn.listOfWordsLeft
                    listOfWordsCenterToReturn = composesASentenceResultsToReturn.listOfWordsCenter
                    listOfWordsRightToReturn = composesASentenceResultsToReturn.listOfWordsRight
                 }
                if (numberOfWordsChosenToReturn == 3)
                {
                    // grammar arrangement
                    val composesASentenceResults: ComposesASentenceResults = grammaticalArrangement (
                        context,
                        realm,
                        numberOfWordsChosenToReturn,
                        leftColumnContentToReturn,
                        middleColumnContentToReturn,
                        rightColumnContentToReturn,
                        listOfWordsLeftToReturn as ArrayList<String>,
                        listOfWordsCenterToReturn as ArrayList<String>,
                        listOfWordsRightToReturn as ArrayList<String>,
                        sharedLastPlayer
                    )
                    leftColumnContentToReturn = composesASentenceResults.leftColumnContent
                    middleColumnContentToReturn = composesASentenceResults.middleColumnContent
                    rightColumnContentToReturn = composesASentenceResults.rightColumnContent
                    listOfWordsLeftToReturn.clear()
                    listOfWordsLeftToReturn.addAll(composesASentenceResults.listOfWordsLeft)
                    listOfWordsCenterToReturn.clear()
                    listOfWordsCenterToReturn.addAll(composesASentenceResults.listOfWordsCenter)
                    listOfWordsRightToReturn.clear()
                    listOfWordsRightToReturn.addAll(composesASentenceResults.listOfWordsRight)
                }
            }
            1 -> {
                // the second word of the sentence was chosen
                // 1) if the chosen word (central) is a verb (type = 3 verbs)
                // 1a) if the chosen word is on the left, I register the choice and keep the options
                // list in the recycler view on the right
                // 1b) if the chosen word is in the center the choice is not allowed
                // (because it has already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 1c) if the word chosen is on the right i register the choice and
                // keep the options list in the recycler view on the left
                // 2) if the chosen (middle) word is not a verb
                // 2a) if the chosen word is on the left, i register the choice,
                // moving the central column to the right and the left column (that of the choice)
                // to the center and proposing in the left column the choices compatible
                // with the choice (it should be a verb)
                // 2b) if the chosen word is in the center the choice is not allowed (because it has
                // already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 2c) if the word chosen is on the right, i register the choice moving the central
                // column to the left and the right column (that of the choice) to the center and
                // proposing in the right column the choices compatible with the choice (it should be a verb)
                val middleColumnContentType = searchType(context, listOfWordsCenterToReturn[0], realm)
                // type = 3 verbs
                // 1) if the chosen word (central) is a verb (type = 3 verbs)
                // 1a) if the chosen word is on the left, I register the choice and keep the options
                // list in the recycler view on the right
                // 1b) if the chosen word is in the center the choice is not allowed
                // (because it has already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 1c) if the word chosen is on the right i register the choice and
                // keep the options list in the recycler view on the left
                if (middleColumnContentType == "3") {
                    // 1a) if the chosen word is on the left, I register the choice and keep the options
                    // list in the recycler view on the right
                    if (chosenColumn == "left" && leftColumnContentToReturn == context.getString(R.string.nessuno)) {
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsLeftToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsLeftToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                                listOfWordsLeftToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsLeftToReturn.size == 1) {
                            leftColumnContentToReturn = listOfWordsLeftToReturn[0]
                            numberOfWordsChosenToReturn++
                        }
                    }
                    // 1c) if the word chosen is on the right i register the choice and
                    // keep the options list in the recycler view on the left
                    if (chosenColumn == "right" && rightColumnContentToReturn == context.getString(R.string.nessuno)) {
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsRightToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsRightToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                                listOfWordsRightToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsRightToReturn.size == 1) {
                            rightColumnContentToReturn = listOfWordsRightToReturn[0]
                            numberOfWordsChosenToReturn++
                        }
                    }
                    // 1b) if the chosen word is in the center the choice is not allowed
                    // (because it has already been chosen) i keep both option lists in the recycler views
                    // both on the right and on the left
                }
                else
                // 2) if the chosen (middle) word is not a verb
                {
                    // 2a) if the chosen word is on the left, i register the choice,
                    // moving the central column to the right (retrieving the data of the
                    // eventual prize video) and the left column (that of the choice)
                    // to the center and proposing in the left column the choices compatible
                    // with the choice (it should be a verb)
                    if (chosenColumn == "left" && leftColumnContentToReturn == context.getString(R.string.nessuno)) {
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsLeftToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsLeftToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                                listOfWordsLeftToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsLeftToReturn.size == 1) {
                            listOfWordsRightToReturn.clear()
                            listOfWordsRightToReturn.addAll(listOfWordsCenterToReturn)
                            rightColumnContentToReturn = middleColumnContentToReturn
                            listOfWordsCenterToReturn.clear()
                            listOfWordsCenterToReturn.add(listOfWordsLeftToReturn[0])
                            middleColumnContentToReturn = listOfWordsCenterToReturn[0]
                            listOfWordsLeftToReturn.clear()
                            numberOfWordsChosenToReturn++
                            //
                            val resultsWordPairsLeft = realm.where(WordPairs::class.java)
                                .beginGroup()
                                .equalTo(context.getString(R.string.word2), listOfWordsCenterToReturn[0])
                                .endGroup()
                                .findAll()
                            var resultsWordPairsLeftSize = resultsWordPairsLeft!!.size
                            val resultsWordPairsLeftList: MutableList<WordPairs>?
                            if (resultsWordPairsLeftSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                val resultsWordPairsList =
                                    GetResultsWordPairsList.getResultsWordPairsList(
                                        realm,
                                        resultsWordPairsLeft
                                    )
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsLeftList =
                                refineSearchWordPairs(context, realm, resultsWordPairsList)
                                resultsWordPairsLeftSize = resultsWordPairsLeftList.size
                                if (resultsWordPairsLeftSize != 0) {
                                    listOfWordsLeftToReturn.clear()
                                    var resultsWordPairsIndex = 0
                                    while (resultsWordPairsLeftSize > resultsWordPairsIndex) {
                                        listOfWordsLeftToReturn.add(resultsWordPairsLeftList[resultsWordPairsIndex].word1!!)
                                        resultsWordPairsIndex++
                                    }
                                    //
                                    previouslySearchedClassName = context.getString(R.string.nessuno)
                                    while (listOfWordsLeftToReturn.size == 1
                                        && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
                                    {
                                        // if it is the name of a class I look for the list of its members
                                        // to avoid an infinite loop I exit the loop if the previously searched class name
                                        // is the same as the current class name to search for
                                        if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                                        {
                                            previouslySearchedClassName = listOfWordsLeftToReturn[0]
                                            val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                                            listOfWordsLeftToReturn = listOfMembers
                                        }
                                        else { break }
                                    }
                                    if (listOfWordsLeftToReturn.size == 1) {
                                        leftColumnContentToReturn = listOfWordsLeftToReturn[0]
                                        numberOfWordsChosenToReturn++
                                        // sentence completion check, grammar arrangement and text reading
                                    }
                                    //
                                } else
                                {
                                    numberOfWordsChosenToReturn++
                                    // sentence completion check, grammar arrangement and text reading
                                }
                            }
                            else
                            {
                                numberOfWordsChosenToReturn++
                                // sentence completion check, grammar arrangement and text reading
                            }
                    }
                }
                    // 2c) if the word chosen is on the right, i register the choice moving the central
                    // column to the left and the right column (that of the choice) to the center and
                    // proposing in the right column the choices compatible with the choice (it should be a verb)
                    if (chosenColumn == "right" && rightColumnContentToReturn == context.getString(R.string.nessuno)) {
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsRightToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsRightToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                                listOfWordsRightToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsRightToReturn.size == 1) {
                            listOfWordsLeftToReturn.clear()
                            listOfWordsLeftToReturn.addAll(listOfWordsCenterToReturn)
                            leftColumnContentToReturn = middleColumnContentToReturn
                            listOfWordsCenterToReturn.clear()
                            listOfWordsCenterToReturn.add(listOfWordsRightToReturn[0])
                            middleColumnContentToReturn = listOfWordsCenterToReturn[0]
                            listOfWordsRightToReturn.clear()
                            numberOfWordsChosenToReturn++
                            //
                            val resultsWordPairsRight = realm.where(WordPairs::class.java)
                                .beginGroup()
                                .equalTo(context.getString(R.string.word1), listOfWordsCenterToReturn[0])
                                .endGroup()
                                .findAll()
                            var resultsWordPairsRightSize = resultsWordPairsRight!!.size
                            val resultsWordPairsRightList: MutableList<WordPairs>?
                            if (resultsWordPairsRightSize != 0) {
                                // convert RealmResults<Model> to ArrayList<Model>
                                val resultsWordPairsList =
                                    GetResultsWordPairsList.getResultsWordPairsList(
                                        realm,
                                        resultsWordPairsRight
                                    )
                                // does not consider wordpairs with pairs of nouns or pairs of verbs
                                // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                                // (leaves only noun-verb pairs or vice versa verb-noun
                                // or verb-verb if the first is an auxiliary verb or a servile verb)
                                resultsWordPairsRightList =
                                    refineSearchWordPairs(context, realm, resultsWordPairsList)
                                resultsWordPairsRightSize = resultsWordPairsRightList.size
                                if (resultsWordPairsRightSize != 0) {
                                    listOfWordsRightToReturn.clear()
                                    var resultsWordPairsIndex = 0
                                    while (resultsWordPairsRightSize > resultsWordPairsIndex) {
                                        listOfWordsRightToReturn.add(resultsWordPairsRightList[resultsWordPairsIndex].word2!!)
                                        resultsWordPairsIndex++
                                    }
                                    //
                                    previouslySearchedClassName = context.getString(R.string.nessuno)
                                    while (listOfWordsRightToReturn.size == 1
                                        && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
                                    {
                                        // if it is the name of a class I look for the list of its members
                                        // to avoid an infinite loop I exit the loop if the previously searched class name
                                        // is the same as the current class name to search for
                                        if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                                        {
                                            previouslySearchedClassName = listOfWordsRightToReturn[0]
                                            val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                                            listOfWordsRightToReturn = listOfMembers
                                        }
                                        else { break }
                                    }
                                    if (listOfWordsRightToReturn.size == 1) {
                                        rightColumnContentToReturn = listOfWordsRightToReturn[0]
                                        numberOfWordsChosenToReturn++
                                        // sentence completion check, grammar arrangement and text reading
                                    }
                                    //
                                } else
                                {
                                    numberOfWordsChosenToReturn++
                                    // sentence completion check, grammar arrangement and text reading
                                }
                            }
                            else
                            {
                                numberOfWordsChosenToReturn++
                                // sentence completion check, grammar arrangement and text reading
                            }
                        }
                    }
                    // 2b) if the chosen word is in the center the choice is not allowed (because it has
                    // already been chosen) i keep both option lists in the recycler views
                    // both on the right and on the left
                }
        }
            2 -> {
                // chosen the 3rd and last word of the sentence (to be checked if the sentence completes)
                // the following clicks on the words (case 3) will start the tts
                // 1a) if the word chosen is on the left, register the choice (if it has not already been made)
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)

                // 1a) if the word chosen is on the left, register the choice
                // (if it has not already been made)
                if (chosenColumn == "left" && leftColumnContentToReturn == context.getString(R.string.nessuno)) {
                    var previouslySearchedClassName = context.getString(R.string.nessuno)
                    while (listOfWordsLeftToReturn.size == 1
                        && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
                    {
                        // if it is the name of a class I look for the list of its members
                        // to avoid an infinite loop I exit the loop if the previously searched class name
                        // is the same as the current class name to search for
                        if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                        {
                            previouslySearchedClassName = listOfWordsLeftToReturn[0]
                            val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                            listOfWordsLeftToReturn = listOfMembers
                        }
                        else { break }
                    }
                    if (listOfWordsLeftToReturn.size == 1) {
                        leftColumnContentToReturn = listOfWordsLeftToReturn[0]
                        numberOfWordsChosenToReturn++
                    }
                }
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)
                if (chosenColumn == "right" && rightColumnContentToReturn == context.getString(R.string.nessuno)) {
                    var previouslySearchedClassName = context.getString(R.string.nessuno)
                    while (listOfWordsRightToReturn.size == 1
                        && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
                    {
                        // if it is the name of a class I look for the list of its members
                        // to avoid an infinite loop I exit the loop if the previously searched class name
                        // is the same as the current class name to search for
                        if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                        {
                            previouslySearchedClassName = listOfWordsRightToReturn[0]
                            val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                            listOfWordsRightToReturn = listOfMembers
                        }
                        else { break }
                    }
                    if (listOfWordsRightToReturn.size == 1) {
                        rightColumnContentToReturn = listOfWordsRightToReturn[0]
                        numberOfWordsChosenToReturn++
                    }
                }
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)

            }
    }
        if (numberOfWordsChosenToReturn == 3)
        {
            // grammar arrangement
            val composesASentenceResults: ComposesASentenceResults = grammaticalArrangement (
                context,
                realm,
                numberOfWordsChosenToReturn,
                leftColumnContentToReturn,
                middleColumnContentToReturn,
                rightColumnContentToReturn,
                listOfWordsLeftToReturn as ArrayList<String>,
                listOfWordsCenterToReturn as ArrayList<String>,
                listOfWordsRightToReturn as ArrayList<String>,
                sharedLastPlayer
            )
            numberOfWordsChosenToReturn = composesASentenceResults.numberOfWordsChosen
            leftColumnContentToReturn = composesASentenceResults.leftColumnContent
            middleColumnContentToReturn = composesASentenceResults.middleColumnContent
            rightColumnContentToReturn = composesASentenceResults.rightColumnContent
            listOfWordsLeftToReturn.clear()
            listOfWordsLeftToReturn.addAll(composesASentenceResults.listOfWordsLeft)
            listOfWordsCenterToReturn.clear()
            listOfWordsCenterToReturn.addAll(composesASentenceResults.listOfWordsCenter)
            listOfWordsRightToReturn.clear()
            listOfWordsRightToReturn.addAll(composesASentenceResults.listOfWordsRight)
        }
        return ComposesASentenceResults(numberOfWordsChosenToReturn,
            leftColumnContentToReturn, middleColumnContentToReturn, rightColumnContentToReturn,
            listOfWordsLeftToReturn as ArrayList<String>,
            listOfWordsCenterToReturn as ArrayList<String>,
            listOfWordsRightToReturn as ArrayList<String>
        )
    }

    /**
     * called by composesASentence in case the user has chosen the first word.
     * Then it returns the three lists and the number of words chosen by normalizing them
     *
     * @param context context
     * @param realm realm
     * @param numberOfWordsChosen int
     * @param leftColumnContent String
     * @param middleColumnContent  String
     * @param rightColumnContent  String
     * @param listOfWordsLeft Array<String
     * @param listOfWordsCenter  Array<String
     * @param listOfWordsRight  Array<String
     * @return ComposesASentenceResults
     * @see GetResultsWordPairsList.getResultsWordPairsList
     * @see refineSearchWordPairs
     * @see checksWhetherWordRepresentsAClass
     * @see listOfMembers
     */
    @JvmStatic
    fun theFirstWordOfTheSentenceWasChosen(
        context: Context,
        realm: Realm,
        numberOfWordsChosen: Int,
        leftColumnContent: String,
        middleColumnContent: String,
        rightColumnContent: String,
        listOfWordsLeft: ArrayList<String>,
        listOfWordsCenter: ArrayList<String>,
        listOfWordsRight: ArrayList<String>
    ): ComposesASentenceResults {
        var numberOfWordsChosenToReturn: Int
        var leftColumnContentToReturn: String
        var middleColumnContentToReturn: String
        var rightColumnContentToReturn: String
        var listOfWordsLeftToReturn: MutableList<String>
        var listOfWordsCenterToReturn: MutableList<String>
        var listOfWordsRightToReturn: MutableList<String>
        numberOfWordsChosenToReturn = numberOfWordsChosen
        leftColumnContentToReturn = leftColumnContent
        middleColumnContentToReturn = middleColumnContent
        rightColumnContentToReturn = rightColumnContent
        listOfWordsLeftToReturn = listOfWordsLeft.toMutableList()
        listOfWordsCenterToReturn = listOfWordsCenter.toMutableList()
        listOfWordsRightToReturn = listOfWordsRight.toMutableList()
        // search for related words on wordpairs
        // if the chosen word is the first of the pair,
        // the corresponding second words go to the right column
        // vice versa if the chosen word is the second of the pair,
        // the corresponding second words go to the left column
        var resultsWordPairsLeft = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.word2), middleColumnContentToReturn)
            .endGroup()
            .findAll()
        var resultsWordPairsLeftSize = resultsWordPairsLeft!!.size
        var resultsWordPairsLeftList: MutableList<WordPairs>? = null
        if (resultsWordPairsLeftSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            val resultsWordPairsList =
                GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsLeft)
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsLeftList = refineSearchWordPairs(context, realm, resultsWordPairsList)
            resultsWordPairsLeftSize = resultsWordPairsLeftList.size
            listOfWordsLeftToReturn.clear()
            var resultsWordPairsIndex = 0
            while (resultsWordPairsLeftSize > resultsWordPairsIndex) {
                listOfWordsLeftToReturn.add(resultsWordPairsLeftList[resultsWordPairsIndex].word1!!)
                resultsWordPairsIndex++
            }
            //
            var previouslySearchedClassName = context.getString(R.string.nessuno)
            while (listOfWordsLeftToReturn.size == 1
                && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
            {
                // if it is the name of a class I look for the list of its members
                // to avoid an infinite loop I exit the loop if the previously searched class name
                // is the same as the current class name to search for
                if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                {
                    previouslySearchedClassName = listOfWordsLeftToReturn[0]
                    val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                    listOfWordsLeftToReturn = listOfMembers
                }
                else { break }
            }
        }
        //
        val resultsWordPairsRight = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.word1), middleColumnContentToReturn)
            .endGroup()
            .findAll()
        var resultsWordPairsRightSize = resultsWordPairsRight!!.size
        var resultsWordPairsRightList: MutableList<WordPairs>? = null
        if (resultsWordPairsRightSize != 0) {
            // convert RealmResults<Model> to ArrayList<Model>
            val resultsWordPairsList =
                GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsRight)
            // does not consider wordpairs with pairs of nouns or pairs of verbs
            // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
            // (leaves only noun-verb pairs or vice versa verb-noun
            // or verb-verb if the first is an auxiliary verb or a servile verb)
            resultsWordPairsRightList = refineSearchWordPairs(context, realm, resultsWordPairsList)
            resultsWordPairsRightSize = resultsWordPairsRightList.size
            listOfWordsRightToReturn.clear()
            var resultsWordPairsIndex = 0
            while (resultsWordPairsRightSize > resultsWordPairsIndex) {
                listOfWordsRightToReturn.add(resultsWordPairsRightList[resultsWordPairsIndex].word2!!)
                resultsWordPairsIndex++
            }
            //
            var previouslySearchedClassName = context.getString(R.string.nessuno)
            while (listOfWordsRightToReturn.size == 1
                && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
            {
                // if it is the name of a class I look for the list of its members
                // to avoid an infinite loop I exit the loop if the previously searched class name
                // is the same as the current class name to search for
                if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                {
                    previouslySearchedClassName = listOfWordsRightToReturn[0]
                    val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                    listOfWordsRightToReturn = listOfMembers
                }
                else { break }
            }
        }
        // if you have only one possible choice (in the recycler views on the right and left)
        // and this is a verb (if the first word chosen is not a verb, the proposed choices
        // can only be verbs)
        // 1a) if the verb only possible choice is on the left, register the choice
        // by moving the central column to the right and the left column in the center
        // and proposing in the left column the choices compatible with the verb
        // 1b) if the verb only possible choice is on the right, register the choice
        // moving the middle column to the left and the right column in the center
        // and proposing in the right column the choices compatible with the verb
        // 1c) if I have only one possible choice verb both on the left and on the right,
        // I propose the choice both in the column on the right and in the one on the left
        // 2) if the first word chosen (the middle one) is a verb, check if I have only one
        // possible choice in the recycler views on the right and left
        // in this case I register the choice
        val middleColumnContentType = searchType(context, listOfWordsCenter[0], realm)
        // type = 3 verbs
        if (middleColumnContentType != "3") {
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 0) {
                // 1a) if the verb only possible choice is on the left, register the choice
                // by moving the central column to the right and the left column in the center
                // and proposing in the left column the choices compatible with the verb
                listOfWordsRightToReturn.clear()
                listOfWordsRightToReturn.addAll(listOfWordsCenterToReturn)
                rightColumnContentToReturn = middleColumnContentToReturn
                listOfWordsCenterToReturn.clear()
                listOfWordsCenterToReturn.add(resultsWordPairsLeftList!![0].word1!!)
                middleColumnContentToReturn = listOfWordsCenterToReturn[0]
                //
                resultsWordPairsLeft = realm.where(WordPairs::class.java)
                    .beginGroup()
                    .equalTo(context.getString(R.string.word2), listOfWordsCenterToReturn[0])
                    .endGroup()
                    .findAll()
                resultsWordPairsLeftSize = resultsWordPairsLeft!!.size
                if (resultsWordPairsLeftSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    val resultsWordPairsList =
                        GetResultsWordPairsList.getResultsWordPairsList(realm, resultsWordPairsLeft)
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsLeftList = refineSearchWordPairs(context, realm, resultsWordPairsList)
                    resultsWordPairsLeftSize = resultsWordPairsLeftList.size
                    //
                    if (resultsWordPairsLeftSize != 0) {
                        listOfWordsLeftToReturn.clear()
                        var resultsWordPairsIndex = 0
                        while (resultsWordPairsLeftSize > resultsWordPairsIndex) {
                            listOfWordsLeftToReturn.add(resultsWordPairsLeftList[resultsWordPairsIndex].word1!!)
                            resultsWordPairsIndex++
                        }
                        //
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsLeftToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsLeftToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsLeftToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsLeftToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsLeftToReturn[0] ,realm)
                                listOfWordsLeftToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsLeftToReturn.size == 1) {
                            leftColumnContentToReturn = listOfWordsLeftToReturn[0]
                            numberOfWordsChosenToReturn++
                            // sentence completion check, grammar arrangement and text reading
                        }
                        //
                    } else
                    {
                        numberOfWordsChosenToReturn++
                        // sentence completion check, grammar arrangement and text reading
                    }
                } else {
                    // resultsWordPairsLeftSize == 0 : the sentence is complete
                    numberOfWordsChosenToReturn++
                    // sentence completion check, grammar arrangement and text reading
                }
                numberOfWordsChosenToReturn++
            }
            if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 1) {
                // 1b) if the verb only possible choice is on the right, register the choice
                // moving the middle column to the left and the right column in the center
                // and proposing in the right column the choices compatible with the verb
                listOfWordsLeftToReturn.clear()
                listOfWordsLeftToReturn.addAll(listOfWordsCenterToReturn)
                leftColumnContentToReturn = middleColumnContentToReturn
                listOfWordsCenterToReturn.clear()
                listOfWordsCenterToReturn.add(resultsWordPairsRightList!![0].word2!!)
                middleColumnContentToReturn = listOfWordsCenterToReturn[0]
                //
                val resultsWordPairsRight = realm.where(WordPairs::class.java)
                    .beginGroup()
                    .equalTo(context.getString(R.string.word1), resultsWordPairsRightList[0].word2!!)
                    .endGroup()
                    .findAll()
                resultsWordPairsRightSize = resultsWordPairsRight!!.size
                if (resultsWordPairsRightSize != 0) {
                    // convert RealmResults<Model> to ArrayList<Model>
                    val resultsWordPairsList = GetResultsWordPairsList.getResultsWordPairsList(
                        realm,
                        resultsWordPairsRight
                    )
                    // does not consider wordpairs with pairs of nouns or pairs of verbs
                    // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
                    // (leaves only noun-verb pairs or vice versa verb-noun
                    // or verb-verb if the first is an auxiliary verb or a servile verb)
                    resultsWordPairsRightList = refineSearchWordPairs(context, realm, resultsWordPairsList)
                    resultsWordPairsRightSize = resultsWordPairsRightList.size
                    //
                    if (resultsWordPairsRightSize != 0) {
                        listOfWordsRightToReturn.clear()
                        var resultsWordPairsIndex = 0
                        while (resultsWordPairsRightSize > resultsWordPairsIndex) {
                            listOfWordsRightToReturn.add(resultsWordPairsRightList[resultsWordPairsIndex].word2!!)
                            resultsWordPairsIndex++
                        }
                        //
                        var previouslySearchedClassName = context.getString(R.string.nessuno)
                        while (listOfWordsRightToReturn.size == 1
                            && checksWhetherWordRepresentsAClass(context, listOfWordsRightToReturn[0] ,realm))
                        {
                            // if it is the name of a class I look for the list of its members
                            // to avoid an infinite loop I exit the loop if the previously searched class name
                            // is the same as the current class name to search for
                            if (previouslySearchedClassName != listOfWordsRightToReturn[0])
                            {
                                previouslySearchedClassName = listOfWordsRightToReturn[0]
                                val listOfMembers = listOfMembers(context, listOfWordsRightToReturn[0] ,realm)
                                listOfWordsRightToReturn = listOfMembers
                            }
                            else { break }
                        }
                        if (listOfWordsRightToReturn.size == 1) {
                            rightColumnContentToReturn = listOfWordsRightToReturn[0]
                            numberOfWordsChosenToReturn++
                            // sentence completion check, grammar arrangement and text reading
                        }
                        //
                    }
                    else
                    {
                    numberOfWordsChosenToReturn++
                    // sentence completion check, grammar arrangement and text reading
                    }
                } else {
                    // resultsWordPairsRightSize == 0 : la frase è completa
                    numberOfWordsChosenToReturn++
                    // sentence completion check, grammar arrangement and text reading
                }
                numberOfWordsChosenToReturn++
            }
            if (resultsWordPairsLeftSize == 1 && resultsWordPairsRightSize == 1) {
                // 1c) if I have only one possible choice verb both on the left and on the right,
                // I propose the choice both in the column on the right and in the one on the left
                // History registration
                middleColumnContentToReturn = listOfWordsCenterToReturn[0]
                listOfWordsLeftToReturn.clear()
                var resultsWordPairsIndex = 0
                while (resultsWordPairsLeftSize > resultsWordPairsIndex) {
                    listOfWordsLeftToReturn.add(resultsWordPairsLeftList!![resultsWordPairsIndex].word1!!)
                    resultsWordPairsIndex++
                }
                listOfWordsRightToReturn.clear()
                resultsWordPairsIndex = 0
                while (resultsWordPairsRightSize > resultsWordPairsIndex) {
                    listOfWordsRightToReturn.add(resultsWordPairsRightList!![resultsWordPairsIndex].word2!!)
                    resultsWordPairsIndex++
                }
            }
        } else {
            // 2) if the first word chosen (the middle one) is a verb, check if I have only one
            // possible choice in the recycler views on the right and left
            // in this case I register the choice
            middleColumnContentToReturn = listOfWordsCenterToReturn[0]
            if (resultsWordPairsLeftSize == 1) {
                leftColumnContentToReturn = listOfWordsLeftToReturn[0]
                numberOfWordsChosenToReturn++
            }
            if (resultsWordPairsRightSize == 1) {
                rightColumnContentToReturn = listOfWordsRightToReturn[0]
                numberOfWordsChosenToReturn++
            }
        }
        // if you do not have possible choices neither on the recycler view on the right
        // nor on that one on the left, the sentence is completed with a single word
        if (resultsWordPairsLeftSize == 0 && resultsWordPairsRightSize == 0) {
            middleColumnContentToReturn = listOfWordsCenterToReturn[0]
            listOfWordsLeftToReturn.clear()
            numberOfWordsChosenToReturn++
            listOfWordsRightToReturn.clear()
            numberOfWordsChosenToReturn++
            // sentence completion check, grammar arrangement and text reading
        }
        return ComposesASentenceResults(numberOfWordsChosenToReturn,
                                        leftColumnContentToReturn,
                                        middleColumnContentToReturn,
                                        rightColumnContentToReturn,
                                        listOfWordsLeftToReturn as ArrayList<String>,
                                        listOfWordsCenterToReturn as ArrayList<String>,
                                        listOfWordsRightToReturn as ArrayList<String>
        )
    }

    /**
     * leaves only noun-verb pairs or vice versa verb-noun.
     *
     * @param context Context
     * @param realm Realm
     * @param resultsWordPairsList MutableList<WordPairs> to clean up
     * @return MutableList<WordPairs> cleaned up
     * @see WordPairs
     * @see searchType
     * @see searchAuxiliaryVerbs
     * @see searchServileVerbs
     */
    @JvmStatic
    fun refineSearchWordPairs(context:Context, realm:Realm, resultsWordPairsList: MutableList<WordPairs>?): MutableList<WordPairs> {
        // does not consider wordpairs with pairs of nouns or pairs of verbs
        // (accepts two verbs only if the first is an auxiliary verb or a servile verb)
        // (leaves only noun-verb pairs or vice versa verb-noun
        // or verb-verb if the first is an auxiliary verb or a servile verb)
        var auxiliaryVerb: String
        var servileVerb: String
        //
        var i = 0
        var resultsWordPairsSize = resultsWordPairsList!!.size
        while (i < resultsWordPairsSize) {
            val word1Type = searchType(context, resultsWordPairsList[i].word1, realm)
            // type = 3 verbs
            val word2Type = searchType(context, resultsWordPairsList[i].word2, realm)
            //
            auxiliaryVerb = searchAuxiliaryVerbs(context, resultsWordPairsList[i].word1, realm)
            servileVerb = searchServileVerbs(context, resultsWordPairsList[i].word1, realm)
            if (word1Type == "3" && word2Type == "3"
                && auxiliaryVerb != context.getString(R.string.is_an_auxiliary_verb) && servileVerb != context.getString(R.string.is_a_servile_verb)
                || word1Type != "3" && word2Type != "3"
            ) {
                resultsWordPairsList.removeAt(i)
                resultsWordPairsSize--
            } else {
                i++
            }
        }
        return resultsWordPairsList
    }
    /**
     * grammar arrangement:
     * adds the corresponding article.
     *
     * @param context context
     * @param realm realm
     * @param numberOfWordsChosen int
     * @param leftColumnContent String
     * @param middleColumnContent  String
     * @param rightColumnContent  String
     * @param listOfWordsLeft Array<String
     * @param listOfWordsCenter  Array<String
     * @param listOfWordsRight  Array<String
     * @param sharedLastPlayer String
     * @return ComposesASentenceResults
     * @see searchServileVerbs
     * @see searchVerb
     * @see searchPlural
     * @see searchGender
     * @see searchArticle
     * @see WordPairs
     * @see searchVerbsOfMovement
     */
    @JvmStatic
    fun grammaticalArrangement(
        context: Context,
        realm: Realm,
        numberOfWordsChosen: Int,
        leftColumnContent: String,
        middleColumnContent: String,
        rightColumnContent: String,
        listOfWordsLeft: ArrayList<String>,
        listOfWordsCenter: ArrayList<String>,
        listOfWordsRight: ArrayList<String>,
        sharedLastPlayer: String
    ): ComposesASentenceResults {
        val numberOfWordsChosenToReturn: Int
        var leftColumnContentToReturn: String
        var middleColumnContentToReturn: String
        var rightColumnContentToReturn: String
        val listOfWordsLeftToReturn: MutableList<String>
        val listOfWordsCenterToReturn: MutableList<String>
        val listOfWordsRightToReturn: MutableList<String>
        // nominal group subject
        var pluralToSearchRealm = ""
        var genderToSearchRealm: String?
        var formToSearchRealm: String
        val verbOfMovement: String
        //
        var middleColumnContentVerbInTheInfinitiveForm:String? = null
        //
        numberOfWordsChosenToReturn = numberOfWordsChosen
        leftColumnContentToReturn = leftColumnContent
        middleColumnContentToReturn = middleColumnContent
        rightColumnContentToReturn = rightColumnContent
        listOfWordsLeftToReturn = listOfWordsLeft.toMutableList()
        listOfWordsCenterToReturn = listOfWordsCenter.toMutableList()
        listOfWordsRightToReturn = listOfWordsRight.toMutableList()
        //
        val leftColumnContentIsAServileVerb =
            searchServileVerbs(context, leftColumnContentToReturn, realm)
        if (leftColumnContentIsAServileVerb == context.getString(R.string.is_a_servile_verb)) {
            formToSearchRealm = context.getString(R.string.s1)
            val conjugationOfTheVerb =
                searchVerb(context, leftColumnContentToReturn, formToSearchRealm, realm)
            leftColumnContentToReturn = conjugationOfTheVerb
        } else {
            if (leftColumnContentToReturn != sharedLastPlayer && leftColumnContentToReturn != context.getString(R.string.io)) {
                // adds the corresponding article
                // search if plural
                // if gender male / female
                pluralToSearchRealm = searchPlural(context, leftColumnContentToReturn, realm)
                genderToSearchRealm = searchGender(context, leftColumnContentToReturn, realm)
                val articleToSearch = searchArticle(
                    context,
                    leftColumnContentToReturn,
                    genderToSearchRealm, pluralToSearchRealm, "", realm
                )
                leftColumnContentToReturn = articleToSearch + leftColumnContentToReturn
            }
        }
        // verb group verb
        if (leftColumnContentIsAServileVerb != context.getString(R.string.is_a_servile_verb)) {
            if (!(leftColumnContentToReturn == context.getString(R.string.nessuno)
                        && rightColumnContentToReturn == context.getString(R.string.nessuno))) {
                formToSearchRealm = if (leftColumnContentToReturn == context.getString(R.string.io)) {
                    context.getString(R.string.s1)
                } else {
                    if (leftColumnContentToReturn == context.getString(R.string.la_famiglia)
                        || leftColumnContentToReturn == context.getString(R.string.nessuno)) {
                        context.getString(R.string.p1)
                    } else {
                        if (pluralToSearchRealm != context.getString(R.string.character_y)) {
                            context.getString(R.string.s3)
                        } else {
                            context.getString(R.string.p3)
                        }
                    }
                }
                middleColumnContentVerbInTheInfinitiveForm = middleColumnContentToReturn
                val conjugationOfTheVerb =
                    searchVerb(context, middleColumnContentToReturn, formToSearchRealm, realm)
                middleColumnContentToReturn = conjugationOfTheVerb
            }
        } else {
            middleColumnContentVerbInTheInfinitiveForm = middleColumnContentToReturn
        }
        // verbal group direct object
        if (rightColumnContentToReturn != sharedLastPlayer
            && rightColumnContentToReturn != context.getString(R.string.io)
            && rightColumnContentToReturn != context.getString(R.string.nessuno)
        ) {
            // ricerca complementi
            val complement = searchComplement(context, realm,
                middleColumnContentVerbInTheInfinitiveForm!!, rightColumnContentToReturn)
//            val resultsWordPairs = realm.where(WordPairs::class.java)
//                .beginGroup()
//                .equalTo(context.getString(R.string.word1), middleColumnContentVerbInTheInfinitiveForm)
//                .equalTo(context.getString(R.string.word2), rightColumnContentToReturn)
//                .endGroup()
//                .findAll()
//            val resultsWordPairsSize = resultsWordPairs.size
//            if (resultsWordPairsSize != 0) {
//                val resultWordPairs = resultsWordPairs[0]!!
//                val rightColumnComplement = resultWordPairs.complement
//                if (rightColumnComplement != " " && rightColumnComplement != "") {
            if (complement != "non trovato") {
                middleColumnContentToReturn = "$middleColumnContentToReturn $complement"
                }
            //
            // adds the corresponding article
            // search if plural
            // if gender male / female
            verbOfMovement = searchVerbsOfMovement(
                context,
                middleColumnContentVerbInTheInfinitiveForm,
                realm
            )
            pluralToSearchRealm = searchPlural(context, rightColumnContentToReturn, realm)
            genderToSearchRealm = searchGender(context,
                rightColumnContentToReturn, realm)
            val articleToSearch = searchArticle(
                context,
                rightColumnContentToReturn,
                genderToSearchRealm, pluralToSearchRealm, verbOfMovement, realm
            )
            rightColumnContentToReturn = articleToSearch + rightColumnContentToReturn
//            }
        }
        return ComposesASentenceResults(numberOfWordsChosenToReturn,
            leftColumnContentToReturn,
            middleColumnContentToReturn,
            rightColumnContentToReturn,
            listOfWordsLeftToReturn as ArrayList<String>,
            listOfWordsCenterToReturn as ArrayList<String>,
            listOfWordsRightToReturn as ArrayList<String>
        )
    }
    @JvmStatic
    fun searchComplement(
        context: Context,
        realm: Realm,
        verbInTheInfinitiveForm: String,
        word: String
    ): String {
        var resultsWordPairs = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo(context.getString(R.string.word1), verbInTheInfinitiveForm)
            .equalTo(context.getString(R.string.word2), word)
            .endGroup()
            .findAll()
        var resultsWordPairsSize = resultsWordPairs.size
        if (resultsWordPairsSize != 0) {
            val resultWordPairs = resultsWordPairs[0]!!
            return resultWordPairs.complement!!
        }
        else {
            val classToWhichAWordBelongs = searchesForThePossibleClassToWhichAWordBelongs(context, word, realm)
            resultsWordPairs = realm.where(WordPairs::class.java)
                .beginGroup()
                .equalTo(context.getString(R.string.word1), verbInTheInfinitiveForm)
                .equalTo(context.getString(R.string.word2), classToWhichAWordBelongs)
                .endGroup()
                .findAll()
            resultsWordPairsSize = resultsWordPairs.size
            if (resultsWordPairsSize != 0) {
                val resultWordPairs = resultsWordPairs[0]!!
                return resultWordPairs.complement!!
            }
        }
        return "non trovato"
    }
}