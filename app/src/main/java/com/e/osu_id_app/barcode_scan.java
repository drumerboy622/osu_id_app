package com.e.osu_id_app;

import com.e.osu_id_app.SecondActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;


public class barcode_scan extends AppCompatActivity implements BarcodeRetriever{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        BarcodeCapture barcodeCapture = (BarcodeCapture)getSupportFragmentManager().findFragmentById(R.id.barcode);
        barcodeCapture.setRetrieval(this);
    }

    @Override
    public void onPermissionRequestDenied() {

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onRetrieved(final Barcode barcode){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(barcode_scan.this)
                        .setTitle("Code Retrieved")
                        .setMessage(barcode.displayValue);
                builder.show();

                Intent intent = new Intent(barcode_scan.this, SecondActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onRetrievedMultiple(Barcode closetToClick, List<BarcodeGraphic> barcode) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }
}

