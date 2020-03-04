<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="700px"
             :title="(actionType==='ADD'&&'新增定时任务')||(actionType==='EDIT'&&'编辑定时任务')||(actionType==='VIEW'&&'查看定时任务')||''">
    <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">

      <el-form-item label="任务类型" prop="jobType">
        <el-select v-model="form.jobType" :disabled="actionType!=='ADD'" clearable>
          <el-option v-for="{code,desc} in $dict('ScheduleJobTypeEnum')" :label="desc" :key="parseInt(code)" :value="parseInt(code)"/>
        </el-select>
      </el-form-item>

      <el-form-item label="创建时间" prop="createTime" v-show="actionType!=='ADD'">
        <el-input v-model="form.createTime" disabled/>
      </el-form-item>

      <el-form-item label="任务分组" prop="jobGroup">
        <el-input v-model="form.jobGroup" :disabled="actionType!=='ADD'"/>
      </el-form-item>

      <el-form-item label="任务名称" prop="jobName">
        <el-input v-model="form.jobName" :disabled="actionType!=='ADD'"/>
      </el-form-item>

      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker type="datetime" value-format="yyyy-MM-dd HH:mm:ss" v-model="form.startTime" :disabled="actionType!=='ADD'"/>
      </el-form-item>

      <el-form-item label="任务间隔" prop="intervals" v-if="form.jobType===parseInt($dictFlag('ScheduleJobTypeEnum','SIMPLE_JOB').code)">
        <el-input v-model="form.intervals" class="input-with-select" :disabled="actionType==='VIEW'">
          <el-select v-model="form.intervalUnit" slot="append" placeholder="请选择" :disabled="actionType==='VIEW'" style="width: 80px;">
            <el-option :key="1" :value="1" :label="'毫秒'"/>
            <el-option :key="2" :value="2" :label="'秒'"/>
            <el-option :key="3" :value="3" :label="'分'"/>
            <el-option :key="4" :value="4" :label="'小时'"/>
          </el-select>
        </el-input>

      </el-form-item>


      <el-form-item label="corn表达式" prop="cronExpression" v-if="form.jobType===parseInt($dictFlag('ScheduleJobTypeEnum','CORN_JOB').code)">
        <el-input v-model="form.cronExpression" :disabled="actionType==='VIEW'"/>
      </el-form-item>

      <el-form-item label="消息目的地" prop="destination">
        <el-input v-model="form.destination" :disabled="actionType==='VIEW'"/>
      </el-form-item>

      <el-form-item label="任务描述" prop="jobDescription">
        <el-input v-model="form.jobDescription" :disabled="actionType==='VIEW'"/>
      </el-form-item>

      <el-form-item label="已执行次数" prop="executedTimes" v-show="actionType!=='ADD'">
        <el-input v-model="form.executedTimes" disabled/>
      </el-form-item>

      <el-form-item label="上次执行时间" prop="lastExecuteTime" v-show="actionType!=='ADD'">
        <el-input v-model="form.lastExecuteTime" disabled/>
      </el-form-item>

      <el-form-item label="下次执行时间" prop="nextExecuteTime" v-show="actionType!=='ADD'">
        <el-input v-model="form.nextExecuteTime" disabled/>
      </el-form-item>

      <el-form-item label="任务状态" prop="jobStatus" v-show="actionType!=='ADD'">
        <el-input v-model="form.jobStatus" disabled/>
      </el-form-item>

      <el-form-item label="任务参数" prop="paramJson">
        <el-input type="textarea" style="width: 400px;" rows="5" v-model="form.paramJson" :disabled="actionType==='VIEW'"/>
      </el-form-item>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="closeForm">取消</el-button>
      <el-button :loading="loading" type="primary" @click="doSubmit" v-show="actionType!=='VIEW'">确认</el-button>
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
        form: {},
        rules: {
          jobType: [{required: true, message: '请选择任务类型',}],
          jobGroup: [{required: true, message: '请输入任务分组',}],
          jobName: [{required: true, message: '请输入任务名称',}],
          startTime: [{required: true, message: '请输入开始时间'}],
          intervals: [{required: true, message: '请输入任务间隔'}],
          cronExpression: [{required: true, message: '请输入corn表达式',}],
          destination: [{required: true, message: '请输入消息地址',}],
          jobDescription: [{required: true, message: '请输入任务描述'}],
          paramJson: [{required: true, message: '请输入任务描述'}]
        }
      }
    },
    methods: {
      doSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.loading = true
            this.actionType === 'ADD' ? this.doAdd() : this.actionType === 'EDIT' ? this.doEdit() : ''
          }
        })
      },
      doAdd() {
        request.post('/timer/addScheduleJob', this.form)
          .then(({data}) => {
            this.$message.success(data)
            this.closeForm()
            this.$emit('success')
          })
          .finally(() => {
            this.loading = false;
          })
      },
      doEdit() {
        request.post('/timer/editScheduleJob', this.form)
          .then(({data}) => {
            this.$message.success(data)
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

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-input, .el-select {
    width: 200px;
  }
</style>
