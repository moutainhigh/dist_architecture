<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" :title="(actionType==='ADD'&&'新增操作员') || (actionType==='EDIT'&&'编辑操作员') || ''"
             append-to-body width="600px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="登录名" prop="loginName">
        <el-input v-model="form.loginName" :disabled="actionType!=='ADD'" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="登录密码" prop="loginPwd" v-if="actionType==='ADD'">
        <el-input v-model="form.loginPwd" placeholder="登录密码" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="确认密码" prop="loginPwd2" v-if="actionType==='ADD'">
        <el-input v-model="form.loginPwd2" placeholder="确认登录密码" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="姓名" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="手机号" prop="mobileNo">
        <el-input v-model="form.mobileNo" placeholder="手机号" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="角色" prop="roleIds" v-if="form.type===2">
        <el-checkbox-group v-model="form.roleIds" style="width: 450px;">
          <el-checkbox v-for="role in allRoles" :label="role.id" :key="role.id">{{role.roleName}}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" placeholder="描述" style="width: 450px;"/>
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
        loading: false,
        show: false,
        actionType: undefined,
        allRoles: [],
        form: {
          roleIds: []
        },
        rules: {
          loginName: [{required: true, message: '请输入登录名', trigger: 'blur'}],
          loginPwd: [{required: true, message: '请输入密码', trigger: 'blur'}],
          loginPwd2: [{required: true, message: '请输入确认密码', trigger: 'blur'}],
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
            this.actionType === 'ADD' && this.doAdd()
            this.actionType === 'EDIT' && this.doEdit()
          }
        })
      },
      doAdd() {
        request.post('/pmsOperator/addPmsOperator', this.form)
          .then(({data}) => {
            this.$message(data)
            this.closeForm()
            this.$emit('success')
          })
          .finally(() => {
            this.loading = false;
          })
      },
      doEdit() {
        request.post('/pmsOperator/editPmsOperator', this.form)
          .then(({data}) => {
            this.$message(data)
            this.closeForm()
            this.$emit('success')
          })
          .finally(() => {
            this.loading = false;
          })
      },
      closeForm() {
        this.show = false
        this.loading = false
        this.allRoles = []
        this.form = {}
        this.$refs.form.resetFields()
      }
    }
  }
</script>
