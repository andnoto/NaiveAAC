package com.sampietro.NaiveAAC.activities.Game.Utils;

import static androidx.core.content.FileProvider.getUriForFile;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.sampietro.NaiveAAC.activities.Info.EulaActivity;
import com.sampietro.NaiveAAC.activities.Info.InfoActivity;
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        //
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
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
                try {
                    copyPdfFromAssetsToInternalStorage("naive aac manuale istruzioni.pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            case R.id.MENU_INFO:
                /*
                navigate to info screen (InfoActivity)
                */
                Intent intent3 = new Intent(getActivity(), InfoActivity.class);
                startActivity(intent3);
                return true;
            case R.id.MENU_EULA:
                /*
                navigate to eula screen (MainActivity)
                */
                Intent intent4 = new Intent(getActivity(), EulaActivity.class);
                startActivity(intent4);
                return true;
            case R.id.MENU_PRIVACY:
                 /*
                navigate to privacy policy
                */
                try {
                    copyPdfFromAssetsToInternalStorage("privacy.pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert getApplicationContext() != null;
                File pdfFilePrivacy = new File(getApplicationContext().getFilesDir(),"privacy.pdf");
                if (pdfFilePrivacy.exists())
                {
                    Uri pdfUri = getUriForFile(requireContext(), "com.example.NaiveAAC.fileprovider", pdfFilePrivacy);
                    Intent intent5 = new Intent(Intent.ACTION_VIEW);
                    intent5.setDataAndType(pdfUri, "application/pdf");
                    intent5.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent5);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
      /**
       * copy file.
       * <p>
       *
       */
      private void copyPdfFromAssetsToInternalStorage(String nameOfThePdfFile) throws IOException {
          assert getApplicationContext() != null;
          InputStream sourceStream = getApplicationContext().getAssets().open("pdf" + "/" + nameOfThePdfFile);
          FileOutputStream destStream = getApplicationContext().openFileOutput(nameOfThePdfFile, Context.MODE_PRIVATE);
          byte[] buffer = new byte[1024];
          int read;
          while((read = sourceStream.read(buffer)) != -1) {
              destStream.write(buffer, 0, read);
          }
          destStream.close();
          sourceStream.close();
      }
}

