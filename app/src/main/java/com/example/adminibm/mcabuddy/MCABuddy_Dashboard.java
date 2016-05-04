package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MCABuddy_Dashboard extends BaseActivity {

    Point p;
    private Intent backToLogin;
    //Creating a shared preference
    SharedPreferences mPrefs;
    private Bundle bundle;

    private String getRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy_dashboard);
        backToLogin = new Intent(MCABuddy_Dashboard.this, MCABuddy_Login.class);
        //Fetching details from preferences
        mPrefs= getSharedPreferences("user", Context.MODE_PRIVATE);
        //Fetch the data from shared Preference object

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbarmenu, menu);

        /*getMenuInflater().inflate(R.menu.menu_mcabuddy__admin_dashboard, menu);
        return true;*/

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_search:
                Toast.makeText(this, "Search coming soon..", Toast.LENGTH_LONG).show();

                break;
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
                //need to change, should get value from service call
                Intent dashboardIntent= new Intent(MCABuddy_Dashboard.this, MCABuddy_Dashboard.class);
                bundle = new Bundle();

                String loginrole = mPrefs.getString("loginRole", "");
                bundle.putString("loginRole", loginrole);
                dashboardIntent.putExtras(bundle);
                startActivity(dashboardIntent);
                //fetchMessagesForChannel();
                break;
            // action with ID action_add was selected
            case R.id.action_add:

                Intent intent = new Intent(MCABuddy_Dashboard.this, MCABuddy_PostNewMessage.class);

                startActivity(intent);
                break;

            default:
                break;//return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
        /*return super.onOptionsItemSelected(item);*/
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: remove session

                        MCABuddy_Dashboard.this.finish();
                        clearContentsFromSharedPreferences();
                    }
                }).setNegativeButton("No", null).show();

    }


    public void clearContentsFromSharedPreferences(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        settings.edit().clear().commit();

    }
}
