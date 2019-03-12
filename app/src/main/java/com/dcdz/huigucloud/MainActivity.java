package com.dcdz.huigucloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zhouyou.http.EasyHttp;

import org.apache.log4j.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected static Logger log = Logger.getLogger(MainActivity.class);
    protected Button btnTakePhoto;
    protected ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }


    public void getAccessToken() {
        /*EasyHttp.post("/v1/auth/tok")
                .params("clienti", )
                .params("client_secret", )*/
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_take_photo) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, 1);
        }
    }

    private void initView() {
        btnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(MainActivity.this);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivPhoto.setImageBitmap(bitmap);
            }
        }
    }

}
