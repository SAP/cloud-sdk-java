# Credentials

The credential files are required for running the FIPS provider tests.

## Generate Client Credentials

Run the following commands from `cloudplatform/connectivity-fips-sample/src/test/resources/`:

```bash
# Create the directory
mkdir -p certificates

# Generate the key and certificate using Docker (alpine/openssl)
docker run --rm -v "$(pwd)/certificates:/certs" alpine/openssl \
 req -x509 -newkey rsa:2048 -nodes \
 -keyout /certs/client-cert.key \
 -out /certs/client-cert.crt \
 -days 3650 -subj "/CN=localhost"