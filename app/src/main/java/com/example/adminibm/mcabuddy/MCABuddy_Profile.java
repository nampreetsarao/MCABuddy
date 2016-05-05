package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import com.example.adminibm.mcabuddy.bean.Subject;
import com.google.gson.Gson;

import org.w3c.dom.Text;


public class MCABuddy_Profile extends Activity {

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView role;
    private TextView aoe;

    public Button changePassword;

    public Button updatePhone;
    Context context;


    //Creating a shared preference
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__profile);

        changePassword = (Button)findViewById(R.id.changepassword_button);

        //***********update phone dialog box*****************
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MCABuddy_Profile.this, MCABuddy_ChangePassword.class);
                startActivity(intent);
            }
        });

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
                                String phone = input.getText().toString();
                                Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_LONG).show();
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
