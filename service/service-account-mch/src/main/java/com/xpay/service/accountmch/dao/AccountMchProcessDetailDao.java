/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountmch.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.MD5Util;
import com.xpay.facade.accountmch.dto.AccountMchProcessDetailDto;
import com.xpay.facade.accountmch.entity.AccountMchCommonUnique;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AccountMchProcessDetailDao extends MyBatisDao<AccountMchProcessDetail, Long>{
    @Autowired
    private AccountMchCommonUniqueDao accountMchCommonUniqueDao;

    public void uniqueInsert(AccountMchProcessDetail accountDetail) {
        StringBuilder sb = new StringBuilder();
        sb.append("ad_")
                .append(accountDetail.getMerchantNo())
                .append(accountDetail.getTrxNo())
                .append(accountDetail.getProcessType());

        AccountMchCommonUnique unique = new AccountMchCommonUnique();
        unique.setCreateTime(accountDetail.getCreateTime());
        unique.setUniqueKey(MD5Util.getMD5Hex(sb.toString()));
        accountMchCommonUniqueDao.insert(unique);
        super.insert(accountDetail);
    }

    /**
     * 获取扣款的账务明细
     *
     * @param merchantNo 商户编号
     * @param trxNo      平台流水号
     * @return .
     */
    public AccountMchProcessDetailDto getDebitDetailDtoByMchNoAndTrxNo(String merchantNo, String trxNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", merchantNo);
        paramMap.put("trxNo", trxNo);
        paramMap.put("processType", AccountProcessTypeEnum.DEBIT.getValue());
        return getOne("getDebitDetailDtoByMchNoAndTrxNo", paramMap);
    }


    /**
     * 查询已被确认的扣款账务明细的数目
     *
     * @param merchantNoAndTrxNoList      .
     * @param accountDebitCommitStageEnum .
     * @return .
     */
    public long countDebitCommitDetail(List<String[]> merchantNoAndTrxNoList, AccountDebitCommitStageEnum accountDebitCommitStageEnum) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNoAndTrxNoList", merchantNoAndTrxNoList.stream().map(p -> {
            Map<String, String> item = new HashMap<>(2);
            item.put("merchantNo", p[0]);
            item.put("trxNo", p[1]);
            return item;
        }).collect(Collectors.toList()));
        paramMap.put("debitCommitStage", accountDebitCommitStageEnum.getValue());
        paramMap.put("processType", AccountProcessTypeEnum.DEBIT.getValue());
        return countBy("countDebitCommitDetail", paramMap);
    }


    /**
     * 账务确认扣款
     *
     * @param merchantNoAndTrxNoList 商户号和扣款的平台流水号集合，约定每个数组【0】为商户号，【1】为平台流水号
     * @param newStage               .
     */
    public int updateDebitCommitStage(List<String[]> merchantNoAndTrxNoList, AccountDebitCommitStageEnum oldStage, AccountDebitCommitStageEnum newStage) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNoAndTrxNoList", merchantNoAndTrxNoList.stream().map(p -> {
            Map<String, String> item = new HashMap<>(2);
            item.put("merchantNo", p[0]);
            item.put("trxNo", p[1]);
            return item;
        }).collect(Collectors.toList()));
        paramMap.put("oldStage", oldStage.getValue());
        paramMap.put("newStage", newStage.getValue());
        paramMap.put("processType", AccountProcessTypeEnum.DEBIT.getValue());
        return update("updateDebitCommitStage", paramMap);
    }

    //region 数据迁移相关

    public long getMigrationMaxId(Date migrationEndDate) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("createTimeBegin", DateUtil.getDayStart(migrationEndDate));
        paramMap.put("createTimeEnd", DateUtil.getDayEnd(migrationEndDate));
        return Optional.ofNullable((Long) getOne("getMigrationMaxId", paramMap)).orElse(0L);
    }


    public List<Long> listIdsForMigration(DataMigrationDto migrationDto) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("maxId", migrationDto.getMaxId());
        paramMap.put("migrateEndDate", DateUtil.getDayEnd(migrationDto.getMigrateEndDate()));
        paramMap.put("debitCommitStages", Arrays.asList(AccountDebitCommitStageEnum.DEBIT_COMMITTED.getValue(),
                AccountDebitCommitStageEnum.RETURN_COMMITTED.getValue(),
                AccountDebitCommitStageEnum.NO_NEED_COMMIT.getValue()));
        paramMap.put("migrateNumPerTime", migrationDto.getMigrateNumPerTime());
        return getSqlSession().selectList(fillSqlId("listIdsForMigration"), paramMap);
    }

    public int migrateDetailByIds(List<Long> idList) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("idList", idList);
        return insert("migrateDetailByIds", param);
    }

    public int deleteDetailByIds(List<Long> detailIdList) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("idList", detailIdList);
        return deleteBy("deleteDetailByIds", param);
    }
    //endregion
}
