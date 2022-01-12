package org.apache.shardingsphere.ui.util.jdbc;

import cn.com.bluemoon.daps.common.enums.DatabaseType;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.hutool.core.util.RandomUtil;
import com.sun.org.apache.regexp.internal.RE;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class ConnectionProxyUtils {

    public static final String MYSQL = "jdbc:mysql://";
    public static final String PGSQL = "jdbc:postgresql://";
    public static String PG_DRIVER = "org.postgresql.Driver";

    public static ResponseResult<String> connectionDatabase(QueryMetaDataRequest request, List<String> list){
        String url = String.format("jdbc:mysql://%s:%s/%s", request.getIp(),request.getPort(), request.getDbName());
        Connection connection = null;
        Statement  st = null;
        try {
            connection = DriverManager.getConnection(url,request.getUsername(), request.getPassword());
            st = connection.createStatement();
            for (String sql : list) {
                st.addBatch(sql);
            }
            int[] nums = st.executeBatch();
            long count = Arrays.stream(nums).filter(n -> n >= 0).count();
            // todo:记录日志，成功条数以及失败条数
            log.info("共{}条数据，执行成功{}", list.size(), count);
        } catch (SQLException  e) {
            return ResponseResult.error(e.getMessage());
        } finally {
            try {
                if(st != null){
                    st.close();
                }
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ResponseResult.ok("执行成功");
    }

    public static Boolean connectTest(DapSystemDatasourceEnvironment environment){

        try {
            if(environment.getDbType().equals(DatabaseType.PGSQL)){
                Class.forName(PG_DRIVER);
            }
            Connection connection = DriverManager.getConnection(getUrl(environment),environment.getUsername(),environment.getPassword());
            connection.close();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    public static String getUrl(DapSystemDatasourceEnvironment environment){

        String mysqlSuffix = "?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useSSL=false";
        DatabaseType databaseType = environment.getDbType();
        String prefixUrl = databaseType.equals(DatabaseType.MYSQL) ? MYSQL : PGSQL;
        String dbName = StringUtils.isBlank(environment.getDatabaseName()) ? "" : "/"+ environment.getDatabaseName();
        String schema = StringUtils.isBlank(environment.getDatabaseSchema()) ? "" : String.format("?searchpath=%s", environment.getDatabaseSchema());
        String url = prefixUrl + environment.getHost()+":"+environment.getPort() + dbName + (databaseType.equals(DatabaseType.MYSQL) ? mysqlSuffix : schema);
        return url;

    }

    public static void getRequest(String url,QueryMetaDataRequest queryMetaDataRequest){

        String[] split = url.split("\\?");
        String simpleUrl = split[0];
        String[] simpleUrlSplit = simpleUrl.split("://");
        String jdbc = simpleUrlSplit[0];
        String[] jdbcSplit = jdbc.split(":");
        String dbType = jdbcSplit[jdbcSplit.length - 1];
        String urlPortDbname = simpleUrlSplit[simpleUrlSplit.length - 1];
        String[] urlPortDbnameSplit = urlPortDbname.split("/");
        String dbname = urlPortDbnameSplit[urlPortDbnameSplit.length - 1];
        String[] urlAndPort = urlPortDbnameSplit[0].split(":");
        String ip = urlAndPort[0];
        String port = urlAndPort[urlAndPort.length-1];
        queryMetaDataRequest.setIp(ip);
        queryMetaDataRequest.setPort(port);
        queryMetaDataRequest.setDbName(dbname);
        queryMetaDataRequest.setSchemaName(dbname);
        queryMetaDataRequest.setDbType(dbType.equals("mysql") ? DbTypeEnum.MYSQL : DbTypeEnum.PGSQL);
    }

    public static Map<String, DataSourceConfiguration> transToDatasourceString(List<DapSystemDatasourceEnvironment> list){

        // 生成
        Map<String, DataSourceConfiguration> dataSourceConfigurations = new HashMap<>();
        for (DapSystemDatasourceEnvironment environment:list) {
            DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration("com.zaxxer.hikari.HikariDataSource");
            Properties customPoolProps = dataSourceConfiguration.getCustomPoolProps();
            String url = ConnectionProxyUtils.getUrl(environment);
            customPoolProps.setProperty("connectionTimeoutMilliseconds", "30000");
            customPoolProps.setProperty("idleTimeoutMilliseconds", "60000");
            customPoolProps.setProperty("maxPoolSize", "50");
            customPoolProps.setProperty("minPoolSize", "1");
            customPoolProps.setProperty("maxLifetimeMilliseconds", "1800000");
            Map<String, Object> props = dataSourceConfiguration.getProps();
            props.put("customPoolProps", customPoolProps);
            props.put("readOnly", false);
            props.put("maxLifetime", 1800000);
            props.put("minimumIdle", 1);
            props.put("password", environment.getPassword());
            props.put("minPoolSize", 1);
            props.put("idleTimeout", 60000);
            props.put("jdbcUrl",url);
            props.put("dataSourceClassName", dataSourceConfiguration.getDataSourceClassName());
            props.put("maximumPoolSize", 50);
            props.put("connectionTimeout", 30000);
            props.put("maxPoolSize", 50);
            props.put("username", environment.getUsername());
            dataSourceConfigurations.put(environment.getDatabaseName()+"_"+ RandomUtil.randomString(3), dataSourceConfiguration);
        }
        return dataSourceConfigurations;
    }
}
