package ch.csbe.sbbrun;


import android.content.SharedPreferences;
/**
 * Created by Endrit on 06.04.2017.
 */

public class SharePreferences {

    /*
        Dies ist die Klasse in der, der Heimatort Intern gespeichert wird.
         */

    public static final int SETTINGS_MODE = 0;
    public static final String SETTINGS_FILE = "HomeSettings"; //Dies ist die Dateiname
    public SharedPreferences Settings;
    public SharedPreferences.Editor SettingsEditor;

    //Hier werden Daten herausgeholt
    public String getPlace(String key){
        return Settings.getString(key,"empty");
    }

    //Hier werden Daten gespeichert
    public void setPlace(String key, String value) {
        SettingsEditor.putString(key, value);
        SettingsEditor.commit();
    }

}
