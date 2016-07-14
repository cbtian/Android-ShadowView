package com.kevin.shadowview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouwk on 2016/7/14 0014.
 */
public class ShadowView extends View {

    private int defaultShadowColor = Color.parseColor("#EEEEEE");
    private int shadowSize = 70;


    private String text;
    private Drawable background;
    private int textColor;
    private int shadowcolor;
    private int textSize;
    private Rect textBound;
    private int radius;

    private Paint paint;

    private final int LEFT 			= 0;
    private final int TOP			= 1;
    private final int RIGHT 		= 2;
    private final int BOTTOM 		= 4;
    private final int LEFTTOP 		= 8;
    private final int RIGHTTOP 		= 16;
    private final int RIGHTBOTTOM	= 32;
    private final int LEFTBOTTOM 	= 64;

    private int shadowDirection = LEFTTOP;
    private List<Shadow> shadowList;

    public ShadowView(Context context) {
        this(context, null);
    }

    public ShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ShadowView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ShadowView_sv_background:
                    background = a.getDrawable(attr);
                    break;
                case R.styleable.ShadowView_sv_text:
                    text = a.getString(attr);
                    break;
                case R.styleable.ShadowView_sv_textColor:
                    textColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ShadowView_sv_textSize:
                    textSize = a.getDimensionPixelSize(attr, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ShadowView_sv_shadowColor:
                    shadowcolor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ShadowView_sv_radius:
                    radius = a.getDimensionPixelSize(attr, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
                                    getResources().getDisplayMetrics()));
            }

        }
        a.recycle();

        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setFakeBoldText(true);
        paint.setColor(textColor);
        textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        calcShadow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        // 设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            case MeasureSpec.AT_MOST:// WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight() + textBound.width();
                break;
        }

        // 设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:
                height = getPaddingTop() + getPaddingBottom() + textBound.height();
                break;
        }

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
         // 将绘制操作保存到新的图层
        paint.setColor(Color.parseColor("#6DD7B5"));
        RectF rect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRoundRect(rect, radius, radius, paint);

        paint.setColor(shadowcolor);
        for (Shadow shadow : shadowList) {
            canvas.drawText(text, getWidth() / 2 - textBound.width() / 2 + shadow.getDx(), getHeight() / 2 + textBound.height() / 2 + shadow.getDy(), paint);
        }

        paint.setColor(textColor);
        canvas.drawText(text, getWidth() / 2 - textBound.width() / 2, getHeight() / 2 + textBound.height() / 2, paint);

        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(0, radius);
        path.addArc(new RectF(0, 0, 2 * radius, 2 * radius), 180, 90);
        path.lineTo(radius, radius);
        path.lineTo(0, radius);
        path.close();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawPath(path, paint);
        paint.setXfermode(null);

//        path.lineTo(getMeasuredWidth() - radius, 0);
//        path.addArc(new RectF(getMeasuredWidth() - 2 * radius, 0, getMeasuredWidth(), 2 * radius), 270, 90);
//        path.lineTo(getMeasuredWidth(), getMeasuredHeight() - radius);
//        path.addArc(new RectF(getMeasuredWidth() - 2 * radius, getMeasuredHeight() - 2 * radius, getMeasuredWidth(), getMeasuredHeight()), 0, 90);
//        path.lineTo(radius, getMeasuredHeight());
//        path.addArc(new RectF(0, getMeasuredHeight() - 2 * radius, 2 * radius, getMeasuredHeight()), 90, 90);
//        path.moveTo(0, radius);
//        path.close();
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//        canvas.drawPath(path, paint);
//        paint.setXfermode(null);

//        paint.setColor(Color.RED);
//        Path path = new Path();
//        path.moveTo(0, radius);
//        path.addArc(new RectF(0, 0, 2 * radius, 2 * radius), 180, 90);
//        path.lineTo(getMeasuredWidth() - radius, 0);
//        path.addArc(new RectF(getMeasuredWidth() - 2 * radius, 0, getMeasuredWidth(), 2 * radius), 270, 90);
//        path.lineTo(getMeasuredWidth(), getMeasuredHeight() - radius);
//        path.addArc(new RectF(getMeasuredWidth() - 2 * radius, getMeasuredHeight() - 2 * radius, getMeasuredWidth(), getMeasuredHeight()), 0, 90);
//        path.lineTo(radius, getMeasuredHeight());
//        path.addArc(new RectF(0, getMeasuredHeight() - 2 * radius, 2 * radius, getMeasuredHeight()), 90, 90);
//        path.moveTo(0, radius);
//        path.close();
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//        canvas.drawPath(path, paint);
//        paint.setXfermode(null);

//        Path path1 = new Path();
//        path1.moveTo(0, 0);
//        path1.lineTo(0, radius);
//        path1.addArc(new RectF(0, 0, 2 * radius, 2 * radius), 180, 90);
//        path1.lineTo(0, 0);
//        path1.close();
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPath(path1, paint);
//        paint.setXfermode(null);
    }

    private void calcShadow() {
        shadowList = new ArrayList<>(shadowSize);
        for (int i = 0, len = shadowSize; i < len; i++) {
            switch (shadowDirection) {
                case LEFT:
                    shadowList.add(new Shadow(1, -i, 0, defaultShadowColor));
                    break;
				case TOP:
                    shadowList.add(new Shadow(1, 0, -i, defaultShadowColor));
					break;
//				case RIGHT:
//					textshadow += i + 'px 0 0 ' + color + ',';
//					break;
//				case BOTTOM:
//					textshadow += '0 ' + i + 'px 0 ' + color + ',';
//					break;
//
				case LEFTTOP:
//					textshadow += -i + 'px ' + -i + 'px 0 ' + color + ',';
                    shadowList.add(new Shadow(1, -i, -i, defaultShadowColor));
					break;
//				case RIGHTTOP:
//					textshadow += i + 'px ' + -i + 'px 0 ' + color + ',';
//					break;
//				case BOTTOMR:
//					textshadow += -i + 'px ' + i + 'px 0 ' + color + ',';
//					break;
				case RIGHTBOTTOM:
//					textshadow += i + 'px ' + i + 'px 0 ' + color + ',';
                    shadowList.add(new Shadow(1, i, i, defaultShadowColor));
					break;
//				default:
//					textshadow += i + 'px ' + i + 'px 0 ' + color + ',';
//					break;
            }
        }
    }
}
