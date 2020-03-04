package com.xpay.web.pms.controller.system;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.config.entity.DataDictionary;
import com.xpay.facade.config.service.DataDictionaryFacade;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PmsDataDictionaryVO;
import com.xpay.web.pms.vo.system.DataDictionaryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PmsDataDictionaryController
 *
 * @author longfenghua
 * @date 2019/11/14
 */
@RestController
@RequestMapping("dataDictionary")
public class DataDictionaryController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference
    private DataDictionaryFacade dataDictionaryFacade;


    @Permission("config:dictionary:view")
    @RequestMapping("listDataDictionary")
    public RestResult<PageResult<List<DataDictionary>>> listDataDictionary(@RequestParam int pageCurrent,
                                                                           @RequestParam int pageSize,
                                                                           @RequestBody(required = false) PmsDataDictionaryVO pmsDataDictionaryVO) {
        try {
            Map<String, Object> paramMap = BeanUtil.toMap(pmsDataDictionaryVO);
            PageResult<List<DataDictionary>> pageResult = dataDictionaryFacade.listDataDictionaryPage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
            return RestResult.success(pageResult);
        } catch (Exception e) {
            logger.error("== dataDictionaryList exception:", e);
            return RestResult.error("获取数据字典失败");
        }
    }

    @Permission("config:dictionary:view")
    @RequestMapping("getDataDictionaryVOById")
    public RestResult<DataDictionaryVO> getDataDictionaryVOById(@RequestParam long id) {
        try {
            DataDictionary dataDictionary = dataDictionaryFacade.getDataDictionaryById(id);
            return RestResult.success(new DataDictionaryVO(dataDictionary));
        } catch (Exception e) {
            logger.error("== getDataDictionaryVOById exception:", e);
            return RestResult.error("获取数据字典失败");
        }
    }

    @Permission("config:dictionary:delete")
    @RequestMapping("deleteDataDictionary")
    public RestResult<String> deleteDataDictionary(@RequestParam Long id) {
        try {
            DataDictionary dataDictionary = dataDictionaryFacade.getDataDictionaryById(id);
            if (dataDictionary == null) {
                return RestResult.error("数据字典不存在");
            }
            dataDictionaryFacade.deleteDataDictionaryById(id);
            super.logDelete("删除数据字典，名称:" + dataDictionary.getDataName(), true);
            return RestResult.success("删除成功");
        } catch (Exception e) {
            logger.error("== dataDictionaryDelete exception:", e);
            super.logDelete("删除异常:" + e.getMessage(), false);
            return RestResult.error("删除失败");
        }
    }

    @Permission("config:dictionary:add")
    @RequestMapping("addDataDictionary")
    public RestResult<String> addDataDictionary(@CurrentUser PmsOperator currentOperator, @RequestBody DataDictionaryVO dataDictionaryVO) {
        try {
            if (StringUtil.isEmpty(dataDictionaryVO.getDataName())) {
                return RestResult.error("dataName不能为空");
            } else if (dataDictionaryFacade.getDataDictionaryByName(dataDictionaryVO.getDataName().trim()) != null) {
                return RestResult.error("dataName已存在");
            } else if (dataDictionaryVO.getDataInfo().stream().anyMatch(p -> StringUtil.isEmpty(p.getFlag()) || StringUtil.isEmpty(p.getCode()) || StringUtil.isEmpty(p.getDesc()))) {
                return RestResult.error("数据标识、数据编码和数据描述都不能为空");
            } else if (dataDictionaryVO.getDataInfo().stream().collect(Collectors.groupingBy(DataDictionaryVO.Item::getFlag)).size() != dataDictionaryVO.getDataInfo().size()) {
                return RestResult.error("数据标识存在重复");
            } else if (dataDictionaryVO.getDataInfo().stream().collect(Collectors.groupingBy(DataDictionaryVO.Item::getCode)).size() != dataDictionaryVO.getDataInfo().size()) {
                return RestResult.error("数据编码存在重复");
            }

            DataDictionary dataDictionary = new DataDictionary();
            dataDictionary.setCreator(currentOperator.getLoginName());
            dataDictionary.setDataName(dataDictionaryVO.getDataName().trim());
            dataDictionary.setDataInfo(JsonUtil.toString(dataDictionaryVO.getDataInfo()));
            dataDictionary.setRemark(dataDictionaryVO.getRemark());
            dataDictionary.setModifyTime(new Date());
            dataDictionary.setModifyOperator(currentOperator.getLoginName());
            dataDictionary.setSystemType(dataDictionaryVO.getSystemType());
            dataDictionaryFacade.createDataDictionary(dataDictionary);
            super.logEdit("新增数据字典" + dataDictionaryVO.getDataName().trim(), true);
            return RestResult.success("添加成功");
        } catch (Exception e) {
            logger.error("== dataDictionaryAdd exception:", e);
            return RestResult.success("新增失败");
        }
    }

    @Permission("config:dictionary:edit")
    @RequestMapping("editDataDictionary")
    public RestResult<String> editDataDictionary(@CurrentUser PmsOperator currentOperator, @RequestBody DataDictionaryVO dataDictionaryVO) {
        try {
            if (dataDictionaryVO.getDataInfo() == null) {
                dataDictionaryVO.setDataInfo(Collections.emptyList());
            }

            DataDictionary dataDictionaryTemp = dataDictionaryFacade.getDataDictionaryById(dataDictionaryVO.getId());
            if (dataDictionaryTemp == null) {
                return RestResult.success("数据字典不存在");
            } else if (dataDictionaryVO.getDataInfo().stream().anyMatch(p -> StringUtil.isEmpty(p.getFlag()) || StringUtil.isEmpty(p.getCode()) || StringUtil.isEmpty(p.getDesc()))) {
                return RestResult.error("数据标识、数据编码和数据描述都不能为空");
            } else if (dataDictionaryVO.getDataInfo().stream().collect(Collectors.groupingBy(DataDictionaryVO.Item::getFlag)).size() != dataDictionaryVO.getDataInfo().size()) {
                return RestResult.error("数据标识存在重复");
            } else if (dataDictionaryVO.getDataInfo().stream().collect(Collectors.groupingBy(DataDictionaryVO.Item::getCode)).size() != dataDictionaryVO.getDataInfo().size()) {
                return RestResult.error("数据编码存在重复");
            }
            dataDictionaryTemp.setDataInfo(JsonUtil.toString(dataDictionaryVO.getDataInfo()));
            dataDictionaryTemp.setRemark(dataDictionaryVO.getRemark());
            dataDictionaryTemp.setSystemType(dataDictionaryVO.getSystemType());
            dataDictionaryTemp.setModifyOperator(currentOperator.getLoginName());
            dataDictionaryTemp.setModifyTime(new Date());
            dataDictionaryFacade.updateDataDictionary(dataDictionaryTemp);
            super.logEdit("修改数据字典" + dataDictionaryVO.getDataName(), true);
            return RestResult.success("修改成功");
        } catch (Exception e) {
            logger.error("== dataDictionaryEdit exception:", e);
            return RestResult.success("修改失败");
        }
    }

    /**
     * 该接口用于用户运营后台后获取，不添加权限判断
     *
     * @return .
     */
    @RequestMapping("listAllDataDictionaryVO")
    public RestResult<List<DataDictionaryVO>> listAllDataDictionaryVO() {
        List<DataDictionaryVO> list = dataDictionaryFacade.listAllDataDictionary().stream().map(DataDictionaryVO::new).collect(Collectors.toList());
        return RestResult.success(list);
    }
}
