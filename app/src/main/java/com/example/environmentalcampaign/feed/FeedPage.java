package com.example.environmentalcampaign.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.environmentalcampaign.certification_page.CertificationPage;
import com.example.environmentalcampaign.home.HomeActivity;
import com.example.environmentalcampaign.mypage.MyPage;
import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.set_up_page.SetUpCampaignPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FeedPage extends AppCompatActivity {

    LinearLayout lo_home, lo_make, lo_certi, lo_feed, lo_mypage;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<FeedItem> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView feedRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);

//        // 피드에 recyclerView 삽입

        feedRecyclerView = findViewById(R.id.feedRecyclerView);
        feedRecyclerView.setHasFixedSize(true); // 성능 향상시키기위함
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // 이미지 크기 변경
                lp.width = (getWidth() / getSpanCount())-40;
                lp.height = lp.width;
                return true;
            }
        };
        feedRecyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); //FeedItem 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("environmentalCampaign").child("Feed"); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 반복문으로 데이터 리스트를 추출해냄.
                    FeedItem feedItem = snapshot.getValue(FeedItem.class); //만들어둔 FeedItem 객체를 담는다.
                    arrayList.add(feedItem); // 담은 데이터들을 배열 리스트에 넣고 recyclerview에 보낼 준비를 한다.
                }

                // 내림차순 정렬
                Collections.sort(arrayList, new Comparator<FeedItem>() {
                    @Override
                    public int compare(FeedItem feedItem, FeedItem t1) {
                        return t1.getDate().compareTo(feedItem.getDate());
                    }
                });

                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("FeedPageActivity", String.valueOf(error.toException())); //에러문 출력
            }
        });

        adapter = new FeedAdapter(arrayList, this);
        feedRecyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결


        // 하단 메뉴바 페이지 연동

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

}