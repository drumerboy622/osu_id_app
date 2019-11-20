package com.e.osu_id_app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class search {

    public Path myMethod(Path filePath, String barcode){
        try {
            Stream<Path> stream = Files.find(filePath, 1000,
                    (pathA, basicFileAttributes) -> {
                        File file = pathA.toFile();
                        return !file.isDirectory() &&
                                file.getName().contains(barcode + ".jpg");
                    });

            filePath = stream.findFirst().orElse(null);

            return filePath;
        } catch(IOException e){
            return null;
        }
    }

    public String myMethod2(Path filePath) {
        //Get the file_name
        String reversed = new StringBuilder(filePath.toString()).reverse().toString();
        int start = reversed.indexOf('/');
        int end = reversed.indexOf('/', reversed.indexOf('/') + 1);
        String shortened = reversed.substring(start + 1, end);
        String unreversed = new StringBuilder(shortened).reverse().toString();

        return unreversed;
    }

}
