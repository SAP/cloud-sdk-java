## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add support for using the Zero Trust Identity Service (ZTIS) on Kyma by detecting the [well-known environment variable `SPIFFE_ENDPOINT_SOCKET`](https://github.com/spiffe/spiffe/blob/main/standards/SPIFFE_Workload_Endpoint.md#4-locating-the-endpoint).

### 📈 Improvements

- When the circuit breaker opens, the resulting `ResilienceRuntimeException` will have the original `CallNotPermittedException` from the circuit breaker stored as a suppressed exception. 

### 🐛 Fixed Issues

- 
