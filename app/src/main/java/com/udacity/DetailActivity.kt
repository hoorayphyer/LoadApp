package com.udacity

import android.app.DownloadManager
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (intent.action == ACTION_VIEW) {
            val status = intent.extras!!.get(EXTRA_STATUS_KEY)
            val radioId = intent.extras!!.get(EXTRA_RADIO_ID_KEY)

            binding.includedContentDetail.apply {
                statusVal.apply{
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        text = "SUCCESS"
                        setTextColor(getColor(R.color.downloadSuccess))
                    } else {
                        text = "FAILURE"
                        setTextColor(getColor(R.color.downloadFail))
                    }
                }
                filenameVal.text = when (radioId) {
                    R.id.glide_button -> getString(R.string.glide_link_text)
                    R.id.loadapp_button -> getString(R.string.loadapp_link_text)
                    R.id.retrofit_button -> getString(R.string.retrofit_link_text)
                    else -> "Unknown filename"
                }
                filenameVal.setTextColor(getColor(R.color.downloadSuccess))
            }
        }
    }

}
