package com.example.roaddamagedetector;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class RDAdapterMyRecords extends RealmRecyclerViewAdapter<RoadDamage, RDAdapterMyRecords.ViewHolder> {

    // THIS DEFINES WHAT VIEWS YOU ARE FILLING IN
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEntryIdRL;
        TextView tvLocationRL;
        TextView tvDateTimeRL;

        ImageView imgVwRDImgRL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize them from the itemView using standard style
            tvEntryIdRL = itemView.findViewById(R.id.tvEntryIdRL);
            tvLocationRL = itemView.findViewById(R.id.tvLocationRL);
            tvDateTimeRL = itemView.findViewById(R.id.tvDateTimeRL);

            // initialize the buttons in the layout
            imgVwRDImgRL = itemView.findViewById(R.id.imgVwRDImgRL);
        }
    }

    // IMPORTANT
    // THE CONTAINING ACTIVITY NEEDS TO BE PASSED SO YOU CAN GET THE LayoutInflator(see below)
    LoggedInActivity activity;

    public RDAdapterMyRecords(LoggedInActivity activity, @Nullable OrderedRealmCollection<RoadDamage> data, boolean autoUpdate) {
//        RealmCollection<RoadDamage> listOfMyRecords = new RealmCollection<RoadDamage>();
//        for (RoadDamage rd : data) {
//            if
//        }
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // create the raw view for this ViewHolder
        View v = activity.getLayoutInflater().inflate(R.layout.row_rd_view_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        // assign view to the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gives you the data object at the given position
        RoadDamage rd = getItem(position);

        // this will put the image saved to the file system to the imageview
        File getImageDir = activity.getExternalCacheDir();
        if (!rd.getPath().equals("")) {
            File file = new File(getImageDir, rd.getPath());
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.imgVwRDImgRL);
        }

        // copy all the values needed to the appropriate views
        holder.tvEntryIdRL.setText(String.valueOf(rd.getEntryId()));
        holder.tvLocationRL.setText(rd.getLocation());
        holder.tvDateTimeRL.setText(rd.getDate());


        holder.imgVwRDImgRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                activity.edit(rd);
                EditActivity_.intent(activity).start();
            }
        });

    }

}


