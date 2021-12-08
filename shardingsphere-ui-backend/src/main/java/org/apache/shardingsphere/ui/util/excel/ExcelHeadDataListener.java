package org.apache.shardingsphere.ui.util.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import groovy.util.logging.Slf4j;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelHeadDataListener extends AnalysisEventListener<SensitiveInformation> {
    private static final Logger log = LoggerFactory.getLogger(ExcelHeadDataListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    public List<SensitiveInformation> cachedDataList = ListUtils.newArrayList();
    // 定义错误数据集
    public List<String> errorList = ListUtils.newArrayList();

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }

    @Override
    public void invoke(SensitiveInformation data, AnalysisContext context) {

        Integer rowIndex = context.readRowHolder().getRowIndex();
        if(StringUtils.isBlank(data.getDatabaseName())){
            String error = String.format("第%s行数据库名称为空", rowIndex);
            log.info(error);
            errorList.add(error);
        } else if(StringUtils.isBlank(data.getTableName())){
            String error = String.format("第%s行数据库表名为空", rowIndex);
            log.info(error);
            errorList.add(error);
        } else if(StringUtils.isBlank(data.getFieldName())){
            String error = String.format("第%s行数据库列名为空", rowIndex);
            log.info(error);
            errorList.add(error);
        } else if(StringUtils.isBlank(data.getDatatype())){
            String error = String.format("第%s行数据库字段类型为空", rowIndex);
            log.info(error);
            errorList.add(error);
        }else {
            cachedDataList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        Map<Integer, String> map = ConverterUtils.convertToStringMap(headMap, context);
        List<String> values = (List)map.values();
        System.out.println(values);
    }
}
