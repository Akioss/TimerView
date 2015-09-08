package xyz.insly.app.timerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/*****************************************************************************************************************
 * Author: liyi
 * Create Date: 15/9/2.
 * Package: xyz.insly.app.myutils.widgets
 * Discription:
 * Version: 1.0
 * ---------------------------------------------------------------------------------------------------------------
 * Modified By:
 * Modified Date:
 * Why & What is modified :
 *****************************************************************************************************************/
public class TimerView extends TextView implements ViewTreeObserver.OnGlobalFocusChangeListener{

    private static final String TAG = "TimerView";

    /**
     * wantsize默认的宽高
     */
    private static final int DEFULT_WIDTH = 300;
    private static final int DEFULT_HEIGHT = 80;

    /**
     * 传入时间的类型 ms毫秒 s秒 m分钟 h小时
     */
    public static final int TIMETYPE_MS = 0;
    public static final int TIMETYPE_S = 1;
    public static final int TIMETYPE_M = 2;
    public static final int TIMETYPE_H = 3;

    /**
     * 用于更新UI
     */
    private boolean isRunning = false;

    /**
     * 用于绘制
     */
    private TextPaint mTextPaint;
    private Paint mPaint;
    private Path[] paths; //文本绘制路径

    private int width;
    private int height;
    private float backRadius;
    private int backColor;
    private int dotColor;
    private float textSize;
    private int textColor;

    /**
     * 时间属性
     */
    private int mday;
    private int mhour;
    private int mmin;
    private int msecond;

