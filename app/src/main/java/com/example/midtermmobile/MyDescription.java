package com.example.midtermmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyDescription extends AppCompatActivity {
    private MyLocation myLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_description);
        loadData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem item = menu.findItem(R.id.action_direction);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myCollection:
                Intent intent_collection = new Intent(getApplicationContext(), MyCollection.class);
                intent_collection.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent_collection);
                return true;

            case R.id.action_home:
                Intent intent_home = new Intent(getApplicationContext(), MainActivity.class);
                intent_home.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent_home);
                return true;
            case R.id.action_map:
                Intent intent_map = new Intent(getApplicationContext(), MapsActivity.class);

                intent_map.putExtra("name", myLocation.getLocationName());
                intent_map.putExtra("description", myLocation.getLocationDescription());
                intent_map.putExtra("picture", myLocation.getLocationPicture());
                intent_map.putExtra("lat", myLocation.getLocationLat());
                intent_map.putExtra("lng", myLocation.getLocationLng());
                startActivity(intent_map);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        myLocation = (MyLocation) bundle.getSerializable("myLocation");

        ImageView imageView = findViewById(R.id.imageView_locationImageDescription);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),myLocation.getLocationPicture());
        imageView.setImageBitmap(bmp);
        TextView name = findViewById(R.id.textView_locationNameDescription);
        name.setText(myLocation.getLocationName());

        TextView description = findViewById(R.id.textView_Description);
        description.setText(myLocation.getLocationDescription());
    }
}