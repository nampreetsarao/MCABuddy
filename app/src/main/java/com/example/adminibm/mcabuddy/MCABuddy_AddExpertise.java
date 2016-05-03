package com.example.adminibm.mcabuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.Requester;
import com.example.adminibm.mcabuddy.bean.Subject;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;


public class MCABuddy_AddExpertise extends ActionBarActivity {

    private ProgressDialog pd;
    private JSONObject jsonObject;
    private Button addButton;
    private EditText emailEditText;
    private EditText expertiseEditText;

    private String emailId;
    private String expertise;

    //Creating a shared preference
    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__add_expertise);

        //fetching the information from email & expertise edit text
        emailEditText = (EditText) findViewById(R.id.email_editText);
        emailId=emailEditText.getText().toString();
        expertiseEditText = (EditText) findViewById(R.id.addExpertise_editText);
        expertise=expertiseEditText.getText().toString();

        //submit button listener initialization
        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__add_expertise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSubmit(){

        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //fetch the data from shared Preference object
        String accessToken = mPrefs.getString("accessToken", "");


        emailId=emailEditText.getText().toString();
        expertise=expertiseEditText.getText().toString();


        //fetch the data from shared Preference object
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        Subject userDetails = gson.fromJson(json, Subject.class);



        Requester requester= new Requester(accessToken,userDetails.getEmail());
        Gson gson1 = new Gson();
        Type type = new TypeToken<Requester>() {}.getType();
        String jsonString = gson1.toJson(requester, type);
        addExpertise(jsonString);
    }

    private void addExpertise(String jsonString) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        HttpEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(jsonString, ContentType.APPLICATION_JSON);
        client.patch(getBaseContext(), Constants.baseURL + Constants.changeRolePart1+ emailId + Constants.addExpertise+ expertise, null, entity, "application/json", new

                        AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers,
                                                  byte[] bytes) {
                                // called when response HTTP status is "200 OK"
                                try {
                                    pd.dismiss();
                                    // JSON Object
                                    String jsonResponse = new String(bytes, "UTF-8");
                                    jsonObject = new JSONObject(jsonResponse);
                                    if (jsonObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                                        Toast.makeText(getApplicationContext(), "Added expertise successfully.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Adding expertise failed. Please contact administrator.", Toast.LENGTH_LONG).show();
                                    }
                                    pd.dismiss();

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    Toast.makeText(getApplicationContext(), "Error Occurred [Server's JSON response might be invalid]. Please contact administrator.", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    Toast.makeText(getApplicationContext(), "Mostly parsing error. Please contact administrator.", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers,
                                                  byte[] bytes, Throwable throwable) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString() +".Please contact administrator.", Toast.LENGTH_LONG).show();
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onStart() {
                                pd = ProgressDialog.show(MCABuddy_AddExpertise.this, "", "Adding expertise..", false);
                                // called before request is started
                            }
                        }
        );
    }

}
