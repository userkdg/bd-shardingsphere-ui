package org.apache.shardingsphere.ui.util.jdbc;

import cn.com.bluemoon.daps.common.enums.DatabaseType;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import com.sun.org.apache.regexp.internal.RE;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.sql.*;

public class ConnectionProxyUtils {

    public static final String MYSQL = "jdbc:mysql://";
    public static final String PGSQL = "jdbc:postgresql://";
    public static String PG_DRIVER = "org.postgresql.Driver";

    public static ResponseResult<String> connectionDatabase(QueryMetaDataRequest request, String sql){
        String url = String.format("jdbc:mysql://%s:%s/%s", request.getIp(),request.getPort(), request.getDbName());
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DriverManager.getConnection(url,request.getUsername(), request.getPassword());
            ps = connection.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            return ResponseResult.error(e.getMessage());
        } finally {
            try {
                if(ps != null){
                    ps.close();
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

}
