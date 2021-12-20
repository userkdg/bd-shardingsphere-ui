package org.apache.shardingsphere.ui.util.sql;

import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.Properties;

public abstract class FieldFactory {

    static {
        ShardingSphereServiceLoader.register(EncryptAlgorithm.class);
    }

    public static MysqlFieldFactory mysqlFieldFactory(){
        return new MysqlFieldFactory();
    }

    public abstract Integer getFieldLength(String algorithmType, Properties props, String length);

    public abstract String createFieldSql(FiledEncryptionInfo info);

}
