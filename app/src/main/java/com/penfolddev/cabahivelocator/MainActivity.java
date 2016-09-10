package com.penfolddev.cabahivelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private Unbinder unbinder;  

    @OnClick(R.id.get_started_button)
    void navigateToMapActivity() {
        MainActivityPermissionsDispatcher.accessLocationWithCheck(this);
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    void accessLocation() {
        // navigate to maps activity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnShowRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)
    void showLocationRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_location_rationale)
                .setPositiveButton(getString(R.string.button_allow), (dialog, button) -> request.proceed())
                .setNegativeButton(getString(R.string.button_deny), (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(android.Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        Toast.makeText(this, R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(android.Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        Toast.makeText(this, R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
