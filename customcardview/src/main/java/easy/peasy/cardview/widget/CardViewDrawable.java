package easy.peasy.cardview.widget;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

class CardViewDrawable {

  CardViewDrawable(int startColor, int endColor, float radius, int rippleColor) {
    GradientDrawable drawable = new GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT,
      new int[]{startColor, endColor}
    );
    drawable.setCornerRadius(radius);

    setRippleEffect(drawable, rippleColor);
  }

  CardViewDrawable(int color, float radius, int rippleColor) {
    PaintDrawable drawable = new PaintDrawable(color);
    drawable.setCornerRadius(radius);

    setRippleEffect(drawable, rippleColor);
  }

  private void  setRippleEffect(Drawable drawable, int rippleColor) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      this.drawable =  new RippleDrawable(ColorStateList.valueOf(rippleColor), drawable, null);
    } else {
      this.drawable = drawable;
    }
  }

  private Drawable drawable;

  public Drawable getDrawable() {
    return drawable;
  }
}
