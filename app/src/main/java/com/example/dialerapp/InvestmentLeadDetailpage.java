package com.example.dialerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvestmentLeadDetailpage extends Activity {

    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Dialog dialog;
    String lead_id = "", lead_name = "", main_lead_id = "", selecteddate = "", selectedfollowupdate = "", state_id = "",
            frequency_id = "", policy_term = "", ppt = "", policy_term_id = "", ppt_id = "", frequency_str = "", smoker = "", alternate_phonestr = "", hoursstr = "", minutestr = "", ampmstr = "", contact_timestr = "", city_id = "", city_name = "", marital_status = "", smoker_id = "", gender_id = "", primary_feedback_id = "",
            primaryfeedback_name = "",
            secondary_feedback_id = "", secondary_feebackname = "";
    EditText first_name, last_name, email_address, phone_number, sum_assured,
            alternate_phone, age, dob, wishfin_comments, agent_comments, lead_source, followup_date;
    Spinner auto_state, gender_spinner, auto_city, smoker_spinner, marital_spinner, contact_time_hrs, contact_time_mn, contact_time_ampm, primary_feedback_spinner, secondary_feedback_spinner, rider_cover, policy_term_spinner, ppt_spinner;
    TextView loggedin, lead_id_heading;
    int mYear, mMonth, mDay;
    ArrayAdapter<String> state_adapter;
    ArrayAdapter<String> city_adapter;
    ArrayAdapter<String> policy_term_adapter;
    ArrayAdapter<String> ppt_adapter;
    ArrayAdapter<String> rider_adapter;
    ArrayList<Gettersetterforall> getset_state_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_city_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_policy_term_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_ppt_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_rider_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_primaryfeedback_list = new ArrayList<>();
    ArrayList<Gettersetterforall> getset_secondaryfeedback_list = new ArrayList<>();
    ArrayList<String> state_list = new ArrayList<>();
    ArrayList<String> city_list = new ArrayList<>();
    ArrayList<String> policy_term_list = new ArrayList<>();
    ArrayList<String> ppt_list = new ArrayList<>();
    ArrayList<String> rider_list = new ArrayList<>();
    ArrayList<String> primary_feedback_list = new ArrayList<>();
    ArrayList<String> secondary_feedback_list = new ArrayList<>();
    Button calculate, call, save;
    String currenttimestamp = "";
    JSONArray feedbackresponse, policy_term_response;
    String call_id = "";
    CallReceiver broadcastReceiver;
    boolean stateloaded = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.investment_lead_detail_page);

        progressDialog = new ProgressDialog(InvestmentLeadDetailpage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        dialog = new Dialog(InvestmentLeadDetailpage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = "";
                lead_name = "";
                main_lead_id = "";
            } else {
                lead_id = extras.getString("lead_id");
                lead_name = extras.getString("lead_name");
                main_lead_id = extras.getString("main_lead_id");
            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            lead_name = (String) savedInstanceState.getSerializable("lead_name");
            main_lead_id = (String) savedInstanceState.getSerializable("main_lead_id");

        }

        queue = Volley.newRequestQueue(InvestmentLeadDetailpage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(InvestmentLeadDetailpage.this);

        loggedin = findViewById(R.id.loggedin);
        lead_id_heading = findViewById(R.id.lead_id_heading);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email_address = findViewById(R.id.email_address);
        phone_number = findViewById(R.id.phone_number);
        alternate_phone = findViewById(R.id.alternate_phone);
        age = findViewById(R.id.age);
        dob = findViewById(R.id.dob);
        sum_assured = findViewById(R.id.sum_assured);
        rider_cover = findViewById(R.id.rider_cover);
        policy_term_spinner = findViewById(R.id.policy_term_spinner);
        ppt_spinner = findViewById(R.id.ppt_spinner);
        wishfin_comments = findViewById(R.id.wishfin_comments);
        agent_comments = findViewById(R.id.agent_comments);
        lead_source = findViewById(R.id.lead_source);
        followup_date = findViewById(R.id.followup_date);

        auto_state = findViewById(R.id.auto_state);
        auto_city = findViewById(R.id.auto_city);

        gender_spinner = findViewById(R.id.gender_spinner);
        smoker_spinner = findViewById(R.id.smoker_spinner);
        marital_spinner = findViewById(R.id.marital_spinner);
        primary_feedback_spinner = findViewById(R.id.primary_feedback_spinner);
        secondary_feedback_spinner = findViewById(R.id.secondary_feedback_spinner);
        contact_time_hrs = findViewById(R.id.contact_time_hrs);
        contact_time_mn = findViewById(R.id.contact_time_mn);
        contact_time_ampm = findViewById(R.id.contact_time_ampm);

        followup_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicfollowupdate();
            }
        });


        contact_time_hrs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    hoursstr = contact_time_hrs.getSelectedItem().toString() + ":";
                    if (position == 1 || position == 2 || position == 3) {
                        contact_time_ampm.setSelection(0);
                    } else {
                        contact_time_ampm.setSelection(1);
                    }
                } else {
                    hoursstr = "00:";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contact_time_mn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    minutestr = contact_time_mn.getSelectedItem().toString() + " ";
                } else {
                    minutestr = "00 ";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contact_time_ampm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (hoursstr.equalsIgnoreCase("09:") || hoursstr.equalsIgnoreCase("10:") || hoursstr.equalsIgnoreCase("11:")) {
                    contact_time_ampm.setSelection(0);
                } else {
                    contact_time_ampm.setSelection(1);
                }
                ampmstr = contact_time_ampm.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        smoker_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    smoker = "false";
                    smoker_id = "0";
                } else {
                    smoker = "true";
                    smoker_id = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    gender_id = "0";
                } else {
                    gender_id = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        marital_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    marital_status = "0";
                } else {
                    marital_status = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        primary_feedback_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    primary_feedback_id = getset_primaryfeedback_list.get(position - 1).getPrimary_feedback_id();
                    primaryfeedback_name = getset_primaryfeedback_list.get(position - 1).getPrimary_feedback_name();
                } else {
                    primary_feedback_id = "";
                }
                getsecondaryfeedbackdata(primary_feedback_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondary_feedback_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    secondary_feedback_id = getset_secondaryfeedback_list.get(position - 1).getSecondary_feedback_id();
                    secondary_feebackname = getset_secondaryfeedback_list.get(position - 1).getSecondary_feedback_name();
                } else {
                    secondary_feedback_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calculate = findViewById(R.id.calculate);
        call = findViewById(R.id.call);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact_timestr = hoursstr + "" + minutestr + "" + ampmstr;

                if (first_name.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter First name", Toast.LENGTH_SHORT).show();
                } else if (last_name.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Last name", Toast.LENGTH_SHORT).show();

                } else if (email_address.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Email Address", Toast.LENGTH_SHORT).show();

                } else if (age.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select DOB", Toast.LENGTH_SHORT).show();

                } else if (dob.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select DOB", Toast.LENGTH_SHORT).show();

                } else if (sum_assured.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select Premium Amount", Toast.LENGTH_SHORT).show();

                } else if (frequency_id.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select Frequency", Toast.LENGTH_SHORT).show();

                } else if (state_id.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Provide State", Toast.LENGTH_SHORT).show();

                } else if (city_id.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Provide City", Toast.LENGTH_SHORT).show();

                } else if (primary_feedback_id.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select Primary Feedback", Toast.LENGTH_SHORT).show();

                } else if (secondary_feedback_id.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select Secondary Feedback", Toast.LENGTH_SHORT).show();

                } else {
                    if (primaryfeedback_name.equalsIgnoreCase("Not Contactable") ||
                            primaryfeedback_name.equalsIgnoreCase("Not Eligible") ||
                            primaryfeedback_name.equalsIgnoreCase("Not Interested")) {
                        contact_timestr = "";
                        save_data();
                    } else {
                        if (followup_date.getText().toString().equalsIgnoreCase("") || followup_date.getText().toString().equalsIgnoreCase("null")) {
                            Toast.makeText(getApplicationContext(), "Provide Follow Up date", Toast.LENGTH_SHORT).show();

                        } else if (contact_timestr.equalsIgnoreCase("") || contact_timestr.equalsIgnoreCase("null")) {
                            Toast.makeText(getApplicationContext(), "Provide Contact Time", Toast.LENGTH_SHORT).show();

                        } else {
                            try {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1;
                                Date date2;
                                date1 = Calendar.getInstance().getTime();
                                date2 = dates.parse(followup_date.getText().toString());
                                if (date1.compareTo(date2) < 0) {
                                    save_data();

                                } else {

                                    try {
                                        Date mToday = new Date();

                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                        String curTime = sdf.format(mToday);
                                        Date usertime = sdf.parse(curTime);

                                        Date userselectedtime = sdf.parse(contact_timestr);

                                        if (userselectedtime.before(usertime)) {
                                            Toast.makeText(getApplicationContext(), "Contact Time Must Be Greater Than Current Time", Toast.LENGTH_SHORT).show();
                                        } else {
                                            save_data();

                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            } catch (Exception e) {

                            }

                        }
                    }
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long tsLong = System.currentTimeMillis() / 1000;
                currenttimestamp = Long.toString(tsLong);
                getMd5(SessionManager.get_bidder_id(prefs) + "" + SessionManager.get_mobile(prefs) + "" + SessionManager.get_customer_mobile(prefs) + "" + SessionManager.get_product_type(prefs) + "" + currenttimestamp);

                ///////////////////Start Listener Service//////////////////////

                Intent i = new Intent(InvestmentLeadDetailpage.this, PhoneListener.class);
                startService(i);

            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum_assured.getText().toString().trim().equalsIgnoreCase("") || sum_assured.getText().toString().trim().equalsIgnoreCase("0")) {
                    Toast.makeText(getApplicationContext(), "Provide Premium Amount", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(InvestmentLeadDetailpage.this, CalculateInvestmentPremiumPage.class);
                    intent.putExtra("premium", "" + sum_assured.getText().toString());
                    intent.putExtra("policyTerm", "" + policy_term_id);
                    intent.putExtra("premiumPaymentTerm", "" + ppt_id);
                    intent.putExtra("gender", "" + gender_id);
                    intent.putExtra("premiumFrequency", "" + frequency_id);
                    intent.putExtra("age", "" + age.getText().toString().trim());
                    intent.putExtra("lead_id", "" + lead_id);
                    intent.putExtra("lead_name", "" + lead_name);
                    startActivity(intent);
                }

            }
        });

        auto_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                String state_name = String.valueOf(parent.getItemAtPosition(position));
                for (int i = 0; i < getset_state_list.size(); i++) {
                    if (state_name.equalsIgnoreCase(getset_state_list.get(i).getState_name())) {
                        state_id = getset_state_list.get(i).getState_id();
                        if (stateloaded) {
                            get_city_list("0");
                        }
                    }
                }
                stateloaded = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        auto_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String cityname = String.valueOf(parent.getItemAtPosition(position));
                for (int i = 0; i < getset_city_list.size(); i++) {
                    if (cityname.equalsIgnoreCase(getset_city_list.get(i).getCity_name())) {
                        city_id = getset_city_list.get(i).getCity_id();
                        city_name = cityname;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        policy_term_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                policy_term_id = getset_policy_term_list.get(position).getRider_id();
                policy_term = getset_policy_term_list.get(position).getRider_value();

                getppt(policy_term_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ppt_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ppt_id = getset_ppt_list.get(position).getSecondary_feedback_id();
                ppt = getset_ppt_list.get(position).getSecondary_feedback_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rider_cover.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frequency_str = String.valueOf(parent.getItemAtPosition(position));

                for (int i = 0; i < getset_rider_list.size(); i++) {
                    if (frequency_str.equalsIgnoreCase(getset_rider_list.get(i).getRider_value())) {
                        frequency_str = getset_rider_list.get(i).getRider_value();
                        frequency_id = getset_rider_list.get(i).getRider_id();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dob.setOnClickListener(v -> DatePicdob());

        loggedin.setText("Log in as " + SessionManager.get_username(prefs));
        lead_id_heading.setText("Lead Id-" + lead_id + "(" + lead_name + ")");

        loggedin.setOnClickListener(new View.OnClickListener() {
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
                        Intent intent = new Intent(InvestmentLeadDetailpage.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        get_cibil_credit_factors();
    }

    private void getsecondaryfeedbackdata(String primary_feedback_id) {

        getset_secondaryfeedback_list = new ArrayList<>();
        getset_secondaryfeedback_list.clear();

        secondary_feedback_list = new ArrayList<>();
        secondary_feedback_list.clear();
        secondary_feedback_list.add("Select Secondary Feedback");
        try {
            JSONArray jsonArray3 = feedbackresponse;

            for (int i = 0; i < jsonArray3.length(); i++) {

                JSONObject objectnew2 = jsonArray3.getJSONObject(i);

                if (primary_feedback_id.equalsIgnoreCase("" + objectnew2.getString("feedback_id"))) {

                    JSONArray jsonArray2 = (objectnew2.getJSONArray("secondaryfeedback"));

                    for (int j = 0; j < jsonArray2.length(); j++) {
                        JSONObject objectnew3 = jsonArray2.getJSONObject(j);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setSecondary_feedback_id(objectnew3.getString("feedbackid"));
                        pack.setSecondary_feedback_name(objectnew3.getString("secondryfeedback"));
                        getset_secondaryfeedback_list.add(pack);
                        secondary_feedback_list.add(objectnew3.getString("secondryfeedback"));
                    }
                }
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item,
                            secondary_feedback_list); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            secondary_feedback_spinner.setAdapter(spinnerArrayAdapter);
            secondary_feedback_id = "";

        } catch (Exception e) {

        }
    }

    private void getppt(String primary_feedback_id) {

        ppt_list = new ArrayList<>();
        ppt_list.clear();

        try {
            JSONArray jsonArray3 = policy_term_response;

            for (int i = 0; i < jsonArray3.length(); i++) {

                JSONObject objectnew2 = jsonArray3.getJSONObject(i);

                if (primary_feedback_id.equalsIgnoreCase("" + objectnew2.getString("id"))) {

                    JSONObject jsonObject = objectnew2.getJSONObject("value");
                    JSONArray jsonArray2 = (jsonObject.getJSONArray("ppt"));
                    for (int j = 0; j < jsonArray2.length(); j++) {
                        JSONObject objectnew3 = jsonArray2.getJSONObject(j);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setSecondary_feedback_id(objectnew3.getString("id"));
                        pack.setSecondary_feedback_name(objectnew3.getString("value"));
                        getset_ppt_list.add(pack);
                        ppt_list.add(objectnew3.getString("value"));
                    }
                }
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item,
                            ppt_list); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            ppt_spinner.setAdapter(spinnerArrayAdapter);
            ppt_id = getset_ppt_list.get(0).getSecondary_feedback_id();

        } catch (Exception e) {

        }
    }

    public void get_cibil_credit_factors() {

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.BASE_URL + "app_insurance/api/lead-detail/" + lead_id;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {


                    try {
                        getset_state_list = new ArrayList<>();
                        getset_state_list.clear();

                        state_list = new ArrayList<>();
                        state_list.clear();

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            JSONObject jsonObject1 = (jsonObject.getJSONObject("result"));

                            first_name.setText(jsonObject1.getString("first_name"));
                            last_name.setText(jsonObject1.getString("last_name"));
                            email_address.setText(jsonObject1.getString("email"));
                            String lastnumber = jsonObject1.getString("mobile_no").substring(jsonObject1.getString("mobile_no").length() - 4);
                            phone_number.setText("XXXXXX" + lastnumber);

                            if (jsonObject1.getString("alt_mobile").equalsIgnoreCase("null")
                                    || jsonObject1.getString("alt_mobile").equalsIgnoreCase("")
                                    || jsonObject1.getString("alt_mobile").equalsIgnoreCase("0")) {
                                alternate_phone.setText("");
                                alternate_phonestr = "";
                            } else {

                                alternate_phone.setClickable(false);
                                alternate_phone.setCursorVisible(false);
                                alternate_phone.setFocusable(false);
                                alternate_phone.setFocusableInTouchMode(false);
                                alternate_phonestr = jsonObject1.getString("alt_mobile");
                                String lastaltnumber = jsonObject1.getString("alt_mobile").substring(jsonObject1.getString("alt_mobile").length() - 4);
                                alternate_phone.setText("XXXXXX" + lastaltnumber);
                            }

                            dob.setText(jsonObject1.getString("dateOfBirth"));
                            if (jsonObject1.getString("premium_amount").equalsIgnoreCase("null")
                                    || jsonObject1.getString("premium_amount").equalsIgnoreCase("")) {
                                sum_assured.setText("100000");
                            } else {
                                sum_assured.setText(jsonObject1.getString("premium_amount"));
                            }
                            SessionManager.save_customer_mobile(prefs, jsonObject1.getString("mobile_no"));
                            try {
                                if (!jsonObject1.getString("user_comment").equalsIgnoreCase("{}")) {
                                    JSONObject jsonObject11 = (jsonObject1.getJSONObject("user_comment"));
                                    if (!jsonObject11.toString().equals("{}")) {
                                        wishfin_comments.setText(jsonObject11.toString());
                                    }
                                }
                            } catch (Exception e) {
                                JSONObject jsonObject11 = (jsonObject1.getJSONObject("user_comment"));
                                if (!jsonObject11.toString().equals("{}")) {
                                    wishfin_comments.setText(jsonObject11.toString());
                                }
                            }


                            if (jsonObject1.getString("Add_Comment").equalsIgnoreCase("null")
                                    || jsonObject1.getString("Add_Comment").equalsIgnoreCase("")) {
                                agent_comments.setText("");
                            } else {
                                agent_comments.setText(jsonObject1.getString("Add_Comment"));
                            }
                            lead_source.setText(jsonObject1.getString("lead_source"));//for term and for other it will be lead_sources
//                            if (jsonObject1.getString("followup_date").equalsIgnoreCase("null")
//                                    || jsonObject1.getString("followup_date").equalsIgnoreCase("0000-00-00")) {
//                                followup_date.setText("");
//                            } else {
//                                followup_date.setText(jsonObject1.getString("followup_date"));
//                            }

//                            if (jsonObject1.getString("Contact_Time").equalsIgnoreCase("null")
//                                    || jsonObject1.getString("Contact_Time").equalsIgnoreCase("00:00")) {
//                                contact_time.setText("");
//                            } else {
//                                contact_time.setText(jsonObject1.getString("Contact_Time"));
//                            }

                            if (jsonObject1.getString("Gender").equalsIgnoreCase("0")) {
                                gender_spinner.setSelection(0);
                            } else {
                                gender_spinner.setSelection(1);

                            }

                            if (jsonObject1.getString("smoker").equalsIgnoreCase("0")) {
                                smoker_spinner.setSelection(0);
                                smoker = "false";
                            } else {
                                smoker_spinner.setSelection(1);
                                smoker = "true";

                            }

                            if (jsonObject1.getString("Marital_Status").equalsIgnoreCase("0")) {
                                marital_spinner.setSelection(0);
                            } else {
                                marital_spinner.setSelection(1);

                            }

                            age.setText(agecalculator(jsonObject1.getString("dateOfBirth")));

                            JSONArray jsonArray = (jsonObject1.getJSONArray("frequency"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectnew2 = jsonArray.getJSONObject(i);
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setRider_id(objectnew2.getString("id"));
                                pack.setRider_value(objectnew2.getString("value"));
                                getset_rider_list.add(pack);
                                rider_list.add(objectnew2.getString("value"));
                            }

                            rider_adapter = new ArrayAdapter<>
                                    (this, android.R.layout.simple_spinner_item, rider_list);
                            rider_adapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            rider_cover.setAdapter(rider_adapter);
                            frequency_id = getset_rider_list.get(0).getRider_id();

                            JSONArray jsonArray2 = (jsonObject1.getJSONArray("allstates"));

                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject objectnew2 = jsonArray2.getJSONObject(i);
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setState_id(objectnew2.getString("id"));
                                pack.setState_name(objectnew2.getString("states"));
                                getset_state_list.add(pack);
                                state_list.add(objectnew2.getString("states"));
                            }

                            state_adapter = new ArrayAdapter<>
                                    (this, android.R.layout.simple_spinner_item, state_list);
                            state_adapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            auto_state.setAdapter(state_adapter);
                            state_id = getset_state_list.get(0).getState_id();

                            if (jsonObject1.getString("states").equalsIgnoreCase("")
                                    || jsonObject1.getString("states").equalsIgnoreCase("0")) {
                                for (int i = 0; i < getset_state_list.size(); i++) {
                                    if (getset_state_list.get(i).getState_name().equalsIgnoreCase("Delhi")) {
                                        auto_state.setSelection(i);
                                        state_id = getset_state_list.get(i).getState_id();
                                    }
                                }
                            } else {
                                for (int i = 0; i < getset_state_list.size(); i++) {
                                    if (jsonObject1.getString("states").equalsIgnoreCase(getset_state_list.get(i).getState_id())) {
                                        auto_state.setSelection(i);
                                        state_id = getset_state_list.get(i).getState_id();
                                    }
                                }
                            }

                            get_city_list(jsonObject1.getString("City"));

                            getset_primaryfeedback_list = new ArrayList<>();
                            getset_primaryfeedback_list.clear();

                            primary_feedback_list = new ArrayList<>();
                            primary_feedback_list.clear();

                            JSONArray jsonArray3 = (jsonObject1.getJSONArray("primary_feedback"));

                            feedbackresponse = (jsonObject1.getJSONArray("primary_feedback"));
                            primary_feedback_list.add("Select Primary Feedback");
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                JSONObject objectnew2 = jsonArray3.getJSONObject(i);
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setPrimary_feedback_id(objectnew2.getString("feedback_id"));
                                pack.setPrimary_feedback_name(objectnew2.getString("feedbackname"));
                                getset_primaryfeedback_list.add(pack);
                                primary_feedback_list.add(objectnew2.getString("feedbackname"));
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (this, android.R.layout.simple_spinner_item,
                                            primary_feedback_list); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            primary_feedback_spinner.setAdapter(spinnerArrayAdapter);
                            primary_feedback_id = "";
                            getsecondaryfeedbackdata(primary_feedback_id);

                            /////////////////////////////////////


                            getset_policy_term_list = new ArrayList<>();
                            getset_policy_term_list.clear();

                            getset_ppt_list = new ArrayList<>();
                            getset_ppt_list.clear();

                            JSONArray jsonArray1 = (jsonObject1.getJSONArray("policy_term"));
                            policy_term_response = (jsonObject1.getJSONArray("policy_term"));
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject objectnew2 = jsonArray1.getJSONObject(i);
                                JSONObject jsonObject11 = objectnew2.getJSONObject("value");
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setRider_id(objectnew2.getString("id"));
                                pack.setRider_value(jsonObject11.getString("name"));
                                getset_policy_term_list.add(pack);
                                policy_term_list.add(jsonObject11.getString("name"));
                            }

                            policy_term_adapter = new ArrayAdapter<>
                                    (this, android.R.layout.simple_spinner_item, policy_term_list);
                            policy_term_adapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            policy_term_spinner.setAdapter(policy_term_adapter);
                            policy_term_id = getset_policy_term_list.get(0).getRider_id();
                            policy_term = getset_policy_term_list.get(0).getRider_value();

                            getppt(policy_term_id);


                            progressDialog.dismiss();

                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(InvestmentLeadDetailpage.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(InvestmentLeadDetailpage.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                },
                error -> {
                    // TODO Auto-generated method stub
                    progressDialog.dismiss();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", bearer);

                return params;
            }
        };
        queue.add(getRequest);

    }

    public void get_city_list(String strcity_id) {

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.BASE_URL + "app_insurance/api/get-cities/" + state_id;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {


                    try {
                        getset_city_list = new ArrayList<>();
                        getset_city_list.clear();

                        city_list = new ArrayList<>();
                        city_list.clear();

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectnew2 = jsonArray.getJSONObject(i);
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setCity_id(objectnew2.getString("id"));
                                pack.setCity_name(objectnew2.getString("city"));
                                getset_city_list.add(pack);
                                city_list.add(objectnew2.getString("city"));
                            }
                            city_adapter = new ArrayAdapter<>
                                    (this, android.R.layout.simple_spinner_item, city_list);
                            city_adapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            auto_city.setAdapter(city_adapter);
                            city_id = getset_city_list.get(0).getCity_id();

                            for (int i = 0; i < getset_city_list.size(); i++) {
                                if (strcity_id.equalsIgnoreCase(getset_city_list.get(i).getCity_id())) {
                                    auto_city.setSelection(i);
                                    city_id = getset_city_list.get(i).getCity_id();
                                }
                            }


                            progressDialog.dismiss();

                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(InvestmentLeadDetailpage.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(InvestmentLeadDetailpage.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                },
                error -> {
                    // TODO Auto-generated method stub
                    progressDialog.dismiss();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", bearer);

                return params;
            }
        };
        queue.add(getRequest);

    }

    private String agecalculator(String dateOfBirth) {

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(dateOfBirth);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int month = (cal.get(Calendar.MONTH) + 1);
            int day = (cal.get(Calendar.DATE));
            int year = (cal.get(Calendar.YEAR));
            dob.set(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        int ageInt = age;

        return Integer.toString(ageInt);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(InvestmentLeadDetailpage.this, LeadListingPage.class);
        intent.putExtra("lead_name", lead_name);
        intent.putExtra("lead_id", main_lead_id);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void DatePicdob() {

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mcurrentDate.add(Calendar.DATE, -1);

        DatePickerDialog mDatePicker = new DatePickerDialog(InvestmentLeadDetailpage.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            // TODO Auto-generated method stub

            selectedmonth++;
            String month = "";
            if (selectedmonth > 0 && selectedmonth < 10) {
                month = "0" + selectedmonth;
            } else {
                month = "" + selectedmonth;
            }

            String days = "";
            if (selectedday > 0 && selectedday < 10) {
                days = "0" + selectedday;
            } else {
                days = "" + selectedday;
            }

            selecteddate = selectedyear + "-" + month + "-" + days;
            dob.setText(selecteddate);
            age.setText(agecalculator(selecteddate));

        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.getDatePicker().setMaxDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 18)));
        mDatePicker.getDatePicker().setMinDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 65)));
        mDatePicker.show();

    }

    private void DatePicfollowupdate() {

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mcurrentDate.add(Calendar.DATE, -1);

        DatePickerDialog mDatePicker = new DatePickerDialog(InvestmentLeadDetailpage.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            // TODO Auto-generated method stub

            selectedmonth++;
            String month = "";
            if (selectedmonth > 0 && selectedmonth < 10) {
                month = "0" + selectedmonth;
            } else {
                month = "" + selectedmonth;
            }

            String days = "";
            if (selectedday > 0 && selectedday < 10) {
                days = "0" + selectedday;
            } else {
                days = "" + selectedday;
            }

            selectedfollowupdate = selectedyear + "-" + month + "-" + days;
            followup_date.setText(selectedfollowupdate);

        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        mDatePicker.show();

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
            Toast.makeText(InvestmentLeadDetailpage.this, "Click Refresh Button First", Toast.LENGTH_LONG).show();
        }
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

            SessionManager.save_call_id(prefs, call_id);

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

    private void save_data() {

        final JSONObject json = new JSONObject();

        try {
            json.put("lead_id", "" + lead_id);
            json.put("first_name", "" + first_name.getText().toString().trim());
            json.put("last_name", "" + last_name.getText().toString().trim());
            json.put("email", "" + email_address.getText().toString().trim());

            if (alternate_phonestr.equalsIgnoreCase("")) {
                json.put("altphone", "" + alternate_phone.getText().toString().trim());

            } else {
                json.put("altphone", "" + alternate_phonestr);
            }
            json.put("age", "" + age.getText().toString().trim());
            json.put("dob", "" + dob.getText().toString().trim());
            json.put("marital_status", "" + marital_status);
            json.put("gender", "" + gender_id);
            json.put("smoker", "" + smoker_id);
            json.put("premium_amount", "" + sum_assured.getText().toString().trim());
            json.put("cover", "" + sum_assured.getText().toString().trim());
            json.put("rider", "0");
            json.put("ppt", "10");
            json.put("duration", "10");
            json.put("states", "" + state_id);
            json.put("city", "" + city_id);
            json.put("contact_time", "" + contact_timestr);
            json.put("follow_up_date", "" + followup_date.getText().toString().trim());
            json.put("pri_feedback", "" + primary_feedback_id);
            json.put("sec_feedback", "" + secondary_feedback_id);
            json.put("agent_comment", "" + agent_comments.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/leaddetailsave", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response.toString());

                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                Intent intent = new Intent(InvestmentLeadDetailpage.this, LeadListingPage.class);
                                intent.putExtra("lead_name", lead_name);
                                intent.putExtra("lead_id", main_lead_id);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();


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
                Map<String, String> params = new HashMap<String, String>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", bearer);

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);

    }


}
