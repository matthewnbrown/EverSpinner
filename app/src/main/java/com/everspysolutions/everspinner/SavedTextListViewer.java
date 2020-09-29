package com.everspysolutions.everspinner;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SavedTextListViewer extends Fragment {

    private SavedTextMangerVM model;
    private List<SavedTextFile> savedList;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedTextListViewer() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SavedTextListViewer newInstance(int columnCount) {
        SavedTextListViewer fragment = new SavedTextListViewer();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_text_list, container, false);

        savedList = SavedTextFile.loadAllSavedTextFiles(getContext());

        model = new ViewModelProvider(requireActivity()).get(SavedTextMangerVM.class);

        model.getActiveText().observe(getViewLifecycleOwner(), activeText -> {
        });
        model.setTextList(savedList);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(new SavedFileRecyclerViewAdapter(model));
        }
        return view;
    }
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }
}