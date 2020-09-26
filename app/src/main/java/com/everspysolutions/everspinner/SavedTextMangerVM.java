package com.everspysolutions.everspinner;

import com.everspysolutions.everspinner.savedTextFile.SavedTextFile;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavedTextMangerVM extends ViewModel {
    private MutableLiveData<List<SavedTextFile>> savedTexts;
    private MutableLiveData<SavedTextFile> activeText;

    public LiveData<List<SavedTextFile>> getTextList() {
        if (savedTexts == null) {
            savedTexts = new MutableLiveData<List<SavedTextFile>>();
            loadSavedData();
        }
        return savedTexts;
    }

    public LiveData<SavedTextFile> getActiveText() {
        if (activeText == null) {
            activeText = new MutableLiveData<SavedTextFile>();
            // TODO: Try to get default saved text if it exists
        }
        return activeText;
    }

    private void loadSavedData(){
        // TODO: Implement loading saved data
    }
}
