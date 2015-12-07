package it.jaschke.alexandria;

import android.support.v4.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.fragments.AddBook;
import it.jaschke.alexandria.fragments.BookDetail;
import it.jaschke.alexandria.fragments.ListOfBooks;
import it.jaschke.alexandria.fragments.NavigationDrawerFragment;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    private CharSequence mTitle;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver mMessageReciever;

    public static final String MAIN_MESSAGE_EVENT = "MAIN_MESSAGE_EVENT";
    public static final String MAIN_MESSAGE_KEY = "MAIN_MESSAGE_EXTRA";
    public static final String BARCODE_DETECTED_MESSAGE = "BARCODE DETECTED MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mMessageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MAIN_MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReciever, filter);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (fragmentManager.getFragments().size() == 1) {
            startInitialFragment();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Starting the initial fragment according to the preferred home page
        //startInitialFragment();

        // Set up the drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                mDrawerLayout, toolbar);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (findViewById(R.id.book_detail_container) != null) {
            Fragment fragment = getSupportFragmentManager().
                    findFragmentByTag(getString(R.string.details_title));
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        String fragmentTag;

        switch (position){
            default:
            case 1:
                nextFragment = new ListOfBooks();
                fragmentTag = getString(R.string.library_title);
                break;
            case 2:
                nextFragment = new AddBook();
                fragmentTag = getString(R.string.scan_title);
                break;
            case 3:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
            case 4:
                startActivity(new Intent(this, AboutActivity.class));
                return;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, fragmentTag)
                .addToBackStack((String) mTitle)
                .commit();
    }

    public void setTitle(int titleId) {
        mTitle = getString(titleId);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.book_detail_container) != null){
            id = R.id.book_detail_container;
        }

        FragmentTransaction ft = (FragmentTransaction) getSupportFragmentManager().beginTransaction()
                .replace(id, fragment, getString(R.string.details_title));
        if (id != R.id.book_detail_container) {
            ft.addToBackStack(Utils.getPrefHomePage(getApplicationContext()));
        }
        ft.commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MAIN_MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MAIN_MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        } else {
            String homeFragTag = Utils.getPrefHomePage(this).equals("1")?
                    getString(R.string.library_title) : getString(R.string.scan_title);
            Fragment homeFrag = getSupportFragmentManager().findFragmentByTag(homeFragTag);
            if ((getSupportFragmentManager().getBackStackEntryCount()) < 2
                    || (homeFrag != null && homeFrag.isVisible())) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void startInitialFragment() {
        String preferredHomePage = Utils.getPrefHomePage(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment initialFragment;
        String fragmentTag;

        if (preferredHomePage.equals("1")) {
            initialFragment = new ListOfBooks();
            fragmentTag = getString(R.string.library_title);
        } else {
            initialFragment = new AddBook();
            fragmentTag = getString(R.string.scan_title);
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, initialFragment, fragmentTag)
                .addToBackStack((String) mTitle)
                .commit();
    }
}