package com.example.ivan.filemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {
    private ImageButton ibSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener((v) -> {
            Intent iu = new Intent(HomeActivity.this,SearchActivity.class);
            startActivity(iu);
            Toast.makeText(HomeActivity.this, "eeeeeeeeeeee", Toast.LENGTH_SHORT).show();
        });
    }


}
