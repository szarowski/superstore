# SuperStore Product Web Application
 
This is the SuperStore Product REST API with Spring Boot deploed on Embedded Tomcat accessing Embedded MongoDB application working with ReactJS client with secure
and unsecure access. It's pretty well JUnit tested. Secure access is based on spring web authentication, protocol is http for simplicity, but the session
holds the authentication. You can login in your favourite web browser (hopefully it is Chrome) as follows:

1. http://localhost:8080 as roman/szarowski
OR
2. LOGIN: curl.exe -H "Content-Type: application/x-www-form-urlencoded" -X POST -d "username=roman&password=szarowski&submit=Login" http://localhost:8080/login

Currently, the only available credential to log in is:
 user:roman
 password:szarowski

* To access securely, you need to use the rootPath: /store/products
* To access unsecurely (from curl in command line as well), you need to use the rootPath: /store/productsUnsecure

---

You can build the web application by using the following command (the minimum Maven version is 3.2.x):

    mvn clean install

You can clean, build and run the web application by using the following command:

    mvn clean spring-boot:run

---

The list of curl API commands {id} means the id of the product as a String.
curl.exe is for windows, curl under Linux/Unix.
(Don't forget to escape double quotes on the command line.):

* RETRIEVE ALL: curl.exe -G localhost:8080/store/productsUnsecure
* RETRIEVE: curl.exe -G localhost:8080/store/productsUnsecure/{id}
* CREATE: curl.exe -H "Content-Type: application/json" -X POST -d "{\"name\":\"product_1\", \"description\":\"Amazing product to sell\",\"prices\":{\"USD\":121,\"GBP\":111}}" http://localhost:8080/store/productsUnsecure
* UPDATE: curl.exe -H "Content-Type: application/json" -X PUT -d "{\"id\":\"{id}\", \"name\":\"super_product\", \"description\":\"Product with Discount\", \"prices\":{\"GBP\":\"40\", \"USD\":50, \"CZK\":2000}}" http://localhost:8080/store/productsUnsecure/{id}
* DELETE: curl.exe -X DELETE http://localhost:8080/store/productsUnsecure/{id}
* LOGIN: curl.exe -H "Content-Type: application/x-www-form-urlencoded" -X POST -d "username=roman&password=szarowski&submit=Login" http://localhost:8080/login
