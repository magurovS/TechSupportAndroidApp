package com.magurovs.techsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.magurovs.techsupport.utils.NetworkUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TechSupportActivity extends AppCompatActivity {
    private EditText name;
    private EditText middleName;
    private EditText lastName;
    private EditText comment;
    private Button sendButton;
    private Spinner problemsSpinner;
    private CheckBox flalgMiddleName;
    private ProgressBar loadingIndicator;

    private Calendar dateAndTime = Calendar.getInstance();
    private final Map<String, String> DATA_FROM_FIELDS = new HashMap<>();

    String[] typesProblems = {"PROBLEM_1", "PROBLEM_2", "PROBLEM_3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.et_name);
        middleName = findViewById(R.id.et_middle_name);
        lastName = findViewById(R.id.et_last_name);
        comment = findViewById(R.id.et_comment);
        sendButton = findViewById(R.id.b_send);
        problemsSpinner = findViewById(R.id.s_type_problem);
        flalgMiddleName = findViewById(R.id.cb_middle_name);
        loadingIndicator = findViewById(R.id.pb_loadingIndicator);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typesProblems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemsSpinner.setAdapter(adapter);

        //Обработка CheckBox
        flalgMiddleName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    middleName.setFocusableInTouchMode(true);
                    //middleName.setLongClickable(true);
                    middleName.setEnabled(true);
                }
                else {
                    middleName.setText("");
                    middleName.setFocusableInTouchMode(false);
                    //middleName.setLongClickable(false);
                    middleName.setEnabled(false);

                }
            }
        });

        //Получение выбранного элемента в Spinner
        problemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> problemsSpinner, View view, int position, long id) {
                String item = (String) problemsSpinner.getItemAtPosition(position);
                DATA_FROM_FIELDS.put("typeProblem",item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Обработка sendButton
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Создания потока для отправка POST-запроса
                setInitialDateTime();

                DATA_FROM_FIELDS.put("name", name.getText().toString());
                DATA_FROM_FIELDS.put("middleName", middleName.getText().toString());
                DATA_FROM_FIELDS.put("lastName",lastName.getText().toString());
                DATA_FROM_FIELDS.put("comment", comment.getText().toString());

               SenderOrderTask sendOrderTask = new SenderOrderTask();
               sendOrderTask.execute();
            }
        });
    }


    public void setInitialDateTime(){
        DATA_FROM_FIELDS.put("timeProblem", DateUtils.formatDateTime(TechSupportActivity.this,
                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
    }


    public void setTime(View v){
        TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                setInitialDateTime(); }
        };
        new TimePickerDialog(TechSupportActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true).show();
    }



    public void setDate(View v){
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateAndTime.set(Calendar.YEAR, year);
                dateAndTime.set(Calendar.MONTH, monthOfYear);
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime();
            }
        };
        new DatePickerDialog(TechSupportActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }



    class SenderOrderTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            String requestURL = "http://localhost:8080/orders";
            JSONObject jsonData = NetworkUtils.buildJSON(DATA_FROM_FIELDS);

            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.get("application/json");
                RequestBody body = RequestBody.create(jsonData.toString(), mediaType);
                Request request = new Request.Builder()
                        .url(requestURL)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                return response.code();

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode){
            Toast toast;
            loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
            if (responseCode == 201){
                Toast.makeText(TechSupportActivity.this, "Заявка отправлена!", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(TechSupportActivity.this, "Заявка не отправлена!", Toast.LENGTH_SHORT).show();
        }

    }
}