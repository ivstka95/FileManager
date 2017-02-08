package com.example.ivan.filemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Иван on 12.01.2017.
 */

public class DirectoryItemAdapter extends ArrayAdapter<DirectoryItem> {
    private List<DirectoryItem> list;
    private Context context;
    private LayoutInflater inflater;
    private boolean[] selectedItems; //    List selectedItems;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_item, parent, false);
        }

        ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        ImageView ivImageType = (ImageView) view.findViewById(R.id.ivIconType);

        boolean imageSet = false;
        final DirectoryItem file = (DirectoryItem) list.get(position);
        if (new File(file.getFilepath()).isDirectory()) {
            ivIcon.setImageResource(R.drawable.folder);
            ivImageType.setVisibility(View.GONE);
            imageSet = true;
        } else {
            if (file.getType().equalsIgnoreCase(".htm") || file.getType().equalsIgnoreCase(".html") ||
                    file.getType().equalsIgnoreCase(".htmls") || file.getType().equalsIgnoreCase(".htt") ||
                    file.getType().equalsIgnoreCase(".htx") || file.getType().equalsIgnoreCase(".java") ||
                    file.getType().equalsIgnoreCase(".js") || file.getType().equalsIgnoreCase(".pl") ||
                    file.getType().equalsIgnoreCase(".txt") || file.getType().equalsIgnoreCase(".xml")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.txt);
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".doc") || file.getType().equalsIgnoreCase(".docx") ||
                    file.getType().equalsIgnoreCase(".word")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.msword);
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".pdf")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.pdf);
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".mid") || file.getType().equalsIgnoreCase(".mp2") ||
                    file.getType().equalsIgnoreCase(".mp3") || file.getType().equalsIgnoreCase(".wav")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.audio);
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".bmp")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.bmp);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".gif")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.gif);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".jpe")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.jpe);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".jpeg")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.jpeg);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".jpg")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.jpg);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".pic")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.pic);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".png")) {
                ivImageType.setVisibility(View.VISIBLE);
                ivImageType.setImageResource(R.drawable.png);
                ivIcon.setImageURI(Uri.parse(file.getFilepath()));
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".avi") || file.getType().equalsIgnoreCase(".mjpg") ||
                    file.getType().equalsIgnoreCase(".mpeg") || file.getType().equalsIgnoreCase(".mpg") ||
                    file.getType().equalsIgnoreCase(".mp4")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.video);
                imageSet = true;
            }
            if (file.getType().equalsIgnoreCase(".zip")) {
                ivImageType.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.zip);
                imageSet = true;
            }
        }
        if (!imageSet) {
            ivImageType.setVisibility(View.GONE);
            ivIcon.setImageResource(R.drawable.file);
        }

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(list.get(position).getName());
        TextView tvLastModified = (TextView) view.findViewById(R.id.tvLastModified);
        tvLastModified.setText(list.get(position).getLastModified()
        );
        TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
        tvSize.setText("");
        if (new File(file.getFilepath()).isFile())
            tvSize.setText(list.get(position).getSize());
        final CheckBox cbSelected = (CheckBox) view.findViewById(R.id.cbSelected);
        cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

                                              {
                                                  @Override
                                                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                      if (cbSelected.isChecked()) {
                                                          list.get(position).setSelected(true);
                                                      } else list.get(position).setSelected(false);
                                                  }
                                              }

        );
        if (MainActivity.isCheckBoxVisibility())
            cbSelected.setVisibility(View.VISIBLE);
        else cbSelected.setVisibility(View.GONE);
        cbSelected.setChecked(list.get(position).

                getSelected()

        );
        return view;
    }
}
