package com.sampietro.NaiveAAC.activities.Grammar

import android.content.Context
import io.realm.RealmObject
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import com.sampietro.NaiveAAC.R
import io.realm.Realm
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * <h1>ListsOfNames</h1>
 *
 * **ListsOfNames**  contains a list of names classified by a keyword.
 * each name can be associated to an arasaac url or to a file path.
 */
open class ListsOfNames : RealmObject() {
    /**
     * key or class that identifies the list.
     */
    var keyword: String? = null
    /**
     * name belonging to the list identified with the `keyword`.
     */
    var word: String? = null
    /**
     * represent the type of image associated with the `word`.
     *
     *
     * uritype = S then , if it exists, uri refers to storage
     *
     *
     * uritype = A then , if it exists, uri refers to a url of arasaac
     *
     *
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
    var uriType: String? = null
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
    var uri: String? = null
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
            val FILE_NAME = "listsofnames.csv"
            // Grab all data from the DB in question (ListsOfNames):
            val resultsDB = realm.where(ListsOfNames::class.java).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "ListsOfNames")

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
                val daCancellare = realm.where(ListsOfNames::class.java).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "listsofnames.csv"
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
                            line!!.split(cvsSplitBy.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        // checks
                        var f: File? = null
                        var uri: String? = null
                        if (oneWord[3] != null) {
                            if (oneWord[2] == context.getString(R.string.character_s)) {
                                // replace with root of the external storage or data directory
                                uri = oneWord[3]!!
                                    .replaceFirst(
                                        context.getString(R.string.rootdirectory).toRegex(),
                                        rootPath
                                    )
                                //
                                f = File(uri)
                            } else {
                                uri = oneWord[3]
                            }
                        }
                        if (oneWord[0] != null && oneWord[1] != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null) {
                            if (oneWord[0]!!.length > 0 && oneWord[1]!!.length > 0 &&
                                (oneWord[2] != context.getString(R.string.character_s)
                                        || oneWord[2] == context.getString(R.string.character_s) && f!!.exists())
                            ) {
                                //
                                realm.beginTransaction()
                                val listsOfNames = realm.createObject(
                                    ListsOfNames::class.java
                                )
                                // set the fields here
                                listsOfNames.keyword = oneWord[0]
                                listsOfNames.word = oneWord[1]
                                listsOfNames.uriType = oneWord[2]
                                //
                                listsOfNames.uri = uri
                                listsOfNames.fromAssets = oneWord[4]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        } //
    }
}