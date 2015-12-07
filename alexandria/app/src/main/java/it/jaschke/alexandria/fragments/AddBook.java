package it.jaschke.alexandria.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.Utils;
import it.jaschke.alexandria.barcode.BarcodeCaptureActivity;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String SCAN_TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText mEan;
    private final int LOADER_ID = 1;
    private View mRootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    public static final int RC_BARCODE_CAPTURE = 9015;
    private static final String TAG = "BarcodeReader";

    // If the book is already in the library, we don't want it to be deleted when the user presses
    // cancel
    private boolean mBookExists;

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mEan !=null) {
            outState.putString(EAN_CONTENT, mEan.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        TextView appbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_textview);
        appbarTitle.setText(getString(R.string.scan_title));

        mEan = (EditText) mRootView.findViewById(R.id.ean);
        if (savedInstanceState != null) {
            String ean = savedInstanceState.getString(EAN_CONTENT);
            mEan.setText(ean);
        }
        mEan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }

                // Check if the book already exists in the library
                Cursor cursor = getActivity().getContentResolver().query(
                        AlexandriaContract.BookEntry.CONTENT_URI,
                        null,
                        AlexandriaContract.BookEntry._ID + " LIKE ? ",
                        new String[]{ean},
                        null
                );

                if (cursor.moveToFirst()) {
                    mBookExists = true;
                }

                // Once we have an ISBN, start a book intent, only if network is available
                if (Utils.isNetworkAvailable(getActivity())) {
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.putExtra(BookService.EAN, ean);
                    bookIntent.setAction(BookService.FETCH_BOOK);
                    getActivity().startService(bookIntent);
                }

                AddBook.this.restartLoader();
            }
        });

        mRootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch barcode activity.
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        mRootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEan.setText("");
                mBookExists = false;
            }
        });

        mRootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBookExists) {
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.putExtra(BookService.EAN, mEan.getText().toString());
                    bookIntent.setAction(BookService.DELETE_BOOK);
                    getActivity().startService(bookIntent);
                }
                mEan.setText("");
                mEan.setHint(getString(R.string.input_hint));
                mBookExists = false;
            }
        });

        if(savedInstanceState != null) {
            String text = savedInstanceState.getString(EAN_CONTENT);
            mEan.setText(text);
            mEan.setHint(getString(R.string.input_hint));
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mEan.getText().length()==0){
            return null;
        }
        String eanStr= mEan.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            updateEmptyView();
            return;
        }

        mRootView.findViewById(R.id.empty_view).setVisibility(View.GONE);

        // If it's a tablet, load the result data in the card view instead
        if (getActivity().findViewById(R.id.book_detail_container) != null) {
            Bundle args = new Bundle();
            //String ean = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry._ID));
            args.putString(BookDetail.EAN_KEY, mEan.getText().toString());

            BookDetail fragment = new BookDetail();
            fragment.setArguments(args);

            int id = R.id.book_detail_container;
            getFragmentManager().beginTransaction().
                    add(id, fragment, getString(R.string.details_title)).commitAllowingStateLoss();
        } else {

            String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            if (bookTitle != null) {
                ((TextView) mRootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);
            }

            //String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            //((TextView) mRootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            if (authors != null) {
                String[] authorsArr = authors.split(",");
                ((TextView) mRootView.findViewById(R.id.authors)).setLines(authorsArr.length);
                ((TextView) mRootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
            }

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                Glide.with(getActivity())
                        .load(imgUrl)
                        .error(R.drawable.default_cover)
                        .crossFade()
                        .into((ImageView) mRootView.findViewById(R.id.fullBookCover));
            }

            // Setting book describtion
            String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            ((TextView) mRootView.findViewById(R.id.fullBookDesc)).setText(desc);

            // Setting book categories
            String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            if (categories != null) {
                ((TextView) mRootView.findViewById(R.id.categories)).setText(categories);
            }

            mRootView.findViewById(R.id.scan_bookdetails).setVisibility(View.VISIBLE);
        }

        mRootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        if (getActivity().findViewById(R.id.book_detail_container) != null) {
            Fragment fragment = getActivity().getSupportFragmentManager().
                    findFragmentByTag(getString(R.string.details_title));
            if(fragment != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            return;
        }

        mRootView.findViewById(R.id.scan_bookdetails);
        if (mRootView != null) {
            ((TextView) mRootView.findViewById(R.id.fullBookTitle)).setText("");
            //((TextView) mRootView.findViewById(R.id.bookSubTitle)).setText("");
            ((TextView) mRootView.findViewById(R.id.authors)).setText("");
            ((TextView) mRootView.findViewById(R.id.categories)).setText("");
            ((TextView) mRootView.findViewById(R.id.fullBookDesc)).setText("");
            ((ImageView) mRootView.findViewById(R.id.fullBookCover)).setImageResource(R.drawable.default_cover);

            mRootView.findViewById(R.id.scan_bookdetails).setVisibility(View.INVISIBLE);
            mRootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
            mRootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.pref_scan_label);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddBook.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    EditText ean = (EditText) getActivity().findViewById(R.id.ean);
                    if (ean != null) {
                        ean.setText(barcode.displayValue);
                    }
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateEmptyView() {
        TextView emptyView = (TextView) mRootView.findViewById(R.id.empty_view);
        if (emptyView != null) {

            int message = R.string.book_not_found;
            @BookService.AddBookStatus int status = Utils.getAddBookStatus(getActivity());
            switch (status) {
                case BookService.ADD_BOOK_STATUS_SERVER_DOWN:
                    message = R.string.server_down;
                    break;
                case BookService.ADD_BOOK_STATUS_SERVER_INVALID:
                    message = R.string.server_invalid;
                    break;
                default:
                    if (!Utils.isNetworkAvailable(getActivity())) {
                        message = R.string.no_connection;
                    }
            }

            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
            Utils.resetBookStatus(getActivity());
            return;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.add_book_status_key))) {
            updateEmptyView();
        }
    }
}
