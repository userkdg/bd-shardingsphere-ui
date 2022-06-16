package org.apache.shardingsphere.ui.web.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmFactory;
import org.apache.shardingsphere.infra.exception.ShardingSphereException;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.config.BmKMSConfig;
import org.apache.shardingsphere.ui.config.BmKMSConfig.AlgorithmType;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jarod.Kong
 */
@RestController
@RequestMapping("/api/kms-center")
@Slf4j
public class KMSSecureCenterController {
    static {
        ShardingSphereServiceLoader.register(EncryptAlgorithm.class);
    }

    @Autowired
    private BmKMSConfig bmKMSConfig;

    @PostConstruct
    public void init() {
        Stream.of(
                new ArrayList<>(bmKMSConfig.getAes()),
                new ArrayList<>(bmKMSConfig.getMd5()),
                new ArrayList<>(bmKMSConfig.getSm3()),
                new ArrayList<>(bmKMSConfig.getSm4()),
                new ArrayList<>(bmKMSConfig.getRc4()),
                bmKMSConfig.getMysqlAes())
                .flatMap(Collection::stream).forEach(this::checkAlgorithmConfig);
    }

    @RequestMapping(value = "/pull", method = RequestMethod.GET)
    public ResponseResult<String> pull(@RequestParam("sys") String sys,
                                       @RequestParam(value = "type", required = false, defaultValue = "aes") String type) {
        AlgorithmType res = getAgloTypeFactory(sys, type);
        checkAlgorithmConfig(res);
        String result = Base64.getEncoder().encodeToString(JSON.toJSONBytes(res));
        log.info("return result:{}", result);
        return ResponseResult.ok(result);
    }

    private AlgorithmType getAgloTypeFactory(String sys, String type) {
        AlgorithmType res;
        if (type.equalsIgnoreCase("aes")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getAes()), type, sys);
        } else if (type.equalsIgnoreCase("mysql-aes")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getMysqlAes()), type, sys);
        } else if (type.equalsIgnoreCase("sm3")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getSm3()), type, sys);
        } else if (type.equalsIgnoreCase("sm4")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getSm4()), type, sys);
        } else if (type.equalsIgnoreCase("rc4")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getRc4()), type, sys);
        } else if (type.equalsIgnoreCase("md5")) {
            res = getAgloType(Optional.ofNullable(bmKMSConfig.getMd5()), type, sys);
        } else {
            throw new ShardingSphereUIException(500, "参数type=" + type + "不支持");
        }
        if (res == null) {
            throw new ShardingSphereUIException(500, "参数sys=" + sys + ",type=" + type + "，KMS中心未配置");
        }
        return res;
    }

    private AlgorithmType getAgloType(Optional<List<AlgorithmType>> agloTypes, String type, String sys) {
        return agloTypes.flatMap(list -> list.stream().filter(l -> l.getSys().equalsIgnoreCase(sys)).peek(t -> t.setType(type)).findFirst()).orElse(null);
    }

    private void checkAlgorithmConfig(AlgorithmType algorithm) {
        boolean ok = false;
        try {
            Properties props = new Properties();
            props.putAll(JSON.parseObject(algorithm.getKey(), Map.class));
            ShardingSphereAlgorithm res = ShardingSphereAlgorithmFactory.createAlgorithm(new ShardingSphereAlgorithmConfiguration(algorithm.getType().toUpperCase(), props), EncryptAlgorithm.class);
            ok = res != null;
        } catch (Exception e) {
            throw new ShardingSphereException("检查KMS系统算法配置异常", e);
        } finally {
            log.info("启动检查KMS配置情况：{},status:{}", algorithm, ok);
        }
    }

}
