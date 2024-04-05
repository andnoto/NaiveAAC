package com.sampietro.NaiveAAC.activities.Game.Game1

/**
 * <h1>Game1ArrayList</h1>
 *
 * **Game1ArrayList** for choice of words represents the arraylist object
 * to initialize the recyclerview
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 */
class Game1ArrayList {
    /**
     * represent the description displayed in the recycler view for game1.
     */
    var image_title: String? = null
    /**
     * represent the type of image displayed in the recycler view for game1.
     *
     *
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     *
     *
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     */
    var urlType: String? = null
    /**
     * represent the path of image displayed in the recycler view for game1.
     */
    var url: String? = null
    /**
     * true if the title represents a class.
     */
    var theTitleRepresentsAClass: Boolean? = null
}