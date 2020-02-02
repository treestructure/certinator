package com.treestructure.certinator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="truststorebuilder")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CertinatorConfigProperties {

    private String truststorePath;
    private String defaultTrustStorePassword;
    private String certificateInputPath;
}
