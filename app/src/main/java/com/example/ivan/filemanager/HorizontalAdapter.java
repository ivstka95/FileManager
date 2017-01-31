package com.example.ivan.filemanager;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Иван on 29.01.2017.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
    private List<String> horizontalList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
        }
    }

    public HorizontalAdapter(List<String> horizontalList) {
        this.horizontalList = horizontalList;
    }

    private String getQiuckPath(int index) {
        String quickPath = "";
        for (int i = 0; i <= index; i++) {
            quickPath += horizontalList.get(i);
        }
        return quickPath;
    }

    public void updateHorizontalList(List<String> horizontalList) {
        this.horizontalList.clear();
        this.horizontalList.addAll(horizontalList);
        notifyDataSetChanged();
    }

    @Override
    public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);
        return new HorizontalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HorizontalAdapter.MyViewHolder holder, final int position) {
        holder.txtView.setText(horizontalList.get(position));
        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < horizontalList.size() - 1) {
                    String quickPath = "";
                    for (int i = 0; i <= position; i++) {
                        quickPath += horizontalList.get(i);
                    }
                    CopyMoveActivity.path = quickPath;
                    CopyMoveActivity.refreshList();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}