package de.rohan.barcodescanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    String LOGTAG = "Barcode Scanner";
    SwitchMaterial brand, code, status, productname, image, camera;
    Settings settings;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOGTAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        brand = findViewById(R.id.show_brand);
        code = findViewById(R.id.show_code);
        productname = findViewById(R.id.show_product);
        status = findViewById(R.id.show_status);
        image = findViewById(R.id.show_image);
        camera = findViewById(R.id.cameraId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings = new Settings(getApplicationContext());
        brand.setChecked(settings.getBrand());
        brand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setBrand(b);
            }
        });
        code.setChecked(settings.getCode());
        code.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setCode(b);
            }
        });
        productname.setChecked(settings.getProductName());
        productname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setProductName(b);
            }
        });
        status.setChecked(settings.getStatus());
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setStatus(b);
            }
        });
        image.setChecked(settings.getImage());
        image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setImage(b);
            }
        });
        camera.setChecked(settings.getCamera());
        camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setCamera(b);
            }
        });

        Button save_Button = findViewById(R.id.save);
        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}