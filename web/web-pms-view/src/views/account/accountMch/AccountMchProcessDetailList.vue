<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">

      <div class="search-item">
        <label>记账时间</label>
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"/>
      </div>

      <div class="search-item">
        <label>商户编号</label>
        <el-input v-model="searchParam.merchantNo"/>
      </div>

      <div class="search-item">
        <label>账务处理流水号</label>
        <el-input v-model="searchParam.accountProcessNo"/>
      </div>

      <div class="search-item">
        <label>平台流水号</label>
        <el-input v-model="searchParam.trxNo"/>
      </div>

      <div class="search-item">
        <label>账务处理类型</label>
        <el-select v-model="searchParam.processType" clearable>
          <el-option v-for="{code,desc} in $dict('AccountProcessTypeEnum')" :key="code" :value="parseInt(code)" :label="desc"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-switch @change="onHistoryChange" v-model="inHistory" active-text="查询历史表" inactive-text="查询实时表"/>
        <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
      </div>


    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id">

        <el-table-column type="index" label="序号"/>

        <el-table-column width="130px">
          <template v-slot:header="{}">记账时间<br/>业务时间</template>
          <template v-slot="{row}">{{row.createTime}}<br/>{{row.trxTime}}</template>
        </el-table-column>


        <el-table-column width="150px">
          <template v-slot:header="{}">商户编号<br/>商户类型</template>
          <template v-slot="{row}">{{row.merchantNo}}<br/>{{$dictCode('MerchantTypeEnum',row.merchantType).desc}}</template>
        </el-table-column>

        <el-table-column width="200px">
          <template v-slot:header="{}">平台流水号<br/>商户订单号</template>
          <template v-slot="{row}">{{row.trxNo}}<br/>{{row.mchTrxNo}}</template>
        </el-table-column>

        <el-table-column width="200px">
          <template v-slot:header="{}">账务处理流水号<br/>业务类型</template>
          <template v-slot="{row}">{{row.accountProcessNo}}<br/>{{$dictCode('BusinessTypeEnum',row.bizType).desc}}</template>
        </el-table-column>

        <el-table-column width="120px">
          <template v-slot:header="{}">交易金额<br/>账务处理类型</template>
          <template v-slot="{row}">{{formatMoney(row.alterAmount)}}<br/>{{$dictCode('AccountProcessTypeEnum',row.processType).desc}}</template>
        </el-table-column>

        <el-table-column width="120px">
          <template v-slot:header="{}">待清算变动金额<br/>待清算金额</template>
          <template v-slot="{row}">{{formatMoney(row.alterUnsettleAmount)}}<br/>{{formatMoney(row.unsettleAmount)}}</template>
        </el-table-column>

        <el-table-column width="120px">
          <template v-slot:header="{}">可用变动金额<br/>可用余额</template>
          <template v-slot="{row}">{{formatMoney(row.alterUsableAmount)}}<br/>{{formatMoney(row.usableAmount)}}</template>
        </el-table-column>


        <el-table-column prop="remark" label="备注"/>

      </el-table>
    </div>
    <div class="main-page-footer">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10,15,30,50]"
        :total="pageResult.totalRecord"
        :page-size.sync="pageParam.pageSize"
        :current-page.sync="pageParam.pageCurrent"
        @size-change="refreshList"
        @current-change="refreshList"/>
    </div>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import {formatMoney} from "@/utils/commonUtil"
  import dayjs from 'dayjs'


  export default {
    name: "AccountMchProcessDetailList",
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 10},
        inHistory: false,
        createTimeRange: [],
        pageResult: {},
        loading: false,
      };
    },
    methods: {
      formatMoney,
      refreshList() {
        this.loading = true
        request
          .post(`/accountMch/listProcessDetail?inHistory=${this.inHistory}`,
            Object.assign(this.searchParam, this.createTimeRange ? {createTimeBegin: this.createTimeRange[0], createTimeEnd: this.createTimeRange[1]} : null),
            {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      onHistoryChange(history) {
        if (history) {
          this.createTimeRange = [dayjs(new Date()).add(-1, 'M').format("YYYY-MM-DD HH:mm:ss"), dayjs(new Date()).format("YYYY-MM-DD HH:mm:ss")];
        } else {
          this.createTimeRange = [];
        }
        this.pageResult = {}
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>

