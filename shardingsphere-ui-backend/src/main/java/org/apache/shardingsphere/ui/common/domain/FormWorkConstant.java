package org.apache.shardingsphere.ui.common.domain;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormWorkConstant {

    public static List<SensitiveInformation> getData(){
        SensitiveInformation information = new SensitiveInformation();
        information.setDatabaseName("数据库名称");
        information.setSchemaName("模式名称");
        information.setTableName("数据库表名");
        information.setFieldName("数据库列名");
        information.setDatatype("数据库字段类型");
        information.setAlgorithmType("算法类型(默认AES)");
        information.setCipherKey("密钥Key(默认随机生成)");
        return Arrays.asList(information);
    }
}
