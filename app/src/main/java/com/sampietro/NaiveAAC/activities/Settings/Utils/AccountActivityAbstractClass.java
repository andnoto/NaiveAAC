package com.sampietro.NaiveAAC.activities.Settings.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.VideosFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Objects;

import io.realm.Realm;

/**
 * <h1>AccountActivityAbstractClass</h1>
 * <p><b>AccountActivityAbstractClass</b>
 * abstract class containing common methods that is extended by
 * </p>
 * 1) AccountActivity</p>
 * 2) AccountActivityRealmCreation</p>
 * 3) SettingsActivity</p>
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public abstract class AccountActivityAbstractClass extends AppCompatActivity
{
    //
    public View rootViewFragment;
    public Context context;
    public SharedPreferences sharedPref;
    // Realm
    public Realm realm;
    // ActivityResultLauncher
    public ActivityResultLauncher<Intent> imageSearchActivityResultLauncher;
    public ActivityResultLauncher<Intent> imageSearchGameParametersActivityResultLauncher;
    public ActivityResultLauncher<Intent> imageSearchAccountActivityResultLauncher;
    public ActivityResultLauncher<Intent> imageSearchStoriesActivityResultLauncher;
    public ActivityResultLauncher<Intent> videoSearchActivityResultLauncher;
    public ActivityResultLauncher<Intent> videoSearchWordPairsActivityResultLauncher;
    public ActivityResultLauncher<Intent> csvSearchActivityResultLauncher;
    public ActivityResultLauncher<Intent> exportCsvSearchActivityResultLauncher;
    public Uri csvTreeUri;
    //
    public Uri uri;
    public String stringUri;
    //
    public String gameIconType;
    //
    public String filePath;
    public String fileName;
    public byte[] byteArray;
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4147849/muntashir-akon">Muntashir Akon</a>
     * <p>
     * and to <a href="https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/840861/uttam">Uttam</a>
     *
     * @see #getFilePath
     * @see #showImage
     */
    public void setActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        imageSearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            filePath = getString(R.string.non_trovato);
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                try {
                                    filePath = getFilePath(context, uri);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // vedi https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                //
                                ImageView myImage;
                                myImage = (ImageView) findViewById(R.id.imageviewTest);
                                showImage(uri, myImage);
                            }
                        }
                    }
                });
        //
        imageSearchGameParametersActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            filePath = getString(R.string.non_trovato);
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                try {
                                    filePath = getFilePath(context, uri);
                                    gameIconType = "S";
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                //
                                ImageView myImage;
                                myImage = (ImageView) findViewById(R.id.imageviewgameicon);
                                showImage(uri, myImage);
                            }
                        }
                    }
                });
        //
        imageSearchAccountActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            filePath = getString(R.string.non_trovato);
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                try {
                                    filePath = getFilePath(context, uri);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                //
                                ImageView myImage;
                                myImage = (ImageView) findViewById(R.id.imageviewaccounticon);
                                showImage(uri, myImage);
                            }
                        }
                    }
                });
        //
        imageSearchStoriesActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            filePath = getString(R.string.non_trovato);
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                try {
                                    filePath = getFilePath(context, uri);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                //
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byteArray = stream.toByteArray();
                                //
                                EditText uriTypeToAdd = findViewById(R.id.uritypetoadd);
                                EditText uriToAdd = findViewById(R.id.uritoadd);
                                uriTypeToAdd.setText("S");
                                uriToAdd.setText(filePath);
                            }
                        }
                    }
                });
        //
        videoSearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            stringUri = null;
                            //
                            EditText vidD=(EditText) findViewById(R.id.videoDescription);
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                stringUri = uri.toString();
                                //
                                VideosFragment frag= new VideosFragment();
                                Bundle bundle = new Bundle();
                                //
                                if (vidD.length()>0)
                                    {
                                        bundle.putString(getString(R.string.descrizione), vidD.getText().toString());
                                    }
                                    else
                                    {
                                        bundle.putString(getString(R.string.descrizione), getString(R.string.nessuna));
                                    }
                                //
                                bundle.putString(getString(R.string.uri), stringUri);
                                frag.setArguments(bundle);
                                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.settings_container, frag);
                                ft.addToBackStack(null);
                                ft.commit();
                                //
                            }
                        }
                    }
                });
        videoSearchWordPairsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = result.getData();
                            // doSomeOperations();
                            uri = null;
                            stringUri = null;
                            //
                            if (resultData != null) {
                                uri = Objects.requireNonNull(resultData).getData();
                                //
                                //
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final int takeFlags = resultData.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                }
                                //
                                stringUri = uri.toString();
                                //
                                EditText awardType = findViewById(R.id.awardtype);
                                TextView uriPremiumVideo = findViewById(R.id.uripremiumvideo);
                                try {
                                    filePath = getFilePath(context, uri);
                                    //
                                    assert filePath != null;
                                    int cut = filePath.lastIndexOf('/');
                                    if (cut != -1) {
                                        fileName = filePath.substring(cut + 1);
                                    }
                                    //
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                awardType.setText(getString(R.string.character_v));
                                uriPremiumVideo.setText(fileName);
                            }
                        }
                    }
                });
    }
    /**
     * closing realm to be performed on destroy activity
     *
     * @see Activity#onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    /**
     * register the last player name on shared preferences.
     *
     * @param textPersonName string containing the player's name
     */
    public void registerPersonName (String textPersonName) {
        //    context = this;
            sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.preference_LastPlayer), textPersonName);
            editor.apply();
        }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    public void imageSearch(View v)
    {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        //
        switch(v.getId()){
            case R.id.buttonimagesearch:
                /*  the instructions of the button */
                imageSearchActivityResultLauncher.launch(intent);
                break;
            case R.id.buttonimagesearchgameparameters:
                /*  the instructions of the button */
                imageSearchGameParametersActivityResultLauncher.launch(intent);
                break;
            case R.id.buttonimagesearchaccount:
                /*  the instructions of the button */
                imageSearchAccountActivityResultLauncher.launch(intent);
                break;
            case R.id.buttonimagesearchstories:
                /*  the instructions of the button */
                imageSearchStoriesActivityResultLauncher.launch(intent);
                break;
        }
    }
    /**
     * show the chosen image identified by uri.
     * <p>
     * A Uniform Resource Identifier (URI) is a compact sequence of characters that identifies an abstract or physical resource
     *
     * @param uri uri of the image
     * @param myImage ImageView where the image will be displayed
     * @see #scaleImage
     */
    public void showImage(Uri uri, ImageView myImage) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    // On devices running SDK < 24 (Android 7.0), this method will fail to apply
    // correct density scaling to images loaded from content and file schemes.
    // Applications running on devices with SDK >= 24 MUST specify the
    // targetSdkVersion in their manifest as 24 or above for density scaling
    // to be applied to images loaded from these schemes.
        myImage.setImageBitmap(bitmap);
        scaleImage(bitmap, myImage);
    }
    /**
     * scale the image represented in the bitmap.
     * Refer to <a href="https://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/1030049/jarno-argillander">Jarno Argillander</a>
     *
     * @param bitmap bitmap of the image
     * @param view imageview where the image will be displayed
     * @see #dpToPx
     */
    private void scaleImage(Bitmap bitmap, ImageView view) throws NoSuchElementException {
        // Get current dimensions AND the desired bounding box
        int width = 0;
        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }
        int height = bitmap.getHeight();
        int bounding = dpToPx(150);
        // Log.i("Test", "original width = " + Integer.toString(width));
        // Log.i("Test", "original height = " + Integer.toString(height));
        // Log.i("Test", "bounding = " + Integer.toString(bounding));
        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        // Log.i("Test", "xScale = " + Float.toString(xScale));
        // Log.i("Test", "yScale = " + Float.toString(yScale));
        // Log.i("Test", "scale = " + Float.toString(scale));
        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(context.getResources(),scaledBitmap);
        // Log.i("Test", "scaled width = " + Integer.toString(width));
        // Log.i("Test", "scaled height = " + Integer.toString(height));
        // Apply the scaled bitmap
        view.setImageDrawable(result);
        // Now change ImageView's dimensions to match the scaled image
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        // Log.i("Test", "done");
    }
    /**
     * calculate the dimensions of desired bounding box.
     * </p>
     *
     * @param dp int with dimensions of desired bounding box
     * @return int with dimensions of desired bounding box in px
     */
    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
    /**
     * get the FilePath from the uri.
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4118297/jitendra-ramoliya">Jitendra Ramoliya</a>
     * <p>
     *
     * @param context context
     * @param uri uri
     * @return string with the filepath identified by uri from mediastore
     */
    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
    String selection = null;
    String[] selectionArgs = null;
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            return Environment.getExternalStorageDirectory() + "/" + split[1];
        } else if (isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        } else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            if ("image".equals(type)) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            selection = "_id=?";
            selectionArgs = new String[]{
                    split[1]
            };
        }
    }
    if ("content".equalsIgnoreCase(uri.getScheme())) {
        if (isGooglePhotosUri(uri)) {
            return uri.getLastPathSegment();
        }
        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }
    return null;
    }
    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to External Storage Document
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to downloaded documents
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to media documents
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * gets document authority.
     *
     * @param uri uri
     * @return boolean true if uri refers to photos
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    //
    /**
     * copy file.
     * <p>
     *
     * @param sourceUri uri
     * @param destFileName string
     */
    public void copyFileFromSharedToInternalStorage(Uri sourceUri, String destFileName) throws IOException {
        InputStream sourceStream = context.getContentResolver().openInputStream(sourceUri);
        FileOutputStream destStream = openFileOutput(destFileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[1024];
        int read;
        while((read = sourceStream.read(buffer)) != -1) {
            destStream.write(buffer, 0, read);
        }
        destStream.close();
        sourceStream.close();
    }
    //
    /**
     * copy file.
     * <p>
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    public void copyFileFromInternalToSharedStorage(DocumentFile outputFolder, String destFileName) throws IOException {
        InputStream sourceStream = context.openFileInput(destFileName);
        DocumentFile documentFileNewFile = outputFolder.createFile("text/csv", destFileName);
        assert documentFileNewFile != null;
        OutputStream destStream = context.getContentResolver().openOutputStream(documentFileNewFile.getUri());
        byte[] buffer = new byte[1024];
        int read;
        while((read = sourceStream.read(buffer)) != -1) {
            destStream.write(buffer, 0, read);
        }
        destStream.close();
        sourceStream.close();
    }
}
