package com.mayank_amr.news.ui.headlinesdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mayank_amr.news.R
import com.mayank_amr.news.databinding.HeadlineDetailFragmentBinding

/**
 * @Project News
 * @Created_by Mayank Kumar on 15-05-2021 08:14 PM
 */
class HeadlineDetailFragment : Fragment(R.layout.headline_detail_fragment) {

    private val args by navArgs<HeadlineDetailFragmentArgs>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = HeadlineDetailFragmentBinding.bind(view)

        binding.apply {

            // WebViewClient allows you to handle
            // onPageFinished and override Url loading.
            webView.webViewClient = WebViewClient()

            // this will load the url of the website
            webView.loadUrl(args.url)

            // this will enable the javascript settings
            webView.settings.javaScriptEnabled = true

            // to enable zoom feature
            webView.settings.setSupportZoom(true)

        }
    }

}