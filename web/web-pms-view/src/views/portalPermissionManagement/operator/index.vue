<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="search-item">
        登录名：
        <el-input v-model="loginName"></el-input>
      </div>
      <div class="search-item">
        姓名：
        <el-input v-model="realName"></el-input>
      </div>
      <div class="search-item">
        状态：
        <el-select v-model="status">
          <el-option
            v-for="item in pmsOperatorStatusEnums"
            :key="item.value"
            :label="item.desc"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button plain @click="refreshList">
          <i class="el-icon-search"></i>查询
        </el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="tableData" max-height="300" border row-key="id">
        <el-table-column label="登录名" prop="loginName"></el-table-column>
        <el-table-column label="姓名" prop="realName"></el-table-column>
        <el-table-column label="手机号" prop="mobileNo"></el-table-column>
        <el-table-column label="商户编号" prop="merchantNo"></el-table-column>
        <el-table-column label="用户类型" prop="type" :formatter="row => (row.type===1?'商户管理员':'商户操作员')"></el-table-column>
        <el-table-column label="状态" prop="status">
          <template v-slot:default="{row}">{{row.status|statusFilter}}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot:default="{row}">
            <el-button type="text" size="small" @click="editPortalOperator(row)">编辑</el-button>
            <el-button type="text" size="small" @click="changeStatus(row)" v-text="row.status===100?'冻结':'审核'"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10,50,100]"
          :total="total"
          :page-size.sync="pageSize"
          :current-page.sync="pageCurrent"
          @size-change="refreshList"
          @current-change="refreshList"></el-pagination>
      </div>
    </div>
    <pForm ref="form" v-bind="{isAdd}" v-on:success="refreshList"/>
    <resetPwdForm ref="resetPwdForm"/>
  </div>
</template>

<script>
    import request from "@/utils/request";
    import pForm from "./form";
    import resetPwdForm from "./resetPwd";

    export default {
        name: "AccountList",
        components: {
            pForm,
            resetPwdForm,
        },
        data() {
            return {
                loginName: "",
                realName: "",
                status: "",
                pmsOperators: [],
                pmsOperatorStatusEnums: [],
                isAdd: false,
                pageCurrent: 1,
                pageSize: 10,
                total: 0
            };
        },
        methods: {
            getPmsOperatorStatusEnums() {
                request.get("/pmsOperator/getPmsOperatorStatusEnums").then(({data}) => {
                    this.pmsOperatorStatusEnums = data;
                    sessionStorage.setItem(
                        "operatorStatusEnums",
                        JSON.stringify(this.pmsOperatorStatusEnums)
                    );
                });
            },
            refreshList() {
                this.loading = true
                request
                    .post(`/portalOperator/portalOperatorList?pageCurrent=${this.pageCurrent}&pageSize=${this.pageSize}`, {
                        loginName: this.loginName,
                        realName: this.realName,
                        status: this.status
                    })
                    .then(({data}) => {
                        this.pmsOperators = data.data;
                        this.total = data.totalRecord;
                    })
                    .finally(() => {
                        this.loading = false
                    });
            },
            editPortalOperator(row) {
                let p1 = request.get(row.type === 1 ? '/portalRole/getAllAdminRoles' : `/portalRole/getRolesByMerchantNo?merchantNo=${row.merchantNo}`)

                let p2 = request
                    .get("/portalRole/listRolesByOperatorId?id=" + row.id)

                Promise.all([p1, p2])
                    .then(([resp1, resp2]) => {
                        this.$refs.form.allRoles = resp1.data;
                        this.$refs.form.form = {
                            id: row.id,
                            loginName: row.loginName,
                            realName: row.realName,
                            mobileNo: row.mobileNo,
                            type: row.type,
                            merchantNo: row.merchantNo,
                            roles: resp2.data.map(p => p.id),
                            remark: row.remark
                        };
                        this.$refs.form.dialog = true;
                    })
            },
            changeStatus(row) {
                this.$confirm(row.status === "100" ? "确定冻结吗?" : "确定审核吗?")
                    .then(() => request.post("/portalOperator/changeStatus", `id=${row.id}`))
                    .then(() => this.refreshList());
            }
        },
        filters: {
            statusFilter: value => {
                let pmsOperatorStatusEnums = JSON.parse(
                    sessionStorage.getItem("operatorStatusEnums")
                );
                let enumsArr = pmsOperatorStatusEnums.filter(item => item.value == value);
                return enumsArr[0].desc ? enumsArr[0].desc : "";
            }
        },
        computed: {
            tableData() {
                return this.pmsOperators;
            }
        },
        mounted() {
            console.log("aaaa");
            this.getPmsOperatorStatusEnums();
            this.refreshList();
        }
    };
</script>

<style lang="scss" scoped>
  .accountList-container {
    height: calc(100vh - 84px);
    padding: 30px 40px;
    background: #f7f7f7;

    .search-container {
      padding: 30px 30px;
      background: #fff;

      .search-item {
        display: inline-block;
        margin-right: 20px;

        .el-input,
        .el-select {
          width: 180px;
        }
      }

      .search-btn {
        float: right;
      }
    }

    .content-container {
      margin-top: 30px;
      padding: 10px 20px;
      background: #fff;
    }

    .pagination {
      margin: 5px;
    }
  }
</style>
