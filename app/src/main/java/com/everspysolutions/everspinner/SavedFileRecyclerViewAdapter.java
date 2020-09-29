package com.everspysolutions.everspinner;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SavedFileRecyclerViewAdapter extends RecyclerView.Adapter<SavedFileRecyclerViewAdapter.ViewHolder> {

    private final List<SavedTextFile> mValues;
    private final SavedTextFile activeItem;
    private SavedTextMangerVM model;

    public SavedFileRecyclerViewAdapter(SavedTextMangerVM model) {
        mValues = model.getTextList().getValue();
        this.activeItem = model.getActiveText().getValue();
        this.model = model;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_saved_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLabelView.setText(holder.mItem.getLabel());
        holder.mContentView.setText(holder.mItem.getText());

        // Highlight item if it is selected
        if(holder.mItem.equals(activeItem)){
            holder.mContainer.setBackgroundColor(Color.GREEN);
        } else {
            holder.mContainer.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.mEditTextBtn.setOnClickListener(view -> {
            model.setActiveText(holder.mItem);
            Navigation.findNavController(view)
                    .navigate(SavedTextListViewerDirections.actionSavedTextToEditSavedText());
        });

        holder.mDelTextBtn.setOnClickListener(view ->{
            holder.mItem.delete(view.getContext());
            model.getTextList().getValue().remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mLabelView;
        public final LinearLayout mContainer;
        public final ImageButton mEditTextBtn;
        public final ImageButton mDelTextBtn;
        public SavedTextFile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabelView = (TextView) view.findViewById(R.id.item_saved_label);
            mContentView = (TextView) view.findViewById(R.id.item_saved_preview);
            mContainer = (LinearLayout) view.findViewById(R.id.savedText_container);
            mEditTextBtn = (ImageButton) view.findViewById(R.id.item_saved_edit);
            mDelTextBtn = (ImageButton) view.findViewById(R.id.item_saved_delete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}