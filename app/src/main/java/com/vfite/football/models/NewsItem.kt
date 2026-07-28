package com.vfite.football.models

import android.content.Context
import com.vfite.football.R

data class NewsItem(
    val id: Long,
    val itemTitle: String,
    val imageWide: String,
    val imageSmall: String,
    val isImportant: Long,
    val isVideo: Long,
    val category: String,
    val postDate: String,
    val commentsCount: Long,
    val commentsCountTop: Long,
    var isVideoLoaded : Boolean = false
) {
    fun categoryName(context: Context) : String {
        val categoriesNames = context.resources.getStringArray(R.array.categoriesName)
        val categoryCodes = context.resources.getStringArray(R.array.categoriesCodes)

        categoriesNames.forEachIndexed { index, value ->
            if (value.equals(category, ignoreCase = true)) {
                return categoryCodes[index]
            }
        }

        return category
    }
}
