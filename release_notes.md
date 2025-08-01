## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add `TokenCacheParameters` to `OAuth2Options` to configurate token cache duration, expiration delta and cache size.

### 📈 Improvements

- Relax OAuth2 token cache duration to 1hr to avoid unnecessary token refreshes.
- Disable refresh tokens when obtaining user tokens from IAS.
  This acts as a workaround for a limitation of IAS, where obtaining a refresh token invalidates the original token.

### 🐛 Fixed Issues

- OData v2 and OData v4: Fix eager HTTP response evaluation for _Create_, _Update_, and _Delete_ request builders in convenience APIs.
  Previous change of `5.20.0` may have resulted in the HTTP connection being left open after the request was executed.
- Generic OData Client: Revert behavior change introduced in `5.20.0` that let to HTTP responses not being consumed by default.
  If you want to use `disableBufferingHttpResponse()` on `ODataRequestRead` or `ODataRequestReadByKey` please switch to `withoutResponseBuffering()` instead.