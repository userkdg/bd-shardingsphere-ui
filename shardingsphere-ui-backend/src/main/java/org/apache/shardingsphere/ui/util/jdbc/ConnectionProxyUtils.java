package org.apache.shardingsphere.ui.util.jdbc;

import cn.com.bluemoon.daps.common.enums.DatabaseType;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import com.sun.org.apache.regexp.internal.RE;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.sql.*;

public class ConnectionProxyUtils {

    private static final String MYSQL = "jdbc:mysql://";
    private static final String PGSQL = "jdbc:postgresql://";
    private static String PG_DRIVER = "org.postgresql.Driver";


    public static ResponseResult<T> connectionDatabase(){
        String url = "jdbc:mysql://192.168.243.34:23308/ec_order_db?serverTimezone=UTC&useSSL=false";
        // 查看schema
        // String sql = "show databases";
        // 查看schema下的数据源
        // String sql = "SHOW SCHEMA RESOURCES";
        // 查看规则
        // String sql = "show encypt rules"
        try {
            Connection connection = DriverManager.getConnection(url,"root", "root");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet schemas = metaData.getSchemas();
            /*Statement  statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery(sql);*/
            connection.close();
        } catch (SQLException e) {
            return ResponseResult.error(e.getMessage());
        }
        return null;
    }

    public static Boolean connectTest(DapSystemDatasourceEnvironment environment){

        DatabaseType databaseType = environment.getDbType();
        String prefixUrl = databaseType.equals(DatabaseType.MYSQL) ? MYSQL : PGSQL;
        String dbName = StringUtils.isBlank(environment.getDatabaseName()) ? "" : "/"+ environment.getDatabaseName();
        String schema = StringUtils.isBlank(environment.getDatabaseSchema()) ? "" : String.format("?searchpath=%s", environment.getDatabaseSchema());
        String url = prefixUrl + environment.getHost()+":"+environment.getPort() + dbName + (databaseType.equals(DatabaseType.MYSQL) ? "" : schema);
        //获取连接
        try {
            if(databaseType.equals(DatabaseType.PGSQL)){
                Class.forName(PG_DRIVER);
            }
            Connection connection = DriverManager.getConnection(url,environment.getUsername(),environment.getPassword());
            connection.close();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    public static void main(String[] args){

        connectionDatabase();

    }

}
