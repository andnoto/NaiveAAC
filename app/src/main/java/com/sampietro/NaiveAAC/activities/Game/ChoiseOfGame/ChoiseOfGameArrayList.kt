package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

/**
 * <h1>ChoiseOfGameArrayList</h1>
 *
 * **ChoiseOfGameArrayList** for game choice represents the arraylist object
 * to initialize the recyclerview using data from the gameparameters class
 *
 * REFER to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 *
 * @see ChoiseOfGameFragment
 */
class ChoiseOfGameArrayList {
    /**
     * represent the description displayed in the view for game choice.
     */
    var image_title: String? = null
    /**
     * represent the type of icon displayed in the view for game choice.
     *
     *
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     *
     *
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     *
     *
     * gameIconType = AS then , if it exists, gameIconPath refers to assets
     */
    var urlType: String? = null
    /**
     * represent the path of icon displayed in the view for game choice.
     */
    var url: String? = null
    /**
     * if applicable, it contains the video file path (originally contained the uri).
     */
    var urlVideo: String? = null
    /**
     * if applicable, it contains the video copyright information.
     */
    var videoCopyright: String? = null
}