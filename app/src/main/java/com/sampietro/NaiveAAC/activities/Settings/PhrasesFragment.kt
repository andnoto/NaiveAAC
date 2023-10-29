package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import io.realm.Realm

/**
 * <h1>PhrasesFragment</h1>
 *
 * **PhrasesFragment** UI for phrases settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class PhrasesFragment : SettingsFragmentAbstractClass() {
    // Realm
    lateinit var realm: Realm

    //
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var textView4: TextView

    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Phrases
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_phrases, container, false)
        // logic of fragment
        textView1 = rootView.findViewById(R.id.edittextwelcomephrasefirstpart)
        textView2 = rootView.findViewById(R.id.edittextwelcomephrasesecondpart)
        textView3 = rootView.findViewById(R.id.edittextreminderphrase)
        textView4 = rootView.findViewById(R.id.edittextreminderphraseplural)
        // REALM
        realm = Realm.getDefaultInstance()
        //
        val phraseToSearch1 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
            .findFirst()
        if (phraseToSearch1 != null) {
            textView1.setText(phraseToSearch1.descrizione)
        }
        val phraseToSearch2 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
            .findFirst()
        if (phraseToSearch2 != null) {
            textView2.setText(phraseToSearch2.descrizione)
        }
        val phraseToSearch3 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase))
            .findFirst()
        if (phraseToSearch3 != null) {
            textView3.setText(phraseToSearch3.descrizione)
        }
        val phraseToSearch4 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.reminder_phrase_plural))
            .findFirst()
        if (phraseToSearch4 != null) {
            textView4.setText(phraseToSearch4.descrizione)
        }
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}