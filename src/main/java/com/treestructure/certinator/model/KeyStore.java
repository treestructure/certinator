package com.treestructure.certinator.model;


import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class KeyStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String serverPath;
    private String gitPath;
    private String password;
    private KeyStoreType keyStoreType;


    @ManyToOne
    @JoinColumn(name="environment_id", referencedColumnName="id", nullable = false)
    private Environment environment;

    /**
     * checks if repository and server version are equal
     * @return
     */
    public boolean checkForEquality(KeyStoreAnalyzerService analyzerService) {
        if (StringUtils.isEmpty(gitPath) || StringUtils.isEmpty(serverPath)) {
            return false;
        }
        var serverResults = analyzerService.getAllCerts(serverPath, password);
        var gitResults = analyzerService.getAllCerts(gitPath, password);


        return serverResults.entrySet().stream().allMatch(entry -> {
            var certToCompare = gitResults.get(entry.getKey());
            if (certToCompare == null) return false;

            return certToCompare.getSerialNumber().equals(entry.getValue().getSerialNumber());
        });
    }


    public boolean containsOutdatedCertificates(KeyStoreAnalyzerService analyzerService) {
        return analyzerService.hasOutdatedCertificates();
    }


}
