package com.sampietro.NaiveAAC.activities.Game.Utils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2BleActivity
import com.sampietro.NaiveAAC.activities.Info.EulaActivity
import com.sampietro.NaiveAAC.activities.Info.InfoActivity
import com.sampietro.NaiveAAC.activities.Info.PrivacyActivity
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
import io.realm.Realm
import java.io.File
import java.io.IOException

/**
 * <h1>ActionbarFragment</h1>
 *
 * **ActionbarFragment** UI for Action Bar
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/appbar)
 *
 * @version     4.0, 09/09/2023
 * @see Fragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
 */
class ActionbarFragment : Fragment() {
    var root: File? = null
    var rootPath: String? = null

    /**
     * inform the system that your app bar fragment is participating in the population of the options menu.
     *
     *
     *
     * @see Fragment.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * merge your menu into the app bar's options menu.
     *
     *
     *
     * @see Fragment.onCreateOptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_display_message_menu_main, menu)
        //
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
    }

    /**
     * receives the selected menu item as a parameter and returns a boolean to indicate whether or not the touch has been consumed.
     *
     *
     *
     * @see Fragment.onOptionsItemSelected
     *
     * @see SettingsActivity
     *
     * @see ChoiseOfGameActivity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.MENU_SETTINGS -> {
                /*
                navigate to settings screen
                */
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.MENU_MANUAL -> {
                /*
                navigate to manual
                */try {
                    copyPdfFromAssetsToInternalStorage(getString(R.string.naive_aac_manuale_istruzioni_pdf))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                assert(Realm.getApplicationContext() != null)
                try {
                    val pdfFile = File(
                        Realm.getApplicationContext()!!.filesDir, getString(R.string.naive_aac_manuale_istruzioni_pdf)
                    )
                    if (pdfFile.exists()) {
                        val pdfUri = FileProvider.getUriForFile(
                            requireContext(),
                            getString(R.string.com_sampietro_naiveaac_fileprovider),
                            pdfFile
                        )
                        val intent1 = Intent(Intent.ACTION_VIEW)
                        intent1.setDataAndType(pdfUri, getString(R.string.application_pdf))
                        intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(intent1)
                    }
                } catch (e: ActivityNotFoundException) {
                    alertDialogNeedsSomePDFApp()
                }
                true
            }
            R.id.MENU_HOME -> {
                /*
                navigate to home screen (MainActivity)
                */
                val intent2 = Intent(activity, ChoiseOfGameActivity::class.java)
                startActivity(intent2)
                true
            }
            R.id.MENU_COMUNICATORE -> {
                /*
                navigate to game2 screen (Game2Activity)
                */
                val intent3 = Intent(activity, Game2BleActivity::class.java)
                startActivity(intent3)
                true
            }
            R.id.MENU_INFO -> {
                /*
                navigate to info screen (InfoActivity)
                */
                val intent4 = Intent(activity, InfoActivity::class.java)
                startActivity(intent4)
                true
            }
            R.id.MENU_EULA -> {
                /*
                navigate to eula screen (MainActivity)
                */
                val intent5 = Intent(activity, EulaActivity::class.java)
                startActivity(intent5)
                true
            }
            R.id.MENU_PRIVACY -> {
                /*
                navigate to privacy screen (PrivacyActivity)
                */
                val intent6 = Intent(activity, PrivacyActivity::class.java)
                startActivity(intent6)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * copy file.
     *
     *
     *
     */
    @Throws(IOException::class)
    private fun copyPdfFromAssetsToInternalStorage(nameOfThePdfFile: String) {
        assert(Realm.getApplicationContext() != null)
        val sourceStream = Realm.getApplicationContext()!!.assets.open("pdf/$nameOfThePdfFile")
        val destStream = Realm.getApplicationContext()!!
            .openFileOutput(nameOfThePdfFile, Context.MODE_PRIVATE)
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream.read(buffer).also { read = it } != -1) {
            destStream.write(buffer, 0, read)
        }
        destStream.close()
        sourceStream.close()
    }

    /**
     * alert dialog.
     *
     *
     *
     */
    private fun alertDialogNeedsSomePDFApp() {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.attenzione))
        alertDialogBuilder.setMessage(getString(R.string.occorre_installare_un_lettore_pdf_da_play_store))
        alertDialogBuilder.show()
    }
}