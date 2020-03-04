<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="search-item">
        <label>资源ID</label>
        <el-input v-model="searchParam.resourceId"/>
      </div>
      <div class="search-item">
        <label>客户端ID</label>
        <el-input v-model="searchParam.clientId"/>
      </div>

      <div class="search-item">
        <label>资源状态</label>
        <el-select v-model="searchParam.resourceStatus" clearable>
          <el-option v-for="({code,desc}) in $dict('ResourceStatusEnum')" :key="parseInt(code)" :label="desc" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button class="el-button--primary" @click="refreshList">
          <i class="el-icon-search"/>查询
        </el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="pageResult.data" max-height="300" border row-key="id">
        <el-table-column type="index"/>
        <el-table-column label="创建时间" prop="createTime"/>
        <el-table-column label="资源ID" prop="resourceId"/>
        <el-table-column label="资源状态" prop="resourceStatus" :formatter="(row)=>$dictCode('ResourceStatusEnum',row.resourceStatus).desc"/>
        <el-table-column label="描述" prop="remark"/>
        <el-table-column>
          <template v-slot:header="{}">客户端Id<br/>客户端标识</template>
          <template v-slot="{row}">{{row.clientId}}<br/>{{row.clientFlag}}</template>
        </el-table-column>
        <el-table-column>
          <template v-slot:header="{}">上锁时间<br/>过期时间</template>
          <template v-slot="{row}">{{row.lockTime}}<br/>{{row.expireTime}}</template>
        </el-table-column>

        <el-table-column label="操作">
          <template v-slot:default="{row}">
            <el-button type="text" size="small" @click="unlockForce(row)">解锁</el-button>
            <el-button type="text" size="small" @click="unlockAndDelete(row)">删除</el-button>
          </template>
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
    <pForm ref="form" v-bind="{isAdd}" v-on:success="refreshList"/>
  </div>
</template>

<script>
  import request from "@/utils/request";

  export default {
    name: "GlobalLockList",
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 10},
        pageResult: {},
        isAdd: false,
      };
    },
    methods: {
      refreshList() {
        request
          .post(`/globalLock/listGlobalLock`, this.searchParam, {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          });
      },
      unlockForce(row) {
        this.$confirm("确定强制释放锁吗?")
          .then(() => request.post("/globalLock/unlockForce", `resourceId=${row.resourceId}`))
          .then(({data}) => {
            this.refreshList()
            this.$message.success(data)
          });
      },
      unlockAndDelete(row) {
        this.$confirm("确定删除吗?")
          .then(() => request.post("/globalLock/unlockAndDelete", `resourceId=${row.resourceId}`))
          .then(({data}) => {
            this.refreshList()
            this.$message.success(data)
          });
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>
