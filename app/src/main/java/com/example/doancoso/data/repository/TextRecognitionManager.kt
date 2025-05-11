package com.example.doancoso.data.repository

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class TextRecognitionManager {
    suspend fun recognizeTextFromImage(context: Context, imageUri: Uri): String {
        return try {
            if (imageUri == Uri.EMPTY) {
                throw IllegalArgumentException("Invalid image Uri")
            }

            val image = InputImage.fromFilePath(context, imageUri)

            // Khởi tạo TextRecognizer với TextRecognizerOptions
            val recognizer: TextRecognizer = TextRecognition.getClient(
                TextRecognizerOptions.Builder().build()
            )

            val result = recognizer.process(image).await()

            result.text
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            "Hình ảnh không hợp lệ"
        } catch (e: Exception) {
            e.printStackTrace()
            "Có lỗi xảy ra trong quá trình nhận diện"
        }
    }
}
