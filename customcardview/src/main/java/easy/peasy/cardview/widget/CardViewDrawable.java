package easy.peasy.cardview.widget;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

import androidx.core.graphics.drawable.DrawableCompat;

class CardViewDrawable {

  static Bitmap toBitmap(Drawable drawable, int width, int height) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else {
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);

      return bitmap;
    }
  }

  CardViewDrawable(int startColor, int endColor, CornerRadius cornerRadius, int rippleColor) {
    GradientDrawable drawable = new GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT,
      new int[]{startColor, endColor}
    );
    drawable.setCornerRadii(cornerRadius.getRadii());

    setRippleEffect(drawable, rippleColor);
  }

  CardViewDrawable(int color, CornerRadius cornerRadius, int rippleColor) {
    PaintDrawable drawable = new PaintDrawable(color);
    drawable.setCornerRadii(cornerRadius.getRadii());

    setRippleEffect(drawable, rippleColor);
  }

  private void  setRippleEffect(Drawable drawable, int rippleColor) {
    this.drawable =  new RippleDrawable(ColorStateList.valueOf(rippleColor), drawable, null);
  }

  private Drawable drawable;

  public Drawable getDrawable() {
    return drawable;
  }
}
