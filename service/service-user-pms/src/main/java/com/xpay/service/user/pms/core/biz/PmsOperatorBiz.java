package com.xpay.service.user.pms.core.biz;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.facade.user.pms.entity.PmsRoleOperator;
import com.xpay.service.user.pms.core.dao.PmsOperatorDao;
import com.xpay.service.user.pms.core.dao.PmsRoleOperatorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/11
 * Time: 15:53
 * Description:
 */
@Service
public class PmsOperatorBiz {
    public static final int HISTORY_PWD_MAX_COUNT = 4;


    @Autowired
    private PmsOperatorDao pmsOperatorDao;

    @Autowired
    private PmsRoleOperatorDao pmsRoleOperatorDao;

    public PmsOperator getOperatorById(Long id) {
        return pmsOperatorDao.getById(id);
    }


    public PmsOperator getOperatorByLoginName(String loginName) {
        return pmsOperatorDao.findByLoginName(loginName);
    }

    public void updateOperator(PmsOperator pmsOperator) {
        pmsOperatorDao.update(pmsOperator);
    }

    public void createOperator(PmsOperator pmsOperator) {
        pmsOperatorDao.insert(pmsOperator);
    }

    public void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd) {
        PmsOperator pmsOperator = pmsOperatorDao.getById(operatorId);
        String historyPwd = pmsOperator.getHistoryPwd();
        if (StringUtil.isNotEmpty(historyPwd)) {
            if (historyPwd.contains(newPwd)) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("新密码不能与前四次历史密码相同");
            }
            String[] historyPwdArray = historyPwd.split(",");
            if (historyPwdArray.length >= HISTORY_PWD_MAX_COUNT) {
                historyPwd = historyPwd.substring(historyPwd.indexOf(",") + 1);
            }
            historyPwd = historyPwd + "," + newPwd;
        } else {
            historyPwd = newPwd;
        }
        pmsOperator.setLoginPwd(newPwd);
        pmsOperator.setPwdErrorCount(0); // 密码错误次数重置为0
        pmsOperator.setIsChangedPwd(isChangedPwd ? PublicStatus.ACTIVE : PublicStatus.INACTIVE); // 设置密码为已修改过
        pmsOperator.setLastModPwdTime(new Date());// 最后密码修改时间
        pmsOperator.setHistoryPwd(historyPwd);// 更新历史密码
        pmsOperatorDao.update(pmsOperator);
    }

    public List<PmsOperator> listOperatorByRoleId(long roleId) {
        return pmsOperatorDao.listByRoleId(roleId);
    }

    public PageResult<List<PmsOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsOperatorDao.listPage(paramMap, pageParam);
    }

    @Transactional
    public void deleteOperatorById(long id) {
        pmsOperatorDao.deleteById(id);
        pmsRoleOperatorDao.deleteByOperatorId(id);
    }

    @Transactional
    public void updateOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds) {
        pmsOperatorDao.update(operator);
        pmsRoleOperatorDao.deleteByOperatorId(operator.getId());
        if (roleIds != null && roleIds.size() > 0) {
            List<PmsRoleOperator> roleOperatorList = roleIds.stream().map(p -> {
                PmsRoleOperator pmsRoleOperator = new PmsRoleOperator();
                pmsRoleOperator.setRoleId(p);
                pmsRoleOperator.setOperatorId(operator.getId());
                return pmsRoleOperator;
            }).collect(Collectors.toList());
            pmsRoleOperatorDao.insert(roleOperatorList);
        }
    }

    @Transactional
    public void insertOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds) {
        pmsOperatorDao.insert(operator);
        if (roleIds != null && roleIds.size() > 0) {
            List<PmsRoleOperator> roleOperatorList = roleIds.stream().map(p -> {
                PmsRoleOperator pmsRoleOperator = new PmsRoleOperator();
                pmsRoleOperator.setRoleId(p);
                pmsRoleOperator.setOperatorId(operator.getId());
                return pmsRoleOperator;
            }).collect(Collectors.toList());
            pmsRoleOperatorDao.insert(roleOperatorList);
        }
    }
}
