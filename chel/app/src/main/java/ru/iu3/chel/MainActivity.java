package ru.iu3.chel;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mbedcrypto");
        initRng();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btnClickMe);
        btn.setOnClickListener((View v) -> { onButtonClick(v);});

        Button btnHttp = findViewById(R.id.btnHttp);
        btnHttp.setOnClickListener((View v) -> {
            onButtonHttpClick(v);
        });
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // Button btn = findViewById(R.id.btnClickMe);
        // btn.setOnClickListener((View v) -> { onButtonClick(v);});

       byte[] rnd = randomBytes(16);
       byte[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
       byte[] encrypted = encrypt(rnd, data);
       byte[] decrypted = decrypt(rnd, encrypted);
       String originalData  = new String(data, StandardCharsets.UTF_8);
       String encryptedData = new String(encrypted, StandardCharsets.UTF_8);
       String decryptedData = new String(decrypted, StandardCharsets.UTF_8);

       System.out.println(originalData);
       System.out.println(encryptedData);
       System.out.println(decryptedData);

        String output = new String(
                "Original: "  + originalData  + "\n" +
                        "Encrypted: " + encryptedData + "\n" +
                        "Decrypted: " + decryptedData + "\0"
        );

        System.out.println("Original: "  + originalData);
        System.out.println("Encrypted: " + encryptedData);
        System.out.println("Decrypted: " + decryptedData);

        // Example of a call to a native method
       // TextView tv = findViewById(R.id.sample_text);
      //  tv.setText(stringFromJNI());
    }

    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);

    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);

    public static byte[] StringToHex(String s) {
        byte[] hex;
        try {
            return Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex) {
            hex = null;
        }
        return new byte[0];
    }

    protected void onButtonClick(View v) {
        // byte[] key = StringToHex("0123456789ABCDEF0123456789ABCDE0");
        // byte[] enc = encrypt(key, StringToHex("00000000000000102"));
        // byte[] dec = decrypt(key, enc);
        // String s = new String(Hex.encodeHex(dec)).toUpperCase();
        // Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(this, PinpadActivity.class);
       // startActivity(it);

        startActivityForResult(it, 0);
    }

    protected void onButtonHttpClick(View v) {
        TestHttpClient();
    }

    protected void TestHttpClient() {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection) (new URL("https://www.wikipedia.org").openConnection());
                // HttpURLConnection uc = (HttpURLConnection) (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(() -> {
                    Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }


    protected String getPageTitle(String html) {
        /*int pos = html.indexOf("<title");
        String p = "not found";
        if (pos >= 0)
        {
            int pos2 = html.indexOf("<", pos + 1);
            if (pos >= 0)
                p = html.substring(pos + 7, pos2);
        }
        return p;*/
        Pattern pattern = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String p;
        if (matcher.find()) {
            p = matcher.group(1);
        } else {
            p = "Not Found";
        }
        return p;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK || data != null) {
                String pin = data.getStringExtra("pin");
                Toast.makeText(this, pin, Toast.LENGTH_SHORT).show();
            }
        }
    }
}