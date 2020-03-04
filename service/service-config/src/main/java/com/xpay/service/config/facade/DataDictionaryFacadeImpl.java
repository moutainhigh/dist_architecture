package com.xpay.service.config.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.config.entity.DataDictionary;
import com.xpay.facade.config.service.DataDictionaryFacade;
import com.xpay.service.config.biz.DataDictionaryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * DataDictionaryFacadeImpl
 *
 * @author longfenghua
 * @date 2019/11/14
 */
@Service
public class DataDictionaryFacadeImpl implements DataDictionaryFacade {
    @Autowired
    private DataDictionaryBiz dataDictionaryBiz;

    @Override
    public DataDictionary getDataDictionaryById(long id) {
        return dataDictionaryBiz.getDataDictionaryById(id);
    }

    @Override
    public void deleteDataDictionaryById(long id) {
        dataDictionaryBiz.deleteDataDictionaryById(id);
    }

    @Override
    public void createDataDictionary(DataDictionary dataDictionary) {
        dataDictionaryBiz.createDataDictionary(dataDictionary);
    }

    @Override
    public void updateDataDictionary(DataDictionary dataDictionary) {
        dataDictionaryBiz.updateDataDictionary(dataDictionary);
    }

    @Override
    public PageResult<List<DataDictionary>> listDataDictionaryPage(Map<String, Object> paramMap, PageParam pageParam) {
        return dataDictionaryBiz.listDataDictionaryPage(paramMap, pageParam);
    }

    @Override
    public List<DataDictionary> listAllDataDictionary() {
        return dataDictionaryBiz.listAllDataDictionary();
    }

    @Override
    public DataDictionary getDataDictionaryByName(String dataName) {
        return dataDictionaryBiz.getDataDictionaryByName(dataName);
    }
}
