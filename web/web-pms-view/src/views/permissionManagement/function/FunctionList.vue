<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="function-bar">
        <el-button plain class="el-button-small" @click="refreshFunctionList"><i class="el-icon-search"/>刷新</el-button>
        <el-button plain class="el-button-small" @click="addFunctionPage" v-permission="'pms:function:add'"><i class="el-icon-plus"/>添加顶级菜单</el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="treeData" border row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column label="功能名称" prop="name"/>
        <el-table-column label="功能编号" prop="number"/>
        <el-table-column label="URL" prop="url"/>
        <el-table-column label="权限标识" prop="permissionFlag"/>
        <el-table-column label="功能类型" prop="functionType" :formatter="row => (row.functionType===1&&'菜单项')||(row.functionType===2&&'操作项')"/>
        <el-table-column label="操作" v-slot="{row}">
          <el-button type="text" size="small" @click="editFunctionPage(row)" v-permission="'pms:function:edit'">编辑</el-button>
          <el-button type="text" size="small" @click="deleteFunction(row)" v-permission="'pms:function:delete'">删除</el-button>
          <el-button type="text" size="small" @click="addFunctionPage(row)" v-if="row.functionType===1" v-permission="'pms:function:add'">添加子功能</el-button>
        </el-table-column>
      </el-table>
    </div>
    <function-form ref="form" v-on:success="refreshFunctionList"/>
  </div>
</template>

<script>
  import FunctionForm from './FunctionForm'
  import request from '@/utils/request'

  const buildTreeData = function (pid, pidGroup, resultArr) {
    if (!pidGroup[pid]) {
      return;
    }
    pidGroup[pid].forEach(f => {
      resultArr.push(f)
      if (pidGroup[f.id]) {
        f.children = []
        buildTreeData(f.id, pidGroup, f.children)
      }
    })
  }

  export default {
    name: 'FunctionList',
    components: {
      FunctionForm
    },
    data() {
      return {
        functions: []
      }
    },
    methods: {
      refreshFunctionList() {
        request.get('/pmsFunction/listAllPmsFunction').then(({data}) => {
          this.functions = data
        })
      },
      addFunctionPage(row) {
        this.$refs.form.actionType = 'ADD';
        this.$refs.form.form.parentId = row && row.id;
        this.$refs.form.form.parentName = row && row.name;
        this.$refs.form.show = true
      },
      editFunctionPage(row) {
        this.$refs.form.actionType = 'EDIT'
        this.$refs.form.form = {...row}
        this.$refs.form.show = true;
      },
      deleteFunction(row) {
        this.$confirm('确定删除吗?')
          .then(() => request.post("/pmsFunction/deletePmsFunction", `id=${row.id}`))
          .then(() => this.refreshFunctionList())
      }
    },
    computed: {
      treeData() {
        let pidGroup = []
        this.functions.forEach(f => {
          if (!pidGroup[f.parentId]) {
            pidGroup[f.parentId] = [f]
          } else {
            pidGroup[f.parentId].push(f)
          }
        })
        let resultArr = []
        buildTreeData(0, pidGroup, resultArr)
        return resultArr
      }
    },
    mounted() {
      this.refreshFunctionList()
    }
  }
</script>

