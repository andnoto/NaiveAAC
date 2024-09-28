package com.sampietro.NaiveAAC.activities.Game.Game2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>Game2ViewModel</h1>
 *
 *
 * **VoiceToBeRecordedInStoriesViewModel** utility class for registration in Stories class.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 */
class Game2ViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<Game2ViewModelItem>()
    fun setItem(item: Game2ViewModelItem) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<Game2ViewModelItem> {
        return selectedItem
    } //
}