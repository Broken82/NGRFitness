package com.example.ngrfitness;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap myMap;
    private static final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng destinationLatLng,myLocation;
    SearchView mapSearchView;
    Button pathFinderBtn;
    private ApiInterface apiInterface;
    private List<LatLng> polylinelist;
    private PolylineOptions polylineOptions;
    private LatLng origion,dest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_google_maps);
        mapSearchView=findViewById(R.id.mapSearch);
        pathFinderBtn =findViewById(R.id.pathFinderBtn);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null){
                    Geocoder geocoder =new Geocoder(GoogleMaps.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Address address = addressList.get(0);
                    LatLng latLng= new LatLng(address.getLatitude(),address.getLongitude());
                    destinationLatLng=latLng;
                    myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        Retrofit retrofit=new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .baseUrl("https://maps.googleapis.com/")
                                        .build();
        apiInterface=retrofit.create(ApiInterface.class);


        pathFinderBtn.setOnClickListener(view -> {
            myMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Test"));
            myMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLatLng));
        });

    }

    private void getDirection(String origin, String destination){
        apiInterface.getDirection("driving","less_driving",origin,destination,
                getString(R.string.my_ip_key)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Result resoult) {
                        polylinelist= new ArrayList<>();
                        List<Route> routeList=resoult.getRoutes();
                        for (Route route:routeList){
                            String polyline= route.getOverviewPolyline().getPoints();
                            polylinelist.addAll(decodePoly(polyline));
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getApplicationContext(), com.google.android.material.R.color.design_default_color_primary));
                        polylineOptions.width(8);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylinelist);
                        myMap.addPolyline(polylineOptions);


                        LatLngBounds.Builder builder= new LatLngBounds.Builder();
                        builder.include(origion);
                        builder.include(dest);
                        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(GoogleMaps.this, "sadasdjasdjlkasjdlkasjdkalsd", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation =location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(GoogleMaps.this);
                }else {
                    Toast.makeText(GoogleMaps.this, "Brak lokalizacji emulatora", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;


        myLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());


        myMap.addMarker(new MarkerOptions().position(myLocation).title("My location"));

        //myMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        origion= new LatLng(30,76);
        dest= new LatLng(28,77);
        getDirection("30" + "," + "76","28" + "," + "77");

    }


    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<>();
        int index=0, len=encoded.length();
        int lat=0,lng=0;
        while(index<len){
            int b,shift=0,resoult=0;
            do{
                    b=encoded.charAt(index++)-63;
                    resoult |=(b & 0x1f) <<shift;
                    shift+=5;
            } while(b>=0x20);
            int dlat=((resoult & 1) != 0 ? ~(resoult>>1):(resoult>>1));
            lat+=dlat;
            shift=0;
            resoult=0;
            do{
                b=encoded.charAt(index++)-63;
                resoult |=(b & 0x1f) <<shift;
                shift+=5;
            }while (b>=0x20);
            int dlng =((resoult & 1) != 0 ? ~(resoult>>1):(resoult>>1));
            lng+=dlng;

            LatLng p = new LatLng((((double) lat/1E5)),(((double) lng/1E5)));
            poly.add(p);
        }

        return poly;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== FINE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLastLocation();

            }
            else{
                    Toast.makeText(this,"Brak dostępu do lokalizacji",Toast.LENGTH_SHORT).show();
            }
        }
    }
}