package com.sampietro.NaiveAAC.activities.Game.GameADA

/**
 * <h1>GameADAArrayList</h1>
 *
 * **GameADAArrayList** for choice of words represents the arraylist object
 * to initialize the recyclerview
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 */
class GameADAArrayList {
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
    /**
     * refers to a video key in the videos table .
     */
    var video: String? = null
    /**
     * refers to a sound key in the sounds table .
     */
    var sound: String? = null
    /**
     * indicates whether sound replaces TTS (Y or N).
     */
    var soundReplacesTTS: String? = null
    /**
     * indicates whether the sound associated with the phrase replaces the other sounds (Y or N).
     */
    var soundAssociatedWithThePhraseReplacesTheOtherSounds: String? = null
}