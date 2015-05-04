package com.example.yenming.photographygps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.io.File;

/**
 * Created by Yenming on 15/5/5.
 */
public class MapMainActivity extends  ActionBarActivity{
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private File file;
    private String fileName = "myVideo.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 建立儲存檔案路徑的File物件
        file = new File(Environment.getExternalStorageDirectory(), fileName);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE &&
                resultCode == Activity.RESULT_OK) {
            // 取得傳回媒體儲存的檔案路徑
            String path = data.getData().toString();
            TextView output = (TextView) findViewById(R.id.textView2);
            output.setText(path); // 顯示路徑
        }
    }
    // Button元件的事件處理
    public void button_Click(View view) {
        // 使用Intent物件啟動VideoRecorder活動
        Intent intent = new Intent(this, VideoRecorder.class);
        startActivity(intent);
    }
    public void button2_Click(View view) {
        // 建立使用內建程式錄影的Intent物件
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // 新增附件為儲存的媒體檔案
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        // 指定錄影品質
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }
    public void button3_Click(View view) {
        // 使用Intent物件啟動VideoPlayer活動
        Intent intent = new Intent(this, VideoPlayer.class);
        startActivity(intent);
    }
    public void button4_Click(View view){
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

}
