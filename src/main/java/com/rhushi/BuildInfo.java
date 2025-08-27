package com.rhushi;


import java.io.InputStream;
import java.util.Properties;

public class BuildInfo {

   public static void main(String[] args) {
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

