package org.apache.shardingsphere.ui.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveShuffleInfo;
import org.apache.shardingsphere.ui.mapper.DsSySensitiveShuffleInfoMapper;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveShuffleInfoService;
import org.springframework.stereotype.Service;

/**
 * @author Jarod.Kong
 */
@Service
@Slf4j
public class DsSySensitiveShuffleInfoServiceImpl
        extends ServiceImpl<DsSySensitiveShuffleInfoMapper, DsSysSensitiveShuffleInfo>
        implements DsSySensitiveShuffleInfoService {

}
