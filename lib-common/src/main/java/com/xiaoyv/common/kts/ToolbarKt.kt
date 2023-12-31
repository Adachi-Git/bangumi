@file:ExperimentalBadgeUtils

package com.xiaoyv.common.kts

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.blankj.utilcode.util.ColorUtils
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils

/**
 * ToolbarKt
 *
 * @author why
 * @since 11/19/23
 */
fun Toolbar.initNavBack(activity: AppCompatActivity, back: Boolean = true) {
    activity.setSupportActionBar(this)
    if (back) {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

fun MenuItem.initNavBack(activity: AppCompatActivity) {
    when (itemId) {
        android.R.id.home -> {
            activity.finish()
        }
    }
}

/**
 * 设置通知数目红点
 */
fun Toolbar.setBadgeNumber(
    @IdRes menuItemId: Int,
    @IntRange(from = 0) count: Int,
    oldBadgeDrawable: BadgeDrawable? = null,
): BadgeDrawable? {
    BadgeUtils.detachBadgeDrawable(oldBadgeDrawable, this, menuItemId)
    if (count > 0) {
        val badgeDrawable = BadgeDrawable.create(context)
            .apply {
                isVisible = true
                backgroundColor = ColorUtils.getColor(CommonColor.save_dropped)
                number = count
            }
        BadgeUtils.attachBadgeDrawable(
            badgeDrawable,
            this, menuItemId
        )
        return badgeDrawable
    }
    return null
}
