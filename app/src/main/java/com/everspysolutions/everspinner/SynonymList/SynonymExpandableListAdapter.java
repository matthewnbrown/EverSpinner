package com.everspysolutions.everspinner.SynonymList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.everspysolutions.everspinner.R;
import com.everspysolutions.everspinner.SynonymFinder.Synonym;
import com.everspysolutions.everspinner.SynonymFinder.SynonymCacher;

import org.w3c.dom.Text;

import java.util.HashMap;
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

        final Synonym synonym = (Synonym) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.synonym_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.synonym_item_label);
        TextView txtListScore = (TextView) convertView
                .findViewById(R.id.synonym_item_num);

        txtListChild.setText(synonym.getWord());
        txtListScore.setText(Integer.toString(synonym.getScore()));
        return convertView;
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

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.synonym_item_list_group, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.synonym_item_header);

        lblListHeader.setText(headerTitle);

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

}
