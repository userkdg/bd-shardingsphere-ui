package org.apache.shardingsphere.ui.common.domain;

import lombok.Data;

@Data
public class SensitiveInformation {


    private String databaseName;

    private String schemaName = "ec_order_db";

    private String tableName;

    private String fieldName;

    private String datatype;

    private String algorithmType = "AES";

    private String cipherKey;

}
