package de.rohan.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.koushikdutta.ion.Ion;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "BarcodeScanner";
    private String productInfo;
    private Settings settings;

    private TextView brand;
    private TextView code;
    private TextView product;
    private TextView status;
    private ImageView image;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "onCreate");
        setContentView(R.layout.activity_main);

        initializeUI();

        Button scanButton = findViewById(R.id.scan);
        scanButton.setOnClickListener(view -> {
            clear();
            Toast.makeText(this, R.string.scan_starting, Toast.LENGTH_SHORT).show();
            launchBarcodeScanner();
        });

        setupWindowInsets();
    }

    private void initializeUI() {
        brand = findViewById(R.id.brand);
        code = findViewById(R.id.code);
        product = findViewById(R.id.product);
        status = findViewById(R.id.status);
        image = findViewById(R.id.image);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings = new Settings(getApplicationContext());
        updateUIVisibility();
    }

    private void updateUIVisibility() {
        brand.setVisibility(settings.getBrand() ? View.VISIBLE : View.GONE);
        code.setVisibility(settings.getCode() ? View.VISIBLE : View.GONE);
        status.setVisibility(settings.getStatus() ? View.VISIBLE : View.GONE);
        product.setVisibility(settings.getProductName() ? View.VISIBLE : View.GONE);
        image.setVisibility(settings.getImage() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            openSettings();
            return true;
        }
        if (itemId == R.id.menu_torchlight) {
            openTorchlight();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void openTorchlight() {
        startActivity(new Intent(this, Torchlight.class));
    }

    private void launchBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        options.setPrompt(getString(R.string.scan_prompt));
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCameraId(settings.getCamera() ? 1 : 0);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_LONG).show();
                } else {
                    handleScanResult(result.getContents());
                }
            });

    private void handleScanResult(String scannedCode) {
        new Thread(() -> {
            try {
                productInfo = getData(scannedCode);
                Log.d(LOGTAG, "Product response received");
                runOnUiThread(() -> updateProductInfo(productInfo));
            } catch (IOException e) {
                Log.e(LOGTAG, "Product lookup failed", e);
                runOnUiThread(() -> Toast.makeText(this, R.string.lookup_error, Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateProductInfo(String productInfoJson) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OpenFoodFactsResponse> jsonAdapter = moshi.adapter(OpenFoodFactsResponse.class);

        try {
            OpenFoodFactsResponse response = jsonAdapter.fromJson(productInfoJson);
            if (response == null || response.product == null || response.status == null
                    || !response.status.toLowerCase(Locale.ROOT).startsWith("success")) {
                showProductNotFound();
                return;
            }

            brand.setText(getString(R.string.label_brand, displayValue(response.product.brands)));
            product.setText(getString(R.string.label_product, displayValue(response.product.product_name)));
            code.setText(getString(R.string.label_code, displayValue(response.product.code)));
            status.setText(getString(R.string.label_status, response.status));

            if (response.product.image_url != null && !response.product.image_url.isEmpty()) {
                Ion.with(this)
                        .load(response.product.image_url)
                        .withBitmap()
                        .intoImageView(image);
            } else {
                image.setImageBitmap(null);
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Could not parse product response", e);
            Toast.makeText(this, R.string.lookup_error, Toast.LENGTH_LONG).show();
        }
    }

    private String displayValue(String value) {
        return value == null || value.trim().isEmpty() ? getString(R.string.not_available) : value;
    }

    private void showProductNotFound() {
        Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_LONG).show();
        product.setText(R.string.product_not_found);
        code.setText("");
        status.setText("");
        image.setImageBitmap(null);
    }

    private String getData(String code) throws IOException {
        String url = "https://world.openfoodfacts.org/api/v3/product/" + code
                + ".json?fields=brands,product_name,code,image_url";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "BarcodeScanner/1.0 (https://github.com/rmuthukumar23/android-barcode-scanner-openfoodfacts)")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("OpenFoodFacts returned HTTP " + response.code());
            }
            return response.body().string();
        }
    }

    private void clear() {
        brand.setText(null);
        code.setText(null);
        product.setText(null);
        status.setText(null);
        image.setImageBitmap(null);
    }
}
