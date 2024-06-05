/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.settings.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.breezyweather.BuildConfig
import org.breezyweather.R
import org.breezyweather.common.basic.GeoActivity
import org.breezyweather.common.extensions.currentLocale
import org.breezyweather.common.ui.composables.AlertDialogLink
import org.breezyweather.common.ui.widgets.Material3CardListItem
import org.breezyweather.common.ui.widgets.Material3Scaffold
import org.breezyweather.common.ui.widgets.generateCollapsedScrollBehavior
import org.breezyweather.common.ui.widgets.getCardListItemMarginDp
import org.breezyweather.common.ui.widgets.insets.FitStatusBarTopAppBar
import org.breezyweather.common.ui.widgets.insets.bottomInsetItem
import org.breezyweather.common.utils.helpers.IntentHelper
import org.breezyweather.theme.compose.BreezyWeatherTheme
import org.breezyweather.theme.compose.DayNightTheme
import org.breezyweather.theme.compose.rememberThemeRipple

private class AboutAppLinkItem(
    @DrawableRes val iconId: Int,
    @StringRes val titleId: Int,
    val onClick: () -> Unit
)

private class ContributorItem(
    val name: String,
    val github: String? = null,
    val weblate: String? = null,
    val mail: String? = null,
    val url: String? = null,
    @StringRes val contribution: Int? = null
)

private class TranslatorItem(
    val lang: Array<String> = emptyArray(),
    val name: String,
    val github: String? = null,
    val weblate: String? = null,
    val mail: String? = null,
    val url: String? = null
)

class AboutActivity : GeoActivity() {

    private val aboutAppLinks = arrayOf(
        AboutAppLinkItem(
            iconId = R.drawable.ic_shield_lock,
            titleId = R.string.about_privacy_policy
        ) {
            IntentHelper.startPrivacyPolicyActivity(this@AboutActivity)
        },
        AboutAppLinkItem(
            iconId = R.drawable.ic_contract,
            titleId = R.string.about_dependencies
        ) {
            IntentHelper.startDependenciesActivity(this@AboutActivity)
        }
    )

