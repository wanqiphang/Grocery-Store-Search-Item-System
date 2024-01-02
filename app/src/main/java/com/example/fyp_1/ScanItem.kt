package com.example.fyp_1

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fyp_1.databinding.ActivityScanItemBinding
import com.example.fyp_1.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileDescriptor
import java.io.IOException
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp


class ScanItem : AppCompatActivity() {

    private lateinit var binding: ActivityScanItemBinding

    //capture image by gallery or camera
    private val CAMERA_PERMISSION_REQUEST = 100
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private var viewResult: String? = null

    //get the branchName from intent
    private lateinit var branchName: String

    var imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the branch name from the intent
        branchName = intent.getStringExtra("branch_name") ?: ""

        //below are the capture image
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadImageFromUri(imageUri)
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                loadImageFromUri(imageUri)
            }
        }

        binding.clickImageView.setOnClickListener {
            showImagePickerDialog()
        }


        //read labels.txt
        var labels = application.assets.open("labels.txt").bufferedReader().readLines()

        // Predict and search
        binding.searchResulButton.setOnClickListener {
            loadModelInput(labels)
        }
    }


    //capture image by camera or gallery
    private fun showImagePickerDialog() {
        val items = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
        builder.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun loadImageFromUri(uri: Uri?) {
        uri?.let {
            bitmap = uriToBitmap(it)
            binding.clickImageView.setImageBitmap(bitmap)
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun loadModelInput(labels: List<String>) {
        val model = ModelUnquant.newInstance(this)

        // Configure input tensor shape and size
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32) // Adjust data type if needed

        // Resize and normalize the input image
        val inputImage = Bitmap.createScaledBitmap(bitmap!!, 224, 224, true)
        val imageProcessor = ImageProcessor.Builder()
            // Add any necessary image processing operations (e.g., normalization)
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(inputImage)
        val processedImage = imageProcessor.process(tensorImage)
        val byteBuffer = processedImage.buffer
        inputFeature0.loadBuffer(byteBuffer)

        // Log the processed image dimensions and values
        Log.d("ScanItem", "Processed Image Shape: ${processedImage.width} x ${processedImage.height}")
        Log.d("ScanItem", "Processed Image Buffer: $byteBuffer")

        // Run model inference and get the result
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        // Log the raw model output values
        Log.d("ScanItem", "Raw Model Output: ${outputFeature0.joinToString()}")

        // Find the index with the maximum value
        var maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl) {
                maxIdx = index
            }
        }

        // Get the result label
        viewResult = labels[maxIdx]
        Log.d("ScanItem", "$viewResult")

        // Create an Intent to send the scan result to SearchItem.kt
        val searchIntent = Intent(this@ScanItem, SearchItem::class.java).apply {
            putExtra("branch_name", branchName)
            putExtra("scan_result", viewResult)
        }

        // Start the SearchItem activity with the scan result
        startActivity(searchIntent)

        // Release model resources if no longer used
        model.close()
    }

}
