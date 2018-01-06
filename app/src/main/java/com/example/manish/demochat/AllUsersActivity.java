package com.example.manish.demochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView allUserList;
    DatabaseReference ref;
    //CircleImageView displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = (Toolbar) findViewById(R.id.alluser_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //displayImage = (CircleImageView) findViewById(R.id.settings_image);

        allUserList = (ListView) findViewById(R.id.allusers_list);
        ref = FirebaseDatabase.getInstance().getReference().child("Users");


        FirebaseListAdapter mAdapter = new FirebaseListAdapter<Users>(this, Users.class, R.layout.single_profile_layout, ref) {
            @Override
            protected void populateView(View view, Users users, int position) {
                ((TextView)view.findViewById(R.id.single_profile_name)).setText(users.getName());
                ((TextView)view.findViewById(R.id.single_profile_status)).setText(users.getStatus());
                CircleImageView displayImage = (CircleImageView) view.findViewById(R.id.circular_image_user);
                Picasso.with(AllUsersActivity.this).load(users.getThumb_image()).into(displayImage);
                //Toast.makeText(AllUsersActivity.this, users.getImage(), Toast.LENGTH_SHORT).show();

                final String user_id = getRef(position).getKey();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }
                });
            }
        };
        allUserList.setAdapter(mAdapter);

    }
}
