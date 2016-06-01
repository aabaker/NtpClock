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

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Date;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        TextView aboutBuild = (TextView) findViewById(R.id.about_build);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            aboutBuild.setText("Version: " + pInfo.versionName + "\nBuilt: " + buildDate.toString());
        }
        catch(PackageManager.NameNotFoundException e) {
            aboutBuild.setText("Version: Unknown\nBuilt: " + buildDate.toString());
        }
    }
}
