package com.example.dialerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Dashboard extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView credit_factor_list;
    ArrayList<Gettersetterforall> list1 = new ArrayList<>();
    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Share_Adapter radio_question_list_adapter;
    TextView loggedin, heading;
    private SwipeRefreshLayout swipeView;
    Dialog dialog;
    boolean doubleBackToExitPressedOnce = false;
    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardpage);

        progressDialog = new ProgressDialog(Dashboard.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        dialog = new Dialog(Dashboard.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        queue = Volley.newRequestQueue(Dashboard.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);

        credit_factor_list = findViewById(R.id.list);
        heading = findViewById(R.id.heading);
        loggedin = findViewById(R.id.loggedin);
        search_bar = findViewById(R.id.search_bar);
        search_bar.setVisibility(View.GONE);

        swipeView = findViewById(R.id.swipe_view);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeView.setDistanceToTriggerSync(50);
        swipeView.setSize(SwipeRefreshLayout.DEFAULT);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(Dashboard.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(Dashboard.this) {
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

        heading.setText("Dashboard");
        loggedin.setText(SessionManager.get_username(prefs));

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
                        Intent intent = new Intent(Dashboard.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        get_cibil_credit_factors();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

    }

    void filter(String text) {
        ArrayList<Gettersetterforall> temp = new ArrayList<>();
        for (Gettersetterforall d : list1) {
            if (d.getLeadtype().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            } else if (d.getLeadid().contains(text)) {
                temp.add(d);
            }
        }
        if (temp.size() != 0) {
            radio_question_list_adapter = new Share_Adapter(Dashboard.this, temp);
            credit_factor_list.setAdapter(radio_question_list_adapter);
        } else {
            radio_question_list_adapter = new Share_Adapter(Dashboard.this, list1);
            credit_factor_list.setAdapter(radio_question_list_adapter);
        }
    }


    public void get_cibil_credit_factors() {

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.BASE_URL + "app_insurance/api/buckets";
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
                                pack.setLeadtype(objectnew2.getString("display_name"));
                                pack.setLeadcount(objectnew2.getString("count"));
                                pack.setLeadid(objectnew2.getString("searching_name"));
                                list1.add(pack);
                            }
                            progressDialog.dismiss();
                            radio_question_list_adapter = new Share_Adapter(Dashboard.this, list1);
                            credit_factor_list.setAdapter(radio_question_list_adapter);

                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(Dashboard.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(Dashboard.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


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
            TextView tv1, tv2;
            LinearLayout relit;
            ImageView edit_image;

            MyViewHolder(View view) {
                super(view);
                tv1 = view.findViewById(R.id.lead_name);
                tv2 = view.findViewById(R.id.lead_count);
                relit = view.findViewById(R.id.linear);
                edit_image = view.findViewById(R.id.edit_image);

            }
        }

        @Override
        public Share_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_dashboard, parent, false);

            return new Share_Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(Share_Adapter.MyViewHolder holder, final int position) {

            holder.tv1.setText(list_car.get(position).getLeadtype());
            holder.tv2.setText(list_car.get(position).getLeadcount());

            if (list_car.get(position).getLeadtype().equalsIgnoreCase("NEW")) {
                holder.edit_image.setBackgroundResource(R.drawable.ic_new);
            } else if (list_car.get(position).getLeadtype().equalsIgnoreCase("CALL BACK")) {
                holder.edit_image.setBackgroundResource(R.drawable.ic_call_back);
            } else if (list_car.get(position).getLeadtype().equalsIgnoreCase("NOT-CONTACTABLE")) {
                holder.edit_image.setBackgroundResource(R.drawable.ic_not_call);
            } else if (list_car.get(position).getLeadtype().equalsIgnoreCase("FOLLOW UP")) {
                holder.edit_image.setBackgroundResource(R.drawable.ic_follow_up);
            } else {
                holder.edit_image.setBackgroundResource(R.drawable.help);
            }


            holder.relit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!list_car.get(position).getLeadcount().equalsIgnoreCase("0")) {
                        Intent intent = new Intent(Dashboard.this, LeadListingPage.class);
                        intent.putExtra("lead_name", list_car.get(position).getLeadtype());
                        intent.putExtra("lead_id", list_car.get(position).getLeadid());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Dashboard.this, "No Lead Available", Toast.LENGTH_LONG).show();

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
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
