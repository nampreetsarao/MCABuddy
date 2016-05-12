package com.example.adminibm.mcabuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adminibm.mcabuddy.bean.Subject;
import com.google.gson.Gson;


public class MCABuddy_FlashScreen extends ActionBarActivity {

    public ImageView mcaBuddy_logo;
    Handler handle_MCABuddyFlash = new Handler();
    public Intent MCABuddylogin_Intent;

    //Creating a shared preference
    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__flash_screen);

        //assigning ImageView object to layout ImageView control
        mcaBuddy_logo = (ImageView)findViewById(R.id.MCABuddy_imageView);

        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //fetch the data from shared Preference object
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        Subject userDetails = gson.fromJson(json, Subject.class);
        if(userDetails!=null){
            if(userDetails.getEmail()!=null && !userDetails.getEmail().isEmpty()){
                //creating Intent for Profile activity

                Bundle bundle = new Bundle();
                String role;
                boolean isAdmin=false;
                boolean isUser=false;
                if(userDetails.getRoles()!=null){
                    for(String roles:userDetails.getRoles()){
                        if(roles.equalsIgnoreCase("admin")){
                            isAdmin=true;

                        }else if (roles.equalsIgnoreCase("user")){
                            isUser=true;
                        }
                    }
                }

                if(isUser){
                    bundle.putString("loginRole", "user");
                }

                if(isAdmin ){
                    bundle.putString("loginRole", "admin");
                }

                MCABuddylogin_Intent = new Intent(MCABuddy_FlashScreen.this, MCABuddy_Dashboard.class);
                MCABuddylogin_Intent.putExtras(bundle);

            }
        }else{
            //creating Intent for Profile activity
            MCABuddylogin_Intent = new Intent(MCABuddy_FlashScreen.this, MCABuddy_Login.class);

        }


        //Handler object for redirect after delay
        handle_MCABuddyFlash.postDelayed(MCABuddyRedirect, 2000);
    }

    Runnable MCABuddyRedirect = new Runnable() {
        @Override
        public void run() {
            MoveToLogin();
        }
    };

    public void MoveToLogin(){
        startActivity(MCABuddylogin_Intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__flash_screen, menu);
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
