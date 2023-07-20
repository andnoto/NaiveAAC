package com.sampietro.NaiveAAC.activities.Main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.sampietro.NaiveAAC.BuildConfig;
import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Stories.Stories;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * <h1>MyApplication</h1>
 * <p><b>MyApplication</b> When the application process is started,
 * this class is instantiated before any of the application's components.
 */
public class MyApplication extends Application {
    /**
     * configurations :
     * </p>
     * 1) on first use copy defaultrealm from assets
     * 2) Realm init
     * 3) create RealmConfiguration
     * <p>
     * Refer to <a href="https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample">github</a>
     * commits by <a href="https://github.com/cmelchior">Christian Melchior</a>
     *
     * @see Application#onCreate
     * @see #copyFileRealm()
     * @see customRealmMigration
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //
        boolean itSAFreshInstall = false;
        File fileRealm = new File(this.getFilesDir(), "default.realm");
        if (!fileRealm.exists()) {
            // it's a fresh install
            itSAFreshInstall = true;
            try {
                copyFileRealm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //
        Realm.init(this);
        RealmConfiguration realmConfiguration =
                new RealmConfiguration.Builder()
                        .name("default.realm")
                        .schemaVersion(5)
                        .migration(new customRealmMigration())
                        //     .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        //
        if (itSAFreshInstall)
            {
            Context context = this;
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            // register current VersionCode on sharedpref
            editor.putInt("preference_VersionCode", BuildConfig.VERSION_CODE);
            editor.apply();
            }
        else
            {
            appVersionManagement();
            }
    }
    //
    /**
     * copy defaultrealm from assets
     */
    public void copyFileRealm() throws IOException {
        InputStream sourceStream = getAssets().open("copiarealm");
        FileOutputStream destStream = openFileOutput("default.realm", Context.MODE_PRIVATE);
        byte[] buffer = new byte[1024];
        int read = sourceStream.read(buffer);
        while (read != -1) {
            destStream.write(buffer, 0, read);
            read = sourceStream.read(buffer);
        }
        destStream.close();
        sourceStream.close();
        //
    }
    //
    /**
     * app version management
     */
    public void appVersionManagement() {
        int oldVersionCode = 0;
        //
        int currentVersionCode = BuildConfig.VERSION_CODE;
        //
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasVersionCode = sharedPref.contains("preference_VersionCode");
        if (hasVersionCode)
            {
            // I record the old version code (the version code is present from version 7)
            oldVersionCode =
                    sharedPref.getInt ("preference_VersionCode", 0);
            }
        //
        if (oldVersionCode == 0) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 1) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 2) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 3) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 4) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 5) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 6) {
            // rename video
//            Realm realm= Realm.getDefaultInstance();
//            RealmResults<Videos> results =
//                    realm.where(Videos.class).equalTo(getString(R.string.descrizione), "pensieri e parole").findAll();
//            int resultsSize = results.size();
//            if (resultsSize != 0) {
//                realm.beginTransaction();
                // crash
//                Videos daRinominare=results.get(0);
//                assert daRinominare != null;
//                daRinominare.setDescrizione("AUTISMO");
//                realm.commitTransaction();
//            }
            // rename game parameters
//            RealmResults<GameParameters> resultsgp1 =
//                    realm.where(GameParameters.class).equalTo("gameName", "pensieri e parole").findAll();
//            int resultsgp1Size = resultsgp1.size();
//            if (resultsgp1Size != 0) {
//                realm.beginTransaction();
//                GameParameters daRinominaregp1=resultsgp1.get(0);
//                assert daRinominaregp1 != null;
//                daRinominaregp1.setGameName("NAVIGATORE");
//                daRinominaregp1.setGameJavaClass("NAVIGATORE");
//                realm.commitTransaction();
//            }
//            RealmResults<GameParameters> resultsgp2 =
//                    realm.where(GameParameters.class).equalTo("gameName", "parole in libertà").findAll();
//            int resultsgp2Size = resultsgp2.size();
//            if (resultsgp2Size != 0) {
//                realm.beginTransaction();
//                GameParameters daRinominaregp2=resultsgp2.get(0);
//                assert daRinominaregp2 != null;
//                daRinominaregp2.setGameName("COMUNICATORE");
//                daRinominaregp2.setGameJavaClass("COMUNICATORE");
//                realm.commitTransaction();
//            }
            //
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 7) {
            try {
                copyFileFromAssetsToInternalStorage("images", "racconto.png", "racconto.png");
                copyFileFromAssetsToInternalStorage("images", "non.png", "non.png");
                //
//                copyFileFromAssetsToInternalStorage("csv", "toaddversion8-images.csv", "images.csv");
//                copyFileFromAssetsToInternalStorage("csv", "toaddversion8-grammaticalexceptions.csv", "grammaticalexceptions.csv");
                copyFileFromAssetsToInternalStorage("csv", "toaddversion8-stories.csv", "stories.csv");
//                copyFileFromAssetsToInternalStorage("csv", "toaddversion8-gameparameters.csv", "gameparameters.csv");

            } catch (IOException e) {
                e.printStackTrace();
            }
            Realm realm= Realm.getDefaultInstance();
//            Images.importFromCsvFromInternalStorage(context, realm, "Append");
//            GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, "Append");
            Stories.importFromCsvFromInternalStorage(context, realm, "Append");
//            GameParameters.importFromCsvFromInternalStorage(context, realm, "Append");
            //
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 8) {
            // fix error in the transition from version 7 to 8 and update game parameters
            //
            // delete image
            Realm realm= Realm.getDefaultInstance();
//            RealmResults<Images> results =
//                    realm.where(Images.class).equalTo("descrizione", "negazione").findAll();
//            realm.beginTransaction();
//            results.deleteAllFromRealm();
//            realm.commitTransaction();
            //
            try {
//                copyFileFromAssetsToInternalStorage("csv", "toaddversion8-images.csv", "images.csv");
                copyFileFromAssetsToInternalStorage("csv", "grammaticalexceptions.csv", "grammaticalexceptions.csv");
//                copyFileFromAssetsToInternalStorage("csv", "stories.csv", "stories.csv");
//                copyFileFromAssetsToInternalStorage("csv", "gameparameters.csv", "gameparameters.csv");

            } catch (IOException e) {
                e.printStackTrace();
            }
//            Images.importFromCsvFromInternalStorage(context, realm, "Append");
            GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, "Replace");
