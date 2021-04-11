package com.example.environmentalcampaign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyPage extends AppCompatActivity {

    LinearLayout lo_point;
    TextView tv_home, tv_make, tv_certi, tv_feed, tv_mypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // 보유 포인트를 눌렀을 때 포인트 마켓으로 이동한다.
        lo_point = (LinearLayout)findViewById(R.id.lo_point);
        lo_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PointMarket.class);
                startActivity(intent);
            }
        });

        // 하단 메뉴바 페이지 연동

        tv_home = (TextView)findViewById(R.id.tv_home);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        tv_make = (TextView)findViewById(R.id.tv_make);
        tv_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetUpCampaignPage.class);
                startActivity(intent);
            }
        });

        tv_certi = (TextView)findViewById(R.id.tv_certi);
        tv_certi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CertificationPage.class);
                startActivity(intent);
            }
        });

        tv_feed = (TextView)findViewById(R.id.tv_feed);
        tv_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedPage.class);
                startActivity(intent);
            }
        });

        tv_mypage = (TextView)findViewById(R.id.tv_mypage);
        tv_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });
    }
}