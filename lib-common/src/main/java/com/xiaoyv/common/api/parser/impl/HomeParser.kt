package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.config.bean.SampleImageEntity
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Class: [HomeParser]
 *
 * @author why
 * @since 11/24/23
 */
object HomeParser {

    fun Document.parserHomePage(): HomeIndexEntity {
        val entity = HomeIndexEntity()
        entity.images = select("#featuredItems li").map {
            val imageCardEntity = HomeIndexCardEntity()

            val titleRef = it.select("h2.title")
            imageCardEntity.title = titleRef.text()
            imageCardEntity.titleType = titleRef.select("a").hrefId()
            imageCardEntity.images = it.select("li > div").map { item ->
                val title: String
                val imageUrl: String
                val attention: String
                val subjectId: String
                if (item.hasClass("mainItem")) {
                    title = item.select("a").attr("title")
                    subjectId = item.select("a").attr("href")
                        .substringAfterLast("/")
                    attention = item.select(".grey").text()
                    imageUrl = item.select(".image").attr("style")
                        .fetchStyleBackgroundUrl().optImageUrl()
                } else {
                    title = item.select("a.grid").attr("title")
                    subjectId = item.select("a.grid").attr("href")
                        .substringAfterLast("/")
                    attention = item.select(".grey").text()
                    imageUrl = item.select(".grid").attr("style")
                        .fetchStyleBackgroundUrl().optImageUrl()
                }
                BgmMediaEntity(
                    title = title,
                    image = imageUrl,
                    attention = attention,
                    id = subjectId
                )
            }

            imageCardEntity
        }

        val tip = select("#home_calendar .tip").text()
        entity.calendar = select("#home_calendar .week").let {
            val today = it.getOrNull(0)
            val tomorrow = it.getOrNull(1)

            HomeIndexCalendarEntity(
                tip = tip,
                today = today.selectCalendarItem(),
                tomorrow = tomorrow.selectCalendarItem()
            )
        }

        entity.banner.banners = entity.images.map {
            val media = it.images.firstOrNull() ?: return@map null
            SampleImageEntity(
                id = media.id,
                image = media.image.optImageUrl(largest = true),
                title = media.title.toString()
            )
        }.filterNotNull()
        return entity
    }

    private fun Element?.selectCalendarItem(): List<BgmMediaEntity> {
        val element = this ?: return emptyList()
        return element.select(".coverList .thumbTip").map {
            BgmMediaEntity(
                title = it.select("a").attr("title").parseHtml(),
                id = it.select("a").hrefId(),
                image = it.select("img").attr("src").optImageUrl()
            )
        }
    }
}