package ch.csbe.sbbrun;

import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static MainActivity obj;
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 0;

    public String et_lugar;

    private WebView webview;
    private boolean load = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obj = this;
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("http://fahrplan.sbb.ch/bin/query.exe/dn?Id=std5.a&");

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            Geocoder geocoder = new Geocoder(this, Locale.getDefault());


            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                    }
                    et_lugar = strReturnedAddress.toString();
                }
                else {
                    et_lugar = "No Address returned!";
                }
            } catch (IOException e) {
                e.printStackTrace();
                et_lugar = "Canont get Address!";
            }



            Toast.makeText(getApplicationContext(), "Your Location is -\nLat: " + latitude + "\nLong: " + longitude + "\nName: " + et_lugar, Toast.LENGTH_LONG).show();


        } else {
            gps.showSettingsAlert();
        }


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Toast.makeText(MainActivity.this, "Page Finished", Toast.LENGTH_SHORT).show();
                if (load == false) {
                    webview.evaluateJavascript("javascript:{document.getElementsByTagName('body')[0].style.display='none'," +
                                    "document.formular.REQ0JourneyStopsS0G.value='" + et_lugar + "'," +
                                    "document.formular.REQ0JourneyStopsZ0G.value='Burgdorf'," +
                                    "document.getElementsByName('changeQueryInputData=yes&start')[0].click();}",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Toast.makeText(MainActivity.this, "Res Value", Toast.LENGTH_SHORT).show();
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

        });

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
            return true;
        }

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
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
            return PlaceholderFragment.newInstance(position + 1);
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
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
