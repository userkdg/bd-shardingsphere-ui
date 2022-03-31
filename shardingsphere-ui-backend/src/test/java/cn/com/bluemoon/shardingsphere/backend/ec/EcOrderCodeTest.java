package cn.com.bluemoon.shardingsphere.backend.ec;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jarod.Kong
 */
public class EcOrderCodeTest {

    @Test
    public void testKey() {
        BigDecimal r = new BigDecimal("1021190030000000000000000");
        BigDecimal p = new BigDecimal("1020190000000000000000000");
        BigDecimal sub = r.subtract(p);
        BigDecimal per = sub.divide(new BigDecimal("1000"));
        System.out.println(per.toPlainString());
        System.out.println(per.longValue());
        BigDecimal pre = p, curr = p;
        List<String> partitions = new ArrayList<>();
        String first = String.format("order_code < %s", p);
        System.out.println(first);
        partitions.add(first);
        while (((curr = pre.add(per))).compareTo(r) < 0){
            String part = String.format("order_code < %s and order_code >=%s", curr.toPlainString(), pre);
            System.out.println(part);
            partitions.add(part);
            pre = curr;
        }
        String part = String.format("order_code >=%s", pre);
        System.out.println(part);
        partitions.add(part);
        System.out.println(partitions.size());
    }
}
