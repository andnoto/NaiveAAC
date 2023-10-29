package com.sampietro.NaiveAAC.activities.Arasaac

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
 * <h1>PictogramsAllToModify</h1>
 *
 * **PictogramsAllToModify**  contains the list of data from the Arasaac which have been corrected because deemed inappropriate.
 */
open class PictogramsAllToModify : RealmObject() {
    /**
     * represent the key of the pictogram relating to the data to be modified.
     */
    private var _id: String? = null
    /**
     * represent the type of modification.
     *
     *
     * modificationType = E id pictogram to exclude
     *
     *
     * modificationType = I id pictogram to include (to implement)
     *
     *
     * modificationType = C id pictogram to change (to implement)
     */
    var modificationType: String? = null
    /**
     * represent the PictogramAll#keyword to be included or changed.
     */
    var keyword: String? = null
    /**
     * represent the PictogramAll#plural to be included or changed.
     */
    var plural: String? = null
    /*
     * getter, setter and other methods
     */
    /**
     * get `_id`.
     *
     * @return _id string data to get
     */
    fun get_id(): String? {
        return _id
    }
    //
    /**
     * set `_id`.
     *
     * @param _id string data to set
     */
    fun set_id(_id: String?) {
        this._id = _id
    }

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
            val FILE_NAME = "pictogramsalltomodify.csv"
            // Grab all data from the DB in question (PictogramsAllToModify):
            val resultsDB = realm.where(
                PictogramsAllToModify::class.java
            ).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header =
                AdvancedSettingsDataImportExportHelper.grabHeader(realm, "PictogramsAllToModify")

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
            //
        }
        //
        /**
         * Import from CSV.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         */
        @JvmStatic
        fun importFromCsvFromInternalStorage(context: Context, realm: Realm) {
            // clear the table
            val daCancellare = realm.where(
                PictogramsAllToModify::class.java
            ).findAll()
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            val FILE_NAME = "pictogramsalltomodify.csv"
            //adding to db
            var br: BufferedReader? = null
            var line = ""
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
                        val oneWord =
                            line.split(cvsSplitBy.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        //
                        realm.beginTransaction()
                        val pictogramsAllToModify = realm.createObject(
                            PictogramsAllToModify::class.java
                        )
                        // set the fields here
                        pictogramsAllToModify.set_id(oneWord[0])
                        pictogramsAllToModify.modificationType = oneWord[1]
                        pictogramsAllToModify.keyword = oneWord[2]
                        pictogramsAllToModify.plural = oneWord[3]
                        realm.commitTransaction()
                    }
                }
            }
        }
    }
}