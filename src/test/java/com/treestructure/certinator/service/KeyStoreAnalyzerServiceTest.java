package com.treestructure.certinator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class KeyStoreAnalyzerServiceTest {

    @InjectMocks
    KeyStoreAnalyzerService service;

    @Test
    void analyzeKeyStore() {
        var results = service.getAllCerts("src/test/resources/testTruststores/trustStore.jceks", "opra_api");
    }
}
