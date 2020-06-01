package com.example.dialerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    Button button, refresh, logout;
    TextView name;
    CallReceiver broadcastReceiver;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Dialog dialog;
    String currenttimestamp = "";
    String call_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(true);

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        queue = Volley.newRequestQueue(MainActivity.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        button = findViewById(R.id.button);
        refresh = findViewById(R.id.refresh);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);

        if (isThereInternetConnection()) {
            get_lead();
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet", Toast.LENGTH_LONG).show();

        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();
                TextView cancle = dialog.findViewById(R.id.cancle);
                TextView ok = dialog.findViewById(R.id.ok);

                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SessionManager.dataclear(prefs);
                        dialog.dismiss();
                        finish();
                    }
                });

            }
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isThereInternetConnection()) {
                    get_lead();
                } else {
                    Toast.makeText(MainActivity.this, "Please check your internet", Toast.LENGTH_LONG).show();

                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long tsLong = System.currentTimeMillis() / 1000;
                currenttimestamp = Long.toString(tsLong);
                getMd5(SessionManager.get_bidder_id(prefs) + "" + SessionManager.get_mobile(prefs) + "" + SessionManager.get_customer_mobile(prefs) + "" + SessionManager.get_product_type(prefs) + "" + currenttimestamp);

                ///////////////////Start Listener Service//////////////////////

                Intent i = new Intent(MainActivity.this, PhoneListener.class);
                startService(i);

            }
        });


    }

    private void call_dialer() {

        if (!SessionManager.get_customer_mobile(prefs).equalsIgnoreCase("")) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel: " + SessionManager.get_customer_mobile(prefs)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            startActivity(intent);
            registerReceiver(broadcastReceiver, new IntentFilter("CALL_ENDED"));

        } else {
            Toast.makeText(MainActivity.this, "Click Refresh Button First", Toast.LENGTH_LONG).show();
        }
    }

    private void get_lead() {

        final JSONObject json = new JSONObject();

        try {

            json.put("user_name", "" + SessionManager.get_username(prefs));
            json.put("mobile", "" + SessionManager.get_mobile(prefs));
            json.put("type", "Android_App");
            json.put("otp", "" + SessionManager.get_otp(prefs));
            json.put("app_version", Constants.app_version);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/get_lead_details", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response.toString());

                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                JSONObject jsonObject1 = (jsonObject.getJSONObject("data"));
                                progressDialog.dismiss();
                                button.setClickable(true);
                                SessionManager.save_customer_mobile(prefs, jsonObject1.getString("phone"));
                                name.setText(jsonObject1.getString("name"));
                                Toast.makeText(MainActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                button.setClickable(false);
                                name.setText("No Lead Available");

                                Toast.makeText(MainActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();

                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();

            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");

                return header;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);

    }

    protected boolean isThereInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }

    private void push_start_call_api() {

        final JSONObject json = new JSONObject();

        try {

            json.put("agentUserName", "" + SessionManager.get_username(prefs));
            json.put("agentMobileNumber", "" + SessionManager.get_mobile(prefs));
            json.put("customerMobileNumber", "" + SessionManager.get_customer_mobile(prefs));
            json.put("callStartTime", "" + getdateandtime());
            json.put("lmsType", "" + SessionManager.get_lms_type(prefs));
            json.put("productTye", "" + SessionManager.get_product_type(prefs));
            json.put("bidderId", "" + SessionManager.get_bidder_id(prefs));
            json.put("call_id", call_id);

            SessionManager.save_call_id(prefs,call_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.AYUSH_BASE_URL, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            progressDialog.dismiss();
                            call_dialer();


                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();

            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");

                return header;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);

    }

    private String getdateandtime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        return sdf.format(new Date());

    }

    public void getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            call_id = no.toString(16);
            while (call_id.length() < 32) {
                call_id = "0" + call_id;
            }


            push_start_call_api();

        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
