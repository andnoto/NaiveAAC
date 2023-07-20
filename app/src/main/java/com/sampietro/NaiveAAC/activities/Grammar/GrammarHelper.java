package com.sampietro.NaiveAAC.activities.Grammar;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAll;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.history.History;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GrammarHelper</h1>
 *
 * <p><b>GrammarHelper</b> utility class for Italian grammar.</p>
 *
 * @version     1.3, 05/05/22
 */
public class GrammarHelper {
    /**
     * splits a sentence into an array of words
     *
     * @param eText string containing the sentence to split
     */
    public static String[] splitString(String eText)
    {
        String[] arr = eText.split(" ");
        return arr;
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
     * @deprecated  replaced by {@link #searchArticle(String,String,String,String,Realm)}
     */
    public static String searchArticle(String kKeyword, String gender, String plural)
    {
        String articleToSearch = "";
        String keywordFirstChar=kKeyword.substring(0,1);
        if ((plural.equals("N")) &&
                ((keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"))))
                articleToSearch = "l'";
        if ((plural.equals("N")) && (gender.equals("M")) &&
                !((keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"))))
                articleToSearch = "il ";
        if ((plural.equals("N")) && (gender.equals("F")) &&
                !((keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"))))
                articleToSearch = "la ";
        if ((plural.equals("Y")) && (gender.equals("M")) &&
                ((keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"))))
                articleToSearch = "gli ";
        if ((plural.equals("Y")) && (gender.equals("M")) &&
                !((keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"))))
                articleToSearch = "i ";
        if ((plural.equals("Y")) && (gender.equals("F")))
                articleToSearch = "le ";

        return articleToSearch;
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
     * @param kKeyword string containing the keyword Arasaac
     * @param gender string containing the gender masculine (gender = M) or feminine (gender = F) of the keyword Arasaac
     * @param plural string containing the singular (plural = N) / plural (plural = S) category of the keyword Arasaac
     * @param verbOfMovement string which indicates whether the keyword Arasaac is preceded by a verb of motion
     * @param realm realm
     * @return string the article or the complement of motion
     * @see GrammaticalExceptions
     */
    public static String searchArticle(String kKeyword, String gender, String plural,
                                       String verbOfMovement, Realm realm)
    {
        String articleToSearch = "";
        //
        RealmResults<GrammaticalExceptions> results =
                realm.where(GrammaticalExceptions.class)
                        .beginGroup()
                        .equalTo("keyword", kKeyword)
                        .equalTo("exceptionType", "Article")
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            GrammaticalExceptions result = results.get(0);
            if (result != null) {
                switch(verbOfMovement){
                    case "Is a verb of movement to place":
                        articleToSearch = result.getException3() + " ";
                        break;
                    case "Is a verb of movement from place":
                        articleToSearch = result.getException2() + " ";
                        break;
                    default:
                        articleToSearch = result.getException1() + " ";
                }
            return articleToSearch;
            }
        }
        //
        String keywordFirstChar=kKeyword.substring(0,1);
        String keywordSecondChar=kKeyword.substring(1,1);
        boolean keywordFirstCharVocale = (keywordFirstChar.equals("a")) || (keywordFirstChar.equals("e")) ||
                (keywordFirstChar.equals("i")) || (keywordFirstChar.equals("o")) ||
                (keywordFirstChar.equals("u"));
        boolean keywordSecondCharVocale = (keywordSecondChar.equals("a")) || (keywordSecondChar.equals("e")) ||
                (keywordSecondChar.equals("i")) || (keywordSecondChar.equals("o")) ||
                (keywordSecondChar.equals("u"));
        //
        if ((plural.equals("N")) &&
                keywordFirstCharVocale) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "all'";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dall'";
                    break;
                default:
                    articleToSearch = "l'";
            }
            return articleToSearch;
        }
        //
        if (plural.equals("N") && gender.equals("M") &&
                (keywordFirstChar.equals("z") ||
                        (keywordFirstChar.equals("s") && !keywordSecondCharVocale))) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "allo ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dallo ";
                    break;
                default:
                    articleToSearch = "lo ";
            }
            return articleToSearch;
        }
        //
        if ((plural.equals("N")) && (gender.equals("M")) &&
                !keywordFirstCharVocale) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "al ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dal ";
                    break;
                default:
                    articleToSearch = "il ";
            }
            return articleToSearch;
        }
        //
        if ((plural.equals("N")) && (gender.equals("F")) &&
                !keywordFirstCharVocale) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "alla ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dalla ";
                    break;
                default:
                    articleToSearch = "la ";
            }
            return articleToSearch;
        }
        //
        if ((plural.equals("Y")) && (gender.equals("M")) &&
                keywordFirstCharVocale) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "agli ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dagli ";
                    break;
                default:
                    articleToSearch = "gli ";
            }
            return articleToSearch;
        }
        //
        if ((plural.equals("Y")) && (gender.equals("M")) &&
                !keywordFirstCharVocale) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "ai ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dai ";
                    break;
                default:
                    articleToSearch = "i ";
            }
            return articleToSearch;
        }
        //
        if ((plural.equals("Y")) && (gender.equals("F"))) {
            switch(verbOfMovement){
                case "Is a verb of movement to place":
                    articleToSearch = "alle ";
                    break;
                case "Is a verb of movement from place":
                    articleToSearch = "dalle ";
                    break;
                default:
                    articleToSearch = "le ";
            }
            return articleToSearch;
        }
        //
        return articleToSearch;
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
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string the gender
     * @see PictogramsAll
     */
    public static String searchGender(String kKeyword, Realm realm)
    {
        String genderToSearchRealm = "non trovato";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class)
                        .beginGroup()
                        .equalTo("keyword", kKeyword)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            PictogramsAll result = results.get(0);
            if (result != null) {
                String keywordLastChar = kKeyword.substring(kKeyword.length() - 1);
                String plural = result.getKeywordPlural();
                String pluralLastChar = plural.substring(plural.length() - 1);
                if (pluralLastChar.equals("i"))
                {
                    genderToSearchRealm = "M";
                }
                else
                {
                    if (pluralLastChar.equals("e") && keywordLastChar.equals("a"))
                    {
                        genderToSearchRealm = "F";
                    }
                }
            }
        }
        return genderToSearchRealm;
    }
    // data la keyword Arasaac restituisce
    // se l'argomento si riferisce ad un plurale
    /**
     * given the keyword Arasaac returns if the argument refers to a plural
     *
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string plural
     * @see PictogramsAll
     */
    public static String searchPlural(String kKeyword, Realm realm)
    {
        String pluralToSearchRealm = "non trovato";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class)
                        .beginGroup()
                        .equalTo("keyword", kKeyword)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            PictogramsAll result = results.get(0);
            if (result != null) {
                pluralToSearchRealm = result.getPlural();
            }
        }
        return pluralToSearchRealm;
    }
    // dato l'identificativo e la keyword Arasaac restituisce
    // se gli argomenti si riferiscono ad un plurale
    /**
     * given the identifier and the keyword Arasaac returns if the arguments refer to a plural
     *
     * @param k_Id string containing the identifier Arasaac
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string plural
     * @see PictogramsAll
     */
    public static String searchPlural(String k_Id, String kKeyword, Realm realm)
    {
        String pluralToSearchRealm = "non trovato";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class)
                        .beginGroup()
                        .equalTo("_id", k_Id)
                        .and()
                        .equalTo("keyword", kKeyword)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            PictogramsAll result = results.get(0);
            if (result != null) {
                pluralToSearchRealm = result.getPlural();
            }
        }
        return pluralToSearchRealm;
    }
    // dato l'identificativo e la keyword Arasaac restituisce
    // l'eventuale plurale corrispondente
    /**
     * given the identifier and the keyword Arasaac returns any corresponding plural
     *
     * @param k_Id string containing the identifier Arasaac
     * @param kKeyword string containing the keyword Arasaac
     * @param realm realm
     * @return string corresponding plural
     * @see PictogramsAll
     */
    public static String searchKeywordPlural(String k_Id, String kKeyword, Realm realm)
    {
        String pluralToSearchRealm = "non trovato";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class)
                        .beginGroup()
                        .equalTo("_id", k_Id)
                        .and()
                        .equalTo("keyword", kKeyword)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            PictogramsAll result = results.get(0);
            if (result != null) {
                pluralToSearchRealm = result.getKeywordPlural();
            }
        }
        return pluralToSearchRealm;
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
     * @param k string containing the identifier Arasaac
     * @param realm realm
     * @return string with the type of word Arasaac
     * @see PictogramsAll
     */
    public static String searchType(String k, Realm realm)
    {
        String typeToSearchRealm = "non trovato";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class).equalTo("keyword", k).findAll();
        int count = results.size();
        if (count != 0) {
            //
            PictogramsAll result = results.get(0);
            if (result != null) {
                typeToSearchRealm = result.getType();
            }
        }
        return typeToSearchRealm;
    }
    // data una coniugazione restituisce l'infinito
    /**
     * given a conjugation returns the infinitive
     *
     * @param k string containing the conjugation
     * @param realm realm
     * @return string with the infinitive
     * @see Verbs
     */
    public static String searchVerb(String k, Realm realm)
    {
        String verbToSearchRealm = "non trovato";
        //
        RealmResults<Verbs> results =
                realm.where(Verbs.class).equalTo("conjugation", k).findAll();
        int count = results.size();
        if (count != 0) {

            Verbs result = results.get(0);
            if (result != null) {
                verbToSearchRealm = result.getInfinitive();
            }
        }
        return verbToSearchRealm;
    }
    // dato l'infinito , la forma 1a, 2a o 3a persona singolare o plurale
    // restituisce la coniugazione all'indicativo presente
    /**
     * given the infinitive, the 1st, 2nd or 3rd person singular or plural form
     * returns the conjugation to the present indicative
     * <p>
     * form=s1 first person singular
     * <p>
     * form=s2 second person singular
     * <p>
     * form=s3 third person singular
     * <p>
     * form=p1 first person plural
     * <p>
     * form=p2 second person plural
     * <p>
     * form=p3 third person plural
     *
     * @param k string containing the infinitive
     * @param form string containing the form
     * @param realm realm
     * @return string with the conjugation to the present indicative
     * @see Verbs
     */
    public static String searchVerb(String k,String form, Realm realm)
    {
        String verbToSearchRealm = "non trovato";
        //
        RealmResults<Verbs> results =
                realm.where(Verbs.class)
                        .beginGroup()
                        .equalTo("infinitive", k)
                        .equalTo("group", "indicative/present")
                        .equalTo("form", form)
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            Verbs result = results.get(0);
            if (result != null) {
                verbToSearchRealm = result.getConjugation();
            }
        }
        return verbToSearchRealm;
    }
    // dato l'infinito restituisce se si tratta di un verbo di movimento
    /**
     * given the infinitive, it returns if it is a verb of movement
     *
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is a verb of movement
     * @see GrammaticalExceptions
     */
    public static String searchVerbsOfMovement(String k, Realm realm)
    {
        String verbToSearchRealm = "non trovato";
        //
        RealmResults<GrammaticalExceptions> results =
                realm.where(GrammaticalExceptions.class)
                        .beginGroup()
                        .equalTo("keyword", k)
                        .equalTo("exceptionType", "Is a verb of movement to place")
                        .endGroup()
                        .or()
                        .beginGroup()
                        .equalTo("keyword", k)
                        .equalTo("exceptionType", "Is a verb of movement from place")
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            GrammaticalExceptions result = results.get(0);
            if (result != null) {
                verbToSearchRealm = result.getExceptionType();
            }
        }
        return verbToSearchRealm;
    }
    // dato l'infinito restituisce se si tratta di un verbo ausiliare
    /**
     * given the infinitive, it returns if it is an auxiliary verb
     *
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is an auxiliary verb
     * @see GrammaticalExceptions
     */
    public static String searchAuxiliaryVerbs(String k, Realm realm)
    {
        String verbToSearchRealm = "non trovato";
        //
        RealmResults<GrammaticalExceptions> results =
                realm.where(GrammaticalExceptions.class)
                        .beginGroup()
                        .equalTo("keyword", k)
                        .equalTo("exceptionType", "Is an auxiliary verb")
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            GrammaticalExceptions result = results.get(0);
            if (result != null) {
                verbToSearchRealm = result.getExceptionType();
            }
        }
        return verbToSearchRealm;
    }
    // dato l'infinito restituisce se si tratta di un verbo servile
    /**
     * given the infinitive, it returns if it is a servile verb
     *
     * @param k string containing the infinitive
     * @param realm realm
     * @return string with if it is a servile verb
     * @see GrammaticalExceptions
     */
    public static String searchServileVerbs(String k, Realm realm)
    {
        String verbToSearchRealm = "non trovato";
        //
        RealmResults<GrammaticalExceptions> results =
                realm.where(GrammaticalExceptions.class)
                        .beginGroup()
                        .equalTo("keyword", k)
                        .equalTo("exceptionType", "Is a servile verb")
                        .endGroup()
                        .findAll();
        int count = results.size();
        if (count != 0) {
            GrammaticalExceptions result = results.get(0);
            if (result != null) {
                verbToSearchRealm = result.getExceptionType();
            }
        }
        return verbToSearchRealm;
    }
    // data una stringa restituisce se contiene un avverbio di negazione
    /**
     * given a string, if it contains a negation adverb
     * it returns description of the corresponding image in the images class
     *
     * @param k string
     * @param realm realm
     * @return string with if it contains a negation adverb
     * @see GrammaticalExceptions
     */
    public static String searchNegationAdverb(String k, Realm realm)
    {
        String NegationAdverb = "non trovato";
        //
        // decomposes k
        String[] arrWords = GrammarHelper.splitString(k);
        //
        int arrWordsLength=arrWords.length;
        int i=0;
        while(i < arrWordsLength) {
            RealmResults<GrammaticalExceptions> results =
                    realm.where(GrammaticalExceptions.class)
                            .beginGroup()
                            .equalTo("keyword", arrWords[i])
                            .equalTo("exceptionType", "Is a negation adverb")
                            .endGroup()
                            .findAll();
            int count = results.size();
            if (count != 0) {
                GrammaticalExceptions result = results.get(0);
                if (result != null) {
                    NegationAdverb = result.getException1();
                    break;
                }
            }
            i++;
        }
        return NegationAdverb;
    }
    // data una parola controlla se si tratta di un complemento al nome
    /**
     * given a word, check if it is a complement to the noun
     *
     * @param k string containing the word
     * @param realm realm
     * @return string with if it is a complement to the noun
     * @see ComplementsOfTheName
     */
    public static String searchComplement(String k, Realm realm)
    {
        String complementToSearchRealm = "non trovato";
        //
        RealmResults<ComplementsOfTheName> results =
                realm.where(ComplementsOfTheName.class).equalTo("keyword", k).findAll();
        int count = results.size();
        if (count != 0) {
            ComplementsOfTheName result = results.get(0);
            if (result != null) {
                complementToSearchRealm = result.getKeyword();
            }
        }
        return complementToSearchRealm;
    }
    /**
     * in game2 it creates a response phrase to the input received via speech recognizer
     *
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with a response phrase
     * @see #lookForTheLastPieceOfTheSentence
     * @see #verifyIfLastWordOfTheSentenceIsPlural
     * @see Phrases
     */
    public static String lookForTheAnswerToLastPieceOfTheSentence
            (int sharedLastPhraseNumber, Realm realm)
    {
        String answerToLastPieceOfTheSentence = " ";
        answerToLastPieceOfTheSentence =
                lookForTheLastPieceOfTheSentence(sharedLastPhraseNumber, realm);
        //
        String lastWordOfTheSentencePlural = " ";
        lastWordOfTheSentencePlural =
                verifyIfLastWordOfTheSentenceIsPlural(sharedLastPhraseNumber, realm);
        //
        Phrases phraseToSearch;
        if (lastWordOfTheSentencePlural.equals("Y"))
                {
                phraseToSearch = realm.where(Phrases.class)
                    .equalTo("tipo", "reminder phrase plural")
                    .findFirst(); }
                else
                {
                phraseToSearch = realm.where(Phrases.class)
                        .equalTo("tipo", "reminder phrase")
                        .findFirst(); }
        if (phraseToSearch != null)   {
                answerToLastPieceOfTheSentence = phraseToSearch.getDescrizione()
                        + " " + answerToLastPieceOfTheSentence;
                }
                else
                {
                    answerToLastPieceOfTheSentence = " ";
                }
        return answerToLastPieceOfTheSentence;
    }
    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence to get the last part of the sentence
     *
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with the last part of the sentence
     * @see #lookForTheAnswerToLastPieceOfTheSentence
     * @see History
     * @see #SearchLastWordOfTheSentence
     * @see #splitString
     * @see #searchComplement
     */
    public static String lookForTheLastPieceOfTheSentence(int sharedLastPhraseNumber, Realm realm)
    {
    // READS REALM HISTORY
    // considering on history the whole sentence instead of the single words
    String lastPieceOfTheSentence = " ";
    RealmResults<History> results =
            realm.where(History.class).equalTo("phraseNumber",
                    String.valueOf(sharedLastPhraseNumber)).findAll();
    int count = results.size();
    if (count != 0) {
        History result = results.get(0);
        String sentence = result.getWord();
        // search for the last noun in the sentence
        String lastWordOfTheSentence =
        SearchLastWordOfTheSentence (sharedLastPhraseNumber, realm);
        // from the last noun of the sentence it proceeds backwards including all the complements to the noun
        sentence = sentence.toLowerCase();
        //
        String[] arrSentence = splitString(sentence);
        int arrSentenceLength=arrSentence.length;
        int i=arrSentenceLength-1;
        //
        while((i >= 0  )) {
            if (!arrSentence[i].equals(lastWordOfTheSentence))
                { lastPieceOfTheSentence = arrSentence[i] + lastPieceOfTheSentence; }
                else
                {
                lastPieceOfTheSentence = arrSentence[i] + lastPieceOfTheSentence;
                int ii=i-1;
                String complement;
                while((ii >= 0  ))
                    {
                        complement = searchComplement(arrSentence[ii], realm);
                        if (complement.equals("non trovato"))
                            { break; }
                            else
                            { lastPieceOfTheSentence = arrSentence[ii] + lastPieceOfTheSentence; }
                        ii--;
                    }
                break;
                }
            i--;
        }
    }
        return lastPieceOfTheSentence;
    }
    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence to verify if last word of the sentence is plural
     *
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with if last word of the sentence is plural
     * @see #lookForTheAnswerToLastPieceOfTheSentence
     * @see History
     */
    public static String verifyIfLastWordOfTheSentenceIsPlural(int sharedLastPhraseNumber, Realm realm)
    {
        // scroll the sentence backwards excluding verbs (type = 3)
        String lastWordOfTheSentencePlural = " ";
        RealmResults<History> results =
                realm.where(History.class).
                        equalTo("phraseNumber",
                                String.valueOf(sharedLastPhraseNumber)).findAll();
        int count = results.size();
        if (count != 0) {
            int i=count-1;
            while(i > 0  ) {
                History result = results.get(i);
                assert result != null;
                String type = result.getType();
                if (type.equals("1") || type.equals("2")) {
                    // non mette il plurale nello storico
                    lastWordOfTheSentencePlural = result.getPlural();
                    break;
                }
                i--;
            }
        }
        return lastWordOfTheSentencePlural;
    }
    /**
     * used by lookForTheAnswerToLastPieceOfTheSentence via lookForTheLastPieceOfTheSentence to
     * search last word of the sentence
     *
     * @param sharedLastPhraseNumber int containing last phrase number
     * @param realm realm
     * @return string with the last word of the sentence
     * @see #lookForTheAnswerToLastPieceOfTheSentence
     * @see History
     */
    public static String SearchLastWordOfTheSentence(int sharedLastPhraseNumber, Realm realm)
    {
        // scroll the sentence backwards excluding verbs (type = 3)
        String lastWordOfTheSentence = " ";
        RealmResults<History> results =
                realm.where(History.class).
                        equalTo("phraseNumber", String.valueOf(sharedLastPhraseNumber)).findAll();
        int count = results.size();
        if (count != 0) {
            int i=count-1;
            while(i > 0  ) {
                History result = results.get(i);
                assert result != null;
                String type = result.getType();
                if (type.equals("1") || type.equals("2")) {
                    lastWordOfTheSentence = result.getWord();
                    break;
                }
                i--;
            }
        }
        return lastWordOfTheSentence;
    }
    /**
     * check if there is a correspondence between two sentences considering a margin of allowed errors
     *
     * @param phrase1 string containing the first sentence
     * @param phrase2 string containing the second sentence
     * @param percentualeErroriAmmessa  int containing the percentage of errors allowed
     * @return boolean true if the two sentences match
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean thereIsACorrespondenceWithAnAllowedMarginOfError(String phrase1, String phrase2, int percentualeErroriAmmessa)
    {
        String alphabet = "0123456789abcdefghijklmnopqrstuvwxyz";
        int alphabetLengt = alphabet.length();
        //
        char[] phrase1Array = phrase1.toCharArray();
        int[] phrase1countArray = new int[36];
        for (char x : phrase1Array) {
            for (int i = 0; i < alphabetLengt; i++) {
                if (alphabet.charAt(i) == x) {
                    phrase1countArray[i]++;
                }
            }
        }
        //
        char[] phrase2Array = phrase2.toCharArray();
        int[] phrase2countArray = new int[36];
        for (char x : phrase2Array) {
            for (int i = 0; i < alphabetLengt; i++) {
                if (alphabet.charAt(i) == x) {
                    phrase2countArray[i]++;
                }
            }
        }
        //
        int differenze = 0;
        for (int i = 0; i < alphabetLengt; i++) {
            if (phrase1countArray[i] > phrase2countArray[i]) {
                differenze = differenze + (phrase1countArray[i] - phrase2countArray[i]);
            }
            else {
                differenze = differenze + (phrase2countArray[i] - phrase1countArray[i]);
            }
        }
        //
        int percentualedifferenze = differenze*100/2/phrase2.replaceAll("\\s+","").length();
        //
        return percentualedifferenze <= percentualeErroriAmmessa;
    }
}

