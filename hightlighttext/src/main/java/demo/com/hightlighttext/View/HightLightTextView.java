package demo.com.hightlighttext.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import demo.com.hightlighttext.R;

/**
 * 自定义搜索文本显示
 * 有以下五种情况：
 * 1，keyword不超过控件宽度，text超过控件宽度，keyword在可见范围内（只在最后加三个点）
 * 2，keyword不超过控件宽度，text超过控件宽度，keyword在不可见可见范围的最后（只在前面加三个点）
 * 3，keyword不超过控件宽度，text超过控件宽度，keyword在不可见可见范围的中间（前面加三个点最后加三个点）
 * 4，keyword超过控件宽度，只显示keyword（只在最后加三个点）
 * 5，keyword等于控件宽度，只显示keyword
 */
public class HightLightTextView extends View {
    private Paint mPaint;
    private String mText;
    private String mKeyword;
    private float mTextSize;
    private int mTextColor;
    private int mKeywordColor;

    public HightLightTextView(Context context) {
        this(context, null);
    }

    public HightLightTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HightLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HightLightTextView, defStyleAttr, 0);
        mTextColor = typedArray.getColor(R.styleable.HightLightTextView_hTextColor, -1);
        mKeywordColor = typedArray.getColor(R.styleable.HightLightTextView_hKeywordColor, -1);
        mTextSize = typedArray.getDimension(R.styleable.HightLightTextView_hTextSize, 12);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        setPaintColor(mTextColor);
//        setPaintColor(mKeywordColor);
        mPaint.setTextSize(mTextSize);
    }

    private void setPaintColor(int mTextColor) {
        mPaint.setColor(mTextColor);
    }

    /**
     * View 的 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (TextUtils.isEmpty(mText)) {
            mText = "";
        }
        if (TextUtils.isEmpty(mKeyword)) {
            mKeyword = "";
        }
        //1. 获取 自定义 View 的宽度，高度 的模式
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (MeasureSpec.AT_MOST == heigthMode) {
            Rect bounds = new Rect();
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
            Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
            height = (fontMetricsInt.bottom - fontMetricsInt.top)/*bounds.height()*/ + getPaddingBottom() + getPaddingTop();
        }

        if (MeasureSpec.AT_MOST == widthMode) {
            Rect bounds = new Rect();
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
            width = bounds.width() + getPaddingLeft() + getPaddingRight();
        }
        setMeasuredDimension(width, height);
    }

    public void setText(String text, String field) {
        mText = text;
        mKeyword = field;
        requestLayout();
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        getShowText();
        //计算基线
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;//todo 不是很明白
        int x = getPaddingLeft();
        int fieldIndex = mText.indexOf(mKeyword);
        String txt1 = "";
        String txt2 = "";
        String txt3 = "";
        if (fieldIndex >= 0) {
            txt1 = mText.substring(0, fieldIndex);
            txt2 = mText.substring(fieldIndex, fieldIndex + mKeyword.length());
            txt3 = mText.substring(fieldIndex + mKeyword.length(), mText.length());
            if (!TextUtils.isEmpty(txt1)) {
                setPaintColor(mTextColor);
                canvas.drawText(txt1, x, baseLine, mPaint);
            }
            int columnWidth = 3;//字符间的间距
            if (!TextUtils.isEmpty(txt2)) {
                x += getTextWiath(txt1) + columnWidth;
                setPaintColor(mKeywordColor);
                canvas.drawText(txt2, x, baseLine, mPaint);
            }
            if (!TextUtils.isEmpty(txt3)) {
                x += getTextWiath(txt2) + columnWidth;
                setPaintColor(mTextColor);
                canvas.drawText(txt3, x, baseLine, mPaint);
            }
        } else {
            canvas.drawText(mText, x, baseLine, mPaint);
        }
    }

    private void getShowText() {
        int width = getTextWiath(mText);
        if (mText.contains(mKeyword)) {
            if (width > getWidth()) {
                int w = getTextWiath(mKeyword);
                if (w > getWidth()) {//field超过控件宽度，只显示field（只在最后加三个点）
                    mText = getVisibleTextEnd(width, mKeyword);
                } else if (w == getWidth()) {//field等于控件宽度，只显示field
                    mText = mKeyword;
                } else {//field不超过控件宽度
                    String text = getVisibleTextEnd(width, mText);
                    int fieldIndexLength = mText.indexOf(mKeyword) + mKeyword.length();
                    if (fieldIndexLength < text.length() && text.contains(mKeyword)) {//field在可见范围内（只在最后加三个点）
                        mText = text;
                    } else {//field在不可见可见范围
                        if (fieldIndexLength < mText.length()) {//field在不可见可见范围的中间（前面加三个点最后加三个点）
                            mText = getVisibleTextStartEnd(width, mText.substring(0, mText.indexOf(mKeyword) + mKeyword.length()));
                        } else {//field在不可见可见范围的最后（只在前面加三个点）
                            mText = getVisibleTextStart(width, mText.substring(0, mText.indexOf(mKeyword) + mKeyword.length()));
                        }
                    }
                }
            }
        } else {
            mText = getVisibleTextEnd(width, mText);
        }
    }

    /**
     * 获取屏幕内可见的text...
     */
    private String getVisibleTextEnd(int width, String text) {
        int count = 1;
        String txt = text;
        while (width > getWidth()) {
            int len = text.length() - count;
            if (len > 0) {
                txt = text.substring(0, len) + "...";
                width = getTextWiath(txt);
                count++;
            } else {
                break;
            }
        }
        return txt;
    }

    /**
     * 获取屏幕内可见的...text...
     */
    private String getVisibleTextStartEnd(int width, String text) {
        int count = 1;
        String txt = text;
        while (width > getWidth()) {
            if (count < text.length()) {
                txt = "..." + text.substring(count, text.length()) + "...";
                width = getTextWiath(txt);
                count++;
            } else {
                break;
            }
        }
        return txt;
    }

    /**
     * 获取屏幕内可见的...text
     */
    private String getVisibleTextStart(int width, String text) {
        int count = 1;
        String txt = text;
        while (width > getWidth()) {
            if (count < text.length()) {
                txt = "..." + text.substring(count, text.length());
                width = getTextWiath(txt);
                count++;
            } else {
                break;
            }
        }
        return txt;
    }

    /**
     * 获取字符串的宽度
     */
    private int getTextWiath(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width() + getPaddingLeft() + getPaddingRight();
    }
}