    private val contributors: Array<ContributorItem> = arrayOf(
        ContributorItem("炊神", github = "zhangjq0908"),
        ContributorItem("jiaowosike", github = "zhangjq0908", contribution = R.string.about_contribution_WangDaYeeeeee),
        ContributorItem("野村山夫", contribution = R.string.about_contribution_designer)
    )
    // Please keep them ordered by the main language translated so that we can easily sort translators by % contributed
    // Here, we want to sort by language code, which is a different order than in Language.kt
    // If you significantly contributed more than other translators, and you would like to appear
    // first in the list, please open a GitHub issue
    private val translators = arrayOf(
        TranslatorItem(arrayOf("zh_rCN"), "董志民"),
        TranslatorItem(arrayOf("zh_rCN"), "煮粥",)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BreezyWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }
    }

    @Composable
    private fun ContentView() {
        val scrollBehavior = generateCollapsedScrollBehavior()

        val linkToOpen = remember { mutableStateOf("") }
        val dialogLinkOpenState = remember { mutableStateOf(false) }

        val locale = this.currentLocale
        val language = locale.language
        val languageWithCountry = locale.language + (if(!locale.country.isNullOrEmpty()) "_r" + locale.country else "")
        var filteredTranslators = translators.filter {
            it.lang.contains(language) || it.lang.contains(languageWithCountry)
        }
        if (filteredTranslators.isEmpty()) {
            // No translators found? Language doesn’t exist, so defaulting to English
            filteredTranslators = translators.filter { it.lang.contains("en") }
        }

        val contactLinks = arrayOf(
            AboutAppLinkItem(
                iconId = R.drawable.ic_code,
                titleId = R.string.about_source_code,
            ) {
                linkToOpen.value = "https://github.com/breezy-weather/breezy-weather"
                dialogLinkOpenState.value = true
            },
            AboutAppLinkItem(
                iconId = R.drawable.ic_forum,
                titleId = R.string.about_matrix,
            ) {
                linkToOpen.value = "https://matrix.to/#/#breezy-weather:matrix.org"
                dialogLinkOpenState.value = true
            }
        )

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.action_about),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = it,
            ) {
                item {
                    Header()
                    SectionTitle(stringResource(R.string.about_contact))
                }
                items(contactLinks) { item ->
                    AboutAppLink(
                        iconId = item.iconId,
                        title = stringResource(item.titleId),
                        onClick = item.onClick,
                    )
                }

                item {
                    SectionTitle(stringResource(R.string.about_app))
                }
                items(aboutAppLinks) { item ->
                    AboutAppLink(
                        iconId = item.iconId,
                        title = stringResource(item.titleId),
                        onClick = item.onClick,
                    )
                }

                item { SectionTitle(stringResource(R.string.about_contributors)) }
                items(contributors) { item ->
                    ContributorView(name = item.name, contribution = item.contribution) {
                        linkToOpen.value = when {
                            !item.github.isNullOrEmpty() -> "https://github.com/${item.github}"
                            !item.weblate.isNullOrEmpty() -> "https://hosted.weblate.org/user/${item.weblate}/"
                            !item.mail.isNullOrEmpty() -> "mailto:${item.mail}"
                            !item.url.isNullOrEmpty() -> item.url
                            else -> ""
                        }
                        if (linkToOpen.value.isNotEmpty()) {
                            dialogLinkOpenState.value = true
                        }
                    }
                }

                item { SectionTitle(stringResource(R.string.about_translators)) }
                items(filteredTranslators) { item ->
                    ContributorView(name = item.name) {
                        linkToOpen.value = when {
                            !item.github.isNullOrEmpty() -> "https://github.com/${item.github}"
                            !item.weblate.isNullOrEmpty() -> "https://hosted.weblate.org/user/${item.weblate}/"
                            !item.mail.isNullOrEmpty() -> "mailto:${item.mail}"
                            !item.url.isNullOrEmpty() -> item.url
                            else -> ""
                        }
                        if (linkToOpen.value.isNotEmpty()) {
                            dialogLinkOpenState.value = true
                        }
                    }
                }

                bottomInsetItem(
                    extraHeight = getCardListItemMarginDp(this@AboutActivity).dp
                )
            }

            if (dialogLinkOpenState.value) {
                AlertDialogLink(
                    onClose = { dialogLinkOpenState.value = false },
                    linkToOpen = linkToOpen.value
                )
            }
        }
    }

    @Composable
    private fun Header() {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_round),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.little_margin))
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.breezy_weather),
                color = DayNightTheme.colors.titleColor,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = versionFormatted,
                color = DayNightTheme.colors.captionColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }

    @Composable
    private fun SectionTitle(title: String) {
        Text(
            text = title,
            modifier = Modifier.padding(dimensionResource(R.dimen.normal_margin)),
            color = DayNightTheme.colors.captionColor,
            style = MaterialTheme.typography.labelMedium,
        )
    }

    private val versionFormatted: String
        get() = when {
            BuildConfig.DEBUG -> "Debug ${BuildConfig.COMMIT_SHA}"
            else -> "Stable ${BuildConfig.VERSION_NAME}"
        }

    @Composable
    private fun AboutAppLink(
        @DrawableRes iconId: Int,
        title: String,
        onClick: () -> Unit,
    ) {
        Material3CardListItem {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberThemeRipple(),
                        onClick = onClick,
                    )
                    .padding(dimensionResource(R.dimen.normal_margin)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    tint = DayNightTheme.colors.titleColor,
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.normal_margin)))
                Text(
                    text = title,
                    color = DayNightTheme.colors.titleColor,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }

    @Composable
    private fun ContributorView(
        name: String,
        @StringRes contribution: Int? = null,
        onClick: () -> Unit
    ) {
        Material3CardListItem {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberThemeRipple(),
                        onClick = {
                            onClick()
                        },
                    )
                    .padding(dimensionResource(R.dimen.normal_margin))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        color = DayNightTheme.colors.titleColor,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (contribution != null) {
                    Text(
                        text = stringResource(contribution),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        BreezyWeatherTheme(lightTheme = isSystemInDarkTheme()) {
            ContentView()
        }
    }
}
