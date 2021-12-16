package org.apache.shardingsphere.ui.util.sql;

import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.Properties;

public abstract class FieldFactory {

    public static MysqlFieldFactory mysqlFieldFactory(){
        return new MysqlFieldFactory();
    }

    public abstract Integer getFieldLength(String algorithmType, Properties props, String length);

    public abstract String createFieldSql(FiledEncryptionInfo info);

}
