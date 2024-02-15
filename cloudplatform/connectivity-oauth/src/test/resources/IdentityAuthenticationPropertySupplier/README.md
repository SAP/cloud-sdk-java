
# Credentials

The credential files are generated from command line. This process can be automated.

## CREATE CLIENT CREDENTIALS

* Generate an **RSA** private key:
```bash
openssl genrsa -out privatekey.pem 2048 -traditional
```

* Generate a **C**ertificate **S**igning **R**equest:
```bash
openssl req -new -key privatekey.pem -out csr.pem
```

* Sign the **CSR** with the **RSA** private key:
```bash
openssl x509 -req -days 3650 -in csr.pem -signkey privatekey.pem -out certificate.pem
```
