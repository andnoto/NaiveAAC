package com.sampietro.NaiveAAC.activities.WordPairs

import android.content.Context
import io.realm.RealmObject
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import io.realm.Realm
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * <h1>WordPairs</h1>
 *
 * **WordPairs**  contains word pairs.
 * in game1 they are not considered wordpairs with pairs of names or pairs of verbs
 * (only noun-verb pairs are considered or vice versa verb-noun)
 *
 * @version     4.0, 09/09/2023
 */
open class WordPairs : RealmObject() {
    /**
     * word
     * in game1 , if it is not a verb, it represents the subject of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    var word1: String? = null
    /**
     * word
     * in game1 , if it is not a verb, it represents the object complement of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    var word2: String? = null
    /**
     * if `word1` is a verb, the articles and prepositions for the object complement and
     * the complements of motion to place and motion from place are automatically generated;
     * if necessary, for other types of complement, indicate the complement itself (example
     * andare - nonno insert "con il"
     */
    var complement: String? = null
    /**
     * game1 is designed as a search engine where categories are represented by image menus.
     * isMenuItem = TLM : `word1` is a top-level menu item of Game1
     * (in this case word1 must be equal to word2).
     * isMenuItem = SLM : `word2` is a second level menu item in Game1
     * where `word1` is its top level menu.
     */
    var isMenuItem: String? = null
    /**
     * game1 displays collections of word images that you can select to form simple sentences.
     * when the sentence is completed the app listens and
     * if spoken matches the sentence the player will receive a prize
     * awardType = V : premium video of the Game1
     * awardType = Y : premium Youtube video of the Game1.
     * if awardtype is empty the award is a simple balloon popping game
     */
    var awardType: String? = null
    /**
     * contains the video key in the videos table
     * or the Youtube url of the premium video (originally contained the uri).
     *
     * if awardType = V and uriPremiumVideo is empty,
     * the video prize is the one with the prize key in the videos table.
     */
    var uriPremiumVideo: String? = null
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
            val FILE_NAME = "wordpairs.csv"
            // Grab all data from the DB in question (WordPairs):
            val resultsDB = realm.where(WordPairs::class.java).findAll()

            // Here we need to put in header fields
            var dataP: String?
            val header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "WordPairs")

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
                val daCancellare = realm.where(WordPairs::class.java).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
            }
            //
            val rootPath = context.filesDir.absolutePath
            //
            val FILE_NAME = "wordpairs.csv"
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
                            line!!.split(cvsSplitBy).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        // checks
                        // register excluding lines with words that do not correspond to images
                        if (oneWord[0] != null && oneWord[1] != null) {
                            var image1: ResponseImageSearch?
                            var image2: ResponseImageSearch?
                            image1 = ImageSearchHelper.imageSearch(context, realm, oneWord[0])
                            image2 = ImageSearchHelper.imageSearch(context, realm, oneWord[1])
                            if (image1 != null && image2 != null && oneWord[2] != null && oneWord[3] != null && oneWord[4] != null && oneWord[5] != null && oneWord[6] != null) {
                                //
                                realm.beginTransaction()
                                val wordPairs = realm.createObject(
                                    WordPairs::class.java
                                )
                                // set the fields here
                                wordPairs.word1 = oneWord[0]
                                wordPairs.word2 = oneWord[1]
                                wordPairs.complement = oneWord[2]
                                wordPairs.isMenuItem = oneWord[3]
                                wordPairs.awardType = oneWord[4]
                                // replace with root of the external storage or data directory
                                val uri = oneWord[5]!!
                                    .replaceFirst("rootdirectory".toRegex(), rootPath)
                                //
                                wordPairs.uriPremiumVideo = uri
                                wordPairs.fromAssets = oneWord[6]
                                realm.commitTransaction()
                            }
                        }
                    }
                }
            }
        } //
    }
}