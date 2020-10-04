package com.everspysolutions.everspinner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;
import com.everspysolutions.everspinner.TextSpinner.TextSpinner;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Spinner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Spinner extends Fragment implements OnClickListener {

    private static final String INITTEXT = "This {is|is not} good UI design";

    private SavedTextMangerVM model;

    private TextView inputTextBox, outputTextBox;
    private SavedTextFile activeTextFile;

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
        args.putString(INITTEXT, inputText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Button copyBtn = view.findViewById(R.id.btn_spinner_copy);
        Button pasteBtn = view.findViewById(R.id.btn_spinner_paste);
        Button spinBtn = view.findViewById(R.id.btn_spinner_spin);
        Button saveBtn = view.findViewById(R.id.btn_spinner_save);

        copyBtn.setOnClickListener(this);
        pasteBtn.setOnClickListener(this);
        spinBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        inputTextBox = view.findViewById(R.id.txt_spinner_input);
        outputTextBox = view.findViewById(R.id.txt_spinner_output);

        // Detect changing of actively selected text
        model = new ViewModelProvider(requireActivity()).get(SavedTextMangerVM.class);
        this.activeTextFile = model.getActiveText().getValue();
        model.getActiveText().observe(getViewLifecycleOwner(), activeText -> {
            activeTextFile = activeText;
            inputTextBox.setText(activeText.getText());
        });


        model.setTextList(SavedTextFile.loadAllSavedTextFiles(getContext()));
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
        this.outputTextBox.setText(spinText(this.inputTextBox.getText().toString()));
    }
    public void onSaveClick(View view) {
        String text = inputTextBox.getText().toString();
        activeTextFile = new SavedTextFile();
        activeTextFile.setText(text);
        model.setActiveText(activeTextFile);
        Navigation.findNavController(view).navigate(
                SpinnerDirections.actionSpinnerToEditSavedText());
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
        return TextSpinner.solveSelections(text);
    }

}