package com.xpay.facade.config.service;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.config.entity.DataDictionary;

import java.util.List;
import java.util.Map;

/**
 * DataDictionaryFacade
 *
 * @author longfenghua
 * @date 2019/11/14
 */
public interface DataDictionaryFacade {

    DataDictionary getDataDictionaryById(long id);

    void deleteDataDictionaryById(long id);

    void createDataDictionary(DataDictionary dataDictionary);

    void updateDataDictionary(DataDictionary dataDictionary);

    PageResult<List<DataDictionary>> listDataDictionaryPage(Map<String, Object> paramMap, PageParam pageParam);

    List<DataDictionary> listAllDataDictionary();

    DataDictionary getDataDictionaryByName(String dataName);

}
