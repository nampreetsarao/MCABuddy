package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.NewUserBean;
import com.example.adminibm.mcabuddy.bean.Requester;
import com.example.adminibm.mcabuddy.bean.Subject;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;


public class MCABuddy_ChangeRole extends Activity {

    private EditText emailEditText;
    private String emailId;
    private String selectedRole;
    private Spinner spinner ;
    private Button submitButton;
    private String loginRole;

    private ProgressDialog pd;
    private JSONObject jsonObject;

    //Creating a shared preference
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__change_role);

        submitButton = (Button) findViewById(R.id.submit_button);

        //fetching the information from email edit text
        emailEditText = (EditText) findViewById(R.id.email_editText);
        emailId=emailEditText.getText().toString();
        // Spinner element
        spinner = (Spinner) findViewById(R.id.role_spinner);

        List<String> categories = new ArrayList<String>();
        categories.add("Change Role To");
        categories.add("Admin");
        categories.add("SME");
        categories.add("User");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        selectedRole=spinner.getSelectedItem().toString();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeRole();
            }
        });

    }



    public void onChangeRole(){

        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //fetch the data from shared Preference object
        String accessToken = mPrefs.getString("accessToken", "");


        emailId=emailEditText.getText().toString();
        selectedRole=spinner.getSelectedItem().toString().toLowerCase();


        //fetch the data from shared Preference object
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        Subject userDetails = gson.fromJson(json, Subject.class);



        Requester requester= new Requester(accessToken,userDetails.getEmail());

        for(String role:userDetails.getRoles()){
            if(role.equalsIgnoreCase("admin")){
                loginRole=role;
            }else {
                loginRole="user";
            }
        }
        Gson gson1 = new Gson();
        Type type = new TypeToken<Requester>() {}.getType();
        String jsonString = gson1.toJson(requester, type);
        updateUserRole(jsonString);
    }

    private void updateUserRole(String jsonString) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        HttpEntity entity = null;
        entity = new cz.msebera.android.httpclient.entity.StringEntity(jsonString, ContentType.APPLICATION_JSON);
        client.patch(getBaseContext(), Constants.baseURL + Constants.changeRolePart1 + emailId + Constants.changeRolePart2 + selectedRole, null, entity, "application/json", new

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
                                    if (jsonObject.getString("status").equals("SUCCESS")) {
                                        Toast.makeText(getApplicationContext(), "Role Updated", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Role Update Failed. Please contact administrator.", Toast.LENGTH_LONG).show();
                                    }
                                    pd.dismiss();


                                   /* //Get the bundle
                                    Bundle bundle = getIntent().getExtras();

                                    bundle.putString("loginRole",loginRole );
                                    Intent intent= new Intent(MCABuddy_ChangeRole.this,MCABuddy_Dashboard.class);
                                    startActivity(intent);*/
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    Toast.makeText(getApplicationContext(), "Error Occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    Toast.makeText(getApplicationContext(), "Mostly parsing error", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers,
                                                  byte[] bytes, Throwable throwable) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onStart() {

                                pd = ProgressDialog.show(MCABuddy_ChangeRole.this, "", "Updating Role..", false);

                                // called before request is started
                            }

                        }

        );
        }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__change_role, menu);
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

    public void onBackPressed() {
        MCABuddy_ChangeRole.this.finish();
    }



}
