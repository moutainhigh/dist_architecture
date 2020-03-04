<template>
  <el-dialog title="分配功能" :visible.sync="dialogFormVisible">
    <el-input placeholder="输入关键字进行过滤" v-model="filterText">
    </el-input>
    <el-tree ref="functionTree" :data="functions" node-key="id" show-checkbox default-expand-all
             @check-change="handleCheckChange" :filter-node-method="filterNode">
      <div class="treeNode" slot-scope="{ node, data }">
        <div class="menuNode">{{data.name}}</div>
        <div class="actionNode" v-if="data.actionChildren">
          <el-checkbox v-model="actionCheckedKeys" v-for="(item, index) in data.actionChildren"
                       :key="index" :label="item.id" @change="actionChange(item)">
            {{item.name}}
          </el-checkbox>
        </div>
      </div>
    </el-tree>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancel">取 消</el-button>
      <el-button type="primary" @click="allocateFunction">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
    import request from '@/utils/request';

    export default {
        name: "allocateFunction",
        props: {
            roleId: {
                type: Number
            }
        },
        data() {
            return {
                filterText: '',
                dialogFormVisible: false,
                functions: [],
                actionCheckedKeys: [],
            }
        },
        watch: {
            filterText(val) {
                this.$refs.functionTree.filter(val);
            }
        },
        methods: {
            init() {
                this.$nextTick(() => {
                    this.getRoleFuntion();
                });
            },
            handleCheckChange(data, checked, indeterminate) {
                if (checked) {
                    // 选中菜单项下的操作项
                    data.actionChildren.forEach(action => {
                        if (!this.actionCheckedKeys.includes(action.id)) {
                            this.actionCheckedKeys.push(action.id);
                        }
                    });
                } else {
                    // 取消选中菜单下的操作项
                    data.actionChildren.forEach(action => {
                        let idx = this.actionCheckedKeys.indexOf(action.id);
                        if (idx > -1) {
                            this.actionCheckedKeys.splice(idx, 1);
                        }
                    });
                }
            },
            filterNode(value, data) {
                if (!value) {
                    return true;
                } else {
                    if (data.name.indexOf(value) !== -1) {
                        return true;
                    }
                    // 匹配操作项
                    let flag = false;
                    for (let i = 0; i < data.actionChildren.length; ++i) {
                        if (data.actionChildren[i].name.indexOf(value) !== -1) {
                            flag = true;
                            break;
                        }
                    }
                    return flag;
                }
            },
            async actionChange(item) {
                // 操作项被选中
                if (this.actionCheckedKeys.includes(item.id)) {
                    let checkedKeys = this.$refs.functionTree.getCheckedKeys();
                    // 如果父菜单没有被选中，则选中
                    if (!checkedKeys.includes(item.parentId)) {
                        checkedKeys.push(item.parentId);
                        let tempKeys = this.actionCheckedKeys.slice();
                        await this.$refs.functionTree.setCheckedKeys(checkedKeys);
                        this.actionCheckedKeys = tempKeys;
                    }
                }
            },
            async getRoleFuntion() {
                // 清除已选择权限
                await this.$refs.functionTree.setCheckedKeys([]);
                this.actionCheckedKeys = [];

                // 请求接口
                request.get('/portalRole/portalRoleFunctionList?roleId=' + this.roleId).then(async response => {
                    if (response.data != null && response.data.length > 0) {
                        let menuKeys = [];
                        let actionKeys = [];
                        response.data.forEach((portalFunction) => {
                            if (portalFunction.functionType === 1) {
                                if (this.$refs.functionTree.getNode(portalFunction.id).isLeaf) {
                                    // 只有叶子节点需要选中，父节点会根据子节点的情况自动设置为选中、半选、不选中
                                    menuKeys.push(portalFunction.id);
                                }
                            } else {
                                actionKeys.push(portalFunction.id);
                            }
                        });

                        await this.$refs.functionTree.setCheckedKeys(menuKeys);
                        this.actionCheckedKeys = actionKeys;
                    }
                })
            },
            allocateFunction() {
                // 合并选中功能id
                let functionIds = [].concat(this.$refs.functionTree.getCheckedKeys())
                    .concat(this.$refs.functionTree.getHalfCheckedKeys())
                    .concat(this.actionCheckedKeys);

                request.post('/portalRole/portalRoleAssignPermission?roleId=' + this.roleId, functionIds).then(({data}) => {
                    this.$message.success(data);
                    this.dialogFormVisible = false;
                });
            },
            cancel() {
                this.dialogFormVisible = false;
            }
        },
        mounted() {
            request.get('/portalFunction/portalFunctionTree').then(({data}) => {
                this.functions = data;
            })
        }
    }
</script>

<style lang="scss" scoped>
  /deep/ .el-dialog {
    width: 800px;

    .el-input {
      margin-bottom: 10px
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
                  margin-top: 5px
                }
              }
            }
          }
        }
      }
    }
  }
</style>
