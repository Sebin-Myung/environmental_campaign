package com.example.environmentalcampaign.set_up_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.environmentalcampaign.R;
import com.example.environmentalcampaign.cp_info.CampaignItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    private final int GALLERY_CODE = 777;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    FirebaseStorage storage;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

        iv_cp_logo = (ImageView)findViewById(R.id.iv_cp_logo);
        et_cp_name = (EditText)findViewById(R.id.et_cp_name);
        tv_frequency = (TextView)findViewById(R.id.tv_frequency);
        tv_period = (TextView)findViewById(R.id.tv_period);

        storage = FirebaseStorage.getInstance();

        // ???????????? ????????? ?????????????????? ????????? ??????
        iv_cp_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


        // ?????? ?????? ?????????
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        String today = simpleDateFormat.format(cal.getTime());

        tv_sDate = (TextView)findViewById(R.id.tv_sDate);
        tv_sDate.setText(today);
        tv_eDate = (TextView)findViewById(R.id.tv_eDate);

        // ?????? ?????? ?????? ??????
        tv_frequency_select = (TextView)findViewById(R.id.tv_frequency_select);
        tv_frequency_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrequency();
            }
        });

        // ?????? ?????? ?????? ??????
        tv_period_select = (TextView)findViewById(R.id.tv_period_select);
        tv_period_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPeriod();
            }
        });

        // ?????? ?????????
        tv_next = (TextView)findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkImage(iv_cp_logo)) {
                    Toast.makeText(setup1.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else
                    if(checkEditText(et_cp_name)) {
                    Toast.makeText(setup1.this, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else if(tv_frequency.getVisibility() == View.GONE) {
                    Toast.makeText(setup1.this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else if(tv_period.getVisibility() == View.GONE) {
                    Toast.makeText(setup1.this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // uid ????????????
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = firebaseUser.getUid();

                     // TemporarySave - uid??? ??????
                     database = FirebaseDatabase.getInstance();
                     databaseReference = database.getReference("environmentalCampaign").child("TemporarySave").child(uid);
                     databaseReference.child("logo").setValue(makeToken(imagePath));
                     databaseReference.child("title").setValue(et_cp_name.getText().toString());
                     databaseReference.child("frequency").setValue(tv_frequency.getText().toString());
                     databaseReference.child("period").setValue(tv_period.getText().toString());


                    Intent intent = new Intent(getApplicationContext(), setup2.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            }
        });

        // ???????????? ?????? ?????????
        bt_back = (ImageButton)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // ?????? ?????? ?????? ?????????
    void selectFrequency() {
        final CharSequence[] fItems = {"??? 1???", "??? 2???", "??? 3???", "??? 4???", "??? 5???", "??? 6???", "??? 7???"};

        AlertDialog.Builder fDialog = new AlertDialog.Builder(this);
        fDialog.setTitle("?????? ????????? ???????????????")
                .setItems(fItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_frequency.setText(fItems[i]);
                        tv_frequency.setVisibility(View.VISIBLE);
                    }
                }).show();
    }

    // ?????? ?????? ?????? ?????????
    void selectPeriod() {
        final CharSequence[] pItems = {"2???", "3???", "4???", "5???", "6???", "7???", "8???", "9???", "10???"};

        AlertDialog.Builder pDialog = new AlertDialog.Builder(this);
        pDialog.setTitle("?????? ????????? ???????????????")
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

    // ?????? ?????? ????????? ?????? ?????????
    String endDate(String period) {
        int len = period.length();
        int n = Integer.parseInt(period.substring(0, len-1));

        Calendar eCal = Calendar.getInstance(Locale.KOREA);
        eCal.add(Calendar.DATE, 7*n);
        String end = simpleDateFormat.format(eCal.getTime());

        return end;
    }

    // ????????? ??????????????? ??????(??????????????? false)
    boolean checkImage(ImageView imageView) {
        BitmapDrawable imageDrawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap imageBitmap = imageDrawable.getBitmap();

        BitmapDrawable checkDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.add_image);
        Bitmap checkBitmap = checkDrawable.getBitmap();

        return imageBitmap.equals(checkBitmap);
    }

    // edittext??? ??????????????? ??????
    boolean checkEditText(EditText editText) {
        return editText.getText().toString().equals("") || editText.getText().toString()==null;
    }

    // imageView?????? bitmap??? byte[]??? ??????
    public byte[] bitmapToByteArray(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    // byte[]??? String?????? ??????
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // byte??? String?????? ??????
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for(int bit = 0; bit < 8; bit++) {
            if(((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    // ????????? ???????????? ?????? ????????? 1
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //??????????????? ????????????
                    // Storage??? ????????????
                    StorageReference storageRef = storage.getReference();

                    Uri file = Uri.fromFile(new File(getRealPathFromURI(data.getData())));
                    StorageReference riversRef = storageRef.child("Campaign/").child("images/"+file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnSuccessListener
                            (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }

    // ????????? ???????????? ?????? ????????? 2
    private void sendPicture(Uri imgUri) {
        imagePath = getRealPathFromURI(imgUri); // path ??????
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//????????? ?????? ??????????????? ??????
        iv_cp_logo.setImageBitmap(rotate(bitmap, exifDegree));//????????? ?????? ????????? ??????
    }

    // ????????? ???????????? ?????? ????????? 3
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // ????????? ???????????? ?????? ????????? 4
    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix ?????? ??????
        Matrix matrix = new Matrix();
        // ?????? ?????? ??????
        matrix.postRotate(degree);
        // ???????????? Matrix ??? ???????????? Bitmap ?????? ??????
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    // ????????? ???????????? ?????? ????????? 5
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    // ?????? ?????????
    String makeToken(String imagePath) {
        int index = imagePath.lastIndexOf("/");
        String imageName = imagePath.substring(index+1);
        String token = "https://firebasestorage.googleapis.com/v0/b/environmental-campaign.appspot.com/o/Campaign%2Fimages%2F"
                 + imageName + "?alt=media";

        return token;
    }
}