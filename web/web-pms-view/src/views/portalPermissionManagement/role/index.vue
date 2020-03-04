<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="search-item">
        角色：
        <el-input v-model="roleName"></el-input>
      </div>
      <div class="function-bar">
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="toCreateRole">新增角色</el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="rolesData" v-loading="loading" max-height="300" border>
        <el-table-column label="角色名称" prop="roleName">
        </el-table-column>

        <el-table-column label="角色类型" prop="roleType" :formatter="row=>row.roleType===1?'商户管理员':'商户操作员'">
        </el-table-column>

        <el-table-column label="描述" prop="remark">
        </el-table-column>

        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="toAllocatFunction(scope.row.id)">分配功能</el-button>
            <el-button type="text" size="small" @click="toDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10,50,100]"
                       :total="total" :page-size="pageSize" :current-page="pageCurrent"
                       @size-change="search" @current-change="search">
        </el-pagination>
      </div>
    </div>

    <createRoleForm @success="createRoleSuccess" ref="createRoleForm" :is-add="true"/>
    <allocatFunction :roleId="roleId" ref="allocatFunction"/>
  </div>
</template>

<script>
    import createRoleForm from './form';
    import allocatFunction from './allocateFunction';
    import request from '@/utils/request'

    export default {
        name: 'RoleManagement',
        components: {
            createRoleForm,
            allocatFunction
        },
        data() {
            return {
                loading: false,
                roleName: '',
                pageCurrent: 1,
                pageSize: 10,
                total: 0,
                rolesData: [],
                roleId: -1
            }
        },
        methods: {
            toCreateRole() {
                this.$refs.createRoleForm.dialogFormVisible = true;
            },
            createRoleSuccess() {
                this.roleName = '';
                this.pageCurrent = 1;
                this.search();
            },
            search() {
                this.loading = true;
                request.post('/portalRole/portalRoleList?roleName=' + this.roleName,
                    {pageCurrent: this.pageCurrent, pageSize: this.pageSize}
                ).then(({data}) => {
                    this.rolesData = data.data;
                    this.total = data.totalRecord;
                }).finally(() => {
                    this.loading = false;
                });
            },
            toAllocatFunction(roleId) {
                this.roleId = roleId;
                this.$refs.allocatFunction.dialogFormVisible = true;
                this.$refs.allocatFunction.init();
            },
            toDelete(id) {
                this.$confirm('确认删除角色？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.loading = true;
                    request.post('/portalRole/portalRoleDelete?id=' + id).then(({data}) => {
                        this.$message.success(data);
                        this.search();
                    }).finally(() => {
                        this.loading = false;
                    });
                });
            }
        },
        mounted() {
            this.search();
        }
    }
</script>
