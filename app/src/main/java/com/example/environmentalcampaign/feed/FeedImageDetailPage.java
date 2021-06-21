package com.example.environmentalcampaign.feed;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.cp_info.CampaignInformation;
import com.example.environmentalcampaign.cp_info.CampaignItem;
import com.example.environmentalcampaign.home.UserAccount;
import com.example.environmentalcampaign.mypage.MyPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedImageDetailPage extends AppCompatActivity {

    ImageButton bt_back;

    ImageView iv_profile, iv_feed, feed_heart, feed_comment, iv_declaration;

    TextView tv_nickname, tv_date, tv_contents, feed_heart_text, feed_declaration_text;

    public Boolean heartButtonPush;
    int heartN;

    public Boolean warningButtonPush;
    int warningN;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, databaseReference1, wDatabaseRef;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseRef;

    String feedDate, feedPublisher, uid;

    FeedItem feedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_image_detail_page);

        iv_profile = (ImageView)findViewById(R.id.iv_profile);
        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_date = (TextView)findViewById(R.id.tv_date);
        iv_feed = (ImageView)findViewById(R.id.iv_feed);
        tv_contents = (TextView)findViewById(R.id.tv_contents);
        feed_heart = (ImageView)findViewById(R.id.feed_heart);
        feed_heart_text = (TextView)findViewById(R.id.feed_heart_text);
        feed_comment = (ImageView)findViewById(R.id.feed_comment);
        iv_declaration = (ImageView)findViewById(R.id.iv_declaration);
        feed_declaration_text = (TextView)findViewById(R.id.feed_declaration_text);

        Intent intent = getIntent();
        feedDate = intent.getStringExtra("FeedDate");
        feedPublisher = intent.getStringExtra("FeedPublisher");

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        wDatabaseRef = FirebaseDatabase.getInstance().getReference("environmentalCampaign").child("Warning");

        // 게시자의 사진과 닉네임을 가져온다.
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("environmentalCampaign").child("UserAccount").child(feedPublisher); // DB 테이블 연결
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                Glide.with(FeedImageDetailPage.this).load(userAccount.getProfileImg()).into(iv_profile);
                iv_profile.setBackground(new ShapeDrawable(new OvalShape()));
                iv_profile.setClipToOutline(true);
                tv_nickname.setText(userAccount.getNickName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("MyPageActivity", String.valueOf(error.toException())); //에러문 출력
            }
        });

        // 작성한 날짜 가져오기
        tv_date.setText(feedDate);

        databaseReference1 = database.getReference("environmentalCampaign").child("Feed").child(feedDate);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                feedItem = snapshot.getValue(FeedItem.class);

                // 이미지 붙이기
                Glide.with(FeedImageDetailPage.this).load(feedItem.getImage()).into(iv_feed);
                iv_feed.setVisibility(View.VISIBLE);
                // 인증 내용 불러오기
                tv_contents.setText(feedItem.getContents());

                // 좋아요 개수 불러오기
                feed_heart_text.setText("좋아요 "+feedItem.getHeartN()+"개");
                heartN = feedItem.getHeartN();

                heartButtonPush = feedItem.heartTotalN.containsKey(uid);
                if(heartButtonPush == true){
                    feed_heart.setImageResource(R.drawable.full_heart);
                }else {
                    feed_heart.setImageResource(R.drawable.empty_heart);
                }

                // 경고 개수 불러오기
                feed_declaration_text.setText("신고 "+feedItem.getWarningN()+"개");
                warningN = feedItem.getWarningN();

                warningButtonPush = feedItem.warningTotalN.containsKey(uid);
                if(warningButtonPush == true){
                    iv_declaration.setImageResource(R.drawable.alert_click);
                }else {
                    iv_declaration.setImageResource(R.drawable.alert_unclick);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("FeedImageDetailPage", String.valueOf(error.toException())); //에러문 출력
            }
        });

        feed_heart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (heartButtonPush == true){
                    feed_heart.setImageResource(R.drawable.empty_heart);
                    feed_heart_text.setText("좋아요 "+heartN+"개");
                    onHeartClicked(databaseReference1);
                }else{
                    feed_heart.setImageResource(R.drawable.full_heart);
                    feed_heart_text.setText("좋아요 "+heartN+"개");
                    onHeartClicked(databaseReference1);
                }
            }
        });

        // 경고 이미지 누르면
        iv_declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (warningButtonPush == true){
                    iv_declaration.setImageResource(R.drawable.alert_unclick);
                    feed_declaration_text.setText("신고 "+warningN+"개");
                    onWarningClicked(databaseReference1);
                }else{
                    iv_declaration.setImageResource(R.drawable.alert_click);
                    feed_declaration_text.setText("신고 "+warningN+"개");
                    onWarningClicked(databaseReference1);
                    showDialog();
                }
            }
        });

        // 댓글달기 화면으로 이동
        feed_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(FeedImageDetailPage.this, FeedCommentActivity.class);
                commentIntent.putExtra("commentFeedDate", feedDate);
                commentIntent.putExtra("commentFeedPublisher", feedPublisher);
                startActivity(commentIntent);
            }
        });

        // 뒤로가기 버튼 이벤트
        bt_back = (ImageButton)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void onHeartClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                FeedItem heartfeedItem = mutableData.getValue(FeedItem.class);
                if (heartfeedItem == null) {
                    return Transaction.success(mutableData);
                }

                if (heartButtonPush) {
                    // Unstar the post and remove self from stars
                    heartN = heartfeedItem.getHeartN();
                    heartfeedItem.setHeartN( heartN - 1);
                    heartfeedItem.heartTotalN.remove(uid);
                } else {
                    // Star the post and add self to stars
                    heartN = heartfeedItem.getHeartN();
                    heartfeedItem.setHeartN( heartN + 1);
                    heartfeedItem.heartTotalN.put(uid, true);
                }

                // Set value and report transaction success
                mutableData.setValue(heartfeedItem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
            }
        });
    }

    private void onWarningClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                FeedItem warningfeedItem = mutableData.getValue(FeedItem.class);
                if (warningfeedItem == null) {
                    return Transaction.success(mutableData);
                }

                if (warningButtonPush) {
                    // Unstar the post and remove self from stars
                    warningN = warningfeedItem.getWarningN();
                    warningfeedItem.setWarningN( warningN - 1);
                    warningfeedItem.warningTotalN.remove(uid);
                } else {
                    // Star the post and add self to stars
                    warningN = warningfeedItem.getWarningN();
                    warningfeedItem.setWarningN( warningN + 1);
                    warningfeedItem.warningTotalN.put(uid, true);
                }

                // Set value and report transaction success
                mutableData.setValue(warningfeedItem);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
            }
        });
    }

    private void showDialog() {

        final String[] warningReason = new String[1];

        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(FeedImageDetailPage.this);
        msgBuilder.setTitle("신고하기");

        final String[] versionArray = new String[] {"다른 캠페인 인증","인증 조건 맞지않음","환경 캠페인과 무관한 사진"};
        msgBuilder.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                warningReason[0] = versionArray[i];
            }
        });

        msgBuilder.setPositiveButton("신고하기", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {

                Warning warning = new Warning();
                warning.setFeedPublisher(feedPublisher);
                warning.setFeedDate(feedDate);
                warning.setWarningReason(warningReason[0]);

                // 현재시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String getTime = simpleDate.format(mDate);

                wDatabaseRef.child(feedPublisher).child(feedDate).child(getTime).setValue(warning);

                Toast.makeText(FeedImageDetailPage.this, "신고를 완료하였습니다.", Toast.LENGTH_SHORT).show();


            }
        });
        msgBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(FeedImageDetailPage.this, "신고를 취소합니다.", Toast.LENGTH_SHORT).show();
                warningButtonPush = true;
                iv_declaration.setImageResource(R.drawable.alert_unclick);
                onWarningClicked(databaseReference1);
                feed_declaration_text.setText("신고 "+warningN+"개");
            }
        });

        msgBuilder.show();
    }


}