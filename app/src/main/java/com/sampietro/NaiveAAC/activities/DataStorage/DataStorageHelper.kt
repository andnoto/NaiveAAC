package com.sampietro.NaiveAAC.activities.DataStorage

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.sampietro.NaiveAAC.R
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URISyntaxException
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object DataStorageHelper {
    /**
     * copy file.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param in inputstream
     * @param out outputstream
     */
    @Throws(IOException::class)
    @JvmStatic
    fun copyFile(`in`: InputStream, out: OutputStream?) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out!!.write(buffer, 0, read)
        }
    }
    /**
     * copy file.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android)
     * answer of [Rakshi](https://stackoverflow.com/users/979752/rakshi)
     *
     * @param src file source
     * @param dst file destination
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(src: File?, dst: File?) {
        FileInputStream(src).use { `in` ->
            FileOutputStream(dst).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }

    /**
     * strip file name extension.
     *
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/7541550/remove-the-extension-of-a-file)
     * answer of [palacsint](https://stackoverflow.com/users/843804/palacsint)
     *
     * @param s string file name
     * @return string file name without extension
     */
    @JvmStatic
    fun stripExtension(s: String?): String {
        return if (s != null && s.lastIndexOf(".") > 0) s.substring(
            0,
            s.lastIndexOf(".")
        ) else s!!
    }
    /**
     * copy assets images and videos to storage.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param context Context
     * @param path string assets folder to copy
     * @see copyFile
     */
    @JvmStatic
    fun copyAssets(context: Context, path: String) {
        val assetManager = context.assets
        var files: Array<String>? = null
        try {
            files = assetManager.list(path)
        } catch (e: IOException) {
            // Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            for (filename in files) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = assetManager.open("$path/$filename")
                    //
                    out = context.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
                    copyFile(`in`, out)
                } catch (e: IOException) {
                    // Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (`in` != null) {
                        try {
                            `in`.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }
    /**
     * copy the single file from assets
     * @param context Context
     * @param dirName string with the name of the directory of the file to be copied
     * @param fileName string with the name of the file to be copied
     * @param destFileName string with the name of the destination file
     */
    @Throws(IOException::class)
    @JvmStatic
    fun copyFileFromAssetsToInternalStorage(
        context: Context,
        dirName: String,
        fileName: String,
        destFileName: String?
    ) {
        val sourceStream = context.assets.open(dirName + context.getString(R.string.character_slash) + fileName)
        val destStream = context.openFileOutput(destFileName, Application.MODE_PRIVATE)
        val buffer = ByteArray(1024)
        var bytesRead = 0
        while (sourceStream.read(buffer).also { bytesRead = it } != -1) {
            destStream.write(buffer, 0, bytesRead)
        }
        destStream.close()
        sourceStream.close()
    }
    /**
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/6683600/zip-compress-a-folder-full-of-files-on-android)
     * answer of [HailZeon](https://stackoverflow.com/users/2053024/hailzeon)
     *
     * @param sourcePath string
     * @param toLocation string
     * @return boolean
     */
    @JvmStatic
    fun zipFileAtPath(sourcePath: String, toLocation: String?): Boolean {
        val BUFFER = 2048
        val sourceFile = File(sourcePath)
        try {
            val origin: BufferedInputStream?
            val dest = FileOutputStream(toLocation)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            if (sourceFile.isDirectory) {
                zipSubFolder(out, sourceFile, sourceFile.parent!!.length)
            } else {
                val data = ByteArray(BUFFER)
                val fi = FileInputStream(sourcePath)
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(getLastPathComponent(sourcePath))
                entry.time = sourceFile.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /*
     *
     * Zips a subfolder
     *
     */
    @Throws(IOException::class)
    @JvmStatic
    private fun zipSubFolder(
        out: ZipOutputStream, folder: File,
        basePathLength: Int
    ) {
        val BUFFER = 2048
        val fileList = folder.listFiles()
        var origin: BufferedInputStream?
        for (file in fileList!!) {
            if (file.isDirectory) {
                zipSubFolder(out, file, basePathLength)
            } else {
                val data = ByteArray(BUFFER)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath
                    .substring(basePathLength)
                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(relativePath)
                entry.time = file.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    @JvmStatic
    fun getLastPathComponent(filePath: String): String {
        val segments = filePath.split("/".toRegex()).toTypedArray()
        return if (segments.size == 0) "" else segments[segments.size - 1]
    }

    /**
     * unzip file with subdirectories.
     *
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/43672241/how-to-unzip-file-with-sub-directories-in-android)
     * answer of [A.B.](https://stackoverflow.com/users/4914757/a-b)
     * Refer to [support.google.com](https://support.google.com/faqs/answer/9294009)
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56303842/fixing-a-zip-path-traversal-vulnerability-in-android)
     * answer of [Indra Kumar S](https://stackoverflow.com/users/3577946/indra-kumar-s)
     *
     * @param destination file
     * @param zipFile file
     * @return boolean
     */
    @Throws(ZipException::class, IOException::class)
    @JvmStatic
    fun extractFolder(destination: File, zipFile: File): Boolean {
        val BUFFER = 8192
        //This can throw ZipException if file is not valid zip archive
        val zip = ZipFile(zipFile)
        val newPath = destination.absolutePath + File.separator + stripExtension(
            zipFile.name.substring(zipFile.name.lastIndexOf("/") + 1)
        )
        //Create destination directory
        File(newPath).mkdir()
        val zipFileEntries: Enumeration<*> = zip.entries()

        //Iterate overall zip file entries
        while (zipFileEntries.hasMoreElements()) {
            val entry = zipFileEntries.nextElement() as ZipEntry
            val currentEntry = entry.name
            val destFile = File(destination.absolutePath, currentEntry)
            //
            try {
                ensureZipPathSafety(destFile, destination)
            } catch (e: Exception) {
                // SecurityException
                e.printStackTrace()
                return false
            }
            //
            val destinationParent = destFile.parentFile
            //If entry is directory create sub directory on file system
            destinationParent!!.mkdirs()
            if (!entry.isDirectory) {
                //Copy over data into destination file
                val `is` = BufferedInputStream(
                    zip
                        .getInputStream(entry)
                )
                var currentByte: Int
                val data = ByteArray(BUFFER)
                //orthodox way of copying file data using streams
                val fos = FileOutputStream(destFile)
                val dest = BufferedOutputStream(fos, BUFFER)
                while (`is`.read(data, 0, BUFFER).also { currentByte = it } != -1) {
                    dest.write(data, 0, currentByte)
                }
                dest.flush()
                dest.close()
                `is`.close()
            }
        }
        return true //some error codes etc.
    }

    /**
     * ensure Zip Path Safety.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56303842/fixing-a-zip-path-traversal-vulnerability-in-android)
     * answer of [Indra Kumar S](https://stackoverflow.com/users/3577946/indra-kumar-s)
     *
     * @param destFile file
     * @param destination directory
     */
    @Throws(Exception::class)
    @JvmStatic
    private fun ensureZipPathSafety(destFile: File, destination: File) {
        val destDirCanonicalPath = destination.canonicalPath
        val destFileCanonicalPath = destFile.canonicalPath
        if (!destFileCanonicalPath.startsWith(destDirCanonicalPath)) {
            throw Exception(
                String.format(
                    "Found Zip Path Traversal Vulnerability with %s",
                    destFileCanonicalPath
                )
            )
        }
    }
    //
    /**
     * copy sounds images and videos to root internal storage.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param dirName string folder to copy
     * @see copyFile
     */
    @JvmStatic
    fun copyFilesInFolderToRoot(context: Context, dirName: String) {
        val fDirName = File(dirName)
        val files: Array<String>?
        files = fDirName.list()
        if (files != null) {
            for (filename in files) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = FileInputStream("$dirName/$filename")
                    //                  in = context.openFileInput(dirName + "/" + filename);
                    //
                    out = context.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
                    copyFile(`in`, out)
                } catch (e: IOException) {
                    // Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (`in` != null) {
                        try {
                            `in`.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }

    /**
     * copy file.
     *
     *
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    @Throws(IOException::class)
    @JvmStatic
    fun copyFileZipFromInternalToSharedStorage(context:Context, outputFolder: DocumentFile?, destFileName: String?) {
        val sourceStream: InputStream = context.openFileInput(destFileName)
        val documentFileNewFile = outputFolder!!.createFile("application/zip", destFileName!!)!!
        val destStream = context.contentResolver.openOutputStream(
            documentFileNewFile.uri
        )
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream.read(buffer).also { read = it } != -1) {
            destStream!!.write(buffer, 0, read)
        }
        destStream?.close()
        sourceStream.close()
    }
    /**
     * copy file.
     *
     *
     *
     * @param sourceUri uri
     * @param destFileName string
     */
    @Throws(IOException::class)
    @JvmStatic
    fun copyFileFromSharedToInternalStorage(context:Context, sourceUri: Uri?, destFileName: String?) {
        val sourceStream = context.contentResolver.openInputStream(sourceUri!!)
        val destStream = context.openFileOutput(destFileName, AppCompatActivity.MODE_PRIVATE)
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream!!.read(buffer).also { read = it } != -1) {
            destStream.write(buffer, 0, read)
        }
        destStream.close()
        sourceStream.close()
    }
    /**
     * copy file.
     *
     *
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    @Throws(IOException::class)
    @JvmStatic
    fun copyFileFromInternalToSharedStorage(context:Context, outputFolder: DocumentFile, destFileName: String?) {
        val sourceStream: InputStream = context.openFileInput(destFileName)
        val documentFileNewFile = outputFolder.createFile("text/csv", destFileName!!)!!
        val destStream = context.contentResolver.openOutputStream(
            documentFileNewFile.uri
        )
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream.read(buffer).also { read = it } != -1) {
            destStream!!.write(buffer, 0, read)
        }
        destStream?.close()
        sourceStream.close()
    }
    /**
     * get the FilePath from the uri.
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore)
     * answer of [Jitendra Ramoliya](https://stackoverflow.com/users/4118297/jitendra-ramoliya)
     * This funcion has been replaced by copyFileFromSharedToInternalStorageAndGetPath starting from Android 14
     *
     *
     * @param context context
     * @param uri uri
     * @return string with the filepath identified by uri from mediastore
     */
    @Deprecated("replaced by {@link #copyFileFromSharedToInternalStorageAndGetPath(Context?,Uri?):String?}")
    @JvmStatic
    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context?, uri: Uri?): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (
            DocumentsContract.isDocumentUri(
                context!!.applicationContext,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                return "da download"
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
        if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            val cursor: Cursor?
            try {
                cursor = context.contentResolver
                    .query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    val filePathC = cursor.getString(column_index)
                    cursor.close()
                    return filePathC
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to External Storage Document
     */
    fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri!!.authority
    }

    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to downloaded documents
     */
    @JvmStatic
    fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents" == uri!!.authority
    }

    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to media documents
     */
    @JvmStatic
    fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents" == uri!!.authority
    }

    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to photos
     */
    @JvmStatic
    fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content" == uri!!.authority
    }
    /**
     * copy file and get the FilePath from the uri.
     * This function replaces getFilePath starting from Android 14
     *
     * @param context context
     * @param uri uri
     * @return string with the filepath identified by uri from mediastore
     */
    @JvmStatic
    @SuppressLint("NewApi")
    fun copyFileFromSharedToInternalStorageAndGetPath(context: Context, uri: Uri): String? {
        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = context.contentResolver.query(
            uri, null, null, null, null, null)
        var displayName: String? = null
        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {
                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                val myCursorColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                displayName =
                    it.getString(myCursorColumnIndex)
            }
        }
        //
        val rootPath = context.filesDir.absolutePath
        if (displayName != null)
        {
            copyFileFromSharedToInternalStorage(context, uri, displayName)
            return rootPath+"/"+displayName
        }
        return null
    }
}