package com.example.roaddamagedetector;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class UserAdapter extends RealmRecyclerViewAdapter<com.example.roaddamagedetector.User, UserAdapter.ViewHolder> {

    // THIS DEFINES WHAT VIEWS YOU ARE FILLING IN
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameOnRow;
        TextView passwordOnRow;
        TextView tvPath;

        ImageButton imgBtnEdit;
        ImageButton imgBtnDelete;

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize them from the itemView using standard style
            usernameOnRow = itemView.findViewById(R.id.tvUsernameOnRow);
            passwordOnRow = itemView.findViewById(R.id.tvPasswordOnRow);
            imageView = itemView.findViewById(R.id.imageView);

            // initialize the buttons in the layout
            imgBtnEdit = itemView.findViewById(R.id.imgBtnEdit);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
        }
    }

    // IMPORTANT
    // THE CONTAINING ACTIVITY NEEDS TO BE PASSED SO YOU CAN GET THE LayoutInflator(see below)
    AdminActivity activity;

    public UserAdapter(AdminActivity activity, @Nullable OrderedRealmCollection<com.example.roaddamagedetector.User> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // create the raw view for this ViewHolder
        View v = activity.getLayoutInflater().inflate(R.layout.row_user_view_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        // assign view to the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gives you the data object at the given position
        com.example.roaddamagedetector.User u = getItem(position);

        // this will put the image saved to the file system to the imageview
        File getImageDir = activity.getExternalCacheDir();
        if (!u.getPath().equals("")) {
            File file = new File(getImageDir, u.getPath());
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.imageView);
        }

        // copy all the values needed to the appropriate views
        holder.usernameOnRow.setText(u.getName());
        holder.passwordOnRow.setText(u.getPassword());


        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.edit(u);
            }
        });

        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.delete(u);
            }
        });


    }

}

