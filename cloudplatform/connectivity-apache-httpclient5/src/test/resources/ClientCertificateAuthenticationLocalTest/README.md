
# Credentials

The credential files are generated from command line. This process can be automated.

## CREATE CLIENT CREDENTIALS

* Client keystore
  ```
  docker run --rm -v $(pwd)/certs:/certs eclipse-temurin:17-jre \
   keytool -genkeypair \
   -alias client1 \
   -keyalg RSA \
   -keysize 2048 \
   -validity 3650 \
   -storetype JKS \
   -keystore /certs/client1.jks \
   -storepass changeit \
   -keypass changeit \
   -dname "CN=client1"
  ```

  <details><summary>(Windows)</summary>

  ```
  docker run --rm -v ${pwd}/certs:/certs eclipse-temurin:17-jre keytool -genkeypair -alias client1 -keyalg RSA -keysize 2048 -validity 3650 -storetype JKS -keystore /certs/client1.jks -storepass changeit -keypass changeit -dname "CN=client1"
  ```
  
  </details>

* Export client certificate
  ```
  docker run --rm -v $(pwd)/certs:/certs eclipse-temurin:17-jre \
   keytool -exportcert \
   -alias client1 \
   -keystore /certs/client1.jks \
   -storepass changeit \
   -file /certs/client1.cer
   ```

  <details><summary>(Windows)</summary>

  ```
  docker run --rm -v ${pwd}/certs:/certs eclipse-temurin:17-jre keytool -exportcert -alias client1 -keystore /certs/client1.jks -storepass changeit -file /certs/client1.cer
  ```

  </details>

* PKCS12 keystore for client

  ```
  docker run --rm -v $(pwd)/certs:/certs eclipse-temurin:17-jre \
   keytool -importkeystore \
   -srckeystore /certs/client1.jks \
   -srcstoretype JKS \
   -srcstorepass changeit \
   -destkeystore /certs/client1.p12 \
   -deststoretype PKCS12 \
   -deststorepass changeit \
   -destkeypass changeit
  ```

  <details><summary>(Windows)</summary>

  ```
  docker run --rm -v ${pwd}/certs:/certs eclipse-temurin:17-jre keytool -importkeystore -srckeystore /certs/client1.jks -srcstoretype JKS -srcstorepass changeit -destkeystore /certs/client1.p12 -deststoretype PKCS12 -deststorepass changeit -destkeypass changeit
  ```
  
  </details>


## CREATE SERVER CREDENTIALS

* Server keystore. Run once
  ```
  docker run --rm -v $(pwd)/certs:/certs eclipse-temurin:17-jre \
   keytool -genkeypair \
   -alias wiremock-server \
   -keyalg RSA \
   -keysize 2048 \
   -validity 3650 \
   -storetype JKS \
   -keystore /certs/server.jks \
   -storepass changeit \
   -keypass changeit \
   -dname "CN=localhost" \
   -ext SAN=dns:localhost,ip:127.0.0.1
   ```

  <details><summary>(Windows)</summary>
  
  ```
  docker run --rm -v ${pwd}/certs:/certs eclipse-temurin:17-jre keytool -genkeypair -alias wiremock-server -keyalg RSA -keysize 2048 -validity 3650 -storetype JKS -keystore /certs/server.jks -storepass changeit -keypass changeit -dname "CN=localhost" -ext SAN=dns:localhost,ip:127.0.0.1
  ```
  
  </details>

* Truststore for wiremock

  ```
  docker run --rm -v $(pwd)/certs:/certs eclipse-temurin:17-jre \
   keytool -importcert \
   -alias client1 \
   -file /certs/client1.cer \
   -keystore /certs/truststore.jks \
   -storepass changeit \
   -noprompt
  ```

  <details><summary>(Windows)</summary>

  ```
  docker run --rm -v ${pwd}/certs:/certs eclipse-temurin:17-jre keytool -importcert -alias client1 -file /certs/client1.cer -keystore /certs/truststore.jks -storepass changeit -noprompt
  ```
  
  </details>
