package com.treestructure.certinator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Service
@Slf4j
public class TruststoreCheckService {

    /**
     * calls the given url and verifies that the url is reachable
     * @param trustStorePath
     * @param checkUrl
     * @param trustStorePassword
     */
    public boolean checkForTrust(String trustStorePath, String checkUrl, String trustStorePassword) {

        try {
            var sslContext = new SSLContextBuilder().loadTrustMaterial(
                    new File(trustStorePath),
                    trustStorePassword.toCharArray(),
                    new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
            var httpClient = HttpClients.custom() .setSSLSocketFactory(socketFactory).build();
            var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

            var restTemplate = new RestTemplate(factory);
            try {
                var result = restTemplate.getForEntity(checkUrl, String.class);
                return result.getStatusCode() != HttpStatus.FORBIDDEN;
            }
            catch( ResourceAccessException e) {
                return false;
            }

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
