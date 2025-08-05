## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- **Destination Service API Version Support**: Added support for specifying the API version when calling the destination service. Users can now opt into newer API versions (e.g., v2) by using the new `apiVersion()` method on `DestinationServiceOptionsAugmenter`. If not specified, the default API version (v1) is used, ensuring backward compatibility.

  Example usage:
  ```java
  // Use the new v2 API
  DestinationOptions options = DestinationOptions.builder()
      .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().apiVersion("v2"))
      .build();
  
  Destination destination = DestinationAccessor.getDestination("my-destination", options);
  ```

### 📈 Improvements

- 

### 🐛 Fixed Issues

-
