package org.apache.shardingsphere.ui.common.dto;

import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Properties;

@Setter
@Getter
@NoArgsConstructor
public class FiledEncryptionInfo {


    public String algorithmType;

    public Properties props;

    public ColumnInfoVO columnInfoVO;

}
