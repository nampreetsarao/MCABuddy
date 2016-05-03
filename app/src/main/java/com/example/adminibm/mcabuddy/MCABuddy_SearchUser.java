package com.example.adminibm.mcabuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

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


    private EditText email;
    private List<Subject> subjects= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__search_user);
        //fetch the email id from the app
        email = (EditText)findViewById(R.id.searchUser_editText);

        //calling the API: Get User by Email
         getUserByAny("nampreet");
        if(subjects==null){
            //// TODO: 4/28/2016 report error 
        }else{
            //// TODO: 4/28/2016 pass this value to new profile screen to display the user details. 
        }
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


    public void getUserByAny(String any){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Constants.baseURL+Constants.searchUserByAny+any, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    JSONObject jsonObject;
                    String jsonResponse = new String(bytes, "UTF-8");
                    jsonObject = new JSONObject(jsonResponse);
                    if (jsonObject.getString("status").equals("SUCCESS")) {

                        //Initialize the user details object: Subject
                        Subject subject = new Subject();
                        //JSONObject jsonObject= jsonObject.getJSONObject("response");

                        /*for(int k=0;j<jsonObject.getlength();k++){
                            List<String> roles = new ArrayList<String>();
                            //Fetch and populate user roles
                            for (int i = 0; i < jsonArray.get(k).getJSONArray("roles").length(); i++) {
                                roles.add(responseObj.getJSONArray("roles").getString(i));
                            }

                            //Fetch and populate expertise
                            List<String> aoe = new ArrayList<String>();
                            for (int i = 0; i < responseObj.getJSONArray("aoe").length(); i++) {
                                aoe.add(responseObj.getJSONArray("aoe").getString(i));
                            }

                            //setting user profile information in the shared preferences
                            subject.setAoe(aoe);
                            subject.setRoles(roles);
                            subject.setEmail(responseObj.getString("email"));
                            subject.setPhone(responseObj.getString("phone"));
                            subject.setFname(responseObj.getString("fname"));
                            subject.setLname(responseObj.getString("lname"));
                            subjects.add(subject);
                        }*/

                    }else{

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
                //Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + e.toString(), Toast.LENGTH_LONG).show();

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
