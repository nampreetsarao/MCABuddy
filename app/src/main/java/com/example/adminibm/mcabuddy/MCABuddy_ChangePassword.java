package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.helper.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;


public class MCABuddy_ChangePassword extends Activity {

    public TextView listHeader;
    public EditText oldPassword;
    public EditText newPassword;
    public EditText confirmNewPassword;
    public Button submitButton;
    private JSONObject updatePasswordJsonObject;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__change_password);

        listHeader = (TextView) findViewById(R.id.lblListHeader);
        submitButton = (Button) findViewById(R.id.submitPassword_button);

        registerViews();
    }


    private void registerViews() {
        oldPassword = (EditText) findViewById(R.id.oldPassword_editText);
        // TextWatcher would let us check validation error on the fly
        oldPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(oldPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        newPassword = (EditText) findViewById(R.id.newPassword_edittext);
        // TextWatcher would let us check validation error on the fly
        newPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(newPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        confirmNewPassword = (EditText)findViewById(R.id.confirmnewpassword_edittext);
        confirmNewPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(confirmNewPassword);
                Validations.compareText(newPassword, confirmNewPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

/*        accesstoken = (EditText) findViewById(R.id.accesstoken_edittext);
        accesstoken.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.isPhoneNumber(accesstoken, false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });*/



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    changePassword();
                   // Toast.makeText(getApplicationContext(), "Form Pass", Toast.LENGTH_LONG).show();
                } else {
                   // Toast.makeText(getApplicationContext(), "Form Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(oldPassword)) ret = false;
        if (!Validations.hasText(newPassword)) ret = false;
        if (!Validations.hasText(confirmNewPassword)) ret = false;
        if (!Validations.compareText(newPassword, confirmNewPassword)) ret = false;
        //if (!Validations.isPhoneNumber(accesstoken, false)) ret = false;

        return ret;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__change_password, menu);
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

    private void changePassword() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");

        RequestParams requestParams = new RequestParams();
        requestParams.add("oldPwd",oldPassword.getText().toString());
        requestParams.add("newPwd",newPassword.getText().toString());
        client.addHeader("content-type", "application/x-www-form-urlencoded");

//        HttpEntity entity = new cz.msebera.android.httpclient.entity.StringEntity("oldPwd="+oldPassword.getText().toString()+"&newPwd="+oldPassword.getText().toString(), "UTF-8");
        Bundle bundle=getIntent().getExtras();
        client.patch( Constants.baseURL + Constants.updatePasswordPart1 + bundle.getString("emailId") + Constants.updatePasswordPart2, requestParams, new

                        AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers,
                                                  byte[] bytes) {
                                // called when response HTTP status is "200 OK"
                                try {
                                    pd.dismiss();
                                    // JSON Object
                                    String jsonResponse = new String(bytes, "UTF-8");
                                    updatePasswordJsonObject = new JSONObject(jsonResponse);
                                    if (updatePasswordJsonObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                                        Toast.makeText(getApplicationContext(), "Updated password successfully.", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Updating password failed. Please contact administrator.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString() + ".Please contact administrator.", Toast.LENGTH_LONG).show();
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onStart() {
                                pd = ProgressDialog.show(MCABuddy_ChangePassword.this, "", "Updating password..", false);
                                // called before request is started
                            }
                        }
        );
    }


}
