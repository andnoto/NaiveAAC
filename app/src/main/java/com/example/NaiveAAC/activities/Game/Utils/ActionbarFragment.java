package com.example.NaiveAAC.activities.Game.Utils;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot;

import static io.realm.Realm.getApplicationContext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.example.NaiveAAC.activities.Settings.SettingsActivity;

import java.io.File;
import java.util.Objects;

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
    public File root;
    public String rootPath;
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
            case R.id.MENU_MANUAL:
                /*
                navigate to manual
                */
                assert getApplicationContext() != null;
                File pdfFile = new File(getApplicationContext().getFilesDir(),"naive aac manuale istruzioni.pdf");
                if (pdfFile.exists())
                {
                    Uri pdfUri = getUriForFile(requireContext(), "com.example.NaiveAAC.fileprovider", pdfFile);
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setDataAndType(pdfUri, "application/pdf");
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent1);
                }
                return true;
            case R.id.MENU_HOME:
                /*
                navigate to home screen (MainActivity)
                */
                Intent intent2 = new Intent(getActivity(), ChoiseOfGameActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

