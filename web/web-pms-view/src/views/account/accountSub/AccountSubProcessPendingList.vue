<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <el-form inline label-width="100px">
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
          <label>账务处理流水号</label>
          <el-input v-model="searchParam.accountProcessNo"/>
        </div>

        <div class="search-item">
          <label>交易流水号</label>
          <el-input v-model="searchParam.trxNo" placeholder="与创建时间搭配使用"/>
        </div>

        <div class="search-item">
          <label>商户编号</label>
          <el-input v-model="searchParam.merchantNo" placeholder="与创建时间搭配使用"/>
        </div>

        <div class="search-item">
          <label>账务处理阶段</label>
          <el-select v-model="searchParam.processStage" clearable>
            <el-option v-for="{code,desc} in $dict('AccountProcessPendingStageEnum')" :label="desc" :key="parseInt(code)" :value="parseInt(code)"/>
          </el-select>
        </div>

        <div class="function-bar">
          <el-switch @change="onHistoryChange" v-model="inHistory" active-text="查询历史表" inactive-text="查询实时表"/>
          <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
        </div>
      </el-form>

    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id">

        <el-table-column type="index" label="序号"/>

        <el-table-column prop="createTime" label="创建时间"/>

        <el-table-column prop="modifyTime" label="修改时间"/>

        <el-table-column prop="accountProcessNo" label="账务处理流水号"/>

        <el-table-column label="处理阶段" v-slot="{row}" width="70px">
          <span v-if="row.processStage===1" style="color: #ff7f11;">待处理</span>
          <span v-else-if="row.processStage===2" style="color:red;">处理中</span>
          <span v-else-if="row.processStage===3" style="color:green;">已处理</span>
        </el-table-column>

        <el-table-column prop="remark" label="备注"/>

        <el-table-column label="操作" v-slot="{row}">
          <el-button type="text" size="small" @click="viewAccountProcessPendingPage(row)" v-permission="'account:sub:processPending:view'">查看详情</el-button>
        </el-table-column>
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

    <AccountSubProcessPendingView ref="processPendingView"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import AccountSubProcessPendingView from "./AccountSubProcessPendingView";
  import {formatMoney} from "@/utils/commonUtil"
  import dayjs from "dayjs"

  export default {
    name: "AccountSubProcessPendingList",
    components: {
      AccountSubProcessPendingView,
    },
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 30},
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
          .post(`accountSub/listProcessPending?inHistory=${this.inHistory}`,
            Object.assign(this.searchParam, this.createTimeRange ? {createTimeBegin: this.createTimeRange[0], createTimeEnd: this.createTimeRange[1]} : null),
            {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      viewAccountProcessPendingPage(row) {
        request.post(`accountSub/viewProcessPending?inHistory=${row.inHistory}`, `id=${row.id}`)
          .then(({data}) => {
            this.$refs.processPendingView.processPending = data;
            this.$refs.processPendingView.show = true;
          })
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

