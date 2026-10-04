package pharmlane.com.PharmLaneStore.utills.cropper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import java.io.File;

import pharmlane.com.PharmLaneStore.utills.AppController;

/**
 * The subset of the old edmodo {@code CropImage} API this app uses, backed by the maintained
 * CanHub cropper. The edmodo artifact only existed on JCenter, which has shut down.
 */
public final class CropImage {

    public static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203;

    private CropImage() {
    }

    public static ActivityBuilder activity(Uri uri) {
        return new ActivityBuilder(uri);
    }

    public static ActivityResult getActivityResult(Intent data) {
        return new ActivityResult(new CropImageContract().parseResult(Activity.RESULT_OK, data));
    }

    public static final class ActivityBuilder {
        private final Uri source;
        private final CropImageOptions options = new CropImageOptions();

        private ActivityBuilder(Uri source) {
            this.source = source;
        }

        public ActivityBuilder setCropShape(CropImageView.CropShape cropShape) {
            options.cropShape = cropShape;
            return this;
        }

        public ActivityBuilder setGuidelines(CropImageView.Guidelines guidelines) {
            options.guidelines = guidelines;
            return this;
        }

        public ActivityBuilder setFixAspectRatio(boolean fixAspectRatio) {
            options.fixAspectRatio = fixAspectRatio;
            return this;
        }

        public ActivityBuilder setAspectRatio(int aspectRatioX, int aspectRatioY) {
            options.aspectRatioX = aspectRatioX;
            options.aspectRatioY = aspectRatioY;
            options.fixAspectRatio = true;
            return this;
        }

        public ActivityBuilder setInitialCropWindowPaddingRatio(float ratio) {
            options.initialCropWindowPaddingRatio = ratio;
            return this;
        }

        public ActivityBuilder setActivityTitle(CharSequence title) {
            options.activityTitle = title;
            return this;
        }

        public Intent getIntent(Context context) {
            return new CropImageContract().createIntent(context, new CropImageContractOptions(source, options));
        }

        public void start(Activity activity) {
            activity.startActivityForResult(getIntent(activity), CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public static final class ActivityResult {
        private final CropImageView.CropResult result;

        private ActivityResult(CropImageView.CropResult result) {
            this.result = result;
        }

        /** A file:// Uri of the cropped image, or null if cropping failed. */
        public Uri getUri() {
            if (!result.isSuccessful()) {
                return null;
            }
            Context context = AppController.Companion.getInstance().getAppContext();
            String path = result.getUriFilePath(context, false);
            return path == null ? null : Uri.fromFile(new File(path));
        }

        public Exception getError() {
            return result.getError();
        }
    }
}
