package com.example.mapamap;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
GoogleMap mapa;

Double lat , lng ;
EditText txtLat , txtLong;
Circle circulo=null;
Slider sliderRadio;
float Radio ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtLat= findViewById(R.id.txtlat);
        txtLong= findViewById(R.id.txtlon);
        sliderRadio= findViewById(R.id.sliderRadio);
        sliderRadio.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                Radio= slider.getValue();
                updateInterfaz();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {

            }
        });



    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);

        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(-1.012351, -79.46956), 19);
        mapa.moveCamera(camUpd1);


        LatLng punto = new LatLng(-1.012351,
                -79.46956);
        mapa.addMarker(new
                MarkerOptions().position(punto)
                .title("UTEQ"));





        mapa.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mapa.getCameraPosition().target;
                lat = center.latitude;
                lng =center.longitude;
                updateInterfaz();
            }
        });






    }

    private void updateInterfaz(){
        txtLat.setText(String.format("%.4f", lat));
        txtLong.setText(String.format("%.4f", lng));
        PintarCirculo();
    }

    private void PintarCirculo(){

        if(circulo!=null){ circulo.remove(); circulo = null; }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat,lng))
                .radius(Radio*100) //En Metros
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 150, 50, 50));
        circulo = mapa.addCircle(circleOptions);


    }


}