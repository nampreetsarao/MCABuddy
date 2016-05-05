package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MCABuddy_ChangePassword extends Activity {

    public TextView listHeader;
    public EditText oldPassword;
    public EditText newPassword;
    public EditText confirmNewPassword;
    public Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__change_password);

        listHeader = (TextView) findViewById(R.id.lblListHeader);
        submitButton = (Button) findViewById(R.id.submitPassword_button);

        registerViews();
    }


    private void registerViews() {
        oldPassword = (EditText) findViewById(R.id.oldPassword_editText);
        // TextWatcher would let us check validation error on the fly
        oldPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(oldPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        newPassword = (EditText) findViewById(R.id.newPassword_edittext);
        // TextWatcher would let us check validation error on the fly
        newPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(newPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        confirmNewPassword = (EditText)findViewById(R.id.confirmnewpassword_edittext);
        confirmNewPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.hasText(confirmNewPassword);
                Validations.compareText(newPassword, confirmNewPassword);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

/*        accesstoken = (EditText) findViewById(R.id.accesstoken_edittext);
        accesstoken.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validations.isPhoneNumber(accesstoken, false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });*/



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    Toast.makeText(getApplicationContext(),"Form Pass", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Form Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(oldPassword)) ret = false;
        if (!Validations.hasText(newPassword)) ret = false;
        if (!Validations.hasText(confirmNewPassword)) ret = false;
        if (!Validations.compareText(newPassword, confirmNewPassword)) ret = false;
        //if (!Validations.isPhoneNumber(accesstoken, false)) ret = false;

        return ret;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__change_password, menu);
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
