<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <div class="search-item">
        <label>操作人</label>
        <el-input v-model="searchParam.operatorLoginName"/>
      </div>
      <div class="search-item">
        <label>创建时间</label>
        <el-date-picker v-model="searchParam.createTimeBegin" value-format="yyyy-MM-dd HH:mm:ss" type="datetime"/>
      </div>
      <div class="search-item">
        <label>至</label>
        <el-date-picker v-model="searchParam.createTimeEnd" value-format="yyyy-MM-dd HH:mm:ss" type="datetime"/>
      </div>
      <div class="search-item">
        <label>操作类型</label>
        <el-select v-model="searchParam.operateType" clearable>
          <el-option v-for="{code,desc} in $dict('PmsOperateLogTypeEnum')" :key="parseInt(code)" :label="desc" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button @click="refreshList" type="primary"><i class="el-icon-search"/>查询</el-button>
      </div>
    </div>

    <div class="main-page-content">
      <el-table border :data="pageResult.data">
        <el-table-column type="index"/>
        <el-table-column prop="createTime" label="创建时间" sortable/>
        <el-table-column prop="operatorLoginName" label="操作人"/>
        <el-table-column prop="ip" label="IP"/>
        <el-table-column prop="content" label="操作内容"/>
        <el-table-column prop="status" label="操作状态" :formatter="row=>row.status===1?'成功':'失败'"/>
        <el-table-column prop="operateType" label="操作类型" :formatter="row=>$dictCode('PmsOperateLogTypeEnum',row.operateType).desc"/>
      </el-table>
    </div>

    <div class="main-page-footer">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10,15,30]"
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

  export default {
    name: 'OperateLogList',
    data() {
      return {
        loading: false,
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 15},
        pageResult: {},
      };
    },
    methods: {
      refreshList() {
        this.loading = true;
        request
          .post("/pmsOperator/listOperateLogPage", this.searchParam, {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          })
          .finally(() => {
            this.loading = false;
          })
      },
    },
    mounted() {
      this.refreshList();
    }
  };
</script>
