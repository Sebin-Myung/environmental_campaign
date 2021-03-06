package com.example.environmentalcampaign.mypage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.environmentalcampaign.cp_info.MyCampaignItem;
import com.example.environmentalcampaign.home.LoginActivity;
import com.example.environmentalcampaign.certification_page.CertificationPage;
import com.example.environmentalcampaign.home.UserAccount;
import com.example.environmentalcampaign.pointmarket.PointMarket;
import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.feed.FeedPage;
import com.example.environmentalcampaign.home.HomeActivity;
import com.example.environmentalcampaign.set_up_page.SetUpCampaignItem;
import com.example.environmentalcampaign.set_up_page.SetUpCampaignPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPage extends AppCompatActivity {

    LinearLayout lo_point, lo_cp_ing, lo_cp_complete, lo_cp_make;
    TextView tv_ing, tv_complete, tv_mycp, tv_point;
    LinearLayout lo_home, lo_make, lo_certi, lo_feed, lo_mypage;
    Button btn_logout;

    ImageView iv_profile;
    TextView tv_nickname;
    String uid;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        iv_profile = (ImageView)findViewById(R.id.iv_profile);
        tv_nickname = (TextView)findViewById(R.id.tv_nickname);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid(); // ?????? ???????????? ???????????? uid ????????????

        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
        databaseReference = database.getReference("environmentalCampaign").child("UserAccount").child(uid); // DB ????????? ??????
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ?????????????????? ????????????????????? ???????????? ???????????? ???
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                Glide.with(MyPage.this).load(userAccount.getProfileImg()).into(iv_profile);
                iv_profile.setBackground(new ShapeDrawable(new OvalShape()));
                iv_profile.setClipToOutline(true);
                tv_nickname.setText(userAccount.getNickName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB??? ???????????? ??? ?????? ?????? ???
                Log.e("MyPageActivity", String.valueOf(error.toException())); //????????? ??????
            }
        });

        tv_ing = (TextView)findViewById(R.id.tv_ing);
        tv_complete = (TextView)findViewById(R.id.tv_complete);
        tv_mycp = (TextView)findViewById(R.id.tv_mycp);

        ArrayList<MyCampaignItem> arrayList1 = new ArrayList<>();
        ArrayList<CompleteCampaignItem> arrayList2 = new ArrayList<>();
        ArrayList<SetUpCampaignItem> arrayList3 = new ArrayList<>();

        database.getReference("environmentalCampaign").child("MyCampaign").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList1.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyCampaignItem myCampaignItem = snapshot.getValue(MyCampaignItem.class);

                    // ???????????? ????????????
                    long now = System.currentTimeMillis();
                    Date mDate = new Date(now);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
                    String today = simpleDate.format(mDate);

                    // ???????????? ????????????
                    if(today.compareTo(myCampaignItem.getEndDate()) <= 0) { arrayList1.add(myCampaignItem); }
                }
                tv_ing.setText(String.valueOf(arrayList1.size()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference("environmentalCampaign").child("CompleteCampaign").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList2.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CompleteCampaignItem completeCampaignItem = snapshot.getValue(CompleteCampaignItem.class);
                    arrayList2.add(completeCampaignItem);
                }
                tv_complete.setText(String.valueOf(arrayList2.size()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference("environmentalCampaign").child("SetUpCampaign").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList3.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SetUpCampaignItem setUpCampaignItem = snapshot.getValue(SetUpCampaignItem.class);
                    arrayList3.add(setUpCampaignItem);
                }
                tv_mycp.setText(String.valueOf(arrayList3.size()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // ????????? ?????? ????????? ??????
        lo_cp_ing = (LinearLayout)findViewById(R.id.lo_cp_ing);
        lo_cp_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CampaignSituation.class);
                intent.putExtra("intent_number", 1);
                startActivity(intent);
            }
        });

        lo_cp_complete = (LinearLayout)findViewById(R.id.lo_cp_complete);
        lo_cp_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CampaignSituation.class);
                intent.putExtra("intent_number", 2);
                startActivity(intent);
            }
        });

        lo_cp_make = (LinearLayout)findViewById(R.id.lo_cp_make);
        lo_cp_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CpMakelist.class);
                startActivity(intent);
            }
        });

        // ????????? ????????????
        tv_point = (TextView)findViewById(R.id.tv_point);
        database.getReference("environmentalCampaign").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Point")&&snapshot.child("Point").hasChild(uid)) {
                    PointItem pointItem = snapshot.child("Point").child(uid).getValue(PointItem.class);
                    tv_point.setText(pointItem.getPoint() + "p");
                } else {
                    PointItem pointItem = new PointItem();
                    pointItem.setUid(uid);
                    pointItem.setPoint(0);
                    database.getReference("environmentalCampaign").child("Point").child(uid).setValue(pointItem);
                    tv_point.setText(thousandsComma(pointItem.getPoint()) + "p");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // ?????? ???????????? ????????? ??? ????????? ???????????? ????????????.
        lo_point = (LinearLayout)findViewById(R.id.lo_point);
        lo_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PointMarket.class);
                startActivity(intent);
            }
        });

        btn_logout = (Button)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // ???????????? ????????? ????????????,
                        mFirebaseAuth.signOut();
                        Toast.makeText(MyPage.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyPage.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // ?????? ??????????????? ???????????????.
                    }
                });
            }
        });

        // ?????? ????????? ????????? ??????

        lo_home = (LinearLayout)findViewById(R.id.lo_home);
        lo_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        lo_make = (LinearLayout)findViewById(R.id.lo_make);
        lo_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetUpCampaignPage.class);
                startActivity(intent);
            }
        });

        lo_certi = (LinearLayout)findViewById(R.id.lo_certi);
        lo_certi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CertificationPage.class);
                startActivity(intent);
            }
        });

        lo_feed = (LinearLayout)findViewById(R.id.lo_feed);
        lo_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedPage.class);
                startActivity(intent);
            }
        });

        lo_mypage = (LinearLayout) findViewById(R.id.lo_mypage);
        lo_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });
    }

    // ??? ???????????? , ???????????? ?????????
    public String thousandsComma(int n) {
        String number = String.valueOf(n);
        String cost = "";
        int count = 0;
        for(int i = number.length()-1; i >= 0; i--) {
            if((count != 0) && ((count%3) == 0))
                cost = "," + cost;
            cost = number.charAt(i) + cost;
            count++;
        }
        return cost;
    }
}