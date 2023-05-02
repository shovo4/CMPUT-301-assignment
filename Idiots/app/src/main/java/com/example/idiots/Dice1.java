package com.example.idiots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Dice1 extends AppCompatActivity {
    private ArrayList<String> dice = new ArrayList<>();
    private ArrayList<String> listOfTables = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice1);
        loadData();
        sqLiteDatabase = this.openOrCreateDatabase("MyData", MODE_PRIVATE, null);
        String name = getIntent().getStringExtra("Table_Name");


            sqLiteDatabase.execSQL("CREATE TABLE [" + name + "] (id TEXT PRIMARY KEY, rolls INTEGER)");
            setDefault(name);

        listOfTables.add(name);
        save();

        for(int i = 1; i <= 6; i++){
            dice.add(i+"");
        }
        listView = findViewById(R.id.listView_Dice1);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dice);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addData(name, arrayAdapter.getItem(i));
            }
        });
    }

    public void save(){
        SharedPreferences prf = getSharedPreferences("save_arrays", MODE_PRIVATE);
        SharedPreferences.Editor editor = prf.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listOfTables);
        editor.putString("Lists", json);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences prf = getSharedPreferences("save_arrays", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString("Lists", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        listOfTables = gson.fromJson(json, type);
        if(listOfTables == null){
            listOfTables = new ArrayList<>();
        }
    }

    public boolean search(String s){
        if (listOfTables ==null){
            return true;
        }
        for (int i =0; i <listOfTables.size(); i++){
            if (listOfTables.get(i).equals(s)){
                return false;
            }
        }
        return true;
    }

    public void setDefault(String table_name) {
        for (int j = 1; j <= 6; j++) {
            sqLiteDatabase.execSQL("INSERT INTO [" + table_name + "] (id, rolls) VALUES (" + j + ", 0)");
        }
    }

    public void updateValue(String name, String id, int update_data){
        String q = "UPDATE ["+name + "] SET rolls = " +update_data+ " WHERE id = " +id;
        sqLiteDatabase.execSQL(q);
    }

    public boolean addData(String name, String id){
        Cursor info = getEntry(name, id);
        int existed_data = -1;
        int col = info.getColumnIndex("rolls");
        while(info.moveToNext()){
            existed_data = info.getInt(col);
        }
        if(existed_data > -1) {
            int update_data = existed_data + 1;
            updateValue(name, id, update_data);
            Log.i("data ", update_data+"");
            return true;
        }
        else{
            return false;
        }
    }
    public Cursor getEntry(String name, String id){
        String q = "SELECT rolls FROM ["+name+"] WHERE id = " +id+"";
        Cursor data = sqLiteDatabase.rawQuery(q, null);
        return data;
    }
}