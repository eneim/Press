package press.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updatePaddingRelative
import com.squareup.contour.ContourLayout
import kotlinx.android.parcel.Parcelize
import me.saket.press.R
import me.saket.press.shared.home.HomeModel
import me.saket.press.shared.localization.strings
import me.saket.press.shared.preferences.PreferencesScreenKey
import me.saket.press.shared.theme.EditText
import me.saket.press.shared.theme.TextStyles.appTitle
import press.extensions.borderlessRippleDrawable
import press.extensions.getDrawable
import press.extensions.hideKeyboard
import press.extensions.showKeyboard
import press.navigation.navigator
import press.theme.themeAware
import press.widgets.PressToolbar

class HomeToolbar(context: Context, showNavIcon: Boolean) : ContourLayout(context) {
  private val baseToolbar = PressToolbar(context, showNavIcon)
  private val searchView = SearchToolbar(context)
  val searchField get() = searchView.editText

  init {
    id = R.id.home_toolbar

    baseToolbar.layoutBy(
      x = matchParentX(),
      y = topTo { parent.top() }
    )
    searchView.layoutBy(
      x = matchParentX(),
      y = matchYTo(baseToolbar)
    )
    contourHeightOf { baseToolbar.bottom() }
    check(baseToolbar.elevation == 0f)

    themeAware { palette ->
      with(baseToolbar) {
        menu.clear()
        menu.add(
          icon = context.getDrawable(R.drawable.ic_search_24, palette.accentColor),
          title = context.strings().home.menu_search_notes,
          onClick = { setSearchVisible(true) }
        )
        menu.add(
          icon = context.getDrawable(R.drawable.ic_preferences_24dp, palette.accentColor),
          title = context.strings().home.menu_preferences,
          onClick = { navigator().lfg(PreferencesScreenKey) }
        )
      }
    }

    searchView.isVisible = false
    searchView.backButton.setOnClickListener {
      setSearchVisible(false)
      hideKeyboard()
    }
  }

  override fun onSaveInstanceState(): Parcelable {
    return SavedState(
      superState = super.onSaveInstanceState(),
      isSearchVisible = isSearchVisible()
    )
  }

  override fun onRestoreInstanceState(state: Parcelable) {
    check(state is SavedState)
    super.onRestoreInstanceState(state.superState)
    setSearchVisible(state.isSearchVisible)
  }

  fun render(model: HomeModel) {
    baseToolbar.title = model.title
    searchView.editText.hint = model.searchFieldHint
  }

  fun setSearchVisible(visible: Boolean) {
    searchView.isVisible = visible

    if (visible) {
      post {
        searchView.editText.showKeyboard()
      }
    } else {
      searchView.editText.setText("")
    }
  }

  fun isSearchVisible(): Boolean {
    return searchView.isVisible
  }

  private fun Menu.add(icon: Drawable, title: String, onClick: () -> Unit) {
    add(title).let {
      it.icon = icon
      it.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
      it.setOnMenuItemClickListener { onClick(); true }
    }
  }
}

@Parcelize
private data class SavedState(
  val superState: Parcelable?,
  val isSearchVisible: Boolean
) : Parcelable

private class SearchToolbar(context: Context) : ContourLayout(context) {
  val backButton = ImageButton(context).apply {
    contentDescription = context.strings().home.close_search_contentdescriptoin
    themeAware {
      background = borderlessRippleDrawable(it).apply { radius = 20.dip }
      setImageDrawable(context.getDrawable(R.drawable.ic_arrow_back_24, it.accentColor))
      setPadding(14.dip)
    }
  }

  val editText = EditText(context, appTitle).apply {
    id = R.id.home_notes_search_textfield
    background = null
    isSingleLine = true
    updatePaddingRelative(top = 14.dip, bottom = 14.dip, end = 16.dip)
  }

  init {
    backButton.layoutBy(
      x = leftTo { parent.left() },
      y = matchParentY()
    )
    editText.layoutBy(
      x = leftTo { backButton.right() }.rightTo { parent.right() },
      y = matchParentY()
    )

    themeAware {
      setBackgroundColor(it.window.backgroundColor)
    }
  }
}