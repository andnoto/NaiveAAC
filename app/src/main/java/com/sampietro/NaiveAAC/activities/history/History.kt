package com.sampietro.NaiveAAC.activities.history

import android.content.Context
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import io.realm.Realm
import io.realm.RealmObject

/**
 * <h1>History</h1>
 *
 * **History**  it is used for passing complex data between activities and fragments
 * it also contains the history of games for statistical use.
 */
open class History : RealmObject() {
    /**
     * represents the progressive number of the app session that is incremented in main activity.
     */
    var session: String? = null

    /**
     * represents the progressive number of the sentence.
     */
    var phraseNumber: String? = null
    /**
     * represents the date of the sentence.
     */
    var date: String? = null
    /**
     * if wordNumber = 0 then word contains the entire sentence
     * if wordNumber > 0 then wordNumber contains the pass number of the word and
     * word contains the word itself
     */
    var wordNumber: String? = null
    /**
     * type = 1 word is a noun without plural
     * type = 2 word is a noun with plural
     * type = 3 word is a verb
     */
    var type: String? = null
    /**
     * word or sentence
     */
    var word: String? = null
    /**
     * plural = N then word is not a plural
     * plural = Y then word is a plural
     */
    var plural: String? = null
    /**
     * uritype = A then , if it exists, image corresponding to the word refers to Arasaac
     * uritype = S then , if it exists, image corresponding to the word refers to storage
     */
    var uriType: String? = null
    /**
     * contains the file path or the url of arasaac (originally contained the uri).
     */
    var uri: String? = null
    /**
     * refers to a video key in the videos table .
     */
    var video: String? = null
    /**
     * refers to a sound key in the sounds table .
     */
    var sound: String? = null
    /**
     * indicates whether sound replaces TTS (Y or N).
     */
    var soundReplacesTTS: String? = null

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
            val FILE_NAME = "history.csv"
            // Grab all data from the DB in question :
            val resultsDB = realm.where(
                History::class.java
            ).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "History")

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
    }
}