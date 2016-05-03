package com.example.adminibm.mcabuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.CreateMessage;
import com.example.adminibm.mcabuddy.bean.Message;
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


public class MCABuddy_ThreadDetails extends ActionBarActivity {

    Bundle bundle;

    public String strHeaderValue;
    public String strDetailsValue;
    public String strRole;
    public String strAuther;
    public String strDate;
    public String[] strTags;
    public String uuid;
    public int likes;
    private boolean likeClicked;

    TextView lblListItemDetails;
    TextView lblHeaderItemText;

    private TextView addcommentTextView;
    private RelativeLayout itemDetailsLayout;
    private ScrollView mainScroll;
    private TextView autherName;
    private TextView threadDate;
    private ImageButton likeImageButton;
    private Button onPostClickButton;
    private TextView numberOfLikes;

    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private TextView tag4;
    private TextView tag5;

    private EditText tagEditText;

    private LinearLayout mLayout;
    private RelativeLayout actionLayout;
    private RelativeLayout tagActionLayout;
    Handler handle_listItemHandle = new Handler();
    SharedPreferences mPrefs;
    private Subject userDetails;
    private EditText editText;
    private  String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_details);
        //is user previously clicked like button or not
        likeClicked = false;

        itemDetailsLayout = (RelativeLayout)findViewById(R.id.itemDetailsLayout);
        mLayout = (LinearLayout) findViewById(R.id.linearLayout);
        actionLayout = (RelativeLayout) findViewById(R.id.listInfoBarLayout);
        tagActionLayout = (RelativeLayout) findViewById(R.id.listTagBarLayout);
        mainScroll = (ScrollView)findViewById(R.id.mainScrollView);
        autherName = (TextView)findViewById(R.id.auther_textView);
        threadDate = (TextView)findViewById(R.id.date_textView);
        tagEditText = (EditText)findViewById(R.id.tag_editText);
        numberOfLikes = (TextView)findViewById(R.id.numberOfLikes_textView);
        editText =  (EditText)findViewById(R.id.addComment_editText);

        tag1 = (TextView)findViewById(R.id.tag1_textView);
        tag2 = (TextView)findViewById(R.id.tag2_textView);
        tag3 = (TextView)findViewById(R.id.tag3_textView);
        tag4 = (TextView)findViewById(R.id.tag4_textView);
        tag5 = (TextView)findViewById(R.id.tag5_textView);

        likeImageButton = (ImageButton)findViewById(R.id.like_imageButton);

        TextView textView = new TextView(this);

        bundle = getIntent().getExtras();

        strHeaderValue = bundle.getString("strHeader");
        strDetailsValue = bundle.getString("strDetails");
        strRole = bundle.getString("strRole");
        strAuther = bundle.getString("strAuther");
        strDate = bundle.getString("strDate");
        uuid=bundle.getString("uuid");
        channelName=bundle.getString("channelName");
        likes=bundle.getInt("likes");


        List<String> list = bundle.getStringArrayList("tags");
      /*  if(bundle.getInt("like") == 0) {
            likes = 0;
        }
        else{
            likes = bundle.getInt("like");
        }
*/
        if(list!=null){
            strTags = new String[list.size()];
            for(int i=0;i<bundle.getStringArrayList("tags").size();i++){
                strTags[i]=    bundle.getStringArrayList("tags").get(i);
            }
        }

        autherName.setText(strAuther);
        threadDate.setText(strDate);
        numberOfLikes.setText(String.valueOf(likes));

        if(strRole.equals("user")){
            mLayout.setVisibility(View.GONE);
        }


        lblListItemDetails = (TextView)findViewById(R.id.lblListItemDetails);
        lblHeaderItemText = (TextView)findViewById(R.id.lblHeaderItemDetails);

        lblHeaderItemText.setText(strHeaderValue);
        lblListItemDetails.setText(strDetailsValue);

        addcommentTextView = (TextView)findViewById(R.id.addComment_editText);
        onPostClickButton = (Button)findViewById(R.id.postComment_button);



        likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //**************************If user previuosly clicked like button or not******************
                if(!likeClicked){
                    //Service call to like a message

                    likeAMessage(bundle.getString("uuid"));
                    likeImageButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    likes = likes + 1;

                    numberOfLikes.setText(String.valueOf(likes));
                    likeClicked = true;
                    likeImageButton.setClickable(false);
                }
                else{
                    likeImageButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    likeImageButton.setClickable(false);
                }
            }
        });

        onPostClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fetching details from preferences
                mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
                //Fetch the data from shared Preference object
                Gson gson = new Gson();
                mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
                String json = mPrefs.getString("userDetails", "");
                userDetails = gson.fromJson(json, Subject.class);


                mLayout.addView(createNewTextView(addcommentTextView.getText().toString() + System.getProperty("line.separator") + userDetails.getEmail()));
                //mLayout.addView(createNewTextView("-" + strAuther));
                handle_listItemHandle.postDelayed(scrollListItem, 1000);

                //*************************Make service call to persist the reply********************************************
                if(addcommentTextView.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "Post cannot be empty.", Toast.LENGTH_LONG).show();
                }
                else{
                    createMessageForReply();
                }
            }
        });
        addcommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        //********************Binding tags from service to UI***************
        if(strTags!=null) {
            for (int y = 0; y < strTags.length; y++) {
                if (tag1.getText().toString().isEmpty()) {
                    tag1.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                    tag1.setText(strTags[y].toString());
                    tag1.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else if (tag2.getText().toString().isEmpty()) {
                    tag2.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                    tag2.setText(strTags[y].toString());
                    tag2.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else if (tag3.getText().toString().isEmpty()) {
                    tag3.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                    tag3.setText(strTags[y].toString());
                    tag3.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else if (tag4.getText().toString().isEmpty()) {
                    tag4.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                    tag4.setText(strTags[y].toString());
                    tag4.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else {
                    tag5.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                    tag5.setText(strTags[y].toString());
                    tag5.setTextColor(getResources().getColor(R.color.textColorPrimary));
                    tagEditText.setVisibility(View.GONE);
                }
            }
        }

        tagEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            if (tag1.getText().toString().isEmpty() && tagEditText.getText().length() != 0) {
                                tag1.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag1.setText(tagEditText.getText().toString());
                                tag1.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            } else if (tag2.getText().toString().isEmpty() && tagEditText.getText().length() != 0) {
                                tag2.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag2.setText(tagEditText.getText().toString());
                                tag2.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            } else if (tag3.getText().toString().isEmpty() && tagEditText.getText().length() != 0) {
                                tag3.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag3.setText(tagEditText.getText().toString());
                                tag3.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            } else if (tag4.getText().toString().isEmpty() && tagEditText.getText().length() != 0) {
                                tag4.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                tag4.setText(tagEditText.getText().toString());
                                tag4.setTextColor(getResources().getColor(R.color.textColorPrimary));
                            } else {
                                if (tagEditText.getText().length() != 0) {
                                    tag5.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                                    tag5.setText(tagEditText.getText());
                                    tag5.setTextColor(getResources().getColor(R.color.textColorPrimary));
                                    tagEditText.setVisibility(View.GONE);
                                }
                            }

                            //TODO :  decide when to call the below API
                            tagAMessage(bundle.getString("uuid"),tagEditText.getText().toString());

                            tagEditText.setText("");

                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });
    }


    Runnable scrollListItem = new Runnable() {
        @Override
        public void run() {
            mainScroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final TextView textView = new TextView(MCABuddy_ThreadDetails.this);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setBackgroundResource(android.R.drawable.editbox_background_normal);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        return textView;
    }


    final LinearLayout.LayoutParams lactionparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private TextView createAutherTextView(String auther){
        final TextView auther_textview = new TextView(MCABuddy_ThreadDetails.this);
        lactionparams.gravity = Gravity.LEFT;

        auther_textview.setTextSize(10);
        auther_textview.setTextColor(Color.parseColor("#FF4081"));
        auther_textview.setLayoutParams(lactionparams);
        auther_textview.setText(auther);

        return auther_textview;
    }



    private TextView createDateTextView(String date){
        final TextView date_textview = new TextView(MCABuddy_ThreadDetails.this);
        lactionparams.gravity = Gravity.BOTTOM;

        date_textview.setPadding(0, 50, 0, 0);
        date_textview.setTextSize(10);
        date_textview.setTextColor(Color.parseColor("#FF4081"));
        date_textview.setLayoutParams(lactionparams);
        date_textview.setText(date);

        return date_textview;
    }

    private TextView createTagTextView(String tag){
        lactionparams.setMargins(10,200,10,10);

        final TextView tag_textview = new TextView(MCABuddy_ThreadDetails.this);

        tag_textview.setTextSize(10);
        tag_textview.setTextColor(Color.parseColor("#FFFFFF"));
        tag_textview.setBackgroundColor(Color.parseColor("#FF4081"));
        tag_textview.setLayoutParams(lactionparams);
        tag_textview.setText(tag);

        return tag_textview;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_item_details, menu);
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

    @Override
    public void onBackPressed() {
        // Write your code here
        //finish();
        MCABuddy_ThreadDetails.this.finish();
    }

    private void createMessageForReply(){
        //Fetching details from preferences
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //Fetch the data from shared Preference object
        String accessToken = mPrefs.getString("accessToken", "");

        //Fetch the data from shared Preference object

        List<String> categories = new ArrayList<String>();
        Gson gson = new Gson();
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        String json = mPrefs.getString("userDetails", "");
        userDetails = gson.fromJson(json, Subject.class);



        Requester requester = new Requester(accessToken,userDetails.getEmail());
        Message message = new Message();
        message.setMessage(addcommentTextView.getText().toString());
        if(!strHeaderValue.contains("Re: ")){
            message.setTitle("Re: " + strHeaderValue);
        }else{
            message.setTitle(strHeaderValue);
        }

        message.setLikes(0);
        message.setAuthor(userDetails.getEmail());
        message.setChannel(channelName);


        CreateMessage createMessage = new CreateMessage(requester, message);
        Type type = new TypeToken<CreateMessage>() {}.getType();
        String requestJson = gson.toJson(createMessage, type);
        try {
            replyToThread(requestJson,uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reply to thread
     * @param jsonString
     * @param uuid
     */
    private void replyToThread(String jsonString,String uuid) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        HttpEntity entity  = new cz.msebera.android.httpclient.entity.StringEntity(jsonString, ContentType.APPLICATION_JSON);

        client.put(getBaseContext(), Constants.baseURL+Constants.replyToMessagePart1+channelName+Constants.replyToMessagePart2+uuid+Constants.replyToMessagePart3, null,
                entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    // JSON Object
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getString("status").equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Message posted successfully !!", Toast.LENGTH_LONG).show();
                        addcommentTextView.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to post messages at this time, try later!!", Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    /**
     * Reply to thread
     * @param uuid
     */
    private void likeAMessage(String uuid) {
        //fetch details for broadcast
        AsyncHttpClient client = new AsyncHttpClient();
        client.patch(Constants.baseURL + Constants.likeMessagePart1 + uuid + Constants.likeMessagePart2, new AsyncHttpResponseHandler() {

            //client.get(Constants.baseURL + Constants.likeMessagePart1+ uuid+ Constants.likeMessagePart2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    // JSON Object
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getString("status").equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Liked successfully !!", Toast.LENGTH_LONG).show();
                        //check if the user is admin or not
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to add user at this time, try later!!", Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }


    /**
     * Reply to thread
     * @param uuid
     * @param tagText
     */
    private void tagAMessage(String uuid, String tagText) {
        //fetch details for broadcast
        AsyncHttpClient client = new AsyncHttpClient();
        client.patch(Constants.baseURL + Constants.tagMessagePart1+ uuid+ Constants.tagMessagePart2+tagText, new AsyncHttpResponseHandler(){

            //client.get(Constants.baseURL + Constants.likeMessagePart1+ uuid+ Constants.likeMessagePart2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    // JSON Object
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getString("status").equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Tag added successfully !!", Toast.LENGTH_LONG).show();
                        //check if the user is admin or not
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to add tag at this time, try later!!", Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
