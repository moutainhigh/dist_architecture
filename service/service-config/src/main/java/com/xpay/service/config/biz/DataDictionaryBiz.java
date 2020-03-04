package com.xpay.service.config.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.config.entity.DataDictionary;
import com.xpay.service.config.dao.DataDictionaryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DataDictionaryBiz
 *
 * @author longfenghua
 * @date 2019/11/14
 */
@Service
public class DataDictionaryBiz {
    @Autowired
    private DataDictionaryDao dataDictionaryDao;

    public DataDictionary getDataDictionaryById(Long id) {
        return dataDictionaryDao.getById(id);
    }

    public void deleteDataDictionaryById(long id) {
        dataDictionaryDao.deleteById(id);
    }

    public void updateDataDictionary(DataDictionary dataDictionary) {
        dataDictionaryDao.update(dataDictionary);
    }

    public void createDataDictionary(DataDictionary dataDictionary) {
        dataDictionaryDao.insert(dataDictionary);
    }

    public PageResult<List<DataDictionary>> listDataDictionaryPage(Map<String, Object> paramMap, PageParam pageParam) {
        return dataDictionaryDao.listPage(paramMap, pageParam);
    }

    public List<DataDictionary> listAllDataDictionary() {
        return dataDictionaryDao.listAll();
    }

    public DataDictionary getDataDictionaryByName(String dataName) {
        if (StringUtil.isEmpty(dataName)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "dataName不能为空");
        }
        return dataDictionaryDao.getOne(Collections.singletonMap("dataName", dataName));
    }


}
