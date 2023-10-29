package com.sampietro.NaiveAAC.activities.Stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>VoiceToBeRecordedInStoriesViewModel</h1>
 *
 *
 * **VoiceToBeRecordedInStoriesViewModel** utility class for registration in Stories class.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @see Stories
 *
 * @version     4.0, 09/09/2023
 */
class VoiceToBeRecordedInStoriesViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<VoiceToBeRecordedInStories>()
    fun setItem(item: VoiceToBeRecordedInStories) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<VoiceToBeRecordedInStories> {
        return selectedItem
    } //
}