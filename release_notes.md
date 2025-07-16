## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- OData v2 and OData v4: Fix eager HTTP response evaluation for _Create_, _Update_, and _Delete_ request builders in convenience APIs.
  Previous change of `5.20.0` may have resulted in the HTTP connection being left open after the request was executed.
- Generic OData Client: Revert behavior change introduced in `5.20.0` that let to HTTP responses not being consumed by default.
  If you want to use `disableBufferingHttpResponse()` on `ODataRequestRead` or `ODataRequestReadByKey` please switch to `withoutResponseBuffering()` instead.