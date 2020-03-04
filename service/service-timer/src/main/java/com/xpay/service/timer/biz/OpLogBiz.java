package com.xpay.service.timer.biz;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.timer.dto.OpLogContentDto;
import com.xpay.facade.timer.entity.OpLog;
import com.xpay.facade.timer.enums.OpTypeEnum;
import com.xpay.service.timer.config.TimerConfig;
import com.xpay.service.timer.dao.OpLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class OpLogBiz{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	TimerConfig timerConfig;
	@Autowired
	private OpLogDao opLogDao;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addAsync(String operator, String jobGroup, String jobName, OpTypeEnum opType, String logInfo){
		addAsync(operator, getObjKeyForJob(jobGroup, jobName), opType, logInfo);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addAsync(String operator, String objKey, OpTypeEnum opType, String logInfo){
		try{
			OpLogContentDto contentDto = new OpLogContentDto();
			contentDto.setOpType(opType.getValue());
			contentDto.setLogInfo(logInfo);
			addAsync(operator, objKey, contentDto);
		}catch(Throwable e){
			logger.error("保存日志异常 objKey={} operator={} msg={}", objKey, operator, e.getMessage());
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addAsync(String operator, String objKey, OpLogContentDto contentDto){
		if(timerConfig.getOpLogEnable() == false){
			return;
		}

		CompletableFuture.runAsync(() -> {
			try{
				OpLog log = new OpLog();
				log.setCreateTime(new Date());
				log.setOperator(operator);
				log.setObjKey(objKey);
				log.setContent(JsonUtil.toString(contentDto));
				opLogDao.insert(log);
			}catch(Throwable e){
				logger.error("保存日志异常 objKey={} operator={} msg={}", objKey, operator, e.getMessage());
			}
		});
	}

	public PageResult<List<OpLog>> listOpLogPage(Map<String, Object> paramMap, PageParam pageParam){
		return opLogDao.listPage(paramMap, pageParam);
	}

	private String getObjKeyForJob(String jobGroup, String jobName){
		return jobGroup + "-" + jobName;
	}
}
