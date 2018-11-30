package com.board.applicion.view.cable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.board.applicion.R;
import com.board.applicion.mode.cable.CableBean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private int groupLayout, childLayout;
    private List<CableBean> data = new ArrayList<>();

    public CableAdapter(Context context, int groupLayout, int childLayout) {
        this.context = context;
        this.groupLayout = groupLayout;
        this.childLayout = childLayout;
    }

    public void setData(List<CableBean> data) {
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).toString();
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
            holder.cableName = convertView.findViewById(R.id.cableName);
            holder.idName = convertView.findViewById(R.id.idName);
            holder.stateIv = convertView.findViewById(R.id.state);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            holder.mLine.setVisibility(View.VISIBLE);
        } else {
            holder.mLine.setVisibility(View.VISIBLE);
        }
        if (data.get(groupPosition).getId() == 0) {
            holder.idName.setText("");
            holder.cableName.setText(data.get(groupPosition).getCableNum());
        } else {
            holder.cableName.setText(data.get(groupPosition).getCableNum() + "~");
            holder.idName.setText(String.valueOf(data.get(groupPosition).getId()));
        }
        if (isExpanded) {
            holder.stateIv.setImageDrawable(context.getResources().getDrawable(R.drawable.list_narrow_top_normal));
            if (getChildrenCount(groupPosition) == 0) {
                holder.division.setVisibility(View.GONE);
            } else {
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
            holder.text1 = convertView.findViewById(R.id.text1);
            holder.text2 = convertView.findViewById(R.id.text2);
            holder.text3 = convertView.findViewById(R.id.text3);
            holder.text4 = convertView.findViewById(R.id.text4);
            holder.text5 = convertView.findViewById(R.id.text5);
            holder.text6 = convertView.findViewById(R.id.text6);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.text1.setText(data.get(groupPosition).getSubstationName());
        holder.text2.setText(data.get(groupPosition).getMcrName());
        holder.text3.setText(data.get(groupPosition).getCableNum());
        holder.text4.setText(data.get(groupPosition).getStarting());
        holder.text5.setText(data.get(groupPosition).getEnding());
        holder.text6.setText(data.get(groupPosition).getCableSpec());
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
        TextView cableName;
        TextView idName;
        View mLine;
        View division;
        ImageView stateIv;
    }

    /**
     * 内部显示ViewHolder
     */
    private class ChildViewHolder {
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
        TextView text5;
        TextView text6;
    }
}
