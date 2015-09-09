# Android TimerView
===================

用于抢购或者团购模块的倒计时View，可以自定义字体，时间色块颜色，冒号颜色，字体颜色及大小。

 * xml建立布局
 * 在Activity中，首先`settime()`，然后执行`start()`方法开始计时
 * 设置倒计时结束的监听 `setOnTimeOutListener()`
 
####Activity
```java
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
```
####XML
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingBottom="@dimen/activity_vertical_margin"
    tools:context=".MainActivity">

    <xyz.insly.app.timerview.TimerView
        android:id="@+id/timerview"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        app:itextColor="@color/white"
        app:iradius="5dp"
        app:idotColor="@color/teal"
        app:ibackground="@color/orange"
        app:itextSize="30sp"
        />

</RelativeLayout>
```
