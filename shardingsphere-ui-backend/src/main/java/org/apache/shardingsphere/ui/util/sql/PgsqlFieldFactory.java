package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PgsqlFieldFactory extends FieldFactory{


    @Override
    public Integer getFieldLength(String algorithmType, Properties props, String length) {
        return null;
    }

    @Override
    public String createCipherFieldSql(FiledEncryptionInfo info) {
        return null;
    }

    @Override
    public String renamePlainFieldSql(ColumnInfoVO columnInfoVO) {
        return null;
    }

    @Override
    public String renameCipherFieldSql(FiledEncryptionInfo info) {
        return null;
    }

    @Override
    public String createPlainBakFieldSql(FiledEncryptionInfo info) {
        return null;
    }

    @Override
    public String deletePlainFieldData(FiledEncryptionInfo info) {
        return null;
    }

    @Override
    public String deletePlainFieldSql(FiledEncryptionInfo inf) {
        return null;
    }

    @Override
    public String changePlainFieldNullable(FiledEncryptionInfo info) {
        return null;
    }

    @Override
    public List<String> createCipherIndexes(Map<QueryMetaDataRequest, List<TableInfoVO>> dataSourceMetadata, List<ColumnInfoVO> fieldList, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors) {
        return null;
    }

    @Override
    public String changeCipherFieldSqlNotNull(FiledEncryptionInfo info) {
        return null;
    }

}
