package com.sampietro.NaiveAAC.activities.Stories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/*
the data being stored is wrapped in a MutableLiveData class.
LiveData is a lifecycle-aware observable data holder class. MutableLiveData allows its value to be changed.
 */
/**
 * <h1>VoiceToBeRecordedInStoriesViewModel</h1>
 *
 * <p><b>VoiceToBeRecordedInStoriesViewModel</b> utility class for registration in Stories class.</p>
 * Refer to <a href="https://developer.android.com/guide/fragments/communicate">developer.android.com</a>
 *
 * @see Stories
 * @version     3.0, 2023
 */
public class VoiceToBeRecordedInStoriesViewModel extends ViewModel {
    private final MutableLiveData<VoiceToBeRecordedInStories> selectedItem = new MutableLiveData<VoiceToBeRecordedInStories>();
    public void setItem(VoiceToBeRecordedInStories item) {
        selectedItem.setValue(item);
    }
    public LiveData<VoiceToBeRecordedInStories> getSelectedItem() {
        return selectedItem;
    }
    //
}
