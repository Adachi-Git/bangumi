package com.xiaoyv.bangumi.ui

import android.graphics.Typeface
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.badge.BadgeDrawable
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityHomeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UpdateHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import kotlinx.coroutines.delay


/**
 * Class: [HomeActivity]
 *
 * @author why
 * @since 11/24/23
 */
class HomeActivity : BaseViewModelActivity<ActivityHomeBinding, MainViewModel>() {
    private val vpAdapter by lazy { HomeAdapter(this) }

    private val robot by lazy { HomeRobot(this) }

    private val pageMap by lazy {
        mapOf(
            0 to R.id.bottom_menu_home,
            1 to R.id.bottom_menu_timeline,
            2 to R.id.bottom_menu_media,
            3 to R.id.bottom_menu_discover,
            4 to R.id.bottom_menu_profile
        )
    }

    override fun initWindowConfig(window: Window) {
        ScreenUtils.setPortrait(this)

        // 窗口参数
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }

    override fun initView() {
        binding.vpView.isUserInputEnabled = false
        binding.vpView.offscreenPageLimit = vpAdapter.itemCount
        binding.vpView.adapter = vpAdapter

        // 初始 TAB
        val defaultTab = ConfigHelper.homeDefaultTab
        if (defaultTab != 0) {
            binding.vpView.setCurrentItem(defaultTab, false)
            if (pageMap[defaultTab] != null) {
                binding.navView.selectedItemId = pageMap[defaultTab]!!
            }
        }
    }

    override fun initData() {
        // Bangumi 娘开关
        if (ConfigHelper.isRobotEnable) {
            viewModel.startRobotSayQueue()
        } else {
            robot.disable()
        }

        // 更新检测
        UpdateHelper.checkUpdate(this, false)

        showTip()
    }

    override fun initListener() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> binding.vpView.setCurrentItem(0, false)
                R.id.bottom_menu_timeline -> binding.vpView.setCurrentItem(1, false)
                R.id.bottom_menu_media -> binding.vpView.setCurrentItem(2, false)
                R.id.bottom_menu_discover -> binding.vpView.setCurrentItem(3, false)
                R.id.bottom_menu_profile -> binding.vpView.setCurrentItem(4, false)
            }
            true
        }

        binding.navView.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    viewModel.resetDiscoverIndex()
                }
            }
        }

        // 中心TAB
        val menuItem = binding.navView.menu.getItem(2)
        when (ConfigHelper.centerTabType) {
            GlobalConfig.PAGE_RANK -> menuItem.setTitle("排行榜")
            GlobalConfig.PAGE_PROCESS -> menuItem.setTitle("追番进度")
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        // 多条语句可能同时触发，先加个列队
        currentApplication.globalRobotSpeech.observe(this) {
            debugLog { "春菜：$it" }
            viewModel.addRobotSayQueue(it.trim())
        }

        currentApplication.globalNotify.observe(this) {
            val badge = binding.navView.getOrCreateBadge(R.id.bottom_menu_profile)
            if (it != 0) {
                badge.number = it
                badge.badgeGravity = BadgeDrawable.TOP_END
            } else {
                binding.navView.removeBadge(R.id.bottom_menu_profile)
            }
        }

        viewModel.onRobotSay.observe(this) {
            robot.onSay(it)
        }
    }

    private fun showTip() {
        if (ConfigHelper.showVersionTip) {
            launchUI {
                delay(2000)
                showConfirmDialog(
                    title = "App 声明",
                    message = SpanUtils.with(null)
                        .append("此客户端为班固米用户：")
                        .appendSpace(4.dpi)
                        .append("小玉")
                        .setTypeface(Typeface.DEFAULT_BOLD)
                        .setForegroundColor(getAttrColor(GoogleAttr.colorPrimary))
                        .appendSpace(4.dpi)
                        .append("为爱发电，耗时半个多月打造的班固米全功能三方客户端。")
                        .appendLine()
                        .appendLine()
                        .append("此 App 为原生安卓应用，并且有做大量优化如评论分页，缓存等，保证你在使用过程中响应极速不卡顿。")
                        .setForegroundColor(getAttrColor(GoogleAttr.colorPrimary))
                        .appendLine()
                        .appendLine()
                        .append("欢迎大家积极提出反馈或建议，或者加入交流群讨论，需求反馈等将第一时间得到回复。\n\n此软件不收集任何隐私数据并且完全开源。")
                        .create(),
//                    cancelText = "加群",
                    neutralText = "不再提醒",
                    confirmText = "我知道了",
                    onCancelClick = {
//                        openInBrowser("https://qm.qq.com/q/YomiSMeyUs")
                    },
                    onNeutralClick = {
                        ConfigHelper.showVersionTip = false
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        robot.onResume()
    }

    override fun onPause() {
        super.onPause()
        robot.onPause()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        robot.onAttachedToWindow(binding.navView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        robot.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }
}