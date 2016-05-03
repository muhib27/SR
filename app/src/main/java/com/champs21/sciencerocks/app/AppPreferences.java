package com.champs21.sciencerocks.app;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.champs21.sciencerocks.R;

/**
 * Created by BLACK HAT on 19-Apr-16.
 */
public class AppPreferences extends PreferenceActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final CheckBoxPreference checkBoxPrefSkin1 = (CheckBoxPreference) findPreference("pref_key_skin1");
            checkBoxPrefSkin1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if(checkBoxPrefSkin1.isChecked()){
                        ApplicationSingleton.getInstance().savePrefBoolean("pref_skin1", true);
                    }
                    else{
                        ApplicationSingleton.getInstance().savePrefBoolean("pref_skin1", false);
                    }



                    return true;
                }
            });
        }
    }
}
