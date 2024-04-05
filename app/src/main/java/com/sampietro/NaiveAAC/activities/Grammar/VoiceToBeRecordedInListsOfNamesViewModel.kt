package com.sampietro.NaiveAAC.activities.Grammar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>VoiceToBeRecordedInListsOfNamesViewModel</h1>
 *
 *
 * **VoiceToBeRecordedInListsOfNamesViewModel** utility class for registration in ListsOfNames class.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @see ListsOfNames
 *
 * @version     5.0, 01/04/2024
 */
class VoiceToBeRecordedInListsOfNamesViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<VoiceToBeRecordedInListsOfNames>()
    fun setItem(item: VoiceToBeRecordedInListsOfNames) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<VoiceToBeRecordedInListsOfNames> {
        return selectedItem
    } //
}