package com.cermati.yoris;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener {
    private ArrayList<User> userList = new ArrayList<User>();
    private SuperListview listView;
    private ListAdapter adapter;
    private EditText searchkey;
    private int page = 1;
    RequestQueue queue;
    private CoordinatorLayout coordinatorLayout;
    private final int trigger = 1;
    private final long delay = 1000;
    Handler handler;
    private TextView txtnotfound;
    CountDownTimer Count;
    private int total_count = 0;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (SuperListview) findViewById(R.id.recycler_view);
        searchkey = (EditText)findViewById(R.id.search_key);
        txtnotfound = (TextView)findViewById(R.id.txtnotfound);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        adapter = new ListAdapter(this, userList);
        listView.setAdapter(adapter);
        listView.setupMoreListener(this, 1);
        queue  = Volley.newRequestQueue(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == trigger) {
                    userList.clear();
                    adapter.notifyDataSetChanged();
                    txtnotfound.setVisibility(View.INVISIBLE);
                    getData(searchkey.getText().toString(), 1);
                }
            }
        };

        searchkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                handler.removeMessages(trigger);
                handler.sendEmptyMessageDelayed(trigger, delay);

            }
        });
    }

    public void getData(String key, int page){
        if(!key.matches("")) {
                String url = "https://api.github.com/search/users?q=" + key + "&page=" + page + "&per_page=100";
                Log.d("url", url);
                final int p = page;

                if(p==1){
                    progressBar.setVisibility(View.VISIBLE);
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Response is: ", response.toString());
                                try {
                                    total_count = response.getInt("total_count");
                                    if(total_count == 0){
                                        txtnotfound.setVisibility(View.VISIBLE);
                                        userList.clear();
                                        //adapter.notifyDataSetChanged();
                                    }else{
                                        txtnotfound.setVisibility(View.GONE);
                                        if (p == 1) {userList.clear();}
                                        JSONArray jsonArray = response.getJSONArray("items");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jobj;
                                            jobj = jsonArray.getJSONObject(i);
                                            User u = new User(jobj.getString("login"), jobj.getString("id"), jobj.getString("avatar_url"), jobj.getString("gravatar_id"), jobj.getString("url"));
                                            userList.add(u);
                                        }
                                        //adapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException je) {je.printStackTrace();}
                                adapter.notifyDataSetChanged();
                                listView.hideMoreProgress();
                                if(p==1){
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("MainActivity", error.getMessage());
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            try {
                                JSONObject obj = new JSONObject(jsonError);
                                Toast.makeText(getApplicationContext(), obj.getString("message"),Toast.LENGTH_LONG).show();
                                listView.hideMoreProgress();
                            } catch (Throwable t) {
                                Log.e("My App", "Could not parse malformed JSON: \"" + jsonError + "\"");
                            }
                        }
                    }
                });
                queue.add(jsonObjReq);
        }else{
            userList.clear();
            adapter.notifyDataSetChanged();
            listView.hideMoreProgress();
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        listView.hideProgress();
        //Toast.makeText(this, "More", Toast.LENGTH_LONG).show();
        page++;
        Log.d("page",String.valueOf(page));
        Log.d("total",String.valueOf(userList.size()));

        if(page%10 == 0){
            final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Take it easy pal, you have reached limit of 10 requests per minute!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("", null);
            snackbar.show();
                Count = new CountDownTimer(61000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        Log.d("search", "counter");
                        if(total_count != userList.size()){
                            snackbar.dismiss();
                            getData(searchkey.getText().toString(),page);
                        }else{
                            snackbar.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "All users has been loaded, cheers :)", Snackbar.LENGTH_LONG)
                                    .setAction("", null);
                            snackbar.show();
                            listView.hideMoreProgress();
                        }
                    }
                };
                Count.start();
        }else{
            if(total_count != userList.size()) {
                Log.d("search","bukan mod 10");

                getData(searchkey.getText().toString(), page);
            }else{
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "All users has been loaded, cheers :)", Snackbar.LENGTH_LONG)
                        .setAction("", null);
                snackbar.show();
                listView.hideMoreProgress();

            }
        }
    }

}
