package com.sampietro.NaiveAAC.activities.Bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>BluetoothStatusViewModel</h1>
 *
 *
 * **BluetoothStatusViewModel** utility class for Bluetooth communication.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @see Stories
 *
 * @version     5.0, 01/04/2024
 */
class BluetoothStatusViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<BluetoothStatus>()
    fun setItem(item: BluetoothStatus) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<BluetoothStatus> {
        return selectedItem
    } //
}