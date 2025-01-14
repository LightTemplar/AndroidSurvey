package org.adaptlab.chpir.android.survey.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.adaptlab.chpir.android.survey.R;

import java.util.List;
import java.util.Map;

public class NavigationDrawerAdapter extends BaseExpandableListAdapter {

    private List<String> mExpandableListTitle;
    private Map<String, List<String>> mExpandableListDetail;
    private final LayoutInflater mLayoutInflater;

    public NavigationDrawerAdapter(Context context, List<String> expandableListTitle,
                                   Map<String, List<String>> expandableListDetail) {
        mExpandableListTitle = expandableListTitle;
        mExpandableListDetail = expandableListDetail;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<String> expandableListTitle, Map<String, List<String>> expandableListDetail) {
        mExpandableListTitle = expandableListTitle;
        mExpandableListDetail = expandableListDetail;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mExpandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_text_view, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}