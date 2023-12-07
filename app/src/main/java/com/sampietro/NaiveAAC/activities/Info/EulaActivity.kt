package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment

/**
 * <h1>EulaActivity</h1>
 *
 * **EulaActivity** app Eula info.
 *
 * @version     4.0, 09/09/2023
 * @see InfoActivityAbstractClass
 *
 * @see InfoFragmentAbstractClass
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
                .add(R.id.settings_container, EulaFragment(), "EulaFragment")
                .commit()
        }
    } //
}