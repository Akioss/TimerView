package xyz.insly.app.timerview;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
