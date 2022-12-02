package com.banklannister.imagecapture

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.banklannister.imagecapture.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val IMAGE_ID = 1
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCaptureImg.setOnClickListener {
                takeImage()
                textView.text = ""
            }

            btnDetectImg.setOnClickListener {
                if (imageBitmap != null) {
                    processImage()
                } else {
                    Toast.makeText(this@MainActivity, "Select a photo first", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun processImage() {
        val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val imageInput = InputImage.fromBitmap(imageBitmap!!, 0)

        imageLabeler.process(imageInput).addOnSuccessListener { imageLabels ->
            var finalResult = ""
            for (imageLabel in imageLabels) {
                val stringLabel = imageLabel.text
                val floatConfidence = imageLabel.confidence
                val index = imageLabel.index

                finalResult += "$index\n $stringLabel:\n $floatConfidence"

            }

            binding.textView.text = finalResult
        }
    }

    private fun takeImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"

        intent.putExtra("crop", "true")
        intent.putExtra("scale", "true")
        intent.putExtra("return", "true")
        startActivityForResult(intent, IMAGE_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_ID && resultCode == RESULT_OK) {
            val extras: Bundle? = data?.extras
            if (extras != null) {
                imageBitmap = extras.getParcelable("data")

            }

            if (imageBitmap != null) {
                binding.imageView.setImageBitmap(imageBitmap)
            }
        }
    }
}