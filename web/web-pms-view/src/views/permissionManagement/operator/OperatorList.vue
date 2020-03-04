<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <div class="search-item">
        <label>登录名</label>
        <el-input v-model="searchParam.loginName"/>
      </div>
      <div class="search-item">
        <label>姓名</label>
        <el-input v-model="searchParam.realName"/>
      </div>
      <div class="search-item">
        <label>状态</label>
        <el-select v-model="searchParam.status" clearable>
          <el-option v-for="{code,desc} in $dict('PmsOperatorStatusEnum')" :key="parseInt(code)" :label="desc" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button @click="refreshList" type="primary"><i class="el-icon-search"/>查询</el-button>
        <el-button @click="addOperatorPage" v-permission="'pms:operator:add'"><i class="el-icon-plus"/>创建账户平台</el-button>
      </div>
    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id">
        <el-table-column type="index"/>
        <el-table-column label="登录名" prop="loginName"/>
        <el-table-column label="姓名" prop="realName"/>
        <el-table-column label="手机号" prop="mobileNo"/>
        <el-table-column label="用户类型" prop="type" :formatter="row => (row.type===1&&'超级管理员')||(row.type===2&&'普通操作员')"/>
        <el-table-column label="状态" prop="status" :formatter="row=> $dictCode('PmsOperatorStatusEnum',row.status).desc"/>
        <el-table-column label="操作" width="300px" v-slot="{row}">
          <el-button type="text" size="small" @click="editOperatorPage(row)" v-permission="'pms:operator:edit'">编辑</el-button>
          <el-button type="text" size="small" @click="resetPwdPage(row)" v-permission="'pms:operator:resetpwd'">修改密码</el-button>
          <el-button type="text" size="small" @click="changePmsOperatorStatus(row)"
                     v-text="row.status===-1?'激活':row.status===1?'冻结':row.status===2?'审核':''" v-permission="'pms:operator:changestatus'"/>
          <el-button type="text" size="small" @click="deletePmsOperator(row)" v-permission="'pms:operator:delete'">删除</el-button>
        </el-table-column>
      </el-table>
    </div>

    <div class="main-page-footer">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10,50,100]"
        :total="pageResult.totalRecord"
        :page-size.sync="pageParam.pageSize"
        :current-page.sync="pageParam.pageCurrent"
        @size-change="refreshList"
        @current-change="refreshList"/>
    </div>

    <operator-form ref="form" v-on:success="refreshList"/>
    <reset-pwd ref="resetPwdForm"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import OperatorForm from "./OperatorForm";
  import ResetPwd from "./ResetPwd";

  export default {
    name: "OperatorList",
    components: {
      OperatorForm,
      ResetPwd
    },
    data() {
      return {
        loading: false,
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 10},
        pageResult: {}
      };
    },
    methods: {
      refreshList() {
        this.loading = true;
        request
          .post("/pmsOperator/listPmsOperator", this.searchParam, {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          })
          .finally(() => {
            this.loading = false;
          });
      },
      addOperatorPage() {
        request.get("/pmsRole/listAllPmsRoles").then(({data}) => {
          this.$refs.form.actionType = 'ADD';
          this.$refs.form.allRoles = data;
          this.$refs.form.show = true;
        });
      },
      editOperatorPage(row) {
        let p1 = request.get(`/pmsRole/listAllPmsRoles`)
        let p2 = request.get(`/pmsOperator/getPmsOperatorById?operatorId=${row.id}`)
        Promise.all([p1, p2])
          .then(([{data: allRoles}, {data: pmsOperator}]) => {
            this.$refs.form.allRoles = allRoles;
            this.$refs.form.form = pmsOperator;
            this.$refs.form.actionType = 'EDIT';
            this.$refs.form.show = true;
          })
      },
      resetPwdPage(row) {
        this.$refs.resetPwdForm.form = {operatorId: row.id, loginName: row.loginName};
        this.$refs.resetPwdForm.show = true;
      },

      changePmsOperatorStatus(row) {
        this.$confirm(`确定${(row.status === 1 && '冻结') || (row.status === -1 && '激活') || (row.status === 2 && '审核')}吗`)
          .then(() =>
            request.post("/pmsOperator/changePmsOperatorStatus", `id=${row.id}`)
          ).then(({data}) => {
          this.$message.success(data)
          this.refreshList()
        });
      },

      deletePmsOperator(row) {
        this.$confirm(`确定删除吗操作员${row.loginName}吗?`)
          .then(() =>
            request.post("/pmsOperator/deletePmsOperator", `id=${row.id}`)
          ).then(({data}) => {
            this.$message.success(data);
            this.refreshList()
          }
        );
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>
