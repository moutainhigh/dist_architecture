<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" :title="(actionType==='ADD'&&'新增功能') || (actionType==='EDIT'&&'编辑功能')||''" append-to-body
             width="600px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="父功能" v-if="actionType==='ADD'">
        <el-input v-model="form.parentName" :disabled="true" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="功能名称" prop="name">
        <el-input v-model="form.name" placeholder="名称" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="功能编号" prop="number">
        <el-input v-model="form.number" placeholder="编号" style="width: 450px;"/>
      </el-form-item>
      <el-form-item label="权限标识" prop="permissionFlag">
        <el-input v-model="form.permissionFlag" placeholder="权限标识" style="width: 450px;" :disabled="actionType!=='ADD'"/>
      </el-form-item>
      <el-form-item label="功能类型" prop="functionType">
        <el-radio-group v-model="form.functionType" :disabled="actionType!=='ADD'" @change="value=>{value===2&&(form.url=undefined)}">
          <el-radio v-model="form.functionType" :label="1">菜单项</el-radio>
          <el-radio v-model="form.functionType" :label="2">操作项</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="URL地址" prop="url">
        <el-input v-model="form.url" placeholder="URL" style="width: 450px;" :disabled="form.functionType===2"/>
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
        form: {},
        rules: {
          name: [{required: true, message: '请输入功能名称', trigger: 'blur'}],
          number: [{required: true, message: '请输入功能编号,必须为纯数字', trigger: 'blur', pattern: /^[0-9]*$/}],
          permissionFlag: [{required: true, message: '请输入权限标识', trigger: 'blur'}],
          functionType: [{required: true, message: '请选择功能类型'}],
          url: [{
            required: true,
            validator: (rule, value, callback) => {
              if (this.form.functionType === 1 && !value) {
                callback(new Error('请输入URL地址'))
              } else {
                callback()
              }
            }
          }],

        }
      }
    },
    methods: {
      doSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true
            this.actionType === 'ADD' && this.doAdd();
            this.actionType === 'EDIT' && this.doEdit();
          }
        })
      },
      doAdd() {
        request.post('/pmsFunction/addPmsFunction', this.form)
          .then(({data}) => {
            this.$message.success(data)
            this.closeForm()
            this.$emit('success')
          })
      },
      doEdit() {
        request.post('/pmsFunction/editPmsFunction', this.form)
          .then(({data}) => {
            this.$message.success(data)
            this.closeForm()
            this.$emit('success')
          })
      },
      closeForm() {
        this.show = false
        this.loading = false
        this.form = {}
        this.$refs.form.resetFields()
      }
    }
  }
</script>
