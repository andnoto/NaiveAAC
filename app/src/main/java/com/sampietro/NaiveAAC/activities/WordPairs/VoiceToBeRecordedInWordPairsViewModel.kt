package com.sampietro.NaiveAAC.activities.WordPairs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>VoiceToBeRecordedInListsOfNamesViewModel</h1>
 *
 *
 * **VoiceToBeRecordedInWordPairsViewModel** utility class for registration in WordPairs class.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @see WordPairs
 *
 * @version     5.0, 01/04/2024
 */
class VoiceToBeRecordedInWordPairsViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<VoiceToBeRecordedInWordPairs>()
    fun setItem(item: VoiceToBeRecordedInWordPairs) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<VoiceToBeRecordedInWordPairs> {
        return selectedItem
    } //
}