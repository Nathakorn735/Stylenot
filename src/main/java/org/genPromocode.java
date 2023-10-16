package main.java.org;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Random;

public class genPromocode {
    private static final String PROMOCODE_FILE = "src/resources/json/promotionCode.json";

    public static String generatePromotionCode() {
        try {
            String promotionCode = genPromotionCode(); // แก้ตรงนี้เป็น generatePromotionCode()
            savePromotionCodeToFile(promotionCode);
            System.out.println("Promotion Code generated and saved successfully: " + promotionCode);
            return promotionCode; // เพิ่มบรรทัดนี้เพื่อส่งค่า promotionCode กลับ
        } catch (IOException e) {
            System.out.println("Error generating or saving Promotion Code: " + e.getMessage());
            return null; // เพิ่มบรรทัดนี้เพื่อส่งค่า null ในกรณีเกิดข้อผิดพลาด
        }
    }

    private static String genPromotionCode() {
        // สร้างตัวเลขสุ่ม 10 ตัว
        Random random = new Random();
        StringBuilder promotionCodeBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            // กำหนดให้เป็นตัวเลขหรือตัวอักษรสุ่ม
            char nextChar = random.nextBoolean() ? getRandomDigit() : getRandomLetter();
            promotionCodeBuilder.append(nextChar);
        }
        return promotionCodeBuilder.toString();
    }

    private static char getRandomDigit() {
        Random random = new Random();
        // สุ่มตัวเลข 0-9
        return (char) ('0' + random.nextInt(10));
    }

    private static char getRandomLetter() {
        Random random = new Random();
        // สุ่มตัวอักษร a-z
        return (char) ('a' + random.nextInt(26));
    }

    private static void savePromotionCodeToFile(String promotionCode) throws IOException {
        // สร้าง JSONObject และเพิ่ม Promotion Code เข้าไป
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("promotionCode", promotionCode);

        // อ่านข้อมูล JSON จากไฟล์เดิม (ถ้ามี)
        JSONArray jsonArray;
        try {
            jsonArray = readJSONArrayFromFile(PROMOCODE_FILE);
        } catch (Exception e) {
            jsonArray = new JSONArray();
        }

        // เพิ่ม JSONObject ใน JSONArray
        jsonArray.add(jsonObject);

        // เขียน JSONArray ลงในไฟล์
        try (FileWriter fileWriter = new FileWriter(PROMOCODE_FILE)) {
            fileWriter.write(jsonArray.toJSONString());
        }
    }

    private static JSONArray readJSONArrayFromFile(String filePath) throws Exception {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONArray) jsonParser.parse(reader);
        }
    }
}