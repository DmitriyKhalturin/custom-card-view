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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

class CardViewBaseImpl implements CardViewImpl {

  // @SuppressWarnings("WeakerAccess") /* synthetic access */
  // final RectF mCornerRect = new RectF();

  @Override
  public void initStatic() {
    // Draws a round rect using 7 draw operations. This is faster than using
    // canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
    // shapes.
    // RoundRectDrawableWithShadow.sRoundRectHelper =
    //   new RoundRectDrawableWithShadow.RoundRectHelper() {
    //     @Override
    //     public void drawRoundRect(Canvas canvas, RectF bounds, CornerRadius cornerRadius,
    //                               Paint paint) {
    //       RoundRectShape shape = new RoundRectShape(cornerRadius.getRadii(), null, null);
    //       shape.resize(bounds.width(), bounds.height());
    //       shape.draw(canvas, paint);
    //     }
    //   };
  }

  @Override
  public void initialize(CardViewDelegate cardView, Context context,
                         ColorStateList backgroundColor,
                         CornerRadius cornerRadius,
                         float elevation, float maxElevation,
                         int shadowStartColor, int shadowEndColor) {
    RoundRectDrawableWithShadow background = createBackground(context,
      backgroundColor,
      cornerRadius,
      elevation, maxElevation,
      shadowStartColor, shadowEndColor);
    background.setAddPaddingForCorners();
    cardView.setCardBackground(background);
    updatePadding(cardView);
  }

  private RoundRectDrawableWithShadow createBackground(Context context,
                                                       ColorStateList backgroundColor,
                                                       CornerRadius cornerRadius,
                                                       float elevation, float maxElevation,
                                                       int shadowStartColor, int shadowEndColor) {
    return new RoundRectDrawableWithShadow(context.getResources(),
      backgroundColor,
      cornerRadius,
      elevation, maxElevation,
      shadowStartColor, shadowEndColor);
  }

  @Override
  public void updatePadding(CardViewDelegate cardView) {
    Rect shadowPadding = new Rect();
    getShadowBackground(cardView).getMaxShadowAndCornerPadding(shadowPadding);
    cardView.setMinWidthHeightInternal((int) Math.ceil(getMinWidth(cardView)),
      (int) Math.ceil(getMinHeight(cardView)));
    cardView.setShadowPadding(shadowPadding.left, shadowPadding.top,
      shadowPadding.right, shadowPadding.bottom);
  }

  @Override
  public void setBackgroundColor(CardViewDelegate cardView, @Nullable ColorStateList color) {
    getShadowBackground(cardView).setColor(color);
  }

  @Override
  public ColorStateList getBackgroundColor(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getColor();
  }

  @Override
  public void setCornerRadii(CardViewDelegate cardView, float[] radii) {
    getShadowBackground(cardView).setCornerRadii(radii);
    updatePadding(cardView);
  }

  @Override
  public float[] getCornerRadii(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getCornerRadii();
  }

  @Override
  public void setElevation(CardViewDelegate cardView, float elevation) {
    getShadowBackground(cardView).setShadowSize(elevation);
  }

  @Override
  public float getElevation(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getShadowSize();
  }

  @Override
  public void setMaxElevation(CardViewDelegate cardView, float maxElevation) {
    getShadowBackground(cardView).setMaxShadowSize(maxElevation);
    updatePadding(cardView);
  }

  @Override
  public float getMaxElevation(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getMaxShadowSize();
  }

  @Override
  public void setShadowStartColor(CardViewDelegate cardView, @ColorInt int color) {
    getShadowBackground(cardView).setShadowStartColor(color);
  }

  @Override
  @ColorInt
  public int getShadowStartColor(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getShadowStartColor();
  }

  @Override
  public void setShadowEndColor(CardViewDelegate cardView, @ColorInt int color) {
    getShadowBackground(cardView).setShadowEndColor(color);
  }

  @Override
  @ColorInt
  public int getShadowEndColor(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getShadowEndColor();
  }

  @Override
  public float getMinWidth(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getMinWidth();
  }

  @Override
  public float getMinHeight(CardViewDelegate cardView) {
    return getShadowBackground(cardView).getMinHeight();
  }

  private RoundRectDrawableWithShadow getShadowBackground(CardViewDelegate cardView) {
    return ((RoundRectDrawableWithShadow) cardView.getCardBackground());
  }
}
