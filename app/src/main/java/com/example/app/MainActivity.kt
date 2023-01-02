package com.example.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.app.ml.Model
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private val imageSize = 32

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create on click listener for 'Take Picture' button
        takePicture.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        // Create on click listener for 'Upload From Gallery' button
        openGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 3) {
            var image = data?.extras?.get("data") as Bitmap
            val dimension = Math.min(image.width, image.height)
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            imageView.setImageBitmap(image)

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
            classifyImage(image)
        } else {
            val dat = data?.data
            var image: Bitmap? = MediaStore.Images.Media.getBitmap(this.contentResolver, dat)

            imageView.setImageBitmap(image)

            image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, false)
            classifyImage(image)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun classifyImage(image: Bitmap?) {
        val model = Model.newInstance(applicationContext)

        // Prediction lasses
        val classes = arrayOf("Bruise", "Light Burn", "Severe Burn", "Skin Cut")

        // Creates inputs for reference
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        image?.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val rgb = intValues[pixel++]
                byteBuffer.putFloat((rgb shr 16 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((rgb shr 8 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((rgb and 0xFF) * (1f / 1))
            }
        }

        inputFeature0.loadBuffer(byteBuffer)

        // Run model inference and get result
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Array of each classes' confidence
        val confidences = outputFeature0.floatArray

        // Get class with greatest confidence
        var pos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                pos = i
            }
        }

        // Display prediction
        result.text = classes[pos]

        // Releases model resources
        model.close()
    }
}