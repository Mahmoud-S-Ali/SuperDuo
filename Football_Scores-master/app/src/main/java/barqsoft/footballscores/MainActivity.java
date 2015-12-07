package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.sync.FootballScoresSyncAdapter;

public class MainActivity extends ActionBarActivity
{
    public static int mSelectedMatchId;
    public static int mCurrentFragment = 2;
    private static final String LOG_TAG = "MainActivity";
    private static final String SAVE_TAG = "Save Test";

    private static final String CURRENT_PAGER_KEY = "CURRENT PAGER";
    private static final String SELECTED_MATCH_KEY = "SALECTED MATCH";
    private static final String MAIN_FRAGMENT_KEY = "MAIN FRAGMENT";

    private PagerFragment mMainPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FootballScoresSyncAdapter.initializeSyncAdapter(this);

        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        if (savedInstanceState == null) {
            mMainPagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMainPagerFragment)
                    .commit();
        }
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
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(SAVE_TAG,"will save");
        Log.v(SAVE_TAG,"fragment: "+String.valueOf(mMainPagerFragment.mPagerHandler.getCurrentItem()));
        Log.v(SAVE_TAG, "selected id: " + mSelectedMatchId);

        outState.putInt(CURRENT_PAGER_KEY, mMainPagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt(SELECTED_MATCH_KEY, mSelectedMatchId);
        getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT_KEY, mMainPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(SAVE_TAG,"will retrive");
        Log.v(SAVE_TAG,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(SAVE_TAG,"selected id: "+savedInstanceState.getInt("Selected_match"));

        mCurrentFragment = savedInstanceState.getInt(CURRENT_PAGER_KEY);
        mSelectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_KEY);
        mMainPagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, MAIN_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
