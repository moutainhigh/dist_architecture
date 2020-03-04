<template>
  <el-dialog ref="dialog" :visible.sync="dialog" :close-on-click-modal="false" :before-close="closeForm" :title="isAdd ? '新增操作人' : '编辑操作人'" append-to-body width="600px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="登录名">
        <el-input v-model="form.loginName" :readonly="true" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="form.realName" :readonly="true" placeholder="姓名" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="mobileNo">
        <el-input v-model="form.mobileNo" :readonly="true" placeholder="手机号" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-radio-group v-model="form.type" :disabled="true">
          <el-radio :label="1">商户管理员</el-radio>
          <el-radio :label="2">商户操作员</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="商户编号" prop="merchantNo">
        <el-input v-model="form.merchantNo" placeholder="商户编号" :readonly="true" style="width: 450px;"></el-input>
      </el-form-item>

      <el-form-item label="角色" prop="roles">
        <el-checkbox-group v-model="form.roles" style="width: 450px;">
          <el-checkbox v-for="role in allRoles" :label="role.id" :key="role.id">{{role.roleName}}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" :readonly="true" placeholder="描述" style="width: 450px;"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="closeForm">取消</el-button>
      <el-button :loading="loading" type="primary" @click="doSubmit">确认</el-button>
    </div>
  </el-dialog>
</template>

<script>
    import request from '@/utils/request'

    export default {
        props: {
            isAdd: {
                type: Boolean,
                required: true
            }
        },
        data() {
            return {
                loading: false, dialog: false,
                allRoles: [],
                form: {
                    id: '',
                    loginName: '',
                    realName: '',
                    mobileNo: '',
                    type: '',
                    merchantNo: '',
                    roles: [],
                    remark: ''
                },
                rules: {
                    loginName: [{required: true, message: '请输入登录名', trigger: 'blur'}],
                    realName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
                    mobileNo: [{required: true, message: '请输入手机号码', trigger: 'blur', pattern: /^1[0-9]{10}$/}],
                }
            }
        },
        methods: {
            doSubmit() {
                this.$refs.form.validate((valid) => {
                    if (valid) {
                        this.loading = true
                        if (!this.isAdd) {
                            this.doEdit()
                        }
                    }
                })
            },
            doEdit() {
                request.post('/portalOperator/portalOperatorEdit/', this.form)
                    .then(({data}) => {
                        this.$message.success(data)
                        this.closeForm()
                        this.$emit('success')
                    })
            },
            closeForm() {
                this.dialog = false
                this.loading = false
                this.form = {}
                this.$refs.form.resetFields()
            }
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
