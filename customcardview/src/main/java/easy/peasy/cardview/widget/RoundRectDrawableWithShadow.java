/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package easy.peasy.cardview.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import easy.peasy.cardview.R;

/**
 * A rounded rectangle drawable which also includes a shadow around.
 */
class RoundRectDrawableWithShadow extends Drawable {

  private static final float SHADOW_MULTIPLIER = 1.5f;

  private static final int SHADOW_OFFSET = 25;

  private static final float RADIUS_OFFSET = .5f;

  private final int mInsetShadow; // extra shadow to avoid gaps between card and shadow

  /*
   * This helper is set by CardView implementations.
   * <p>
   * Prior to API 17, canvas.drawRoundRect is expensive; which is why we need this interface
   * to draw efficient rounded rectangles before 17.
   * */
  // static RoundRectHelper sRoundRectHelper;

  private final Paint mPaint;

  private final Paint mCornerShadowPaint;

  private final Paint mEdgeShadowPaint;

  private final RectF mCardBounds;

  private CornerRadius mCornerRadius;

  private Path mCornerShadowPath;

  // actual value set by developer
  private float mRawMaxShadowSize;

  // multiplied value to account for shadow offset
  private float mShadowSize;

  // actual value set by developer
  private float mRawShadowSize;

  private ColorStateList mBackground;

  private boolean mDirty = true;

  private int mShadowStartColor;

  private int mShadowEndColor;

  /**
   * If shadow size is set to a value above max shadow, we print a warning
   */
  private boolean mPrintedShadowClipWarning = false;

  RoundRectDrawableWithShadow(Resources resources,
                              ColorStateList backgroundColor,
                              CornerRadius cornerRadius,
                              float shadowSize, float maxShadowSize, int shadowStartColor, int shadowEndColor) {
    if (shadowStartColor != 0) {
      mShadowStartColor = shadowStartColor;
    } else {
      mShadowStartColor = resources.getColor(R.color.cardview_shadow_start_color);
    }
    if (shadowEndColor != 0) {
      mShadowEndColor = shadowEndColor;
    } else {
      mShadowEndColor = resources.getColor(R.color.cardview_shadow_end_color);
    }
    mInsetShadow = resources.getDimensionPixelSize(R.dimen.cardview_compat_inset_shadow);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    setBackground(backgroundColor);
    mCornerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mCornerShadowPaint.setStyle(Paint.Style.FILL);
    mCornerRadius = cornerRadius;
    mCornerRadius.addOffset(RADIUS_OFFSET);
    mCardBounds = new RectF();
    mEdgeShadowPaint = new Paint(mCornerShadowPaint);
    mEdgeShadowPaint.setAntiAlias(false);
    setShadowSize(shadowSize, maxShadowSize);
  }

  private void setBackground(ColorStateList color) {
    mBackground = (color == null) ?  ColorStateList.valueOf(Color.TRANSPARENT) : color;
    mPaint.setColor(mBackground.getColorForState(getState(), mBackground.getDefaultColor()));
  }

  /**
   * Casts the value to an even integer.
   */
  private int toEven(float value) {
    int i = (int) (value + .5f);
    if (i % 2 == 1) {
      return i - 1;
    }
    return i;
  }

  void setAddPaddingForCorners() {
    invalidateSelf();
  }

