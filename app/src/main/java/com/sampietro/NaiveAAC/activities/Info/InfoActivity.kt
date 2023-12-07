package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment

/**
 * <h1>InfoActivity</h1>
 *
 * **InfoActivity** app info.
 *
 * @version     4.0, 09/09/2023
 * @see InfoActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass
 */
class InfoActivity : InfoActivityAbstractClass() {
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
                .add(R.id.settings_container, InfoFragment(), "InfoFragment")
                .commit()
        }
    }
    //
    /**
     * Called when the user taps the images copyright button from info.
     *
     * the activity is notified to view the images copyright list.
     *
     *
     * @param view view of tapped button
     * @see InfoFragment
     *
     * @see InfoImagesCopyrightListFragment
     */
    fun imagesCopyright(view: View?) {
        // view the images copyright list  initializing InfoImagesCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = InfoImagesCopyrightListFragment()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    //
    /**
     * Called when the user taps the sounds copyright button from info.
     *
     * the activity is notified to view the sounds copyright list.
     *
     *
     * @param view view of tapped button
     * @see InfoFragment
     *
     * @see InfoSoundsCopyrightListFragment
     */
    fun soundsCopyright(view: View?) {
        // view the sounds copyright list  initializing InfoSoundsCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = InfoSoundsCopyrightListFragment()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    //
    /**
     * Called when the user taps the videos copyright button from info.
     *
     * the activity is notified to view the videos copyright list.
     *
     *
     * @param view view of tapped button
     * @see InfoFragment
     *
     * @see InfoVideosCopyrightListFragment
     */
    fun videosCopyright(view: View?) {
        // view the videos copyright list  initializing InfoVideosCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = InfoVideosCopyrightListFragment()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
}