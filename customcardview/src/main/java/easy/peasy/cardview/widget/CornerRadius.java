package easy.peasy.cardview.widget;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class CornerRadius {

  private static final int COUNT_RADII = 4;

  private static final int TOP_LEFT_INDEX = 0;
  private static final int TOP_RIGHT_INDEX = 1;
  private static final int BOTTOM_RIGHT_INDEX = 2;
  private static final int BOTTOM_LEFT_INDEX = 3;

  static CornerRadius build(float anyRadius,
                            float radiusTopLeft, float radiusTopRight,
                            float radiusBottomRight, float radiusBottomLeft) {
    CornerRadius cornerRadius = new CornerRadius();
    float[] radii = new float[] {radiusTopLeft, radiusTopRight, radiusBottomRight, radiusBottomLeft};
    float sumRadii = 0;

    for (int i = 0; i < COUNT_RADII; i++) {
      float radius = radii[i];
      if (radius < 0f) {
        throw new IllegalArgumentException("Invalid radius " + radius + ". Must be >= 0");
      }
      sumRadii += radius;
    }

    if (sumRadii > 0) {
      cornerRadius.mCornerRadii = radii;
    } else {
      cornerRadius.mCornerRadii = new float[] {anyRadius, anyRadius, anyRadius, anyRadius};
    }

    return cornerRadius;
  }

  /**
   * The corners are ordered top-left, top-right, bottom-right, bottom-left.
   */
  private float[] mCornerRadii;


  float[] getCornerRadii() {
    return mCornerRadii;
  }

  /**
   * Specify radii for each of the 4 corners. For each corner, the array contains 2 values, [X_radius, Y_radius].
   * The corners are ordered top-left, top-right, bottom-right, bottom-left radii the x and y radii of the corners.
   */
  float[] getRadii() {
    float[] radii = new float[8];
    for (int i = 0; i < COUNT_RADII; i++) {
      radii[2 * i] = mCornerRadii[i];
      radii[2 * i + 1] = mCornerRadii[i];
    }
    return radii;
  }

  float getTopLeft() {
    return mCornerRadii[TOP_LEFT_INDEX];
  }

  float getTopRight() {
    return mCornerRadii[TOP_RIGHT_INDEX];
  }

  float getBottomRight() {
    return mCornerRadii[BOTTOM_RIGHT_INDEX];
  }

  float getBottomLeft() {
    return mCornerRadii[BOTTOM_LEFT_INDEX];
  }

  float getMaxRadius() {
    float maxRadius = 0;
    for (int i = 0; i < COUNT_RADII; i++) {
      maxRadius = Math.max(maxRadius, mCornerRadii[i]);
    }
    return maxRadius;
  }

  void addOffset(float offset) {
    for (int i = 0; i < COUNT_RADII; i++) {
      mCornerRadii[i] = (int) (mCornerRadii[i] + offset);
    }
  }

  public boolean equals(float[] radii) {
    return Arrays.equals(mCornerRadii, radii);
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == null) {
      return false;
    }
    else if (!(obj instanceof CornerRadius))
      return false;
    else {
      return ((CornerRadius) obj).equals(mCornerRadii);
    }
  }
}
