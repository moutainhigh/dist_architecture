<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <div class="search-item">
        角色：
        <el-input v-model="searchParam.roleName"/>
      </div>
      <div class="function-bar">
        <el-button type="primary" @click="refreshList">搜索</el-button>
        <el-button @click="addRolePage">新增角色</el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="pageResult.data" border>
        <el-table-column type="index"/>
        <el-table-column label="角色名称" prop="roleName"/>
        <el-table-column label="描述" prop="remark"/>
        <el-table-column label="操作" v-slot="{row}">
          <el-button type="text" size="small" @click="assignFunctionPage(row)">分配功能</el-button>
          <el-button type="text" size="small" @click="editRolePage(row)">编辑</el-button>
          <el-button type="text" size="small" @click="deletePmsRole(row)">删除</el-button>
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

    <role-form @success="refreshList" ref="roleForm"/>
    <assign-function-form ref="assignForm"/>
  </div>
</template>

<script>
  import RoleForm from "./RoleForm";
  import AssignFunctionForm from "./AssignFunctionForm";
  import request from "@/utils/request";

  export default {
    name: "RoleList",
    components: {
      RoleForm,
      AssignFunctionForm
    },
    data() {
      return {
        loading: false,
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 10},
        pageResult: {},
      };
    },
    methods: {
      refreshList() {
        this.loading = true;
        request
          .post('/pmsRole/listPmsRole', ``, {params: {...this.pageParam, ...this.searchParam}})
          .then(({data}) => {
            this.pageResult = data;
          })
          .finally(() => {
            this.loading = false;
          });
      },

      addRolePage() {
        this.$refs.roleForm.actionType = 'ADD';
        this.$refs.roleForm.form = {};
        this.$refs.roleForm.show = true;
      },

      editRolePage(row) {
        this.$refs.roleForm.actionType = 'EDIT';
        this.$refs.roleForm.form = {...row};
        this.$refs.roleForm.show = true;
      },

      deletePmsRole(row) {
        this.$confirm("确认删除角色?")
          .then(() => request.post(`/pmsRole/deletePmsRole?id=${row.id}`))
          .then(({data}) => {
            this.$message.success(data);
            this.refreshList();
          });
      },

      assignFunctionPage(row) {
        this.$refs.assignForm.roleId = row.id;
        this.$refs.assignForm.show = true;
        this.$refs.assignForm.init();
      }
    },

    mounted() {
      this.refreshList();
    }
  };
</script>
