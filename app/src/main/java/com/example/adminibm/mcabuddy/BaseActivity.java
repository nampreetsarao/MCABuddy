package com.example.adminibm.mcabuddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.adminibm.mcabuddy.bean.Message;
import com.example.adminibm.mcabuddy.helper.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;

    private MenuItem menu;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String emailFlag;
    private String profile_check;

    //trying
    private String[] groupsBroadcast;
    private String[][] childrenBroadcast;

    private String[] groupsInformation;
    private String[][] childrenInformation;


    private String[] groupsSos;
    private String[][] childrenSos;

    private String[] groupsKnowledge;
    private String[][] childrenKnowledge;

    private ProgressDialog pd;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        /**
         * This is going to be our actual root layout.
         */
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        /**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         */
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        /**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.
         */
        super.setContentView(fullLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        pd = ProgressDialog.show(BaseActivity.this, "", "Fetching messages..", false);
        pd.show();
        fetchMessagesForChannel();
        //SystemClock.sleep(4000);

        if (useToolbar()){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else{
            toolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        setUpNavView();

       //To remove selected menuItems on the basis of profile

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the data�
        profile_check = bundle.getString("loginRole");

        if(profile_check.equals("user")){
            hideItem();
            //hideTab();
        }
    }

    private void hideItem(){
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.MCABuddy_AddUser).setVisible(false);
        nav_Menu.findItem(R.id.MCABuddy_ChangeRole).setVisible(false);
        nav_Menu.findItem(R.id.MCABuddy_AddExpertise).setVisible(false);
    }

    private void hideTab(){
        tabLayout.removeTabAt(0);
        tabLayout.removeTabAt(0);
    }

    /**
     * Fetch messages for a channel
     *
     */
    public void fetchMessagesForChannel() {

        //fetch details for broadcast
        AsyncHttpClient client = new AsyncHttpClient();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        client.get(Constants.baseURL+Constants.getMessageForAThreadURLFirstPart+Constants.broadcast+Constants.getMessageForAThreadURLSecondPart, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if(!jsonObject.getString("response").equals("null")) {
                        List<Message> messageList = new ArrayList<Message>();
                        String[] headerStringArray = new String[jsonObject.getJSONArray("response").length()];
                        String[][] bodyStringArray = new String[jsonObject.getJSONArray("response").length()][1];

                        for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                            Message message = new Message();
                            message.setUuid(jsonObject.getJSONArray("response").getJSONObject(i).getString("uuid"));
                            message.setTitle(jsonObject.getJSONArray("response").getJSONObject(i).getString("title"));
                            message.setMessage(jsonObject.getJSONArray("response").getJSONObject(i).getString("message"));
                            message.setAuthor(jsonObject.getJSONArray("response").getJSONObject(i).getString("author"));

                            long tms = Long.parseLong(jsonObject.getJSONArray("response").getJSONObject(i).getString("date"));
                            Date dd = new Date(tms);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            message.setDate(sdf.format(dd).toString());
                            message.setThreadId(jsonObject.getJSONArray("response").getJSONObject(i).getString("threadId"));
                            message.setChannel(jsonObject.getJSONArray("response").getJSONObject(i).getString("channel"));
                            message.setLikes(Integer.parseInt(jsonObject.getJSONArray("response").getJSONObject(i).getString("likes")));
                            List<String> tags = new ArrayList<String>();
                            if(!jsonObject.getJSONArray("response").getJSONObject(i).getString("tags").equalsIgnoreCase("null")){
                                for (int j = 0; j < jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").length(); j++) {
                                    tags.add(jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").getString(j));
                                }
                            }
                            message.setTags(tags);
                            messageList.add(message);
                            message.setUuid(jsonObject.getJSONArray("response").getJSONObject(i).getString("uuid"));
                            headerStringArray[i] = jsonObject.getJSONArray("response").getJSONObject(i).getString("title");
                            bodyStringArray[i][0] = jsonObject.getJSONArray("response").getJSONObject(i).getString("message");

                        }
                        groupsBroadcast = headerStringArray;
                        childrenBroadcast = bodyStringArray;
                        adapter.addFrag(new Broadcast(groupsBroadcast, childrenBroadcast, messageList), "BROADCAST");
                        adapter.notifyDataSetChanged();

                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);


                    }else{

                        adapter.addFrag(new Sos(), "BROADCAST");
                        adapter.notifyDataSetChanged();

                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

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
                String hellp = "";
                throwable.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + e.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


        //fetch details for Information
        AsyncHttpClient clientForInformation = new AsyncHttpClient();
        clientForInformation.get(Constants.baseURL+Constants.getMessageForAThreadURLFirstPart+Constants.information+Constants.getMessageForAThreadURLSecondPart, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String hellp = "";
                throwable.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {


                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if(!jsonObject.getString("response").equals("null")) {
                        List<Message> messageList = new ArrayList<Message>();
                        String[] headerStringArray = new String[jsonObject.getJSONArray("response").length()];
                        String[][] bodyStringArray = new String[jsonObject.getJSONArray("response").length()][1];

                        for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                            Message message = new Message();
                            message.setUuid(jsonObject.getJSONArray("response").getJSONObject(i).getString("uuid"));
                            message.setTitle(jsonObject.getJSONArray("response").getJSONObject(i).getString("title"));
                            message.setMessage(jsonObject.getJSONArray("response").getJSONObject(i).getString("message"));
                            message.setAuthor(jsonObject.getJSONArray("response").getJSONObject(i).getString("author"));
                            message.setThreadId(jsonObject.getJSONArray("response").getJSONObject(i).getString("threadId"));
                            long tms = Long.parseLong(jsonObject.getJSONArray("response").getJSONObject(i).getString("date"));
                            Date dd = new Date(tms);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            message.setDate(sdf.format(dd).toString());

                            message.setChannel(jsonObject.getJSONArray("response").getJSONObject(i).getString("channel"));
                            List<String> tags = new ArrayList<String>();
                            if(!jsonObject.getJSONArray("response").getJSONObject(i).getString("tags").equalsIgnoreCase("null")){
                                for (int j = 0; j < jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").length(); j++) {
                                    tags.add(jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").getString(j));
                                }
                            }
                            message.setTags(tags);
                            messageList.add(message);
                            headerStringArray[i] = jsonObject.getJSONArray("response").getJSONObject(i).getString("title");
                            bodyStringArray[i][0] = jsonObject.getJSONArray("response").getJSONObject(i).getString("message");

                        }
                        groupsInformation = headerStringArray;


                        childrenInformation = bodyStringArray;


                        adapter.addFrag(new Broadcast(groupsInformation, childrenInformation, messageList), "INFORMATION");
                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

                        adapter.notifyDataSetChanged();
                    }else{

                        adapter.addFrag(new Sos(), "INFORMATION");
                        adapter.notifyDataSetChanged();

                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

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
            public void onStart() {
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


        //fetch details for SOS
        AsyncHttpClient clientForSos = new AsyncHttpClient();
        clientForSos.get(Constants.baseURL+Constants.getMessageForAThreadURLFirstPart+Constants.sos+Constants.getMessageForAThreadURLSecondPart, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String hellp = "";
                throwable.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {

                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    if(!jsonObject.getString("response").equals("null")) {
                        List<Message> messageList = new ArrayList<Message>();
                        String[] headerStringArray = new String[jsonObject.getJSONArray("response").length()];
                        String[][] bodyStringArray = new String[jsonObject.getJSONArray("response").length()][1];

                        for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                            Message message = new Message();
                            message.setUuid(jsonObject.getJSONArray("response").getJSONObject(i).getString("uuid"));
                            message.setTitle(jsonObject.getJSONArray("response").getJSONObject(i).getString("title"));
                            message.setMessage(jsonObject.getJSONArray("response").getJSONObject(i).getString("message"));
                            message.setAuthor(jsonObject.getJSONArray("response").getJSONObject(i).getString("author"));
                            message.setThreadId(jsonObject.getJSONArray("response").getJSONObject(i).getString("threadId"));
                            long tms = Long.parseLong(jsonObject.getJSONArray("response").getJSONObject(i).getString("date"));
                            Date dd = new Date(tms);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            message.setDate(sdf.format(dd).toString());

                            message.setChannel(jsonObject.getJSONArray("response").getJSONObject(i).getString("channel"));
                            List<String> tags = new ArrayList<String>();
                            if(!jsonObject.getJSONArray("response").getJSONObject(i).getString("tags").equalsIgnoreCase("null")){
                                for (int j = 0; j < jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").length(); j++) {
                                    tags.add(jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").getString(j));
                                }
                            }

                            message.setTags(tags);
                            messageList.add(message);
                            headerStringArray[i] = jsonObject.getJSONArray("response").getJSONObject(i).getString("title");
                            bodyStringArray[i][0] = jsonObject.getJSONArray("response").getJSONObject(i).getString("message");

                        }
                        groupsSos = headerStringArray;
                        childrenSos = bodyStringArray;


                        adapter.addFrag(new Broadcast(groupsSos, childrenSos, messageList), "SOS");
                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

                        adapter.notifyDataSetChanged();
                    }else{

                        adapter.addFrag(new Sos(), "SOS");
                        adapter.notifyDataSetChanged();

                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

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
            public void onStart() {
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

        //fetch details for Knowledge
        AsyncHttpClient clientForKnowledge = new AsyncHttpClient();
        clientForKnowledge.get(Constants.baseURL+Constants.getMessageForAThreadURLFirstPart+Constants.knowledge+Constants.getMessageForAThreadURLSecondPart, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String hellp = "";
                throwable.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error Occurred - Server returned bad message :" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int code, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                // called when response HTTP status is "200 OK"
                try {

                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);


                    if(!jsonObject.getString("response").equals("null")) {
                        List<Message> messageList = new ArrayList<Message>();
                        String[] headerStringArray = new String[jsonObject.getJSONArray("response").length()];
                        String[][] bodyStringArray = new String[jsonObject.getJSONArray("response").length()][1];

                        for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                            Message message = new Message();
                            message.setUuid(jsonObject.getJSONArray("response").getJSONObject(i).getString("uuid"));
                            message.setTitle(jsonObject.getJSONArray("response").getJSONObject(i).getString("title"));
                            message.setMessage(jsonObject.getJSONArray("response").getJSONObject(i).getString("message"));
                            message.setAuthor(jsonObject.getJSONArray("response").getJSONObject(i).getString("author"));
                            message.setThreadId(jsonObject.getJSONArray("response").getJSONObject(i).getString("threadId"));
                            long tms = Long.parseLong(jsonObject.getJSONArray("response").getJSONObject(i).getString("date"));
                            Date dd = new Date(tms);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            message.setDate(sdf.format(dd).toString());

                            message.setChannel(jsonObject.getJSONArray("response").getJSONObject(i).getString("channel"));
                            List<String> tags = new ArrayList<String>();
                            if(!jsonObject.getJSONArray("response").getJSONObject(i).getString("tags").equalsIgnoreCase("null")){
                                for (int j = 0; j < jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").length(); j++) {
                                    tags.add(jsonObject.getJSONArray("response").getJSONObject(i).getJSONArray("tags").getString(j));
                                }
                            }
                            message.setTags(tags);
                            messageList.add(message);
                            headerStringArray[i] = jsonObject.getJSONArray("response").getJSONObject(i).getString("title");
                            bodyStringArray[i][0] = jsonObject.getJSONArray("response").getJSONObject(i).getString("message");
                        }
                        groupsKnowledge = headerStringArray;
                        childrenKnowledge = bodyStringArray;


                        adapter.addFrag(new Broadcast(groupsKnowledge, childrenKnowledge, messageList), "Knowledge");
                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

                        adapter.notifyDataSetChanged();
                    }else{

                        adapter.addFrag(new Sos(), "Knowledge");
                        adapter.notifyDataSetChanged();

                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

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
            public void onStart() {
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


        pd.dismiss();
        viewPager.setAdapter(adapter);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new Broadcast(), "BROADCAST");
        adapter.addFrag(new Information(), "INFORMATION");
        adapter.addFrag(new Discussion(), "DISCUSSION");
        adapter.addFrag(new Sos(), "SOS");
        viewPager.setAdapter(adapter);
    }



    private void setupMenuItems(Toolbar menutoolbar){

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    /**
     * Helper method that can be used by child classes to
     * specify that they don't want a {@link Toolbar}
     * @return true
     */
    protected boolean useToolbar()
    {
        return true;
    }

    protected void setUpNavView()
    {
        navigationView.setNavigationItemSelectedListener(this);

        if( useDrawerToggle()) { // use the hamburger menu
            drawerToggle = new ActionBarDrawerToggle(this, fullLayout, toolbar,
                    R.string.nav_drawer_opened,
                    R.string.nav_drawer_closed);

            fullLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else if(useToolbar() && getSupportActionBar() != null) {
            // Use home/back button instead
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources()
                    .getDrawable(R.drawable.abc_action_bar_item_background_material));
        }
    }

    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     * @return
     */
    protected boolean useDrawerToggle()
    {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        fullLayout.closeDrawer(GravityCompat.START);
        selectedNavItemId = menuItem.getItemId();

        return onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.MCABuddy_Logout   :
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(BaseActivity.this, MCABuddy_Login.class));
                                clearContentsFromSharedPreferences();
                            }
                        }).setNegativeButton("No", null).show();
                return true;

            case R.id.MCABuddy_profile  :
                startActivity(new Intent(this, MCABuddy_Profile.class));
                return true;

            case R.id.MCABuddy_AddUser :
                startActivity(new Intent(this, MCABuddy_NewUserRegistration.class));
                return true;

            case R.id.MCABuddy_ChangeRole :
                startActivity(new Intent(this, MCABuddy_ChangeRole.class));
                return true;

            case R.id.MCABuddy_AddExpertise :
                startActivity(new Intent(this, MCABuddy_AddExpertise.class));
                return true;

            case R.id.MCABuddy_SearchUser :
                startActivity(new Intent(this, MCABuddy_SearchUser.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearContentsFromSharedPreferences(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        settings.edit().clear().commit();

    }
}