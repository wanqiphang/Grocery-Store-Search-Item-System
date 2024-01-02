package com.example.fyp_1
//
//import android.app.Activity
//import android.graphics.Bitmap
//import android.util.Log
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.support.image.ImageProcessor
//import org.tensorflow.lite.support.image.TensorImage
//import org.tensorflow.lite.support.image.ops.ResizeOp
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import java.io.FileInputStream
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel
//
//object ModelManager {
//    private var interpreter: Interpreter? = null
//    private var labels: List<String>? = null
//
//    fun initModel(activity: Activity) {
//        if (interpreter == null) {
//            // Load the model file as MappedByteBuffer
//            val modelFileDescriptor = activity.assets.openFd("model.tflite")
//            val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
//            val fileChannel = inputStream.channel
//            val startOffset = modelFileDescriptor.startOffset
//            val declaredLength = modelFileDescriptor.declaredLength
//            val modelByteBuffer: MappedByteBuffer =
//                fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//            inputStream.close()
//
//            // Initialize the Interpreter with the MappedByteBuffer
//            interpreter = Interpreter(modelByteBuffer)
//            labels = activity.assets.open("labels.txt").bufferedReader().readLines()
//        }
//    }
//
//    fun closeModel() {
//        interpreter?.close()
//        interpreter = null
//        labels = null
//    }
//
//    fun processImage(bitmap: Bitmap): String? {
//        // Ensure that the interpreter and labels are initialized
//        if (interpreter == null || labels == null) {
//            return null
//        }
//
//        // Configure input tensor shape and size
//        val inputShape = interpreter!!.getInputTensor(0).shape()
//        val inputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
//
//        // Resize and normalize the input image
//        val inputImage = Bitmap.createScaledBitmap(bitmap, inputShape[1], inputShape[2], true)
//        val imageProcessor = ImageProcessor.Builder()
//            .add(ResizeOp(inputShape[1], inputShape[2], ResizeOp.ResizeMethod.BILINEAR))
//            // Add any necessary image processing operations (e.g., normalization)
//            .build()
//        val tensorImage = TensorImage(DataType.FLOAT32)
//        tensorImage.load(inputImage)
//        val processedImage = imageProcessor.process(tensorImage)
//        val byteBuffer = ByteBuffer.allocateDirect(inputShape[1] * inputShape[2] * 3 * 4)
//        byteBuffer.order(ByteOrder.nativeOrder())
//        processedImage.tensorBuffer.loadBuffer(byteBuffer)
//
//        // Run model inference and get the result
//        val outputShape = interpreter!!.getOutputTensor(0).shape()
//        val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
//        interpreter?.run(byteBuffer, outputBuffer.buffer)
//
//        // Log raw output scores
//        Log.d("RawScores", outputBuffer.floatArray?.contentToString() ?: "null")
//
//        val softmax = outputBuffer.floatArray?.let { softmax(it) }
//        Log.d("SoftmaxValues", softmax?.contentToString() ?: "null")
//
//// Find the index with the maximum probability
//        var maxIdx = softmax?.indices?.maxByOrNull { softmax[it] } ?: 0
//        Log.d("MaxIndex", maxIdx.toString())
//
//// Get the result label
//        return labels?.get(maxIdx)
//    }
//}
//
//private fun softmax(scores: FloatArray): FloatArray {
//    val maxScore = scores.maxOrNull() ?: 0.0f
//    val expScores = scores.map { Math.exp((it - maxScore).toDouble()).toFloat() }
//    val sumExpScores = expScores.sum()
//    return expScores.map { it / sumExpScores }.toFloatArray()
//}