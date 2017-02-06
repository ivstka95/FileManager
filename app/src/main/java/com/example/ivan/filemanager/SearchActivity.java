package com.example.ivan.filemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ivan.filemanager.Constants.DIRECTORY_COPY_TO;
import static com.example.ivan.filemanager.Constants.INTENT_COPY;
import static com.example.ivan.filemanager.Constants.INTENT_MAIN;
import static com.example.ivan.filemanager.Constants.INTENT_MOVE;
import static com.example.ivan.filemanager.Constants.PATH;

public class SearchActivity extends AppCompatActivity {
    private static DirectoryItemAdapter directoryItemAdapter;
    private static List items = new ArrayList<DirectoryItem>();
    private ListView listView;
    private LinearLayout llDelete;
    private LinearLayout llCopy;
    private LinearLayout bMove;
    private static boolean checkBoxVisibility = false;
    private LinearLayout llButtons;
    private LinearLayout llShare;
    private EditText etSearch;
    private ImageButton ibHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setViews();
        directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
    }

    public static boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    @Override
    protected void onResume() {
//        refreshList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (checkBoxVisibility)
            checkBoxVisibility = false;
        if (etSearch.getText().toString().length() == 0)
            finish();
        llButtons.setVisibility(View.GONE);
    }

    private String cutPath(String path) {
        do {
            path = path.substring(0, path.length() - 1);
        } while (path.charAt(path.length() - 1) != '/');
        return path;
    }

    public static void refreshList(String s, String path) {
        File dir = new File(path);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                DirectoryItem di = new DirectoryItem(path, file, false);
                if (!file.startsWith(".") && file.contains(s)) {
                    items.add(di);
                    directoryItemAdapter.updateList(items);
                }
//                if (new File(di.getFilepath()).isDirectory())
//                    refreshList(s, di.getFilepath());
            }
        }
        // Put the data into the list
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
        if (requestCode == INTENT_MAIN && resultCode == RESULT_OK)
            finish();
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_COPY) {
                if (data.hasExtra(DIRECTORY_COPY_TO)) {
                    List<DirectoryItem> list = directoryItemAdapter.getList();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSelected()) {
                            DirectoryItem di = (DirectoryItem) list.get(i);
                            try {
                                copyFile(new File(list.get(i).getFilepath()), new File(data.getStringExtra(DIRECTORY_COPY_TO)));
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
                            DirectoryItem di = list.get(i);
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
        ibHome = (ImageButton) findViewById(R.id.ibHome);
        ibHome.setOnClickListener((v) -> {
            finish();
        });
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Toast.makeText(SearchActivity.this, "on" + s, Toast.LENGTH_LONG).show();
                items.clear();
                directoryItemAdapter.updateList(items);
                refreshList(s.toString(), "/");
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);
        llShare = (LinearLayout) findViewById(R.id.llShare);
        llShare.setOnClickListener((v) -> {
            List<DirectoryItem> list = directoryItemAdapter.getList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSelected()) {
                    DirectoryItem di = (DirectoryItem) list.get(i);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(di.getFilepath())));
                    shareIntent.setType("image/jpg");
                    startActivity(Intent.createChooser(shareIntent, "Share"));
                }
            }

        });
        llDelete = (LinearLayout) findViewById(R.id.llDelete);
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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
//                                        refreshList();
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
                    Intent intent = new Intent(SearchActivity.this, CopyMoveActivity.class);
//                    intent.putExtra(PATH, path);
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
                    Intent intent = new Intent(SearchActivity.this, CopyMoveActivity.class);
//                    intent.putExtra(PATH, path);
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
                                                    Intent directoryIntent = new Intent(SearchActivity.this, MainActivity.class);
                                                    directoryIntent.putExtra(PATH, filename);
                                                    startActivityForResult(directoryIntent, INTENT_MAIN);
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
