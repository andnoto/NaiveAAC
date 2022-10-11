package com.example.NaiveAAC.activities.Info;

import android.app.Activity;
import android.os.Bundle;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.example.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>EulaActivity</h1>
 * <p><b>EulaActivity</b> app Eula info.</p>
 *
 * @version     1.0, 06/13/22
 * @see InfoActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 */
public class EulaActivity extends InfoActivityAbstractClass
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
                    .add(R.id.settings_container, new EulaFragment(), "EulaFragment")
                    .commit();
            }
    }
//
}
