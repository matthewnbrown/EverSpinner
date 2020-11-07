package com.everspysolutions.everspinner.SynonymList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

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

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SYNONYM_CACHER_KEY = "synonym_cacher_key";

    // TODO: Rename and change types of parameters
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
    // TODO: Rename and change types and number of parameters
    public static SynonymExpandableListFragment newInstance(SynonymCacher synonymCacher) {
        SynonymExpandableListFragment fragment = new SynonymExpandableListFragment();
        Bundle args = new Bundle();
        //args.put(SYNONYM_CACHER_KEY, synonymCacher);
        //args.putString(ARG_PARAM1, param1);
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

        expListView = rootView.findViewById(R.id.synonym_expandable_list);

        SynonymCacher synonymCacher = new SynonymCacher();
        Random random = new Random();

        // Generating Random Data for testing.
        for(int i = 0; i < 6; i++) {
            int childCount = random.nextInt(6);
            ArrayList<Synonym> synonyms = new ArrayList<>();
            for (int j = 0; j < childCount; j++) {
                synonyms.add(new Synonym(Integer.toString(j), random.nextInt(10000)));
            }
            synonymCacher.addItemToCache(Integer.toString(i), synonyms);
        }
        listAdapter = new SynonymExpandableListAdapter(synonymCacher);
        expListView.setAdapter(listAdapter);
        expListView.setGroupIndicator(null);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}