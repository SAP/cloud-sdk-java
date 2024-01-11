
# Credentials

The credential files are generated from command line. This process can be automated.

## CREATE CLIENT CREDENTIALS

* Generate key pair
  ```bash
  openssl req -newkey rsa:2048 -nodes -keyout cca-client.key.pem -x509 -days 750 -out cca-client.csr.pem -subj "/C=DE/ST=Brandenburg/L=Potsdam/O=SAP/OU=SAP/CN=cca-client"
  ```

* Generate _PKCS#12_ keystore
  ```bash
  openssl pkcs12 -inkey cca-client.key.pem -in cca-client.csr.pem -export -out cca-client.p12 -passout "pass:cca-password"
  ```
  
## CREATE HOST CREDENTIALS

* Generate _JKS_ keystore with localhost credentials
  ```bash
  keytool -genkey -alias cca-host -storetype jks -keyalg RSA -keysize 2048 -keystore cca-host.jks -validity 750 -storepass "cca-password" -dname "CN=localhost, OU=SAP, O=SAP, L=Potsdam, ST=Brandenburg, C=DE" -keypass "cca-password"
  ```

* Add public client certificate to keystore
  ```bash
  openssl x509 -outform der -in cca-client.csr.pem -out cca-client.der
  keytool -import -alias cca-client -keystore cca-host.jks -file cca-client.der -storepass "cca-password" -noprompt
  ```