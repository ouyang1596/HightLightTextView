package demo.com.hightlighttextview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import demo.com.hightlighttext.View.HightLightTextView;

public class MainActivity extends AppCompatActivity {
    private static final String DESC = "Android是一种基于Linux的自由及开放源代码的操作系统。主要使用于移动设备，如智能手机和平板电脑，由Google公司和开放手机联盟领导及开发。尚未有统一中文名称，中国大陆地区较多人使用“安卓”。";
    private HightLightTextView hightLightTextView;
    private HightLightTextView hightLightTextView2;
    private HightLightTextView hightLightTextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hightLightTextView = findViewById(R.id.hightLightTextView);
        hightLightTextView2 = findViewById(R.id.hightLightTextView2);
        hightLightTextView3 = findViewById(R.id.hightLightTextView3);
        hightLightTextView.setText(DESC, "Android");
        hightLightTextView2.setText(DESC, "移动设备");
        hightLightTextView3.setText(DESC, "中国大陆地区较多人使用“安卓”。");
    }
}
