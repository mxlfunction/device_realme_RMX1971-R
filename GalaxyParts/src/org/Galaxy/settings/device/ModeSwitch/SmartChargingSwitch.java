/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.Galaxy.settings.device.ModeSwitch;

import android.app.Service;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;
import android.os.UserHandle;
import android.util.Log;

import org.Galaxy.settings.device.GalaxyParts;
import org.Galaxy.settings.device.Utils;

public class SmartChargingSwitch implements OnPreferenceChangeListener {

    private static Context mContext;

    public SmartChargingSwitch(Context context) {
        mContext = context;
    }

    private static final String FILE = "/sys/class/power_supply/battery/mmi_charging_enable";

    public static String getFile() {
        if (Utils.fileWritable(FILE)) {
            return FILE;
        }
        return null;
    }

    public static boolean isSupported() {
        return Utils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBoolean(getFile(), false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Intent SmartChargingSVC = new Intent(mContext, org.Galaxy.settings.device.SmartChargingService.class);
        if (enabled) {
            mContext.startServiceAsUser(SmartChargingSVC, UserHandle.CURRENT);
            GalaxyParts.mSeekBarPreference.setEnabled(true);
            GalaxyParts.mResetStats.setEnabled(true);
            Log.d("GalaxyParts", "Starting SmartChargingSVC");
        } else {
            mContext.stopServiceAsUser(SmartChargingSVC, UserHandle.CURRENT);
            GalaxyParts.mSeekBarPreference.setEnabled(false);
            GalaxyParts.mResetStats.setEnabled(false);
            Utils.writeValue(FILE, "1");
            Log.d("GalaxyParts", "Stopping SmartChargingSVC");
        }
        //Utils.writeValue(getFile(), enabled ? "1" : "0");
        return true;
    }
}
