package com.sampietro.NaiveAAC.activities.Graphics

import android.content.Context
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * <h1>Videos</h1>
 *
 * **Videos**  contains filepaths for videos.
 *
 * @version     4.0, 09/09/2023
 */
open class Videos : RealmObject() {
    /**
     * search key for premium videos and thumbnail videos for the choice of the game.
     * descrizione = prize for default premium video
     * descrizione = GameParameters#gameName for thumbnail videos for the choice of the game
     */
    @Index
    var descrizione: String? = null
    /**
     * contains the file path (originally contained the uri).
     */
    var uri: String? = null
    /**
     * contains copyright information.
     */
    var copyright: String? = null
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
//
            val FILE_NAME = "videos.csv"
            // Grab all data from the DB in question :
            val resultsDB = realm.where(Videos::class.java).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Videos")

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
         * Export to CSV with set.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         * Refer to [stackoverflow](https://stackoverflow.com/questions/32066648/realm-query-with-list)
         * answer of [Sagar Chapagain](https://stackoverflow.com/users/3710341/sagar-chapagain)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @param videosDescriptionSet HashSet<String> to export
         * @param dir File with the output folder
         * @see AdvancedSettingsDataImportExportHelper.grabHeader
         * @see AdvancedSettingsDataImportExportHelper.savBak
         * @see AdvancedSettingsDataImportExportHelper.dataProcess
        </String> */
        @JvmStatic
        fun exporttoCsv(
            context: Context?,
            realm: Realm,
            videosDescriptionSet: HashSet<String?>,
            dir: File?
        ) {
//
            val FILE_NAME = "videos.csv"
            // Grab all data from the DB in question :
            val videosDescriptionArray = arrayOfNulls<String>(videosDescriptionSet.size)
            videosDescriptionSet.toArray(videosDescriptionArray)
            val realmQuery = realm.where(Videos::class.java)
                .`in`("descrizione", videosDescriptionArray)
            val resultsDB = realmQuery.findAll()
            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Videos")

            // We write the header to file
            AdvancedSettingsDataImportExportHelper.savBak(context, header, FILE_NAME, dir)

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
                            dir,
                            true
                        )
                    } else {
                        AdvancedSettingsDataImportExportHelper.savBak(
                            context,
                            dataP,
                            FILE_NAME,
                            dir,
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
                val daCancellare = realm.where(Videos::class.java).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "videos.csv"
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
                        //
                        // checks
                        if (oneWord[1] != null) {
                            // replace with root of the external storage or data directory
                            val uri = oneWord[1]!!
                                .replaceFirst("rootdirectory".toRegex(), rootPath)
                            //
                            val f = File(uri)
                            if (oneWord[0] != null && oneWord[0]!!.length > 0 && oneWord[2] != null && oneWord[3] != null && f.exists()) {
                                realm.beginTransaction()
                                val images = realm.createObject(Videos::class.java)
                                // set the fields here
                                images.descrizione = oneWord[0]
                                images.uri = uri
                                images.copyright = oneWord[2]
                                images.fromAssets = oneWord[3]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
        //
        /**
         * Import from CSV the videos of a story.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         */
        @JvmStatic
        fun importFromCsvFromInternalStorage(context: Context, realm: Realm) {
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "videos.csv"
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
                        //
                        // checks
                        if (oneWord[1] != null) {
                            // clear the table
                            val daCancellare =
                                realm.where(Videos::class.java).equalTo("descrizione", oneWord[0])
                                    .findAll()
                            realm.beginTransaction()
                            daCancellare.deleteAllFromRealm()
                            realm.commitTransaction()
                            // replace with root of data directory
//                      String uri = oneWord[1].replaceFirst("rootdirectory", rootPath);
                            val fileName = oneWord[1]!!
                                .substring(oneWord[1]!!.lastIndexOf("/") + 1)
                            val uri = "$rootPath/$fileName"
                            //
                            val f = File(uri)
                            if (oneWord[0] != null && oneWord[0]!!.length > 0 && oneWord[2] != null && oneWord[3] != null && f.exists()) {
                                realm.beginTransaction()
                                val images = realm.createObject(Videos::class.java)
                                // set the fields here
                                images.descrizione = oneWord[0]
                                images.uri = uri
                                images.copyright = oneWord[2]
                                images.fromAssets = oneWord[3]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
    }
}