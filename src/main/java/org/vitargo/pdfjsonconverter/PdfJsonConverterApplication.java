package org.vitargo.pdfjsonconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.vitargo.pdfjsonconverter.convertor.ParserUtility;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.vitargo.pdfjsonconverter.convertor.ParserUtility.convertImageToStringBase64;

@SpringBootApplication
public class PdfJsonConverterApplication {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(PdfJsonConverterApplication.class, args);

//        --------------------
        ClassLoader classLoader = PdfJsonConverterApplication.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("docs/r_2_2.pdf")).getFile());
        String json = ParserUtility.pdfToJson(file);
        System.out.println(json);
//        --------------------
        File image = new File(Objects.requireNonNull(classLoader.getResource("docs/Image_1.png")).getFile());
        String imageString = convertImageToStringBase64(image);
        System.out.println(imageString);
    }
}
