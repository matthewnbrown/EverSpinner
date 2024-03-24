package com.everspysolutions.everspinner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditSavedText#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditSavedText extends Fragment implements OnClickListener {

    private SavedTextMangerVM model;
    private MaterialCheckBox setDefaultCB;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private SavedTextFile savedTextFile;
    private TextView labelTextBox, inputTextBox;

    public EditSavedText() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_saved_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_edit_saved_save).setOnClickListener(this);

        setDefaultCB = view.findViewById(R.id.edit_saved_set_default_cb);
        inputTextBox = view.findViewById(R.id.edit_saved_input_text);
        labelTextBox = view.findViewById(R.id.edit_saved_label);
        TextView date = view.findViewById(R.id.edit_saved_date_created);

        // Detect changing of actively selected text
        model = new ViewModelProvider(requireActivity()).get(SavedTextMangerVM.class);
        model.getActiveText().observe(getViewLifecycleOwner(), activeText -> {
            savedTextFile = activeText;
            inputTextBox.setText(activeText.getText());
            labelTextBox.setText(activeText.getLabel());
            date.setText(activeText.getTimeCreated().toString());
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_edit_saved_save) {
            onSaveClick(v);
        }
    }

    private void onSaveClick(View v) {
        savedTextFile.setLabel(labelTextBox.getText().toString());
        savedTextFile.setText(inputTextBox.getText().toString());
        savedTextFile.saveToDisk(getContext());

        model.setActiveText(savedTextFile);

        List<SavedTextFile> m = model.getTextList().getValue();

        if (m != null && !m.contains(savedTextFile)) {
            m.add(savedTextFile);
        }

        if(setDefaultCB.isChecked()){
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(v.getContext());
            sharedPref.edit().putString("default_text_id", savedTextFile.getID()).apply();

        }
        Navigation.findNavController(v).navigateUp();
    }
}