package com.treestructure.certinator.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PasswordStoreServiceTest {

    @InjectMocks
    PasswordStoreService service;

    @Test
    void getAllEntries() throws Exception {
        var results = service.getAllEntries("src/test/resources/testpwdstores/demo.jceks", "pcal1256");
        log.info(results.toString());
    }

    @Test
    void getAllEntries_differentPwd() throws Exception {
        var results = service.getAllEntries("src/test/resources/testpwdstores/demodifferentpwd.jceks", "test12345");
        Assert.assertEquals(1, results.size());
    }

    @Test
    void updateEntry() throws Exception {
        service.updateEntry("src/test/resources/testpwdstores/demo.jceks", "test", "newvalue", "pcal1256");
    }

    @Test
    void deleteEntry() throws Exception {
        service.deleteEntry("src/test/resources/testpwdstores/demo.jceks", "test", "pcal1256");
    }

    @Test
    void addEntry() throws Exception {
        service.addEntry("src/test/resources/testpwdstores/demo.jceks", "demovalue", "valueFromTest", "pcal1256");
    }
}
