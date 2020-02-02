package com.treestructure.certinator.service;

import com.treestructure.certinator.CertinatorConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TruststorebuilderService {

    @Autowired
    CertinatorConfigProperties config;


    /**
     *
     * @throws IOException
     * @throws KeyStoreException
     */
    public void buildTrustStoreFromPath() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);

        // check all files in given path  for beeing a certificate and add them to the
        // trust store
        try (Stream<Path> stream = Files.walk(Paths.get(config.getCertificateInputPath()), 10)) {
            stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(path -> {
                        try {
                            CertificateFactory fact = CertificateFactory.getInstance("X.509");
                            FileInputStream certInputStream = new FileInputStream(path.toFile());
                            log.info("adding certificate {} to Truststore", path.getFileName());
                            return (X509Certificate) fact.generateCertificate(certInputStream);
                        } catch (FileNotFoundException | CertificateException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }).filter(Objects::nonNull)
                    .peek(cert -> {
                        try {
                            trustStore.setCertificateEntry(cert.getSerialNumber().toString(), cert);
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        }
                    }).collect(Collectors.toList());


                    try (FileOutputStream outputStream = new FileOutputStream(config.getTruststorePath())){
                        trustStore.store(outputStream, config.getDefaultTrustStorePassword().toCharArray());
                    } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
                        e.printStackTrace();
                    }

        }




    }


}
