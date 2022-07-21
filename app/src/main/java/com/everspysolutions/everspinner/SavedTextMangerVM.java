package com.everspysolutions.everspinner;

import android.content.Context;

import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SavedTextMangerVM extends ViewModel {
    private MutableLiveData<List<SavedTextFile>> savedTexts;
    private MutableLiveData<SavedTextFile> activeText;

    public LiveData<List<SavedTextFile>> getTextList() {
        return savedTexts;
    }

    public LiveData<SavedTextFile> getActiveText() {
        if (activeText == null) {
            activeText = new MutableLiveData<>();
        }
        return activeText;
    }

    public void setActiveText (SavedTextFile savedTextFile) {
        if (activeText == null) {
            activeText = new MutableLiveData<>();
        }
        activeText.setValue(savedTextFile);
    }

    public void setTextList(List<SavedTextFile> savedTextFileList){
        if (savedTexts == null) {
            savedTexts = new MutableLiveData<>();
        }
        savedTexts.setValue(savedTextFileList);
    }

    private void loadSavedData(){
    }
}
