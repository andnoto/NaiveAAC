package com.sampietro.NaiveAAC.activities.Info;

import android.app.Activity;
import android.os.Bundle;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>PrivacyActivity</h1>
 * <p><b>PrivacyActivity</b> app Privacy info.</p>
 *
 * @version     1.0, 06/13/22
 * @see InfoActivityAbstractClass
 */
public class PrivacyActivity extends InfoActivityAbstractClass
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
                    .add(R.id.settings_container, new PrivacyFragment(), "PrivacyFragment")
                    .commit();
            }
    }
//
}
