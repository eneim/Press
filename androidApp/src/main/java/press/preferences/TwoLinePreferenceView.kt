package press.preferences

import android.content.Context
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.attaches
import com.jakewharton.rxbinding3.view.detaches
import com.squareup.contour.ContourLayout
import me.saket.press.shared.listen
import me.saket.press.shared.preferences.Setting
import me.saket.press.shared.theme.TextStyles.mainTitle
import me.saket.press.shared.theme.TextStyles.smallBody
import me.saket.press.shared.theme.TextView
import press.extensions.rippleDrawable
import press.extensions.textColor
import press.theme.themePalette

/** Shows a title and a subtitle. */
class TwoLinePreferenceView(context: Context) : ContourLayout(context) {
  private val titleView = TextView(context, mainTitle)
  private val subtitleView = TextView(context, smallBody)

  init {
    titleView.layoutBy(
      x = matchParentX(marginLeft = 20.dip, marginRight = 20.dip),
      y = topTo { parent.top() + 16.dip }
    )
    subtitleView.layoutBy(
      x = matchXTo(titleView),
      y = topTo { titleView.bottom() }
    )
    contourHeightOf {
      (if (subtitleView.isVisible) subtitleView.bottom() else titleView.bottom()) + 16.ydip
    }

    titleView.textColor = themePalette().textColorPrimary
    subtitleView.textColor = themePalette().textColorSecondary
    background = rippleDrawable()
  }

  fun render(title: String, subtitle: CharSequence? = null, onClick: () -> Unit) {
    titleView.text = title
    subtitleView.text = subtitle
    subtitleView.isVisible = subtitle != null
    setOnClickListener { onClick() }
  }

  fun <T : Any> render(
    setting: Setting<T>,
    title: String,
    subtitle: (T) -> CharSequence,
    onClick: () -> Unit
  ) {
    render(title, subtitle = "", onClick)

    attaches()
      .switchMap { setting.listen() }
      .takeUntil(detaches())
      .subscribe { (preference) ->
        render(title, subtitle(preference!!), onClick)
      }
  }
}
