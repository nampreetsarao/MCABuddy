package com.example.adminibm.mcabuddy;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


public class MCABuddy_Credits extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcabuddy__credits);

        final Animation animAccelerateDecelerate = AnimationUtils.loadAnimation(this, R.anim.accelerate_decelerate);
        AlphaAnimation fadeout = new AlphaAnimation(1.0f, 0.0f);

        final TextView credit_tanmay_ambre_textView = (TextView) findViewById(R.id.credit_tanmay_ambre);
        final TextView credit_nampreet_singh_textView = (TextView) findViewById(R.id.credit_nampreet_singh);
        final TextView credit_girish_fuluskar_textView = (TextView) findViewById(R.id.credit_girish_fuluskar);

        credit_tanmay_ambre_textView.startAnimation(animAccelerateDecelerate);
        credit_nampreet_singh_textView.startAnimation(animAccelerateDecelerate);
        credit_girish_fuluskar_textView.startAnimation(animAccelerateDecelerate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mcabuddy__credits, menu);
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
