package jaygoo.m3u8downloader;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import jaygoo.library.m3u8downloader.utils.MUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MUtils.parseHeadContent(getExternalCacheDir().getPath(), "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}