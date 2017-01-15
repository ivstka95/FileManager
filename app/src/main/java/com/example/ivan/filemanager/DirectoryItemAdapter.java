package com.example.ivan.filemanager;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Иван on 12.01.2017.
 */

public class DirectoryItemAdapter extends ArrayAdapter<DirectoryItem> {
    private List<DirectoryItem> list;
    private Context context;
    private LayoutInflater inflater;

    public DirectoryItemAdapter(Context context, int resource) {
        super(context, resource);
        list = new ArrayList<>();
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateList(List<DirectoryItem> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public List<DirectoryItem> getList() {
        return list;
    }

    @Nullable
    @Override
    public DirectoryItem getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_item, parent, false);
        }

        ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        int imageID;
        DirectoryItem file = (DirectoryItem) list.get(position);
        String filepath;
        if (file.getPath().endsWith(File.separator)) {
            filepath = file.getPath() + file.getName();
        } else {
            filepath = file.getPath() + File.separator + file.getName();
        }
        if (new File(filepath).isDirectory()) {
            imageID = R.drawable.folder;
        } else {
            imageID = R.drawable.file;
        }
        ivIcon.setBackgroundResource(imageID);

        TextView tvName = (TextView) view.findViewById(R.id.twName);
        tvName.setText(list.get(position).getName());

        return view;
    }
}
