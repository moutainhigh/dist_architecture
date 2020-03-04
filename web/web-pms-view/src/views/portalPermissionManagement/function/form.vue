<template>
  <el-dialog ref="dialog" :visible.sync="dialog" :close-on-click-modal="false" :before-close="closeForm" :title="isAdd ? '新增菜单' : '编辑菜单'" append-to-body width="600px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="父功能" v-if="isAdd">
        <el-input v-model="form.parentName" :disabled="true" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="功能名称" prop="name">
        <el-input v-model="form.name" placeholder="名称" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="功能编号" prop="number">
        <el-input v-model="form.number" placeholder="编号" style="width: 450px;"></el-input>
      </el-form-item>
      <el-form-item label="权限标识" prop="permissionFlag">
        <el-input v-model="form.permissionFlag" placeholder="权限标识" style="width: 450px;" :readonly="!isAdd"></el-input>
      </el-form-item>
      <el-form-item label="功能类型" prop="functionType">
        <el-radio-group v-model="form.functionType" :disabled="!isAdd" @change="value=>{value===2?(form.url=undefined):undefined}">
          <el-radio v-model="form.functionType" :label="1">菜单项</el-radio>
          <el-radio v-model="form.functionType" :label="2">操作项</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="URL地址" prop="url">
        <el-input v-model="form.url" placeholder="URL" style="width: 450px;" :readonly="form.functionType===2"></el-input>
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
                form: {
                    id: undefined,
                    parentId: undefined,
                    parentName: undefined,
                    name: undefined,
                    number: undefined,
                    permissionFlag: undefined,
                    functionType: undefined,
                    url: undefined
                },
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
                        this.isAdd ? this.doAdd() : this.doEdit()
                    }
                })
            },
            doAdd() {
                request.post('/portalFunction/portalFunctionAdd/', this.form)
                    .then(({data}) => {
                        this.$message(data)
                        this.closeForm()
                        this.$emit('success')
                    })
            },
            doEdit() {
                request.post('/portalFunction/portalFunctionEdit/', this.form)
                    .then(({data}) => {
                        this.$message(data)
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
