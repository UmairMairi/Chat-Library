package com.xint.example.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xint.chatlibrary.utils.ChatConstants
import com.xint.example.databinding.ActivityImageVideoPreviewBinding
import java.io.File

class ImageVideoPreviewActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding: ActivityImageVideoPreviewBinding
    private lateinit var filePath: String
    private lateinit var fileType: String
    private lateinit var srcFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageVideoPreviewBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
    }
    private fun init(){

        binding.footer.sendMsg.visibility = View.VISIBLE
        binding.footer.ivMic.visibility = View.GONE
        binding.footer.imageView2.visibility = View.GONE
        binding.footer.ivGallery.visibility = View.GONE

        intent.extras?.let {
            filePath = it.getString(ChatConstants.Chat.ChatViewType.IMAGE_PREVIEW_PATH)!!
            fileType = it.getString(ChatConstants.Chat.ChatViewType.FILE_TYPE)!!
            srcFile = filePath
        }


        if (fileType == ChatConstants.Chat.ChatFileType.IMAGE) {
            binding.imgFile.visibility = View.VISIBLE
            filePath.let {
                Glide.with(this)
                    .load(File(it))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.imgFile)
            }
        }

        binding.footer.sendMsg.setOnClickListener(this::onClick)
        binding.btnClose.setOnClickListener(this::onClick)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.footer.sendMsg.id -> {
                if (fileType == ChatConstants.Chat.ChatFileType.IMAGE) {
                    val intent = intent.putExtras(
                        bundleOf(
                            Pair(ChatConstants.Chat.ChatViewType.FILE_TYPE, fileType),
                            Pair(ChatConstants.Chat.ChatViewType.IMAGE_PREVIEW_PATH, filePath),
                            Pair(ChatConstants.Chat.ChatViewType.BODY, binding.footer.etText.text.toString())
                        )
                    )
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val intent = intent.putExtras(
                        bundleOf(
                            Pair(ChatConstants.Chat.ChatViewType.FILE_TYPE, fileType),
                            Pair(ChatConstants.Chat.ChatViewType.IMAGE_PREVIEW_PATH, filePath),
                            Pair(ChatConstants.Chat.ChatViewType.BODY, binding.footer.etText.text.toString())
                        )
                    )
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            binding.btnClose.id -> {
                finish()
            }
        }

    }
}