# Simple TODO application 


```
mvn clean install
mvn spring-boot:run
```

App UI: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui/index.html#/

Api-Docs: http://localhost:8080/v3/api-docs


### Docker

Build JAR:

```
mvn clean package
```

Build image:

```
docker build -t java-todo-app .
```

Run container: 

```
docker run -d -p 8080:8080 --name todo-app java-todo-app
```