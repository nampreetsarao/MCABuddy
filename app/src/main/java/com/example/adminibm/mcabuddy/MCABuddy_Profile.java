package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.NumberKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.Requester;
import com.example.adminibm.mcabuddy.bean.Subject;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;


public class MCABuddy_Profile extends Activity {

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView role;
    private TextView aoe;


    public Button changePassword;

    public Button updatePhone;
    Context context;

    private JSONObject updatePhoneJsonObject;
    private ProgressDialog pd;
    private  String updatePhoneNumber;

    //Creating a shared preference
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__profile);

        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //fetch the data from shared Preference object
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        Subject userDetails = gson.fromJson(json, Subject.class);

        email = (TextView) findViewById(R.id.ProfileEmail_textView);
        email.setText(userDetails.getEmail());

        name = (TextView) findViewById(R.id.ProfileName_textView);
        name.setText(userDetails.getFname()+" "+userDetails.getLname());

        phone = (TextView) findViewById(R.id.ProfilePhone_textView);
        phone.setText(userDetails.getPhone());

        // TODO :pending roles and aoe

        role = (TextView)findViewById(R.id.ProfileRole_textView);
        for(int i=0;i<userDetails.getRoles().size();i++){
            if(i==userDetails.getRoles().size()-1){
                role.append(userDetails.getRoles().get(i));
            }else{
                role.append(userDetails.getRoles().get(i)+", ");
            }
        }

        aoe = (TextView)findViewById(R.id.ProfileAoe_textView);
        for(int i=0;i<userDetails.getAoe().size();i++){
            if(i==userDetails.getAoe().size()-1){
                aoe.append(userDetails.getAoe().get(i));
            }else{
                aoe.append(userDetails.getAoe().get(i)+", ");
            }
        }


        changePassword = (Button)findViewById(R.id.changepassword_button);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("emailId", email.getText().toString());

                Intent intent = new Intent(MCABuddy_Profile.this, MCABuddy_ChangePassword.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //***********update phone dialog box*****************
        updatePhone = (Button)findViewById(R.id.changephone_button);
        updatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder updatePhoneDialog = new AlertDialog.Builder(MCABuddy_Profile.this);
                updatePhoneDialog.setTitle("Update Phone");
                updatePhoneDialog.setMessage("Enter phone");

                final EditText input = new EditText(MCABuddy_Profile.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                input.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(13),
                        //DigitsKeyListener.getInstance(),
                });
                //input.setKeyListener(DigitsKeyListener.getInstance());
                updatePhoneDialog.setView(input);
                updatePhoneDialog.setIcon(android.R.drawable.sym_action_call);

                updatePhoneDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               updatePhoneNumber=input.getText().toString();
                                updatePhoneNumber(updatePhoneNumber);
                                //Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_LONG).show();
                            }
                        }
                );
                updatePhoneDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                updatePhoneDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__profile, menu);
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



    private void updatePhoneNumber(final String updatePhoneNumber) {

        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //fetch the data from shared Preference object
        String accessToken = mPrefs.getString("accessToken", "");

        //fetch the data from shared Preference object
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        Subject userDetails = gson.fromJson(json, Subject.class);

        Requester requester= new Requester(accessToken,userDetails.getEmail());
        Gson gson1 = new Gson();
        Type type = new TypeToken<Requester>() {}.getType();
        String jsonString = gson1.toJson(requester, type);


        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        HttpEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(jsonString, ContentType.APPLICATION_JSON);
        client.patch(getBaseContext(), Constants.baseURL + Constants.updatePasswordPart1+ userDetails.getEmail()+ Constants.updatePhonePart2+ updatePhoneNumber , null, entity, "application/json", new

                        AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers,
                                                  byte[] bytes) {
                                // called when response HTTP status is "200 OK"
                                try {
                                    pd.dismiss();
                                    // JSON Object
                                    String jsonResponse = new String(bytes, "UTF-8");
                                    JSONObject jsonObject2 = new JSONObject(jsonResponse);
                                    if (jsonObject2.getString("status").equalsIgnoreCase("SUCCESS")) {
                                        Toast.makeText(getApplicationContext(), "Update phone number successfully.", Toast.LENGTH_LONG).show();
                                        phone.setText(updatePhoneNumber);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Updating phone number failed. Please contact administrator.", Toast.LENGTH_LONG).show();
                                    }

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
                                pd = ProgressDialog.show(MCABuddy_Profile.this, "", "Updating phone number..", false);
                                // called before request is started
                            }
                        }
        );
    }


}
