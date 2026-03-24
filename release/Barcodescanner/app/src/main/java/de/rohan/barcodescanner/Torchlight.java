package de.rohan.barcodescanner;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Torchlight extends AppCompatActivity {

    private static final String LOGTAG = "TorchlightActivity";
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;
    private Handler handler;
    private CheckBox alwaysOnCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torchlight);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Log.e(LOGTAG, "Camera access exception: " + e.getMessage());
        }

        handler = new Handler();
        EditText inputText = findViewById(R.id.input_text);
        Button startButton = findViewById(R.id.start_button);
        alwaysOnCheckBox = findViewById(R.id.checkbox_always_on);

        startButton.setOnClickListener(v -> {
            Log.e("torch", "start");
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                if (alwaysOnCheckBox.isChecked()) {
                    setFlashlight(true);
                } else {
                    transmitMorseCode(text);
                }
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFlashlight(boolean state) {
        try {
            cameraManager.setTorchMode(cameraId, state);
            isFlashOn = state;
        } catch (CameraAccessException e) {
            Log.e(LOGTAG, "Error setting flashlight: " + e.getMessage());
        }
    }

    private void transmitMorseCode(String text) {
        String morseCode = textToMorse(text);
        Log.e("torch", morseCode);
        long dotDuration = 200*4; // Duration for a dot
        long dashDuration = dotDuration * 2; // Duration for a dash
        long gapDuration = dotDuration; // Gap between dots and dashes in a character
        long letterGapDuration = dotDuration ; // Gap between letters
        long wordGapDuration = dotDuration * 3; // Gap between words

        handler.post(new Runnable() {
            private int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < morseCode.length()) {
                    char currentChar = morseCode.charAt(currentIndex);
                    switch (currentChar) {
                        case '.':
                            Log.e("torch", ".");
                            setFlashlight(true);
                            handler.postDelayed(this, dotDuration);
                            break;
                        case '-':
                            Log.e("torch", "-");
                            setFlashlight(true);
                            handler.postDelayed(this, dashDuration);
                            break;
                        case ' ':
                            Log.e("torch", "gap");
                            setFlashlight(false);
                            handler.postDelayed(this, gapDuration);
                            break;
                        case '/':
                            Log.e("torch", "/");
                            setFlashlight(false);
                            handler.postDelayed(this, wordGapDuration);
                            break;
                        default:
                            setFlashlight(false);
                            handler.postDelayed(this, letterGapDuration);
                    }

                    currentIndex++;
                } else {
                    setFlashlight(false); ;
                }
            }
        });
    }

    private String textToMorse(String text) {
        StringBuilder morse = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            switch (c) {
                case 'a': morse.append(".- "); break;
                case 'b': morse.append("-... "); break;
                case 'c': morse.append("-.-. "); break;
                case 'd': morse.append("-.. "); break;
                case 'e': morse.append(". "); break;
                case 'f': morse.append("..-. "); break;
                case 'g': morse.append("--. "); break;
                case 'h': morse.append(".... "); break;
                case 'i': morse.append(".. "); break;
                case 'j': morse.append(".--- "); break;
                case 'k': morse.append("-.- "); break;
                case 'l': morse.append(".-.. "); break;
                case 'm': morse.append("-- "); break;
                case 'n': morse.append("-. "); break;
                case 'o': morse.append("--- "); break;
                case 'p': morse.append(".--. "); break;
                case 'q': morse.append("--.- "); break;
                case 'r': morse.append(".-. "); break;
                case 's': morse.append("... "); break;
                case 't': morse.append("- "); break;
                case 'u': morse.append("..- "); break;
                case 'v': morse.append("...- "); break;
                case 'w': morse.append(".-- "); break;
                case 'x': morse.append("-..- "); break;
                case 'y': morse.append("-.-- "); break;
                case 'z': morse.append("--.. "); break;
                case '1': morse.append(".---- "); break;
                case '2': morse.append("..--- "); break;
                case '3': morse.append("...-- "); break;
                case '4': morse.append("....- "); break;
                case '5': morse.append("..... "); break;
                case '6': morse.append("-.... "); break;
                case '7': morse.append("--... "); break;
                case '8': morse.append("---.. "); break;
                case '9': morse.append("----. "); break;
                case '0': morse.append("----- "); break;
                case ' ': morse.append("/ "); break;
                default: morse.append(""); break;
            }
        }
        return morse.toString().trim();
    }
}
