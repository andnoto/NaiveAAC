package com.sampietro.NaiveAAC.activities.Settings.Utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListenerWithoutConstructor
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ArrayList
import org.json.JSONException

/**
 * <h1>ImageSearchArasaacFragment</h1>
 *
 * **ImageSearchArasaacFragment** UI for game1
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithListenerWithoutConstructor
 */
class ImageSearchArasaacFragment() : FragmentAbstractClassWithListenerWithoutConstructor() {
    //
    var keywordToSearchArasaac: String? = null
    private var mRequestQueue: RequestQueue? = null

    //
    var createLists = ArrayList<Game1ArrayList>()

    //
    var keywordArasaacToSearch: EditText? = null

    /**
     * prepares the ui and makes the callback to the activity
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Game1ArrayList
     *
     * @see ImageSearchArasaacRecyclerViewAdapter
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_stories_arasaac, container, false)
        //
        val request: JsonArrayRequest
        //
        keywordArasaacToSearch =
            rootView.findViewById<View>(R.id.keywordarasaactosearch) as EditText
        //
        val bundle = this.arguments
        //
        if (bundle != null) {
            keywordToSearchArasaac = bundle.getString("keywordToSearchArasaac")
            keywordArasaacToSearch!!.setText(keywordToSearchArasaac)
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
            mRequestQueue = Volley.newRequestQueue(ctext)
            request = JsonArrayRequest(
                Request.Method.GET, url + keywordToSearchArasaac, null,
                { response ->

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
                    for (i in 0 until response.length()) {
                        try {
                            val j = response.getJSONObject(i)
                            val j_id = j.getInt("_id")
                            //
                            val jkeywords = j.getJSONArray("keywords")
                            val jkeywordObject = jkeywords.getJSONObject(0)
                            val jkeyword = jkeywordObject.getString("keyword")
                            val createList = Game1ArrayList()
                            createList.image_title = jkeyword
                            createList.urlType = "A"
                            createList.url = REMOTE_ADDR_PICTOGRAM + j_id + "?download=false"
                            createLists.add(createList)
                        } catch (e: JSONException) {
                        }
                    }
                    //
                    val recyclerView =
                        rootView.findViewById<View>(R.id.imagegallery) as RecyclerView
                    recyclerView.setHasFixedSize(true)
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
                    recyclerView.layoutManager = layoutManager
                    // ArrayList<Game1ArrayList> createLists = prepareData();
                    val adapter = ImageSearchArasaacRecyclerViewAdapter(ctext, createLists)
                    recyclerView.adapter = adapter
                }
            ) {
                //
            }
            //
            // Utilizziamo il metodo addToRequestQueue per aggiungere la richiesta JSON alla RequestQueue
            mRequestQueue!!.add(request)
        }
        //
        val buttonSearchArasaac =
            rootView.findViewById<View>(R.id.btn_search_arasaac) as ImageButton
        buttonSearchArasaac.setOnClickListener {
            listener.receiveResultSettings(buttonSearchArasaac)
        }
        //
        return rootView
    }

    companion object {
        //
        private const val url = "https://api.arasaac.org/api/pictograms/it/search/"
        private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"
    }
}