package easy.peasy.cardview.widget;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.RippleDrawable;

class CardViewDrawable {

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

  private void setRippleEffect(Drawable drawable, int rippleColor) {
    this.drawable =  new RippleDrawable(ColorStateList.valueOf(rippleColor), drawable, null);
  }

  private Drawable drawable;

  public Drawable getDrawable() {
    return drawable;
  }
}
