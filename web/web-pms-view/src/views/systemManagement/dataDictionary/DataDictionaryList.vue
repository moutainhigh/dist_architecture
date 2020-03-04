<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="search-item">
        <label>数据名称</label>
        <el-input v-model="searchParam.dataName"/>
      </div>
      <div class="search-item">
        <label>系统标识</label>
        <el-select v-model="searchParam.systemType">
          <el-option v-for="({code,desc}) in $dict('SystemTypeEnum')" :key="parseInt(code)" :label="desc" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-button class="el-button--primary" @click="refreshDataDictionaryList">
          <i class="el-icon-search"/>查询
        </el-button>
        <el-button @click="addDataDictionary">
          <i class="el-icon-plus"/>创建数据字典
        </el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id">
        <el-table-column label="数据名称" prop="dataName"/>
        <el-table-column label="字典信息" prop="dataInfo" :show-overflow-tooltip="true"/>
        <el-table-column label="系统标识" prop="systemType" :formatter="(row)=>$dictCode('SystemTypeEnum',row.systemType).desc"/>
        <el-table-column label="描述" prop="remark"/>
        <el-table-column label="操作" v-slot="{row}">
          <el-button type="text" size="small" @click="editDataDictionary(row)">编辑</el-button>
          <el-button type="text" size="small" @click="deleteDataDictionary(row)">删除</el-button>
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
        @size-change="refreshDataDictionaryList"
        @current-change="refreshDataDictionaryList"/>
    </div>
    <data-dictionary-form ref="form" v-on:success="refreshDataDictionaryList"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import DataDictionaryForm from "./DataDictionaryForm";

  export default {
    name: "DataDictionaryList",
    components: {
      DataDictionaryForm,
    },
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 10},
        pageResult: {},
      };
    },
    methods: {
      refreshDataDictionaryList() {
        request
          .post(`/dataDictionary/listDataDictionary`, this.searchParam, {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          });
      },
      addDataDictionary() {
        this.$refs.form.actionType = "ADD"
        this.$refs.form.form = {id: "", dataInfo: []};
        this.$refs.form.show = true;
      },
      editDataDictionary(row) {
        this.$refs.form.actionType = "EDIT"
        request.get(`/dataDictionary/getDataDictionaryVOById?id=${row.id}`)
          .then(({data}) => {
            this.$refs.form.form = {
              id: data.id,
              dataName: data.dataName,
              dataInfo: data.dataInfo,
              remark: data.remark,
              systemType: data.systemType
            };
            this.$refs.form.show = true;
          })
      },
      deleteDataDictionary(row) {
        this.$confirm("确定删除吗?")
          .then(() => request.post("/dataDictionary/deleteDataDictionary", `id=${row.id}`))
          .then(() => this.refreshDataDictionaryList());
      }
    },
    mounted() {
      this.refreshDataDictionaryList();
    }
  };
</script>
