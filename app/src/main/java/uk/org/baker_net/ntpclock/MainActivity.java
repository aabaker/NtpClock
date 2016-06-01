/*
 * Copyright (C) 2016 Adam Baker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.baker_net.ntpclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.TimeZone;

public class MainActivity extends ActionBarActivity {

    SntpClient sntp=new SntpClient();
    boolean offsetValid=false;
    long systemOffset=0; // in milliseconds
    long lastUpdate=0;
    int tzOffset;
    int updateInterval=60;
    boolean keepScreenOn;
    String server="";
    SharedPreferences sharedPref;

    private class NtpTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... x)
        {
            String srv=sharedPref.getString("server1","");
            boolean result=sntp.requestTime(srv, 500);
            if (result)
            {
                server=srv;
                lastUpdate = System.currentTimeMillis();
                systemOffset = sntp.getNtpTime() - System.currentTimeMillis();
                tzOffset = TimeZone.getDefault().getOffset(sntp.getNtpTime());
                offsetValid=true;
            }
            if (!result)
            {
                srv=sharedPref.getString("server2","");
                result=sntp.requestTime(srv, 500);
                if (result)
                {
                    server=srv;
                    lastUpdate = System.currentTimeMillis();
                    systemOffset = sntp.getNtpTime() - System.currentTimeMillis();
                    tzOffset = TimeZone.getDefault().getOffset(sntp.getNtpTime());
                    offsetValid=true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
            {
                TextView serverName = (TextView) findViewById(R.id.serverName);
                serverName.setText(server);
                TextView offset = (TextView) findViewById(R.id.systemOffset);
                offset.setText(String.format("%6.3f", systemOffset / 1000.0));
                TextView roundTrip = (TextView) findViewById(R.id.roundTrip);
                roundTrip.setText(String.format("%6.3f", sntp.getRoundTripTime() / 1000.0));
            }
        }
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis()+systemOffset+tzOffset;
            if (offsetValid) {
                TextView ntpTime = (TextView) findViewById(R.id.ntpTime);
                int secs = (int) ((now / 1000) % 60);
                int mins = (int) ((now / 60 / 1000) % 60);
                int hours = (int) ((now / 60 / 60 / 1000) % 24);
                ntpTime.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
                TextView timeZone = (TextView) findViewById(R.id.timeZone);
                mins = (tzOffset / 60 / 1000) % 60;
                hours = tzOffset / 60 / 60 / 1000;
                timeZone.setText(String.format("%+d:%02d",hours,mins));
            }

            updateInterval=Integer.parseInt(sharedPref.getString("update_interval", ""));

            if (!offsetValid || (System.currentTimeMillis() - lastUpdate > updateInterval*1000)) {
                new NtpTask().execute();
            }

            Boolean keepScreenOnNew=sharedPref.getBoolean("screen_on", true);
            if (keepScreenOnNew && !keepScreenOn)
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else if (!keepScreenOnNew && keepScreenOn)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            keepScreenOn=keepScreenOnNew;

            timerHandler.postDelayed(this, 1000-(now%1000));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        updateInterval=Integer.parseInt(sharedPref.getString("update_interval", "60"));
        keepScreenOn=sharedPref.getBoolean("screen_on",true);
        if (keepScreenOn)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume() {
        super.onPause();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
     public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent p = new Intent(this, SettingsActivity.class);
            startActivity(p);
            return true;
        } else if (id == R.id.action_about) {
            Intent a = new Intent(this, AboutActivity.class);
            startActivity(a);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpdateInterval(int x)
    {
        updateInterval=x;
    }

}
