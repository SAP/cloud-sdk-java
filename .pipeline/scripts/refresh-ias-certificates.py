#!/usr/bin/env python3

import subprocess


class Binding:
    def __init__(self, app_name, service_name, app_identifier):
        self.app_name = app_name
        self.service_name = service_name
        self.app_identifier = app_identifier

# This script is used to refresh the IAS certificates for the following service bindings:
bindings = [
    Binding("scp-cf-spring-sms-provider-test-java17", "cf-sprias-ias", "sms-17-app"),
    Binding("scp-cf-spring-sms-provider-test-java17-approuter", "cf-sprias-ias", "sms-17-router"),

    Binding("scp-cf-spring-sms-provider-test", "cf-sprias-ias", "sms-app"),
    Binding("scp-cf-spring-sms-provider-test-approuter", "cf-sprias-ias", "sms-router"),

    Binding("scp-cf-spring-ias-test-java17", "cf-spring-ias", "ias-17-app"),
    Binding("scp-cf-spring-ias-test-java17-approuter", "cf-spring-ias", "ias-17-router"),

    Binding("scp-cf-spring-ias-test", "cf-spring-ias", "ias-app"),
    Binding("scp-cf-spring-ias-test-approuter", "cf-spring-ias", "ias-router"),
]

for binding in bindings:
    # Information on the parameter can be found here:
    # https://github.wdf.sap.corp/CPSecurity/Knowledge-Base/blob/master/08_Tutorials/iasbroker/README.md#service-binding-creation
    binding_parameter = (f'{{"credential-type": "X509_GENERATED", "app-identifier": "{binding.app_identifier}", '
                         f'"validity": 1, "validity-type": "YEARS"}}')
    subprocess.run(["cf", "unbind-service", binding.app_name, binding.service_name])
    subprocess.run(["cf", "bind-service", binding.app_name, binding.service_name, "-c", binding_parameter])

