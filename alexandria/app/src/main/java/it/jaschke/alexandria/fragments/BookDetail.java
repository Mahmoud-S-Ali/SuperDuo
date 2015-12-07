package it.jaschke.alexandria.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View mRootView;
    private String mEan;
    private String mBookTitle;

    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_full_book, container, false);

        TextView mainToolbarViewText = (TextView) getActivity().findViewById(R.id.toolbar_textview);
        mainToolbarViewText.setText(getString(R.string.details_title));

        Bundle arguments = getArguments();
        if (arguments != null) {
            mEan = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        return mRootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        showMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, mEan);
            bookIntent.setAction(BookService.DELETE_BOOK);
            getActivity().startService(bookIntent);
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(mEan)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        // Setting book title
        mBookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        if (mBookTitle != null) {
            ((TextView) mRootView.findViewById(R.id.fullBookTitle)).setText(mBookTitle);
        }

        //String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        //((TextView) mRootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

        // Setting book describtion
        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        if (desc != null) {
            ((TextView) mRootView.findViewById(R.id.fullBookDesc)).setText(desc);
        }

        // Setting the author name
        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if (authors != null) {
            String[] authorsArr = authors.split(",");
            ((TextView) mRootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) mRootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        }

        //Setting the cover photo
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(getActivity())
                .load(imgUrl)
                .error(R.drawable.default_cover)
                .crossFade()
                .into((ImageView) mRootView.findViewById(R.id.fullBookCover));
        //mRootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);

        // Setting categories
        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        if (categories != null) {
            ((TextView) mRootView.findViewById(R.id.categories)).setText(categories);
        }

        showMenu();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        /*if(MainActivity.IS_TABLET && mRootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }*/
    }

    /*@Override
    public void onResume() {
        super.onResume();
        showMenu();
    }*/



    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+
                " " + mBookTitle + " #Alexandria App");
        return shareIntent;
    }

    private void showMenu() {
        Toolbar toolbarView = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (null != toolbarView) {
            Menu menu = toolbarView.getMenu();
            if (null != menu)
                menu.clear();
            toolbarView.inflateMenu(R.menu.book_detail);
            finishCreatingMenu(toolbarView.getMenu());
        }
    }
}