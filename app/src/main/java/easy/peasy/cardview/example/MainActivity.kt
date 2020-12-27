package easy.peasy.cardview.example

import android.content.res.Resources
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import easy.peasy.cardview.widget.CardView
import easy.peasy.cardview.widget.CornerRadius

class MainActivity : AppCompatActivity() {

  companion object {
    private const val DEFAULT_CARD_RADIUS = 16
    private const val DEFAULT_CARD_ELEVATION = 4

    fun dpToPx(dp: Int): Float = (dp * Resources.getSystem().displayMetrics.density)
    fun pxToDp(px: Int): Float = (px / Resources.getSystem().displayMetrics.density)
  }

  private val backgroundPaletteToggleButton: MaterialButtonToggleGroup by lazy { findViewById(R.id.backgroundPaletteToggleButton) }
  private val shadowPaletteToggleButton: MaterialButtonToggleGroup by lazy { findViewById(R.id.shadowPaletteToggleButton) }

  private val topLeftSeekBar: SeekBar by lazy { findViewById(R.id.topLeftSeekBar) }
  private val topRightSeekBar: SeekBar by lazy { findViewById(R.id.topRightSeekBar) }
  private val bottomRightSeekBar: SeekBar by lazy { findViewById(R.id.bottomRightSeekBar) }
  private val bottomLeftSeekBar: SeekBar by lazy { findViewById(R.id.bottomLeftSeekBar) }
  private val elevationSeekBar: SeekBar by lazy { findViewById(R.id.elevationSeekBar) }

  private val cardView: CardView by lazy { findViewById(R.id.cardView) }

  private val radiiBuffer = RadiiBuffer()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    cardView.radii = radiiBuffer.get()
    cardView.cardElevation = dpToPx(DEFAULT_CARD_ELEVATION)

    topLeftSeekBar.run {
      setOnSeekBarChangeListener(buildOnChangeRadius(CornerRadius.TOP_LEFT_INDEX))
      progress = DEFAULT_CARD_RADIUS
    }
    topRightSeekBar.run {
      setOnSeekBarChangeListener(buildOnChangeRadius(CornerRadius.TOP_RIGHT_INDEX))
      progress = DEFAULT_CARD_RADIUS
    }
    bottomRightSeekBar.run {
      setOnSeekBarChangeListener(buildOnChangeRadius(CornerRadius.BOTTOM_RIGHT_INDEX))
      progress = DEFAULT_CARD_RADIUS
    }
    bottomLeftSeekBar.run {
      setOnSeekBarChangeListener(buildOnChangeRadius(CornerRadius.BOTTOM_LEFT_INDEX))
      progress = DEFAULT_CARD_RADIUS
    }

    elevationSeekBar.run {
      setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
          if (fromUser) {
            cardView.cardElevation = dpToPx(progress)
          }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
      })
      progress = DEFAULT_CARD_ELEVATION
    }

    backgroundPaletteToggleButton.addOnButtonCheckedListener { _, checkedId, _ ->
      when(checkedId) {
        R.id.backgroundPaletteN1 -> {}
        R.id.backgroundPaletteN2 -> {}
        R.id.backgroundPaletteN3 -> {}
      }
    }

    shadowPaletteToggleButton.addOnButtonCheckedListener { _, checkedId, _ ->
      when(checkedId) {
        R.id.shadowPaletteN1 -> {}
        R.id.shadowPaletteN2 -> {}
        R.id.shadowPaletteN3 -> {}
      }
    }
  }

  private fun buildOnChangeRadius(radiusPosition: Int): SeekBar.OnSeekBarChangeListener {
    return object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
          radiiBuffer.set(radiusPosition, progress)
          cardView.radii = radiiBuffer.get()
          cardView.invalidate()
        }
      }
      override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
      override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }
  }

  private class RadiiBuffer {

    private val radii = FloatArray(CornerRadius.COUNT_RADII)

    init {
      radii[CornerRadius.TOP_LEFT_INDEX] = dpToPx(DEFAULT_CARD_RADIUS)
      radii[CornerRadius.TOP_RIGHT_INDEX] = dpToPx(DEFAULT_CARD_RADIUS)
      radii[CornerRadius.BOTTOM_RIGHT_INDEX] = dpToPx(DEFAULT_CARD_RADIUS)
      radii[CornerRadius.BOTTOM_LEFT_INDEX] = dpToPx(DEFAULT_CARD_RADIUS)
    }

    fun set(radiusPosition: Int, radiusDp: Int) { radii[radiusPosition] = dpToPx(radiusDp) }

    fun get(): FloatArray = radii
  }
}
