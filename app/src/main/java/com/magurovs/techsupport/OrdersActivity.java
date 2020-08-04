package com.magurovs.techsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class OrdersActivity extends AppCompatActivity {
    private TextView fieldOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        fieldOrders = findViewById(R.id.tv_field_orders);

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra("arrayOrders")){
            String strArray = intentThatStartedThisActivity.getStringExtra("arrayOrders");
            try {
                JSONArray j = new JSONArray(strArray);
                for (int i=0; i<j.length(); i++){
                    JSONObject order = j.getJSONObject(i);
                    String str = "number: " + order.getString("number") + "\n" +
                            "typeProblem: " + order.getString("typeProblem") + "\n" +
                            "timeProblem: " + order.getString("timeProblem") + "\n\n";

                    fieldOrders.setText(str);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}