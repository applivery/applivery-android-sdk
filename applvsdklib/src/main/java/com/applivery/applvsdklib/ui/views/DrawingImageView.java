package com.applivery.applvsdklib.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.applivery.applvsdklib.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Hernandez on 13/11/16.
 */
public class DrawingImageView extends ImageView {

  private final float width = 4f;
  private final List<Holder> holderList = new ArrayList<Holder>();
  private int color;

  public DrawingImageView(Context context) {
    super(context);
    init(context);
  }

  public DrawingImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public DrawingImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    color = context.getResources().getColor(R.color.applivery_drawing_color);
    setDrawingCacheEnabled(true);
    holderList.add(new Holder(color, width));
  }

  public Bitmap getBitmap() {
    return Bitmap.createBitmap(getDrawingCache());
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (Holder holder : holderList) {
      canvas.drawPath(holder.path, holder.paint);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    float eventX = event.getX();
    float eventY = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        holderList.add(new Holder(color, width));
        holderList.get(holderList.size() - 1).path.moveTo(eventX, eventY);
        return true;
      case MotionEvent.ACTION_MOVE:
        holderList.get(holderList.size() - 1).path.lineTo(eventX, eventY);
        break;
      case MotionEvent.ACTION_UP:
        break;
      default:
        return false;
    }

    invalidate();
    return true;
  }

  private class Holder {
    Path path;
    Paint paint;

    Holder(int color, float width) {
      path = new Path();
      paint = new Paint();
      paint.setAntiAlias(true);
      paint.setStrokeWidth(width);
      paint.setColor(color);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeJoin(Paint.Join.ROUND);
      paint.setStrokeCap(Paint.Cap.ROUND);
    }
  }
}
