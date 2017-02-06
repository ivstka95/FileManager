package com.example.ivan.filemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.ivan.filemanager.Constants.INTENT_MAIN;
import static com.example.ivan.filemanager.Constants.PATH;

public class CategoryActivity extends AppCompatActivity {
    private static DirectoryItemAdapter directoryItemAdapter;
    private static List items = new ArrayList<DirectoryItem>();
    private ListView listView;
    private static boolean checkBoxVisibility = false;
    private LinearLayout llButtons;
    private ImageButton ibHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setViews();
        directoryItemAdapter = new DirectoryItemAdapter(this, R.layout.layout_list_item);
        listView.setAdapter(directoryItemAdapter);
    }

    public static boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    @Override
    protected void onResume() {
        Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
        refreshList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (checkBoxVisibility)
            checkBoxVisibility = false;
        else finish();
        llButtons.setVisibility(View.GONE);
    }

    private Set<String> getFavorites() {
        SharedPreferences sPref = getSharedPreferences("Favorites", MODE_PRIVATE);
        Set<String> favoriteFiles = sPref.getStringSet("listOfFavorites", null);
        Toast.makeText(this, "Favorite loaded", Toast.LENGTH_SHORT).show();
        return favoriteFiles;
    }

    public void refreshList() {
        items.clear();
        List<String> favorites = new ArrayList<String>(getFavorites());
        for (String s: favorites) {
            items.add(new DirectoryItem(s));
        }
        directoryItemAdapter.updateList(items);
    }

    private int getCountOfSelectedItems() {
        int count = 0;
        List<DirectoryItem> list = directoryItemAdapter.getList();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getSelected())
                count++;
        return count;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        llButtons.setVisibility(View.GONE);
        if (requestCode == INTENT_MAIN && resultCode == RESULT_OK)
            finish();
        if (resultCode == RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //a method sets views
    private void setViews() {
        ibHome = (ImageButton) findViewById(R.id.ibHome);
        ibHome.setOnClickListener((v) -> {
            finish();
        });

        listView = (ListView) findViewById(R.id.listView);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                DirectoryItem file = (DirectoryItem) items.get(position);
                                                String filename = file.getFilepath();
                                                File intentFile = new File(filename);
                                                if (intentFile.isDirectory()) {
                                                    Intent directoryIntent = new Intent(CategoryActivity.this, MainActivity.class);
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
