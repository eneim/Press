package me.saket.press.shared.theme.palettes

import com.github.ajalt.colormath.RGB
import me.saket.press.shared.theme.DisplayUnits
import me.saket.press.shared.theme.blendWith
import me.saket.press.shared.theme.darkenBy
import me.saket.press.shared.theme.darkenColorBy
import me.saket.press.shared.theme.saturateBy
import me.saket.press.shared.theme.toHsvColor
import me.saket.press.shared.theme.toRgbColorInt
import me.saket.press.shared.theme.withAlpha
import me.saket.wysiwyg.style.WysiwygStyle
import me.saket.wysiwyg.style.WysiwygStyle.BlockQuote
import me.saket.wysiwyg.style.WysiwygStyle.Code
import me.saket.wysiwyg.style.WysiwygStyle.Heading
import me.saket.wysiwyg.style.WysiwygStyle.Link
import me.saket.wysiwyg.style.WysiwygStyle.ThematicBreak
import kotlin.DeprecationLevel.ERROR
import kotlin.math.roundToInt

sealed class ThemePalette(
  val name: String,
  val isLightTheme: Boolean,
  val primaryColor: Int,
  val primaryColorDark: Int,
  val accentColor: Int,
  val window: WindowPalette,
  val markdown: MarkdownPalette,
  val textColorHeading: Int,
  val textColorPrimary: Int,  // Equivalent to base00 in terminal.
  val textColorWarning: Int,
  val fabColor: Int           // Floating Action Button.
) {

  val fabIcon: Int
    get() = fabColor.blendWith(if (isLightTheme) WHITE else BLACK, ratio = 0.65f)

  val fabColorPressed: Int
    get() = fabColor.toHsvColor().darkenBy(0.3f).saturateBy(0.5f).toRgbColorInt()

  val divider: Int
    get() = window.backgroundColor.darkenColorBy(0.15f)

  val buttonNormal: Int
    get() = window.backgroundColor.blendWith(if (isLightTheme) WHITE else BLACK, ratio = 0.2f)

  val textColorSecondary =
    textColorPrimary.blendWith(window.backgroundColor, ratio = 0.30f)

  val textColorHint =
    textColorPrimary.blendWith(window.backgroundColor, ratio = 0.50f)

  val textColorHighlight =
    window.backgroundColor.blendWith(accentColor, ratio = 1f).withAlpha(0.4f)

  init {
    // Need these colors to be translucent to avoid covering the
    // cursor because it is drawn behind ForegroundColorSpan on Android.
    check(RGB.fromInt(textColorHighlight).alpha < 1f)
  }

  fun pressedColor(normalColor: Int): Int {
    return if (normalColor == TRANSPARENT) {
      BLACK.withAlpha(0.1f)
    } else {
      normalColor.darkenColorBy(if (isLightTheme) 0.2f else -0.2f)
    }
  }

  companion object {
    private const val BLACK = 0xFF000000.toInt()
    private const val WHITE = 0xFFFFFFFF.toInt()
    private const val TRANSPARENT: Int = 0
  }
}

data class WindowPalette(
  val backgroundColor: Int, // Equivalent to base3 color in terminal.
  val elevatedBackgroundColor: Int
)

data class MarkdownPalette(
  val syntaxColor: Int,
  val blockQuoteTextColor: Int,
  val linkTextColor: Int,
  val linkUrlColor: Int,
  val codeBackgroundColor: Int,
  val thematicBreakColor: Int
)

fun ThemePalette.wysiwygStyle(displayUnits: DisplayUnits): WysiwygStyle {
  return WysiwygStyle(
    syntaxColor = markdown.syntaxColor,
    strikethroughTextColor = textColorPrimary.blendWith(window.backgroundColor, .5f),
    blockQuote = BlockQuote(
      leftBorderColor = markdown.blockQuoteTextColor,
      leftBorderWidth = displayUnits.scaledPixels(4).roundToInt(),
      indentationMargin = displayUnits.scaledPixels(24).roundToInt(),
      textColor = markdown.blockQuoteTextColor
    ),
    code = Code(
      backgroundColor = markdown.codeBackgroundColor,
      codeBlockMargin = displayUnits.scaledPixels(8).roundToInt()
    ),
    heading = Heading(
      textColor = textColorHeading
    ),
    link = Link(
      textColor = markdown.linkTextColor,
      urlColor = markdown.linkUrlColor
    ),
    list = WysiwygStyle.List(
      indentationMargin = displayUnits.scaledPixels(8).roundToInt()
    ),
    thematicBreak = ThematicBreak(
      color = markdown.thematicBreakColor,
      height = displayUnits.scaledPixels(4)
    )
  )
}

@Deprecated("use divider", level = ERROR, replaceWith = ReplaceWith("divider"))
val ThemePalette.separator: Int
  get() = divider
