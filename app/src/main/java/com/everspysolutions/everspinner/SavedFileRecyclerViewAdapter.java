package com.everspysolutions.everspinner;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.everspysolutions.everspinner.SavedTextFile.SavedTextFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SavedFileRecyclerViewAdapter extends RecyclerView.Adapter<SavedFileRecyclerViewAdapter.ViewHolder> {

    private final List<SavedTextFile> mValues;
    private final SavedTextFile activeItem;
    private SavedTextMangerVM model;

    public SavedFileRecyclerViewAdapter(SavedTextMangerVM model) {
        this.mValues = model.getTextList().getValue();
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
            holder.mContainer.setCardBackgroundColor(Color.parseColor("#aff2ae"));
        } else {
            holder.mContainer.setBackgroundColor(Color.TRANSPARENT);
        }

        // Edit button switches to EditSavedText fragment
        holder.mEditTextBtn.setOnClickListener(view -> {
            model.setActiveText(holder.mItem);
            Navigation.findNavController(view)
                    .navigate(SavedTextListViewerDirections.actionSavedTextToEditSavedText());
        });

        holder.mDelTextBtn.setOnClickListener
                (view -> onDeleteClick(view.getContext(), holder, position));

        holder.mContainer.setOnClickListener(view -> {
            model.setActiveText(holder.mItem);
            Navigation.findNavController(view)
                    .navigate(SavedTextListViewerDirections.actionSavedTextToSpinner());
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
        public final CardView mContainer;
        public final ImageButton mEditTextBtn;
        public final ImageButton mDelTextBtn;
        public SavedTextFile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabelView = view.findViewById(R.id.item_saved_label);
            mContentView = view.findViewById(R.id.item_saved_preview);
            mContainer = view.findViewById(R.id.savedText_container);
            mEditTextBtn = view.findViewById(R.id.item_saved_edit);
            mDelTextBtn = view.findViewById(R.id.item_saved_delete);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private void onCardClick(Context ctx) {

    }
    private void onDeleteClick(Context ctx, final ViewHolder holder, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage
                ("Are you sure you want to delete \"" + holder.mItem.getLabel() +"\"?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    holder.mItem.delete(ctx);
                    model.getTextList().getValue().remove(position);
                    notifyDataSetChanged();

                    CharSequence text = "Deleted saved text.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(ctx, text, duration);
                    toast.show();

                    dialog.cancel();
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}