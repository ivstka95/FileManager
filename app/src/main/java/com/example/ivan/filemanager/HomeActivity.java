package com.example.ivan.filemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import static com.example.ivan.filemanager.Constants.CATEGORY;
import static com.example.ivan.filemanager.Constants.DOCUMENTS;
import static com.example.ivan.filemanager.Constants.FAVORITES;
import static com.example.ivan.filemanager.Constants.MUSIC;
import static com.example.ivan.filemanager.Constants.PICTURES;
import static com.example.ivan.filemanager.Constants.VIDEOS;


public class HomeActivity extends AppCompatActivity {
    private ImageButton ibSearch;
    private LinearLayout llFavorites;
    private LinearLayout llPictures;
    private LinearLayout llMusic;
    private LinearLayout llVideos;
    private LinearLayout llDocuments;
    private LinearLayout llAllFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        llAllFiles = (LinearLayout) findViewById(R.id.llAllFiles);
        llAllFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allFilesIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(allFilesIntent);
            }
        });
        llPictures = (LinearLayout) findViewById(R.id.llPictures);
        llPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCategory = new Intent(getApplicationContext(),CategoryActivity.class);
                intentCategory.putExtra(CATEGORY, PICTURES);
                startActivity(intentCategory);
            }
        });
        llMusic = (LinearLayout) findViewById(R.id.llMusic);
        llMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCategory = new Intent(getApplicationContext(),CategoryActivity.class);
                intentCategory.putExtra(CATEGORY, MUSIC);
                startActivity(intentCategory);
            }
        });
        llVideos = (LinearLayout) findViewById(R.id.llVideos);
        llVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCategory = new Intent(getApplicationContext(),CategoryActivity.class);
                intentCategory.putExtra(CATEGORY, VIDEOS);
                startActivity(intentCategory);
            }
        });
        llDocuments = (LinearLayout) findViewById(R.id.llDocuments);
        llDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCategory = new Intent(getApplicationContext(),CategoryActivity.class);
                intentCategory.putExtra(CATEGORY, DOCUMENTS);
                startActivity(intentCategory);
            }
        });
        llFavorites = (LinearLayout) findViewById(R.id.llFavourites);
        llFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCategory = new Intent(getApplicationContext(),CategoryActivity.class);
                intentCategory.putExtra(CATEGORY, FAVORITES);
                startActivity(intentCategory);
            }
        });
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener((v) -> {
            Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
            startActivity(intent);
        });
    }


}
