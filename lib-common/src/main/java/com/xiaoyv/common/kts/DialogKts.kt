package com.xiaoyv.common.kts

import android.content.Context
import android.text.InputFilter.LengthFilter
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.common.databinding.ViewInputLine1Binding
import com.xiaoyv.common.databinding.ViewInputLine2Binding

/**
 * showConfirmDialog
 *
 * @author why
 * @since 12/7/23
 */
inline fun Context.showConfirmDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    message: CharSequence = "",
    confirmText: String? = getString(CommonString.common_done),
    neutralText: String? = null,
    cancelText: String? = getString(CommonString.common_cancel),
    cancelable: Boolean = true,
    crossinline onNeutralClick: (() -> Unit) = {},
    crossinline onCancelClick: (() -> Unit) = {},
    crossinline onConfirmClick: (() -> Unit) = {},
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(cancelable)
        .let {
            if (neutralText != null) {
                it.setNeutralButton(neutralText) { _, _ ->
                    onNeutralClick()
                }
            } else {
                it
            }
        }
        .let {
            if (cancelText != null) {
                it.setNegativeButton(cancelText) { _, _ ->
                    onCancelClick()
                }
            } else {
                it
            }
        }
        .let {
            if (confirmText != null) {
                it.setPositiveButton(confirmText) { _, _ ->
                    onConfirmClick()
                }
            } else {
                it
            }
        }
        .create()
        .apply {
            setCanceledOnTouchOutside(cancelable)
        }.show()
}

inline fun Context.showOptionsDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    items: List<String>,
    crossinline onItemClick: (String, Int) -> Unit = { _, _ -> },
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setItems(items.toTypedArray()) { _, which ->
            onItemClick(items[which], which)
        }
        .create()
        .show()
}

/**
 * 输入弹窗
 */
inline fun Context.showInputDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    inputHint: String = "输入内容...",
    maxInput: Int = 1000,
    default: String = "",
    crossinline onInput: (String) -> Unit = { _ -> },
) {
    val binding = ViewInputLine1Binding.inflate(inflater)
    binding.etLine1.hint = inputHint

    if (maxInput > 0) {
        binding.etLine1.filters = arrayOf(LengthFilter(maxInput))
    }

    if (default.isNotBlank()) {
        binding.etLine1.setText(default)
        binding.etLine1.selectAll()
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(binding.root)
        .setNegativeButton(getString(CommonString.common_cancel), null)
        .setPositiveButton(getString(CommonString.common_done)) { _, _ ->
            val content = binding.etLine1.text.toString().trim()
            if (content.isNotBlank()) {
                onInput(binding.etLine1.text.toString().trim())
            }
        }
        .create()
        .show()

    binding.etLine1.postDelayed({
        KeyboardUtils.showSoftInput(binding.etLine1)
    }, 100)
}

/**
 * 输入弹窗
 */
inline fun Context.showInputLine2Dialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    inputHint1: String = "输入内容...",
    inputHint2: String = "输入内容...",
    default1: String = "",
    default2: String = "",
    crossinline onInput: (String, String) -> Unit = { _, _ -> },
) {
    val binding = ViewInputLine2Binding.inflate(inflater)
    binding.etLine1.hint = inputHint1
    binding.etLine2.hint = inputHint2

    if (default1.isNotBlank()) {
        binding.etLine1.setText(default1)
        binding.etLine1.selectAll()
    } else {
        binding.etLine2.setText(default2)
        binding.etLine2.selectAll()
    }

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(binding.root)
        .setNegativeButton(getString(CommonString.common_cancel), null)
        .setPositiveButton(getString(CommonString.common_done)) { _, _ ->
            val content1 = binding.etLine1.text.toString().trim()
            val content2 = binding.etLine2.text.toString().trim()
            onInput(content1, content2)
        }
        .create()
        .show()

    binding.etLine1.postDelayed({
        KeyboardUtils.showSoftInput(binding.etLine1)
    }, 100)
}