//            Stories.importFromCsvFromInternalStorage(context, realm, "Replace");
//            GameParameters.importFromCsvFromInternalStorage(context, realm, "Replace");
            //
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 9) {
            // delete story
//            Realm realm= Realm.getDefaultInstance();
//            RealmResults<Stories> results =
//                    realm.where(Stories.class).equalTo("story", "il gatto con gli stivali").findAll();
//            realm.beginTransaction();
//            results.deleteAllFromRealm();
//            realm.commitTransaction();
//            // replace story from csv in assets
//            try {
//                copyFileFromAssetsToInternalStorage("csv", "toaddversion10-stories.csv", "stories.csv");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Stories.importFromCsvFromInternalStorage(context, realm, "Append");
            //
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 10) {
            //
            Realm realm= Realm.getDefaultInstance();
            // delete story
             RealmResults<Stories> results =
                    realm.where(Stories.class).equalTo("story", "il gatto con gli stivali").findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
            // replace story from csv in assets
            try {
                copyFileFromAssetsToInternalStorage("sounds", "cat-meow-14536.mp3", "cat-meow-14536.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "fiaker-73119.mp3", "fiaker-73119.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "Hallelujah.mp3", "Hallelujah.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "jumento-101690.mp3", "jumento-101690.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "lion-roar-6011.mp3", "lion-roar-6011.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "river-stream-moderate-flow-2-24370.mp3", "river-stream-moderate-flow-2-24370.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "scary-laugh-123862.mp3", "scary-laugh-123862.mp3");
                copyFileFromAssetsToInternalStorage("sounds", "Wedding March.mp3", "Wedding March.mp3");
                copyFileFromAssetsToInternalStorage("images", "punto interrogativo.png", "punto interrogativo.png");
                copyFileFromAssetsToInternalStorage("videos", "cat-4916.mp4", "cat-4916.mp4");
                copyFileFromAssetsToInternalStorage("videos", "cat-92641.mp4", "cat-92641.mp4");
                copyFileFromAssetsToInternalStorage("videos", "man-131605.mp4", "man-131605.mp4");
                copyFileFromAssetsToInternalStorage("videos", "mouse-117573.mp4", "mouse-117573.mp4");
                copyFileFromAssetsToInternalStorage("videos", "rabbit-141719.mp4", "rabbit-141719.mp4");
                copyFileFromAssetsToInternalStorage("videos", "tags-bird-9267.mp4", "tags-bird-9267.mp4");
                copyFileFromAssetsToInternalStorage("csv", "sounds.csv", "sounds.csv");
                copyFileFromAssetsToInternalStorage("csv", "images.csv", "images.csv");
                copyFileFromAssetsToInternalStorage("csv", "videos.csv", "videos.csv");
                copyFileFromAssetsToInternalStorage("csv", "toaddversion10-stories.csv", "stories.csv");
                copyFileFromAssetsToInternalStorage("csv", "gameparameters.csv", "gameparameters.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // eliminazione di tutte le immagini tranne quelle relative ad "io" e a nome utente
            String sharedLastPlayer =
                    sharedPref.getString (getString(R.string.preference_LastPlayer), "DEFAULT");
            RealmResults<Images> resultsImages =
                    realm.where(Images.class)
                            .equalTo("descrizione", "io")
                            .or()
                            .equalTo("descrizione", sharedLastPlayer)
                            .findAll();
            int imagesSize = resultsImages.size();
            List<Images> resultsImagesList = realm.copyFromRealm(resultsImages);
            // clear the table
            RealmResults<Images> daCancellare = realm.where(Images.class).findAll();
            realm.beginTransaction();
            daCancellare.deleteAllFromRealm();
            realm.commitTransaction();
            //
            int irrh=0;
            while(irrh < imagesSize)   {
                Images resultImages = resultsImagesList.get(irrh);
                assert resultImages != null;
                realm.beginTransaction();
                realm.copyToRealm(resultImages);
                realm.commitTransaction();
                irrh++;
            }
            //
            Sounds.importFromCsvFromInternalStorage(context, realm, "Replace");
            Images.importFromCsvFromInternalStorage(context, realm, "Append");
            Videos.importFromCsvFromInternalStorage(context, realm, "Replace");
            Stories.importFromCsvFromInternalStorage(context, realm, "Append");
            GameParameters.importFromCsvFromInternalStorage(context, realm, "Replace");
            //
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 11) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 12) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 13) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 14) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode < currentVersionCode) {
            throw new IllegalStateException(String.format(Locale.US, "Migration missing from v%d to v%d", oldVersionCode, currentVersionCode));
        }


        // register current VersionCode on sharedpref
        editor.putInt("preference_VersionCode", currentVersionCode);
        editor.apply();
    }
    //
    /**
     * copy the single file from assets
     * @param dirName string with the name of the directory of the file to be copied
     * @param fileName string with the name of the file to be copied
     * @param destFileName string with the name of the destination file
     */
    public void copyFileFromAssetsToInternalStorage(String dirName, String fileName, String destFileName) throws IOException {

        InputStream sourceStream = getAssets().open(dirName + getString(R.string.character_slash) + fileName);
        FileOutputStream destStream = openFileOutput(destFileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[100];
        int bytesRead=0;
        while ( (bytesRead=sourceStream.read(buffer))!=-1) {
            destStream.write(buffer,0,bytesRead);
        }
        destStream.close();
        sourceStream.close();

    }
}