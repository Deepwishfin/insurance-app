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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalculateInvestmentPremiumPage extends Activity {

    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Dialog dialog;
    String age = "", premium = "", policyTerm = "", premiumPaymentTerm = "", gender, premiumFrequency = "", lead_id = "", lead_name = "";
    TextView total_premium, loggedin, lead_id_heading, coverupto, maxlimit, sumassured, select_single, select_yearly, select_halfyearly, select_monthly, select_quaterly;
    ViewPager viewPager;
    SpringDotsIndicator springDotsIndicator;
    ArrayList<Gettersetterforall> cardlist = new ArrayList<>();
    ArrayList<String> companynames = new ArrayList<>();
    RecyclerView policy_premium_listing, policy_rider_listing;
    Share_Adapter radio_question_list_adapter;
    Share_Adapter2 radio_question_rider_adapter;
    ArrayList<Gettersetterforall> policypremiumlist = new ArrayList<>();
    ArrayList<Gettersetterforall> filter_policypremiumlist = new ArrayList<>();
    ArrayList<Gettersetterforall> filter_policyriderlist = new ArrayList<>();
    ArrayList<Gettersetterforall> policyriderlist = new ArrayList<>();
    int row_index = 0;
    String frequency = "Yearly";
    float totalpremium = 0, totalriderpremium = 0;
    boolean pagechanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.investmentpremiumdetailpage);

        progressDialog = new ProgressDialog(CalculateInvestmentPremiumPage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        dialog = new Dialog(CalculateInvestmentPremiumPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        queue = Volley.newRequestQueue(CalculateInvestmentPremiumPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(CalculateInvestmentPremiumPage.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = "";
                lead_name = "";
                age = "";
                premium = "";
                policyTerm = "";
                premiumPaymentTerm = "";
                premiumFrequency = "";
                gender = "";
            } else {
                lead_id = extras.getString("lead_id");
                lead_name = extras.getString("lead_name");
                age = extras.getString("age");
                premium = extras.getString("premium");
                policyTerm = extras.getString("policyTerm");
                premiumPaymentTerm = extras.getString("premiumPaymentTerm");
                premiumFrequency = extras.getString("premiumFrequency");
                gender = extras.getString("gender");

            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            lead_name = (String) savedInstanceState.getSerializable("lead_name");
            age = (String) savedInstanceState.getSerializable("age");
            premium = (String) savedInstanceState.getSerializable("premium");
            policyTerm = (String) savedInstanceState.getSerializable("policyTerm");
            premiumPaymentTerm = (String) savedInstanceState.getSerializable("premiumPaymentTerm");
            premiumFrequency = (String) savedInstanceState.getSerializable("premiumFrequency");
            gender = (String) savedInstanceState.getSerializable("gender");

        }

        loggedin = findViewById(R.id.loggedin);
        lead_id_heading = findViewById(R.id.lead_id_heading);
        springDotsIndicator = findViewById(R.id.spring_dots_indicator);
        viewPager = findViewById(R.id.view_pager);
        policy_premium_listing = findViewById(R.id.policy_premium_listing);
        policy_rider_listing = findViewById(R.id.policy_rider_listing);
        sumassured = findViewById(R.id.sumassured);
        coverupto = findViewById(R.id.coverupto);
        maxlimit = findViewById(R.id.maxlimit);
        total_premium = findViewById(R.id.total_premium);

        select_single = findViewById(R.id.select_single);
        select_yearly = findViewById(R.id.select_yearly);
        select_halfyearly = findViewById(R.id.select_halfyearly);
        select_monthly = findViewById(R.id.select_monthly);
        select_quaterly = findViewById(R.id.select_quaterly);


        select_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frequency = "Single";
//                row_index = 0;
                totalriderpremium = 0;
                single_adapter_notify();

            }
        });

        select_yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frequency = "Yearly";
//                row_index = 0;
                totalriderpremium = 0;
                yearly_adapter_notify();
            }
        });

        select_halfyearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frequency = "Half Yearly";
//                row_index = 0;
                totalriderpremium = 0;
                haly_yearly_adapter_notify();
            }
        });

        select_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frequency = "Monthly";
//                row_index = 0;
                totalriderpremium = 0;
                monthly_adapter_notify();
            }
        });

        select_quaterly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frequency = "Quaterly";
