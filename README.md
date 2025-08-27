# Package a FAT jar for REST client calls

This is a project to generate the fat jar for 3DExperience client calls using
1. google guava
2. apache poi
3. apache commons


# Package the jar

```
mvn clean package -DgitTag=v1.2.3 -DbuildNumber=123
```

## Run the code

```
java -cp target/json-http-excel-guava.jar com.rhushi.BuildInfo
```


