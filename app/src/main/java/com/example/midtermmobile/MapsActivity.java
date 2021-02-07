package com.example.midtermmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MyLocation mMyLocation;
    private Marker mMarker;
    private TextToSpeech mTextToSpeech;
    private boolean mIsTextToSpeechReady = true;

    String mapBoxToken = "pk.eyJ1IjoibmdvY25oaSIsImEiOiJja2R5djRxMWExaTJlMnFwaWY4ODE3dXRrIn0.Q82AZK-MGh8RXTRq4P3zvg";

    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadData();
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
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

            case R.id.action_direction:
                currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                getDestinationRoutes(String.valueOf(mMyLocation.getLocationLng()),
                        String.valueOf(mMyLocation.getLocationLat()),
                        mMyLocation.getLocationName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponents() {
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mIsTextToSpeechReady = true;
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        mMyLocation = new MyLocation(intent.getStringExtra("name"),
                intent.getStringExtra("description"),
                intent.getIntExtra("picture", 0),
                intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0),
                false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        displayMarker();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(mIsTextToSpeechReady){
                    mTextToSpeech.speak(mMyLocation.getLocationDescription(), TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), mMyLocation.getLocationDescription(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void displayMarker() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), mMyLocation.getLocationPicture());
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/4, bmp.getHeight()/4, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mMyLocation.getLocationLat(), mMyLocation.getLocationLng()))
                .title(mMyLocation.getLocationName())
                .snippet(mMyLocation.getLocationDescription())
                .icon(bitmapDescriptor)
        );

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mMyLocation.getLocationLat(), mMyLocation.getLocationLng()))
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getDestinationRoutes(String lng, String lat, final String place_name) {
        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/"
                + currentLocation.longitude + ","
                + currentLocation.latitude + ";"
                + lng + "," + lat
                + "?access_token=" + mapBoxToken + "&geometries=geojson";

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(MapsActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url , null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject routes = response.getJSONArray("routes").getJSONObject(0);
                            /*JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
                            double distance = legs.getInt("distance")/1000.0;
                            double duration = legs.getInt("duration")/60.0;*/
                            JSONObject geometry = routes.getJSONObject("geometry");
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            ArrayList<LatLng> arraySteps = new ArrayList<>();
                            for(int i = 0; i < coordinates.length() - 1; i++){
                                JSONArray LngLat = coordinates.getJSONArray(i);
                                double lng = LngLat.getDouble(0);
                                double lat = LngLat.getDouble(1);
                                arraySteps.add(new LatLng(lat, lng));
                            }
                            drawRoutes(arraySteps, place_name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MapsActivity.this, "Can't get destination route", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void drawRoutes(ArrayList<LatLng> arraySteps, String place_name) {
        mMap.clear();
        PolylineOptions polylineOptions = new PolylineOptions()
                .clickable(true)
                .addAll(arraySteps)
                .color(Color.BLUE)
                .width(10f);
        mMap.addPolyline(polylineOptions);
        LatLng latLng = currentLocation;
        mMap.addMarker(new MarkerOptions().position(latLng).title("My location"));
        mMap.addMarker(new MarkerOptions().position(arraySteps.get(arraySteps.size() - 1)).title(place_name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}