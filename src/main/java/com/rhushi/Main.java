package com.rhushi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.List;


public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String apiUrl = "https://jsonplaceholder.typicode.com/users";

        // 1. GET Request
        JsonNode users = fetchUsers(apiUrl);

        // 2. Write files
        writeCsvAndTsv(users);
        writeExcel(users);
        writeJson(users);

        // 3. POST sample user
        postSampleUser("https://jsonplaceholder.typicode.com/posts");

        System.out.println("All files written and POST done.");
    }

    private static JsonNode fetchUsers(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    private static void writeCsvAndTsv(JsonNode users) throws Exception {
        try (FileWriter csvWriter = new FileWriter("users.csv");
             FileWriter tsvWriter = new FileWriter("users.tsv")) {

            // Header
            csvWriter.write("ID,Name,Email,City\n");
            tsvWriter.write("ID\tName\tEmail\tCity\n");

            Joiner csvJoiner = Joiner.on(',').useForNull("");
            Joiner tsvJoiner = Joiner.on('\t').useForNull("");

            for (JsonNode user : users) {
                String id = user.get("id").asText();
                String name = user.get("name").asText();
                String email = user.get("email").asText();
                String city = user.path("address").path("city").asText();

                List<String> values = ImmutableList.of(id, name, email, city);
                csvWriter.write(csvJoiner.join(values) + "\n");
                tsvWriter.write(tsvJoiner.join(values) + "\n");
            }
        }
    }

    private static void writeExcel(JsonNode users) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Users");

            var row = sheet.createRow(0);
            row.createCell(0).setCellValue("ID");
            row.createCell(1).setCellValue("Name");
            row.createCell(2).setCellValue("Email");
            row.createCell(3).setCellValue("City");

            int rowNum = 1;
            for (JsonNode user : users) {
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.get("id").asInt());
                row.createCell(1).setCellValue(user.get("name").asText());
                row.createCell(2).setCellValue(user.get("email").asText());
                row.createCell(3).setCellValue(user.path("address").path("city").asText());
            }

            try (FileOutputStream out = new FileOutputStream("users.xlsx")) {
                workbook.write(out);
            }
        }
    }

    private static void writeJson(JsonNode users) throws Exception {
        try (PrintWriter out = new PrintWriter("users.json")) {
            out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(users));
        }
    }

    private static void postSampleUser(String postUrl) throws Exception {
        ObjectNode userJson = mapper.createObjectNode();
        userJson.put("title", "Test Title");
        userJson.put("body", "This is a sample post body.");
        userJson.put("userId", 1);

        String requestBody = mapper.writeValueAsString(userJson);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postUrl))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("POST response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }
}
