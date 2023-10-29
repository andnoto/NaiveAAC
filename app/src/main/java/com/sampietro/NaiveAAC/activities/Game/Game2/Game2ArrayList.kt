package com.sampietro.NaiveAAC.activities.Game.Game2

/**
 * <h1>Game2ArrayList</h1>
 *
 * **Game2ArrayList** for choice of words represents the arraylist object
 * to initialize the recyclerview
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 */
class Game2ArrayList {
    /**
     * represent the description displayed in the recycler view for game2.
     */
    var image_title: String? = null
    /**
     * represent the type of image displayed in the recycler view for game2.
     *
     *
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     *
     *
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     */
    var urlType: String? = null
    /**
     * represent the path of image displayed in the recycler view for game2.
     */
    var url: String? = null
}