    /**
     * 倒计时结束监听
     */
    private onTimeOutListener listener;

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources res = getResources();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.density = res.getDisplayMetrics().density;
        mPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerView, defStyle, 0);

        textSize = a.getDimension(R.styleable.TimerView_itextSize, 32f);
        textColor = a.getColor(R.styleable.TimerView_itextColor, 0xff000000);
        backRadius = a.getDimension(R.styleable.TimerView_iradius, 8f);
        backColor = a.getColor(R.styleable.TimerView_ibackground, 0xffffffff);
        dotColor = a.getColor(R.styleable.TimerView_idotColor, 0xffffffff);

        a.recycle();

        paths = new Path[3];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureSize(widthMeasureSpec, DEFULT_WIDTH, DEFULT_WIDTH);
        height = (int) (width / 3 * 0.8);

        Log.d(TAG, "onMeasure after: width" + width + "height" + height);

        setMeasuredDimension(width, height);
    }

    /**
     * 测量view的宽高
     *
     * @param measureSpec spec
     * @param wantSize    defultSize
     * @param minSize     min
     * @return 宽高
     */
    public static int measureSize(int measureSpec, int wantSize, int minSize) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // 父布局想要view的大小
            result = specSize;
        } else {
            result = wantSize;
            if (specMode == MeasureSpec.AT_MOST) {
                // wrap_content
                result = Math.min(result, specSize);
            }
        }
        //测量的尺寸和最小尺寸取大
        return Math.max(result, minSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float blockWidth = width * 4 / 15;
        float blockHeight = height;
        float borderWidth = width / 10;

        /**
         * 绘制背景
         */
        drawBlock(canvas, blockWidth, blockHeight, borderWidth);
        drawDot(canvas, blockWidth, blockHeight, borderWidth);

        /**
         * 绘制数字
         */
        drawTime(canvas, blockWidth, blockHeight, borderWidth);
    }

    /**
     * 绘制显示时间的背景色块
     *
     * @param canvas  画布
     * @param blockw  色块宽
     * @param blockh  色块高
     * @param borderw 色块之间间隔
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawBlock(Canvas canvas, float blockw, float blockh, float borderw) {

        int version = Build.VERSION.SDK_INT;
        canvas.save();
        mPaint.setColor(backColor);
        if (version < 21) {
            canvas.drawRect(0f, 0f, blockw, blockh, mPaint);
            canvas.drawRect(blockw + borderw, 0f, blockw * 2 + borderw, blockh, mPaint);
            canvas.drawRect((blockw + borderw) * 2, 0f, width, blockh, mPaint);
        }
        if (version >= 21) {
            canvas.drawRoundRect(0f, 0f, blockw, blockh, backRadius, backRadius, mPaint);
            canvas.drawRoundRect(blockw + borderw, 0f, blockw * 2 + borderw, blockh, backRadius,
                    backRadius, mPaint);
            canvas.drawRoundRect((blockw + borderw) * 2, 0f, width, blockh, backRadius,
                    backRadius, mPaint);
        }
        canvas.restore();
    }

    /**
     * 绘制时间色块之间的dot
     *
     * @param canvas  画布
     * @param blockw  色块宽
     * @param blockh  色块高
     * @param borderw 色块之间间隔
     */
    private void drawDot(Canvas canvas, float blockw, float blockh, float borderw) {

        canvas.save();
        mPaint.setColor(dotColor);
        canvas.drawCircle(blockw + borderw / 2, blockh / 3, blockh / 20, mPaint);
        canvas.drawCircle(blockw + borderw / 2, blockh * 2 / 3, blockh / 20, mPaint);
        canvas.drawCircle(blockw * 2 + borderw * 3 / 2, blockh / 3, blockh / 20, mPaint);
        canvas.drawCircle(blockw * 2 + borderw * 3 / 2, blockh * 2 / 3, blockh / 20, mPaint);
        canvas.restore();
    }

    /**
     * 绘制时间数字
     *
     * @param canvas  画布
     * @param blockw  色块宽
     * @param blockh  色块高
     * @param borderw 色块间隔
     */
    private void drawTime(Canvas canvas, float blockw, float blockh, float borderw) {

        canvas.save();
        /**
         * 字体画笔
         */
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        /**
         * 使时间数字在色块中间
         */
        float tw = mTextPaint.measureText(String.valueOf(mhour));
        Log.d(TAG, "drawTime: tw: " + tw);
        Log.d(TAG, "drawTime: height: " + height);
        Log.d(TAG, "drawTime: textsize: " + textSize);
        Log.d(TAG, "drawTime: hoffset: " + (height + textSize) / 2);
//        paths[0].
//        canvas.drawTextOnPath();
        canvas.drawText(getHour() + "/" + getMin() + "/" + getSecond(), 0f,
                (height + textSize) / 2, mTextPaint);
        canvas.restore();
    }

    public void setTypeface(Typeface tf) {
        if (mTextPaint != null && mTextPaint.getTypeface() != tf) {
            mTextPaint.setTypeface(tf);
            invalidate();
        }
    }

    /**
     * 初始化时间参数
     *
     * @param times 毫秒数，秒数，分钟数，或小时数
     * @param type  传入类型 MS，S，M，H
     */
    public void setTime(int times, @TimeType int type) {

        switch (type) {
            case TIMETYPE_MS:
                mday = times / (24 * 60 * 60 * 1000);
                mhour = times / (60 * 60 * 1000) - mday * 24;
                mmin = (times / (60 * 1000)) - mday * 24 * 60 - mhour * 60;
                msecond = times / 1000 - mday * 24 * 60 * 60 - mhour * 60 * 60 - mmin * 60;
                break;
            case TIMETYPE_S:
                mday = times / (24 * 60 * 60);
                mhour = times / (60 * 60) - mday * 24;
                mmin = times / 60 - mday * 24 * 60 - mhour * 60;
                msecond = times - mday * 24 * 60 * 60 - mhour * 60 * 60 - mmin * 60;
                break;
            case TIMETYPE_M:
                mday = times / (24 * 60);
                mhour = times / 60 - mday * 24;
                mmin = times - mday * 24 * 60 - mhour * 60;
                msecond = 0;
                break;
            case TIMETYPE_H:
                mday = times / 24;
                mhour = times - mday * 24;
                mmin = 0;
                msecond = 0;
                break;
            default:
                throw new IllegalArgumentException("no such time type!");
        }
    }

    /**
     * 对时间进行格式化 1-01 2-02
     * @return  时间字符串
     */
    private String getDay() {
        return mday <= 9 && mday >= 0 ? "0" + mday : String.valueOf(mday);
    }

    private String getHour() {
        return mhour <= 9 && mhour >= 0 ? "0" + mhour : String.valueOf(mday);
    }

    private String getMin() {
        return mmin <= 9 && mmin >= 0 ? "0" + mmin : String.valueOf(mmin);
    }

    private String getSecond() {
        return msecond <= 9 && msecond >= 0 ? "0" + msecond : String.valueOf(msecond);
    }

    /**
     * 倒计时计算
     */
    private void computeTime() {
        msecond--;
        if (msecond < 0) {
            mmin--;
            msecond = 59;
            if (mmin < 0) {
                mmin = 59;
                mhour--;
                if (mhour < 0) {
                    // 倒计时结束
                    mhour = 24;
                    mday--;
                }
            }
        }
    }

    public boolean getRunning() {
        return isRunning;
    }

    /**
     * 启动倒计时
     */
    public void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    computeTime();
                    if (!isTimeOut()) {
                        invalidateWrap();
                    }else {
                        stop();
                        if (listener != null) {
                            listener.onTimeOut();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 结束倒计时
     */
    public void stop() {
        isRunning = false;
        invalidateWrap();
    }

    /**
     * 倒计时是否结束
     * @return true结束 false未结束
     */
    private boolean isTimeOut() {
        return mday == 0 && mhour == 0 && mmin == 0 && msecond == 0;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void invalidateWrap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            postInvalidate();
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {

    }

    /**
     * 设置倒计时结束监听
     *
     * @param listener listener
     */
    public void setOnTimeOutListener(onTimeOutListener listener) {
        this.listener = listener;
    }

    public interface onTimeOutListener {
        public abstract void onTimeOut();
    }

    @IntDef({TIMETYPE_MS, TIMETYPE_S, TIMETYPE_M, TIMETYPE_H})
    private @interface TimeType{}

}
