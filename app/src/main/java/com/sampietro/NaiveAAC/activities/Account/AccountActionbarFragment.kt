package com.sampietro.NaiveAAC.activities.Account

import android.content.Context
import android.os.Bundle
import android.view.MenuInflater
import com.sampietro.NaiveAAC.R
import androidx.core.content.FileProvider
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromAssetsToInternalStorage
import com.sampietro.NaiveAAC.activities.Info.InfoActivity
import com.sampietro.NaiveAAC.activities.Info.EulaActivity
import io.realm.Realm
import java.io.File
import java.io.IOException
import kotlin.Throws

/**
 * <h1>ActionbarFragment</h1>
 *
 * **ActionbarFragment** UI for Action Bar
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/appbar)
 *
 * @version     5.0, 24/02/2024
 * @see Fragment
 *
 */
class AccountActionbarFragment : Fragment() {
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
        inflater.inflate(R.menu.account_activity_display_message_menu_main, menu)
    }

    /**
     * receives the selected menu item as a parameter and returns a boolean to indicate whether or not the touch has been consumed.
     *
     *
     *
     * @see Fragment.onOptionsItemSelected
     *
     * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.MENU_MANUAL -> {
                /*
                navigate to manual
                */try {
                    assert(Realm.getApplicationContext() != null)
                    copyFileFromAssetsToInternalStorage(
                        Realm.getApplicationContext()!!,
                        "pdf",
                        "naive aac manuale istruzioni.pdf",
                        "naive aac manuale istruzioni.pdf"
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                assert(Realm.getApplicationContext() != null)
                val pdfFile = File(
                    Realm.getApplicationContext()!!.filesDir, "naive aac manuale istruzioni.pdf"
                )
                if (pdfFile.exists()) {
                    val pdfUri = FileProvider.getUriForFile(
                        requireContext(),
                        getString(R.string.com_sampietro_naiveaac_fileprovider),
                        pdfFile
                    )
                    val intent1 = Intent(Intent.ACTION_VIEW)
                    intent1.setDataAndType(pdfUri, "application/pdf")
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent1)
                }
                true
            }
            R.id.MENU_INFO -> {
                /*
                navigate to info screen (InfoActivity)
                */
                val intent3 = Intent(activity, InfoActivity::class.java)
                startActivity(intent3)
                true
            }
            R.id.MENU_EULA -> {
                /*
                navigate to eula screen (MainActivity)
                */
                val intent4 = Intent(activity, EulaActivity::class.java)
                startActivity(intent4)
                true
            }
            R.id.MENU_PRIVACY -> {
                /*
                navigate to privacy policy
                */try {
                    assert(Realm.getApplicationContext() != null)
                    copyFileFromAssetsToInternalStorage(
                        Realm.getApplicationContext()!!,
                        "pdf",
                        "privacy.pdf",
                        "privacy.pdf"
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                assert(Realm.getApplicationContext() != null)
                val pdfFilePrivacy = File(
                    Realm.getApplicationContext()!!.filesDir, "privacy.pdf"
                )
                if (pdfFilePrivacy.exists()) {
                    val pdfUri = FileProvider.getUriForFile(
                        requireContext(),
                        getString(R.string.com_sampietro_naiveaac_fileprovider),
                        pdfFilePrivacy
                    )
                    val intent5 = Intent(Intent.ACTION_VIEW)
                    intent5.setDataAndType(pdfUri, "application/pdf")
                    intent5.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent5)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}