<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="700px"
             :title="'待账务处理详情'">
    <el-form ref="form" :inline="true" :model="processPending" size="small" label-width="100px">


      <el-form-item label="创建时间">
        <el-input v-model="processPending.createTime" disabled/>
      </el-form-item>

      <el-form-item label="修改时间">
        <el-input v-model="processPending.modifyTime" disabled/>
      </el-form-item>

      <el-form-item label="账务处理流水号">
        <el-input v-model="processPending.accountProcessNo" disabled/>
      </el-form-item>

      <el-form-item label="处理阶段">
        <el-input :value="$dictCode('AccountProcessPendingStageEnum',processPending.processStage).desc" disabled/>
      </el-form-item>

      <el-form-item label="请求信息">
        <el-input type="textarea" style="width: 500px;" v-model="processPending.requestDto" rows="3" disabled/>
      </el-form-item>

      <el-form-item label="处理的数据">
        <el-input type="textarea" style="width: 500px;" v-model="processPending.processDtoList" rows="5" disabled/>
      </el-form-item>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="closeForm">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    data() {
      return {
        show: false,
        processPending: {},
      }
    },
    methods: {
      closeForm() {
        this.show = false
        this.processPending = {}
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
