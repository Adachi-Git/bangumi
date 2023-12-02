package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.replaceSmiles
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * 解析文章的评论
 *
 * @author why
 * @since 12/1/23
 */
fun Document.parserBottomComment(): List<CommentTreeEntity> {
    return select("#comment_list > div").mapCommentItems()
}

/**
 * 解析发回复表单
 */
fun Document.parserReplyForm(): CommentFormEntity {
    val formEntity = CommentFormEntity()
    select("#ReplyForm input").forEach {
        formEntity.inputs[it.attr("name")] = it.attr("value")
    }
    formEntity.action = select("#ReplyForm").attr("action")
    return formEntity
}

private fun Elements.mapCommentItems(): List<CommentTreeEntity> {
    return map { item ->
        val entity = CommentTreeEntity()
        val topicSubReply = item.select(".topic_sub_reply").remove()
        if (topicSubReply.isNotEmpty()) {
            entity.topicSubReply = topicSubReply.select(".topic_sub_reply > div")
                .mapCommentItems()
        }
        entity.id = item.attr("id")
        item.select("a.avatar").apply {
            entity.userId = attr("href").substringAfterLast("/")
            entity.userAvatar = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        entity.userName = item.select("strong > a").text()
        entity.floor = item.select(".post_actions a.floor-anchor").text()
        entity.replyJs = item.select(".post_actions .action > a.icon").attr("onclick")
        entity.time = item.select(".post_actions small")
            .firstOrNull()?.lastChild()?.toString().orEmpty().trim()
            .removePrefix("-").trim()
        entity.replyContent = item.select(".reply_content > .message")
            .ifEmpty { item.select(".inner > .cmt_sub_content") }
            .html().replaceSmiles()
        entity
    }
}

/**
 * Main
 * - subReply('blog',327295,195250,0,837364,824741,0)
 *
 * Sub
 * - subReply('blog' , 327295, 195202, 195251,   539713, 539713,1)
 *
 * 对应
 * - type,
 * - topic_id,
 * - post_id,
 * - sub_reply_id,
 * - sub_reply_uid,
 * - post_uid,
 * - sub_post_type
 */
fun String.parserReplyParam(): CommentFormEntity.CommentParam {
    val groupValues =
        "\\(\\s*'(.*?)'\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\)".toRegex()
            .find(this)?.groupValues.orEmpty()
    val param = CommentFormEntity.CommentParam()
    param.type = groupValues.getOrNull(1).orEmpty()
    param.topicId = groupValues.getOrNull(2)?.toLongOrNull() ?: 0
    param.postId = groupValues.getOrNull(3)?.toLongOrNull() ?: 0
    param.subReplyId = groupValues.getOrNull(4)?.toLongOrNull() ?: 0
    param.subReplyUid = groupValues.getOrNull(5)?.toLongOrNull() ?: 0
    param.postUid = groupValues.getOrNull(6)?.toLongOrNull() ?: 0
    param.subPostType = groupValues.getOrNull(7)?.toLongOrNull() ?: 0
    return param
}