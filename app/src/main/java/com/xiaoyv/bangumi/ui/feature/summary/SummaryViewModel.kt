package com.xiaoyv.bangumi.ui.feature.summary

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.EncryptUtils
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.exception.NeedConfigException
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.randId
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Class: [SummaryViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SummaryViewModel : BaseViewModel() {
    internal var summary: Array<out String> = emptyArray()
    internal var summaryOriginal = MutableLiveData<List<CharSequence>?>()
    internal var summaryTranslate = MutableLiveData<String>()
    internal var isShowOriginal = true

    internal val onNeedConfig = UnPeekLiveData<Unit>()

    /**
     * 待翻译的文本
     */
    private val needTranslateText: String
        get() = summary.joinToString("\n") {
            it.parseHtml(imageGetter = null).toString().trim()
        }

    private val cacheKey: String
        get() = EncryptUtils.encryptMD5ToString(needTranslateText)

    private val conditions = DownloadConditions.Builder()
        .build()

    /**
     * 直接刷新
     */
    fun showOriginal() {
        launchUI(stateView = loadingViewState) {
            isShowOriginal = true
            summaryOriginal.value = withContext(Dispatchers.IO) {
                summary.map { it.parseHtml(true) }
            }
        }
    }

    fun doTranslate() {
        // Create an English-German translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.JAPANESE)
            .setTargetLanguage(TranslateLanguage.CHINESE)
            .build()
        val englishGermanTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                debugLog { "模型下载完成" }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        englishGermanTranslator.translate(
            "日本各地に、突如として謎の門が出現。\n" +
                    "その先の異空間『魔都』には女性にのみ食べた者に特異な能力をもたらす桃が存在し、『醜鬼』と呼ばれる怪物を退治するため、女性兵による戦闘集団『魔防隊』が組織された。\n" +
                    "\n" +
                    "活躍できる場所を求めていた男子高校生・和倉優希はある日、魔都へと迷い込むと醜鬼に襲われてしまう。\n" +
                    "そこへ駆けつけたのは魔防隊七番組の美しき組長・羽前京香。\n" +
                    "京香の能力で奴隷（スレイブ）と化した優希は力を開花させ、醜鬼を見事打ち倒し、奴隷 兼 魔防隊の管理人として醜鬼と戦うことに。\n" +
                    "\n" +
                    "“飼われる少年”のバトルファンタジー、ここに開幕！"
        )
            .addOnSuccessListener { translatedText ->
                debugLog { translatedText }
                // Translation successful.
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                // Error.
                // ...
            }
    }

    fun showTranslate() {
        isShowOriginal = false

        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                if (it is NeedConfigException) {
                    onNeedConfig.value = Unit
                } else {
                    showToastCompat(it.errorMsg)
                }
            },
            block = {
                // 翻译缓存
                val translate = CacheHelper.readTranslate(cacheKey)
                if (translate.isNotBlank()) {
                    summaryTranslate.value = translate
                    return@launchUI
                }

                // 翻译
                val translateResult = when (ConfigHelper.translateType) {
                    0 -> doTranslateWithAiModel()
                    1 -> doTranslateWithBaidu()
                    else -> doTranslateWithAiModel()
                }

                // 缓存结果
                CacheHelper.saveTranslate(cacheKey, translateResult)

                summaryTranslate.value = translateResult
            }
        )
    }

    /**
     * AI 模型翻译
     */
    private suspend fun doTranslateWithAiModel(): String {
        return withContext(Dispatchers.IO) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.JAPANESE)
                .setTargetLanguage(TranslateLanguage.CHINESE)
                .build()

            Translation.getClient(options).use { translator ->
                // 按需下载
                suspendCancellableCoroutine { emit ->
                    translator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            emit.resumeWith(Result.success(true))
                        }
                        .addOnFailureListener { exception ->
                            emit.resumeWith(Result.failure(exception))
                        }
                }

                // 翻译
                suspendCancellableCoroutine { emit ->
                    translator.translate(needTranslateText)
                        .addOnSuccessListener {
                            emit.resumeWith(Result.success(it))
                        }
                        .addOnFailureListener { exception ->
                            emit.resumeWith(Result.failure(exception))
                        }
                }
            }
        }
    }

    /**
     * 百度翻译
     */
    private suspend fun doTranslateWithBaidu(): String {
        return withContext(Dispatchers.IO) {
            val translateText = needTranslateText
            val (appId, secret) = ConfigHelper.readBaiduTranslateConfig()
            val salt = randId()
            val sign = generateSign(translateText, appId, secret, salt)

            if (appId.isBlank() || secret.isBlank()) {
                throw NeedConfigException("需要配置百度翻译")
            }

            val result = BgmApiManager.bgmJsonApi.postBaiduTranslate(
                q = translateText,
                appId = appId,
                secret = secret,
                salt = salt,
                sign = sign
            )

            require(result.errorMsg.isNullOrBlank()) { result.errorMsg.orEmpty() }

            result.transResult.orEmpty()
                .joinToString("\n") { it.dst.orEmpty() }
        }
    }

    private fun generateSign(text: String, appId: String, secret: String, salt: String): String {
        return EncryptUtils.encryptMD5ToString("$appId$text$salt$secret").lowercase()
    }
}