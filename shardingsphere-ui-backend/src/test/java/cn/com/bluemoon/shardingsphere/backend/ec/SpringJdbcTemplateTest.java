//package cn.com.bluemoon.shardingsphere.backend;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.ColumnMapRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Jarod.Kong
// */
//public class SpringJdbcTemplateTest extends BaseTest{
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Test
//    public void testBean() {
//        List<Map<String, Object>> res = jdbcTemplate.query("select * from dap_system_info", new ColumnMapRowMapper());
//        System.out.println(res);
//    }
//}
