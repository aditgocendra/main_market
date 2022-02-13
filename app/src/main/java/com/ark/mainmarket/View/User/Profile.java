package com.ark.mainmarket.View.User;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import com.ark.mainmarket.Model.ModelUser;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.Admin.AdministratorMenu;
import com.ark.mainmarket.databinding.ActivityProfileBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private LocationRequest locationRequest;
    private List<Address> addressList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Utility.roleCurrentUser.equals("Admin")) {
            binding.administratorBtn.setVisibility(View.VISIBLE);
        }

        listenerClick();
        setDataProfile();
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> {
            Utility.updateUI(Profile.this, HomeApp.class);
            finish();
        });

        binding.administratorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.roleCurrentUser.equals("Admin")){
                    Utility.updateUI(Profile.this, AdministratorMenu.class);
                }

            }
        });

        binding.logoutBtn.setOnClickListener(view -> auth.signOut());

        binding.editDataProfile.setOnClickListener(view -> {
            String username = binding.usernameEditTi.getText().toString();
            String email = binding.emailEditTi.getText().toString();
            String phoneNumber = binding.phoneNumberEditTi.getText().toString();
            String address = binding.locationTiEdit.getText().toString();

            if (username.isEmpty()) {
                binding.usernameEditTi.setError("Username kosong");
            } else if (email.isEmpty()) {
                binding.emailEditTi.setError("Email kosong");
            } else if (phoneNumber.isEmpty()) {
                binding.phoneNumberEditTi.setError("Nomor telpon harus diisi");
            } else if (address.isEmpty()) {
                binding.locationTiEdit.setError("Alamat tidak boleh kosong");
            } else {
                changeProfile(username, email, phoneNumber, address);
                binding.progressCircular.setVisibility(View.VISIBLE);
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        binding.locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (isGPSEnabled()) {
                            binding.progressCircular.setVisibility(View.VISIBLE);
                            LocationServices.getFusedLocationProviderClient(Profile.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(Profile.this).removeLocationUpdates(this);

                                    if (locationResult.getLocations().size() > 0){
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();


                                        Geocoder geocoder = new Geocoder(Profile.this, Locale.getDefault());
                                        try {
                                            addressList = geocoder.getFromLocation(latitude, longitude,1);
                                            binding.locationTiEdit.setText(addressList.get(0).getAddressLine(0));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        Utility.toastLS(Profile.this, "Gagal mengambil lokasi");
                                    }
                                    binding.progressCircular.setVisibility(View.GONE);
                                }
                            }, Looper.getMainLooper());

                        } else {
                            turnOnGPS();
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> responseTask = LocationServices.getSettingsClient(Profile.this).checkLocationSettings(builder.build());

        responseTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    task.getResult(ApiException.class);
                    Utility.toastLS(Profile.this, "GPS is On");
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Profile.this, 1005);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Utility.toastLS(Profile.this, "Device do not have location");
                            break;
                    }
                }
            } else {
                Utility.toastLS(Profile.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void changeProfile(String username, String email, String phoneNumber, String address) {
        ModelUser modelUser = new ModelUser(
                username,
                email,
                phoneNumber,
                address,
                Utility.roleCurrentUser,
                "-"
        );

        reference.child("users").child(user.getUid()).setValue(modelUser).addOnSuccessListener(unused -> {
            binding.progressCircular.setVisibility(View.GONE);
            Utility.updateUI(Profile.this, HomeApp.class);
            finish();
        }).addOnFailureListener(e -> {
            Utility.toastLS(Profile.this, e.getMessage());
            binding.progressCircular.setVisibility(View.VISIBLE);
        });
    }

    private void setDataProfile(){
        reference.child("users").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                ModelUser modelUser = Objects.requireNonNull(task.getResult()).getValue(ModelUser.class);
                assert modelUser != null;
                binding.usernameEditTi.setText(modelUser.getUsername());
                binding.emailEditTi.setText(modelUser.getEmail());

                if (!modelUser.getUrl_photo().equals("-")){
                    Picasso.get().load(modelUser.getUrl_photo()).into(binding.profileImg);
                }

                if (!modelUser.getPhone_number().equals("-")){
                    binding.phoneNumberEditTi.setText(modelUser.getPhone_number());
                }

                if (!modelUser.getAddress().equals("-")){
                    binding.locationTiEdit.setText(modelUser.getAddress());
                }

            }else {
                Utility.toastLS(Profile.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }
}