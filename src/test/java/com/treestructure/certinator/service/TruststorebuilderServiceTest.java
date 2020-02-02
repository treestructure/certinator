package com.treestructure.certinator.service;

import com.treestructure.certinator.CertinatorConfigProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@ExtendWith(MockitoExtension.class)
class TruststorebuilderServiceTest {

    @InjectMocks
    TruststorebuilderService serviceUnderTest;

    @BeforeEach
    void setUp() {
        CertinatorConfigProperties properties = CertinatorConfigProperties.builder()
                .certificateInputPath("src/test/resources/testcerts")
                .truststorePath("src/test/resources/trustStore.jceks")
                .defaultTrustStorePassword("test")
                .build();

        serviceUnderTest.config = properties;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void buildTrustStoreFromFiles() {

        try {
            serviceUnderTest.buildTrustStoreFromPath();
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
