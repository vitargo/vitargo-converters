package org.vitargo.pdfjsonconverter.convertor;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ParserUtility {

    public static Map<Date, LinkedList<Date>> logParser(String fileName) {
        Map<Date, LinkedList<Date>> map = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(" ");
                String date = tokens[0].trim();
                String time = tokens[1];
                String level = tokens[2];
                String message = line.substring(line.indexOf(tokens[3]));
                if (!date.isEmpty()) {
                    Date d = dateParser(date);
                    Date d2 = dateTimeParser(date, time);
                    LinkedList<Date> list = map.get(d);
                    if (list == null) {
                        LinkedList<Date> listNew = new LinkedList<>();
                        listNew.add(d2);
                        map.put(d, listNew);
                    } else {
                        list.add(d2);
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private static Date dateParser(String inputString) {
        String dateString;
        int endIndex = inputString.length();
        if (endIndex > 10) {
            int startIndex = endIndex - 10;
            dateString = inputString.substring(startIndex, endIndex);
        } else {
            dateString = inputString;
        }
        String formatString = "yyyy-MM-dd";
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        try {
            date = formatter.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private static Date dateTimeParser(String inputString, String timeString) {
        String dateString;
        int endIndex = inputString.length();
        if (endIndex > 10) {
            int startIndex = endIndex - 10;
            dateString = inputString.substring(startIndex, endIndex);
        } else {
            dateString = inputString;
        }
        String formatString = "yyyy-MM-dd HH:mm:ss";
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        try {
            date = formatter.parse(dateString + " " + timeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String pdfToJson(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        String[] paragraphs = text.split("\n \n");

        JSONObject js = new JSONObject();
        try {
            Field changeMap = js.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(js, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i].trim();
            if (!paragraph.isEmpty()) {
                String index = i < 10 ? "0" + i : String.valueOf(i);
                js.put("content_" + index, paragraph);

            }
        }
        try (FileWriter f =
                     new FileWriter(System.getProperty("user.dir") + "/src/main/resources/results/article_2.json")) {
            f.write(js.toString());
            f.flush();
            System.out.println("Successfully wrote JSON object to file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing JSON object to file: " + e.getMessage());
        }
        document.close();

        return js.toString();
    }

    public static String convertImageToStringBase64(File image) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(image);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private static String convertDate(Map<Date, LinkedList<Date>> map) {
        Set<Date> dates = map.keySet();
        ArrayList<Date> sort = dates.stream().sorted().collect(Collectors.toCollection(ArrayList::new));

        String str = "";
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dat = calendar.getTime();
        for (Date date : sort) {
            if (date.after(dat)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(date);
                LinkedList<Date> l = map.get(date);
                str = str + dateString + "[logs - " + l.size() + "]" + "\n";
                count++;
            }
        }
        System.out.println("[Sorted cases: " + count + "]");
        return str;
    }
}

