package com.everspysolutions.everspinner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Spinner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Spinner extends Fragment implements OnClickListener {

    private static final String INITTEXT = "This {is|is not} good UI design";

    private SavedTextMangerVM model;

    private TextView inputTextBox, outputTextBox;
    private Button copyBtn, pasteBtn, spinBtn, saveBtn;
    private SavedTextFile activeTextFile;

    // TODO: Rename and change types of parameters
    private String mParam1;

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

        if (getArguments() != null) {
            mParam1 = getArguments().getString(INITTEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spinner, container, false);
    }


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

        copyBtn = view.findViewById(R.id.btn_spinner_copy);
        pasteBtn = view.findViewById(R.id.btn_spinner_paste);
        spinBtn = view.findViewById(R.id.btn_spinner_spin);
        saveBtn = view.findViewById(R.id.btn_spinner_save);

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

        activeTextFile.setText(inputTextBox.getText().toString());
        Navigation.findNavController(view).navigate(
                SpinnerDirections.actionSpinnerToEditSavedText());
    }

    /**
     * Error checks and spins text
     * @param text Text to be spun
     * @return Error message or spun text.
     */
    public String spinText(String text) {
        int selectionCount = errorCheckText(text);
        if (selectionCount == -1) {
            return getString(R.string.spinner_error_parse_1);
        }
        if (selectionCount == -2) {
            return getString(R.string.spinner_error_parse_2);
        }
        return solveSelections(text);
    }

    /**
     * Spins a properly formatted string of text
     * @param text A string of text which is to be spun. Selections formatted similar to
     *            {A|B|C|{D|E}}
     * @return Text with randomly chosen selections
     */
    private String solveSelections(String text) {
        StringBuilder sb = new StringBuilder();
        int cutStartPoint = 0;
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '{') {
                sb.append(text.substring(cutStartPoint, i));
                int cutStartPoint2 = findClosingBracket(text, i);
                sb.append(solveSelection(text.substring(i, cutStartPoint2 + 1)));
                cutStartPoint = cutStartPoint2 + 1;
                i = cutStartPoint;
            }
            i++;
        }

        sb.append(text.substring(cutStartPoint));
        return sb.toString();
    }

    /**
     * Solves a single selection
     * @param selection A selection formatted as {options seperated by '|'}
     * @return The chosen option
     */
    private String solveSelection(String selection) {
        String selection2 = selection.substring(1, selection.length() - 1);
        List<String> options = new ArrayList<>();
        int lastPos = 0;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < selection2.length()) {
            char c = selection2.charAt(i);
            if (c == '|') {
                sb.append(selection2.substring(lastPos, i));
                options.add(sb.toString());
                sb = new StringBuilder();
                lastPos = i + 1;
            } else if (c == '{') {
                sb.append(selection2.substring(lastPos, i));
                int lastPos2 = findClosingBracket(selection2, i);
                sb.append(solveSelection(selection2.substring(i, lastPos2 + 1)));
                i = lastPos2;
                lastPos = lastPos2 + 1;
            }
            i++;
        }
        sb.append(selection2.substring(lastPos));
        options.add(sb.toString());
        return chooseOption((String[]) options.toArray(new String[0]));
    }

    /**
     * Finds the closing curly bracket for a given selection.
     * @param text String of text that contains a selection
     * @param startPos Position of opening curly bracket in text
     * @return position of closing curly bracket in text
     */
    private int findClosingBracket(String text, int startPos) {
        int endPos = startPos;
        int counter = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                counter++;
            }
            if (c == '}') {
                counter--;
            }
            if (counter == 0) {
                endPos = i;
                break;
            }
        }
        return endPos;
    }

    /**
     * Randomly chooses an option in an array of strings
     * @param options Array of possible choices
     * @return Chosen option
     */
    private String chooseOption(String[] options) {
        Random rand = new Random();
        return options[rand.nextInt(options.length)];
    }

    /**
     * Check a string of text for properly formatted selections
     * @param text Arbitrary string of text
     * @return -1 if there is an closing bracket unmatched with an open. -2 if there exists an
     * unclosed selection. Otherwise number of selections is returned
     */
    public int errorCheckText(String text) {
        int openCount = 0;
        int closeCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                openCount++;
            } else if (c == '}') {
                closeCount++;
                openCount--;
            }
            // A closed bracket is unmatched with an open {}}
            if (openCount < 0) {
                closeCount = -1;
                break;
            }
        }
        // Unclosed open brackets exist {}{
        if (openCount > 0) {
            closeCount = -2;
        }

        return closeCount;
    }
}