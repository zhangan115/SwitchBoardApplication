package com.board.applicion.view.examination;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.board.applicion.R;
import com.board.applicion.mode.databases.MainControlRoom;
import com.board.applicion.mode.databases.Substation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SubListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int groupLayout, childLayout;
    private List<Substation> data = new ArrayList<>();

    private ItemClickListener listener;

    public interface ItemClickListener {

        void onItemClick(MainControlRoom room);

    }

    public void setItemListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public SubListAdapter(Context context, int groupLayout, int childLayout) {
        this.context = context;
        this.groupLayout = groupLayout;
        this.childLayout = childLayout;
    }

    public void setData(List<Substation> data) {
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).mainControlRoomToMany.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).mainControlRoomToMany.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(groupLayout, null);
            holder.mGroup = convertView.findViewById(R.id.layout);
            holder.mLine = convertView.findViewById(R.id.line);
            holder.division = convertView.findViewById(R.id.division);
            holder.mName = convertView.findViewById(R.id.name);
            holder.stateIv = convertView.findViewById(R.id.state);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            holder.mLine.setVisibility(View.GONE);
        } else {
            holder.mLine.setVisibility(View.VISIBLE);
        }
        holder.mName.setText(data.get(groupPosition).name);
        if (isExpanded) {
            holder.stateIv.setImageDrawable(context.getResources().getDrawable(R.drawable.list_narrow_top_normal));
            if (getChildrenCount(groupPosition)==0){
                holder.division.setVisibility(View.GONE);
            }else {
                holder.division.setVisibility(View.VISIBLE);
            }
        } else {
            holder.stateIv.setImageDrawable(context.getResources().getDrawable(R.drawable.list_narrow_under_normal));
            holder.division.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(childLayout, null);
            holder.mName = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        MainControlRoom room = data.get(groupPosition).mainControlRoomToMany.get(childPosition);
        holder.mName.setText(room.name);
        convertView.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            listener.onItemClick(data.get(groupPosition).mainControlRoomToMany.get(childPosition));
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 外部显示ViewHolder
     */
    private class GroupViewHolder {
        LinearLayout mGroup;
        TextView mName;
        View mLine;
        View division;
        ImageView stateIv;
    }

    /**
     * 内部显示ViewHolder
     */
    private class ChildViewHolder {
        TextView mName;
        View mLine;
        LinearLayout mChild;
    }
}
