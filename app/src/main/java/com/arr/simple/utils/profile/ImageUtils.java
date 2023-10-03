package com.arr.simple.utils.profile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {

    private static final String TAG = "ImageHelper";
    private static final String DIRECTORY_NAME = "SIMple";
    private static final String FILE_NAME = "image.png";

    private Context context;
    private boolean rounded;

    public ImageUtils(Context context) {
        this.context = context;
    }

    // guardar imagen
    public boolean saveImage(Uri imageUri) {
        if (isVersionCodeTiramisu()) {
            Log.d(TAG, "SDK 33 no comprobar permisos");
            return saveImageWithPermissions(imageUri);
        } else {
            if (!hasWritePermission()) {
                Log.e(TAG, "No tienes permisos de escritura");
                return false;
            } else {
                return saveImageWithPermissions(imageUri);
            }
        }
    }

    // obtener el bitmap
    public Bitmap getSavedImage() {
        if (isVersionCodeTiramisu()) {
            if (isRounded()) {
                return roundedBitmap(getSavedImageWithPermissions());
            }
            return getSavedImageWithPermissions();
        } else {
            if (!hasWritePermission()) {
                return null;
            } else {
                if (isRounded()) {
                    return roundedBitmap(getSavedImageWithPermissions());
                }
                return getSavedImageWithPermissions();
            }
        }
    }

    // salvar la imagen obtenida mediante un Uri
    private boolean saveImageWithPermissions(Uri imageUri) {
        File file = createFile();
        if (file == null) {
            Log.e(TAG, "No se pudo crear el archivo");
            return false;
        }

        try {
            ContentResolver resolver = context.getContentResolver();
            Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri));
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error al guardar la imagen: " + e.getMessage());
            return false;
        }
    }

    // obtener el Bitmap desde la ruta
    private Bitmap getSavedImageWithPermissions() {
        File file = createFile();
        if (file == null || !file.exists()) {
            Log.e(TAG, "La imagen no existe");
            return null;
        }

        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    // comprobar si la version SDK es 33
    private boolean isVersionCodeTiramisu() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    // comprobar permisos de WRITE_EXTERNAL_STORAGE
    private boolean hasWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck =
                    ContextCompat.checkSelfPermission(
                            context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // comprobar permiso de READ_EXTERNAL_STORAGE
    private boolean hasReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck =
                    ContextCompat.checkSelfPermission(
                            context, Manifest.permission.READ_EXTERNAL_STORAGE);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // crear File con el directorio donde se guardará la imagen
    private File createFile() {
        File directory;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directory =
                    new File(
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            DIRECTORY_NAME);
        } else {
            directory =
                    new File(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES),
                            DIRECTORY_NAME);
        }

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "No se pudo crear el directorio");
                return null;
            }
        }
        return new File(directory, FILE_NAME);
    }

    // boolean para confirmar si se carga el bitmap redondeado
    private boolean isRounded() {
        return rounded;
    }

    // rounded image
    public ImageUtils setRounded(boolean isRound) {
        this.rounded = isRound;
        return this;
    }

    // redondear un Bitmap
    private Bitmap roundedBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();
            int diameter = Math.min(width, heigth);
            Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawCircle(diameter >> 1, diameter >> 1, diameter >> 1, paint);
            return output;
        } else {
            return null;
        }
    }
}
