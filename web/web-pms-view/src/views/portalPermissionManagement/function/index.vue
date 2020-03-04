<template>
  <div class="main-page">
    <div class="main-page-header">
      <div class="function-bar">
        <el-button type="primary" class="el-button-small" @click="refreshFunctionList"><i class="el-icon-search"></i>刷新</el-button>
        <el-button class="el-button-small" @click="addFunction"><i class="el-icon-plus"></i>添加顶级菜单</el-button>
      </div>
    </div>
    <div class="main-page-content">
      <el-table :data="treeData" border row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column label="功能名称" prop="name"></el-table-column>
        <el-table-column label="功能编号" prop="number"></el-table-column>
        <el-table-column label="URL" prop="url"></el-table-column>
        <el-table-column label="权限标识" prop="permissionFlag"></el-table-column>
        <el-table-column label="功能类型" prop="functionType" :formatter="row => (row.functionType===1?'菜单项':'操作项')"></el-table-column>
        <el-table-column label="操作">
          <template v-slot:default="{row}">
            <el-button type="text" size="small" @click="editFunction(row)">编辑</el-button>
            <el-button type="text" size="small" @click="deleteFunction(row)">删除</el-button>
            <el-button type="text" size="small" @click="addFunction(row)" v-if="row.functionType===1">添加子功能</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <eForm ref="form" v-bind="{isAdd,currentAddPid}" v-on:success="refreshFunctionList"/>
  </div>
</template>

<script>
    import eForm from './form'
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
        name: 'MenuManagement',
        components: {
            eForm
        },
        data() {
            return {
                functions: [],
                isAdd: false,
                currentAddPid: null
            }
        },
        methods: {
            refreshFunctionList() {
                request.get('/portalFunction/portalFunctionList').then(({data}) => {
                    this.functions = data
                })
            },
            addFunction(row) {
                this.isAdd = true;
                this.$refs.form.form = {parentId: row ? row.id : 0, parentName: row ? row.name : undefined}
                this.$refs.form.dialog = true
            },
            editFunction(row) {
                this.isAdd = false
                this.$refs.form.form = {
                    id: row.id,
                    parentId: row.parentId,
                    name: row.name,
                    number: row.number,
                    permissionFlag: row.permissionFlag,
                    functionType: row.functionType,
                    url: row.url
                }
                this.$refs.form.dialog = true;
            },
            deleteFunction(row) {
                this.$confirm('确定删除吗?')
                    .then(() => request.post("/portalFunction/portalFunctionDelete", `id=${row.id}`))
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

