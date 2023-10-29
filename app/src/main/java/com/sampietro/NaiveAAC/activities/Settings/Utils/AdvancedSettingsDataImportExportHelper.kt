package com.sampietro.NaiveAAC.activities.Settings.Utils

import android.content.Context
import io.realm.RealmSchema
import io.realm.RealmObjectSchema
import com.sampietro.NaiveAAC.R
import android.os.Environment
import io.realm.Realm
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * <h1>AdvancedSettingsDataImportExportHelper</h1>
 *
 *
 * **AdvancedSettingsDataImportExportHelper** utility class for data import / export.
 * REFER to [stackoverflow](https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json)
 * answer of [vrasheed](https://stackoverflow.com/users/8989439/vrasheed)
 */
object AdvancedSettingsDataImportExportHelper {
    /**
     * prepares the realm table header for export
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param model string containing the name of the realm class to export
     * @return string header for export
     * @see Realm.getSchema
     * @see RealmSchema.get
     * @see RealmObjectSchema.getFieldNames
     * @see [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)
     *
     * @see [Matcher](https://docs.oracle.com/javase/9/docs/api/java/util/regex/Matcher.html)
     */
    fun grabHeader(realm: Realm, model: String): String {
        val schema = realm.schema
        //
        val testSchema = schema[model]!!
        //
        val header = testSchema.fieldNames.toString()
        val dataProcessed = StringBuilder("")
        //
        val p = Pattern.compile("\\[(.*?)]")
        val m = p.matcher(header)
        //
        while (m.find()) {
            // String 	Matcher#group(int group)
            // Returns the input subsequence captured by the given group during the previous
            // match operation.
            // String#trim()
            // Returns a copy of the string, with leading and trailing whitespace omitted.
            // String 	String#replaceAll(String regex, String replacement)
            // Replaces each substring of this string that matches the given regular expression
            // with the given replacement.
            // \Z 	The end of the input but for the final terminator, if any
            // toglie l'ultimo carattere ?
            dataProcessed.append(Objects.requireNonNull(m.group(1)).trim { it <= ' ' }
                .replace("\\p{Z}".toRegex(), ""))
        }
        return dataProcessed.toString()
    }
    //
    /**
     * process the data corresponding to the fields of the table to export and add commas and formatting
     *
     * @param dataP string containing data to be processed
     * @return string processed data
     * @see [Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)
     *
     * @see [Matcher](https://docs.oracle.com/javase/9/docs/api/java/util/regex/Matcher.html)
     */
    fun dataProcess(dataP: String?): String {
        // We process the data obtained and add commas and formatting:
        val dataProcessed = StringBuilder("")
        //
//        val p = Pattern.compile(":(.*?)\\}")
        val p = Pattern.compile(":(.*?)\\}")
        val m = p.matcher(dataP!!)
        //
        while (m.find()) {
            // String 	Matcher#group(int group)
            // Returns the input subsequence captured by the given group during the previous
            // match operation.
            // String#trim()
            // Returns a copy of the string, with leading and trailing whitespace omitted.
            // String 	String#replaceAll(String regex, String replacement)
            // Replaces each substring of this string that matches the given regular expression
            // with the given replacement.
            // searches for characters between : and }
            dataProcessed.append(Objects.requireNonNull(m.group(1)).trim { it <= ' ' })
            dataProcessed.append(",")
        }
        return dataProcessed.toString()
    }
    //
    /**
     * writes data to the output file
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     */
    fun savBak(context: Context, data: String, fileName: String?) {
        var fos: FileOutputStream? = null
        try {
            //
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fos.write(data.toByteArray())
            fos.write("\n".toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //
    /**
     * writes data to the output file
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     */
    fun savBak(context: Context?, data: String, fileName: String?, dir: File?) {
        var fos: FileOutputStream? = null
        try {
            //
//          fos = context.openFileOutput(fileName, MODE_PRIVATE);
            fos = FileOutputStream(File(dir, fileName!!))
            fos.write(data.toByteArray())
            fos.write("\n".toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //
    /**
     * writes data to the output file
     *
     *
     * if it is the last line to write, it does not add the newline character
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     * @param isLastRow boolean true if it is the last line to write
     */
    fun savBak(context: Context, data: String, fileName: String?, isLastRow: Boolean?) {
        var fos: FileOutputStream? = null
        try {
            // fos = openFileOutput(FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            // fos = null
            fos = context.openFileOutput(fileName, Context.MODE_APPEND)
            fos.write(data.toByteArray())
            if (!isLastRow!!) fos.write("\n".toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //
    /**
     * writes data to the output file
     *
     *
     * if it is the last line to write, it does not add the newline character
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     * @param isLastRow boolean true if it is the last line to write
     */
    fun savBak(
        context: Context?,
        data: String,
        fileName: String?,
        dir: File?,
        isLastRow: Boolean?
    ) {
        var fos: FileOutputStream? = null
        try {
            // fos = openFileOutput(FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            // fos = null
            // fos = context.openFileOutput(fileName, MODE_APPEND);
            fos = FileOutputStream(File(dir, fileName!!), true)
            fos.write(data.toByteArray())
            if (!isLastRow!!) fos.write("\n".toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    //
    //
    /**
     * open the output csv file
     *
     * @param fileName string name of the output file
     * @param mode boolean if true the file will be written in append mode
     * @return fileoutputstream
     * @see [](https://docs.oracle.com/javase/7/docs/api/java/io/FileOutputStream.html.FileOutputStream
    ) */
    fun openFileOutput(context: Context, fileName: String?, mode: Boolean?): FileOutputStream? {
        val root = findExternalStorageRoot()
        try {
            val dir =
                File(root!!.absolutePath + "/" + context.getString(R.string.app_name))
            val file = File(dir, fileName!!)
            file.setReadable(true, true)
            return FileOutputStream(file, mode!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    //
    /**
     * open the input csv file
     *
     * @param fileName string name of the output file
     * @return file
     */
    @JvmStatic
    fun openFileInput(context: Context, fileName: String?): File {
        val root = findExternalStorageRoot()
        val dir =
            File(root!!.absolutePath + "/" + context.getString(R.string.app_name))
        return File(dir, fileName!!)
    }

    /**
     * find external storage root
     *
     * @return file external storage root
     */
    @JvmStatic
    fun findExternalStorageRoot(): File? {
        val externalStorageState = Environment.getExternalStorageState()
        val root: File?
        root =
            if (externalStorageState == Environment.MEDIA_MOUNTED) // Find the root of the external storage.
            {
                Environment.getExternalStorageDirectory()
            } else {
                Environment.getDataDirectory()
            }
        return root
    }
}