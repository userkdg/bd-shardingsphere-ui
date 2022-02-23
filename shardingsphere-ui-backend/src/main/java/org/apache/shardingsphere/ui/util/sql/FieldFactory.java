package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import com.baomidou.mybatisplus.annotation.DbType;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.Properties;

public abstract class FieldFactory {

    static {
        ShardingSphereServiceLoader.register(EncryptAlgorithm.class);
    }

    public static FieldFactory mysqlFieldFactory(DbType dbType){

        if(dbType.equals(DbType.MYSQL)){
            return new MysqlFieldFactory();
        }else {
            return new PgsqlFieldFactory();
        }
    }

    public abstract Integer getFieldLength(String algorithmType, Properties props, String length);

    public abstract String createCipherFieldSql(FiledEncryptionInfo info);

    public abstract String renamePlainFieldSql(ColumnInfoVO columnInfoVO);

    public abstract String renameCipherFieldSql(FiledEncryptionInfo info);
}
