package xyz.insly.app.timerview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.insly.app.timecounter.TimerView;

public class MainActivity extends Activity {
    @Bind(R.id.timerview)
    TimerView timerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /**
         * 可以设置时间数字的TypeFace
         */
        Typeface font = Typeface.createFromAsset(getAssets(), "font/KAISER.TTF");
        timerview.setTypeface(font);
        /**
         * TimerView.TIMETYPE_MS 传入参数单位为毫秒
         * TimerView.TIMETYPE_S 传入参数单位为秒
         * TimerView.TIMETYPE_M 传入参数单位为分钟
         * TimerView.TIMETYPE_H 传入参数单位为小时
         */
        timerview.setTime(120, TimerView.TIMETYPE_S);
        if (!timerview.getRunning()) {
            timerview.start();
        }
        /**
         * 倒计时结束的监听
         */
        timerview.setOnTimeOutListener(new TimerView.onTimeOutListener() {
            @Override
            public void onTimeOut() {
                handler.sendEmptyMessage(100);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                Toast.makeText(MainActivity.this, "time out!", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
