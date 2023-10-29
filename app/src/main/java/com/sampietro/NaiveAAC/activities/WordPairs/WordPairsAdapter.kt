package com.sampietro.NaiveAAC.activities.WordPairs

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import io.realm.Realm

/**
 * this adapter is the View "supplier" for the listview in the UI for word pairs list settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.WordPairsFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class WordPairsAdapter(private val context: Context, private val wordPairs: List<WordPairs>?, listview: ListView) :
    BaseAdapter() {
    /**
     * <h1>WordPairsAdapterInterface</h1>
     *
     * **WordPairsAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see WordPairsAdapter
     */
    interface WordPairsAdapterInterface {
        fun reloadWordPairsFragment()
    }

    lateinit var listener: WordPairsAdapterInterface

    //
//    private val wordPairs: List<WordPairs>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<WordPairs>
     *
     * @return int with size of list<WordPairs>
    </WordPairs></WordPairs> */
    override fun getCount(): Int {
        return wordPairs!!.size
    }

    /**
     * return the element of a specified index within the list<WordPairs>
     *
     * @param position int index within the list<WordPairs>
     * @return object with the element within the list<WordPairs>
    </WordPairs></WordPairs></WordPairs> */
    override fun getItem(position: Int): Any {
        return wordPairs!![position]
    }

    /**
     * return the hashCode of a specified index within the list<WordPairs>
     *
     * @param position int index within the list<WordPairs>
     * @return long with the the hashCode of the element within the list<WordPairs>
    </WordPairs></WordPairs></WordPairs> */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }
    // per prima cosa, viene controllato se la View passata in input è nulla e solo in questo
    // caso viene inizializzata con il LayoutInflater.
    // Questo aspetto è molto importante ai fini della salvaguardia delle risorse infatti
    // Android riciclerà quanto possibile le View già create.
    // Il LayoutInflater attua per i layout quello che abbiamo già visto fare per i menu
    // con il MenuInflater.
    // In pratica la View da creare verrà strutturata in base al “progetto” definito nel
    // layout XML indicatogli.
    // Dopo il blocco if, la View non sarà sicuramente nulla perciò procederemo al completamento
    // dei suoi campi.
    // I dati verranno prelevati dall’oggetto ArticleInfo di posizione position recuperato
    // mediante getItem, già implementato.
    // Al termine, getView restituirà la View realizzata.
    /**
     * return the view with the row corresponding to a specific index within the list<WordPairs>
     *
     * @param position int index within the list<WordPairs>
     * @param v view to inflate with the row corresponding to a specific index within the list<WordPairs>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<WordPairs>
     * @see getItem
     *
     * @see clickListenerDeleteWordPairs
    </WordPairs></WordPairs></WordPairs></WordPairs> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <WordPairs> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <WordPairs> of position position is set
//        var v = v

        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <WordPairs> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <WordPairs> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as WordPairs
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.word1 + " " + l.word2
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteWordPairs)
        //
        return v1
    }

    /**
     * listener for the delete button.
     *
     *
     * it deletes the selected element from the realm and
     * makes the callback to the activity to reload the fragment with the updated data
     *
     * @see View.OnClickListener
     *
     * @see WordPairsAdapterInterface.reloadWordPairsFragment
     */
    private val clickListenerDeleteWordPairs = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete
        realm = Realm.getDefaultInstance()
        var results = realm.where(WordPairs::class.java).findAll()
        //
        results = results.sort("word1")
        //
        realm.beginTransaction()
        val daCancellare = results[position]
        daCancellare!!.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadWordPairsFragment()
        //
    }

    /**
     * WordPairsAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
    */
    init {
//        this.wordPairs = wordPairs
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as WordPairsAdapterInterface
    }
}