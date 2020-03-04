<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <div class="search-item">
        <label>创建时间</label>
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
        <label>父商户编号</label>
        <el-input v-model="searchParam.parentMerchantNo"/>
      </div>

      <div class="search-item">
        <label>账户状态</label>
        <el-select v-model="searchParam.status" clearable>
          <el-option v-for="{code,desc} in $dict('AccountStatusEnum')" :label="desc" :key="parseInt(code)" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
      </div>
    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id">

        <el-table-column type="index" label="序号"/>

        <el-table-column width="140" prop="createTime" label="创建时间"/>

        <el-table-column prop="merchantNo" label="商户编号"/>

        <el-table-column prop="parentMerchantNo" label="父商户编号"/>

        <el-table-column label="状态" :formatter="row=>$dictCode('AccountStatusEnum',row.status).desc"/>

        <el-table-column label="待清算余额" prop="unsettleAmount" :formatter="row=>formatMoney(row.unsettleAmount)"/>

        <el-table-column label="可用余额" prop="usableAmount" :formatter="row=>formatMoney(row.usableAmount)"/>

      </el-table>
    </div>

    <div class="main-page-footer">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[15,30,50,100]"
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

  export default {
    name: "NotifyRecordList",
    data() {
      return {
        searchParam: {},
        createTimeRange: [],
        pageParam: {pageCurrent: 1, pageSize: 30},
        pageResult: {},
        loading: false,
      };
    },
    methods: {
      formatMoney,
      refreshList() {
        this.loading = true
        request.post(`/accountSub/listAccountSub`, Object.assign(this.searchParam, {createTimeBegin: this.createTimeRange[0], createTimeEnd: this.createTimeRange[1]}),
          {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>

