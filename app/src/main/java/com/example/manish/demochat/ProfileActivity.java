package com.example.manish.demochat;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn,mDeclineBtn;

    private Toolbar profileToolbar;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mListFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser mUser;

 //   private DatabaseReference mUserRef;
   // private FirebaseAuth mAuth;

//    private ProgressDialog mProgress;

    private Toolbar toolbar;

    // 0 : Not Friend, send Friend request
    // 1 : Request in process
    // 2 : Friend Request Send Successfully
    // 3 : Cancel Frnd Request
    // 4 : request received
    // 5 : Friends
    // 6 : UnFriend
    private int current_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_uid = getIntent().getStringExtra("user_id");
//        textView = (TextView) findViewById(R.id.);
//        textView.setText(str);

        toolbar = (Toolbar) findViewById(R.id.profile_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mListFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_frnd_cnt);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button)findViewById(R.id.profile_decline_frnd_req);

//        mProgress = new ProgressDialog(this);
//        mProgress.setTitle("Loading User Data");
//        mProgress.setCanceledOnTouchOutside(false);
//        mProgress.show();

        current_state = 0; //
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.common_google_signin_btn_icon_light).into(mProfileImage);

                //Toast.makeText(ProfileActivity.this, mUser.getUid(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(ProfileActivity.this, user_uid, Toast.LENGTH_SHORT).show();
                // Friend list/request feature

                mFriendReqDatabase.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_uid)){
                            String req_type = dataSnapshot.child(user_uid).child("request_type").getValue().toString();
                            Toast.makeText(ProfileActivity.this, req_type, Toast.LENGTH_SHORT).show();
                            if(req_type.equals("recv")){
                                current_state = 4;
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);
                            }else if(req_type.equals("sent")){
                                current_state = 2;
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }else{
                            mListFriendDatabase.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_uid)){
                                        current_state = 5;
                                        mProfileSendReqBtn.setText("Unfriend :(");
                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                //mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendReqBtn.setEnabled(false);
                // Not friend -> Friend Request sent
                if(current_state == 0){

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_uid).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String,String> notificatonData = new HashMap<>();
                    notificatonData.put("from",mUser.getUid());
                    notificatonData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/"+mUser.getUid()+"/"+user_uid+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_uid+"/"+mUser.getUid()+"/request_type","recv");
                    requestMap.put("notifications/"+user_uid+"/"+newNotificationId,notificatonData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Toast.makeText(ProfileActivity.this,"There was some error sending request",Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                            current_state = 2;
                            mProfileSendReqBtn.setText("CANCEL FRIEND REQUEST");
                        }
                    });
                }

                // Friend request sent --> Cancel Friend Request
                if(current_state == 2){


                    mFriendReqDatabase.child(mUser.getUid()).child(user_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_uid).child(mUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    current_state = 0;
                                    mProfileSendReqBtn.setText("SEND FRIEND REQUEST");
                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                // request received -> request accept
                if(current_state == 4){
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsMap = new HashMap();
                    //SimpleDateFormat
                    friendsMap.put("Friends/"+mUser.getUid()+"/"+user_uid+"/date",current_date);
                    friendsMap.put("Friends/"+user_uid+"/"+mUser.getUid()+"/date",current_date);

                    friendsMap.put("Friend_req/"+mUser.getUid()+"/"+user_uid,null);
                    friendsMap.put("Friend_req/"+user_uid+"/"+mUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){
                                mProfileSendReqBtn.setEnabled(true);
                                current_state = 5;
                                mProfileSendReqBtn.setText("UNFRIEND :(");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                            else{
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }


                //unfriend

                if(current_state == 5){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+mUser.getUid()+"/"+user_uid,null);
                    unfriendMap.put("Friends/"+user_uid+"/"+mUser.getUid(),null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                mProfileSendReqBtn.setEnabled(true);
                                current_state = 0;
                                mProfileSendReqBtn.setText("SEND FRIEND REQUEST");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                            else{
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }

            }
        });


    }
}
