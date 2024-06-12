package com.example.ngrfitness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Klasa aktywności obsługująca wyświetlanie mapy Google i zarządzanie lokalizacją.
 */
public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap myMap; // Zmienna do przechowywania instancji Google Map
    private static final int FINE_PERMISSION_CODE = 1; // Kod żądania uprawnień do lokalizacji
    private static final int FINE_PERMISSION_CODE2 = 2; // Kod żądania uprawnień do wysyłania SMS-ów
    Location currentLocation; // Obiekt lokalizacji użytkownika
    FusedLocationProviderClient fusedLocationProviderClient; // Klient do uzyskiwania lokalizacji
    LatLng destinationLatLng, myLocation; // Zmienne do przechowywania współrzędnych
    SearchView mapSearchView; // Widok wyszukiwania lokalizacji na mapie
    Button pathFinderBtn, sendHelpBtn, backBtn; // Przycisk do znalezienia ścieżki, wysłania SMS-a i powrotu
    private ApiInterface apiInterface; // Interfejs do komunikacji z API Google Maps
    private List<LatLng> polylinelist; // Lista współrzędnych polilinii
    private PolylineOptions polylineOptions; // Opcje polilinii
    private LatLng origion, dest; // Współrzędne początkowe i końcowe

    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     *
     * @param savedInstanceState Zapisany stan instancji.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Włącz wyświetlanie krawędzi do krawędzi
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_google_maps);
        mapSearchView = findViewById(R.id.mapSearch);
        pathFinderBtn = findViewById(R.id.pathFinderBtn);
        sendHelpBtn = findViewById(R.id.sendHelpBtn);
        backBtn = findViewById(R.id.goBack);

        // Ustaw wcięcia okna dla wyświetlania krawędzi do krawędzi
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja klienta do uzyskiwania lokalizacji
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Pobierz ostatnią znaną lokalizację użytkownika
        getLastLocation();

        // Ustaw listener dla pola wyszukiwania
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Po przesłaniu zapytania
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null) {
                    Geocoder geocoder = new Geocoder(GoogleMaps.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Pobierz współrzędne z adresu
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    destinationLatLng = latLng;
                    // Dodaj marker na mapie
                    myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Inicjalizacja Retrofit do połączenia z API Google Maps
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        // Listener dla przycisku "pathFinderBtn"
        pathFinderBtn.setOnClickListener(view -> {
            if (destinationLatLng != null) {
                // Dodaj marker na mapie
                myMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Test"));
                myMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLatLng));
                dest = new LatLng(destinationLatLng.latitude, destinationLatLng.longitude);
                origion = new LatLng(myLocation.latitude, myLocation.longitude);

                // Pobierz kierunki
                getDirection(myLocation.latitude + "," + myLocation.longitude,
                        destinationLatLng.latitude + "," + destinationLatLng.longitude);
            }
        });

        // Listener dla przycisku "sendHelpBtn"
        sendHelpBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(GoogleMaps.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                // Żądanie uprawnień do wysyłania SMS-ów
                ActivityCompat.requestPermissions(GoogleMaps.this, new String[]{Manifest.permission.SEND_SMS}, FINE_PERMISSION_CODE2);
            }
        });

        // Listener dla przycisku "backBtn"
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Obsługa przycisku wstecz
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(GoogleMaps.this, MainActivity.class);
                startActivity(intent);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Metoda do wysyłania SMS-a z prośbą o pomoc.
     */
    private void sendSMS() {
        String phone = "+1-555-521-5554";
        String message = "Potrzebuję pilnej pomocy! Moje współrzędne:\n " + myLocation.latitude + "," + myLocation.longitude;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Toast.makeText(this, GoogleMaps.this.getResources().getString(R.string.pomoc), Toast.LENGTH_SHORT).show();
    }

    /**
     * Metoda do pobierania kierunków.
     *
     * @param origin      Punkt początkowy w formacie "latitude,longitude".
     * @param destination Punkt docelowy w formacie "latitude,longitude".
     */
    private void getDirection(String origin, String destination) {
        apiInterface.getDirection("driving", "less_driving", origin, destination,
                        getString(R.string.my_ip_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Result result) {
                        polylinelist = new ArrayList<>();
                        List<Route> routeList = result.getRoutes();
                        for (Route route : routeList) {
                            String polyline = route.getOverviewPolyline().getPoints();
                            polylinelist.addAll(decodePoly(polyline));
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getApplicationContext(), com.google.android.material.R.color.design_default_color_primary));
                        polylineOptions.width(8);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylinelist);
                        myMap.addPolyline(polylineOptions);

                        // Ustawienie kamery na trasie
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(origion);
                        builder.include(dest);
                        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(GoogleMaps.this, GoogleMaps.this.getResources().getString(R.string.blad), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Metoda do pobierania ostatniej znanej lokalizacji użytkownika.
     */
    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                // Inicjalizacja fragmentu mapy
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(GoogleMaps.this);
            } else {
                Toast.makeText(GoogleMaps.this, GoogleMaps.this.getResources().getString(R.string.brak_lokalizcji), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Metoda wywoływana, gdy mapa jest gotowa do użycia.
     *
     * @param googleMap Obiekt Google Map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Dodaj marker na aktualnej lokalizacji użytkownika
        myMap.addMarker(new MarkerOptions().position(myLocation).title("My location"));

        // Przesuń kamerę do aktualnej lokalizacji użytkownika
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));
    }

    /**
     * Metoda do dekodowania polilinii z kodowanej reprezentacji.
     *
     * @param encoded Kodowana reprezentacja polilinii.
     * @return List<LatLng> Lista współrzędnych polilinii.
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    /**
     * Metoda wywoływana po zakończeniu żądania uprawnień.
     *
     * @param requestCode  Kod żądania uprawnień.
     * @param permissions  Tablica żądanych uprawnień.
     * @param grantResults Wyniki przyznania uprawnień.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, GoogleMaps.this.getResources().getString(R.string.brak_dost_lokalizcji), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == FINE_PERMISSION_CODE2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, GoogleMaps.this.getResources().getString(R.string.brak_pozwolenia), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