//                row_index = 0;
                totalriderpremium = 0;
                quaterly_adapter_notify();


            }
        });

        coverupto.setText("Cover Upto");

        sumassured.setText("Sum Assured\n" + premium);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(CalculateInvestmentPremiumPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(CalculateInvestmentPremiumPage.this) {
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

        policy_premium_listing.setLayoutManager(layoutManager1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(CalculateInvestmentPremiumPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(CalculateInvestmentPremiumPage.this) {
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

        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        Drawable mDivider1 = ContextCompat.getDrawable(this, R.drawable.divider);
        DividerItemDecoration hItemDecoration1 = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        if (mDivider != null) {
            hItemDecoration1.setDrawable(mDivider1);
        }

        policy_rider_listing.setLayoutManager(layoutManager2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                select_yearly.setVisibility(View.GONE);
                select_halfyearly.setVisibility(View.GONE);
                select_quaterly.setVisibility(View.GONE);
                select_monthly.setVisibility(View.GONE);
                select_single.setVisibility(View.GONE);
                row_index = 0;
                totalriderpremium = 0;
                pagechanged = true;
                update_policy_listing(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
                        Intent intent = new Intent(CalculateInvestmentPremiumPage.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        get_cibil_credit_factors();

    }


    private void update_policy_listing(int position) {

        try {
            JSONObject jsonObject = new JSONObject(Constants.response);

            if (jsonObject.getString("status").equalsIgnoreCase("200")) {


                JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                policypremiumlist = new ArrayList<>();
                policypremiumlist.clear();

                policyriderlist = new ArrayList<>();
                policyriderlist.clear();

                String company_name = companynames.get(position);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectnew2 = jsonArray.getJSONObject(i);
                    if (company_name.equalsIgnoreCase(objectnew2.getString("name"))) {
                        JSONArray jsonArray1 = objectnew2.getJSONArray("policies");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            if (object.getString("isVisible").equalsIgnoreCase("true")) {
                                Gettersetterforall pack = new Gettersetterforall();
                                JSONObject jsonObject1=object.getJSONObject("maturityBenefit");
                                pack.setPolicy_name(object.getString("name"));
                                pack.setYearly_premium(jsonObject1.getString("annually"));
                                pack.setMonthly_premium(jsonObject1.getString("monthly"));
                                pack.setHalfyearly_premium(jsonObject1.getString("halfYearly"));
                                pack.setQuaterly_premium(jsonObject1.getString("quarterly"));
                                pack.setSingle_premium(object.getString("premium"));
                                policypremiumlist.add(pack);
                            }

                        }
                    }

                }

                if (frequency.equalsIgnoreCase("Yearly")) {
                    yearly_adapter_notify();
                } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                    haly_yearly_adapter_notify();
                } else if (frequency.equalsIgnoreCase("Single")) {
                    single_adapter_notify();
                } else if (frequency.equalsIgnoreCase("Quaterly")) {
                    quaterly_adapter_notify();
                } else if (frequency.equalsIgnoreCase("Monthly")) {
                    monthly_adapter_notify();
                }


//                radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, policypremiumlist);
//                policy_premium_listing.setAdapter(radio_question_list_adapter);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get_cibil_credit_factors() {

        final JSONObject json = new JSONObject();

        try {
            json.put("premium", premium);
            json.put("age", age);
            json.put("policyTerm", policyTerm);
            json.put("premiumPaymentTerm", premiumPaymentTerm);
            json.put("premiumFrequency", premiumFrequency);
            int gen = Integer.parseInt(gender);
            json.put("gender", gen);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/investmentcalc", json,
                response -> {

                    try {
                        Constants.response = response.toString();
                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                            if (jsonArray.length() != 0) {
                                cardlist = new ArrayList<>();
                                cardlist.clear();
                                companynames = new ArrayList<>();
                                companynames.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject objectnew2 = jsonArray.getJSONObject(i);
                                    if (companynames.size() != 0) {
                                        if (objectnew2.getString("isVisible").equalsIgnoreCase("true")) {
                                            if (!companynames.contains(objectnew2.getString("name"))) {
                                                companynames.add(objectnew2.getString("name"));
                                                Gettersetterforall pack = new Gettersetterforall();
                                                pack.setCompany_name(objectnew2.getString("name"));

                                                cardlist.add(pack);
                                            }
                                        }
                                    } else {
                                        if (objectnew2.getString("isVisible").equalsIgnoreCase("true")) {
                                            companynames.add(objectnew2.getString("name"));
                                            Gettersetterforall pack = new Gettersetterforall();
                                            pack.setCompany_name(objectnew2.getString("name"));
                                            cardlist.add(pack);
                                        }
                                    }

                                }


                                DotIndicatorOfferListingAdapter adapter = new DotIndicatorOfferListingAdapter(this, cardlist);
                                viewPager.setAdapter(adapter);
                                viewPager.setCurrentItem(0);
                                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                                springDotsIndicator.setViewPager(viewPager);

//For The First Time
                                select_yearly.setBackgroundResource(R.drawable.selectedpaolicybackground);
                                select_single.setBackgroundResource(R.drawable.ontimepaymentbackground);
                                select_halfyearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
                                select_monthly.setBackgroundResource(R.drawable.ontimepaymentbackground);
                                select_quaterly.setBackgroundResource(R.drawable.ontimepaymentbackground);

                                update_policy_listing(0);

                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(CalculateInvestmentPremiumPage.this, "No Plan Available", Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();
                                finish();
                            }
                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(CalculateInvestmentPremiumPage.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(CalculateInvestmentPremiumPage.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


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

    public class DotIndicatorOfferListingAdapter extends PagerAdapter {
        private ArrayList<Gettersetterforall> list_car;
        Activity context;

        DotIndicatorOfferListingAdapter(Activity mcontext, ArrayList<Gettersetterforall> list) {
            this.list_car = list;
            this.context = mcontext;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View item = LayoutInflater.from(container.getContext()).inflate(R.layout.card_dot_indicator, container, false);


            TextView heading = item.findViewById(R.id.heading);
            TextView suminsured = item.findViewById(R.id.suminsured);
            TextView tvpremium = item.findViewById(R.id.premium);

            heading.setText(list_car.get(position).getCompany_name());
            suminsured.setText("Premium - " + premium);


            container.addView(item);
            return item;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return list_car.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    }

    public class Share_Adapter extends RecyclerView.Adapter<Share_Adapter.MyViewHolder> {

        private ArrayList<Gettersetterforall> list_car;
        Activity context;

        Share_Adapter(Activity mcontext, ArrayList<Gettersetterforall> list) {
            this.list_car = list;
            this.context = mcontext;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv1, tv2, tv3, tv4, tv5, tv6;
            LinearLayout linear;

            MyViewHolder(View view) {
                super(view);
                tv1 = view.findViewById(R.id.policy_name);
                tv2 = view.findViewById(R.id.yearly);
                tv3 = view.findViewById(R.id.halfyearly);
                tv4 = view.findViewById(R.id.monthly);
                tv5 = view.findViewById(R.id.quaterly);
                tv6 = view.findViewById(R.id.single);
                linear = view.findViewById(R.id.linear);

            }
        }

        @Override
        public Share_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_policy_premium, parent, false);

            return new Share_Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(Share_Adapter.MyViewHolder holder, final int position) {

            holder.tv1.setText(list_car.get(position).getPolicy_name());
            if (list_car.get(position).getYearly_premium().equalsIgnoreCase("0")) {
                holder.tv2.setVisibility(View.GONE);
            } else {
                select_yearly.setVisibility(View.VISIBLE);
                holder.tv2.setText("Yearly: " + list_car.get(position).getYearly_premium());
            }

            if (list_car.get(position).getHalfyearly_premium().equalsIgnoreCase("0")) {
                holder.tv4.setVisibility(View.GONE);
            } else {
                select_halfyearly.setVisibility(View.VISIBLE);
                holder.tv4.setText("Half Yearly: " + list_car.get(position).getHalfyearly_premium());
            }

            if (list_car.get(position).getMonthly_premium().equalsIgnoreCase("0")) {
                holder.tv3.setVisibility(View.GONE);

            } else {
                select_monthly.setVisibility(View.VISIBLE);
                holder.tv3.setText("Monthly: " + list_car.get(position).getMonthly_premium());
            }

            if (list_car.get(position).getQuaterly_premium().equalsIgnoreCase("0")) {
                holder.tv5.setVisibility(View.GONE);
            } else {
                select_quaterly.setVisibility(View.VISIBLE);
                holder.tv5.setText("Quaterly: " + list_car.get(position).getQuaterly_premium());
            }

            holder.tv6.setVisibility(View.GONE);

            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    row_index = position;
                    notifyDataSetChanged();

                    if (frequency.equalsIgnoreCase("Yearly")) {
                        totalpremium = Float.parseFloat(list_car.get(row_index).getYearly_premium());
                    } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                        totalpremium =  Float.parseFloat(list_car.get(row_index).getHalfyearly_premium());
                    } else if (frequency.equalsIgnoreCase("Monthly")) {
                        totalpremium =  Float.parseFloat(list_car.get(row_index).getMonthly_premium());
                    } else if (frequency.equalsIgnoreCase("Quaterly")) {
                        totalpremium =  Float.parseFloat(list_car.get(row_index).getQuaterly_premium());
                    } else if (frequency.equalsIgnoreCase("Single")) {
                        totalpremium =  Float.parseFloat(list_car.get(row_index).getSingle_premium());
                    }

                    total_premium.setText("Rs: " + totalpremium + " " + frequency);

                    totalpremium = totalpremium + totalriderpremium;
                    total_premium.setText("Rs: " + totalpremium + " " + frequency);


                }
            });
            if (list_car.size() == row_index) {
                row_index = 0;
            }
            if (row_index == position) {
                holder.linear.setBackgroundResource(R.drawable.selectedpaolicybackground);
            } else {
                holder.linear.setBackgroundResource(R.drawable.deselectedpolicypremiumback);
            }

            if (frequency.equalsIgnoreCase("Yearly")) {
                totalpremium =  Float.parseFloat(list_car.get(row_index).getYearly_premium());
            } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                totalpremium =  Float.parseFloat(list_car.get(row_index).getHalfyearly_premium());
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                totalpremium =  Float.parseFloat(list_car.get(row_index).getMonthly_premium());
            } else if (frequency.equalsIgnoreCase("Quaterly")) {
                totalpremium =  Float.parseFloat(list_car.get(row_index).getQuaterly_premium());
            } else if (frequency.equalsIgnoreCase("Single")) {
                totalpremium =  Float.parseFloat(list_car.get(row_index).getSingle_premium());
            }

            total_premium.setText("Rs: " + totalpremium + " " + frequency);

            totalpremium = totalpremium + totalriderpremium;
            total_premium.setText("Rs: " + totalpremium + " " + frequency);


        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

    }

    public class Share_Adapter2 extends RecyclerView.Adapter<Share_Adapter2.MyViewHolder> {

        private ArrayList<Gettersetterforall> list_car;
        Activity context;

        Share_Adapter2(Activity mcontext, ArrayList<Gettersetterforall> list) {
            this.list_car = list;
            this.context = mcontext;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CheckBox rider_premium_check;

            MyViewHolder(View view) {
                super(view);
                rider_premium_check = view.findViewById(R.id.rider_premium_check);

            }
        }

        @Override
        public Share_Adapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_rider_premium, parent, false);

            return new Share_Adapter2.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(Share_Adapter2.MyViewHolder holder, final int position) {

            if (frequency.equalsIgnoreCase("Yearly")) {
                if (list_car.get(position).getRider_yearly_value().equalsIgnoreCase("0")) {
                    holder.rider_premium_check.setVisibility(View.GONE);
                } else {
                    holder.rider_premium_check.setText(list_car.get(position).getRider_policy_name() + "-" + list_car.get(position).getRider_yearly_value());
                }
            } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                if (list_car.get(position).getRider_half_yearly_value().equalsIgnoreCase("0")) {
                    holder.rider_premium_check.setVisibility(View.GONE);
                } else {
                    holder.rider_premium_check.setText(list_car.get(position).getRider_policy_name() + "-" + list_car.get(position).getRider_half_yearly_value());
                }
            } else if (frequency.equalsIgnoreCase("Single")) {
                if (list_car.get(position).getRider_single_value().equalsIgnoreCase("0")) {
                    holder.rider_premium_check.setVisibility(View.GONE);
                } else {
                    holder.rider_premium_check.setText(list_car.get(position).getRider_policy_name() + "-" + list_car.get(position).getRider_single_value());
                }
            } else if (frequency.equalsIgnoreCase("Quaterly")) {
                if (list_car.get(position).getRider_quaterly_value().equalsIgnoreCase("0")) {
                    holder.rider_premium_check.setVisibility(View.GONE);
                } else {
                    holder.rider_premium_check.setText(list_car.get(position).getRider_policy_name() + "-" + list_car.get(position).getRider_quaterly_value());
                }
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                if (list_car.get(position).getRider_monthly_value().equalsIgnoreCase("0")) {
                    holder.rider_premium_check.setVisibility(View.GONE);
                } else {
                    holder.rider_premium_check.setText(list_car.get(position).getRider_policy_name() + "-" + list_car.get(position).getRider_monthly_value());
                }
            }


            if (list_car.get(position).getRider_check().equalsIgnoreCase("true")) {

                holder.rider_premium_check.setChecked(true);
                int riderpremium = 0;
                if (frequency.equalsIgnoreCase("Yearly")) {
                    riderpremium = Integer.parseInt(list_car.get(position).getRider_yearly_value());
                } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                    riderpremium = Integer.parseInt(list_car.get(position).getRider_half_yearly_value());
                } else if (frequency.equalsIgnoreCase("Single")) {
                    riderpremium = Integer.parseInt(list_car.get(position).getRider_single_value());
                } else if (frequency.equalsIgnoreCase("Quaterly")) {
                    riderpremium = Integer.parseInt(list_car.get(position).getRider_quaterly_value());
                } else if (frequency.equalsIgnoreCase("Monthly")) {
                    riderpremium = Integer.parseInt(list_car.get(position).getRider_monthly_value());
                }
                totalpremium = totalpremium + riderpremium;
                totalriderpremium = totalriderpremium + riderpremium;
            } else {
                holder.rider_premium_check.setChecked(false);
            }

            total_premium.setText("Rs: " + totalpremium + " " + frequency);

            holder.rider_premium_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int riderpremium = 0;
                    if (isChecked) {

                        for (int i = 0; i < policyriderlist.size(); i++) {
                            Gettersetterforall pack = new Gettersetterforall();
                            pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                            pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                            pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                            pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                            pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                            pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                            if (i == position) {
                                pack.setRider_check("true");
                                filter_policyriderlist.set(i, pack);
                            }


                        }
                        if (frequency.equalsIgnoreCase("Yearly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_yearly_value());
                        } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_half_yearly_value());
                        } else if (frequency.equalsIgnoreCase("Single")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_single_value());
                        } else if (frequency.equalsIgnoreCase("Quaterly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_quaterly_value());
                        } else if (frequency.equalsIgnoreCase("Monthly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_monthly_value());
                        }
                        totalpremium = totalpremium + riderpremium;
                        totalriderpremium = totalriderpremium + riderpremium;

                    } else {

                        for (int i = 0; i < policyriderlist.size(); i++) {
                            Gettersetterforall pack = new Gettersetterforall();
                            pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                            pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                            pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                            pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                            pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                            pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                            if (i == position) {
                                pack.setRider_check("false");
                                filter_policyriderlist.set(i, pack);
                            }

                        }
                        if (frequency.equalsIgnoreCase("Yearly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_yearly_value());
                        } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_half_yearly_value());
                        } else if (frequency.equalsIgnoreCase("Single")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_single_value());
                        } else if (frequency.equalsIgnoreCase("Quaterly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_quaterly_value());
                        } else if (frequency.equalsIgnoreCase("Monthly")) {
                            riderpremium = Integer.parseInt(list_car.get(position).getRider_monthly_value());
                        }
                        totalpremium = totalpremium - riderpremium;
                        totalriderpremium = totalriderpremium - riderpremium;


                    }

                    total_premium.setText("Rs: " + totalpremium + " " + frequency);


                }
            });
        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

    }

    private void quaterly_adapter_notify() {

        filter_policypremiumlist = new ArrayList<>();
        filter_policypremiumlist.clear();

        if (pagechanged) {
            filter_policyriderlist = new ArrayList<>();
            filter_policyriderlist.clear();

            for (int i = 0; i < policyriderlist.size(); i++) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                pack.setRider_check("false");

                filter_policyriderlist.add(pack);
            }
        }

        for (int i = 0; i < policypremiumlist.size(); i++) {
            if (!policypremiumlist.get(i).getQuaterly_premium().equalsIgnoreCase("0")) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setPolicy_name(policypremiumlist.get(i).getPolicy_name());
                pack.setMonthly_premium(policypremiumlist.get(i).getMonthly_premium());
                pack.setYearly_premium(policypremiumlist.get(i).getYearly_premium());
                pack.setQuaterly_premium(policypremiumlist.get(i).getQuaterly_premium());
                pack.setHalfyearly_premium(policypremiumlist.get(i).getHalfyearly_premium());
                pack.setSingle_premium(policypremiumlist.get(i).getSingle_premium());
                filter_policypremiumlist.add(pack);
            }
        }

        select_quaterly.setBackgroundResource(R.drawable.selectedpaolicybackground);
        select_yearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_halfyearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_monthly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_single.setBackgroundResource(R.drawable.ontimepaymentbackground);
        if (filter_policypremiumlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "No " + frequency + " Policy Available", Toast.LENGTH_SHORT).show();
            frequency = "Yearly";
            yearly_adapter_notify();
        } else {
            radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, filter_policypremiumlist);
            policy_premium_listing.setAdapter(radio_question_list_adapter);

            if (pagechanged) {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
                pagechanged = false;
            } else {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, filter_policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
            }

        }

    }

    private void yearly_adapter_notify() {

        filter_policypremiumlist = new ArrayList<>();
        filter_policypremiumlist.clear();

        if (pagechanged) {
            filter_policyriderlist = new ArrayList<>();
            filter_policyriderlist.clear();

            for (int i = 0; i < policyriderlist.size(); i++) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                pack.setRider_check("false");

                filter_policyriderlist.add(pack);
            }
        }

        for (int i = 0; i < policypremiumlist.size(); i++) {
            if (!policypremiumlist.get(i).getYearly_premium().equalsIgnoreCase("0")) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setPolicy_name(policypremiumlist.get(i).getPolicy_name());
                pack.setMonthly_premium(policypremiumlist.get(i).getMonthly_premium());
                pack.setYearly_premium(policypremiumlist.get(i).getYearly_premium());
                pack.setQuaterly_premium(policypremiumlist.get(i).getQuaterly_premium());
                pack.setHalfyearly_premium(policypremiumlist.get(i).getHalfyearly_premium());
                pack.setSingle_premium(policypremiumlist.get(i).getSingle_premium());
                filter_policypremiumlist.add(pack);
            }
        }

        select_yearly.setBackgroundResource(R.drawable.selectedpaolicybackground);
        select_quaterly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_halfyearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_monthly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_single.setBackgroundResource(R.drawable.ontimepaymentbackground);

        if (filter_policypremiumlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "No " + frequency + " Policy Available", Toast.LENGTH_SHORT).show();
        }
        radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, filter_policypremiumlist);
        policy_premium_listing.setAdapter(radio_question_list_adapter);

        if (pagechanged) {
            radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, policyriderlist);
            policy_rider_listing.setAdapter(radio_question_rider_adapter);
            pagechanged = false;
        } else {
            radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, filter_policyriderlist);
            policy_rider_listing.setAdapter(radio_question_rider_adapter);
        }

    }

    private void haly_yearly_adapter_notify() {

        filter_policypremiumlist = new ArrayList<>();
        filter_policypremiumlist.clear();

        if (pagechanged) {
            filter_policyriderlist = new ArrayList<>();
            filter_policyriderlist.clear();

            for (int i = 0; i < policyriderlist.size(); i++) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                pack.setRider_check("false");

                filter_policyriderlist.add(pack);
            }
        }
        for (int i = 0; i < policypremiumlist.size(); i++) {
            if (!policypremiumlist.get(i).getHalfyearly_premium().equalsIgnoreCase("0")) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setPolicy_name(policypremiumlist.get(i).getPolicy_name());
                pack.setMonthly_premium(policypremiumlist.get(i).getMonthly_premium());
                pack.setYearly_premium(policypremiumlist.get(i).getYearly_premium());
                pack.setQuaterly_premium(policypremiumlist.get(i).getQuaterly_premium());
                pack.setHalfyearly_premium(policypremiumlist.get(i).getHalfyearly_premium());
                pack.setSingle_premium(policypremiumlist.get(i).getSingle_premium());
                filter_policypremiumlist.add(pack);
            }
        }

        select_halfyearly.setBackgroundResource(R.drawable.selectedpaolicybackground);
        select_yearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_quaterly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_monthly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_single.setBackgroundResource(R.drawable.ontimepaymentbackground);
        if (filter_policypremiumlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "No " + frequency + " Policy Available", Toast.LENGTH_SHORT).show();
            frequency = "Yearly";
            yearly_adapter_notify();
        } else {
            radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, filter_policypremiumlist);
            policy_premium_listing.setAdapter(radio_question_list_adapter);

            if (pagechanged) {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
                pagechanged = false;
            } else {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, filter_policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
            }

        }

    }

    private void single_adapter_notify() {

        filter_policypremiumlist = new ArrayList<>();
        filter_policypremiumlist.clear();

        if (pagechanged) {
            filter_policyriderlist = new ArrayList<>();
            filter_policyriderlist.clear();

            for (int i = 0; i < policyriderlist.size(); i++) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                pack.setRider_check("false");

                filter_policyriderlist.add(pack);
            }
        }

        for (int i = 0; i < policypremiumlist.size(); i++) {
            if (!policypremiumlist.get(i).getSingle_premium().equalsIgnoreCase("0")) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setPolicy_name(policypremiumlist.get(i).getPolicy_name());
                pack.setMonthly_premium(policypremiumlist.get(i).getMonthly_premium());
                pack.setYearly_premium(policypremiumlist.get(i).getYearly_premium());
                pack.setQuaterly_premium(policypremiumlist.get(i).getQuaterly_premium());
                pack.setHalfyearly_premium(policypremiumlist.get(i).getHalfyearly_premium());
                pack.setSingle_premium(policypremiumlist.get(i).getSingle_premium());
                filter_policypremiumlist.add(pack);
            }
        }

        select_single.setBackgroundResource(R.drawable.selectedpaolicybackground);
        select_yearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_halfyearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_monthly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_quaterly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        if (filter_policypremiumlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "No " + frequency + " Policy Available", Toast.LENGTH_SHORT).show();
            frequency = "Yearly";
            yearly_adapter_notify();
        } else {
            radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, filter_policypremiumlist);
            policy_premium_listing.setAdapter(radio_question_list_adapter);

            if (pagechanged) {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
                pagechanged = false;
            } else {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, filter_policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
            }

        }

    }

    private void monthly_adapter_notify() {

        filter_policypremiumlist = new ArrayList<>();
        filter_policypremiumlist.clear();

        if (pagechanged) {
            filter_policyriderlist = new ArrayList<>();
            filter_policyriderlist.clear();

            for (int i = 0; i < policyriderlist.size(); i++) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setRider_policy_name(policyriderlist.get(i).getRider_policy_name());
                pack.setRider_yearly_value(policyriderlist.get(i).getRider_yearly_value());
                pack.setRider_half_yearly_value(policyriderlist.get(i).getRider_half_yearly_value());
                pack.setRider_monthly_value(policyriderlist.get(i).getRider_monthly_value());
                pack.setRider_quaterly_value(policyriderlist.get(i).getRider_quaterly_value());
                pack.setRider_single_value(policyriderlist.get(i).getRider_single_value());
                pack.setRider_check("false");

                filter_policyriderlist.add(pack);
            }
        }

        for (int i = 0; i < policypremiumlist.size(); i++) {
            if (!policypremiumlist.get(i).getMonthly_premium().equalsIgnoreCase("0")) {
                Gettersetterforall pack = new Gettersetterforall();
                pack.setPolicy_name(policypremiumlist.get(i).getPolicy_name());
                pack.setMonthly_premium(policypremiumlist.get(i).getMonthly_premium());
                pack.setYearly_premium(policypremiumlist.get(i).getYearly_premium());
                pack.setQuaterly_premium(policypremiumlist.get(i).getQuaterly_premium());
                pack.setHalfyearly_premium(policypremiumlist.get(i).getHalfyearly_premium());
                pack.setSingle_premium(policypremiumlist.get(i).getSingle_premium());
                filter_policypremiumlist.add(pack);
            }
        }

        select_monthly.setBackgroundResource(R.drawable.selectedpaolicybackground);
        select_yearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_halfyearly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_quaterly.setBackgroundResource(R.drawable.ontimepaymentbackground);
        select_single.setBackgroundResource(R.drawable.ontimepaymentbackground);
        if (filter_policypremiumlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "No " + frequency + " Policy Available", Toast.LENGTH_SHORT).show();
            frequency = "Yearly";
            yearly_adapter_notify();
        } else {
            radio_question_list_adapter = new Share_Adapter(CalculateInvestmentPremiumPage.this, filter_policypremiumlist);
            policy_premium_listing.setAdapter(radio_question_list_adapter);
            if (pagechanged) {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
                pagechanged = false;
            } else {
                radio_question_rider_adapter = new Share_Adapter2(CalculateInvestmentPremiumPage.this, filter_policyriderlist);
                policy_rider_listing.setAdapter(radio_question_rider_adapter);
            }


        }

    }

}
