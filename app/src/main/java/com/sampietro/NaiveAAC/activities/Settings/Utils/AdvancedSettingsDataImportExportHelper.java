package com.sampietro.NaiveAAC.activities.Settings.Utils;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Environment;

import com.sampietro.NaiveAAC.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * <h1>AdvancedSettingsDataImportExportHelper</h1>
 *
 * <p><b>AdvancedSettingsDataImportExportHelper</b> utility class for data import / export.</p>
 * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
 * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
 */
public class AdvancedSettingsDataImportExportHelper {
    /**
     * prepares the realm table header for export
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param model string containing the name of the realm class to export
     * @return string header for export
     * @see Realm#getSchema()
     * @see RealmSchema#get(String className)
     * @see RealmObjectSchema#getFieldNames()
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html">Pattern</a>
     * @see <a href="https://docs.oracle.com/javase/9/docs/api/java/util/regex/Matcher.html">Matcher</a>
     */
    public static String grabHeader(Realm realm, String model){

        final RealmSchema schema = realm.getSchema();
        //
        final RealmObjectSchema testSchema = schema.get(model);
        assert testSchema != null;
        //
        final String header = testSchema.getFieldNames().toString();

        StringBuilder dataProcessed = new StringBuilder("");
        //
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(header);
        //
        while(m.find()) {
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
            dataProcessed.append(Objects.requireNonNull(m.group(1)).trim().replaceAll("\\p{Z}", ""));
        }

        return dataProcessed.toString();
    }
    //
    /**
     * process the data corresponding to the fields of the table to export and add commas and formatting
     *
     * @param dataP string containing data to be processed
     * @return string processed data
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html">Pattern</a>
     * @see <a href="https://docs.oracle.com/javase/9/docs/api/java/util/regex/Matcher.html">Matcher</a>
     */
    public static String dataProcess(String dataP){
        // We process the data obtained and add commas and formatting:
        StringBuilder dataProcessed = new StringBuilder("");
        //
        Pattern p = Pattern.compile(":(.*?)\\}");
        Matcher m = p.matcher(dataP);
        //
        while(m.find()) {
            // String 	Matcher#group(int group)
            // Returns the input subsequence captured by the given group during the previous
            // match operation.
            // String#trim()
            // Returns a copy of the string, with leading and trailing whitespace omitted.
            // String 	String#replaceAll(String regex, String replacement)
            // Replaces each substring of this string that matches the given regular expression
            // with the given replacement.
            // searches for characters between : and }

            dataProcessed.append(Objects.requireNonNull(m.group(1)).trim());
            dataProcessed.append(",");
        }

        return dataProcessed.toString();

    }
    //
    /**
     * writes data to the output file
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     */
    public static void savBak(Context context, String data, String  fileName){
        FileOutputStream fos = null;

        try {
            //
            fos = context.openFileOutput(fileName, MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.write("\n".getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //
    /**
     * writes data to the output file
     * <p>
     * if it is the last line to write, it does not add the newline character
     *
     * @param data string data to be recorded
     * @param fileName string name of the output file
     * @param isLastRow boolean true if it is the last line to write
     */
    public static void savBak (Context context, String data, String  fileName, Boolean isLastRow){
        FileOutputStream fos = null;

        try {
            // fos = openFileOutput(FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            // fos = null
            fos = context.openFileOutput(fileName, MODE_APPEND);
            fos.write(data.getBytes());
            if (!isLastRow)
                fos.write("\n".getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/io/FileOutputStream.html#FileOutputStream(java.io.File,%20boolean)">FileOutputStream</a>
     */
    public static FileOutputStream openFileOutput(Context context, String fileName, Boolean mode){
        File root = findExternalStorageRoot();

        try {
             File dir = new File (root.getAbsolutePath() + "/" + context.getString(R.string.app_name));
             File file = new File(dir, fileName);
             file.setReadable(true, true);
             FileOutputStream f = new FileOutputStream(file, mode);
             return f;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        return null;
    }
    //
    /**
     * open the input csv file
     *
     * @param fileName string name of the output file
     * @return file
     */
    public static File openFileInput(Context context, String fileName){
        File root = findExternalStorageRoot();

        File dir = new File (root.getAbsolutePath() + "/" + context.getString(R.string.app_name));

        File file = new File(dir, fileName);
        return file;

    }
    /**
     * find external storage root
     *
     * @return file external storage root
     */
    public static File findExternalStorageRoot(){
        String externalStorageState = Environment.getExternalStorageState();
        File root = null;
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED))
        // Find the root of the external storage.
        { root = Environment.getExternalStorageDirectory(); }
        else
        { root = Environment.getDataDirectory(); }
        return root;
    }

    }
