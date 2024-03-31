package com.everspysolutions.everspinner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;
import com.everspysolutions.everspinner.SynonymFinder.SynonymCacheLoaderSaver;
import com.everspysolutions.everspinner.SynonymFinder.SynonymCacher;
import com.everspysolutions.everspinner.SynonymFinder.SynonymFinder;
import com.everspysolutions.everspinner.TextSpinner.TextSpinner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Spinner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Spinner extends Fragment implements OnClickListener {

    private SavedTextMangerVM model;

    private TextView inputTextBox, outputTextBox;
    private SavedTextFile activeTextFile;
    private SynonymFinder synonymFinder;

    public Spinner() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param inputText initial text to fill spinner input
     * @return A new instance of fragment spinner.
     */
    public static Spinner newInstance(String inputText) {
        Spinner fragment = new Spinner();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(SavedTextMangerVM.class);
        if(model.getTextList() == null) {
            model.setTextList(SavedTextFile.loadAllSavedTextFiles(getContext()));
        }
        SynonymCacher cache = SynonymCacheLoaderSaver.loadLocalSynonymCache(getContext());
        if(cache == null) {
            synonymFinder = new SynonymFinder();
        } else {
            synonymFinder = new SynonymFinder(cache);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spinner, container, false);
    }

    /**
     * Handles onclick events. Handled in this format because this class is a fragment
     * @param v View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_spinner_copy:
                onCopyClick(v);
                break;
            case R.id.btn_spinner_paste:
                onPasteClick(v);
                break;
            case R.id.btn_spinner_spin:
                onSpinClick(v);
                break;
            case R.id.btn_spinner_save:
                onSaveClick(v);
                break;
            case R.id.btn_spinner_cache:
                onCacheClick(v);
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Button copyBtn = view.findViewById(R.id.btn_spinner_copy);
        Button pasteBtn = view.findViewById(R.id.btn_spinner_paste);
        Button spinBtn = view.findViewById(R.id.btn_spinner_spin);
        Button saveBtn = view.findViewById(R.id.btn_spinner_save);
        Button cacheBtn = view.findViewById(R.id.btn_spinner_cache);

        copyBtn.setOnClickListener(this);
        pasteBtn.setOnClickListener(this);
        spinBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        cacheBtn.setOnClickListener(this);

        inputTextBox = view.findViewById(R.id.txt_spinner_input);
        outputTextBox = view.findViewById(R.id.txt_spinner_output);

        // Detect changing of actively selected text

        this.activeTextFile = model.getActiveText().getValue();

        model.getActiveText().observe(getViewLifecycleOwner(), activeText -> {
            activeTextFile = activeText;
            inputTextBox.setText(activeText.getText());
        });

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        String defaultTextID = sharedPref.getString("default_text_id", null);

        List<SavedTextFile> stfList = model.getTextList().getValue();

        if(defaultTextID != null && stfList != null && activeTextFile == null){
            for(SavedTextFile stf : stfList){
                if(stf.getID().equals(defaultTextID)){
                    activeTextFile=stf;
                    model.setActiveText(stf);
                    break;
                }
            }
        }

        if(activeTextFile == null) {
            model.setActiveText(new SavedTextFile());
        }

    }

    @Override
    public void onDestroyView() {
        updateActiveText(inputTextBox.getText().toString());
        super.onDestroyView();
    }

    public void onPasteClick(View view) {
        ClipboardManager cb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cbContents = cb.getPrimaryClip();

        String newInput = "";
        if(cbContents != null) {
            newInput = cbContents.getItemAt(0).getText().toString();
        }
        inputTextBox.setText(newInput);
    }
    public void onCopyClick(View view) {
        ClipboardManager cb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cb.setPrimaryClip(ClipData.newPlainText("Spun Text", this.outputTextBox.getText()));
    }
    public void onSpinClick(View view) {
        //outputTextBox.setText(spinText(inputTextBox.getText().toString()));
        new AsyncSpinText(this).execute(inputTextBox.getText().toString());
    }

    private void newEditSaveFile(View view) {
        String text = inputTextBox.getText().toString();
        activeTextFile = new SavedTextFile();
        updateActiveText(text);
        Navigation.findNavController(view).navigate(
                SpinnerDirections.actionSpinnerToEditSavedText());
    }
    public void onSaveClick(View view) {

        if(activeTextFile.getLastSaveTime() == null) {
            newEditSaveFile(view);
        } else {
            Context context = getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Overwrite Existing File");
            //builder.setIcon(R.drawable.icon);
            builder.setMessage("Would you like to overwrite existing file?");
            builder.setPositiveButton("Overwrite",
                    (dialog, id) -> {
                        Navigation.findNavController(view).navigate(
                                SpinnerDirections.actionSpinnerToEditSavedText());
                        dialog.cancel();
                    });

            builder.setNeutralButton("Cancel",
                    (dialog, id) -> dialog.cancel());

            builder.setNegativeButton("New File",
                    (dialog, id) -> {
                        newEditSaveFile(view);
                        dialog.cancel();
                    });
            builder.create().show();
        }
    }
    public void onCacheClick(View view) {

        Navigation.findNavController(view).navigate(SpinnerDirections
                .actionSpinnerToSynonymExpandableListFragment(synonymFinder.getSynonymCacher())
        );
    }


    /**
     * Error checks and spins text
     * @param text Text to be spun
     * @return Error message or spun text.
     */
    public String spinText(String text) {
        int selectionCount = TextSpinner.errorCheckText(text);
        if (selectionCount == -1) {
            return getString(R.string.spinner_error_parse_1);
        }
        if (selectionCount == -2) {
            return getString(R.string.spinner_error_parse_2);
        }
        text = fulfillSynonymRequests(text);
        TextSpinner textSpinner = new TextSpinner();
        return textSpinner.solveSelections(text);
    }

    private String fulfillSynonymRequests(String text) {
        Pattern p = Pattern.compile("[Ss][Yy][Nn]\\([^\\(\\)]*\\)");
        Matcher m = p.matcher(text);
        List<String> allMatches = new ArrayList<String>();
        List<int[]> matchIndex = new ArrayList<int[]>();

        while(m.find()) {
            String match = m.group();
            allMatches.add(match.substring(4, match.length()-1));
            matchIndex.add(new int[] {m.start(), m.end()});
        }

        if(allMatches.isEmpty()) {
            return text;
        }

        loadCacheIfOld();

        StringBuilder sb = new StringBuilder();
        int lastPos = 0;
        for(int i = 0; i < allMatches.size(); i++) {

            sb.append(text.substring(lastPos, matchIndex.get(i)[0]));
            lastPos = matchIndex.get(i)[1];
            String syn = synonymFinder.findRandomWeightedSynonym(requireContext(), allMatches.get(i));

            if(syn == null){
                sb.append(allMatches.get(i));
            } else {
                sb.append(syn);
            }
        }

        sb.append(text.substring(lastPos));

        saveCacheIfNew();

        return sb.toString();
    }


    private void loadCacheIfOld() {
        if(this.getContext() != null
                && SynonymCacheLoaderSaver.getLastSaveTime(this.getContext()).getTime()
                > synonymFinder.getSynonymCacher().getLastUpdateTime().getTime()) {

            synonymFinder.setSynonymCacher(
                    SynonymCacheLoaderSaver.loadLocalSynonymCache(this.getContext()));
        }
    }

    private void saveCacheIfNew() {
        if(this.getContext() != null &&
                SynonymCacheLoaderSaver.getLastSaveTime(this.getContext()).getTime()
                < synonymFinder.getSynonymCacher().getLastUpdateTime().getTime()) {
            SynonymCacheLoaderSaver.saveLocalSynonymCache
                    (this.getContext(), synonymFinder.getSynonymCacher());
        }
    }

    private void updateActiveText(String text){
        activeTextFile.setText(text);
        model.setActiveText(activeTextFile);
    }

    private static class AsyncSpinText extends AsyncTask<String, Void, String> {

        private WeakReference<Spinner> activityReference;

        AsyncSpinText(Spinner ctx) {
            activityReference = new WeakReference<>(ctx);
        }
        ProgressDialog pdLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading = new ProgressDialog(activityReference.get().getContext());
            pdLoading.setMessage("\tSpinning...");
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String text = strings[0];
            Spinner spinner = activityReference.get();
            text = spinner.spinText(text);
            return text;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pdLoading.dismiss();
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            Spinner spinner = activityReference.get();
            if(spinner == null || spinner.isRemoving()) return;
            pdLoading.dismiss();
            try {

                TextView outputBox = spinner.getActivity().findViewById(R.id.txt_spinner_output);
                outputBox.setText(text);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


    }

}