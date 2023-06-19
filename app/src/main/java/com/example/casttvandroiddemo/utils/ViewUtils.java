package com.example.casttvandroiddemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class ViewUtils {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerRadius) {
        // 创建一个正方形的Bitmap
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // 创建一个Canvas对象，将output作为绘制的目标
        Canvas canvas = new Canvas(output);

        // 创建一个Paint对象，并设置其属性
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // 创建一个RectF对象，用于定义圆角的矩形区域
        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 将Bitmap绘制到Canvas上，并通过RectF对象定义圆角
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        // 设置Paint的Xfermode为PorterDuff.Mode.SRC_IN，以保留与圆角重叠的部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 将原始的Bitmap绘制到Canvas上
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

}
