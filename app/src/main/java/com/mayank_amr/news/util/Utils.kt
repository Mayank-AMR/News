package com.mayank_amr.news.util

import android.view.View

/**
 * @Project News
 * @Created_by Mayank Kumar on 15-05-2021 12:43 PM
 */

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}