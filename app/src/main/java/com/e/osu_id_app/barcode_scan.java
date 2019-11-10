package com.e.osu_id_app;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Home Button
        ImageButton imageView = (ImageButton)findViewById(R.id.imageView4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(barcode_scan.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Manual Scan Button
        TextView textView = (TextView)findViewById(R.id.textView6);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(barcode_scan.this, manual_scan.class);
                Bundle get_intent = getIntent().getExtras();
                String file_name = get_intent.getString("FileName");
                intent.putExtra("FileName", file_name);
                startActivity(intent);
            }
        });



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


                Bundle get_intent = getIntent().getExtras();
                String file_name = get_intent.getString("FileName");

                Intent intent = new Intent(barcode_scan.this, photo_session.class);
                intent.putExtra("FileName", file_name);
                intent.putExtra("student_barcode", barcode.displayValue);
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



