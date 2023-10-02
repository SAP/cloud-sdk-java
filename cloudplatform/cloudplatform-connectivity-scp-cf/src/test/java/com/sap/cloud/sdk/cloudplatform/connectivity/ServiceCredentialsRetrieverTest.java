/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cloud.sdk.cloudplatform.security.ClientCertificate;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;

public class ServiceCredentialsRetrieverTest
{
    private static final JsonObject EXPLICIT_BINDING_SECRET =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"credential-type\": \"binding-secret\",\n"
                    + "    \"clientid\": \"explicit-binding-client-id\",\n"
                    + "    \"clientsecret\": \"explicit-binding-client-secret\"\n"
                    + "}")
            .getAsJsonObject();

    private static final JsonObject EXPLICIT_INSTANCE_SECRET =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"credential-type\": \"instance-secret\",\n"
                    + "    \"clientid\": \"explicit-instance-client-id\",\n"
                    + "    \"clientsecret\": \"explicit-instance-client-secret\"\n"
                    + "}")
            .getAsJsonObject();

    private static final JsonObject IMPLICIT_BINDING_SECRET =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"clientid\": \"implicit-binding-client-id\",\n"
                    + "    \"clientsecret\": \"implicit-binding-client-secret\"\n"
                    + "}")
            .getAsJsonObject();

    private static final JsonObject EXPLICIT_X509 =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"certurl\": \"xsuaa.cert.url\",\n"
                    + "    \"credential-type\": \"x509\",\n"
                    + "    \"clientid\": \"explicit-x509-client-id\",\n"
                    + "    \"certificate\": \"-----BEGIN CERTIFICATE-----\\nMIIFujCCA6KgAwIBAgIQF4MAxxsfdrxjWMfDkeJwuTANBgkqhkiG9w0BAQsFADB5\\nMQswCQYDVQQGEwJERTENMAsGA1UEBwwERVUxMDEPMA0GA1UECgwGU0FQIFNFMSMw\\nIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czElMCMGA1UEAwwcU0FQ\\nIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTAeFw0yMTA3MjcwODAwMzlaFw0yMTA4\\nMDMwOTAwMzlaMIGzMQswCQYDVQQGEwJERTEPMA0GA1UEChMGU0FQIFNFMSMwIQYD\\nVQQLExpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czEtMCsGA1UECxMkNDRhYWRj\\nNWItYWJiZS00M2MxLTg2MWEtZTQ5MzlmY2M2ZDdjMRAwDgYDVQQHEwdzYXAtdWFh\\nMS0wKwYDVQQDEyQ4ZDRhZGM5MS1lZWM4LTQ2ZTMtYTU5Ny1jYmMzY2ZiN2NkZWUw\\nggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCvAehPfvazIP/4yH6tqz0T\\npddBvfye0L5s6GI4HC83IY+TqV0Vi56PP6qK9/q3TosAKq0iv+LAdzt6IaM5lCcd\\nmLDCGwEvYFh9p9gyNFUUZzZD5BUgLMpdTSAZqYsvJpLM1dwmgijWdvMUx8gaBCrb\\n1c4RPnT2kVDrq1VigNZM4gZp6V486/UuihGut0LWXA6lg3WJ1Mx3aQTDlMcL1CfT\\nrkXZ7YoAkyqPIfI+tVUloeb73o0RUhOL765FIuadsrjMT63eWbxSOEv5TfoCHtfR\\n02a9b1313hviOACBixJXuGavCkQryFK8mW5osZpD/fcTR9W/ZObD7cPkjGesXFSb\\nAgMBAAGjggEBMIH+MAkGA1UdEwQCMAAwHwYDVR0jBBgwFoAUTbDu7F1X4ag1KP8f\\nh31SsZ9FM2QwHQYDVR0OBBYEFANvMcFqO0A45LnD9jLHWcWTg3eTMA4GA1UdDwEB\\n/wQEAwIFoDATBgNVHSUEDDAKBggrBgEFBQcDAjCBiwYDVR0fBIGDMIGAMH6gfKB6\\nhnhodHRwOi8vc2FwLWNsb3VkLXBsYXRmb3JtLWNsaWVudC1jYS1ldTEwLWNybHMu\\nczMuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb20vY3JsLzkyZDc1MTg2LTNmNTIt\\nNDczMy04NTEwLWQ2ZDYyYTZjMTk2Zi5jcmwwDQYJKoZIhvcNAQELBQADggIBAGdY\\nH/NyOnJ1VfC4fm25+RgmBc2xF81lrwm+OHerfjCzeBp2Dxax09uWUovnlffdC5bE\\nXq1WyUuAqYZxSpp0qKrda3WJ1+ini5OJ1/4CQkIJMFfWS1As0VrieriGWij1QUV8\\nnf/ugyIljCvoaZtwadxofWyOhgVNEfp/AU8C/EwRjny0s04tLwbtqVSLeUjDhVx+\\nYx2xHYwCSDGXIXSOa0eyHq613+oDAW9eDq1sSz45wENsAJJ1CYiI11HPuakYkjS0\\n3GoharUgQQ3cSds0UkooCCYyVbal26/3AiIU79H7uGleqrxmN041YPw3e/QZsWU+\\nnHtBoNG83OcFY/VHkYjPB1xaXSvb/ZDEjyw2q3Q5U05OV8dULswN+qle0LILfdhz\\nSjGhZGi2G6p1IatRrPv94hLQvpAC02nNv5gP94jqsgae7S8m/XgIx72o2GIOPHIT\\ndwLlodaHu1hDNmCiz+uTN8djA6sxuo4LYUkK4YoIAjX2omLAXwxu/zdaZcwCe0w6\\nIIeU9wUepXtyIb/+WcGB+q1Xj3H4Rkc9tRmJweX7wWeRIdddVjo0jmfMEP+vs1TQ\\n5DsTRd0L9EU69KAMyM5Wuvpw/+U8rxL8yX1vu8ISofBglEZX7FRe2kWar3Cs2MIy\\nI3fSAsTRE0Jua2yEd38lKbRJqVPi1YcH1yrhr0rQ\\n-----END CERTIFICATE-----\\n-----BEGIN CERTIFICATE-----\\nMIIGYDCCBEigAwIBAgITcAAAAAR7yX5CNr+FlgAAAAAABDANBgkqhkiG9w0BAQsF\\nADBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNB\\nUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMjAwNjIzMDg0MDQz\\nWhcNMzAwNjIzMDg1MDQzWjB5MQswCQYDVQQGEwJERTENMAsGA1UEBwwERVUxMDEP\\nMA0GA1UECgwGU0FQIFNFMSMwIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xp\\nZW50czElMCMGA1UEAwwcU0FQIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTCCAiIw\\nDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBALlGo3XFeYInLJjlKDxBb4lBIfyy\\n/v7Keo12B/yW2eVRChhxUeTyOPGmKZkzh40zAfXLQRrmVlAyksC4jQpurxD4rTXc\\nd9AxBMqm9ExqRP1SKkOQ7+pb7osaekGImuVKn+nNJ3nsna+MkgIfX34qU7Yt3aX+\\nnFJSaF8AchjtDFXxQTclOog3uIyHJOShCOKs+hIuNh4ueAlSE63yNR8Koe0ptBJC\\nrMVcuCIwcxwLMA9n8PVs95/2Zh7ZDZJh/s9X79FzSNnFvNJiRpowGU6HJYVNwGYk\\n7M0WcBS5Z+9RNF00DqR0Kt6zqFPfh+EeGB3AUb3V+nLP7Sxy7Giqvkc2H1hxbEPy\\nvafV2o4hkrJLBfmwk3N+ZbWTot+Wb9YE/PBm6w2TGmAASbXAaVegA0Rr8yj/2AkH\\n115kB1DTWoPrCLgZm1guz7jVGTT3uEot7AjPkEUIiS1Fviq1+PC5utBkTq60K37S\\n+arnNvrmLOMdJ3ir2LA0givs9A88b6w++kI8XjcaA2LFlo/TimMWTzKAsg6geUYZ\\nK23OnHZUzJpUpkMLjS8NYQDkZn/JSm92loXSxT/y5n49fGt5mieU28NiOgGl/0yM\\nwOQ5LpsdnLrWHcQ29jF1u02I121gAebic7Wtx/fz0j7RYqUMf5WcIoqc2x5jCkWj\\nJ8G7ZK+h0Qs8YkhHAgMBAAGjggELMIIBBzASBgNVHRMBAf8ECDAGAQH/AgEAMB0G\\nA1UdDgQWBBRNsO7sXVfhqDUo/x+HfVKxn0UzZDAfBgNVHSMEGDAWgBQcvGYrDsqN\\nS5+Nk3GpD8pnRmkTIjBKBgNVHR8EQzBBMD+gPaA7hjlodHRwOi8vY2RwLnBraS5j\\nby5zYXAuY29tL2NkcC9TQVAlMjBDbG91ZCUyMFJvb3QlMjBDQS5jcmwwVQYIKwYB\\nBQUHAQEESTBHMEUGCCsGAQUFBzAChjlodHRwOi8vYWlhLnBraS5jby5zYXAuY29t\\nL2FpYS9TQVAlMjBDbG91ZCUyMFJvb3QlMjBDQS5jcnQwDgYDVR0PAQH/BAQDAgEG\\nMA0GCSqGSIb3DQEBCwUAA4ICAQCSpyq1AtVo5V2Vny+SUsr1jf8uPZ2sSmj8NKAN\\n2fHQWqC+M/tQ33Oun9nIdLtAhftm2xMV8ymWY0ce+/+YTpztDioWSJMjjNVpb4O7\\nBk3SHf+E5xnJSvwVNHbwECxNH0A8beZKeu9WiuLHfUwWenlmuc9aFuiBWVlQM6ia\\n8O78HxZJUCXQsC9pc261BB6kKWv1mJM6Hkf4HLnkrcAZ5+1XnlLbA6fadRSxb+w9\\nAwYbHneTbt+mwJu3/YmnCQT90gEYbanXdv/+VCN+o3umjVBlV6lihQtIOW6noSYs\\nM8sXTkP+f2ZxIfXOpPdRn/SnWv0Y+eMvpYYgxSWI2/Fhye+eKJTeJ9QEQSUBU/Wi\\nKH9+W+MnLKt+UyZFw81ZQE1KksMBUkiHgrEnkh9U+tnXWqMYDTFVqgP4es5kxkJx\\nOC2mNzDwxudO4Hb6KyHzpF70AtyHVXXeheNtMlKYcLp0m/SS9+KZwmeVVWMHZlpy\\n5F1v0ecyznI9YbMJDezRMfd4d5ZSvCwqIfRuaj1yEXPZenlNCA1hb+eiHyxKcL70\\nMXqZTGZV9j/8cdAaD74O9bWhw9BiLjh+1XdUG6EGLxi7/JBDlWxqFvU7JzIjXAjX\\n1ZXJjSjTJ0Rhia2/ZtZNi/YgcRbYXFH7AOajdcqnpkOUyqd/Eb4MDZWYksZY/0+6\\nnRvEvg==\\n-----END CERTIFICATE-----\\n-----BEGIN CERTIFICATE-----\\nMIIFZjCCA06gAwIBAgIQGHcPvmUGa79M6pM42bGFYjANBgkqhkiG9w0BAQsFADBN\\nMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNBUCBT\\nRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMTkwMjEzMTExOTM2WhcN\\nMzkwMjEzMTEyNjMyWjBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYx\\nDzANBgNVBAoMBlNBUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwggIi\\nMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQChbHLXJoe/zFag6fB3IcN3d3HT\\nY14nSkEZIuUzYs7B96GFxQi0T/2s971JFiLfB4KaCG+UcG3dLXf1H/wewq8ahArh\\nFTsu4UR71ePUQiYlk/G68EFSy2zWYAJliXJS5k0DFMIWHD1lbSjCF3gPVJSUKf+v\\nHmWD5e9vcuiPBlSCaEnSeimYRhg0ITmi3RJ4Wu7H0Xp7tDd5z4HUKuyi9XRinfvG\\nkPALiBaX01QRC51cixmo0rhVe7qsNh7WDnLNBZeA0kkxNhLKDl8J6fQHKDdDEzmZ\\nKhK5KxL5p5YIZWZ8eEdNRoYRMXR0PxmHvRanzRvSVlXSbfqxaKlORfJJ1ah1bRNt\\no0ngAQchTghsrRuf3Qh/2Kn29IuBy4bjKR9CdNLxGrClvX/q26rUUlz6A3lbXbwJ\\nEHSRnendRfEiia+xfZD+NG2oZW0IdTXSqkCbnBnign+uxGH5ECjuLEtvtUx6i9Ae\\nxAvK2FqIuud+AchqiZBKzmQAhUjKUoACzNP2Bx2zgJOeB0BqGvf6aldG0n2hYxJF\\n8Xssc8TBlwvAqtiubP/UxJJPs+IHqU+zjm7KdP6dM2sbE+J9O3n8DzOP0SDyEmWU\\nUCwnmoPOQlq1z6fH9ghcp9bDdbh6adXM8I+SUYUcfvupOzBU7rWHxDCXld/24tpI\\nFA7FRzHwKXqMSjwtBQIDAQABo0IwQDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/\\nBAUwAwEB/zAdBgNVHQ4EFgQUHLxmKw7KjUufjZNxqQ/KZ0ZpEyIwDQYJKoZIhvcN\\nAQELBQADggIBABdSKQsh3EfVoqplSIx6X43y2Pp+kHZLtEsRWMzgO5LhYy2/Fvel\\neRBw/XEiB5iKuEGhxHz/Gqe0gZixw3SsHB1Q464EbGT4tPQ2UiMhiiDho9hVe6tX\\nqX1FhrhycAD1xHIxMxQP/buX9s9arFZauZrpw/Jj4tGp7aEj4hypWpO9tzjdBthy\\n5vXSviU8L2HyiQpVND/Rp+dNJmVYTiFLuULRY28QbikgFO2xp9s4RNkDBnbDeTrT\\nCKWcVsmlZLPJJQZm0n2p8CvoeAsKzIULT9YSbEEBwmeqRlmbUaoT/rUGoobSFcrP\\njrBg66y5hA2w7S3tDH0GjMpRu16b2u0hYQocUDuMlyhrkhsO+Qtqkz1ubwHCJ8PA\\nRJw6zYl9VeBtgI5F69AEJdkAgYfvPw5DJipgVuQDSv7ezi6ZcI75939ENGjSyLVy\\n4SuP99G7DuItG008T8AYFUHAM2h/yskVyvoZ8+gZx54TC9aY9gPIKyX++4bHv5BC\\nqbEdU46N05R+AIBW2KvWozQkjhSQCbzcp6DHXLoZINI6y0WOImzXrvLUSIm4CBaj\\n6MTXInIkmitdURnmpxTxLva5Kbng/u20u5ylIQKqpcD8HWX97lLVbmbnPkbpKxo+\\nLvHPhNDM3rMsLu06agF4JTbO8ANYtWQTx0PVrZKJu+8fcIaUp7MVBIVZ\\n-----END CERTIFICATE-----\\n\",\n"
                    + "    \"key\": \"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2MQTcaR8eeeILLme4ALNJTHxd1alhJRQ5E2UE4MM4eba/ziJFWZnmDcm2/sTZd52czTEzUmWaMNCwx+jQR6LLEHtXtuc2prugDazAoatkYru0g+9FqiAKpcDmCySaR1Z2jmy50xw8odBwZKtlNik2zhndoE3virI6PGycdbdviB6Su51McbqdlFGawmxI7JhtHDnbmljFTIHhu2OWap5XtAk2Uh7JDv2D080Gt0B9K96krqmi9mhWrV6pntZp4WLvIi0dpzjsRrt8tO/ErzYUU04W2Xx3N6x7PCUXr7765UAWiv/Wn3mqtHyEdHxqrrPt9L0WBVxCgt7zSMg2cQEEQIDAQAB-----END PUBLIC KEY-----\"\n"
                    + "}")
            .getAsJsonObject();

    private static final JsonObject EXPLICIT_X509_WITH_SECRET =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"certurl\": \"xsuaa.cert.url\",\n"
                    + "    \"credential-type\": \"x509\",\n"
                    + "    \"clientid\": \"explicit-x509-client-id\",\n"
                    + "    \"clientsecret\": \"compatibility-client-secret\",\n"
                    + "    \"certificate\": \"-----BEGIN CERTIFICATE-----\\nMIIFujCCA6KgAwIBAgIQF4MAxxsfdrxjWMfDkeJwuTANBgkqhkiG9w0BAQsFADB5\\nMQswCQYDVQQGEwJERTENMAsGA1UEBwwERVUxMDEPMA0GA1UECgwGU0FQIFNFMSMw\\nIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czElMCMGA1UEAwwcU0FQ\\nIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTAeFw0yMTA3MjcwODAwMzlaFw0yMTA4\\nMDMwOTAwMzlaMIGzMQswCQYDVQQGEwJERTEPMA0GA1UEChMGU0FQIFNFMSMwIQYD\\nVQQLExpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czEtMCsGA1UECxMkNDRhYWRj\\nNWItYWJiZS00M2MxLTg2MWEtZTQ5MzlmY2M2ZDdjMRAwDgYDVQQHEwdzYXAtdWFh\\nMS0wKwYDVQQDEyQ4ZDRhZGM5MS1lZWM4LTQ2ZTMtYTU5Ny1jYmMzY2ZiN2NkZWUw\\nggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCvAehPfvazIP/4yH6tqz0T\\npddBvfye0L5s6GI4HC83IY+TqV0Vi56PP6qK9/q3TosAKq0iv+LAdzt6IaM5lCcd\\nmLDCGwEvYFh9p9gyNFUUZzZD5BUgLMpdTSAZqYsvJpLM1dwmgijWdvMUx8gaBCrb\\n1c4RPnT2kVDrq1VigNZM4gZp6V486/UuihGut0LWXA6lg3WJ1Mx3aQTDlMcL1CfT\\nrkXZ7YoAkyqPIfI+tVUloeb73o0RUhOL765FIuadsrjMT63eWbxSOEv5TfoCHtfR\\n02a9b1313hviOACBixJXuGavCkQryFK8mW5osZpD/fcTR9W/ZObD7cPkjGesXFSb\\nAgMBAAGjggEBMIH+MAkGA1UdEwQCMAAwHwYDVR0jBBgwFoAUTbDu7F1X4ag1KP8f\\nh31SsZ9FM2QwHQYDVR0OBBYEFANvMcFqO0A45LnD9jLHWcWTg3eTMA4GA1UdDwEB\\n/wQEAwIFoDATBgNVHSUEDDAKBggrBgEFBQcDAjCBiwYDVR0fBIGDMIGAMH6gfKB6\\nhnhodHRwOi8vc2FwLWNsb3VkLXBsYXRmb3JtLWNsaWVudC1jYS1ldTEwLWNybHMu\\nczMuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb20vY3JsLzkyZDc1MTg2LTNmNTIt\\nNDczMy04NTEwLWQ2ZDYyYTZjMTk2Zi5jcmwwDQYJKoZIhvcNAQELBQADggIBAGdY\\nH/NyOnJ1VfC4fm25+RgmBc2xF81lrwm+OHerfjCzeBp2Dxax09uWUovnlffdC5bE\\nXq1WyUuAqYZxSpp0qKrda3WJ1+ini5OJ1/4CQkIJMFfWS1As0VrieriGWij1QUV8\\nnf/ugyIljCvoaZtwadxofWyOhgVNEfp/AU8C/EwRjny0s04tLwbtqVSLeUjDhVx+\\nYx2xHYwCSDGXIXSOa0eyHq613+oDAW9eDq1sSz45wENsAJJ1CYiI11HPuakYkjS0\\n3GoharUgQQ3cSds0UkooCCYyVbal26/3AiIU79H7uGleqrxmN041YPw3e/QZsWU+\\nnHtBoNG83OcFY/VHkYjPB1xaXSvb/ZDEjyw2q3Q5U05OV8dULswN+qle0LILfdhz\\nSjGhZGi2G6p1IatRrPv94hLQvpAC02nNv5gP94jqsgae7S8m/XgIx72o2GIOPHIT\\ndwLlodaHu1hDNmCiz+uTN8djA6sxuo4LYUkK4YoIAjX2omLAXwxu/zdaZcwCe0w6\\nIIeU9wUepXtyIb/+WcGB+q1Xj3H4Rkc9tRmJweX7wWeRIdddVjo0jmfMEP+vs1TQ\\n5DsTRd0L9EU69KAMyM5Wuvpw/+U8rxL8yX1vu8ISofBglEZX7FRe2kWar3Cs2MIy\\nI3fSAsTRE0Jua2yEd38lKbRJqVPi1YcH1yrhr0rQ\\n-----END CERTIFICATE-----\\n-----BEGIN CERTIFICATE-----\\nMIIGYDCCBEigAwIBAgITcAAAAAR7yX5CNr+FlgAAAAAABDANBgkqhkiG9w0BAQsF\\nADBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNB\\nUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMjAwNjIzMDg0MDQz\\nWhcNMzAwNjIzMDg1MDQzWjB5MQswCQYDVQQGEwJERTENMAsGA1UEBwwERVUxMDEP\\nMA0GA1UECgwGU0FQIFNFMSMwIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xp\\nZW50czElMCMGA1UEAwwcU0FQIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTCCAiIw\\nDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBALlGo3XFeYInLJjlKDxBb4lBIfyy\\n/v7Keo12B/yW2eVRChhxUeTyOPGmKZkzh40zAfXLQRrmVlAyksC4jQpurxD4rTXc\\nd9AxBMqm9ExqRP1SKkOQ7+pb7osaekGImuVKn+nNJ3nsna+MkgIfX34qU7Yt3aX+\\nnFJSaF8AchjtDFXxQTclOog3uIyHJOShCOKs+hIuNh4ueAlSE63yNR8Koe0ptBJC\\nrMVcuCIwcxwLMA9n8PVs95/2Zh7ZDZJh/s9X79FzSNnFvNJiRpowGU6HJYVNwGYk\\n7M0WcBS5Z+9RNF00DqR0Kt6zqFPfh+EeGB3AUb3V+nLP7Sxy7Giqvkc2H1hxbEPy\\nvafV2o4hkrJLBfmwk3N+ZbWTot+Wb9YE/PBm6w2TGmAASbXAaVegA0Rr8yj/2AkH\\n115kB1DTWoPrCLgZm1guz7jVGTT3uEot7AjPkEUIiS1Fviq1+PC5utBkTq60K37S\\n+arnNvrmLOMdJ3ir2LA0givs9A88b6w++kI8XjcaA2LFlo/TimMWTzKAsg6geUYZ\\nK23OnHZUzJpUpkMLjS8NYQDkZn/JSm92loXSxT/y5n49fGt5mieU28NiOgGl/0yM\\nwOQ5LpsdnLrWHcQ29jF1u02I121gAebic7Wtx/fz0j7RYqUMf5WcIoqc2x5jCkWj\\nJ8G7ZK+h0Qs8YkhHAgMBAAGjggELMIIBBzASBgNVHRMBAf8ECDAGAQH/AgEAMB0G\\nA1UdDgQWBBRNsO7sXVfhqDUo/x+HfVKxn0UzZDAfBgNVHSMEGDAWgBQcvGYrDsqN\\nS5+Nk3GpD8pnRmkTIjBKBgNVHR8EQzBBMD+gPaA7hjlodHRwOi8vY2RwLnBraS5j\\nby5zYXAuY29tL2NkcC9TQVAlMjBDbG91ZCUyMFJvb3QlMjBDQS5jcmwwVQYIKwYB\\nBQUHAQEESTBHMEUGCCsGAQUFBzAChjlodHRwOi8vYWlhLnBraS5jby5zYXAuY29t\\nL2FpYS9TQVAlMjBDbG91ZCUyMFJvb3QlMjBDQS5jcnQwDgYDVR0PAQH/BAQDAgEG\\nMA0GCSqGSIb3DQEBCwUAA4ICAQCSpyq1AtVo5V2Vny+SUsr1jf8uPZ2sSmj8NKAN\\n2fHQWqC+M/tQ33Oun9nIdLtAhftm2xMV8ymWY0ce+/+YTpztDioWSJMjjNVpb4O7\\nBk3SHf+E5xnJSvwVNHbwECxNH0A8beZKeu9WiuLHfUwWenlmuc9aFuiBWVlQM6ia\\n8O78HxZJUCXQsC9pc261BB6kKWv1mJM6Hkf4HLnkrcAZ5+1XnlLbA6fadRSxb+w9\\nAwYbHneTbt+mwJu3/YmnCQT90gEYbanXdv/+VCN+o3umjVBlV6lihQtIOW6noSYs\\nM8sXTkP+f2ZxIfXOpPdRn/SnWv0Y+eMvpYYgxSWI2/Fhye+eKJTeJ9QEQSUBU/Wi\\nKH9+W+MnLKt+UyZFw81ZQE1KksMBUkiHgrEnkh9U+tnXWqMYDTFVqgP4es5kxkJx\\nOC2mNzDwxudO4Hb6KyHzpF70AtyHVXXeheNtMlKYcLp0m/SS9+KZwmeVVWMHZlpy\\n5F1v0ecyznI9YbMJDezRMfd4d5ZSvCwqIfRuaj1yEXPZenlNCA1hb+eiHyxKcL70\\nMXqZTGZV9j/8cdAaD74O9bWhw9BiLjh+1XdUG6EGLxi7/JBDlWxqFvU7JzIjXAjX\\n1ZXJjSjTJ0Rhia2/ZtZNi/YgcRbYXFH7AOajdcqnpkOUyqd/Eb4MDZWYksZY/0+6\\nnRvEvg==\\n-----END CERTIFICATE-----\\n-----BEGIN CERTIFICATE-----\\nMIIFZjCCA06gAwIBAgIQGHcPvmUGa79M6pM42bGFYjANBgkqhkiG9w0BAQsFADBN\\nMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNBUCBT\\nRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMTkwMjEzMTExOTM2WhcN\\nMzkwMjEzMTEyNjMyWjBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYx\\nDzANBgNVBAoMBlNBUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwggIi\\nMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQChbHLXJoe/zFag6fB3IcN3d3HT\\nY14nSkEZIuUzYs7B96GFxQi0T/2s971JFiLfB4KaCG+UcG3dLXf1H/wewq8ahArh\\nFTsu4UR71ePUQiYlk/G68EFSy2zWYAJliXJS5k0DFMIWHD1lbSjCF3gPVJSUKf+v\\nHmWD5e9vcuiPBlSCaEnSeimYRhg0ITmi3RJ4Wu7H0Xp7tDd5z4HUKuyi9XRinfvG\\nkPALiBaX01QRC51cixmo0rhVe7qsNh7WDnLNBZeA0kkxNhLKDl8J6fQHKDdDEzmZ\\nKhK5KxL5p5YIZWZ8eEdNRoYRMXR0PxmHvRanzRvSVlXSbfqxaKlORfJJ1ah1bRNt\\no0ngAQchTghsrRuf3Qh/2Kn29IuBy4bjKR9CdNLxGrClvX/q26rUUlz6A3lbXbwJ\\nEHSRnendRfEiia+xfZD+NG2oZW0IdTXSqkCbnBnign+uxGH5ECjuLEtvtUx6i9Ae\\nxAvK2FqIuud+AchqiZBKzmQAhUjKUoACzNP2Bx2zgJOeB0BqGvf6aldG0n2hYxJF\\n8Xssc8TBlwvAqtiubP/UxJJPs+IHqU+zjm7KdP6dM2sbE+J9O3n8DzOP0SDyEmWU\\nUCwnmoPOQlq1z6fH9ghcp9bDdbh6adXM8I+SUYUcfvupOzBU7rWHxDCXld/24tpI\\nFA7FRzHwKXqMSjwtBQIDAQABo0IwQDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/\\nBAUwAwEB/zAdBgNVHQ4EFgQUHLxmKw7KjUufjZNxqQ/KZ0ZpEyIwDQYJKoZIhvcN\\nAQELBQADggIBABdSKQsh3EfVoqplSIx6X43y2Pp+kHZLtEsRWMzgO5LhYy2/Fvel\\neRBw/XEiB5iKuEGhxHz/Gqe0gZixw3SsHB1Q464EbGT4tPQ2UiMhiiDho9hVe6tX\\nqX1FhrhycAD1xHIxMxQP/buX9s9arFZauZrpw/Jj4tGp7aEj4hypWpO9tzjdBthy\\n5vXSviU8L2HyiQpVND/Rp+dNJmVYTiFLuULRY28QbikgFO2xp9s4RNkDBnbDeTrT\\nCKWcVsmlZLPJJQZm0n2p8CvoeAsKzIULT9YSbEEBwmeqRlmbUaoT/rUGoobSFcrP\\njrBg66y5hA2w7S3tDH0GjMpRu16b2u0hYQocUDuMlyhrkhsO+Qtqkz1ubwHCJ8PA\\nRJw6zYl9VeBtgI5F69AEJdkAgYfvPw5DJipgVuQDSv7ezi6ZcI75939ENGjSyLVy\\n4SuP99G7DuItG008T8AYFUHAM2h/yskVyvoZ8+gZx54TC9aY9gPIKyX++4bHv5BC\\nqbEdU46N05R+AIBW2KvWozQkjhSQCbzcp6DHXLoZINI6y0WOImzXrvLUSIm4CBaj\\n6MTXInIkmitdURnmpxTxLva5Kbng/u20u5ylIQKqpcD8HWX97lLVbmbnPkbpKxo+\\nLvHPhNDM3rMsLu06agF4JTbO8ANYtWQTx0PVrZKJu+8fcIaUp7MVBIVZ\\n-----END CERTIFICATE-----\\n\",\n"
                    + "    \"key\": \"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2MQTcaR8eeeILLme4ALNJTHxd1alhJRQ5E2UE4MM4eba/ziJFWZnmDcm2/sTZd52czTEzUmWaMNCwx+jQR6LLEHtXtuc2prugDazAoatkYru0g+9FqiAKpcDmCySaR1Z2jmy50xw8odBwZKtlNik2zhndoE3virI6PGycdbdviB6Su51McbqdlFGawmxI7JhtHDnbmljFTIHhu2OWap5XtAk2Uh7JDv2D080Gt0B9K96krqmi9mhWrV6pntZp4WLvIi0dpzjsRrt8tO/ErzYUU04W2Xx3N6x7PCUXr7765UAWiv/Wn3mqtHyEdHxqrrPt9L0WBVxCgt7zSMg2cQEEQIDAQAB-----END PUBLIC KEY-----\"\n"
                    + "}")
            .getAsJsonObject();

    private static final JsonObject UNKNOWN_CREDENTIAL_TYPE =
        JsonParser
            .parseString(
                "{\n"
                    + "    \"url\": \"xsuaa.url\",\n"
                    + "    \"credential-type\": \"unknown\",\n"
                    + "    \"clientid\": \"unknown-binding-client-id\",\n"
                    + "    \"clientsecret\": \"unknown-binding-client-secret\"\n"
                    + "}")
            .getAsJsonObject();

    @Test
    public void testGetExplicitBindingSecretCredentials()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(EXPLICIT_BINDING_SECRET);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCredentials.class);
        assertThat(credentials.getUri()).hasToString("xsuaa.url");

        final ClientCredentials clientCredentials = (ClientCredentials) credentials.getCredentials();
        assertThat(clientCredentials.getClientId()).isEqualTo("explicit-binding-client-id");
        assertThat(clientCredentials.getClientSecret()).isEqualTo("explicit-binding-client-secret");
    }

    @Test
    public void testGetExplicitInstanceSecretCredentials()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(EXPLICIT_INSTANCE_SECRET);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCredentials.class);
        assertThat(credentials.getUri()).hasToString("xsuaa.url");

        final ClientCredentials clientCredentials = (ClientCredentials) credentials.getCredentials();
        assertThat(clientCredentials.getClientId()).isEqualTo("explicit-instance-client-id");
        assertThat(clientCredentials.getClientSecret()).isEqualTo("explicit-instance-client-secret");
    }

    @Test
    public void testGetImplicitBindingSecretCredentials()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(IMPLICIT_BINDING_SECRET);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCredentials.class);

        final ClientCredentials clientCredentials = (ClientCredentials) credentials.getCredentials();
        assertThat(clientCredentials.getClientId()).isEqualTo("implicit-binding-client-id");
        assertThat(clientCredentials.getClientSecret()).isEqualTo("implicit-binding-client-secret");
    }

    @Test
    public void testGetExplicitBindingSecretCredentialsWithMissingProperty()
    {
        final JsonObject malformedCredentials =
            JsonParser
                .parseString(
                    "{\n"
                        + "    \"url\": \"xsuaa.url\",\n"
                        + "    \"credential-type\": \"binding-secret\",\n"
                        + "    \"clientid\": \"explicit-binding-client-id\"\n"
                        + "}")
                .getAsJsonObject();

        assertThatCode(() -> new ServiceCredentialsRetriever().getCredentials(malformedCredentials))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetCredentialsDefaultsToClientCredentials()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(UNKNOWN_CREDENTIAL_TYPE);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCredentials.class);
        assertThat(credentials.getUri()).hasToString("xsuaa.url");

        final ClientCredentials clientCredentials = (ClientCredentials) credentials.getCredentials();
        assertThat(clientCredentials.getClientId()).isEqualTo("unknown-binding-client-id");
        assertThat(clientCredentials.getClientSecret()).isEqualTo("unknown-binding-client-secret");
    }

    @Test
    public void testGetExplicitX509Credentials()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(EXPLICIT_X509);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCertificate.class);
        assertThat(credentials.getUri()).hasToString("xsuaa.cert.url");

        final ClientCertificate clientCertificate = (ClientCertificate) credentials.getCredentials();
        assertThat(clientCertificate.getClientId()).isEqualTo("explicit-x509-client-id");
        assertThat(clientCertificate.getCertificate()).isNotEmpty();
        assertThat(clientCertificate.getKey()).isNotEmpty();
    }

    @Test
    public void testGetExplicitX509CredentialsIgnoringTheClientSecret()
    {
        final ServiceCredentialsRetriever.OAuth2Credentials credentials =
            new ServiceCredentialsRetriever().getCredentials(EXPLICIT_X509_WITH_SECRET);

        assertThat(credentials.getCredentials()).isInstanceOf(ClientCertificate.class);
        assertThat(credentials.getUri()).hasToString("xsuaa.cert.url");

        final ClientCertificate clientCertificate = (ClientCertificate) credentials.getCredentials();
        assertThat(clientCertificate.getClientId()).isEqualTo("explicit-x509-client-id");
        assertThat(clientCertificate.getCertificate()).isNotEmpty();
        assertThat(clientCertificate.getKey()).isNotEmpty();
    }

    @Test
    public void testGetExplicitX509CredentialsWithMissingProperty()
    {
        final JsonObject malformedCredentials =
            JsonParser
                .parseString(
                    "{\n"
                        + "    \"url\": \"xsuaa.url\",\n"
                        + "    \"credential-type\": \"x509\",\n"
                        + "    \"clientid\": \"explicit-x509-client-id\",\n"
                        + "    \"key\": \"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2MQTcaR8eeeILLme4ALNJTHxd1alhJRQ5E2UE4MM4eba/ziJFWZnmDcm2/sTZd52czTEzUmWaMNCwx+jQR6LLEHtXtuc2prugDazAoatkYru0g+9FqiAKpcDmCySaR1Z2jmy50xw8odBwZKtlNik2zhndoE3virI6PGycdbdviB6Su51McbqdlFGawmxI7JhtHDnbmljFTIHhu2OWap5XtAk2Uh7JDv2D080Gt0B9K96krqmi9mhWrV6pntZp4WLvIi0dpzjsRrt8tO/ErzYUU04W2Xx3N6x7PCUXr7765UAWiv/Wn3mqtHyEdHxqrrPt9L0WBVxCgt7zSMg2cQEEQIDAQAB-----END PUBLIC KEY-----\"\n"
                        + "}")
                .getAsJsonObject();

        assertThatCode(() -> new ServiceCredentialsRetriever().getCredentials(malformedCredentials))
            .isInstanceOf(NullPointerException.class);
    }
}
