package com.everspysolutions.everspinner.SavedTextFile;

import android.content.Context;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SavedTextFile implements Serializable {
    private static final String SAVE_FOLDER_PATH = "saved_text";
    private String filename;
    private String text;
    private String label;
    private Date timeCreated;
    private Date lastEdit;
    private Boolean unsavedChanges = false;

    public SavedTextFile(){
        this.text = "Text";
        this.label = "Label";
        this.timeCreated = new Date();
        this.lastEdit = new Date();
        this.filename = UUID.randomUUID().toString();
    }

    /**
     * Generate savedTextFile from a label/text. Should not be used for regenerating old saves
     * @param label
     * @param text
     */
    public SavedTextFile(String label, String text){
        this.text = text;
        this.label = label;
        this.timeCreated = new Date();
        this.lastEdit = new Date();
        this.filename = UUID.randomUUID().toString();
    }

    /**
     * Generate a savedTextFile from a file
     * @param file file to parse from
     */
    public SavedTextFile(File file, Context ctx) {
        loadFile(file, ctx);
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
            File outputDir = new File(ctx.getFilesDir(), SAVE_FOLDER_PATH);
            File outputFile = new File(outputDir, filename);

            if(!outputDir.exists()){
                outputDir.mkdir();
            }
            if(!outputFile.exists()){
                outputFile.createNewFile();
            }

           // FileOutputStream fos = ctx.openFileOutput(outputFile, Context.MODE_APPEND);
            FileOutputStream fos = new FileOutputStream(outputFile);

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

    /**
     * Reads data from passed file to fill in data for current savedfile
     * @param file File to read from
     * @param ctx App context
     */
    private void loadFile(File file, Context ctx){
        String inputData = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer = new char[fis.available()];
            int res = isr.read(inputBuffer);

            inputData = new String(inputBuffer);

            isr.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert to XML

        try {
            InputStream is = new ByteArrayInputStream(inputData.getBytes(StandardCharsets.UTF_8));
            //ArrayList<XmlData> xmlDataList = new ArrayList<XmlData>()
            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            NodeList items = null;
            Document dom;

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);

            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("SavedText");

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            for (int i = 0; i < items.getLength(); i++)
            {
                Node item = items.item(i);
                NodeList children = item.getChildNodes();

                for(int j = 0; j < children.getLength(); j++){
                    String nodeName = children.item(j).getNodeName();
                    String nodeVal = children.item(j).getTextContent();
                    switch (nodeName){
                        case "UUID":
                            this.filename = nodeVal;
                            break;
                        case "Label":
                            this.label = nodeVal;
                            break;
                        case "CreationTime":
                            this.timeCreated = dateFormat.parse(nodeVal);
                            break;
                        case "LastEditTime":
                            this.lastEdit = dateFormat.parse(nodeVal);
                            break;
                        case "Text":
                            this.text = nodeVal;
                            break;
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean delete(Context ctx) {
        File dir = new File(ctx.getFilesDir(), SAVE_FOLDER_PATH);
        return (new File(dir,this.filename)).delete();
    }
    public static List<SavedTextFile> loadAllSavedTextFiles(Context ctx){
        return loadAllSavedTextFiles(ctx, SAVE_FOLDER_PATH);
    }
    public static List<SavedTextFile> loadAllSavedTextFiles(Context ctx, String path){
        List<SavedTextFile> loadedFiles = new ArrayList<SavedTextFile>();

        File inputDir = new File(ctx.getFilesDir(), path);
        File lister = inputDir.getAbsoluteFile();
        for(File file : lister.listFiles())
        {
            loadedFiles.add(new SavedTextFile(file, ctx));
        }

        return loadedFiles;
    }

    public boolean equals(SavedTextFile y){
        return this.filename.equals(y.filename);
    }
}
