package it.jaschke.alexandria;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Toty on 11/19/2015.
 * Src: http://www.android4devs.com/2014/12/how-to-make-material-design-navigation-drawer.html
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerAdapterViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private final Context mContext;
    private final DrawerAdapterOnClickHandler mClickHandler;

    final private ItemChoiceManager mICM;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public class DrawerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int holderId;

        TextView textView;
        ImageView imageView;

        // Creating ViewHolder Constructor with View and viewType As a parameter
        public DrawerAdapterViewHolder(View itemView, int ViewType) {
            super(itemView);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.nav_item_textview);
                imageView = (ImageView) itemView.findViewById(R.id.nav_item_imageview);
                holderId = TYPE_ITEM;
            } else {
                holderId = TYPE_HEADER;
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(, "test", Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
            mClickHandler.onClick(position, this);
            mICM.onClick(this);
        }
    }

    public DrawerAdapter(Context context, String[] titles, int[] icons, int choiceMode,
                         DrawerAdapterOnClickHandler dh) {
        mClickHandler = dh;
        mContext = context;
        mNavTitles = titles;
        mIcons = icons;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or
    // else we inflate header.xml if the viewType is TYPE_HEADER and pass it to the view holder

    @Override
    public DrawerAdapter.DrawerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item_row, parent, false);
            DrawerAdapterViewHolder vhItem = new DrawerAdapterViewHolder(v, viewType);
            return vhItem;

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header, parent, false);
            DrawerAdapterViewHolder vhHeader = new DrawerAdapterViewHolder(v, viewType);
            return vhHeader;

        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerAdapter.DrawerAdapterViewHolder holder, int position) {
        if (holder.holderId == TYPE_ITEM) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {
            // Fill any views in the header if there any exists
        }

        mICM.onBindViewHolder(holder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    // With the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public static interface DrawerAdapterOnClickHandler {
        void onClick(int position, DrawerAdapterViewHolder vh);
    }
}

