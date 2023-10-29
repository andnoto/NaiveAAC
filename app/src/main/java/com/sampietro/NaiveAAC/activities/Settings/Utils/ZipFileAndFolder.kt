package com.sampietro.NaiveAAC.activities.Settings.Utils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipFileAndFolder {

    @JvmStatic
    fun zipFileAtPath(sourcePath: String, dirName: String) {
        val inputDirectory = File(sourcePath)
        val outputZipFile = File.createTempFile(dirName, ".zip")
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
            inputDirectory.walkTopDown().forEach { file ->
                val zipFileName = file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
                val entry = ZipEntry( "$zipFileName${(if (file.isDirectory) "/" else "" )}")
                zos.putNextEntry(entry)
                if (file.isFile) {
                    file.inputStream().use { fis -> fis.copyTo(zos) }
                }
            }
        }

    }
}