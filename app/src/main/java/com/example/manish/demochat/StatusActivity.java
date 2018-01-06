package com.example.manish.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout statusf;
    private Button btnf;

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    //private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String status_value = getIntent().getStringExtra("status_value");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusf = (TextInputLayout) findViewById(R.id.status_input);
        statusf.getEditText().setText(status_value);
        btnf = (Button) findViewById(R.id.save_status);


        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_LONG).show();


        btnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                progressDialog = new ProgressDialog(StatusActivity.this);
//                progressDialog.setTitle("Saving Changes");
//                progressDialog.setMessage("Please wait we save the changes");
//                progressDialog.show();


                String status = statusf.getEditText().getText().toString();

                //Toast.makeText(getApplicationContext(), "Came here", Toast.LENGTH_LONG).show();

                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Came here", Toast.LENGTH_LONG).show();
                        if(task.isSuccessful()){
                            //progressDialog.dismiss();
                            Intent intent = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_LONG).show();
                        }
                    }
                });




            }
        });


    }
}
