package com.treestructure.certinator.service;


import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
public class KeyStoreAnalyzerService {


    public Map<String, X509Certificate> getAllCerts(String path, String password) {
        var result = new HashMap<String, X509Certificate>();
        try {
            var keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path), password.toCharArray());
            keyStore.aliases().asIterator().forEachRemaining(alias -> {
                try {
                    var cert = (X509Certificate) keyStore.getCertificate(alias);
                    result.put(alias, cert);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
            });
            return result;
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasOutdatedCertificates() {
        return true;
    }
}
