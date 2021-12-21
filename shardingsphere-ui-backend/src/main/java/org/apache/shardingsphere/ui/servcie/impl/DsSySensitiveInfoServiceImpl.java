package org.apache.shardingsphere.ui.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveInfo;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.mapper.DsSySensitiveInfoMapper;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DsSySensitiveInfoServiceImpl extends ServiceImpl<DsSySensitiveInfoMapper, DsSysSensitiveInfo> implements DsSySensitiveInfoService {

    @Override
    public void insertRuleConfig(List<SensitiveInformation> list) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String three_days_after = sdf.format(new Date());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<DsSysSensitiveInfo> infos = Lists.newArrayList();
        for (SensitiveInformation info : list) {
            DsSysSensitiveInfo information = new DsSysSensitiveInfo();
            BeanUtils.copyProperties(info, information);
            information.setDataType(info.getDatatype());
            information.setIncrField(info.getTableIncrField().equals("是"));
            information.setCreateTime(LocalDateTime.parse(three_days_after, df));
            infos.add(information);
        }
        this.saveBatch(infos);
    }
}
