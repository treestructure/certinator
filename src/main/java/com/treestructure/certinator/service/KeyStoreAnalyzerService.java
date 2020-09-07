package com.treestructure.certinator.service;


import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.naming.ldap.LdapName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class KeyStoreAnalyzerService {


    /**
     *
     * @param path
     * @param password
     * @return
     */
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
            return Collections.emptyMap();
        }
    }

    /**
     *
     * @param key
     * @param updateValue
     */
    public void updateEntry(String path, String key, String updateValue, String password) throws Exception{
        var keyStore = KeyStore.getInstance("JKS");

        try (var fis = new FileInputStream(path)) {
            keyStore.load(fis, password.toCharArray());
            keyStore.deleteEntry(key);

            var  pwdProtection =new KeyStore.PasswordProtection(password.toCharArray());
            var factory = SecretKeyFactory.getInstance("PBE");
            var generatedSecret =
                    factory.generateSecret(new PBEKeySpec(
                            updateValue.toCharArray()));
            keyStore.setEntry(key, new KeyStore.SecretKeyEntry(generatedSecret), pwdProtection);

            var fos = new FileOutputStream(path);
            keyStore.store(fos, password.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes an entry from the keystore
     * @param path
     * @param key
     * @param password
     * @throws Exception
     */
    public void deleteEntry(String path, String key, String password) throws Exception{
        var keyStore = KeyStore.getInstance("JKS");

        try (var fis = new FileInputStream(path)) {
            keyStore.load(fis, password.toCharArray());
            keyStore.deleteEntry(key);
            var fos = new FileOutputStream(path);
            keyStore.store(fos, password.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String path, String password, String pathToNewCert) throws Exception {
        var keyStore = KeyStore.getInstance("JKS");

        try (var fis = new FileInputStream(path)) {
            keyStore.load(fis, password.toCharArray());

            FileInputStream certInputStream = new FileInputStream(pathToNewCert);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            var cert = (X509Certificate) fact.generateCertificate(certInputStream);
            var issuer = new LdapName(cert.getIssuerDN().toString());
            var newCertKey = issuer.getRdns().stream()
                    .filter(r -> r.getType().equals("CN"))
                    .findFirst()
                    .map(rdn -> rdn.getValue().toString())
                    .map(name -> name.replace(" ", "_") + "::")
                    .orElse("");
            newCertKey += cert.getSerialNumber().toString();
            keyStore.setCertificateEntry(newCertKey, cert);

            try (FileOutputStream outputStream = new FileOutputStream(path)){
                keyStore.store(outputStream, password.toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
                e.printStackTrace();
            }

        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasOutdatedCertificates() {
        return true;
    }
}
