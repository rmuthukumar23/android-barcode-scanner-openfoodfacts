package de.rohan.barcodescanner;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class Settings {

    private final SharedPreferences settings;
    private static final String PREFS = "barcodeScanner";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_CODE = "code";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PRODUCTNAME = "productName";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_CAMERA = "camera";

    public Settings(Context context) {
        settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean getBrand() {
        return settings.getBoolean(KEY_BRAND, true);
    }

    public void setBrand(boolean value) {
        saveValue(KEY_BRAND, value);
    }

    public boolean getCode() {
        return settings.getBoolean(KEY_CODE, true);
    }

    public void setCode(boolean value) {
        saveValue(KEY_CODE, value);
    }

    public boolean getStatus() {
        return settings.getBoolean(KEY_STATUS, true);
    }

    public void setStatus(boolean value) {
        saveValue(KEY_STATUS, value);
    }

    public boolean getProductName() {
        return settings.getBoolean(KEY_PRODUCTNAME, true);
    }

    public void setProductName(boolean value) {
        saveValue(KEY_PRODUCTNAME, value);
    }

    public boolean getImage() {
        return settings.getBoolean(KEY_IMAGE, true);
    }

    public void setImage(boolean value) {
        saveValue(KEY_IMAGE, value);
    }

    public boolean getCamera() {
        return settings.getBoolean(KEY_CAMERA, false);
    }

    public void setCamera(boolean value) {
        saveValue(KEY_CAMERA, value);
    }

    private void saveValue(String name, boolean value) {
        settings.edit().putBoolean(name, value).apply();
    }
}
