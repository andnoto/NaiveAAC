package com.example.NaiveAAC.activities.Game.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.example.NaiveAAC.activities.Settings.SettingsActivity;

/**
 * <h1>ActionbarFragment</h1>
 * <p><b>ActionbarFragment</b> UI for Action Bar
 * </p>
 * Refer to <a href="https://developer.android.com/guide/fragments/appbar">developer.android.com</a>
 *
 * @version     1.3, 05/05/22
 * @see Fragment
 * @see Game1Activity
 */
public class ActionbarFragment extends Fragment {
    /**
     * inform the system that your app bar fragment is participating in the population of the options menu.
     * <p>
     *
     * @see Fragment#onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    /**
     * merge your menu into the app bar's options menu.
     * <p>
     *
     * @see Fragment#onCreateOptionsMenu
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.activity_display_message_menu_main, menu);
    }
    /**
     * receives the selected menu item as a parameter and returns a boolean to indicate whether or not the touch has been consumed.
     * <p>
     *
     * @see Fragment#onOptionsItemSelected
     * @see SettingsActivity
     * @see ChoiseOfGameActivity
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();
        switch(id)
        {
            case R.id.MENU_SETTINGS:
                /*
                navigate to settings screen
                */
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.MENU_HOME:
                /*
                navigate to home screen (MainActivity)
                */
                Intent intent1 = new Intent(getActivity(), ChoiseOfGameActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

