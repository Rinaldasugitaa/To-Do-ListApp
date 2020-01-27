package com.rinal.todolistapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private FloatingActionButton fab;
    private ListView listView;
    public EditText edtAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtask();
            }
        });
        items = new ArrayList<>();
        showSP();

        itemsAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int which_item = position;

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Apakah Anda Yakin ?")
                        .setMessage("Apakah Anda Ingin Menghapusnya ? ")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                items.remove(which_item);
                                itemsAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                builder2.setMessage("Are You Sure ? ");
                builder2.setTitle("Want To Change It ?");
                final EditText inputfield2 = new EditText(MainActivity.this);
                inputfield2.setText(items.get(position));
                builder2.setView(inputfield2);
                builder2.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Value = inputfield2.getText().toString();
                        items.remove(position);
                        items.add(position, Value);
                        sortDel();

                        itemsAdapter.notifyDataSetChanged();
                    }
                });
                builder2.setNegativeButton("Cancel", null);
                builder2.show();
            }
        });
    }
    public void sortDel(){
        SharedPreferences sh = getSharedPreferences("todo",MODE_PRIVATE);
        SharedPreferences.Editor editor= sh.edit();

        editor.clear();
        editor.apply();

        for(int i=0; i < items.size(); i++){
            editor.putString(String.valueOf(i), items.get(i));
        }

        editor.apply();

    }

    public void initial() {
        fab = findViewById(R.id.fab);
        listView = findViewById(R.id.lv);

    }

    public void addtask() {
        final View view = View.inflate(this,R.layout.edt_txt_alert, null);
        edtAdd = view.findViewById(R.id.edt_add);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ingin Menambah Catatan ? ");
        builder.setTitle("Add New");
        //final EditText inputField = new EditText(this);
        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int newKey = items.size();
                String input = edtAdd.getText().toString();
                boolean isEmptyInput = false;

                if (TextUtils.isEmpty(edtAdd.getText().toString())){
                    isEmptyInput = true;
                    edtAdd.setError("Reeminder Tidak Boleh Kosong");
                    //Toast.makeText(getApplicationContext(),"Reeminder Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    items.add(newKey, input);
                    addToSh(newKey, input);
                    itemsAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Data telah di tambahkan", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
    private void delSP(int position){
        String key = String.valueOf(position);
        SharedPreferences sh = getSharedPreferences("todo", MODE_PRIVATE);
        sh.edit().remove(key);
        //hapus 1 item. celah kosong
    }

    private void sortSP(){
        SharedPreferences sh = getSharedPreferences("todo",MODE_PRIVATE);
        SharedPreferences.Editor editor= sh.edit();
        editor.clear();
        editor.apply();
        for(int i = 0; i < items.size();i++){
            editor.putString(String.valueOf(i),items.get(i));
        }
        editor.apply();

    }

    private void showSP(){
        SharedPreferences sh = getSharedPreferences("todo", MODE_PRIVATE);
        if (sh.getAll().size() > 0){
            for (int i=0 ; i< sh.getAll().size(); i++){
                String key = String.valueOf(i);
                items.add(sh.getString(key, null));
            }
        }
    }

    private void addToSh(int key, String item){
        SharedPreferences sh = getSharedPreferences("todo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        String k = String.valueOf(key);
        editor.putString(k, item);
        editor.apply();
    }


    private void ShowDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Konfirmasi Keluar")
                .setMessage("Anda Yakin Ingin Keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false);
        alert.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ShowDialog();
        }
        return true;
    }

}