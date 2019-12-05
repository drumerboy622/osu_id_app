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
            System.out.println(filePath);
            return filePath;
        } catch(IOException e){
            return null;
        }
    }

    public Path myMethod3(Path filePath, String barcode){
        try {
            Stream<Path> stream = Files.find(filePath, 1000,
                    (pathA, basicFileAttributes) -> {
                        File file = pathA.toFile();
                        return file.isDirectory() &&
                                file.getName().contains(barcode);
                    });

            filePath = stream.findFirst().orElse(null);
            System.out.println(filePath);
            return filePath;
        } catch(IOException e){
            return null;
        }
    }

}
