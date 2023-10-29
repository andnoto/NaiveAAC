package com.sampietro.NaiveAAC.activities.Grammar

import android.content.Context
import io.realm.RealmObject
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import com.sampietro.NaiveAAC.R
import io.realm.Realm
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * <h1>GrammaticalExceptions</h1>
 *
 * **GrammaticalExceptions**  contains grammatical exceptions.
 * in normal cases given the keyword Arasaac the masculine or feminine gender comes identified approximately as follows:
 * if the plural ends in "i" -> masculine gender;
 * if the singular ends in "a" and the plural in "e" -> feminine gender.
 * in normal cases given the keyword Arasaac, the gender, the singular / plural category and the type of the movement verb, if any
 * after checking for any exceptions the article or the complement of motion come identified approximately as follows:
 * if singular and the keyword starts with a vowel -> l'
 * if masculine singular, the keyword starts with consonant s followed by consonant or starts with consonant z -> lo
 * if masculine singular and the keyword begins with a consonant in the other cases -> il
 * if feminine singular and the keyword starts with a consonant -> la
 * if masculine plural and the keyword starts with a vowel -> gli
 * if masculine plural and the keyword starts with a consonant -> i
 * if plural feminine -> le
 */
open class GrammaticalExceptions : RealmObject() {
    /**
     * word or phrase from the Arasaac /pictograms/all API.
     */
    var keyword: String? = null
    /**
     * represent the type of exception.
     *
     *
     * exceptionType    = Is an auxiliary verb
     *
     *
     * exceptionType    = Is a servile verb
     *
     *
     * exceptionType    = Is a verb of movement to place
     *
     *
     * exceptionType    = Is a verb of movement from place
     *
     *
     * exceptionType    = Article
     *
     *
     * exceptionType    = Is a negation adverb
     */
    var exceptionType: String? = null
    /**
     * if exceptionType    = Article then exception1 = article
     * if exceptionType    = Is a negation adverb then exception1 = description of the corresponding image in the images class
     */
    var exception1: String? = null
    /**
     * if exceptionType    = Article then exception2 = complement of motion from place
     */
    var exception2: String? = null
    /**
     * if exceptionType    = Article then exception2 = complement of motion to place
     */
    var exception3: String? = null
    /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null
    //
    companion object {
        //
        /**
         * Export to CSV.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @see AdvancedSettingsDataImportExportHelper.grabHeader
         * @see AdvancedSettingsDataImportExportHelper.savBak
         * @see AdvancedSettingsDataImportExportHelper.dataProcess
         */
        @JvmStatic
        fun exporttoCsv(context: Context?, realm: Realm) {
            val FILE_NAME = "grammaticalexceptions.csv"
            // Grab all data from the DB in question (ListsOfNames):
            val resultsDB = realm.where(
                GrammaticalExceptions::class.java
            ).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header =
                AdvancedSettingsDataImportExportHelper.grabHeader(realm, "GrammaticalExceptions")

            // We write the header to file
            AdvancedSettingsDataImportExportHelper.savBak(context!!, header, FILE_NAME)

            // Now we write all the data corresponding to the fields grabbed above:
            //
            val count = resultsDB.size
            if (count != 0) {
                var irrh = 0
                while (irrh < count) {
                    val taskitems = resultsDB[irrh]!!
                    //
                    dataP = taskitems.toString()
                    // We process the data obtained and add commas and formatting:
                    dataP = AdvancedSettingsDataImportExportHelper.dataProcess(dataP)

                    // Workaround to remove the last comma from final string
                    val total = dataP.length - 1
                    dataP = dataP.substring(0, total)

                    // Workaround to remove the last line feed from final row
                    // We write the data to file
                    if (irrh == count - 1) {
                        AdvancedSettingsDataImportExportHelper.savBak(
                            context,
                            dataP,
                            FILE_NAME,
                            true
                        )
                    } else {
                        AdvancedSettingsDataImportExportHelper.savBak(
                            context,
                            dataP,
                            FILE_NAME,
                            false
                        )
                    }
                    irrh++
                }
            }
        }
        //
        /**
         * Import from CSV.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @param mode string import mode (Append or Replace)
         */
        @JvmStatic
        fun importFromCsvFromInternalStorage(context: Context, realm: Realm, mode: String?) {
            if (mode == "Replace") {
                // clear the table
                val daCancellare = realm.where(
                    GrammaticalExceptions::class.java
                ).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val FILE_NAME = "grammaticalexceptions.csv"
            //adding to db
            var br: BufferedReader? = null
            var line: String? = ""
            val cvsSplitBy = context.getString(R.string.character_comma)
            //
            try {
                br = BufferedReader(
                    InputStreamReader(
                        context.openFileInput(FILE_NAME),
                        StandardCharsets.UTF_8
                    )
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (br != null) {
                var isTheFirstLine = true
                while (true) {
                    try {
                        if (br.readLine().also { line = it } == null) break
                    } catch (ioException: IOException) {
                        ioException.printStackTrace()
                    }
                    // exclusion of the first line
                    if (isTheFirstLine) {
                        isTheFirstLine = false
                    } else {
                        // use comma as separator
                        val oneWord: Array<String?> =
                            line!!.split(cvsSplitBy.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        //
                        if (oneWord[0] != null && oneWord[0]!!.length > 0 && oneWord[1] != null && oneWord[1]!!.length > 0 && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null) {
                            realm.beginTransaction()
                            val grammaticalExceptions = realm.createObject(
                                GrammaticalExceptions::class.java
                            )
                            // set the fields here
                            grammaticalExceptions.keyword = oneWord[0]
                            grammaticalExceptions.exceptionType = oneWord[1]
                            grammaticalExceptions.exception1 = oneWord[2]
                            grammaticalExceptions.exception2 = oneWord[3]
                            grammaticalExceptions.exception3 = oneWord[4]
                            grammaticalExceptions.fromAssets = oneWord[5]
                            realm.commitTransaction()
                        }
                    }
                }
            }
        } //
    }
}