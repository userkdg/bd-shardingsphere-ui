package org.apache.shardingsphere.ui.common.domain;

import lombok.Data;
import org.apache.shardingsphere.infra.database.type.DatabaseType;

@Data
public class DatasourceInfo {

    private String datasourceId;

    private Integer environment;

    private String host;

    private String password;

    private Integer port;

    private String databaseName;

    private String databaseSchema;

    private String username;

    private String databaseVersion;

    private Boolean sslConnect;

    private String sslCertificate;

    private Boolean isInfoResource;

    private Integer dbType;


}
