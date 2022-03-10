package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

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

}
