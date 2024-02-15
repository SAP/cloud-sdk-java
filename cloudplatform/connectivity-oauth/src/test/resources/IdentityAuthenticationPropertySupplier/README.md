
# Credentials

The credential files are generated from command line. This process can be automated.

## CREATE CLIENT CREDENTIALS

* Generate key pair
  ```bash
  openssl req -x509 -newkey rsa:2048 -utf8 -days 3650 -nodes -config client-cert.conf -keyout client-cert.key -out client-cert.crt
  ```

* Generate _PKCS#12_ keystore
  ```bash
  openssl pkcs12 -export -inkey client-cert.key -in client-cert.crt -out client-cert.p12 -password "pass:cca-password"
  ```
  
* Transform to JKS

  ```bash
  keytool -importkeystore -deststorepass "cca-password" -destkeypass "cca-password" -srckeystore client-cert.p12 -srcstorepass "cca-password" -deststoretype pkcs12 -destkeystore client-cert.pkcs12
  ```
