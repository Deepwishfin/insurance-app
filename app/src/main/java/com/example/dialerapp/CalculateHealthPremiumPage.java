package com.example.dialerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class CalculateHealthPremiumPage extends Activity {

    ProgressDialog progressDialog;
    RequestQueue queue;
    SharedPreferences prefs;
    Dialog dialog;
    String sum_assured_str = "", rider_cover_str = "", cover_upto = "", lead_id = "", lead_name = "", dateOfBirth = "", policy_cover_range = "", noOfChild = "", city_id = "", spouseAge = "", preexistingDisease = "", gender = "", deductible = "";
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
    int totalpremium = 0, totalriderpremium = 0;
    boolean pagechanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.healthpremiumdetailpage);

        progressDialog = new ProgressDialog(CalculateHealthPremiumPage.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        dialog = new Dialog(CalculateHealthPremiumPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        queue = Volley.newRequestQueue(CalculateHealthPremiumPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(CalculateHealthPremiumPage.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = "";
                lead_name = "";
                policy_cover_range = "";
                dateOfBirth = "";
                city_id = "";
                spouseAge = "";
                deductible = "";
                noOfChild = "";
                preexistingDisease = "";
                gender = "";
                cover_upto = "";
                sum_assured_str = "";
                rider_cover_str = "";
            } else {
                lead_id = extras.getString("lead_id");
                lead_name = extras.getString("lead_name");
                policy_cover_range = extras.getString("policy_cover_range");
                dateOfBirth = extras.getString("dateOfBirth");
                city_id = extras.getString("cityId");
                spouseAge = extras.getString("spouseAge");
                deductible = extras.getString("deductible");
                noOfChild = extras.getString("noOfChild");
                preexistingDisease = extras.getString("preexistingDisease");
                gender = extras.getString("gender");
                cover_upto = extras.getString("cover_upto");
                sum_assured_str = extras.getString("sum_assured_str");
                rider_cover_str = extras.getString("rider_cover_str");
            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            lead_name = (String) savedInstanceState.getSerializable("lead_name");
            policy_cover_range = (String) savedInstanceState.getSerializable("policy_cover_range");
            dateOfBirth = (String) savedInstanceState.getSerializable("dateOfBirth");
            city_id = (String) savedInstanceState.getSerializable("cityId");
            spouseAge = (String) savedInstanceState.getSerializable("spouseAge");
            deductible = (String) savedInstanceState.getSerializable("deductible");
            noOfChild = (String) savedInstanceState.getSerializable("noOfChild");
            preexistingDisease = (String) savedInstanceState.getSerializable("preexistingDisease");
            gender = (String) savedInstanceState.getSerializable("gender");
            cover_upto = (String) savedInstanceState.getSerializable("cover_upto");
            sum_assured_str = (String) savedInstanceState.getSerializable("sum_assured_str");
            rider_cover_str = (String) savedInstanceState.getSerializable("rider_cover_str");

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

        coverupto.setText("Cover Upto\n" + cover_upto);
        sumassured.setText("Sum Assured\n" + sum_assured_str);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(CalculateHealthPremiumPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(CalculateHealthPremiumPage.this) {
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

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(CalculateHealthPremiumPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(CalculateHealthPremiumPage.this) {
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

        loggedin.setText(SessionManager.get_username(prefs));
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
                        Intent intent = new Intent(CalculateHealthPremiumPage.this, SplashActivity.class);
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
                    if (company_name.equalsIgnoreCase(objectnew2.getString("companyName"))) {
                        JSONArray jsonArray1 = objectnew2.getJSONArray("planList");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            if (object.getString("isVisible").equalsIgnoreCase("true")) {
                                Gettersetterforall pack = new Gettersetterforall();
                                pack.setPolicy_name(object.getString("productName"));
                                pack.setYearly_premium(object.getString("premium"));
                                pack.setMonthly_premium(object.getString("totalSumInsured"));
                                pack.setSingle_premium(object.getString("policyTerm"));
                                policypremiumlist.add(pack);
                            }

                        }
                    }

                }

                radio_question_list_adapter = new Share_Adapter(CalculateHealthPremiumPage.this, policypremiumlist);
                policy_premium_listing.setAdapter(radio_question_list_adapter);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get_cibil_credit_factors() {

        final JSONObject json = new JSONObject();

        try {
            int coverrange = Integer.parseInt(policy_cover_range);
            json.put("policy_cover_range", coverrange);
            json.put("dateOfBirth", "" + dateOfBirth);
            int noofchild = Integer.parseInt(noOfChild);
            json.put("noOfChild", noofchild);
            int preexist = Integer.parseInt(preexistingDisease);
            json.put("preexistingDisease", preexist);
            int gen = Integer.parseInt(gender);
            json.put("gender", gen);
            int city = Integer.parseInt(city_id);
            json.put("cityId", city);
            json.put("deductible", "" + deductible);
            int spage = Integer.parseInt(spouseAge);
            json.put("spouseAge", spage);
            json.put("app_version", Constants.app_version);
//


        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Constants.BASE_URL + "app_insurance/api/healthcalc", json,
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
                                            if (!companynames.contains(objectnew2.getString("companyName"))) {
                                                companynames.add(objectnew2.getString("companyName"));
                                                Gettersetterforall pack = new Gettersetterforall();
                                                pack.setCompany_name(objectnew2.getString("companyName"));

                                                cardlist.add(pack);
                                            }
                                        }
                                    } else {
                                        if (objectnew2.getString("isVisible").equalsIgnoreCase("true")) {
                                            companynames.add(objectnew2.getString("companyName"));
                                            Gettersetterforall pack = new Gettersetterforall();
                                            pack.setCompany_name(objectnew2.getString("companyName"));
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
                                select_yearly.setBackgroundResource(R.drawable.selectedfrequencybackground);
                                select_single.setBackgroundResource(R.drawable.unselectedfrequencyback);
                                select_halfyearly.setBackgroundResource(R.drawable.unselectedfrequencyback);
                                select_monthly.setBackgroundResource(R.drawable.unselectedfrequencyback);
                                select_quaterly.setBackgroundResource(R.drawable.unselectedfrequencyback);

                                select_yearly.setTextColor(Color.parseColor("#FFFFFF"));
                                select_single.setTextColor(Color.parseColor("#000000"));
                                select_halfyearly.setTextColor(Color.parseColor("#000000"));
                                select_monthly.setTextColor(Color.parseColor("#000000"));
                                select_quaterly.setTextColor(Color.parseColor("#000000"));

                                update_policy_listing(0);

                                if (cardlist.size() == 0) {
                                    Toast.makeText(CalculateHealthPremiumPage.this, "No Plan Available", Toast.LENGTH_LONG).show();

                                    finish();
                                }

                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(CalculateHealthPremiumPage.this, "No Plan Available", Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();
                                finish();
                            }
                        } else if (jsonObject.getString("status").equalsIgnoreCase("403")) {
                            progressDialog.dismiss();
                            SessionManager.dataclear(prefs);
                            Intent intent = new Intent(CalculateHealthPremiumPage.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(CalculateHealthPremiumPage.this, "Session Expired,Login Again", Toast.LENGTH_LONG).show();


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
            TextView premium = item.findViewById(R.id.premium);

            heading.setText(list_car.get(position).getCompany_name());
            suminsured.setText("Sum Insured - " + sum_assured_str);


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
                holder.tv2.setText("Premium: " + list_car.get(position).getYearly_premium());
            }

            if (list_car.get(position).getMonthly_premium().equalsIgnoreCase("0")) {
                holder.tv4.setVisibility(View.GONE);

            } else {
                select_monthly.setVisibility(View.VISIBLE);
                holder.tv4.setText("Total Sum Insurred: " + list_car.get(position).getMonthly_premium());
            }


            if (list_car.get(position).getSingle_premium().equalsIgnoreCase("0")) {
                holder.tv6.setVisibility(View.GONE);
            } else {
                select_single.setVisibility(View.VISIBLE);
                holder.tv6.setText("Policy Term: " + list_car.get(position).getSingle_premium());
            }

            holder.tv3.setVisibility(View.GONE);
            holder.tv5.setVisibility(View.GONE);

            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    row_index = position;
                    notifyDataSetChanged();

                    if (frequency.equalsIgnoreCase("Yearly")) {
                        totalpremium = Integer.parseInt(list_car.get(row_index).getYearly_premium());
                    } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                        totalpremium = Integer.parseInt(list_car.get(row_index).getHalfyearly_premium());
                    } else if (frequency.equalsIgnoreCase("Monthly")) {
                        totalpremium = Integer.parseInt(list_car.get(row_index).getMonthly_premium());
                    } else if (frequency.equalsIgnoreCase("Quaterly")) {
                        totalpremium = Integer.parseInt(list_car.get(row_index).getQuaterly_premium());
                    } else if (frequency.equalsIgnoreCase("Single")) {
                        totalpremium = Integer.parseInt(list_car.get(row_index).getSingle_premium());
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
                holder.linear.setBackgroundResource(R.drawable.selectedpolicybackground);
            } else {
                holder.linear.setBackgroundResource(R.drawable.deselectedpolicypremiumback);
            }
            if (frequency.equalsIgnoreCase("Yearly")) {
                totalpremium = Integer.parseInt(list_car.get(row_index).getYearly_premium());
            } else if (frequency.equalsIgnoreCase("Half Yearly")) {
                totalpremium = Integer.parseInt(list_car.get(row_index).getHalfyearly_premium());
            } else if (frequency.equalsIgnoreCase("Monthly")) {
                totalpremium = Integer.parseInt(list_car.get(row_index).getMonthly_premium());
            } else if (frequency.equalsIgnoreCase("Quaterly")) {
                totalpremium = Integer.parseInt(list_car.get(row_index).getQuaterly_premium());
            } else if (frequency.equalsIgnoreCase("Single")) {
                totalpremium = Integer.parseInt(list_car.get(row_index).getSingle_premium());
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

}
