package com.penfolddev.cabahivelocator;

import android.animation.Animator;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Unbinder unbinder;

    @BindView(R.id.instructions)
    TextView instructions;

    @BindView(R.id.form)
    LinearLayout form;

    @BindView(R.id.description)
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(longClickListener());
        mMap.setOnCameraMoveListener(this);
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private GoogleMap.OnMapLongClickListener longClickListener() {
        return latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(false));
            form.animate().translationY(0).alpha(1.0f);
            instructions.animate().translationY(-instructions.getHeight()).alpha(0.0f);
        };
    }

    @Override
    public void onCameraMove() {
        mMap.clear();
        instructions.animate().translationY(0).alpha(1.0f);
        form.animate().translationY(form.getHeight()).alpha(0.0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                description.setText("");
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng target = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(mMap.getMaxZoomLevel() / 1.5f);
        builder.target(target);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
