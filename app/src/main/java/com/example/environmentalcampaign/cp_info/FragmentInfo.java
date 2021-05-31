package com.example.environmentalcampaign.cp_info;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.environmentalcampaign.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class FragmentInfo extends Fragment {

    TextView cpInfo;
    ImageView iv_info1, iv_info2, iv_info3, iv_info4, iv_info5;

    public FragmentInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_info, container, false);
        cpInfo = (TextView)rootView.findViewById(R.id.tv_cpInfo);
        iv_info1 = (ImageView)rootView.findViewById(R.id.iv_info1);
        iv_info2 = (ImageView)rootView.findViewById(R.id.iv_info2);
        iv_info3 = (ImageView)rootView.findViewById(R.id.iv_info3);
        iv_info4 = (ImageView)rootView.findViewById(R.id.iv_info4);
        iv_info5 = (ImageView)rootView.findViewById(R.id.iv_info5);

        Bundle bundle = getArguments();

        if(bundle != null) {
            String info = bundle.getString("info");
            byte[] arr1 = bundle.getByteArray("infoImage1");
            byte[] arr2 = bundle.getByteArray("infoImage2");
            byte[] arr3 = bundle.getByteArray("infoImage3");
            byte[] arr4 = bundle.getByteArray("infoImage4");
            byte[] arr5 = bundle.getByteArray("infoImage5");
            byte[] checkByteArray = new byte[]{};

            if(arr1 != checkByteArray) {
                iv_info1.setImageDrawable(byteArrayToDrawable(arr1));
                iv_info1.setVisibility(View.VISIBLE);
            }
            if(arr2 != checkByteArray) {
                iv_info2.setImageDrawable(byteArrayToDrawable(arr2));
                iv_info2.setVisibility(View.VISIBLE);
            }
            if(arr3 != checkByteArray) {
                iv_info3.setImageDrawable(byteArrayToDrawable(arr3));
                iv_info3.setVisibility(View.VISIBLE);
            }
            if(arr4 != checkByteArray) {
                iv_info4.setImageDrawable(byteArrayToDrawable(arr4));
                iv_info4.setVisibility(View.VISIBLE);
            }
            if(arr5 != checkByteArray) {
                iv_info5.setImageDrawable(byteArrayToDrawable(arr5));
                iv_info5.setVisibility(View.VISIBLE);
            }

//            Bitmap infoImage1 = BitmapFactory.decodeByteArray(arr1, 0, arr1.length);
//            Bitmap infoImage2 = BitmapFactory.decodeByteArray(arr2, 0, arr2.length);
//            Bitmap infoImage3 = BitmapFactory.decodeByteArray(arr3, 0, arr3.length);
//            Bitmap infoImage4 = BitmapFactory.decodeByteArray(arr4, 0, arr4.length);
//            Bitmap infoImage5 = BitmapFactory.decodeByteArray(arr5, 0, arr5.length);
//            byte[] checkbyte = bundle.getByteArray("checkImage");
//            Bitmap checkImage = BitmapFactory.decodeByteArray(checkbyte, 0, checkbyte.length);
//
//            Bitmap[] infoImages = {infoImage1, infoImage2, infoImage3, infoImage4, infoImage5};
//            ImageView[] imageViews = {iv_info1, iv_info2, iv_info3, iv_info4, iv_info5};
//
//            for(int i = 0; i < infoImages.length; i++) {
//                if(!(infoImages[i].sameAs(checkImage))) {
//                    imageViews[i].setImageBitmap(infoImages[i]);
//                    imageViews[i].setVisibility(View.VISIBLE);
//                }
//            }

            cpInfo.setText(info);
        }

        return rootView;
    }

    // byte[]를 Drawable로 변환
    public Drawable byteArrayToDrawable(byte[] b) {
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Drawable drawable = Drawable.createFromStream(is, "drawable");
        return drawable;
    }
}