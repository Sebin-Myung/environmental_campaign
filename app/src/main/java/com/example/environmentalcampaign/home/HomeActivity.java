package com.example.environmentalcampaign.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.environmentalcampaign.certification_page.CertificationPage;
import com.example.environmentalcampaign.cp_info.CampaignItem;
import com.example.environmentalcampaign.mypage.MyPage;
import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.search_page.SearchPage;
import com.example.environmentalcampaign.set_up_page.SetUpCampaignPage;
import com.example.environmentalcampaign.bookmark.BookMark;
import com.example.environmentalcampaign.feed.FeedPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView tv_search;
    ImageView bookmark;
    TextView tv_make, tv_certi, tv_feed, tv_mypage;

    ViewPager2 viewPager2;
    LinearLayout layoutIndicator;

    private RecyclerView.Adapter fAdapter, nAdapter;
    private  RecyclerView.LayoutManager fLayoutManager, nLayoutManager;
    private RecyclerView fRecyclerView, nRecyclerView;
    public ArrayList<RecyclerViewItem> arrayList, fArrayList, nArrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

//    ArrayList<String> campaignCodes;
//    ArrayList<CampaignItem> campaignItems;

   // TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager2 = findViewById(R.id.viewPager2);
        layoutIndicator = findViewById(R.id.layoutIndicators);

        ArrayList<DataPage> list = new ArrayList<>();
        list.add(new DataPage(ContextCompat.getDrawable(this, R.drawable.tumbler_campaign)));
        list.add(new DataPage(ContextCompat.getDrawable(this, R.drawable.zero_waste)));
        list.add(new DataPage(ContextCompat.getDrawable(this, R.drawable.world_eco_day)));

        viewPager2.setAdapter(new ViewPagerAdapter(list));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        setupIndicators(list.size());


        // RecyclerView 연결

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("environmentalCampaign").child("HomeCampaign");

        arrayList = new ArrayList<>(); // RecyclerViewItem 객체를 담을 ArrayList

        fRecyclerView = findViewById(R.id.famousCampaign);
        fRecyclerView.setHasFixedSize(true); // 성능 향상시키기위함
        fLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        fRecyclerView.setLayoutManager(fLayoutManager);
        fArrayList = new ArrayList<>(); // 인기캠페인 RecyclerViewItem 객체를 담을 ArrayList

        nRecyclerView = findViewById(R.id.newCampaign);
        nRecyclerView.setHasFixedSize(true); // 성능 향상시키기위함
        nLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        nRecyclerView.setLayoutManager(nLayoutManager);
        nArrayList = new ArrayList<>(); // 신규캠페인 RecyclerViewItem 객체를 담을 ArrayList

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecyclerViewItem recyclerViewItem = snapshot.getValue(RecyclerViewItem.class);
                    arrayList.add(recyclerViewItem);
                }

                // 내림차순 정렬
                Collections.sort(arrayList, new Comparator<RecyclerViewItem>() {
                    @Override
                    public int compare(RecyclerViewItem recyclerViewItem, RecyclerViewItem t1) {
                        // reCampaignN을 기준으로 내림차순 정렬
                        if(recyclerViewItem.getReCampaignN() < t1.getReCampaignN()) {
                            return 1; // -1로 하면 오름차순
                        } else if(recyclerViewItem.getReCampaignN() == t1.getReCampaignN()) {
                            return 0;
                        } else {
                            return -1; // 1로 하면 오름차순
                        }
                    }
                });
                fArrayList.clear();
                // 인기캠페인 가져오기
                for(int i=0; i < 2; i++) {
                    RecyclerViewItem recyclerViewItem = arrayList.get(i);
                    fArrayList.add(recyclerViewItem);
                }
                fAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침

                // 내림차순 정렬
                Collections.sort(arrayList, new Comparator<RecyclerViewItem>() {
                    @Override
                    public int compare(RecyclerViewItem recyclerViewItem, RecyclerViewItem t1) {
                        // campaignCode를 기준으로 내림차순 정렬
                        return t1.getCampaignCode().compareTo(recyclerViewItem.getCampaignCode());
                    }
                });
                nArrayList.clear();
                // 신규캠페인 가져오기
                for(int i=0; i < 2; i++) {
                    RecyclerViewItem recyclerViewItem = arrayList.get(i);
                    nArrayList.add(recyclerViewItem);
                }
                nAdapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("HomeActivity3", String.valueOf(error.toException())); //에러문 출력
            }
        });

        fAdapter = new RecyclerViewAdapter(fArrayList, this);
        fRecyclerView.setAdapter(fAdapter);

        nAdapter = new RecyclerViewAdapter(nArrayList, this);
        nRecyclerView.setAdapter(nAdapter);



        Intent intent = getIntent();

        // Second_Certification_Page에서 보내온 신호를 가지고 있으면 수행한다.
        if (intent.hasExtra("sendData") && intent.hasExtra("image_path")){
            // 호출할 인텐트가 보내온 이미지와 메시지 얻어오기

            //byte[] arr = intent.getByteArrayExtra("image");
            Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("image_path"));
            String msg = (String)intent.getExtras().get("sendData");

            // 전달 되어온 정보를 뷰에 넣기
            ImageView BigImage = (ImageView)findViewById(R.id.realtime_image);
            BigImage.setImageBitmap(bitmap);
            TextView txt = (TextView)findViewById(R.id.realtime_text);
            txt.setText(msg);
        }

        // 검색 페이지 연동
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                startActivity(intent);
            }
        });

        // 북마크 페이지 연동

        bookmark = (ImageView)findViewById(R.id.bookmark);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookMark.class);
                startActivity(intent);
            }
        });

        // 하단 메뉴바 페이지 연동

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

    // cardnews indicator
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for(int i=0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for(int i=0; i < childCount; i++) {
            ImageView imageView = (ImageView)layoutIndicator.getChildAt(i);
            if(i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_indicator_inactive));
            }
        }
    }
}