package com.everspysolutions.everspinner.savedTextFile;

import java.io.Serializable;
import java.util.Date;

public class SavedTextFile implements Serializable {
    private String text;
    private String label;
    private final Date timeCreated;
    private Date lastEdit;

    public SavedTextFile(){
        this.text = "Text";
        this.label = "Label";
        this.timeCreated = new Date();
        this.lastEdit = new Date();
    }

    public SavedTextFile(String label, String text){
        this.text = text;
        this.label = label;
        this.timeCreated = new Date();
        this.lastEdit = new Date();
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public Date getLastEdit() {
        return lastEdit;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setLabel(String label) {
        this.label = label;
        this.lastEdit = new Date();
    }

    public void setText(String text) {
        this.lastEdit = new Date();
        this.text = text;
    }

    public void saveToDisk() {

    }
}
