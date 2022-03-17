package com.ark.mainmarket.View.User.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.ark.mainmarket.Model.ModelUser;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.Auth.Login;
import com.ark.mainmarket.View.User.Admin.AdministratorMenu;
import com.ark.mainmarket.databinding.FragmentAccountBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Account extends Fragment {

    public Account() {
        // Required empty public constructor
    }

    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private LocationRequest locationRequest;
    private List<Address> addressList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataProfile();
    }

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        listenerComponent();
        return binding.getRoot();
    }

    private void listenerComponent() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        binding.locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (Utility.isGPSEnabled(requireContext())) {
                            binding.progressCircular.setVisibility(View.VISIBLE);
                            LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(this);

                                    if (locationResult.getLocations().size() > 0){
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();


                                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                        try {
                                            addressList = geocoder.getFromLocation(latitude, longitude,1);
                                            binding.locationTiEdit.setText(addressList.get(0).getAddressLine(0));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        Utility.toastLS(requireContext(), "Gagal mengambil lokasi");
                                    }
                                    binding.progressCircular.setVisibility(View.GONE);
                                }
                            }, Looper.getMainLooper());

                        } else {
                            Utility.turnOnGPS(locationRequest, requireActivity());
                        }
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                    }
                }
            }
        });

        binding.administratorBtn.setOnClickListener(view -> Utility.updateUI(requireContext(), AdministratorMenu.class));

        binding.logoutBtn.setOnClickListener(view -> {
            auth.signOut();
            Utility.updateUI(requireContext(), Login.class);
        });

        binding.editDataProfile.setOnClickListener(view -> {
            String username = Objects.requireNonNull(binding.usernameEditTi.getText()).toString();
            String email = Objects.requireNonNull(binding.emailEditTi.getText()).toString();
            String phoneNumber = Objects.requireNonNull(binding.phoneNumberEditTi.getText()).toString();
            String address = Objects.requireNonNull(binding.locationTiEdit.getText()).toString();

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
            Toast.makeText(requireContext(), "Berhasil mengubah data anda", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Terjadi kesalahan, coba lagi nanti", Toast.LENGTH_SHORT).show();
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
                Utility.toastLS(requireContext(), Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }



}