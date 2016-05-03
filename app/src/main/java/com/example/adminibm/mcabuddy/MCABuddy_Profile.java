package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adminibm.mcabuddy.bean.Subject;
import com.google.gson.Gson;

import org.w3c.dom.Text;


public class MCABuddy_Profile extends Activity {

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView role;
    private TextView aoe;
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
        for(String roleValue: userDetails.getRoles()){
            role.append(roleValue+" ");

        }

        aoe = (TextView)findViewById(R.id.ProfileAoe_textView);
        for(String roleValue: userDetails.getAoe()){
            aoe.append(roleValue+" ");
        }

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


}
