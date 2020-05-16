package com.example.dialerapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends Activity {

    EditText mobilenumber, username, otpone, otptwo, otpthree, otpfour;
    TextView signupone, signupthree, resentotp, lastmobiletext;
    ProgressDialog progressDialog;
    LinearLayout linearone, linearthree;
    RequestQueue queue;
    ImageView backbutton;
    SharedPreferences prefs;
    String otpstring = "", lms_type = "";
    Spinner lmstype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginpage);

        progressDialog = new ProgressDialog(LoginPage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        queue = Volley.newRequestQueue(LoginPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(LoginPage.this);

        backbutton = findViewById(R.id.backbutton);
        linearone = findViewById(R.id.linearone);
        linearthree = findViewById(R.id.linearthree);
        signupone = findViewById(R.id.signupone);
        signupthree = findViewById(R.id.signupthree);
        mobilenumber = findViewById(R.id.mobilenumber);
        username = findViewById(R.id.username);
        otpone = findViewById(R.id.otpone);
        otptwo = findViewById(R.id.otptwo);
        otpthree = findViewById(R.id.otpthree);
        otpfour = findViewById(R.id.otpfour);
        resentotp = findViewById(R.id.resentotp);
        lastmobiletext = findViewById(R.id.lastmobiletext);
        lmstype = findViewById(R.id.usertype);
        otpone.addTextChangedListener(new LoginPage.MyTextWatcher(otpone));
        otptwo.addTextChangedListener(new LoginPage.MyTextWatcher(otptwo));
        otpthree.addTextChangedListener(new LoginPage.MyTextWatcher(otpthree));
        otpfour.addTextChangedListener(new LoginPage.MyTextWatcher(otpfour));

        lmstype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    lms_type = lmstype.getSelectedItem().toString();
                } else {
                    lms_type = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signupone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFormone();
            }
        });

        signupthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otpone.getText().toString().equalsIgnoreCase("") || otptwo.getText().toString().equalsIgnoreCase("")
                        || otpthree.getText().toString().equalsIgnoreCase("") || otpfour.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LoginPage.this, "Enter OTP", Toast.LENGTH_LONG).show();

                } else {
                    verify_otp();
                }

            }
        });

        resentotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_otp();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearone.setVisibility(View.VISIBLE);
                linearthree.setVisibility(View.GONE);
                otpone.setText("");
                otptwo.setText("");
                otpthree.setText("");
                otpfour.setText("");
            }
        });
    }

    private void submitFormone() {

        if (lms_type.equalsIgnoreCase("")) {

            Toast.makeText(LoginPage.this, "Please Select LMS Type", Toast.LENGTH_LONG).show();
            return;
        }
        if (!validateName()) {
            return;
        }

        if (!validateNumber()) {
            return;
        }

        if (isThereInternetConnection()) {
            get_otp();
        } else {
            Toast.makeText(LoginPage.this, "Please check your internet", Toast.LENGTH_LONG).show();

        }

    }

    private boolean validateNumber() {
        if (!mobilenumber.getText().toString().trim().matches("[5-9][0-9]{9}") || mobilenumber.getText().length() != 10) {
            Toast.makeText(LoginPage.this, "Enter 10 Digits Mobile Number", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean validateName() {
        if (username.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(LoginPage.this, "Enter User Name", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    private void get_otp() {

        final JSONObject json = new JSONObject();

        try {
            json.put("user_name", "" + username.getText().toString());
            json.put("mobile", "" + mobilenumber.getText().toString());
            json.put("type", "ANDROID");
            json.put("lms_type", lms_type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/get_otp", json,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            progressDialog.dismiss();

                            String lastnumber = mobilenumber.getText().toString().substring(mobilenumber.getText().toString().length() - 4);

                            linearone.setVisibility(View.GONE);
                            linearthree.setVisibility(View.VISIBLE);
                            timer();
                            lastmobiletext.setText("We have sent an OTP on ******" + lastnumber);
                            Toast.makeText(LoginPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
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

    private void verify_otp() {

        final JSONObject json = new JSONObject();

        try {
            otpstring = otpone.getText().toString() + "" + otptwo.getText().toString() + "" + otpthree.getText().toString() + "" + otpfour.getText().toString();

            json.put("user_name", "" + username.getText().toString().trim());
            json.put("mobile", "" + mobilenumber.getText().toString());
            json.put("lms_type", lms_type);
            json.put("otp", otpstring);
            json.put("type", "ANDROID");
            json.put("device_id", SessionManager.get_device_token(prefs));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/verify_otp", json,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            progressDialog.dismiss();
                            SessionManager.save_access_token(prefs, jsonObject.getString("token"));
                            SessionManager.save_lms_type(prefs, jsonObject.getString("ptype"));
                            SessionManager.save_login(prefs, "True");
                            SessionManager.save_username(prefs, "" + username.getText().toString().trim());
                            SessionManager.save_mobile(prefs, "" + mobilenumber.getText().toString().trim());
                            SessionManager.save_otp(prefs, otpstring);
                            Intent intent = new Intent(LoginPage.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginPage.this, "Welcome " + SessionManager.get_username(prefs), Toast.LENGTH_LONG).show();


                        } else {

                            Toast.makeText(LoginPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();

                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
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

    private void timer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resentotp.setClickable(false);
                String resend_otp_text = "Resend OTP in: " + millisUntilFinished / 1000 + " sec";
                resentotp.setText(resend_otp_text);
                backbutton.setVisibility(View.GONE);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resentotp.setText("Resend OTP");
                resentotp.setClickable(true);
                backbutton.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.otpone:
                    if (otpone.getText().toString().equalsIgnoreCase("")) {
                        otpone.requestFocus();
                    } else {
                        otptwo.requestFocus();
                    }
                    break;
                case R.id.otptwo:
                    if (otptwo.getText().toString().equalsIgnoreCase("")) {
                        otpone.requestFocus();
                    } else {
                        otpthree.requestFocus();
                    }
                    break;
                case R.id.otpthree:
                    if (otpthree.getText().toString().equalsIgnoreCase("")) {
                        otptwo.requestFocus();
                    } else {
                        otpfour.requestFocus();
                    }
                    break;
                case R.id.otpfour:
                    if (otpfour.getText().toString().equalsIgnoreCase("")) {
                        otpthree.requestFocus();
                    }
                    break;

            }
        }
    }
}
