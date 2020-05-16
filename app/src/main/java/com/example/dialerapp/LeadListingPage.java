package com.example.dialerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeadListingPage extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView credit_factor_list;
    ArrayList<Gettersetterforall> list1 = new ArrayList<>();
    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Share_Adapter radio_question_list_adapter;
    TextView loggedin, heading;
    Dialog dialog;
    private SwipeRefreshLayout swipeView;
    String lead_type = "", lead_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardpage);

        progressDialog = new ProgressDialog(LeadListingPage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        dialog = new Dialog(LeadListingPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_type = "";
                lead_name = "";
            } else {
                lead_type = extras.getString("lead_id");
                lead_name = extras.getString("lead_name");
            }
        } else {
            lead_type = (String) savedInstanceState.getSerializable("lead_id");
            lead_name = (String) savedInstanceState.getSerializable("lead_name");

        }

        queue = Volley.newRequestQueue(LeadListingPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(LeadListingPage.this);

        credit_factor_list = findViewById(R.id.list);
        loggedin = findViewById(R.id.loggedin);
        heading = findViewById(R.id.heading);

        swipeView = findViewById(R.id.swipe_view);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeView.setDistanceToTriggerSync(50);
        swipeView.setSize(SwipeRefreshLayout.DEFAULT);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(LeadListingPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(LeadListingPage.this) {
                    private static final float SPEED = 4000f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        Drawable mDivider = ContextCompat.getDrawable(this, R.drawable.divider);
        DividerItemDecoration hItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        if (mDivider != null) {
            hItemDecoration.setDrawable(mDivider);
        }

        credit_factor_list.setLayoutManager(layoutManager1);

        heading.setText(lead_name + " Leads");
        loggedin.setText("Log in as " + SessionManager.get_username(prefs));

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
                        Intent intent = new Intent(LeadListingPage.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        get_cibil_credit_factors();


    }

    public void get_cibil_credit_factors() {

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.BASE_URL + "app_insurance/api/leadsearch?lead_type=" + lead_type;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        list1 = new ArrayList<>();
                        list1.clear();

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objectnew2 = jsonArray.getJSONObject(i);
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setLeadid(objectnew2.getString("id"));
                                try {
                                    pack.setLead_mobile(objectnew2.getString("mobile"));
                                } catch (Exception e) {
                                    pack.setLead_mobile(objectnew2.getString("mobile_no"));

                                }
                                pack.setLead_first_name(objectnew2.getString("first_name"));
                                pack.setLead_lastname(objectnew2.getString("last_name"));
                                pack.setLead_email(objectnew2.getString("email"));
                                pack.setLead_source(objectnew2.getString("lead_source"));
                                pack.setAllocated_date(objectnew2.getString("allocated_date"));
                                list1.add(pack);
                            }
                            progressDialog.dismiss();
                            radio_question_list_adapter = new Share_Adapter(LeadListingPage.this, list1);
                            credit_factor_list.setAdapter(radio_question_list_adapter);

                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(LeadListingPage.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LeadListingPage.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


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

    public class Share_Adapter extends RecyclerView.Adapter<Share_Adapter.MyViewHolder> {

        private ArrayList<Gettersetterforall> list_car;
        Activity context;

        Share_Adapter(Activity mcontext, ArrayList<Gettersetterforall> list) {
            this.list_car = list;
            this.context = mcontext;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv1, tv2, tv3, tv4, tv5;
            LinearLayout relit;

            MyViewHolder(View view) {
                super(view);
                tv1 = view.findViewById(R.id.leadname);
                tv2 = view.findViewById(R.id.leademail);
                tv3 = view.findViewById(R.id.leadmobile);
                tv4 = view.findViewById(R.id.leadsource);
                tv5 = view.findViewById(R.id.leadallocateddate);
                relit = view.findViewById(R.id.linear);

            }
        }

        @Override
        public Share_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_leadlistingpage, parent, false);

            return new Share_Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(Share_Adapter.MyViewHolder holder, final int position) {

            String lead_fullname = list_car.get(position).getLead_first_name() + " " + list_car.get(position).getLead_lastname();
            holder.tv1.setText(lead_fullname);

            String lastnumber = list_car.get(position).getLead_mobile().substring(list_car.get(position).getLead_mobile().length() - 4);

            holder.tv2.setText(list_car.get(position).getLead_email());
            holder.tv3.setText("XXXXXX" + lastnumber);
            holder.tv4.setText("Source: " + list_car.get(position).getLead_source());
            holder.tv5.setText("Allocated Date: " + list_car.get(position).getAllocated_date());

            holder.relit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (SessionManager.get_lms_type(prefs).equalsIgnoreCase("TI")) {
                        SessionManager.save_customer_mobile(prefs, "");
                        Intent intent = new Intent(LeadListingPage.this, TermLeadDetailpage.class);
                        intent.putExtra("lead_id", list_car.get(position).getLeadid());
                        intent.putExtra("lead_name", lead_name);
                        intent.putExtra("main_lead_id", lead_type);
                        startActivity(intent);
                        finish();
                    } else if (SessionManager.get_lms_type(prefs).equalsIgnoreCase("HI")) {
                        SessionManager.save_customer_mobile(prefs, "");
                        Intent intent = new Intent(LeadListingPage.this, HealthLeadDetailpage.class);
                        intent.putExtra("lead_id", list_car.get(position).getLeadid());
                        intent.putExtra("lead_name", lead_name);
                        intent.putExtra("main_lead_id", lead_type);
                        startActivity(intent);
                        finish();
                    } else {
                        SessionManager.save_customer_mobile(prefs, "");
                        Intent intent = new Intent(LeadListingPage.this, InvestmentLeadDetailpage.class);
                        intent.putExtra("lead_id", list_car.get(position).getLeadid());
                        intent.putExtra("lead_name", lead_name);
                        intent.putExtra("main_lead_id", lead_type);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

    }

    @Override
    public void onRefresh() {
        get_cibil_credit_factors();
        swipeView.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LeadListingPage.this, Dashboard.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
