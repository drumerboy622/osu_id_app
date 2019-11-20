package com.e.osu_id_app;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;

import org.apache.logging.log4j.core.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.List;
import java.util.stream.Stream;

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
                String path = get_intent.getString("Path");
                intent.putExtra("FileName", file_name);
                intent.putExtra("Path", path);
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
    public void onRetrieved(final Barcode barcode) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                search searchIt=new search();

                Bundle get_intent = getIntent().getExtras();
                String file_name = get_intent.getString("FileName");
                String path = get_intent.getString("Path");
                boolean exists = true;

                    File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/media/com.osu_id_app");
                    Path filePath = Paths.get(dir.getAbsolutePath());
                    System.out.println(filePath);
                    System.out.println(barcode.displayValue);

                    filePath = searchIt.myMethod(filePath, barcode.displayValue);
/*
                    Stream<Path> stream = Files.find(filePath, 1000,
                            (pathA, basicFileAttributes) -> {
                                File file = pathA.toFile();
                                return !file.isDirectory() &&
                                        file.getName().contains(barcode.displayValue + ".jpg");
                            });

                    filePath = stream.findFirst().orElse(null);
*/
                    try {
                        System.out.println(filePath.toString());
                        exists = filePath.toString().isEmpty();
                        System.out.println(!exists);
                    } catch (Exception e) {
                        System.out.println("No File matches");
                    }
                        if (!exists) {

                            String unreversed = searchIt.myMethod2(filePath);
                            //Get the file_name
/*                            String reversed = new StringBuilder(filePath.toString()).reverse().toString();
                            int start = reversed.indexOf('/');
                            int end = reversed.indexOf('/', reversed.indexOf('/') + 1);
                            String shortened = reversed.substring(start + 1, end);
                            String unreversed = new StringBuilder(shortened).reverse().toString();
*/
                            //Send Intent to Review activity
                            Intent intent = new Intent(barcode_scan.this, Review.class);
                            intent.putExtra("FileName", unreversed);
                            intent.putExtra("Path", path);
                            intent.putExtra("student_barcode", barcode.displayValue);
                            intent.putExtra("FilePath", filePath.toString());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(barcode_scan.this, photo_session.class);
                            intent.putExtra("FileName", file_name);
                            intent.putExtra("Path", path);
                            intent.putExtra("student_barcode", barcode.displayValue);
                            startActivity(intent);
                        }

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



