package com.sampietro.NaiveAAC.activities.Info;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>InfoActivity</h1>
 * <p><b>InfoActivity</b> app info.</p>
 *
 * @version     1.0, 06/13/22
 * @see InfoActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 */
public class InfoActivity extends InfoActivityAbstractClass
{
    /**
     * configurations of info start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        context = this;
        //
        if (savedInstanceState == null)
            {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new InfoFragment(), "InfoFragment")
                    .commit();
            }
    }
//
    /**
     * Called when the user taps the images copyright button from info.
     * </p>
     * the activity is notified to view the images copyright list.
     * </p>
     *
     * @param view view of tapped button
     * @see InfoFragment
     * @see InfoImagesCopyrightListFragment
     */
    public void imagesCopyright(View view) {
        // view the images copyright list  initializing InfoImagesCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        InfoImagesCopyrightListFragment frag= new InfoImagesCopyrightListFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    /**
     * Called when the user taps the sounds copyright button from info.
     * </p>
     * the activity is notified to view the sounds copyright list.
     * </p>
     *
     * @param view view of tapped button
     * @see InfoFragment
     * @see InfoSoundsCopyrightListFragment
     */
    public void soundsCopyright(View view) {
        // view the sounds copyright list  initializing InfoSoundsCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        InfoSoundsCopyrightListFragment frag= new InfoSoundsCopyrightListFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    //
    /**
     * Called when the user taps the videos copyright button from info.
     * </p>
     * the activity is notified to view the videos copyright list.
     * </p>
     *
     * @param view view of tapped button
     * @see InfoFragment
     * @see InfoVideosCopyrightListFragment
     */
    public void videosCopyright(View view) {
        // view the videos copyright list  initializing InfoVideosCopyrightListFragment (FragmentTransaction
        // switch between Fragments).
        InfoVideosCopyrightListFragment frag= new InfoVideosCopyrightListFragment();
        //
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
