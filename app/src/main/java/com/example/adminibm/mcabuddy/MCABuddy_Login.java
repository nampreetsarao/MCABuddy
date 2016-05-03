package com.example.adminibm.mcabuddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adminibm.mcabuddy.bean.Subject;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.example.adminibm.mcabuddy.helper.WebServiceHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;


public class MCABuddy_Login extends ActionBarActivity {

    //to store object in session: shared preferences
    SharedPreferences sharedPreferences;

    public EditText EmailEditText;
    public EditText PasswordEditText;
    public Button LoginButton;

    public String email;
    public String password;


    private Handler dashboardHandler = new Handler();

    private Bundle bundle;

    private Intent dashboardIntent;
    //private Intent subscriberDashboardIntent;

    private ProgressDialog pd;
    private JSONObject jsonObject;
    private static String admin = "admin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__login);

        EmailEditText = (EditText) findViewById(R.id.email_editText);
        PasswordEditText = (EditText) findViewById(R.id.password_editText);
        LoginButton = (Button) findViewById(R.id.login_button);
        dashboardIntent = new Intent(MCABuddy_Login.this, MCABuddy_Dashboard.class);

        //initializing the sharedPreference object
        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);


    }


    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.isEmailAddress(EmailEditText, true)) ret = false;
        if (!Validations.hasText(PasswordEditText)) ret = false;

        return ret;
    }

    public void onClick(View v) {
        email = EmailEditText.getText().toString();
        password = PasswordEditText.getText().toString();
        if (email.isEmpty() & password.isEmpty()) {
            EmailEditText.setError("Email cannot be empty.");
            PasswordEditText.setError("Password cannot be empty.");
        } else if (email.isEmpty()) {
            EmailEditText.setError("Email cannot be empty.");

        } else if (password.isEmpty()) {
            EmailEditText.setError("Password cannot be empty.");
        } else {
            //adminDashboardIntent.putExtras(bundle);
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", email);
            requestParams.add("pwd", password);
            authenticateUser(requestParams);
        }

        /*if(checkValidation()){*//*
            adminDashboardIntent.putExtras(bundle);
            RequestParams requestParams = new RequestParams();
            requestParams.add("email", email);
            requestParams.add("pwd", password);
            authenticateUser(requestParams);*/
        /*}
        else{
            Toast.makeText(MCABuddy_Login.this, "Form contains error", Toast.LENGTH_LONG).show();
        }*/

    }

    /**
     * Authenticate user
     *
     * @param requestParams
     */
    private void authenticateUser(RequestParams requestParams) {

        if(WebServiceHelper.isConnectedToInternet(getApplicationContext())){
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("content-type", "application/x-www-form-urlencoded");
            client.post(Constants.baseURL+Constants.authenticateURL, requestParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                    // called when response HTTP status is "200 OK"
                    try {
                        pd.dismiss();
                        // JSON Object
                        String jsonResponse = new String(bytes, "UTF-8");
                        jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.getString("status").equals("SUCCESS")) {
                            Toast.makeText(getApplicationContext(), "Authentication Passed.", Toast.LENGTH_LONG).show();

                            //Initialize the user details object: Subject
                            Subject subject = new Subject();

                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            boolean isAdmin = false;
                            List<String> roles = new ArrayList<String>();
                            //check if the user is admin or not
                            for (int i = 0; i < responseObj.getJSONArray("roles").length(); i++) {
                                roles.add(responseObj.getJSONArray("roles").getString(i));
                                if (responseObj.getJSONArray("roles").getString(i).equalsIgnoreCase(admin)) {
                                    isAdmin = true;
                                }
                            }

                            List<String> aoe = new ArrayList<String>();
                            //populate expertise
                            for (int i = 0; i < responseObj.getJSONArray("aoe").length(); i++) {
                                aoe.add(responseObj.getJSONArray("aoe").getString(i));
                            }

                            //Setting user profile information in the shared preferences
                            subject.setAoe(aoe);
                            subject.setRoles(roles);
                            subject.setEmail(responseObj.getString("email"));
                            subject.setPhone(responseObj.getString("phone"));
                            subject.setFname(responseObj.getString("fname"));
                            subject.setLname(responseObj.getString("lname"));
                            //Convert to gson string to put into shared preference
                            Gson gson = new Gson();
                            String jsonUserObject = gson.toJson(subject);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userDetails", jsonUserObject);
                            editor.putString("accessToken", responseObj.getString("accessToken"));



                            //If the user belongs to the admin group, then admin dashboard is launched
                            if (isAdmin) {
                                bundle = new Bundle();
                                bundle.putString("loginRole", "admin");
                                editor.putString("loginRole", "admin");
                                //need to change, should get value from service call
                                dashboardIntent.putExtras(bundle);
                                startActivity(dashboardIntent);
                            } else {
                                bundle = new Bundle();
                                bundle.putString("loginRole", "user");
                                editor.putString("loginRole","user");
                                dashboardIntent.putExtras(bundle);
                                startActivity(dashboardIntent);
                            }
                            editor.commit();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                        pd.dismiss();
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
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();


                }

                @Override
                public void onStart() {

                    pd = ProgressDialog.show(MCABuddy_Login.this, "", "Authenticating User..", false);
                    // called before request is started
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__login, menu);
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

    private Boolean exit = false;

    @Override
    public void onBackPressed() {

        Intent launchNextActivity;
        launchNextActivity = new Intent(MCABuddy_Login.this, MCABuddy_FlashScreen.class);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(launchNextActivity);

        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

        //MCABuddy_Login.this.finish();

    }
}
