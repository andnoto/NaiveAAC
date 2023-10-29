package com.sampietro.NaiveAAC.activities.Game.GameParameters

import android.content.Context
import io.realm.RealmObject
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import com.sampietro.NaiveAAC.R
import io.realm.Realm
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * <h1>GameParameters</h1>
 *
 * **GameParameters**  contains games parameters.
 */
open class GameParameters : RealmObject() {
    /**
     * represent the description displayed in the view for game choice.
     */
    var gameName: String? = null
    /**
     * indicates whether the game is active or not.
     *
     *
     * gameActive = A then the game is active
     */
    var gameActive: String? = null
    /**
     * represent the type of icon displayed in the view for game choice.
     *
     *
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     *
     *
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     *
     *
     * gameIconType = AS then , if it exists, gameIconPath refers to assets
     */
    var gameIconType: String? = null
    /**
     * represent the path of icon displayed in the view for game choice.
     */
    var gameIconPath: String? = null
    /**
     * represent the info displayed in the view for game choice.
     */
    var gameInfo: String? = null
    /**
     * represent the java class of the game.
     *
     *
     * gameJavaClass = NAVIGATORE
     * the app displays collections of word images that you can select to form simple sentences
     *
     *
     * gameJavaClass = COMUNICATORE
     * the app displays images (uploaded by the user or Arasaac pictograms) of the words spoken after pressing the listen button
     *
     *
     * gameJavaClass = ELENCHI (under construction)
     * the app displays the images in a list:
     * if the word spoken after pressing the listen button matches the image, you will receive a reward
     *
     *
     * gameJavaClass = PLURALE (under construction)
     * the app displays the images (in the singular and in the plural) of a list:
     * if the word pronounced after pressing the listen key corresponds to the plural, you will receive a reward
     *
     *
     * gameJavaClass = A/DA
     * the app tells and displays the images of a story:
     * at certain points questions are asked by proposing images of the possible answers:
     * if the word pronounced after pressing the listen button is correct, you will receive a reward
     *
     *
     * gameJavaClass = E (under construction)
     * the app displays the images of a list of word pairs, one of which is hidden after a few seconds:
     * if the word spoken after pressing the listen button matches the hidden image, you will receive a reward
     */
    var gameJavaClass: String? = null
    /**
     * represent the parameter of the game.
     */
    var gameParameter: String? = null
    /**
     * indicates whether the game uses video and sound.
     */
    var gameUseVideoAndSound: String? = null
    /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null

    //
    companion object {
        //
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
            val FILE_NAME = "gameparameters.csv"
            // Grab all data from the DB in question :
            val resultsDB = realm.where(
                GameParameters::class.java
            ).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "GameParameters")

            // We write the header to file
            AdvancedSettingsDataImportExportHelper.savBak(context!!, header, FILE_NAME)

            // Now we write all the data corresponding to the fields grabbed above:
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
         * Export the game parameters of a story to CSV.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @param resultsDB RealmResults<GameParameters>
         * @param dir file output directory
         * @see AdvancedSettingsDataImportExportHelper.grabHeader
         * @see AdvancedSettingsDataImportExportHelper.savBak
         * @see AdvancedSettingsDataImportExportHelper.dataProcess
        </GameParameters> */
        @JvmStatic
        fun exporttoCsv(
            context: Context?,
            realm: Realm?,
            resultsDB: RealmResults<GameParameters?>,
            dir: File?
        ) {
//
            val FILE_NAME = "gameparameters.csv"
            // Grab all data from the DB in question :
            //        RealmResults<GameParameters> resultsDB = realm.where(GameParameters.class).findAll();

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm!!, "GameParameters")

            // We write the header to file
            AdvancedSettingsDataImportExportHelper.savBak(context, header, FILE_NAME, dir)

            // Now we write all the data corresponding to the fields grabbed above:
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
                val daCancellare = realm.where(
                    GameParameters::class.java
                ).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "gameparameters.csv"
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
                        if (oneWord[0] != null && oneWord[1] != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null && oneWord[6] != null && oneWord[7] != null && oneWord[8] != null) {
                            if (oneWord[0]!!.length > 0 && oneWord[1]!!.length > 0 && oneWord[2]!!.length > 0 && oneWord[3]!!.length > 0 && oneWord[5]!!.length > 0
                                &&
                                (oneWord[2] != context.getString(R.string.character_s)
                                        || oneWord[2] == context.getString(R.string.character_s) && f!!.exists())
                            ) {
                                //
                                realm.beginTransaction()
                                val gameParameters = realm.createObject(
                                    GameParameters::class.java
                                )
                                // set the fields here
                                gameParameters.gameName = oneWord[0]
                                gameParameters.gameActive = oneWord[1]
                                gameParameters.gameIconType = oneWord[2]
                                gameParameters.gameIconPath = uri
                                gameParameters.gameInfo = oneWord[4]
                                gameParameters.gameJavaClass = oneWord[5]
                                gameParameters.gameParameter = oneWord[6]
                                gameParameters.gameUseVideoAndSound = oneWord[7]
                                gameParameters.fromAssets = oneWord[8]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
        //
        /**
         * Import from CSV the game parameters of a story.
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
            val FILE_NAME = "gameparameters.csv"
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
                        // clear the table
                        if (oneWord[0] != null) {
                            val daCancellare = realm.where(
                                GameParameters::class.java
                            ).equalTo("gameName", oneWord[0]).findAll()
                            realm.beginTransaction()
                            daCancellare.deleteAllFromRealm()
                            realm.commitTransaction()
                        }
                        // checks
                        var f: File? = null
                        var uri: String? = null
                        if (oneWord[3] != null) {
                            if (oneWord[2] == context.getString(R.string.character_s)) {
                                // replace with root of data directory
//                          uri = oneWord[3].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                                val fileName = oneWord[3]!!
                                    .substring(oneWord[3]!!.lastIndexOf("/") + 1)
                                uri = "$rootPath/$fileName"
                                //
                                f = File(uri)
                            } else {
                                uri = oneWord[3]
                            }
                        }
                        if (oneWord[0] != null && oneWord[1] != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null && oneWord[6] != null && oneWord[7] != null && oneWord[8] != null) {
                            if (oneWord[0]!!.length > 0 && oneWord[1]!!.length > 0 && oneWord[2]!!.length > 0 && oneWord[3]!!.length > 0 && oneWord[5]!!.length > 0) //                                &&
                            //                                ((!oneWord[2].equals(context.getString(R.string.character_s)))
                            //                                        || (oneWord[2].equals(context.getString(R.string.character_s))  && f.exists())))
                            {
                                //
                                realm.beginTransaction()
                                val gameParameters = realm.createObject(
                                    GameParameters::class.java
                                )
                                // set the fields here
                                gameParameters.gameName = oneWord[0]
                                gameParameters.gameActive = oneWord[1]
                                gameParameters.gameIconType = oneWord[2]
                                if (oneWord[2] == context.getString(R.string.character_s) && !f!!.exists()) {
                                    val puntoInterrogativoFile =
                                        context.getFileStreamPath("punto interrogativo.png")
                                    uri = puntoInterrogativoFile.absolutePath
                                }
                                gameParameters.gameIconPath = uri
                                gameParameters.gameInfo = oneWord[4]
                                gameParameters.gameJavaClass = oneWord[5]
                                gameParameters.gameParameter = oneWord[6]
                                gameParameters.gameUseVideoAndSound = oneWord[7]
                                gameParameters.fromAssets = oneWord[8]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
    }
}