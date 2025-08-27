package com.rhushi;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class BuildInfo {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        System.out.println("Build Timestamp: " + sdf.format(new Date()));
    }
    public static void main1(String[] args) {
        Properties properties = new Properties();

        try (InputStream input = BuildInfo.class
                .getClassLoader()
                .getResourceAsStream("build.properties")) {

            if (input == null) {
                System.err.println("build.properties not found in classpath!");
                return;
            }

            properties.load(input);

            String version = properties.getProperty("version", "unknown");
            String timestamp = properties.getProperty("timestamp", "unknown");
            String gitTag = properties.getProperty("gitTag", "unspecified");

            System.out.println("Build Version: " + version);
            System.out.println("Build Timestamp: " + timestamp);
            System.out.println("Git Tag: " + gitTag);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

