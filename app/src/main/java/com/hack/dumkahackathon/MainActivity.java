package com.hack.dumkahackathon;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private IntentIntegrator mIntentIntegrator;
    public static final String PREFS = "Aadharlogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager mPackageManager = this.getPackageManager();
        int hasPermStorage = mPackageManager.checkPermission(android.Manifest.permission.CAMERA, this.getPackageName());

        if (hasPermStorage != PackageManager.PERMISSION_GRANTED) {
            // do stuff
             Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();

             ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

        } else if (hasPermStorage == PackageManager.PERMISSION_GRANTED) {
            // do stuff
             Toast.makeText(getApplicationContext(), "Has permission", Toast.LENGTH_LONG).show();

        } else {

        }

        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        if(hasLoggedIn) {
            Log.e("intent", "goes");
            Intent faceIntent = new Intent(this, main2.class);
            startActivity(faceIntent);
            return;
        }

        mIntentIntegrator = new IntentIntegrator(this);
        mIntentIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        mIntentIntegrator.setPrompt("Scan your Aadhar QE Code");
        mIntentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        mIntentIntegrator.setOrientationLocked(false);
        mIntentIntegrator.initiateScan();
        Log.e("scan", "initiated");
        //setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        Log.e("scan", "result given");
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            XmlPullParser parser = null;
            InputStream stream = null;
            try {
                Log.e("scan", "parser tried");
                parser = Xml.newPullParser();
                //stream = new ByteArrayInputStream(scanResult.getContents().getBytes("UTF-8"));
                stream = new ByteArrayInputStream(scanResult.getContents().getBytes("UTF-8"));

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(stream, null);
                parser.nextTag();
                if (!parser.getName().equals("PrintLetterBarcodeData")) {
                    mIntentIntegrator.initiateScan();
                    return;
                }
                Log.e("scan", "parsed");
                String uid = parser.getAttributeValue(null, "uid");
                String name = parser.getAttributeValue(null, "name");
                String pincode = parser.getAttributeValue(null, "pc");
                String gender = parser.getAttributeValue(null, "gender");
                String dist = parser.getAttributeValue(null, "dist");
                User user = User.newUser()
                        .setUid(uid)
                        .setName(name)
                        .setPincode(pincode)
                        .setGender(gender)
                        .setDist(dist)
                        .create();
                Log.e("scan", "user created");
                Toast.makeText(getApplicationContext(), user.getName(), Toast.LENGTH_LONG).show();
                Intent faceIntent = new Intent(this, main2.class);

                    SharedPreferences settings = getSharedPreferences(PREFS, 0); // 0 - for private mode
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("hasLoggedIn", true);
                    editor.commit();
                startActivity(faceIntent);
                //finish();
            } catch(XmlPullParserException xppe) {
                mIntentIntegrator.initiateScan();
            } catch (IOException ioe) {
                mIntentIntegrator.initiateScan();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException ioe) {
                    mIntentIntegrator.initiateScan();
                }

            }
        }
    }

}
