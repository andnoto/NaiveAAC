package com.sampietro.NaiveAAC.activities.Phrases

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
 * <h1>Phrases</h1>
 *
 * **Phrases**  contains phrases used by game2 and other games similarly eliza software.
 * Refer to [wikipedia](https://en.wikipedia.org/wiki/ELIZA)
 */
open class Phrases : RealmObject() {
    /**
     * represent the type of phrases.
     *
     *
     * tipo = welcome phrase first part which is pronounced before the player's name
     *
     *
     * tipo = welcome phrase second part which is pronounced after the player's name
     *
     *
     * tipo = reminder phrase which is pronounced if nothing is said several times after pressing the listen button
     *
     *
     * tipo = reminder phrase plural which is pronounced if nothing is said several times after pressing the listen button
     * (if the last noun of the sentence is plural)
     *
     *
     * the last noun of the sentence is added to the reminder phrase and proceeding backwards are added
     * also all related complements to the name
     */
    var tipo: String? = null
    /**
     * phrase.
     */
    var descrizione: String? = null
   /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null

    /*
     * getter, setter and other methods
     */
    //
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
//
            val FILE_NAME = "phrases.csv"
            // Grab all data from the DB in question :
            val resultsDB = realm.where(Phrases::class.java).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Phrases")

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
                val daCancellare = realm.where(Phrases::class.java).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val FILE_NAME = "phrases.csv"
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
                        // added otherwise if the line ends with a comma it does not consider the last subdivision of the csv
                        val lineLastCharacter = line!!.substring(line!!.length - 1)
                        if (lineLastCharacter == cvsSplitBy) {
                            line = "$line "
                        }
                        // use comma as separator
                        val oneWord: Array<String?> =
                            line!!.split(cvsSplitBy.toRegex()).toTypedArray()
                        // checks
                        if (oneWord[0] != null && oneWord[0]!!.length > 0 && oneWord[1] != null && oneWord[1]!!.length > 0 && oneWord[2] != null) {
                            realm.beginTransaction()
                            val phrases = realm.createObject(Phrases::class.java)
                            // set the fields here
                            phrases.tipo = oneWord[0]
                            phrases.descrizione = oneWord[1]
                            phrases.fromAssets = oneWord[2]
                            realm.commitTransaction()
                        }
                    }
                }
            }
        } //
    }
}