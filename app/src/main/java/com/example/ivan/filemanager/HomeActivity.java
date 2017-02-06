package com.example.ivan.filemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {
    private ImageButton ibSearch;
    private LinearLayout llFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        llFavorites = (LinearLayout) findViewById(R.id.llFavourites);
        llFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CategoryActivity.class);
                startActivity(intent);
            }
        });
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener((v) -> {
            Intent io = new Intent(HomeActivity.this,SearchActivity.class);
            startActivity(io);
            Toast.makeText(HomeActivity.this, "eeeeeeeeeeee", Toast.LENGTH_SHORT).show();
        });
    }


}
