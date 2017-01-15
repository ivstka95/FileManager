package com.example.ivan.filemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public static DirectoryItemAdapter directoryItemAdapter;
    List items = new ArrayList<DirectoryItem>();
    private String path;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        // Use the current directory as title
        path = "/";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
//        setTitle(path);

// Read all files sorted into the values-array

        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    items.add(new DirectoryItem(path, file));
                }
            }
        }
//        Collections.sort(values);Collections.sort(items);

        // Put the data into the list
        directoryItemAdapter = new DirectoryItemAdapter(this,
                R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
        directoryItemAdapter.updateList(items);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryItem file = (DirectoryItem) items.get(position);
                String filename;
                if (path.endsWith(File.separator)) {
                    filename = path + file.getName();
                } else {
                    filename = path + File.separator + file.getName();
                }
                Toast.makeText(MainActivity.this, filename, Toast.LENGTH_SHORT).show();
                if (new File(filename).isDirectory()) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("path", filename);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, filename + " is not a directory", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




