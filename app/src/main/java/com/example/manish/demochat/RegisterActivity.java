package com.example.manish.demochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private TextInputLayout mDisName;
    private TextInputLayout mEmail;
    private TextInputLayout mPass;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mDisName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPass = (TextInputLayout) findViewById(R.id.password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String displayName = mDisName.getEditText().getText().toString();
                String emailId = mEmail.getEditText().getText().toString();
                String password = mPass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(displayName) ||
                        !TextUtils.isEmpty(emailId) ||
                        !TextUtils.isEmpty(password) ){
                    mRegProgress.setTitle("Registered user");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(displayName, emailId, password);

                }
            }
        });
    }

    private void register_user(final String displayName, String emailId, String password){

        mAuth.createUserWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", displayName);
                    userMap.put("status", "Hi there I am using chap app");
                    userMap.put("image", "dp_image");
                    userMap.put("thumb_image", "dp_image");
                    userMap.put("device_token",device_token);

                    mDatabaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }else{

                            }
                        }
                    });
                }else{
                    System.out.println("Error found here ----------------------->");
                    task.getException().printStackTrace();
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "You  got  error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
