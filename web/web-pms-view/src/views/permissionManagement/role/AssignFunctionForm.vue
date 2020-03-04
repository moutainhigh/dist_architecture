<template>
  <el-dialog title="分配功能" :visible.sync="show" :close-on-click-modal="false" @close="closeForm">
    <el-input placeholder="输入关键字进行过滤" v-model="filterText"/>
    <el-tree
      ref="functionTree"
      :data="functionTree"
      node-key="id"
      show-checkbox
      default-expand-all
      @check-change="handleMenuCheckChange"
      :filter-node-method="filterNode">

      <div class="treeNode" slot-scope="{data}">
        <div class="menuNode">{{data.name}}</div>
        <div class="actionNode" v-if="data.actionChildren">
          <el-checkbox
            v-model="actionCheckedKeys"
            v-for="(item) in data.actionChildren"
            :key="item.id"
            :label="item.id"
            @change="checked=>handleActionCheckChange(item,checked)">{{item.name}}
          </el-checkbox>
        </div>
      </div>
    </el-tree>
    <div slot="footer" class="dialog-footer">
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="doAssignFunction">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import request from "@/utils/request";

  export default {
    name: "allocateFunction",
    data() {
      return {
        filterText: "",
        roleId: undefined,
        show: false,
        functionTree: [],
        actionCheckedKeys: []
      };
    },
    watch: {
      filterText(val) {
        this.$refs.functionTree.filter(val);
      }
    },
    methods: {
      init() {  //初始化，查询所有的功能以及该角色已经分配的功能，并初始化其选中状态
        //获取所有的功能项组件的树形结构
        let p1 = request.get("/pmsFunction/listPmsFunctionTree");
        let p2 = request.get("/pmsRole/listPmsRoleFunction?roleId=" + this.roleId)
        Promise.all([p1, p2])
          .then(([{data: functionTree}, {data: roleFunctions}]) => {
            this.functionTree = functionTree;
            //设置那些被赋予权限的菜单的叶子节点的选中状态
            let checkedMenuIds = roleFunctions.filter(p => p.functionType === 1 && roleFunctions.some(p1 => p1.functionType === 1 && p1.parentId === p.id)).map(p => p.id)
            //设置所有被赋予权限的操作项的选中状态
            let checkedActionIds = roleFunctions.filter(p => p.functionType === 2).map(p => p.id)
            this.$refs.functionTree.setCheckedKeys(checkedMenuIds);
            this.actionCheckedKeys = checkedActionIds;
          });
      },

      handleMenuCheckChange(data, checked, indeterminate) {
        if (checked) {// 若菜单下没有操作项选中，则选中所有操作项
          !data.actionChildren.some(action => this.actionCheckedKeys.includes(action.id)) && this.actionCheckedKeys.push(...data.actionChildren.map(action => action.id));
        } else {// 取消选中菜单下的操作项
          let idx = -1;
          data.actionChildren.forEach(action => (idx = this.actionCheckedKeys.indexOf(action.id)) !== -1 && this.actionCheckedKeys.splice(idx, 1));
        }
      },

      handleActionCheckChange(item, checked) {
        if (checked) {// 操作项被选中,则选中其父菜单项节点
          this.$refs.functionTree.setChecked(item.parentId, true);
        }
      },

      filterNode(value, data) {
        if (!value) {
          return true;
        }
        //菜单项名称包含过滤条件或者任意操作项包括过滤条件时返回true
        return data.name.indexOf(value) !== -1 || data.actionChildren.some(p => p.name.indexOf(value) !== -1)
      },

      doAssignFunction() {
        // 合并选中功能id
        let functionIds = [...this.$refs.functionTree.getCheckedKeys(), ...this.$refs.functionTree.getHalfCheckedKeys(), ...this.actionCheckedKeys]
        request
          .post(`/pmsRole/assignPmsRoleFunction?roleId=${this.roleId}`, functionIds)
          .then(({data}) => {
            this.$message.success(data);
            this.show = false;
          })
      }
      ,
      closeForm() {
        this.functionTree = [];
        this.actionCheckedKeys = [];
        this.filterText = "";
        this.roleId = undefined;
        this.show = false;
      }
    }
  }
  ;
</script>

<style lang="scss" scoped>
  /deep/ .el-dialog {
    width: 800px;

    .el-input {
      margin-bottom: 10px;
    }

    .el-dialog__body {
      max-height: 600px;
      overflow-y: auto;

      .el-tree {
        border-bottom: 1px solid #ccc;

        .el-tree-node {
          .el-tree-node__content {
            height: auto;
            align-items: flex-start;
            border: 1px solid #ccc;
            border-bottom: 0px;

            .el-tree-node__expand-icon {
              padding: 6px;
              padding-left: 0;
              margin-top: 2px;
            }

            .el-checkbox {
              margin-bottom: 5px;
              margin-top: 5px;
            }

            .treeNode {
              display: flex;

              .menuNode {
                display: inline-block;
                width: 100px;
                margin-right: 50px;
                white-space: normal;
                margin-top: 5px;
              }

              .actionNode {
                width: 500px;
                white-space: normal;

                .el-checkbox {
                  margin-right: 15px;
                  margin-bottom: 5px;
                  margin-top: 5px;
                }
              }
            }
          }
        }
      }
    }
  }
</style>
