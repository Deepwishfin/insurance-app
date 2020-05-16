package com.example.dialerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    Handler handler;
    boolean permission = false;
    int PERMISSION_ALL = 1;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile);

        prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        checkforpermission();
        getdevicetoken();

        final PackageManager packageManager = getPackageManager();

        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            if ("com.android.vending".equals(packageManager.getInstallerPackageName(applicationInfo.packageName))) {
                Toast.makeText(getApplicationContext(),"Play store",Toast.LENGTH_LONG).show();
            }  else if ("com.amazon.venezia".equals(packageManager.getInstallerPackageName(applicationInfo.packageName))) {
                Toast.makeText(getApplicationContext(),"Amzon store",Toast.LENGTH_LONG).show();
            }  else if ("null".equals(packageManager.getInstallerPackageName(applicationInfo.packageName))){
                Toast.makeText(getApplicationContext(),"Else store",Toast.LENGTH_LONG).show();
            }
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void checkforpermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            if (!permission) {
                if (checkAndRequestPermissions()) {
                    // carry on the normal flow, as the case of  permissions  granted.
                    callanotherclass();
                    permission = true;
                }
            }
        } else {
            callanotherclass();
        }
    }

    private void callanotherclass() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SessionManager.get_login(prefs).equalsIgnoreCase("True")) {
                    Intent intent = new Intent(SplashActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1500);

    }

    private boolean checkAndRequestPermissions() {

        int permissioncallphone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionreadcalllogs = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int permissionwritecalllogs = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissioncallphone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (permissionreadcalllogs != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }

        if (permissionwritecalllogs != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CALL_LOG);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            checkforpermission();
        } else {
            finish();
        }
    }

    private void getdevicetoken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            SessionManager.save_device_token(prefs, "Not Found");
                            return;
                        }
                        try {
                            SessionManager.save_device_token(prefs, (task.getResult()).getToken());
                        } catch (Exception e) {
                            SessionManager.save_device_token(prefs, "Not Found");
                        }
                    }
                });

    }
}


