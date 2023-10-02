# Create the client keystore containing the client public and private keys.
keytool -genkey -keyalg RSA -keysize 2048 -alias client -validity 18250 -keypass password -storepass password -keystore client_keystore.jks -dname "CN=Client,OU=Client Company,O=Client Organization,L=Client City,ST=Client State,C=SE"

# Export the client certificate (public key).
keytool -export -alias client -keystore client_keystore.jks -storepass password -file client.cer

# Create the server truststore containing the client certificate (public key).
keytool -importcert -v -trustcacerts -alias client -keystore server_cacerts.jks -keypass password -file client.cer

# Create the server keystore containing the server public and private keys.
keytool -genkey -keyalg RSA -keysize 2048 -alias server -validity 18250 -keypass password -storepass password -keystore server_keystore.jks
 
# Export the server certificate (public key).
keytool -export -alias server -keystore server_keystore.jks -storepass password -file server.cer
 
# Create the client truststore containing the server certificate (public key).
keytool -importcert -v -trustcacerts -alias server -keystore client_cacerts.jks -storepass password -keypass password -file server.cer