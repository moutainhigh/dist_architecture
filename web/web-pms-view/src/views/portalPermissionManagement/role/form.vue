<template>
  <el-dialog title="新增角色" @close="formClose" :visible.sync="dialogFormVisible" @open="form.roleType=(isAdd?1:form.roleType)">
    <el-form ref="createRoleForm" label-position="left" :rules="rules" :model="form">
      <el-form-item prop="roleName" label="角色名称" :label-width="labelWidth">
        <el-input v-model="form.roleName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item prop="merchantNo" label="商户编号" :label-width="labelWidth">
        <el-input v-model="form.merchantNo" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item prop="roleType" label="角色类型" :label-width="labelWidth">
        <el-radio v-model="form.roleType" :label="1" :disabled="true">商户管理员</el-radio>
        <el-radio v-model="form.roleType" :label="2" :disabled="true">商户操作员</el-radio>
      </el-form-item>
      <el-form-item prop="remark" label="描述" :label-width="labelWidth">
        <el-input v-model="form.remark" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogFormVisible = false">取 消</el-button>
      <el-button type="primary" @click="createRole">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
    import request from '@/utils/request'

    export default {
        name: 'createRoleForm',
        props: {
            isAdd: {
                type: Boolean,
                required: true
            }
        },
        data() {
            return {
                form: {
                    merchantNo: '',
                    roleName: '',
                    roleType: null,
                    remark: ''
                },
                rules: {
                    roleName: [
                        {required: true, message: '角色名称不能为空'}
                    ]
                },
                dialogFormVisible: false,
                labelWidth: '100px',
            }
        },
        methods: {
            createRole() {
                this.$refs.createRoleForm.validate((pass) => {
                    if (pass) {
                        request.post('/portalRole/portalRoleAdd', this.form).then(({data}) => {
                            this.$message.success(data);
                            this.dialogFormVisible = false;
                            this.$emit('success');
                        })
                    }
                });
            },
            formClose() {
                this.$refs.createRoleForm.resetFields();
            }
        }
    }
</script>

<style scoped>
  /deep/ .el-dialog {
    width: 450px;
  }

  .el-input {
    width: 300px;
  }
</style>
