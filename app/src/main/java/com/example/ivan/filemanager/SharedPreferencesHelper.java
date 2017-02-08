package com.example.ivan.filemanager;

import android.content.SharedPreferences;
import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ivan.filemanager.Constants.FAVORITES;
import static com.example.ivan.filemanager.Constants.LIST_OF_FAVORITES;

/**
 * Created by Иван on 08.02.2017.
 */

public class SharedPreferencesHelper {
    private SharedPreferences sPref;
    private Context context;

    SharedPreferencesHelper(Context context) {
        this.context = context;
        this.sPref = context.getSharedPreferences(FAVORITES, MODE_PRIVATE);
    }

    public void addToFavorites(String s) {
        Set<String> favoriteFiles = sPref.getStringSet(LIST_OF_FAVORITES, getFavorites());
        if (favoriteFiles == null)
            favoriteFiles = new HashSet<>();
        favoriteFiles.add(s);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putStringSet(LIST_OF_FAVORITES, favoriteFiles);
        ed.commit();
    }

    public Set<String> getFavorites() {
        Set<String> favoriteFiles = sPref.getStringSet(LIST_OF_FAVORITES, null);
        return favoriteFiles;
    }
}
