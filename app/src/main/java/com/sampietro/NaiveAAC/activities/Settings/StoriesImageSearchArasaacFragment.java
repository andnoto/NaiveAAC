package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ArrayList;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <h1>StoriesImageSearchArasaacFragment</h1>
 * <p><b>StoriesImageSearchArasaacFragment</b> UI for game1
 * </p>
 *
 * @version     1.3, 05/05/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 */
public class StoriesImageSearchArasaacFragment extends SettingsFragmentAbstractClass {
    //
    public String keywordToSearchArasaac;
    //
    private static final String url=
            "https://api.arasaac.org/api/pictograms/it/search/";
    private static final String REMOTE_ADDR_PICTOGRAM=
            "https://api.arasaac.org/api/pictograms/";
    private RequestQueue mRequestQueue=null;
    //
    public ArrayList<Game1ArrayList> createLists = new ArrayList<>();
    //
    public EditText keywordArasaacToSearch;
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
     * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Game1ArrayList
     * @see StoriesImageSearchArasaacRecyclerViewAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories_arasaac, container, false);
        //
        JsonArrayRequest request;
        //
        keywordArasaacToSearch = (EditText)rootView.findViewById(R.id.keywordarasaactosearch);
        //
        Bundle bundle = this.getArguments();
        //
        if (bundle != null) {
            keywordToSearchArasaac = bundle.getString("keywordToSearchArasaac");
            keywordArasaacToSearch.setText(keywordToSearchArasaac);
            //
            // inserire qui ricerca su arasaac
            // Il protocollo HTTP come sappiamo è basato sull’interazione tra client e server in termini
            // di richiesta-risposta:
            // con un derivato della classe Request formuleremo la richiesta (ad esempio, la risorsa
            // che vogliamo leggere) e l’accoderemo nella RequestQueue.
            // Sarà poi Volley ad eseguirla appena possibile, secondo le sue politiche e le condizioni del sistema.
            //
            // In ogni oggetto Request, saranno inclusi i riferimenti a due listener:
            // uno derivante da Response.Listener invocato nel caso in cui la richiesta venga svolta con
            // successo; l’altro, ErrorListener, entra in causa in caso di errori.
            mRequestQueue = Volley.newRequestQueue(ctext);
            request = new JsonArrayRequest(Request.Method.GET, url+keywordToSearchArasaac, null,
                    new Response.Listener<JSONArray>() {
                        //
                        // Come si vede, abbiamo utilizzato la classe JsonObjectRequest per costruire una richiesta
                        // HTTP GET verso l’URL specificato nella stringa url.
                        // Se la richiesta va a buon fine, viene eseguita la callback associata all’evento onResponse,
                        // che in questo caso si limita a valorizzare la stringa result con il
                        // contenuto del file JSON così ottenuto.
                        // D’ora in avanti, quindi, supporremo che i nostri contenuti formattati in JSON siano
                        // memorizzati nella stringa result.
                        // JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        //        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject j = response.getJSONObject(i);
                                    int j_id = j.getInt("_id");
                                    //
                                    JSONArray jkeywords = j.getJSONArray("keywords");
                                    JSONObject jkeywordObject = jkeywords.getJSONObject(0);
                                    String jkeyword = jkeywordObject.getString("keyword");
                                    Game1ArrayList createList = new Game1ArrayList();
                                    createList.setImage_title(jkeyword);
                                    createList.setUrlType("A");
                                    createList.setUrl(REMOTE_ADDR_PICTOGRAM + j_id +"?download=false");
                                    createLists.add(createList);
                                } catch (JSONException e) {
                                }
                            }
                            //
                            RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.imagegallery);
                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager =
                                    new LinearLayoutManager(ctext);
                            recyclerView.setLayoutManager(layoutManager);
                            // ArrayList<Game1ArrayList> createLists = prepareData();
                            StoriesImageSearchArasaacRecyclerViewAdapter adapter =
                                    new StoriesImageSearchArasaacRecyclerViewAdapter(ctext, createLists);
                            recyclerView.setAdapter(adapter);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        //
                        }
                    });
            //
            // Utilizziamo il metodo addToRequestQueue per aggiungere la richiesta JSON alla RequestQueue
            mRequestQueue.add(request);
        }
        //
        listener.receiveResultSettings(rootView);
        return rootView;
    }

}

