package com.example.ivan.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public static DirectoryItemAdapter directoryItemAdapter;
    List items = new ArrayList<DirectoryItem>();
    private String path = "/";
    private ListView listView;
    private Button bNewFolder;

    private RecyclerView horizontal_recycler_view;
    private ArrayList<String> horizontalList;
    private HorizontalAdapter horizontalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontalList = new ArrayList<>();
        horizontalAdapter = new HorizontalAdapter(horizontalList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        listView = (ListView) findViewById(R.id.listView);
        bNewFolder = (Button) findViewById(R.id.bNewFolder);
        bNewFolder.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              File f = null;
                                              boolean bool = false;
                                              try {
                                                  // returns pathnames for files and directory
                                                  f = new File(path + "/NEW_FOLDER");
                                                  // create
                                                  bool = f.mkdir();
                                              } catch (Exception e) {
                                                  // if any error occurs
                                                  e.printStackTrace();
                                              }
                                              if (bool) {
                                                  refreshList(path);
                                              }
                                          }
                                      }
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                DirectoryItem file = (DirectoryItem) items.get(position);
                                                String filename = file.getFilepath();
                                                File intentFile = new File(filename);
                                                if (intentFile.isDirectory()) {
                                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                    intent.putExtra("path", filename);
                                                    startActivity(intent);
                                                }
                                                if (intentFile.isFile()) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(intentFile), file.getIntentType());
                                                    startActivity(intent);
                                                }
                                            }
                                        }
        );
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

                                            {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                                                               long id) {
                                                    DirectoryItem file = (DirectoryItem) items.get(position);
                                                    final String filename = file.getFilepath();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                    builder.setTitle("Delete " + file.getName() + "?")
                                                            .setCancelable(false)
                                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    delete(filename);
                                                                    if (!new File(filename).exists()) {
                                                                        Toast.makeText(MainActivity.this, "Deleted" + filename, Toast.LENGTH_SHORT).show();
                                                                        refreshList(cutPath(path));
                                                                    } else {
                                                                        Toast.makeText(MainActivity.this, "NOT deleted" + filename, Toast.LENGTH_SHORT).show();
                                                                        refreshList(cutPath(path));
                                                                    }
                                                                }
                                                            })
                                                            .setNegativeButton("NO",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            dialog.cancel();
                                                                        }
                                                                    });
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                    return false;
                                                }
                                            }
        );
    }

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

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.txtView.setText(horizontalList.get(position));
            holder.txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < horizontalList.size() - 1) {
                        String filename = getQiuckPath(position);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("path", filename);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

    private void refreshList(String path) {
        items.clear();

        // Use the current directory as title
        if (getIntent().hasExtra("path")) {
            this.path = getIntent().getStringExtra("path");
        }
//        Read all files sorted into the values-array
        File dir = new File(this.path);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    items.add(new DirectoryItem(this.path, file));
                }
            }
        }
        // Put the data into the list
        directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
        directoryItemAdapter.updateList(items);
        Toast.makeText(MainActivity.this, "INSIDErefresh  " + this.path, Toast.LENGTH_SHORT).show();
    }

    private void delete(String fileToDelete) {
        File F = new File(fileToDelete);
        if (!F.exists())
            return;
        if (F.isDirectory()) {
            for (File file : F.listFiles())
                delete(file.getPath());
            F.delete();
        } else {
            F.delete();
        }
    }

    @Override
    protected void onResume() {
        refreshList(path);
        horizontalList.clear();
        horizontalList.addAll(getCurrentPathButtonsList());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        path = cutPath(path);
        super.onBackPressed();
    }

    private String cutPath(String path) {
        do {
            path = path.substring(0, path.length() - 1);
        } while (path.charAt(path.length() - 1) != '/');
        return path;
    }

    private List<String> getCurrentPathButtonsList() {
        List buttons = new ArrayList<String>();
        buttons.add("/");
        if (path.length() > 1) {
            int i = 1;
            String button = "";
            while (i < path.length()) {
                button += path.charAt(i);
                if (path.charAt(i) == '/')
                    break;
                i++;
            }
            buttons.add(button);
            button = "";
            for (int j = i + 1; j < path.length(); j++) {
                button += path.charAt(j);
                if (path.charAt(j) == '/' || j == path.length() - 1) {
                    buttons.add(button);
                    button = "";
                }
            }
        }
        return buttons;
    }

    private String getQiuckPath(int index) {
        String quickPath = "";
        for (int i = 0; i <= index; i++) {
            quickPath += horizontalList.get(i);
        }
        return quickPath;
    }
}




