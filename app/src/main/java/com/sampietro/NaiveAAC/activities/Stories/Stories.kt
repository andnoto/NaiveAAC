package com.sampietro.NaiveAAC.activities.Stories

import android.content.Context
import io.realm.RealmObject
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import com.sampietro.NaiveAAC.R
import io.realm.Realm
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * <h1>Stories</h1>
 *
 * **Stories**  contains stories identified by a keyword.
 */
open class Stories : RealmObject() {
    /**
     * key that identifies the story.
     */
    var story: String? = null
    /**
     * order number of the sentence in a given story.
     */
    var phraseNumberInt = 0
    /**
     * order number of the word in a given sentence or the entire sentence or a question / answer related to the phrase or
     * the topic of the sentence.
     *
     *
     * if wordNumber = 0 then word contains the entire sentence
     *
     *
     * if wordNumber > 0 and < 99 then wordNumber contains the pass number of the word and word contains the word itself
     *
     *
     * if wordNumber = 99 then word contain a question related to the phrase
     *
     *
     * if wordNumber = 999 the word contain the answer related to the phrase
     *
     *
     * if wordNumber = 9999 the word contain the topic of the sentence
     */
    var wordNumberInt = 0
    /**
     * order number of the word in a given story
     *
     *
     */
    var wordNumberIntInTheStory = 0
    /**
     * sentence, word, question , answer or topic belonging to the story identified with the `story`.
     */
    var word: String? = null
    /**
     * represent the type of media associated with the `word`.
     *
     *
     * uritype = S then , if it exists, uri refers to an image from storage
     *
     *
     * uritype = A then , if it exists, uri refers to an image from a url of arasaac
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
     * contains the type of action associated with the `word` in case the word is an answer.
     *
     *
     * answerActionType = V then the action consists of playing a video
     *
     *
     * answerActionType = G then the action consists consists of executing a goto (phraseNumber) statement
     * corresponding to the topic of a sentence
     */
    var answerActionType: String? = null
    /**
     * contains the action associated with the `word` in case the word is an answer:
     *
     *
     * contains the video key in the videos table or the topic of a sentence
     */
    var answerAction: String? = null
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
    /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null
    /*
     * getter, setter and other methods
     */
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
            val FILE_NAME = "stories.csv"
            // Grab all data from the DB in question :
            val resultsDB = realm.where(Stories::class.java).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Stories")

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
         * Export a story to CSV .
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @param resultsDB RealmResults<Stories> story to export
         * @param dir File with output directory
         * @see AdvancedSettingsDataImportExportHelper.grabHeader
         * @see AdvancedSettingsDataImportExportHelper.savBak
         * @see AdvancedSettingsDataImportExportHelper.dataProcess
        </Stories> */
        @JvmStatic
        fun exporttoCsv(
            context: Context?,
            realm: Realm?,
            resultsDB: RealmResults<Stories?>,
            dir: File?
        ) {
//
            val FILE_NAME = "stories.csv"
            // Grab all data from the DB in question :
            //        RealmResults<Stories> resultsDB = realm.where(Stories.class).findAll();

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm!!, "Stories")

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
                val daCancellare = realm.where(Stories::class.java).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "stories.csv"
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
                        // also including empty strings
                        val oneWord: Array<String?> =
                            line!!.split(cvsSplitBy.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        // checks
                        var f: File? = null
                        var uri: String? = null
                        if (oneWord[6] != null) {
                            if (oneWord[5] == context.getString(R.string.character_s)) {
                                // replace with root of the external storage or data directory
                                uri = oneWord[6]!!
                                    .replaceFirst(
                                        context.getString(R.string.rootdirectory).toRegex(),
                                        rootPath
                                    )
                                //
                                f = File(uri)
                            } else {
                                uri = oneWord[6]
                            }
                        }
                        if (oneWord[0] != null && oneWord[1] != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null && oneWord[6] != null && oneWord[7] != null && oneWord[8] != null && oneWord[9] != null && oneWord[10] != null && oneWord[11] != null && oneWord[12] != null) {
                            if (oneWord[0]!!.length > 0 && oneWord[1]!!.length > 0 && oneWord[2]!!.length > 0 && oneWord[3]!!.length > 0 && oneWord[4]!!.length > 0 &&
                                (oneWord[5] != context.getString(R.string.character_s)
                                        || oneWord[5] == context.getString(R.string.character_s) && f!!.exists())
                            ) {
                                //
                                realm.beginTransaction()
                                val history = realm.createObject(Stories::class.java)
                                // set the fields here
                                history.story = oneWord[0]
                                history.phraseNumberInt = oneWord[1]!!.toInt()
                                history.wordNumberInt = oneWord[2]!!.toInt()
                                history.wordNumberIntInTheStory = oneWord[3]!!.toInt()
                                history.word = oneWord[4]
                                history.uriType = oneWord[5]
                                //
                                history.uri = uri
                                //
                                history.answerActionType = oneWord[7]
                                history.answerAction = oneWord[8]
                                //
                                history.video = oneWord[9]
                                history.sound = oneWord[10]
                                history.soundReplacesTTS = oneWord[11]
                                history.fromAssets = oneWord[12]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
        //
        /**
         * Import from CSV of a story.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
         * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
         *
         * @param context context
         * @param realm realm obtained from the activity by Realm#getDefaultInstance
         * @param story string story to import
         */
        @JvmStatic
        fun importStoryFromCsvFromInternalStorage(context: Context, realm: Realm, story: String?) {
            // clear the table
            val daCancellare = realm.where(Stories::class.java).equalTo("story", story).findAll()
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "stories.csv"
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
                        // also including empty strings
                        val oneWord: Array<String?> =
                            line!!.split(cvsSplitBy.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        // checks
                        var f: File? = null
                        var uri: String? = null
                        if (oneWord[6] != null) {
                            if (oneWord[5] == context.getString(R.string.character_s)) {
//                          uri = oneWord[5].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                                // replace with root of data directory
                                val fileName = oneWord[6]!!
                                    .substring(oneWord[6]!!.lastIndexOf("/") + 1)
                                uri = "$rootPath/$fileName"
                                //
                                f = File(uri)
                            } else {
                                uri = oneWord[6]
                            }
                        }
                        if (oneWord[0] != null && oneWord[1] != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null && oneWord[6] != null && oneWord[7] != null && oneWord[8] != null && oneWord[9] != null && oneWord[10] != null && oneWord[11] != null && oneWord[12] != null) {
                            if (oneWord[0]!!.length > 0 && oneWord[1]!!.length > 0 && oneWord[2]!!.length > 0 && oneWord[3]!!.length > 0 && oneWord[4]!!.length > 0 &&
                                (oneWord[5] != context.getString(R.string.character_s)
                                        || oneWord[5] == context.getString(R.string.character_s) && f!!.exists())
                            ) {
                                //
                                realm.beginTransaction()
                                val history = realm.createObject(Stories::class.java)
                                // set the fields here
                                history.story = oneWord[0]
                                history.phraseNumberInt = oneWord[1]!!.toInt()
                                history.wordNumberInt = oneWord[2]!!.toInt()
                                history.wordNumberIntInTheStory = oneWord[3]!!.toInt()
                                history.word = oneWord[4]
                                history.uriType = oneWord[5]
                                //
                                history.uri = uri
                                //
                                history.answerActionType = oneWord[7]
                                history.answerAction = oneWord[8]
                                //
                                history.video = oneWord[9]
                                history.sound = oneWord[10]
                                history.soundReplacesTTS = oneWord[11]
                                history.fromAssets = oneWord[12]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        }
    }
}