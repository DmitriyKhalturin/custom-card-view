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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import easy.peasy.cardview.R;

/**
 * A FrameLayout with a rounded corner background and shadow.
 * <p>
 * CardView uses <code>elevation</code> property on Lollipop for shadows and falls back to a
 * custom emulated shadow implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before Lollipop, CardView does
 * not clip its children that intersect with rounded corners. Instead, it adds padding to avoid such
 * intersection.
 * <p>
 * Before Lollipop, CardView adds padding to its content and draws shadows to that area. This
 * padding amount is equal to <code>maxCardElevation + (1 - cos45) * cornerRadius</code> on the
 * sides and <code>maxCardElevation * 1.5 + (1 - cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on CardView.
 * Instead, you can use content padding attributes in XML or
 * {@link #setContentPadding(int, int, int, int)} in code to set the padding between the edges of
 * the CardView and children of CardView.
 * <p>
 * Note that, if you specify exact dimensions for the CardView, because of the shadows, its content
 * area will be different between platforms before Lollipop and after Lollipop. By using api version
 * specific resource values, you can avoid these changes.
 * <p>
 * To change CardView's elevation in a backward compatible way, use
 * {@link #setCardElevation(float)}. CardView will use elevation API on Lollipop and before
 * Lollipop, it will change the shadow size. To avoid moving the View while shadow size is changing,
 * shadow size is clamped by {@link #getMaxCardElevation()}. If you want to change elevation
 * dynamically, you should call {@link #setMaxCardElevation(float)} when CardView is initialized.
 *
 * {@link R.attr#cardBackgroundColor}
 * {@link R.attr#cardCornerRadius}
 * {@link R.attr#cardElevation}
 * {@link R.attr#cardMaxElevation}
 * {@link R.attr#contentPadding}
 * {@link R.attr#contentPaddingLeft}
 * {@link R.attr#contentPaddingTop}
 * {@link R.attr#contentPaddingRight}
 * {@link R.attr#contentPaddingBottom}
 */
public class CardView extends FrameLayout {

  private static final int[] COLOR_RIPPLE_ATTR = {android.R.attr.colorControlHighlight};
  private static final CardViewImpl cardView;

  static {
    cardView = new CardViewApiLollipopImpl();
    cardView.initStatic();
  }

  /**
   * CardView requires to have a particular minimum size to draw shadows before API 21. If
   * developer also sets min width/height, they might be overridden.
   *
   * CardView works around this issue by recording user given parameters and using an internal
   * method to set them.
   */
  int mUserSetMinWidth, mUserSetMinHeight;

  final Rect mContentPadding = new Rect();

  final Rect mShadowBounds = new Rect();

  public CardView(@NonNull Context context) {
    this(context, null);
  }

