package pharmlane.com.PharmLaneStore.utills

import android.Manifest
import android.os.Build

/**
 * Runtime permissions the photo upload screens ask for. Storage permissions are never granted on
 * Android 13+ (and are not needed there to pick a photo), so asking for them blocked uploads forever.
 */
val IMAGE_PERMISSIONS: Array<String> = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.CAMERA)
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    else -> arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

/** Request code for asking for the camera permission right before taking a photo. */
const val REQUEST_CAMERA_PERMISSION = 1341
