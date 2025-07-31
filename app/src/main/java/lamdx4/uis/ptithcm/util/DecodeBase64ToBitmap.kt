package lamdx4.uis.ptithcm.util

import android.graphics.BitmapFactory
import android.util.Base64

fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val cleanBase64 = if (base64String.startsWith("data:image")) {
            base64String.split(",")[1]
        } else {
            base64String
        }
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}