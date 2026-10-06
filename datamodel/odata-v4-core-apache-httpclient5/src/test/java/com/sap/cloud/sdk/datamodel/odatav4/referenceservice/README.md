# TripPin Service

The tests in this package are based on the TripPin service originally (24.08.2023) found here: <https://services.odata.org/TripPinRESTierService/(S(4pryolt233c2xoigbbtlyskm))/$metadata>

The modified version can be found here: `<sdk-root>/datamodel/odata-v4/odata-v4-core/src/test/resources/unused_trippin.edmx`

## Regenerating the Model

Using the latest OData v4 Generator, take the metadata linked above and use a command similar to this:

```bash
java -jar odata-v4-generator-cli-4.21.0.jar -i <sdk-root>/datamodel/odata-v4/odata-v4-core/src/test/resources/ -o <sdk-root>/datamodel/odata-v4/odata-v4-core/src/test/java/ -b TripPinRESTierService -f --use-odata-names --service-methods-per-entity-set --sap-copyright-header -p com.sap.cloud.sdk.datamodel.odatav4.referenceservice
```