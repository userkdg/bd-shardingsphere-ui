package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import com.baomidou.mybatisplus.annotation.DbType;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.List;
import java.util.Map;
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

    public abstract String createPlainBakFieldSql(FiledEncryptionInfo info);

    public abstract String deletePlainFieldData(FiledEncryptionInfo info);

    public abstract String deletePlainFieldSql(FiledEncryptionInfo inf);

    public abstract String changePlainFieldNullable(FiledEncryptionInfo info);

    public abstract List<String> createCipherIndexes(Map<QueryMetaDataRequest, List<TableInfoVO>> dataSourceMetadata, List<ColumnInfoVO> fieldList, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors);

    public abstract String changeCipherFieldSqlNotNull(FiledEncryptionInfo info);
}
