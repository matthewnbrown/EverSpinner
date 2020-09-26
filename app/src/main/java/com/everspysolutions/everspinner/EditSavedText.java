package com.everspysolutions.everspinner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.everspysolutions.everspinner.savedTextFile.SavedTextFile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditSavedText#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditSavedText extends Fragment implements OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SavedTextFile savedTextFile;
    private TextView labelTextBox, inputTextBox;

    public EditSavedText() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment editSavedText.
     */
    // TODO: Rename and change types and number of parameters
    public static EditSavedText newInstance(String param1, String param2) {
        EditSavedText fragment = new EditSavedText();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);

            savedTextFile =
                    (SavedTextFile) EditSavedTextArgs.fromBundle(getArguments()).getSavedFile();
        }
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

        inputTextBox = (TextView) view.findViewById(R.id.edit_saved_label);
        labelTextBox = view.findViewById(R.id.edit_saved_input_text);
        TextView date = view.findViewById(R.id.edit_saved_date_created);
        inputTextBox.setText(savedTextFile.getText());
        labelTextBox.setText(savedTextFile.getLabel());
        date.setText(savedTextFile.getTimeCreated().toString());

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
        savedTextFile.saveToDisk();

        Navigation.findNavController(v).navigateUp();
    }
}