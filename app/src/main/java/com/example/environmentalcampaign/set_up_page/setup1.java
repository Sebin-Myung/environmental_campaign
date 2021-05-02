package com.example.environmentalcampaign.set_up_page;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.environmentalcampaign.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class setup1 extends AppCompatActivity {

    ImageButton bt_back;
    TextView tv_next;

    ImageView iv_cp_logo;
    EditText et_cp_name;
    TextView tv_frequency, tv_period;

    TextView tv_frequency_select, tv_period_select;
    TextView tv_sDate, tv_eDate;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

        iv_cp_logo = (ImageView)findViewById(R.id.iv_cp_logo);
        et_cp_name = (EditText)findViewById(R.id.et_cp_name);
        tv_frequency = (TextView)findViewById(R.id.tv_frequency);
        tv_period = (TextView)findViewById(R.id.tv_period);

        // 오늘 날짜 구하기
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        String today = simpleDateFormat.format(cal.getTime());

        tv_sDate = (TextView)findViewById(R.id.tv_sDate);
        tv_sDate.setText(today);
        tv_eDate = (TextView)findViewById(R.id.tv_eDate);

        // 인증 빈도 선택 버튼
        tv_frequency_select = (TextView)findViewById(R.id.tv_frequency_select);
        tv_frequency_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrequency();
            }
        });

        // 인증 기간 선택 버튼
        tv_period_select = (TextView)findViewById(R.id.tv_period_select);
        tv_period_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPeriod();
            }
        });

        // 다음 페이지
        tv_next = (TextView)findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), setup2.class);

                // 이미지 Bitmap 변환
                BitmapDrawable drawable = (BitmapDrawable)iv_cp_logo.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("logo", byteArray);
                intent.putExtra("cp_name", et_cp_name.getText().toString());
                intent.putExtra("frequency", tv_frequency.getText().toString());
                intent.putExtra("period", tv_period.getText().toString());
//                intent.putExtra("eDate", tv_eDate.getText().toString());
                startActivity(intent);
            }
        });

        // 이전 페이지 버튼을 통해 돌아왔을 경우 => 그냥 onBackPressed 하면 되는 것 같다...
//        Intent preIntent = getIntent();
//        if(preIntent.hasExtra("logo")) {
//            byte[] arr = preIntent.getByteArrayExtra("logo");
//            Bitmap logo = BitmapFactory.decodeByteArray(arr, 0, arr.length);
//            String cp_name = (String)preIntent.getExtras().get("cp_name");
//            String frequency = (String)preIntent.getExtras().get("frequency");
//            String period = (String)preIntent.getExtras().get("period");
//            String eDate = (String)preIntent.getExtras().get("eDate");
//
//            iv_cp_logo.setImageBitmap(logo);
//            et_cp_name.setText(cp_name);
//            tv_frequency.setText(frequency);
//            tv_frequency.setVisibility(View.VISIBLE);
//            tv_period.setText(period);
//            tv_period.setVisibility(View.VISIBLE);
//            tv_eDate.setText(eDate);
//            tv_eDate.setVisibility(View.VISIBLE);
//        }

        // 뒤로가기 버튼 이벤트
        bt_back = (ImageButton)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // 인증 빈도 선택 이벤트
    void selectFrequency() {
        final CharSequence[] fItems = {"주 1일", "주 2일", "주 3일", "주 4일", "주 5일", "주 6일", "주 7일"};

        AlertDialog.Builder fDialog = new AlertDialog.Builder(this);
        fDialog.setTitle("인증 빈도를 선택하세요")
                .setItems(fItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_frequency.setText(fItems[i]);
                        tv_frequency.setVisibility(View.VISIBLE);
                    }
                }).show();
    }

    // 인증 기간 선택 이벤트
    void selectPeriod() {
        final CharSequence[] pItems = {"2주", "3주", "4주", "5주", "6주", "7주", "8주", "9주", "10주"};

        AlertDialog.Builder pDialog = new AlertDialog.Builder(this);
        pDialog.setTitle("인증 기간을 선택하세요")
                .setItems(pItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_period.setText(pItems[i]);
                        tv_period.setVisibility(View.VISIBLE);
                        tv_eDate.setText(endDate(tv_period.getText().toString()));
                        tv_eDate.setVisibility(View.VISIBLE);
                    }
                }).show();
    }

    // 인증 기간 마지막 날짜 구하기
    String endDate(String period) {
        int len = period.length();
        int n = Integer.parseInt(period.substring(0, len-1));

        Calendar eCal = Calendar.getInstance(Locale.KOREA);
        eCal.add(Calendar.DATE, 7*n);
        String end = simpleDateFormat.format(eCal.getTime());

        return end;
    }
}