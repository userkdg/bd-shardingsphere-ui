package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.common.enums.IndexTypeEnum;
import cn.com.bluemoon.metadata.common.enums.JudgeEnum;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.FieldInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SqlIndexInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmFactory;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlFieldFactory extends FieldFactory {

    public static final List<String> charList = Arrays.asList("char", "varchar", "varbinary", "binary", "text", "tinytext", "mediumtext", "longtext");
    public static final List<String> intList = Arrays.asList("int", "bigint", "tinyint", "smallint", "mediumint", "float", "double", "decimal");

    @Override
    public Integer getFieldLength(String algorithmType, Properties props, String length) {

        EncryptAlgorithm algorithm = ShardingSphereAlgorithmFactory.createAlgorithm
                (new ShardingSphereAlgorithmConfiguration(algorithmType, props), EncryptAlgorithm.class);
        String join = StrUtil.repeat("中", Integer.parseInt(length));
        return algorithm.encrypt(join).toString().length();
    }

    @Override
    public String createCipherFieldSql(FiledEncryptionInfo info) {

        String fieldSql = null;
        ColumnInfoVO vo = info.getColumnInfoVO();
        // 获取密文字段的长度
        if (charList.contains(vo.getSqlSimpleType())) {
            String fieldLength = StringUtils.isNotBlank(vo.getLength()) ? String.format("(%s)", getFieldLength(info.algorithmType, info.props, vo.getLength())) : "";
            // 字段脚本
            fieldSql = String.format("alter table %s add %s_cipher %s%s comment '%s' after %s;",
                    vo.getTableName(), vo.getName(), vo.getSqlSimpleType(), fieldLength, vo.getComment(), vo.getName());
        } else if (intList.contains(vo.getSqlSimpleType())) {
            fieldSql = String.format("alter table %s add %s_cipher %s(%s) comment '%s' after %s;",
                    vo.getTableName(), vo.getName(), "varchar", "512", vo.getComment(), vo.getName());
        } else {
            String testFormat = "alter table %s add %s_cipher %s comment '%s' after %s;";
            fieldSql = String.format(testFormat, vo.getTableName(), vo.getName(), "text", vo.getComment(), vo.getName());
        }
        return fieldSql;
    }

    @Override
    public String renamePlainFieldSql(ColumnInfoVO vo) {
        return renamePlainFieldSql(vo, false);
    }

    private String renamePlainFieldSql(ColumnInfoVO vo, boolean forceFieldNull) {
        // alter table <表名> change <字段名> <字段新名称> <字段的类型>。
        String length = StringUtils.isBlank(vo.getLength()) ? "" : "(" + vo.getLength() + ")";
        StringBuilder sb = new StringBuilder();
        String targetFieldName = vo.getName().endsWith("_plain") ? vo.getName() : vo.getName() + "_plain ";
        sb.append("alter table ").append(vo.getTableName())
                .append(" change ").append(vo.getName())
                .append(" ").append(targetFieldName).append(" ")
                .append(vo.getSqlSimpleType()).append(length);
        if (vo.getColumnDefault() != null) {
            String defVal;
            if ("".equals(vo.getColumnDefault())) {
                defVal = "''";
            } else if (intList.contains(vo.getSqlSimpleType())) {
                defVal = vo.getColumnDefault();
            } else {
                defVal = String.format("'%s'", vo.getColumnDefault());
            }
            sb.append(" default ").append(defVal);
        }
        if (JudgeEnum.NO.equals(vo.getIsNullable()) && !forceFieldNull) {
            // 2022/5/6 备份列生成后，要把非空，都改为可空，原因：该字段已无效，不应影响业务正常插入数据。
            sb.append(" not null ");
        } else {
            sb.append(" null ");
        }
        if (vo.getComment() != null) {
            sb.append(" comment '").append(vo.getComment()).append("';");
        }
//        String rename = String.format("alter table %s change %s %s_plain %s%s default null comment '%s';",
//                vo.getTableName(), vo.getName(), vo.getName(), vo.getSqlSimpleType(), length, vo.getComment());
        return sb.toString();
    }

    private String changeFieldSqlNotNull(ColumnInfoVO vo, boolean forceFieldNull) {
        // alter table <表名> change <字段名> <字段新名称> <字段的类型>。
        String length = StringUtils.isBlank(vo.getLength()) ? "" : "(" + vo.getLength() + ")";
        StringBuilder sb = new StringBuilder();
        sb.append("alter table ").append(vo.getTableName())
                .append(" change ").append(vo.getName())
                .append(" ").append(vo.getName()).append(" ")
                .append(vo.getSqlSimpleType()).append(length);
        if (vo.getColumnDefault() != null) {
            String defVal;
            if ("".equals(vo.getColumnDefault())) {
                defVal = "''";
            } else if (intList.contains(vo.getSqlSimpleType())) {
                defVal = vo.getColumnDefault();
            } else {
                defVal = String.format("'%s'", vo.getColumnDefault());
            }
            sb.append(" default ").append(defVal);
        }
        if (JudgeEnum.NO.equals(vo.getIsNullable()) && !forceFieldNull) {
            // 2022/5/6 备份列生成后，要把非空，都改为可空，原因：该字段已无效，不应影响业务正常插入数据。
            sb.append(" not null ");
        } else {
            sb.append(" null ");
        }
        if (vo.getComment() != null) {
            sb.append(" comment '").append(vo.getComment()).append("';");
        }
        return sb.toString();
    }

    @Override
    public String renameCipherFieldSql(FiledEncryptionInfo info) {
        // alter table <表名> change <字段名> <字段新名称> <字段的类型>。
        String fieldSql = null;
        ColumnInfoVO vo = info.getColumnInfoVO();
        // 获取密文字段的长度
        if (charList.contains(vo.getSqlSimpleType())) {
            String fieldLength = StringUtils.isNotBlank(vo.getLength()) ? String.format("(%s)", getFieldLength(info.algorithmType, info.props, vo.getLength())) : "";
            // 字段脚本
            fieldSql = String.format("alter table %s change %s_cipher %s %s%s comment '%s';",
                    vo.getTableName(), vo.getName(), vo.getName(), vo.getSqlSimpleType(), fieldLength, vo.getComment());
        } else if (intList.contains(vo.getSqlSimpleType())) {
            fieldSql = String.format("alter table %s change %s_cipher %s %s(%s) comment '%s';",
                    vo.getTableName(), vo.getName(), vo.getName(), "varchar", "512", vo.getComment());
        } else {
            String testFormat = "alter table %s change %s_cipher %s %s comment '%s';";
            fieldSql = String.format(testFormat, vo.getTableName(), vo.getName(), vo.getName(), "text", vo.getComment());
        }
        return fieldSql;
    }

    @Override
    public String createPlainBakFieldSql(FiledEncryptionInfo info) {
        ColumnInfoVO vo = info.getColumnInfoVO();
        String length = StringUtils.isBlank(vo.getLength()) ? "" : "(" + vo.getLength() + ")";
        StringBuilder sb = new StringBuilder();
        sb.append("alter table ").append(vo.getTableName())
                .append(" add column ").append(vo.getName() + "_plain ")
                .append(vo.getSqlSimpleType()).append(length);
        if (vo.getColumnDefault() != null) {
            String defVal;
            if ("".equals(vo.getColumnDefault())) {
                defVal = "''";
            } else if (intList.contains(vo.getSqlSimpleType())) {
                defVal = vo.getColumnDefault();
            } else {
                defVal = String.format("'%s'", vo.getColumnDefault());
            }
            sb.append(" default ").append(defVal);
        }
        if (JudgeEnum.NO.equals(vo.getIsNullable())) {
            sb.append(" not null ");
        }
        if (vo.getComment() != null) {
            sb.append(" comment '").append(vo.getComment()).append("';");
        }
        return sb.toString();
    }

    /**
     * update xxx set xxx = null where 1=1
     */
    @Override
    public String deletePlainFieldData(FiledEncryptionInfo info) {
        ColumnInfoVO vo = info.getColumnInfoVO();
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(vo.getTableName())
                .append(" set ").append(vo.getName()).append("_plain").append(" = ");
        String defVal = null;
        if (vo.getColumnDefault() != null) {
            if ("".equals(vo.getColumnDefault())) {
                defVal = "''";
            } else if (intList.contains(vo.getSqlSimpleType())) {
                defVal = vo.getColumnDefault();
            } else {
                defVal = String.format("'%s'", vo.getColumnDefault());
            }
        }
        sb.append(defVal).append(" where 1=1;");
        return sb.toString();
    }

    @Override
    public String deletePlainFieldSql(FiledEncryptionInfo info) {
        ColumnInfoVO vo = info.getColumnInfoVO();
        StringBuilder sb = new StringBuilder();
        sb.append("alter table ").append(vo.getTableName())
                .append(" drop column ").append(vo.getName()).append("_plain;");
        return sb.toString();
    }

    /**
     * 备份列非空改为可空
     */
    @Override
    public String changePlainFieldNullable(FiledEncryptionInfo info) {
        ColumnInfoVO vo = info.getColumnInfoVO();
        if (JudgeEnum.YES.equals(vo.getIsNullable())) {
            return "";
        }
        ColumnInfoVO columnInfoVO = new ColumnInfoVO();
        BeanUtils.copyProperties(vo, columnInfoVO);
        if (!columnInfoVO.getName().endsWith("_plain")) {
            columnInfoVO.setName(columnInfoVO.getName() + "_plain");
        }
        return renamePlainFieldSql(columnInfoVO, true);
    }

    @Override
    public List<String> createCipherIndexes(Map<QueryMetaDataRequest, List<TableInfoVO>> dataSourceMetadata, List<ColumnInfoVO> needCipherFields, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors) {
        List<TableInfoVO> tableInfoVOS = dataSourceMetadata.values().stream().findFirst().orElse(Collections.emptyList());
        Map<String, List<SqlIndexInfoVO>> tableNameAndIndexes = tableInfoVOS.stream().collect(Collectors.toMap(TableInfoVO::getName, TableInfoVO::getSqlIndexes));
        Map<String, List<ColumnInfoVO>> tableNameAndColInfo = needCipherFields.stream().collect(Collectors.groupingBy(ColumnInfoVO::getTableName));
        List<String> res = new ArrayList<>();
        for (Map.Entry<String, List<ColumnInfoVO>> entry : tableNameAndColInfo.entrySet()) {
            List<ColumnInfoVO> cipherColumns = entry.getValue();
            String tableName = entry.getKey();
            if (tableNameAndIndexes.containsKey(tableName)) {
                List<SqlIndexInfoVO> indexes = tableNameAndIndexes.getOrDefault(tableName, Collections.emptyList());
                Set<String> cipherFields = cipherColumns.stream().map(ColumnInfoVO::getName).collect(Collectors.toSet());
                List<SqlIndexInfoVO> hadCipherFieldIndexes = indexes.stream().peek(i -> {
                    List<FieldInfoVO> fieldInfoVOS = i.getFields().stream().peek(fieldInfoVO -> {
                        String f = fieldInfoVO.getName();
                        // 修正字段名。
                        if (f.endsWith("_plain")) {
                            fieldInfoVO.setName(f.substring(0, f.lastIndexOf("_plain")));
                        } else {
                            fieldInfoVO.setName(f);
                        }
                    }).collect(Collectors.toList());
                    i.setFields(fieldInfoVOS);
                }).filter(i -> {
                    List<String> metadataIndexFields = i.getFields().stream().map(f -> f.getName()).collect(Collectors.toList());
                    return metadataIndexFields.stream().anyMatch(cipherFields::contains);
                }).collect(Collectors.toList());
                if (!hadCipherFieldIndexes.isEmpty()) {
                    List<String> indexDdl = hadCipherFieldIndexes.stream().map(i -> {
                        return String.format("alter table %s add %s index %s(%s);",
                                tableName, IndexTypeEnum.NORMAL.equals(i.getIdxType()) ? "" : i.getIdxType(),
                                i.getName()+"_kms", i.getFields().stream().map(f -> {
                                    String filedName = f.getName();
                                    if (f.getSubPart() != null) {
                                        return String.format("%s(%d)", filedName, f.getSubPart());
                                    }
                                    return filedName;
                                }).map(f -> {
                                    if (cipherFields.contains(f) && !f.endsWith("_cipher")) {
                                        return f + "_cipher";
                                    }
                                    return f;
                                }).collect(Collectors.joining(", ")));
                    }).collect(Collectors.toList());
                    res.addAll(indexDdl);
                }
            }
        }
        return res;
    }

    @Override
    public String changeCipherFieldSqlNotNull(FiledEncryptionInfo info) {
        ColumnInfoVO vo = info.getColumnInfoVO();
        if (vo.getIsNullable().equals(JudgeEnum.YES)) {
            return "";
        }
        return changeFieldSqlNotNull(vo, false);
    }
}
