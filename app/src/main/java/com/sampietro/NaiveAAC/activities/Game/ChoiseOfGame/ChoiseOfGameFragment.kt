package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import java.util.Locale

/**
 * <h1>ChoiseOfGameFragment</h1>
 *
 * **ChoiseOfGameFragment** UI for game choice
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameFragmentAbstractClass
 *
 * @see ChoiseOfGameActivity
 */
class ChoiseOfGameFragment(@LayoutRes contentLayoutId : Int = 0) : GameFragmentAbstractClass(contentLayoutId) {
    private var row1debugGameName = arrayOfNulls<String>(48)
    private var row1debugGameIconType = arrayOfNulls<String>(48)
    private var row1debugGameIconPath = arrayOfNulls<String>(48)
    private var row1debugGameInfo = arrayOfNulls<String>(48)
    private var row1debugGameVideoPath = arrayOfNulls<String>(48)
    private var row1debugGameVideoCopyright = arrayOfNulls<String>(48)

    //
    private var recyclerView: ChoiseOfGameRecyclerView? = null

    /**
     * prepares the ui
     *
     *
     * REFER to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see ChoiseOfGameRecyclerView
     *
     * @see initRecyclerView
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        //
        recyclerView = view.findViewById<View>(R.id.recycler_view) as ChoiseOfGameRecyclerView
        recyclerView!!.setHasFixedSize(true)
        initRecyclerView()
    }

    /**
     * initializes the recyclerview using data from the gameparameters class
     *
     * @see prepareData
     *
     * @see ChoiseOfGameArrayList
     *
     * @see ChoiseOfGameRecyclerViewAdapter
     */
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        //
        val createLists = prepareData()
        recyclerView!!.setMediaObjects(createLists)
        val adapter = ChoiseOfGameRecyclerViewAdapter(ctext, createLists)
        recyclerView!!.adapter = adapter
        //
        recyclerView!!.smoothScrollBy(1, 0)
    }

    /**
     * prepare data for recyclerview using data from the gameparameters class
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see ChoiseOfGameArrayList
     *
     * @see GameParameters
     *
     * @see Videos
    */
    private fun prepareData(): ArrayList<ChoiseOfGameArrayList> {
        var debugUrlNumber = 0
        //
        var results = realm.where(GameParameters::class.java)
            .beginGroup()
            .equalTo(getString(R.string.gameactive), "A")
            .endGroup()
            .findAll()
        results = results.sort(getString(R.string.gamename))
        val count = results.size
        //
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                val result = results[irrh]!!
                row1debugGameName[irrh] = result.gameName
                row1debugGameIconType[irrh] = result.gameIconType
                row1debugGameIconPath[irrh] = result.gameIconPath
                row1debugGameInfo[irrh] = result.gameInfo
                debugUrlNumber++
                //
                val resultsVideo = realm.where(Videos::class.java)
                    .equalTo(getString(R.string.descrizione), row1debugGameName[irrh]).findAll()
                val countVideo = resultsVideo.size
                if (countVideo != 0) {
                    val resultVideo = resultsVideo[0]!!
                    row1debugGameVideoPath[irrh] = resultVideo.uri
                    row1debugGameVideoCopyright[irrh] = resultVideo.copyright
                } else {
                    row1debugGameVideoPath[irrh] = getString(R.string.non_trovato)
                }
                irrh++
            }
        }
        //
        val theimage = ArrayList<ChoiseOfGameArrayList>()
        for (i in 0 until debugUrlNumber) {
            val createList = ChoiseOfGameArrayList()
            createList.image_title = row1debugGameName[i]!!.uppercase(Locale.getDefault())
            createList.urlType = row1debugGameIconType[i]
            createList.url = row1debugGameIconPath[i]
            createList.urlVideo = row1debugGameVideoPath[i]
            createList.videoCopyright = row1debugGameVideoCopyright[i]
            theimage.add(createList)
        }
        return theimage
    }

    /**
     * release the video player
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     *
     * @see ChoiseOfGameRecyclerView.releasePlayer
     */
    override fun onDestroy() {
        if (recyclerView != null) recyclerView!!.releasePlayer()
        super.onDestroy()
    } //
}