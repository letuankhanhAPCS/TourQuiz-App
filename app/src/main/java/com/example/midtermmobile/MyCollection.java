package com.example.midtermmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyCollection extends AppCompatActivity {

    private GridView gridView;
    private MyLocationAdapter adapter;
    private ArrayList<MyLocation> locations;

    private GridView.OnItemClickListener itemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), MyDescription.class);

            MyLocation myLocation = locations.get(position);
            intent.putExtra("myLocation",myLocation);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        System.out.println("I'm here");
        loadData(getIntent());
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem myCollection_Item = menu.findItem(R.id.action_myCollection);
        myCollection_Item.setVisible(false);
        MenuItem item = menu.findItem(R.id.action_direction);
        item.setVisible(false);
        MenuItem map_Item = menu.findItem(R.id.action_map);
        map_Item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent_home = new Intent(getApplicationContext(), MainActivity.class);
                intent_home.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                setResult(RESULT_OK, intent_home);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadData(Intent data) {
        locations = new ArrayList<>();
        Bundle bundle = data.getExtras();
        locations = (ArrayList<MyLocation>) bundle.getSerializable("locations");
    }

    private void initComponents() {
        gridView = findViewById(R.id.gridView_places);
        adapter = new MyLocationAdapter(this, R.layout.my_collection_item, locations);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(itemClickListener);
    }
}