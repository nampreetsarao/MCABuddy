package com.example.adminibm.mcabuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.adapter.UserDetailsAdapter;
import com.example.adminibm.mcabuddy.bean.Subject;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.example.adminibm.mcabuddy.helper.WebServiceHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class MCABuddy_SearchUser extends ActionBarActivity {

    private EditText anyText;
    private TextView textView;
    public Button searchUserButton;
    private UserDetailsAdapter userDetailsAdapter=null;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__search_user);
        //fetch the email id from the app
        anyText = (EditText)findViewById(R.id.searchUser_editText);
        textView = (TextView) findViewById(R.id.nothingToDisplay);
        searchUserButton= (Button) findViewById(R.id.searchUser);
        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserByAny();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__search_user, menu);
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

    /**
     * Service to return the user list based on any search provided by user
     *
     */
    public void getUserByAny(){

        anyText = (EditText)findViewById(R.id.searchUser_editText);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Constants.baseURL + Constants.searchUserByAny + anyText.getText().toString(), new AsyncHttpResponseHandler() {
        List<Subject> subjects= new ArrayList<>();

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    JSONObject jsonObject;
                    String jsonResponse = new String(bytes, "UTF-8");
                    jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.getString("status").equals("SUCCESS")) {

                        JSONObject responseObj = null;
                        if(!jsonObject.getString("response").equalsIgnoreCase("null")){

                            for(int k=0;k<jsonObject.getJSONArray("response").length();k++){
                                Subject subject = new Subject();
                                responseObj=(JSONObject)jsonObject.getJSONArray("response").get(k);
                                List<String> roles = new ArrayList<String>();
                                for (int i = 0; i < responseObj.getJSONArray("roles").length(); i++) {
                                    roles.add(responseObj.getJSONArray("roles").getString(i));
                                }

                                List<String> aoe = new ArrayList<String>();
                                //populate expertise
                                if(!responseObj.getString("aoe").equalsIgnoreCase("null")){
                                    for (int i = 0; i < responseObj.getJSONArray("aoe").length(); i++) {
                                        aoe.add(responseObj.getJSONArray("aoe").getString(i));
                                    }
                                }

                                //Setting user profile information in the shared preferences
                                subject.setAoe(aoe);
                                subject.setRoles(roles);
                                subject.setEmail(responseObj.getString("email"));
                                subject.setPhone(responseObj.getString("phone"));
                                subject.setFname(responseObj.getString("fname"));
                                subject.setLname(responseObj.getString("lname"));
                                subjects.add(subject);

                            }
                            userDetailsAdapter = new
                                    UserDetailsAdapter( MCABuddy_SearchUser.this,
                                    R.layout.user_details,subjects);

                            ListView myList = (ListView)
                                    findViewById(R.id.userResultList);
                            myList.setAdapter(userDetailsAdapter);
                            textView.setText("");
                        }else{
                            textView.setText("Nothing to display.");
                            userDetailsAdapter = new
                                    UserDetailsAdapter( MCABuddy_SearchUser.this,
                                    R.layout.user_details,new ArrayList<Subject>());
                            ListView myList = (ListView)
                                    findViewById(R.id.userResultList);
                            myList.setAdapter(userDetailsAdapter);
                        }
                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG).show();
                        //todo add code if required.
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + throwable.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStart() {
                pd = ProgressDialog.show(MCABuddy_SearchUser.this, "", "Fetching results..", false);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    public void onBackPressed() {
        MCABuddy_SearchUser.this.finish();
    }


}
