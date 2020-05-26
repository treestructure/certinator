package com.treestructure.certinator.service;

import com.treestructure.certinator.CertinatorConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
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
import java.util.ArrayList;
import java.util.List;
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
    public List<String> buildTrustStoreFromPath() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);

        var resultLog = new ArrayList<String>();

        // check all files in given path  for beeing a certificate and add them to the
        // trust store
        try (Stream<Path> stream = Files.walk(Paths.get(config.getCertificateInputPath()), config.getDirectoryDepth())) {
            stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(path -> {
                        try {
                            CertificateFactory fact = CertificateFactory.getInstance("X.509");
                            FileInputStream certInputStream = new FileInputStream(path.toFile());
                            log.info("adding certificate {} to Truststore", path.getFileName());
                            resultLog.add(String.format("added certificate %s to Truststore", path.getFileName()));
                            return (X509Certificate) fact.generateCertificate(certInputStream);
                        } catch (FileNotFoundException | CertificateException e) {
                            log.info("Path {} is ignored as it is not a valid X.509 certificate", path.getFileName());
                            resultLog.add(String.format("File %s was ignored as it is not a valid X.509 certificate", path.getFileName()));
                            return null;
                        }

                    }).filter(Objects::nonNull)
                    .peek(cert -> {
                        try {
                            var issuer = new LdapName(cert.getIssuerDN().toString());

                            var newCertKey = issuer.getRdns().stream()
                                    .filter(r -> r.getType().equals("CN"))
                                    .findFirst()
                                    .map(rdn -> rdn.getValue().toString())
                                    .map(name -> name.replace(" ", "_") + "::")
                                    .orElse("");

                            newCertKey += cert.getSerialNumber().toString();
                            trustStore.setCertificateEntry(newCertKey, cert);
                        } catch (KeyStoreException | InvalidNameException e) {
                            e.printStackTrace();
                        }
                    }).collect(Collectors.toList());


                    try (FileOutputStream outputStream = new FileOutputStream(config.getTruststorePath())){
                        trustStore.store(outputStream, config.getDefaultTrustStorePassword().toCharArray());
                    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
                        e.printStackTrace();
                    }

        }


        return resultLog;


    }


}
