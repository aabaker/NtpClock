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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.support.v4.app.NavUtils;


import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getParent();
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("server1"));
        bindPreferenceSummaryToValue(findPreference("server2"));
        bindPreferenceSummaryToValue(findPreference("update_interval"));
        // TODO how should the screen_on summary be updated
        // bind... needs something that converts to a string and bool doesn't
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            // Range Checks
            // TODO ought to notify MainActivity that this has changed
            if (preference.getKey().equals("update_interval"))
            {
                int intVal=Integer.valueOf((String)value);
                if (intVal < 5) {
                    value = "5";
                    intVal = 5;
                }
                else if (intVal > 3600) {
                    value = "3600";
                    intVal = 3600;
                }
                //main.setUpdateInterval(intVal);
            }

            // Set the summary to the value's simple string representation.
            if (preference.getKey().equals("screen_on")) {
                if (value.equals(true))
                    preference.setSummary("Keep screen awake");
                else
                    preference.setSummary("Allow screen to sleep");
            } else {
                String stringValue = value.toString();
                preference.setSummary(stringValue);
            }

            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}

