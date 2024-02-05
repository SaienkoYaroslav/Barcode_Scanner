package com.santansarah.barcodescanner.data

import android.content.Context
import android.content.Intent
import android.net.Uri

class UriHelper {
    fun isUrl(text: String): Boolean {
        val urlRegex = """^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$""".toRegex()
        return urlRegex.matches(text)
    }

    fun openLinkInBrowser(context: Context, link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(browserIntent)
    }
}