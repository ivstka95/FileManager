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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.ivan.filemanager.Constants.DIRECTORY_COPY_TO;
import static com.example.ivan.filemanager.Constants.INTENT_COPY;
import static com.example.ivan.filemanager.Constants.INTENT_MOVE;
import static com.example.ivan.filemanager.Constants.PATH;
import static java.security.AccessController.getContext;


public class MainActivity extends Activity {

    private static DirectoryItemAdapter directoryItemAdapter;
    private static List items = new ArrayList<DirectoryItem>();
    protected static String path = "/";                              //path to the current directory
    private ListView listView;
//    private LinearLayout llNewFolder;
    private LinearLayout llDelete;
    private LinearLayout llCopy;
    private LinearLayout bMove;
    private static boolean checkBoxVisibility = false;
    private LinearLayout llButtons;
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
        setContentView(R.layout.activity_main);
        setViews();
        horizontalAdapter = new HorizontalAdapter(getCurrentPathButtonsList());
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
        refreshList();
        Toast.makeText(MainActivity.this, "" + getCurrentPathButtonsList().size(), Toast.LENGTH_LONG).show();
    }

    public static boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    @Override
    protected void onResume() {
        refreshList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (checkBoxVisibility)
            checkBoxVisibility = false;
        else
            path = cutPath(path);
        llButtons.setVisibility(View.GONE);
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
            Toast.makeText(MainActivity.this, "Created " + folder, Toast.LENGTH_LONG).show();
            refreshList();
        }
    }


    private int getCountOfSelectedItems() {
        int count = 0;
        List<DirectoryItem> list = directoryItemAdapter.getList();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getSelected())
                count++;
        return count;
    }

    private void moveFile(File file, File dir) throws IOException {
        if (file.isDirectory()) {
            File outputFile = new File(dir, file.getName());
            outputFile.mkdir();
            for (File f : file.listFiles()) {
                moveFile(f, outputFile);
            }
            delete(file.getPath());
        } else {
            File newFile = new File(dir, file.getName());
            FileChannel outputChannel = null;
            FileChannel inputChannel = null;
            try {
                outputChannel = new FileOutputStream(newFile).getChannel();
                inputChannel = new FileInputStream(file).getChannel();
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                inputChannel.close();
                delete(file.getPath());
            } finally {
                if (inputChannel != null) inputChannel.close();
                if (outputChannel != null) outputChannel.close();
            }
        }
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

    private void copyFile(File file, File dir) throws IOException {
        if (file.isDirectory()) {
            File outputFile = new File(dir, file.getName());
            outputFile.mkdir();
            for (File f : file.listFiles()) {
                copyFile(f, outputFile);
            }
        } else {
            File newFile = new File(dir, file.getName());
            FileChannel outputChannel = null;
            FileChannel inputChannel = null;
            try {
                outputChannel = new FileOutputStream(newFile).getChannel();
                inputChannel = new FileInputStream(file).getChannel();
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                inputChannel.close();
            } finally {
                if (inputChannel != null) inputChannel.close();
                if (outputChannel != null) outputChannel.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        llButtons.setVisibility(View.GONE);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_COPY) {
                if (data.hasExtra(DIRECTORY_COPY_TO)) {
                    List<DirectoryItem> list = directoryItemAdapter.getList();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSelected()) {
                            DirectoryItem di = (DirectoryItem) list.get(i);
                            try {
                                copyFile(new File(list.get(i).getFilepath()), new File(data.getStringExtra(DIRECTORY_COPY_TO)));
//                                copyDirectoryOneLocationToAnotherLocation(new File(list.get(i).getFilepath()), new File(data.getStringExtra(DIRECTORY_COPY_TO)));
                            } catch (Throwable throwable) {

                            }
                        }
                    }
                }
            }
            if (requestCode == INTENT_MOVE) {
                if (data.hasExtra(DIRECTORY_COPY_TO)) {
                    List<DirectoryItem> list = directoryItemAdapter.getList();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSelected()) {
                            DirectoryItem di = (DirectoryItem) list.get(i);
                            try {
                                moveFile(new File(list.get(i).getFilepath()), new File(data.getStringExtra(DIRECTORY_COPY_TO)));
                            } catch (Throwable throwable) {

                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //a method sets views
    private void setViews() {
        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        listView = (ListView) findViewById(R.id.listView);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);
        ivRootDirectory = (ImageView) findViewById(R.id.ivRootDirectory);
        ivRootDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = "/";
                refreshList();
            }
        });
//        bNewFolder = (Button) findViewById(R.id.bNewFolder);
//        bNewFolder.setOnClickListener(new View.OnClickListener() {
//                                          @Override
//                                          public void onClick(View v) {
//                                              final EditText etFolderName = new EditText(MainActivity.this);
//                                              AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                                              builder.setTitle("Enter new folder name")
//                                                      .setView(etFolderName)
//                                                      .setCancelable(false)
//                                                      .setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                                                                  @Override
//                                                                  public void onClick(DialogInterface dialog, int which) {
//                                                                      makeNewFolder(etFolderName.getText().toString());
//                                                                  }
//                                                              }
//                                                      )
//                                                      .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                          public void onClick(DialogInterface dialog, int id) {
//                                                              dialog.cancel();
//                                                          }
//                                                      });
//                                              AlertDialog alert = builder.create();
//                                              alert.show();
//                                          }
//                                      }
//        );
        llDelete = (LinearLayout) findViewById(R.id.llDelete);
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete " + getCountOfSelectedItems() + " items?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        List<DirectoryItem> list = directoryItemAdapter.getList();
                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).getSelected()) {
                                                DirectoryItem di = (DirectoryItem) list.get(i);
                                                delete(list.get(i).getFilepath());
                                            }
                                        }
                                        checkBoxVisibility = false;
                                        refreshList();
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        llCopy = (LinearLayout) findViewById(R.id.llCopy);
        llCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCountOfSelectedItems() > 0) {
                    checkBoxVisibility = false;
                    Intent intent = new Intent(MainActivity.this, CopyMoveActivity.class);
                    intent.putExtra(PATH, path);
                    startActivityForResult(intent, INTENT_COPY);
                }
            }
        });
        bMove = (LinearLayout) findViewById(R.id.llMove);
        bMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCountOfSelectedItems() > 0) {
                    checkBoxVisibility = false;
                    Intent intent = new Intent(MainActivity.this, CopyMoveActivity.class);
                    intent.putExtra(PATH, path);
                    startActivityForResult(intent, INTENT_MOVE);
                }
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
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(intentFile), file.getIntentType());
                                                    startActivity(intent);
                                                }
                                            }
                                        }
        );

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                    llButtons.setVisibility(View.VISIBLE);
                                                    checkBoxVisibility = true;
                                                    DirectoryItem di = (DirectoryItem) items.get(position);
                                                    di.setSelected(true);
                                                    items.remove(position);
                                                    items.add(position, di);
                                                    directoryItemAdapter.updateList(items);
                                                    return false;
                                                }
                                            }
        );
    }
}