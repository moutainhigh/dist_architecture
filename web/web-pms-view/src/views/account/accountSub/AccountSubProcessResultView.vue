<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="700px"
             :title="'账务处理结果详情'">
    <el-form ref="form" :inline="true" :model="processResult" size="small" label-width="100px">


      <el-form-item label="创建时间">
        <el-input v-model="processResult.createTime" disabled/>
      </el-form-item>

      <el-form-item label="账务处理流水号">
        <el-input v-model="processResult.accountProcessNo" disabled/>
      </el-form-item>

      <el-form-item label="处理结果">
        <el-input :value="processResult.processResult===1?'成功':processResult.processResult===-1?'失败':''" disabled/>
      </el-form-item>

      <el-form-item label="错误码">
        <el-input :value="processResult.errorCode" disabled/>
      </el-form-item>

      <el-form-item label="是否异步">
        <el-input :value="processResult.isFromAsync===1?'是':processResult.isFromAsync===-1?'否':''" disabled/>
      </el-form-item>

      <el-form-item label="审核阶段">
        <el-input :value="$dictCode('AccountProcessResultAuditStageEnum',processResult.auditStage).desc" disabled/>
      </el-form-item>

      <el-form-item label="回调阶段">
        <el-input :value="$dictCode('AccountProcessResultCallbackStageEnum',processResult.callbackStage).desc" disabled/>
      </el-form-item>

      <el-form-item label="请求信息">
        <el-input type="textarea" style="width: 500px;" v-model="processResult.requestDto" rows="3" disabled/>
      </el-form-item>

      <el-form-item label="处理的数据">
        <el-input type="textarea" style="width: 500px;" v-model="processResult.processDtoList" rows="5" disabled/>
      </el-form-item>

      <el-form-item label="备注">
        <el-input type="textarea" style="width: 500px" :value="processResult.remark" rows="5" disabled/>
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
        processResult: {},
      }
    },
    methods: {
      closeForm() {
        this.show = false
        this.processResult = {}
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
