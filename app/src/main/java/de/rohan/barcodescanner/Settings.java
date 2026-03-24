package de.rohan.barcodescanner;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class Settings {

    private final SharedPreferences settings;
    private static final String PREFS = "barcodeScanner";
    private final String KEY_BRAND = "brand";
    private final String KEY_CODE = "code";
    private final String KEY_STATUS = "status";
    private final String KEY_PRODUCTNAME = "productName";
    private final String KEY_IMAGE = "image";
    private final String KEY_CAMERA = "camera";


    public Settings(Context context) {
        settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean getBrand() {
        return settings.getBoolean(KEY_BRAND, true);
    }

    public void setBrand(boolean b) {
        saveValue(KEY_BRAND, b);
    }

    public boolean getCode() {
        return settings.getBoolean(KEY_CODE, true);
    }

    public boolean getStatus() {
        return settings.getBoolean(KEY_STATUS, true);
    }

    public boolean getImage() {
        return settings.getBoolean(KEY_IMAGE, true);
    }

    public boolean getCamera() {
        return settings.getBoolean(KEY_CAMERA, true);
    }

    public void setStatus(boolean b) {
        saveValue(KEY_STATUS, b);
    }

    public boolean getProductName() {
        return settings.getBoolean(KEY_PRODUCTNAME, true);
    }

    public void setProductName(boolean b) {
        saveValue(KEY_PRODUCTNAME, b);
    }
    public void setCode(boolean b) {
        saveValue(KEY_CODE, b);
    }
    public void setImage(boolean b) {
        saveValue(KEY_IMAGE, b);
    }

    public void setCamera(boolean b) {
        saveValue(KEY_CAMERA, b);
    }

    private void saveValue(String name, boolean value) {
        try {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(name, value);
            editor.apply();
        } catch (Exception ignore) {}
    }

    private void saveValue(String name, String value) {
        try {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(name, value);
            editor.apply();
        } catch (Exception ignore) {}
    }

    private void saveValue(String name, int value) {
        try {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(name, value);
            editor.apply();
        } catch (Exception ignore) {}
    }

    private void saveValue(String name, long value) {
        try {
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(name, value);
            editor.apply();
        } catch (Exception ignore) {}
    }

}