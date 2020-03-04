<template>
  <el-dialog ref="dialog" :visible.sync="dialog" :close-on-click-modal="false" :before-close="closeForm" title="修改密码" append-to-body width="600px">
    <el-form ref="resetPwdForm" :inline="true" :model="resetPwdForm" :rules="rules" size="small" label-width="100px">
      <el-form-item label="登录新密码" prop="newPwd">
        <el-input v-model="resetPwdForm.newPwd" placeholder="登录新密码" style="width: 450px;"></el-input>
      </el-form-item>
       <el-form-item label="确认新密码" prop="newPwd2">
        <el-input v-model="resetPwdForm.newPwd2" placeholder="确认登录新密码" style="width: 450px;"></el-input>
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
        data() {
            return {
                loading: false, dialog: false,
                resetPwdForm: {
                    id: '',
                    newPwd: '',
                    newPwd2: ''
                },
                rules: {
                    newPwd: [{required: true, message: '请输入密码', trigger: 'blur'}],
                    newPwd2: [{required: true, message: '请输入确认密码', trigger: 'blur'}],
                }
            }
        },
        methods: {
            doSubmit() {
                this.$refs.resetPwdForm.validate((valid) => {
                    if (valid) {
                        this.loading = true
                        request.post('/pmsOperator/pmsOperatorResetPwd/', this.resetPwdForm)
                          .then(({data}) => {
                            this.$message(data)
                            this.closeForm()
                            this.$emit('success')
                    })
                    }
                })
            },
            closeForm() {
                this.dialog = false
                this.loading = false
                this.resetPwdForm = {}
                this.$refs.resetPwdForm.resetFields()
            }
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
