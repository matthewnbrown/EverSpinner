package com.everspysolutions.everspinner.SynonymList;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.everspysolutions.everspinner.R;
import com.everspysolutions.everspinner.SynonymFinder.Synonym;
import com.everspysolutions.everspinner.SynonymFinder.SynonymCacher;

import java.util.ArrayList;
import java.util.List;


public class SynonymExpandableListAdapter extends BaseExpandableListAdapter {

    private final SynonymCacher synonymCacher;
    private List<String> synonymBaseWords;


    public SynonymExpandableListAdapter(SynonymCacher synonymCacher) {
        this.synonymCacher = synonymCacher;
        this.synonymBaseWords = synonymCacher.getBaseWords();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return synonymCacher.fetchSynonymsFromCache(synonymBaseWords.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final View row;
        final Synonym synonym = (Synonym) getChild(groupPosition, childPosition);

        ChildViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.synonym_list_item,  parent, false);

            holder = new ChildViewHolder(row);
            row.setTag(holder);

        } else {
            row = convertView;
            holder = (ChildViewHolder) row.getTag();
        }


        holder.updateChildInfo(groupPosition, childPosition, synonym.getWord());

        holder.lblSynonym.setText(synonym.getWord());
        holder.mScoreText.setText(Integer.toString(synonym.getScore()));

        //holder.btnDeleteSyn.setOnClickListener(view -> );
        holder.btnEditSyn.setOnClickListener(view -> onEditSynonymClick(view, null, holder));

        return row;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return synonymCacher.fetchSynonymsFromCache(synonymBaseWords.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.synonymBaseWords.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.synonymBaseWords.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        GroupViewHolder holder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.synonym_item_list_group, parent, false);

            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.updateGroupInfo(groupPosition);

        // Prevent group from not being expandable due to focusable button
        holder.newSynBtn.setFocusable(false);

        holder.newSynBtn.setOnClickListener(
                view -> onNewSynonymClick(view, holder)
        );

        holder.delWordBtn.setFocusable(false);
        holder.delWordBtn.setOnClickListener(
                view -> onDeleteBaseWordClick(view, holder)
        );

        holder.lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void synonymEditAlert(View view, GroupViewHolder pHolder, ChildViewHolder cHolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        LayoutInflater inflater = (LayoutInflater) view.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialoglayout = inflater.inflate(R.layout.synonym_new_syn_alert, null);
        final EditText wordTxt = dialoglayout.findViewById(R.id.new_syn_word_input);
        final EditText scoreTxt = dialoglayout.findViewById(R.id.new_syn_score_input);

        final String title = cHolder == null ? "Edit Synonym" : "New Synonym";
        builder.setTitle(title);

        if(cHolder != null) {
            wordTxt.setFocusable(false);
            wordTxt.setEnabled(false);
            wordTxt.setCursorVisible(false);
            wordTxt.setKeyListener(null);
            wordTxt.setBackgroundColor(Color.TRANSPARENT);
            wordTxt.setTextColor(Color.BLACK);

            Synonym syn = (Synonym) getChild(cHolder.groupPos, cHolder.childPos);
            wordTxt.setText((syn.getWord()));
            scoreTxt.setText(Integer.toString(syn.getScore()));
        }

        builder.setView(dialoglayout);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            if(!TextUtils.isDigitsOnly(scoreTxt.getText())
                    || scoreTxt.getText().toString().length() == 0) {
                return;
            }

            String word = wordTxt.getText().toString().toLowerCase().trim();
            int score = Integer.parseInt(scoreTxt.getText().toString());

            if(cHolder != null){
                synonymCacher.updateSynonymScore((String) getGroup(cHolder.groupPos), word, score);
            } else {
                synonymCacher.addSynonymToBaseWord((String) getGroup(pHolder.groupPos), word, score);
            }
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


        builder.show();
    }

    public void onNewSynonymClick(View view, GroupViewHolder holder) {
        synonymEditAlert(view, holder, null);
    }

    public void onEditSynonymClick(View view, GroupViewHolder pHolder, ChildViewHolder cHolder) {
        synonymEditAlert(view, pHolder, cHolder);
    }

    public void onDeleteBaseWordClick(View view, GroupViewHolder holder) {
        Context ctx = view.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage
                ("Are you sure you want to delete \"" + getGroup(holder.groupPos) + "\"?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    String baseWord = (String) getGroup(holder.groupPos);
                    synonymCacher.removeBaseWord(baseWord);
                    this.synonymBaseWords = synonymCacher.getBaseWords();

                    CharSequence text = "Deleted base word.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(ctx, text, duration);
                    toast.show();

                    dialog.cancel();
                    notifyDataSetChanged();
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        builder.show();
    }

    // Unused/
    public void onDeleteSynonymClick(View view, ChildViewHolder holder) {
        Context ctx = view.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        final Synonym synonym = (Synonym) getChild(holder.groupPos, holder.childPos);
        builder.setMessage
                ("Are you sure you want to delete \""
                        + synonym.getWord() +"\"?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    String baseWord = (String) getGroup(holder.groupPos);
                    // Delete

                    CharSequence text = "Deleted synonym.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(ctx, text, duration);
                    toast.show();

                    dialog.cancel();
                    notifyDataSetChanged();
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        builder.show();
    }

    public static class GroupViewHolder
    {
        public final View mView;
        public final TextView lblListHeader;
        public final ImageButton newSynBtn;
        public final ImageButton delWordBtn;
        public int groupPos;

        public GroupViewHolder(View view)
        {
            this.mView = view;
            this.lblListHeader = view.findViewById(R.id.synonym_item_header);
            this.newSynBtn = view.findViewById(R.id.btn_add_new_synonym);
            this.delWordBtn = view.findViewById(R.id.btn_delete_baseword);
        }

        public void updateGroupInfo (int groupPos) {
            this.groupPos = groupPos;
        }

    }

    public static class ChildViewHolder
    {
        public final View mView;
        public final TextView mScoreText;
        public final TextView lblSynonym;
        public final ImageButton btnEditSyn;
        //public final ImageButton btnDeleteSyn;
        public int groupPos;
        public int childPos;
        public String synonym;

        public ChildViewHolder(View view)
        {
            this.mView = view;
            this.mScoreText = view.findViewById(R.id.synonym_item_score);
            this.lblSynonym = view.findViewById(R.id.synonym_item_label);
            this.btnEditSyn = view.findViewById(R.id.btn_edit_syn);
            //this.btnDeleteSyn = view.findViewById(R.id.btn_delete_syn);

        }

        public void updateChildInfo(int groupPos, int childPos, String synonym) {
            this.groupPos = groupPos;
            this.childPos = childPos;
            this.synonym = synonym;
        }

    }

    public void addBaseWord(String word) {
        synonymCacher.addItemToCache(word, new ArrayList<>());
        this.synonymBaseWords = synonymCacher.getBaseWords();
        notifyDataSetChanged();
    }


}
