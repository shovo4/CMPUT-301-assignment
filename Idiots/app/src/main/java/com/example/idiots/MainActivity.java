package com.example.idiots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    EditText editText2;
    ListView listView_session;
    //ListView listView_dates;
    Button button_add, button_edit, button_delete;
    ArrayList<String> sessions;
    private ArrayAdapter<String> arrayAdapter;
    private Hashtable<String, String> store = new Hashtable<>();
    private int index_to_edit;

    public void add_sessions(View view) {
        String name = editText.getText().toString();
        String dice = editText2.getText().toString();
        sessions.add(name);
        store.put(name, dice);
        arrayAdapter.notifyDataSetChanged();
        editText.setText("");
        editText2.setText("");
        save();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        listView_session = findViewById(R.id.listView);
        editText = findViewById(R.id.editTextInput);
        editText2 = findViewById(R.id.editText_dice);
        button_add = findViewById(R.id.button_add);
        button_edit = findViewById(R.id.button_edit);
//      button_delete = findViewById(R.id.button_delete);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessions);
        listView_session.setAdapter(arrayAdapter);
        listView_session.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = listView_session.getItemAtPosition(i).toString();
                Intent ind = new Intent(getApplicationContext(), Histo.class);
                ind.putExtra("histogram", name);
                startActivity(ind);
                return true;
            }
        });

    }

    public void save(){
        SharedPreferences prf = getSharedPreferences("save_array", MODE_PRIVATE);
        SharedPreferences.Editor editor = prf.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sessions);
        editor.putString("List", json);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences prf = getSharedPreferences("save_array", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString("List", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        sessions = gson.fromJson(json, type);
        if(sessions == null){
            sessions = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.option_for_adding:
                adding();
                return true;
            case R.id.option_for_editing:
                editing();
                return true;
            case R.id.option_for_deleting:
                deleting();
                return true;
            default:
                return false;
        }
    }

    public void deleting(){
        listView_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                store.remove(arrayAdapter.getItem(i));
                sessions.remove(i);
                arrayAdapter.notifyDataSetChanged();
                save();
            }
        });
    }

    public void editing(){
        listView_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                button_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = editText.getText().toString();
                        store.put(str, store.get(arrayAdapter.getItem(i)));
                        sessions.set(i, str);
                        arrayAdapter.notifyDataSetChanged();
                        editText.setText("");
                        save();
                    }
                });
            }
        });
    }

    public void adding(){
        listView_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String go_to = "Dice".concat(store.get(sessions.get(i)));

                try {
                    Intent intent = new Intent(getApplicationContext(), Class.forName("com.example.idiots."+go_to));
                    intent.putExtra("Table_Name", sessions.get(i));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}