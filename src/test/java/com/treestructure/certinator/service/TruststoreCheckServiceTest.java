package com.treestructure.certinator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TruststoreCheckServiceTest {

    @InjectMocks
    TruststoreCheckService checkService;

    @Test
    void checkForTrust_PositiveCheck() {

        checkService.checkForTrust(
                "src/test/resources/testTruststores/trustStore.jceks",
                "https://www.deutsche-bank.de", "opra_api");
    }

    @Test
    void checkForTrust_NegativeCheck() {

        checkService.checkForTrust(
                "src/test/resources/testTruststores/minimalTrust.jks",
                "https://www.deutsche-bank.de", "test");
    }
}
