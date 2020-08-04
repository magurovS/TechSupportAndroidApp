package com.magurovs.techsupport;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button techSupport;
    private Button orders;
    private ProgressBar loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.pb_loadingIndicator);
        }

        public void onCick(View v){
            switch (v.getId()){
                case R.id.b_tech_support:
                    Intent techSupportActivityIntent = new Intent(MainActivity.this, TechSupportActivity.class);
                    startActivity(techSupportActivityIntent);
                    break;
                case R.id.b_orders:
                    Intent ordersActivityIntent = new Intent(MainActivity.this, OrdersActivity.class);
                    RecipientOrders recipientOrders = new RecipientOrders();
                    recipientOrders.execute();
                    try {
                        JSONArray arrayOrders = recipientOrders.get();
                        ordersActivityIntent.putExtra("arrayOrders", arrayOrders.toString());
                        startActivity(ordersActivityIntent);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }

        class RecipientOrders extends AsyncTask<Void, Void, JSONArray> {

            @Override
            protected void onPreExecute() {
                loadingIndicator.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                String requestURL = "http://localhost:8080/orders";

                try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url(requestURL)
                        .build();
                Response response = client.newCall(request).execute();
                String r = response.body().string();
                JSONArray arrayOrders = new JSONArray(r);

                return arrayOrders;
//              JSONObject o1 = new JSONObject(o.get(0).toString());
//              System.out.println(o1.get("typeProblem"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONArray arrayOrders){
                loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
            }
        }

}