  public CardView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, R.attr.cardViewStyle);
  }

  public CardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardView, defStyleAttr,
      R.style.CardView);
    ViewCompat.saveAttributeDataForStyleable(this,
      context, R.styleable.CardView, attrs, a, defStyleAttr, R.style.CardView);
    ColorStateList backgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white));
    float radius = a.getDimension(R.styleable.CardView_cardCornerRadius, 0);
    float elevation = a.getDimension(R.styleable.CardView_cardElevation, 0);
    float maxElevation = a.getDimension(R.styleable.CardView_cardMaxElevation, 0);
    int defaultPadding = a.getDimensionPixelSize(R.styleable.CardView_contentPadding, 0);
    mContentPadding.left = a.getDimensionPixelSize(R.styleable.CardView_contentPaddingLeft,
      defaultPadding);
    mContentPadding.top = a.getDimensionPixelSize(R.styleable.CardView_contentPaddingTop,
      defaultPadding);
    mContentPadding.right = a.getDimensionPixelSize(R.styleable.CardView_contentPaddingRight,
      defaultPadding);
    mContentPadding.bottom = a.getDimensionPixelSize(R.styleable.CardView_contentPaddingBottom,
      defaultPadding);
    if (elevation > maxElevation) {
      maxElevation = elevation;
    }
    mUserSetMinWidth = a.getDimensionPixelSize(R.styleable.CardView_android_minWidth, 0);
    mUserSetMinHeight = a.getDimensionPixelSize(R.styleable.CardView_android_minHeight, 0);
    int shadowStartColor = a.getColor(R.styleable.CardView_cardShadowStartColor, 0);
    int shadowEndColor = a.getColor(R.styleable.CardView_cardShadowEndColor, 0);
    final TypedArray aa = getContext().obtainStyledAttributes(COLOR_RIPPLE_ATTR);
    final int rippleColor = aa.getColor(0, 0);
    aa.recycle();
    CardViewDrawable cardViewDrawable = null;
    if (a.hasValue(R.styleable.CardView_cardBackgroundStartColor) && a.hasValue(R.styleable.CardView_cardBackgroundEndColor)){
      int startColor = a.getColor(R.styleable.CardView_cardBackgroundStartColor, 0);
      int endColor = a.getColor(R.styleable.CardView_cardBackgroundEndColor, 0);
      cardViewDrawable = new CardViewDrawable(startColor, endColor, radius, rippleColor);
    } else if (a.hasValue(R.styleable.CardView_cardBackgroundColor)) {
      int color = a.getColor(R.styleable.CardView_cardBackgroundColor, 0);
      cardViewDrawable = new CardViewDrawable(color, radius, rippleColor);
    }
    a.recycle();

    cardView.initialize(mCardViewDelegate, context, backgroundColor, radius,
      elevation, maxElevation, shadowStartColor, shadowEndColor);

    View view = new View(context);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    );
    if (cardViewDrawable != null) {
      view.setBackground(cardViewDrawable.getDrawable());
    }
    addView(view, params);
    view.requestLayout();
  }

  @Override
  public void setPadding(int left, int top, int right, int bottom) {
    // NO OP
  }

  @Override
  public void setPaddingRelative(int start, int top, int end, int bottom) {
    // NO OP
  }

  /**
   * Sets the padding between the Card's edges and the children of CardView.
   * <p>
   * Depending on platform version, CardView may update these values before calling
   * {@link android.view.View#setPadding(int, int, int, int)}.
   *
   * @param left   The left padding in pixels
   * @param top    The top padding in pixels
   * @param right  The right padding in pixels
   * @param bottom The bottom padding in pixels
   * {@link R.attr#contentPadding}
   * {@link R.attr#contentPaddingLeft}
   * {@link R.attr#contentPaddingTop}
   * {@link R.attr#contentPaddingRight}
   * {@link R.attr#contentPaddingBottom}
   */
  public void setContentPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
    mContentPadding.set(left, top, right, bottom);
    cardView.updatePadding(mCardViewDelegate);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    switch (widthMode) {
      case MeasureSpec.EXACTLY:
      case MeasureSpec.AT_MOST:
        final int minWidth = (int) Math.ceil(cardView.getMinWidth(mCardViewDelegate));
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
          MeasureSpec.getSize(widthMeasureSpec)), widthMode);
        break;
      case MeasureSpec.UNSPECIFIED:
        // Do nothing
        break;
    }

    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    switch (heightMode) {
      case MeasureSpec.EXACTLY:
      case MeasureSpec.AT_MOST:
        final int minHeight = (int) Math.ceil(cardView.getMinHeight(mCardViewDelegate));
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
          MeasureSpec.getSize(heightMeasureSpec)), heightMode);
        break;
      case MeasureSpec.UNSPECIFIED:
        // Do nothing
        break;
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void setMinimumWidth(int minWidth) {
    mUserSetMinWidth = minWidth;
    super.setMinimumWidth(minWidth);
  }

  @Override
  public void setMinimumHeight(int minHeight) {
    mUserSetMinHeight = minHeight;
    super.setMinimumHeight(minHeight);
  }

  /**
   * Updates the background color of the CardView
   *
   * @param color The new color to set for the card background
   * {@link R.attr#cardBackgroundColor}
   */
  public void setCardBackgroundColor(@ColorInt int color) {
    cardView.setBackgroundColor(mCardViewDelegate, ColorStateList.valueOf(color));
  }

  /**
   * Updates the background ColorStateList of the CardView
   *
   * @param color The new ColorStateList to set for the card background
   * {@link R.attr#cardBackgroundColor}
   */
  public void setCardBackgroundColor(@Nullable ColorStateList color) {
    cardView.setBackgroundColor(mCardViewDelegate, color);
  }

  /**
   * Returns the background color state list of the CardView.
   *
   * @return The background color state list of the CardView.
   */
  @NonNull
  public ColorStateList getCardBackgroundColor() {
    return cardView.getBackgroundColor(mCardViewDelegate);
  }

  /**
   * Returns the inner padding after the Card's left edge
   *
   * @return the inner padding after the Card's left edge
   */
  @Px
  public int getContentPaddingLeft() {
    return mContentPadding.left;
  }

  /**
   * Returns the inner padding before the Card's right edge
   *
   * @return the inner padding before the Card's right edge
   */
  @Px
  public int getContentPaddingRight() {
    return mContentPadding.right;
  }

  /**
   * Returns the inner padding after the Card's top edge
   *
   * @return the inner padding after the Card's top edge
   */
  @Px
  public int getContentPaddingTop() {
    return mContentPadding.top;
  }

  /**
   * Returns the inner padding before the Card's bottom edge
   *
   * @return the inner padding before the Card's bottom edge
   */
  @Px
  public int getContentPaddingBottom() {
    return mContentPadding.bottom;
  }

  /**
   * Updates the corner radius of the CardView.
   *
   * @param radius The radius in pixels of the corners of the rectangle shape
   * {@link R.attr#cardCornerRadius}
   * @see #setRadius(float)
   */
  public void setRadius(float radius) {
    cardView.setRadius(mCardViewDelegate, radius);
  }

  /**
   * Returns the corner radius of the CardView.
   *
   * @return Corner radius of the CardView
   * @see #getRadius()
   */
  public float getRadius() {
    return cardView.getRadius(mCardViewDelegate);
  }

  /**
   * Updates the backward compatible elevation of the CardView.
   *
   * @param elevation The backward compatible elevation in pixels.
   * {@link R.attr#cardElevation}
   * @see #getCardElevation()
   * @see #setMaxCardElevation(float)
   */
  public void setCardElevation(float elevation) {
    cardView.setElevation(mCardViewDelegate, elevation);
  }

  /**
   * Returns the backward compatible elevation of the CardView.
   *
   * @return Elevation of the CardView
   * @see #setCardElevation(float)
   * @see #getMaxCardElevation()
   */
  public float getCardElevation() {
    return cardView.getElevation(mCardViewDelegate);
  }

  /**
   * Updates the backward compatible maximum elevation of the CardView.
   * <p>
   * Calling this method has no effect if device OS version is Lollipop or newer.
   *
   * @param maxElevation The backward compatible maximum elevation in pixels.
   * {@link R.attr#cardMaxElevation}
   * @see #setCardElevation(float)
   * @see #getMaxCardElevation()
   */
  public void setMaxCardElevation(float maxElevation) {
    cardView.setMaxElevation(mCardViewDelegate, maxElevation);
  }

  /**
   * Returns the backward compatible maximum elevation of the CardView.
   *
   * @return Maximum elevation of the CardView
   * @see #setMaxCardElevation(float)
   * @see #getCardElevation()
   */
  public float getMaxCardElevation() {
    return cardView.getMaxElevation(mCardViewDelegate);
  }

  private final CardViewDelegate mCardViewDelegate = new CardViewDelegate() {
    private Drawable mCardBackground;

    @Override
    public void setCardBackground(Drawable drawable) {
      mCardBackground = drawable;
      setBackground(drawable);
    }

    @Override
    public void setShadowPadding(int left, int top, int right, int bottom) {
      mShadowBounds.set(left, top, right, bottom);
      CardView.super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
        right + mContentPadding.right, bottom + mContentPadding.bottom);
    }

    @Override
    public void setMinWidthHeightInternal(int width, int height) {
      if (width > mUserSetMinWidth) {
        CardView.super.setMinimumWidth(width);
      }
      if (height > mUserSetMinHeight) {
        CardView.super.setMinimumHeight(height);
      }
    }

    @Override
    public Drawable getCardBackground() {
      return mCardBackground;
    }
  };
}
