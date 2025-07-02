package com.lulu.composedev.ui.bean

import android.net.Uri

/**
 * 作者: shilu
 * 时间: 2025/7/1
 * 描述:
 */
data class PreviewItem(
    val type: Int = 0,
    val uri: Uri,
    val imageUrl: String
)

// 图片类型
const val IMAGE_PREVIEW_LOCAL = 1  // 本地
const val IMAGE_PREVIEW_NETWORK = 2  // 纯图片