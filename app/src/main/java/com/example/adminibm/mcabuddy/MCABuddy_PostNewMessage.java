package com.example.adminibm.mcabuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.CreateMessage;
import com.example.adminibm.mcabuddy.bean.Message;
import com.example.adminibm.mcabuddy.bean.NewUserBean;
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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;

public class MCABuddy_PostNewMessage extends AppCompatActivity {

    public EditText subjectTextView;
    public EditText messageTextView;
    public EditText tagEditText1;

    public TextView tag1;
    public TextView tag2;
    public TextView tag3;
    public TextView tag4;
    public TextView tag5;
    public List<String> tags;

    //Creating a shared preference
    SharedPreferences mPrefs;
    private ProgressDialog pd;

    //channel spinner
    public Spinner spinner ;
    public String selectedChannel;
    private Subject userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__post_new_message);

        subjectTextView = (EditText) findViewById(R.id.subject_editText);
        messageTextView = (EditText) findViewById(R.id.message_editText);
        tagEditText1 = (EditText)findViewById(R.id.posttag_editText);

        tag1 = (TextView)findViewById(R.id.tag1_textView);
        tag2 = (TextView)findViewById(R.id.tag2_textView);
        tag3 = (TextView)findViewById(R.id.tag3_textView);
        tag4 = (TextView)findViewById(R.id.tag4_textView);
        tag5 = (TextView)findViewById(R.id.tag5_textView);

        // Spinner element
        spinner = (Spinner) findViewById(R.id.channel_spinner);

        List<String> categories = new ArrayList<String>();
        Gson gson = new Gson();
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        String json = mPrefs.getString("userDetails", "");
        userDetails = gson.fromJson(json, Subject.class);
        boolean isAdmin=false;
        for(String role:userDetails.getRoles()){
            if(role.equalsIgnoreCase("admin")){
                isAdmin=true;
            }
        }

        if(isAdmin){
            categories.add("Information");
            categories.add("Broadcast");
            categories.add("SOS");
            categories.add("Knowledge");

        }else{
            categories.add("SOS");
            categories.add("Knowledge");
            categories.add("Information");
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        tagEditText1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    switch(keyCode){
                        case KeyEvent.KEYCODE_ENTER:
                            if(tag1.getText().toString().isEmpty() && tagEditText1.getText().length() != 0){
                                tag1.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag1.setText(tagEditText1.getText().toString());
                                tag1.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            }
                            else if (tag2.getText().toString().isEmpty() && tagEditText1.getText().length() != 0){
                                tag2.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag2.setText(tagEditText1.getText().toString());
                                tag2.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            }
                            else if (tag3.getText().toString().isEmpty() && tagEditText1.getText().length() != 0){
                                tag3.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag3.setText(tagEditText1.getText().toString());
                                tag3.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            }
                            else if (tag4.getText().toString().isEmpty() && tagEditText1.getText().length() != 0){
                                tag4.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag4.setText(tagEditText1.getText().toString());
                                tag4.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            }
                            else {
                                if(tagEditText1.getText().length() != 0) {
                                    tag5.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                    tag5.setText(tagEditText1.getText());
                                    tag5.setTextColor(getResources().getColor(R.color.textColorPrimary));
                                    tagEditText1.setVisibility(View.GONE);
                                }
                            }

                            tagEditText1.setText("");

                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });

        //submit button listener initialization
        Button addButton = (Button) findViewById(R.id.postnewmessage_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidation()) {
                    doAction();
                }else{
                    Toast.makeText(MCABuddy_PostNewMessage.this, "Form contains error", Toast.LENGTH_LONG).show();
                }
            }
        });

        postNewMessageValidation();
    }

    private void postNewMessageValidation(){
        subjectTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Validations.hasText(subjectTextView);
            }
        });

        messageTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Validations.hasText(messageTextView);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(subjectTextView)) ret = false;
        if (!Validations.hasText(messageTextView)) ret = false;
        return ret;
    }


    public void doAction(){
        //Fetching details from preferences
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //Fetch the data from shared Preference object
        String accessToken = mPrefs.getString("accessToken", "");

        //Fetch the data from shared Preference object

        //Fetch the channel
        selectedChannel=spinner.getSelectedItem().toString();


        //Fetch the new message details
        String subject=subjectTextView.getText().toString();
        String body=messageTextView.getText().toString();
        tags = new ArrayList<>();
        if(tag1.getText()!=null && tag1.getText().length()>0){
            tags.add(tag1.getText().toString());
        }
        if(tag2.getText()!=null && tag2.getText().length()>0){
            tags.add(tag2.getText().toString());
        }
        if(tag3.getText()!=null && tag3.getText().length()>0){
            tags.add(tag3.getText().toString());
        }
        if(tag4.getText()!=null && tag4.getText().length()>0){
            tags.add(tag4.getText().toString());
        }
        if(tag5.getText()!=null && tag5.getText().length()>0){
            tags.add(tag5.getText().toString());
        }

        Requester requester = new Requester(accessToken,userDetails.getEmail());
        Message  message = new Message();
        message.setMessage(body);
        message.setTitle(subject);
        message.setLikes(0);
        message.setAuthor(userDetails.getEmail());
        //message.setDate("2016-04-15T00:00:00Z");
        message.setChannel(selectedChannel.toLowerCase());
        message.setTags(tags);


        Gson gson = new Gson();
        CreateMessage createMessage = new CreateMessage(requester, message);
        Type type = new TypeToken<CreateMessage>() {}.getType();
        String requestJson = gson.toJson(createMessage, type);
        try {
            createNewMessage(requestJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create new user
     * @param jsonString
     */
    private void createNewMessage(String jsonString) throws Exception{

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        HttpEntity entity = null;
        entity = new cz.msebera.android.httpclient.entity.StringEntity(jsonString, ContentType.APPLICATION_JSON);

        client.put(getBaseContext(), Constants.baseURL+Constants.postNewMessagePart1+selectedChannel.toLowerCase()+Constants.postNewMessagePart2, null, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    pd.dismiss();
                    // JSON Object
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getString("status").equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Message posted successfully !!", Toast.LENGTH_LONG).show();
                        //check if the user is admin or not
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to post message at this time, try again later!!", Toast.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                    //need to change, should get value from service call
                    Intent dashboardIntent= new Intent(MCABuddy_PostNewMessage.this, MCABuddy_Dashboard.class);
                    Bundle bundle = new Bundle();

                    String loginRole = mPrefs.getString("loginRole", "");
                    bundle.putString("loginRole", loginRole);
                    dashboardIntent.putExtras(bundle);
                    startActivity(dashboardIntent);
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
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
                pd = ProgressDialog.show(MCABuddy_PostNewMessage.this, "", "Posting new message..", false);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }


}
