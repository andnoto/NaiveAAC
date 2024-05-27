package com.sampietro.NaiveAAC.activities.Info

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass

/**
 * <h1>EulaActivity</h1>
 *
 * **EulaActivity** app Eula info.
 *
 * @version     5.0, 01/04/2024
 * @see InfoActivityAbstractClass
 */
class EulaActivity : InfoActivityAbstractClass() {
    /**
     * configurations of info start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see androidx.appcompat.app.AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, Fragment(R.layout.activity_eula_information), "EulaFragment")
                .commit()
        }
    } //
}