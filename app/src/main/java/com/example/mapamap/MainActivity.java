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

import com.example.mapamap.WebService.Asynchtask;
import com.example.mapamap.WebService.WebService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Asynchtask {
GoogleMap mapa;

Double lat , lng ;
EditText txtLat , txtLong;
Circle circulo=null;
Slider sliderRadio;
float Radio ;
List<Marker> markers = new ArrayList<Marker>();
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
        mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://turismoquevedo.com/lugar_turistico/json_getlistadoMapa?lat=" + lat+ "&lng=" + lng+"&radio=" + (Radio/10.0) ,datos, MainActivity.this, MainActivity.this);
        ws.execute("GET");
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


    @Override
    public void processFinish(String result) throws JSONException {


        for(Marker marker: markers) marker.remove();
        markers.clear();

        List<Marker> markers= new ArrayList<Marker>();
        JSONObject JSONobj= new JSONObject(result);
        JSONArray jsonLista= JSONobj.getJSONArray("data");
        for(int i=0; i< jsonLista.length(); i++) {
            JSONObject lugar = jsonLista.getJSONObject(i);
            markers.add(mapa.addMarker(
                    new MarkerOptions().position(
                            new LatLng(lugar.getDouble("lat"), lugar.getDouble("lng"))
                    ).title(lugar.get("nombre").toString())));
        }
    }
}