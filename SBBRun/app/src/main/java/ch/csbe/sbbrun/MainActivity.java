package ch.csbe.sbbrun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static MainActivity obj;
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 0;

    static SQLiteDatabase db;

    public MainActivity(){

    }

    private Context context;
    public static String et_lugar;

    private static WebView webview;
    private static boolean load = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static String home = "";

    public MainActivity(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


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


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                        final Bundle savedInstanceState) {
            String s = getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER));
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (s.equals("1")) {
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("SBBRun");

                webview = (WebView) rootView.findViewById(R.id.webview);
                webview.loadUrl("http://fahrplan.sbb.ch/bin/query.exe/de");

                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setDomStorageEnabled(true);

                GPSTracker gps = new GPSTracker(MainActivity.obj);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();


                    Geocoder geocoder = new Geocoder(MainActivity.obj, Locale.getDefault());


                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        if (addresses != null) {
                            Address returnedAddress = addresses.get(0);
                            StringBuilder strReturnedAddress = new StringBuilder();
                            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                            }
                            et_lugar = strReturnedAddress.toString();
                        } else {
                            et_lugar = "No Address returned!";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        et_lugar = "Canont get Address!";
                    }


                    Toast.makeText(MainActivity.obj.getApplicationContext(), "Your Location is -\nLat: " + latitude + "\nLong: " + longitude + "\nName: " + et_lugar, Toast.LENGTH_LONG).show();


                } else {
                    gps.showSettingsAlert();
                }

                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        //Toast.makeText(MainActivity.this, "Page Finished", Toast.LENGTH_SHORT).show();

                        final Database dbs = new Database(MainActivity.obj);
                        //final Cursor data = dbs.getData();
                        final String s = dbs.getS();


                        if(s ==  "") {
                            final LayoutInflater inflater = getLayoutInflater();
                            View alertLayout = inflater.inflate(R.layout.home_alert, null);
                            final EditText etUsername = (EditText) alertLayout.findViewById(R.id.et_home);

                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.obj);
                            alert.setTitle("Heimatort angegeben");
                            // this is set the view from XML inside AlertDialog
                            alert.setView(alertLayout);
                            // disallow cancel of AlertDialog on click of back button and outside touch
                            alert.setCancelable(false);
                            alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alert.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    home = etUsername.getText().toString();
                                    if (!home.isEmpty()) {
                                        dbs.onInsert(home);
                                        //onCreateView(inflater, container, savedInstanceState);

                                        if (load == false) {
                                            webview.evaluateJavascript("javascript:{document.getElementsByTagName('body')[0].style.display='none'," +
                                                            "document.formular.REQ0JourneyStopsS0G.value='" + et_lugar + "'," +
                                                            "document.formular.REQ0JourneyStopsZ0G.value='" + s + "'," +
                                                            "document.getElementsByName('changeQueryInputData=yes&start')[0].click();}",
                                                    new ValueCallback<String>() {
                                                        @Override
                                                        public void onReceiveValue(String s) {
                                                            Toast.makeText(MainActivity.obj, "Res Value", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            load = true;
                                        } else {

                                        }

                                        webview.evaluateJavascript("javascript:{document.getElementsByTagName('body')[0].style.display=''," +
                                                        "document.getElementsByClassName('head')[0].style.display='none'," +
                                                        "document.getElementsByClassName('line mainHd')[0].style.display='none'," +
                                                        "document.getElementsByClassName('hac_greybox')[0].style.display='none'," +
                                                        "document.getElementsByClassName('hac_greybox')[4].style.display='none'," +
                                                        "document.getElementsByClassName('hac_greybox')[5].style.display='none'," +
                                                        "document.getElementsByClassName('hac_greybox')[6].style.display='none'," +
                                                        "document.getElementsByClassName('hac_greybox')[7].style.display='none'," +
                                                        "document.getElementsByClassName('mod modIndexPath')[0].style.display='none'," +
                                                        "document.getElementsByClassName('rightCol sbb-1col sbb-col-left-margin')[0].style.display=\"none\"," +
                                                        "document.getElementsByClassName('open openDetails icon_only')[0].click()," +
                                                        "document.getElementsByClassName('open openDetails icon_only')[2].click();}",
                                                new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String s) {

                                                    }
                                                });
                                    }
                                        dialog.cancel();
                                    }

                            });
                            AlertDialog dialog = alert.create();
                            dialog.show();
                        } else if (s != "") {

                                if (load == false) {
                                    webview.evaluateJavascript("javascript:{document.getElementsByTagName('body')[0].style.display='none'," +
                                                    "document.formular.REQ0JourneyStopsS0G.value='" + et_lugar + "'," +
                                                    "document.formular.REQ0JourneyStopsZ0G.value='" + s + "'," +
                                                    "document.getElementsByName('changeQueryInputData=yes&start')[0].click();}",
                                            new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String s) {
                                                    Toast.makeText(MainActivity.obj, "Res Value", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    load = true;
                                } else {

                                }

                                webview.evaluateJavascript("javascript:{document.getElementsByTagName('body')[0].style.display=''," +
                                                "document.getElementsByClassName('head')[0].style.display='none'," +
                                                "document.getElementsByClassName('line mainHd')[0].style.display='none'," +
                                                "document.getElementsByClassName('hac_greybox')[0].style.display='none'," +
                                                "document.getElementsByClassName('hac_greybox')[4].style.display='none'," +
                                                "document.getElementsByClassName('hac_greybox')[5].style.display='none'," +
                                                "document.getElementsByClassName('hac_greybox')[6].style.display='none'," +
                                                "document.getElementsByClassName('hac_greybox')[7].style.display='none'," +
                                                "document.getElementsByClassName('mod modIndexPath')[0].style.display='none'," +
                                                "document.getElementsByClassName('rightCol sbb-1col sbb-col-left-margin')[0].style.display=\"none\"," +
                                                "document.getElementsByClassName('open openDetails icon_only')[0].click()," +
                                                "document.getElementsByClassName('open openDetails icon_only')[2].click();}",
                                        new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String s) {

                                            }
                                        });
                            }
                        }

                    private LayoutInflater getLayoutInflater() {
                        return inflater;
                    }

                });

            } else if (s.equals("2")) {
                rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.textView3);
                textView.setText("Settings");


            } else if (s.equals("3")) {
                rootView = inflater.inflate(R.layout.fragment_about, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.textView);
                textView.setText("About Us");
                TextView about = (TextView) rootView.findViewById(R.id.about);
                about.setText("© 2017 Computerschule Bern AG\n\n" +
                        "Developed by:\n\n" +
                        "Athavan Sanganathapillai\n" +
                        "Programming & GUI Design\n" +
                        "athavan.sanga@csbe.ch\n\n" +
                        "Endrit Lena\n" +
                        "Programming & GUI Design\n" +
                        "endrit_lena@hotmail.com");
            }

            return rootView;
        }

        public static String getHome() {
            return home;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SBBRun";
                case 1:
                    return "Settings";
                case 2:
                    return "About Us";
            }
            return null;
        }
    }
}
