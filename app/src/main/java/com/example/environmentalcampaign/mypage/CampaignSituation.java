package com.example.environmentalcampaign.mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.environmentalcampaign.MyAdapter;
import com.example.environmentalcampaign.MyCompleteAdapter;
import com.example.environmentalcampaign.MySetUpCampaignAdapter;
import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.cp_info.CampaignInformation;
import com.example.environmentalcampaign.cp_info.MyCampaignItem;
import com.example.environmentalcampaign.set_up_page.SetUpCampaignItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CampaignSituation extends AppCompatActivity {
    TextView tv_cp_situation, tv_cp_number, tv_avr_rate;
    LinearLayout lo_avgRate;
    ImageButton bt_back;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<MyCampaignItem> arrayList;
    private ArrayList<CompleteCampaignItem> arrayList2;
    private ArrayList<SetUpCampaignItem> arrayList3;
    private BaseAdapter adapter;
    private ListView listView;

    ArrayList<String> campaignCodes;
    String uid;
    double sum, avg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_situation);

        bt_back = (ImageButton)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        tv_cp_situation = (TextView)findViewById(R.id.tv_cp_situation);
        tv_cp_number = (TextView)findViewById(R.id.tv_cp_number);
        tv_avr_rate = (TextView)findViewById(R.id.tv_avr_rate);
        lo_avgRate = (LinearLayout)findViewById(R.id.lo_avgRate);
        listView = (ListView)findViewById(R.id.lv_cp_situation);
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        campaignCodes = new ArrayList<>();

        Intent gIntent = getIntent();
        int intent_number = gIntent.getIntExtra("intent_number", 0);

        // uid ????????????
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("environmentalCampaign").child("MyCampaign").child(uid);

        switch(intent_number) {
            case 1:
                tv_cp_situation.setText("????????? ?????????");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MyCampaignItem myCampaignItem = snapshot.getValue(MyCampaignItem.class);

                            // ???????????? ????????????
                            long now = System.currentTimeMillis();
                            Date mDate = new Date(now);
                            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
                            String today = simpleDate.format(mDate);

                            // ???????????? ????????????
                            if(today.compareTo(myCampaignItem.getEndDate()) <= 0) {
                                arrayList.add(myCampaignItem);
                            }
                        }
                        tv_cp_number.setText(arrayList.size() + "???");
                        sum = 0;
                        for(int i = 0; i < arrayList.size(); i++) {
                            int rate = arrayList.get(i).getCertiCompleteCount()*100/arrayList.get(i).getCertiCount();
                            sum += (double)rate;
                        }

                        if(arrayList.size() == 0) { avg = 0.0; }
                        else { avg = Double.parseDouble(String.format("%.1f", sum / arrayList.size())); }
                        tv_avr_rate.setText(avg + "%");
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                adapter = new MyAdapter(arrayList, this, true);
                listView.setAdapter(adapter);

                // ??????????????? ???????????? ????????? ?????? ???????????? ????????????.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // campaign information?????? ????????????.
                        Intent intent = new Intent(getApplicationContext(), CampaignInformation.class);
                        MyCampaignItem item = (MyCampaignItem)adapter.getItem(i);
                        intent.putExtra("campaignCode", item.getCampaignCode());
                        intent.putExtra("signal", "mypage");
                        startActivity(intent);
                    }
                });
                break;
            case 2:
                tv_cp_situation.setText("????????? ?????????");

                database.getReference("environmentalCampaign").child("CompleteCampaign").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList2.clear();
                        if(dataSnapshot.hasChild(uid)) {
                            for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                CompleteCampaignItem completeCampaignItem = snapshot.getValue(CompleteCampaignItem.class);
                                arrayList2.add(completeCampaignItem);
                            }
                        }
                        tv_cp_number.setText(arrayList2.size() + "???");
                        sum = 0;
                        for(int i = 0; i < arrayList2.size(); i++) {
                            double rate = arrayList2.get(i).getAchievementAvg();
                            sum += rate;
                        }

                        if(arrayList2.size() == 0) { avg = 0.0; }
                        else { avg = Double.parseDouble(String.format("%.1f", sum / arrayList2.size())); }
                        tv_avr_rate.setText(avg + "%");
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                adapter = new MyCompleteAdapter(arrayList2, this);
                listView.setAdapter(adapter);

                // ??????????????? ???????????? ????????? ?????? ???????????? ????????????.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // campaign information?????? ????????????.
                        Intent intent = new Intent(getApplicationContext(), CampaignInformation.class);
                        CompleteCampaignItem item = (CompleteCampaignItem)adapter.getItem(i);
                        intent.putExtra("campaignCode", item.getCampaignCode());
                        intent.putExtra("signal", "mypage");
                        startActivity(intent);
                    }
                });
                break;
            case 3:
                tv_cp_situation.setText("?????? ????????? ?????????");

                database.getReference("environmentalCampaign").child("SetUpCampaign").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList3.clear();
                        if(dataSnapshot.hasChild(uid)) {
                            for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                SetUpCampaignItem setUpCampaignItem = snapshot.getValue(SetUpCampaignItem.class);
                                arrayList3.add(setUpCampaignItem);
                            }
                        }
                        tv_cp_number.setText(arrayList3.size() + "???");
                        lo_avgRate.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                adapter = new MySetUpCampaignAdapter(arrayList3, this);
                listView.setAdapter(adapter);

                // ??????????????? ???????????? ????????? ?????? ???????????? ????????????.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // campaign information?????? ????????????.
                        Intent intent = new Intent(getApplicationContext(), CampaignInformation.class);
                        SetUpCampaignItem item = (SetUpCampaignItem)adapter.getItem(i);
                        intent.putExtra("campaignCode", item.getCampaignCode());
                        intent.putExtra("signal", "mypage");
                        startActivity(intent);
                    }
                });
                break;
            case 4:
                tv_cp_situation.setText("????????? ?????????");

                database.getReference("environmentalCampaign").child("SetUpCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        campaignCodes.clear();
                        if(dataSnapshot.hasChild(uid)) {
                            // ?????? ????????? ???????????? campaignCode??? ????????????
                            for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                SetUpCampaignItem setUpCampaignItem = snapshot.getValue(SetUpCampaignItem.class);
                                campaignCodes.add(setUpCampaignItem.getCampaignCode());
                            }

                            database.getReference("environmentalCampaign").child("MyCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    arrayList.clear();
                                    if(dataSnapshot.hasChild(uid)) {
                                        for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                            MyCampaignItem myCampaignItem = snapshot.getValue(MyCampaignItem.class);

                                            // ???????????? ????????????
                                            long now = System.currentTimeMillis();
                                            Date mDate = new Date(now);
                                            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
                                            String today = simpleDate.format(mDate);

                                            // ???????????? ?????????, ?????? ????????? ??????????????????
                                            if((today.compareTo(myCampaignItem.getEndDate()) <= 0)&&(campaignCodes.contains(myCampaignItem.getCampaignCode()))) {
                                                arrayList.add(myCampaignItem);
                                            }
                                        }
                                    }
                                    tv_cp_number.setText(arrayList.size() + "???");
                                    sum = 0;
                                    for(int i = 0; i < arrayList.size(); i++) {
                                        int rate = arrayList.get(i).getCertiCompleteCount()*100/arrayList.get(i).getCertiCount();
                                        sum += (double)rate;
                                    }

                                    if(arrayList.size() == 0) { avg = 0.0; }
                                    else { avg = Double.parseDouble(String.format("%.1f", sum / arrayList.size())); }
                                    tv_avr_rate.setText(avg + "%");
                                    adapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                adapter = new MyAdapter(arrayList, getApplicationContext(), true);
                listView.setAdapter(adapter);

                // ??????????????? ???????????? ????????? ?????? ???????????? ????????????.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // campaign information?????? ????????????.
                        Intent intent = new Intent(getApplicationContext(), CampaignInformation.class);
                        MyCampaignItem item = (MyCampaignItem)adapter.getItem(i);
                        intent.putExtra("campaignCode", item.getCampaignCode());
                        intent.putExtra("signal", "mypage");
                        startActivity(intent);
                    }
                });
                break;
            case 5:
                tv_cp_situation.setText("????????? ?????????");

                database.getReference("environmentalCampaign").child("SetUpCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        campaignCodes.clear();
                        if(dataSnapshot.hasChild(uid)) {
                            // ?????? ????????? ???????????? campaignCode??? ????????????
                            for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                SetUpCampaignItem setUpCampaignItem = snapshot.getValue(SetUpCampaignItem.class);
                                campaignCodes.add(setUpCampaignItem.getCampaignCode());
                            }

                            database.getReference("environmentalCampaign").child("CompleteCampaign").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    arrayList2.clear();
                                    if(dataSnapshot.hasChild(uid)) {
                                        for(DataSnapshot snapshot : dataSnapshot.child(uid).getChildren()) {
                                            CompleteCampaignItem completeCampaignItem = snapshot.getValue(CompleteCampaignItem.class);

                                            // ???????????? ?????????, ?????? ????????? ??????????????????
                                            if(campaignCodes.contains(completeCampaignItem.getCampaignCode())) {
                                                arrayList2.add(completeCampaignItem);
                                            }
                                        }
                                    }
                                    tv_cp_number.setText(arrayList2.size() + "???");
                                    sum = 0;
                                    for(int i = 0; i < arrayList2.size(); i++) {
                                        double rate = arrayList2.get(i).getAchievementAvg();
                                        sum += (double)rate;
                                    }

                                    if(arrayList2.size() == 0) { avg = 0.0; }
                                    else { avg = Double.parseDouble(String.format("%.1f", sum / arrayList2.size())); }
                                    tv_avr_rate.setText(avg + "%");
                                    adapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                adapter = new MyCompleteAdapter(arrayList2, getApplicationContext());
                listView.setAdapter(adapter);

                // ??????????????? ???????????? ????????? ?????? ???????????? ????????????.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // campaign information?????? ????????????.
                        Intent intent = new Intent(getApplicationContext(), CampaignInformation.class);
                        CompleteCampaignItem item = (CompleteCampaignItem)adapter.getItem(i);
                        intent.putExtra("campaignCode", item.getCampaignCode());
                        intent.putExtra("signal", "mypage");
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}