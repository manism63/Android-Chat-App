package com.example.manish.demochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar = (Toolbar) findViewById(R.id.users_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = (RecyclerView) findViewById(R.id.users_list_1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toast.makeText(UsersActivity.this, "onStart running", Toast.LENGTH_LONG).show();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> mAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                userDatabase
        ) {
            @Override
            public void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                Toast.makeText(UsersActivity.this, model.getStatus(), Toast.LENGTH_LONG).show();
                Toast.makeText(UsersActivity.this, model.getName() + ":" + position, Toast.LENGTH_LONG).show();
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage());
            }
        };
        recyclerView.setAdapter(mAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }
        public void setName(String name){
            TextView userNameView = (TextView) mview.findViewById(R.id.users_single_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            TextView statusView = (TextView) mview.findViewById(R.id.users_single_status);
            statusView.setText(status);
        }
        public void setImage(String status){
            //TextView statusView = (TextView) mview.findViewById(R.id.users_single_status);
            //statusView.setText(status);
        }
    }
}
