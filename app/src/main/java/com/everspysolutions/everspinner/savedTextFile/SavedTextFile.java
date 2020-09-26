package com.everspysolutions.everspinner.savedTextFile;

import android.content.Context;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class SavedTextFile implements Serializable {
    private static final String SAVE_FOLDER_PATH = "saved_text/";
    private String filename;
    private String text;
    private String label;
    private final Date timeCreated;
    private Date lastEdit;
    private Boolean unsavedChanges = false;

    public SavedTextFile(){
        this.text = "Text";
        this.label = "Label";
        this.timeCreated = new Date();
        this.lastEdit = new Date();
        this.filename = UUID.randomUUID().toString();
    }

    public SavedTextFile(String label, String text){
        this.text = text;
        this.label = label;
        this.timeCreated = new Date();
        this.lastEdit = new Date();
        this.filename = UUID.randomUUID().toString();
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
        this.unsavedChanges = true;
    }

    public void setText(String text) {
        this.lastEdit = new Date();
        this.text = text;
        this.unsavedChanges = true;
    }

    public void saveToDisk(Context ctx) {
        this.unsavedChanges = false;
        saveFile(ctx);
    }

    private void saveFile(Context ctx) {
        try {
            FileOutputStream fos;
            fos = ctx.openFileOutput(SAVE_FOLDER_PATH.concat(filename), Context.MODE_APPEND);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "SavedText");

            serializer.startTag(null, "UUID");
            serializer.text(this.filename);
            serializer.endTag(null, "UUID");

            serializer.startTag(null, "Label");
            serializer.text(this.label);
            serializer.endTag(null, "Label");

            serializer.startTag(null, "CreationTime");
            serializer.text(this.timeCreated.toString());
            serializer.endTag(null, "CreationTime");

            serializer.startTag(null, "LastEditTime");
            serializer.text(this.lastEdit.toString());
            serializer.endTag(null, "LastEditTime");

            serializer.startTag(null, "Text");
            serializer.text(this.text);
            serializer.endTag(null, "Text");


            serializer.endDocument();
            serializer.flush();

            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
