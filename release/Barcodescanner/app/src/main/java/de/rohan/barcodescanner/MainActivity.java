package de.rohan.barcodescanner;

import android.annotation.SuppressLint;
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
import androidx.activity.result.contract.ActivityResultContracts;
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
            Toast.makeText(this, "Initiating Scan", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.menu_settings:
                openSettings();
                return true;
            case R.id.menu_torchlight:
                openTorchlight();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Toast.makeText(this, "Opened Settings", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void openTorchlight() {
        Toast.makeText(this, "Opened Torchlight", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Torchlight.class)); // Ensure the class name is correct
    }

    private void launchBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        options.setPrompt("Scan a barcode");
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCameraId(settings.getCamera() ? 1 : 0);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    handleScanResult(result.getContents());
                }
            });

    private void handleScanResult(String scannedCode) {
        Toast.makeText(this, "Scanned: " + scannedCode, Toast.LENGTH_LONG).show();
        new Thread(() -> {
            try {
                productInfo = getData(scannedCode);
                Log.d(LOGTAG, "productInfo: " + productInfo);
                runOnUiThread(() -> updateProductInfo(productInfo));
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateProductInfo(String productInfoJson) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OpenFoodFactsResponse> jsonAdapter = moshi.adapter(OpenFoodFactsResponse.class);
        OpenFoodFactsResponse response;
        try {
            response = jsonAdapter.fromJson(productInfoJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (response != null && "success".equalsIgnoreCase(response.status)) {
            brand.setText("Brand: " + response.product.brands);
            product.setText(String.format(Locale.getDefault(), getString(R.string.label_brand), response.product.product_name));
            code.setText("Code: " + response.product.code);
            status.setText("Scan status: " + response.status);

            Ion.with(this)
                    .load(response.product.image_url)
                    .withBitmap()
                    .intoImageView(image);
        } else {
            Toast.makeText(this, "Product not in database", Toast.LENGTH_LONG).show();
            product.setText("Error");
            code.setText("This product isn't part of our database");
        }
    }

    private String getData(String code) throws IOException {
        String url = "https://world.openfoodfacts.org/api/v3/product/" + code + ".json?fields=brands,product_name,code,image_url";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
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
