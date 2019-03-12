package com.kandroid.kropper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionManager {
    private Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean isPermissionGranted(String permissionName) {
        return ActivityCompat.checkSelfPermission(activity, permissionName)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void askForPermission(String permissionName, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[] {permissionName}, requestCode);
    }
}
