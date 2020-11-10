package com.everspysolutions.everspinner.SynonymList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.everspysolutions.everspinner.R;
import com.everspysolutions.everspinner.SavedFileRecyclerViewAdapter;
import com.everspysolutions.everspinner.SynonymFinder.Synonym;
import com.everspysolutions.everspinner.SynonymFinder.SynonymCacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SynonymExpandableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SynonymExpandableListFragment extends Fragment {

    SynonymExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SYNONYM_CACHER_KEY = "synonym_cacher_key";


    private SynonymCacher synonymCacher;

    public SynonymExpandableListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param synonymCacher Parameter 1.
     * @return A new instance of fragment SynonymListFragment.
     */
    public static SynonymExpandableListFragment newInstance(SynonymCacher synonymCacher) {
        SynonymExpandableListFragment fragment = new SynonymExpandableListFragment();
        Bundle args = new Bundle();
        //args.putSerializable(SYNONYM_CACHER_KEY, synonymCacher);

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

        View rootView = inflater.inflate(R.layout.fragment_synonym_item_list, container, false);

        synonymCacher = SynonymExpandableListFragmentArgs
                .fromBundle(getArguments()).getSynonymCacher();

        expListView = rootView.findViewById(R.id.synonym_expandable_list);

        listAdapter = new SynonymExpandableListAdapter(synonymCacher);
        expListView.setAdapter(listAdapter);
        expListView.setGroupIndicator(null);

        ImageButton newBaseWordBtn = rootView.findViewById(R.id.btn_add_new_baseword);
        newBaseWordBtn.setOnClickListener(this::onNewBaseWordClick);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onNewBaseWordClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add New Synonym");

        LayoutInflater inflater = (LayoutInflater) view.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialoglayout = inflater.inflate(R.layout.synonym_new_baseword_alert, null);

        builder.setView(dialoglayout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText wordTxt = dialoglayout.findViewById(R.id.new_word_word_input);

                String word = wordTxt.getText().toString().toLowerCase().trim();
                listAdapter.addBaseWord(word);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show();
    }


}