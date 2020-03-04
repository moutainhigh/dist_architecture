<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" title="修改密码" append-to-body width="600px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="登录名" prop="loginName">
        <el-input v-model="form.loginName" style="width: 450px;" disabled/>
      </el-form-item>
      <el-form-item label="登录新密码" prop="newPwd">
        <el-input v-model="form.newPwd" placeholder="登录新密码" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="确认新密码" prop="newPwd2">
        <el-input v-model="form.newPwd2" placeholder="确认登录新密码" style="width: 450px;"/>
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
        loading: false, show: false,
        form: {operatorId: undefined},
        rules: {
          newPwd: [{required: true, message: '请输入密码', trigger: 'blur'}],
          newPwd2: [{
            required: true, trigger: 'blur', validator: (rule, value, callback) => value !== this.form.newPwd ? callback(new Error('两次输入密码不一致!')) : callback()
          }],
        }
      }
    }
    ,
    methods: {
      doSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true
            request.post('/pmsOperator/resetPmsOperatorPwd/', `operatorId=${this.form.operatorId}&newPwd=${this.form.newPwd}`)
              .then(({data}) => {
                this.$message.success(data)
                this.closeForm()
                this.$emit('success')
              })
              .finally(() => {
                this.loading = false;
              })
          }
        })
      }
      ,
      closeForm() {
        this.show = false
        this.loading = false
        this.form = {}
        this.$refs.form.resetFields()
      }
    }
  }
</script>
