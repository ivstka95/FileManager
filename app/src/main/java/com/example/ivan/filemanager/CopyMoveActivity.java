package com.example.ivan.filemanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.ivan.filemanager.Constants.DIRECTORY_COPY_TO;
import static com.example.ivan.filemanager.Constants.INTENT_MOVE;
import static com.example.ivan.filemanager.Constants.PATH;


public class CopyMoveActivity extends Activity {
    private static DirectoryItemAdapter directoryItemAdapter;
    private static List items = new ArrayList<DirectoryItem>();
    protected static String path = "/";                              //path to the current directory
    private ListView listView;
    private LinearLayout llCancel;
    private LinearLayout llPasteHere;
    private ImageView ivRootDirectory;


    private RecyclerView horizontal_recycler_view;
    private static List<String> horizontalList;               //a list of buttons of folders, leading
    //to the current directory
    private static HorizontalAdapter horizontalAdapter;

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        private List<String> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtView;
            public ImageView ivRootDirectory;

            public MyViewHolder(View view) {
                super(view);
                txtView = (TextView) view.findViewById(R.id.txtView);
            }
        }

        public HorizontalAdapter(List<String> horizontalList) {
            this.horizontalList = horizontalList;
        }

        private String getQiuckPath(int index) {
            String quickPath = "/";
            for (int i = 0; i <= index; i++) {
                quickPath += horizontalList.get(i) + "/";
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
                        path = getQiuckPath(position);
                        refreshList();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_move);
        setViews();
        horizontalAdapter = new HorizontalAdapter(getCurrentPathButtonsList());
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(CopyMoveActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
        if (getIntent().hasExtra(PATH)) {
            this.path = getIntent().getStringExtra(PATH);
        }
        Toast.makeText(CopyMoveActivity.this, path, Toast.LENGTH_LONG).show();

        refreshList();
    }

    @Override
    protected void onResume() {
        refreshList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        path = cutPath(path);
        refreshList();

    }

    private String cutPath(String path) {
        do {
            path = path.substring(0, path.length() - 1);
        } while (path.charAt(path.length() - 1) != '/');
        return path;
    }

    public static void refreshList() {
        items.clear();
        File dir = new File(path);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    items.add(new DirectoryItem(path, file, false));
                }
            }
        }
        // Put the data into the lists
        horizontalList = getCurrentPathButtonsList();
        if (horizontalList.size() > 1)
            horizontalList.remove(0);
        horizontalAdapter.updateHorizontalList(horizontalList);
        directoryItemAdapter.updateList(items);
    }

    private static List<String> getCurrentPathButtonsList() {
        List<String> buttons = new ArrayList<String>(Arrays.asList(path.split("/")));
        return buttons;
    }

    private void makeNewFolder(String folder) {
        File file = null;
        boolean bool = false;
        String filepath;
        if (path.endsWith(File.separator)) {
            filepath = path + folder;
        } else {
            filepath = path + File.separator + folder;
        }
        try {
            file = new File(filepath);
            bool = file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bool) {
            Toast.makeText(CopyMoveActivity.this, "Created " + folder, Toast.LENGTH_LONG).show();
            refreshList();
        }
    }

    //a method sets views
    private void setViews() {
        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view2);
        listView = (ListView) findViewById(R.id.listView2);
        ivRootDirectory = (ImageView) findViewById(R.id.ivRootDirectory);
        ivRootDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = "/";
                refreshList();
            }
        });

        llCancel = (LinearLayout) findViewById(R.id.llCancel);
        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        llPasteHere = (LinearLayout) findViewById(R.id.llPasteHere);
        llPasteHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(DIRECTORY_COPY_TO, path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                DirectoryItem file = (DirectoryItem) items.get(position);
                                                String filename = file.getFilepath();
                                                File intentFile = new File(filename);
                                                if (intentFile.isDirectory()) {
                                                    path = filename;
                                                    refreshList();
                                                }
                                                if (intentFile.isFile()) {
                                                    Toast.makeText(CopyMoveActivity.this, "Choose a directory, not a file", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
        );
    }
}