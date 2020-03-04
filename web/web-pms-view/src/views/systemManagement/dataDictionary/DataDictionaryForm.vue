<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" :title="(actionType==='ADD'&&'新增数据字典')||(actionType==='EDIT'&&'编辑数据字典')||''"
             append-to-body width="800px">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
      <el-form-item label="数据名称" prop="dataName">
        <el-input v-model="form.dataName" placeholder="数据名称" style="width: 450px;" :disabled="actionType!=='ADD'"/>
      </el-form-item>

      <el-form-item label="字典信息" prop="dataInfo">
        <el-table :data="form.dataInfo" max-height="400" border row-key="id">

          <el-table-column label="数据标识" width="300px" v-slot="{row}">
            <el-input v-model="row.flag" size="small"/>
          </el-table-column>

          <el-table-column label="数据编码" width="100px" v-slot="{row}">
            <el-input v-model="row.code" size="small"/>
          </el-table-column>

          <el-table-column label="数据描述" width="120px" v-slot="{row}">
            <el-input v-model="row.desc" size="small"/>
          </el-table-column>

          <el-table-column label="操作" width="50px" v-slot="{row}">
            <el-button type="text" size="small" @click="form.dataInfo.splice(form.dataInfo.indexOf(row),1)">删除</el-button>
          </el-table-column>

        </el-table>
        <div style="margin-top: 5px">
          <el-button @click="form.dataInfo.push({})">添加</el-button>
        </div>

      </el-form-item>

      <el-form-item label="系统标识" prop="systemType">
        <el-select v-model="form.systemType">
          <el-option v-for="{code,desc} in $dict('SystemTypeEnum')" :key="parseInt(code)" :label="desc" :value="parseInt(code)"/>
        </el-select>
      </el-form-item>

      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" placeholder="描述" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" style="width: 450px;"/>
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
        systemTypeEnumOptions: [],
        form: {
          id: '',
          dataName: '',
          dataInfo: undefined,
          remark: '',
          systemType: ''
        },
        rules: {
          dataName: [{required: true, message: '请输入数据名称', trigger: 'blur'}],
          systemType: [{required: true, message: '请选择系统标识'}],
          remark: [{required: true, message: '请输入描述信息'}]
        }
      }
    },
    methods: {
      doSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true
            this.actionType === 'ADD' && this.doAdd()
            this.actionType === "EDIT" && this.doEdit()
          }
        })
      },
      doAdd() {
        request.post('/dataDictionary/addDataDictionary', this.form)
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
        request.post('/dataDictionary/editDataDictionary', this.form)
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
        this.form = {}
        this.$refs.form.resetFields()
      }
    }
  }
</script>
