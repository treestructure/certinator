package com.treestructure.certinator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PasswordStoreService {

    /**
     *
     * @param path
     * @param password
     * @return
     * @throws Exception
     */
    public Map<String, String> getAllEntries(String path, String password) throws Exception{
        var passwordStore = KeyStore.getInstance("JCEKS");
        var results = new HashMap<String, String>();
        try (var fis = new FileInputStream(path)) {
            passwordStore.load(fis, password.toCharArray());
            for (var aliases =  passwordStore.aliases(); aliases.hasMoreElements();) {
                var alias = aliases.nextElement();
                try {
                    var encodedValue = new String(passwordStore.getKey(alias, password.toCharArray()).getEncoded());
                    results.put(alias, encodedValue);
                } catch( UnrecoverableKeyException e) {
                    results.put(alias, "ERROR: Different Password than Password Store");
                    log.info("different password for key {} than for the password store - value cannot be retreived", alias);
                }
            }
            return results;
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
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
        var passwordStore = KeyStore.getInstance("JCEKS");

        try (var fis = new FileInputStream(path)) {
            passwordStore.load(fis, password.toCharArray());
            passwordStore.deleteEntry(key);

            var  pwdProtection =new KeyStore.PasswordProtection(password.toCharArray());
            var factory = SecretKeyFactory.getInstance("PBE");
            var generatedSecret =
                    factory.generateSecret(new PBEKeySpec(
                            updateValue.toCharArray()));
            passwordStore.setEntry(key, new KeyStore.SecretKeyEntry(generatedSecret), pwdProtection);

            var fos = new FileOutputStream(path);
            passwordStore.store(fos, password.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param path path to entry to delete
     * @param key
     * @param password
     * @throws Exception
     */
    public void deleteEntry(String path, String key, String password) throws Exception{
        var passwordStore = KeyStore.getInstance("JCEKS");

        try (var fis = new FileInputStream(path)) {
            passwordStore.load(fis, password.toCharArray());
            passwordStore.deleteEntry(key);
            var fos = new FileOutputStream(path);
            passwordStore.store(fos, password.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String path, String key, String valueToStore, String password) throws Exception {
        var passwordStore = KeyStore.getInstance("JCEKS");

        try (var fis = new FileInputStream(path)) {
            passwordStore.load(fis, password.toCharArray());

            var  pwdProtection = new KeyStore.PasswordProtection(password.toCharArray());
            var factory = SecretKeyFactory.getInstance("PBE");
            var generatedSecret =
                    factory.generateSecret(new PBEKeySpec(
                            valueToStore.toCharArray()));
            passwordStore.setEntry(key, new KeyStore.SecretKeyEntry(generatedSecret), pwdProtection);

            var fos = new FileOutputStream(path);
            passwordStore.store(fos, password.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

}
