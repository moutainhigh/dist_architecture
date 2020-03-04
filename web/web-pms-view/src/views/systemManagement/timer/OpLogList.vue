<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="800px" :title="'操作日志'">
    <div v-loading="loading" element-loading-text="刷新中" element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)">
      <el-form :inline="true">
        <el-form-item label="操作主体" size="medium">
          <el-input v-model="objKey"/>
        </el-form-item>
        <el-form-item size="medium">
          <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="pageResult.data" max-height="300" border row-key="id">
        <el-table-column label="操作主体" prop="objKey"/>

        <el-table-column max-width="200">
          <template v-slot:header="{}">操作时间</template>
          <template v-slot:default="{row}">{{row.createTime}}</template>
        </el-table-column>

        <el-table-column label="操作人" prop="operator" max-width="200"/>

        <el-table-column label="操作内容" prop="content"/>
      </el-table>
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10,30,50,100]"
        :total="pageResult.totalRecord"
        :page-size.sync="pageParam.pageSize"
        :current-page.sync="pageParam.pageCurrent"
        @size-change="refreshList"
        @current-change="refreshList">
      </el-pagination>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button class="el-icon-close" @click="closeForm">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import request from "@/utils/request";

  export default {
    name: "OpLogList",
    data() {
      return {
        objKey: '',
        pageParam: {pageCurrent: 1, pageSize: 10},
        pageResult: {},
        loading: false,
        show: false,
      };
    },
    methods: {
      refreshList() {
        this.loading = true
        this.show = true
        let param = {objKey: this.objKey, pageCurrent: this.pageParam.pageCurrent, pageSize: this.pageParam.pageSize};
        request.post(`/timer/listOpLog`, {}, {params: param})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      genObjKeyForJob(jobGroup, jobName) {
        if (jobGroup !== '' && jobName !== '') {
          this.objKey = jobGroup + '-' + jobName;
        }
      },
      closeForm() {
        this.show = false
        this.loading = false
        this.objKey = '';
        this.pageResult = {};
        this.pageParam.pageCurrent = 1
      }
    },
  };
</script>