  @Override
  public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
    mCornerShadowPaint.setAlpha(alpha);
    mEdgeShadowPaint.setAlpha(alpha);
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    mDirty = true;
  }

  private void setShadowSize(float shadowSize, float maxShadowSize) {
    if (shadowSize < 0f) {
      throw new IllegalArgumentException("Invalid shadow size " + shadowSize
        + ". Must be >= 0");
    }
    if (maxShadowSize < 0f) {
      throw new IllegalArgumentException("Invalid max shadow size " + maxShadowSize
        + ". Must be >= 0");
    }
    shadowSize = toEven(shadowSize);
    maxShadowSize = toEven(maxShadowSize);
    if (shadowSize > maxShadowSize) {
      shadowSize = maxShadowSize;
      if (!mPrintedShadowClipWarning) {
        mPrintedShadowClipWarning = true;
      }
    }
    if (mRawShadowSize == shadowSize && mRawMaxShadowSize == maxShadowSize) {
      return;
    }
    mRawShadowSize = shadowSize;
    mRawMaxShadowSize = maxShadowSize;
    mShadowSize = (int) (shadowSize * SHADOW_MULTIPLIER + mInsetShadow + .5f);
    mDirty = true;
    invalidateSelf();
  }

  @Override
  public boolean getPadding(Rect padding) {
    int bottomOffset = (int) Math.ceil(calculateVerticalPadding(mRawMaxShadowSize, mCornerRadius));
    padding.set(0, 0, 0, bottomOffset);
    return true;
  }

  static float calculateVerticalPadding(float maxShadowSize, CornerRadius cornerRadius) {
    return maxShadowSize * SHADOW_MULTIPLIER;
  }

  @Override
  protected boolean onStateChange(int[] stateSet) {
    final int newColor = mBackground.getColorForState(stateSet, mBackground.getDefaultColor());
    if (mPaint.getColor() == newColor) {
      return false;
    }
    mPaint.setColor(newColor);
    mDirty = true;
    invalidateSelf();
    return true;
  }

  @Override
  public boolean isStateful() {
    return (mBackground != null && mBackground.isStateful()) || super.isStateful();
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    mPaint.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  /**
   * The corners are ordered top-left, top-right, bottom-right, bottom-left.
   */
  void setCornerRadii(float[] radii) {
    int countRadii = radii.length;
    if (countRadii != 4) {
      throw new IllegalArgumentException("Invalid count radii " + countRadii + ". Must be == 4");
    }

    for (int i = 0; i < 4; i++) {
      float radius = radii[i];
      if (radius < 0f) {
        throw new IllegalArgumentException("Invalid radius " + radius + ". Must be >= 0");
      }
    }

    CornerRadius cornerRadius = CornerRadius.build(0f, radii[0], radii[1], radii[2], radii[3]);
    cornerRadius.addOffset(RADIUS_OFFSET);

    if (mCornerRadius.equals(cornerRadius)) {
      return;
    }

    mCornerRadius = cornerRadius;
    mDirty = true;
    invalidateSelf();
  }

  @Override
  public void draw(Canvas canvas) {
    if (mDirty) {
      buildComponents(getBounds());
      mDirty = false;
    }
    canvas.translate(0, mRawShadowSize / 2);
    drawShadow(canvas);
    canvas.translate(0, -mRawShadowSize / 2);
    // sRoundRectHelper.drawRoundRect(canvas, mCardBounds, mCornerRadius, mPaint);
  }

  private void drawShadow(Canvas canvas) {
    final float edgeShadowTop = -mCornerRadius.getMaxRadius() - mShadowSize;
    final float inset = mCornerRadius.getMaxRadius() + mInsetShadow + mRawShadowSize / 2;
    final boolean drawHorizontalEdges = - 2 * SHADOW_OFFSET + mCardBounds.width() - 2 * inset > 0;
    // RB
    int saved = canvas.save();
    canvas.translate(-SHADOW_OFFSET + mCardBounds.right - inset, mCardBounds.bottom - inset);
    canvas.rotate(180f);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    // B
    if (drawHorizontalEdges) {
      canvas.drawRect(
        0,
        edgeShadowTop,
        - 2 * SHADOW_OFFSET + mCardBounds.width() - 2 * inset,
        -mCornerRadius.getMaxRadius() + mShadowSize,
        mEdgeShadowPaint
      );
    }
    canvas.restoreToCount(saved);
    // LB
    saved = canvas.save();
    canvas.translate(SHADOW_OFFSET + mCardBounds.left + inset, mCardBounds.bottom - inset);
    canvas.rotate(270f);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    canvas.restoreToCount(saved);
  }

  private void buildShadowCorners() {
    RectF innerBounds = new RectF(-mCornerRadius.getMaxRadius(), -mCornerRadius.getMaxRadius(), mCornerRadius.getMaxRadius(), mCornerRadius.getMaxRadius());
    RectF outerBounds = new RectF(innerBounds);
    outerBounds.inset(-mShadowSize, -mShadowSize);

    if (mCornerShadowPath == null) {
      mCornerShadowPath = new Path();
    } else {
      mCornerShadowPath.reset();
    }
    mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
    mCornerShadowPath.moveTo(-mCornerRadius.getMaxRadius(), 0);
    mCornerShadowPath.rLineTo(-mShadowSize, 0);
    // outer arc
    mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false);
    // inner arc
    mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false);
    mCornerShadowPath.close();
    float startRatio = mCornerRadius.getMaxRadius() / (mCornerRadius.getMaxRadius() + mShadowSize);
    mCornerShadowPaint.setShader(new RadialGradient(0, 0, mCornerRadius.getMaxRadius() + mShadowSize,
      new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
      new float[]{0f, startRatio, 1f},
      Shader.TileMode.CLAMP));

    // we offset the content shadowSize/2 pixels up to make it more realistic.
    // this is why edge shadow shader has some extra space
    // When drawing bottom edge shadow, we use that extra space.
    mEdgeShadowPaint.setShader(new LinearGradient(0, -mCornerRadius.getMaxRadius() + mShadowSize,
      0, -mCornerRadius.getMaxRadius() - mShadowSize,
      new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
      new float[]{0f, .5f, 1f},
      Shader.TileMode.CLAMP));
    mEdgeShadowPaint.setAntiAlias(false);
  }

  private void buildComponents(Rect bounds) {
    // Card is offset SHADOW_MULTIPLIER * maxShadowSize to account for the shadow shift.
    // We could have different top-bottom offsets to avoid extra gap above but in that case
    // center aligning Views inside the CardView would be problematic.
    final float verticalOffset = mRawMaxShadowSize * SHADOW_MULTIPLIER;
    mCardBounds.set(bounds.left + mRawMaxShadowSize, bounds.top + verticalOffset,
      bounds.right - mRawMaxShadowSize, bounds.bottom - verticalOffset);
    buildShadowCorners();
  }

  float[] getCornerRadii() {
    return mCornerRadius.getCornerRadii();
  }

  void getMaxShadowAndCornerPadding(Rect into) {
    getPadding(into);
  }

  void setShadowSize(float size) {
    setShadowSize(size, mRawMaxShadowSize);
  }

  void setMaxShadowSize(float size) {
    setShadowSize(mRawShadowSize, size);
  }

  float getShadowSize() {
    return mRawShadowSize;
  }

  float getMaxShadowSize() {
    return mRawMaxShadowSize;
  }

  float getMinWidth() {
    final float calculateMaxShadowSize = (SHADOW_OFFSET + mCornerRadius.getMaxRadius() + mInsetShadow + mRawMaxShadowSize / 2);
    final float content = 2 * Math.max(mRawMaxShadowSize, calculateMaxShadowSize);
    return content + (mRawMaxShadowSize + mInsetShadow) * 2;
  }

  float getMinHeight() {
    final float calculateMaxShadowSize = (mCornerRadius.getMaxRadius() + mInsetShadow + mRawMaxShadowSize * SHADOW_MULTIPLIER / 2);
    final float content = Math.max(mRawMaxShadowSize, calculateMaxShadowSize);
    return content + (mRawMaxShadowSize * SHADOW_MULTIPLIER + mInsetShadow) * 2;
  }

  void setShadowStartColor(int color) {
    mShadowStartColor = color;
    mDirty = true;
    invalidateSelf();
  }

  int getShadowStartColor() {
    return mShadowStartColor;
  }

  void setShadowEndColor(int color) {
    mShadowEndColor = color;
    mDirty = true;
    invalidateSelf();
  }

  int getShadowEndColor() {
    return mShadowEndColor;
  }

  void setColor(@Nullable ColorStateList color) {
    setBackground(color);
    invalidateSelf();
  }

  ColorStateList getColor() {
    return mBackground;
  }

  // interface RoundRectHelper {
  //   void drawRoundRect(Canvas canvas, RectF bounds, CornerRadius cornerRadius, Paint paint);
  // }
}
