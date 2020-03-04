<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="700px"
             :title="'通知内容'">
    <el-form ref="form" :inline="true" :model="form" size="small" label-width="100px">


      <el-form-item label="TOPIC">
        <el-input v-model="msgDto.topic" disabled/>
      </el-form-item>

      <el-form-item label="TAG">
        <el-input v-model="msgDto.tags" disabled/>
      </el-form-item>

      <el-form-item label="消息事件类型">
        <el-input v-model="msgDto.eventType" disabled/>
      </el-form-item>

      <el-form-item label="业务流水号">
        <el-input v-model="msgDto.trxNo" disabled/>
      </el-form-item>

      <el-form-item label="通知内容">
        <el-input type="textarea" style="width: 500px;" rows="20" v-model="msgDto.jsonParam" disabled/>
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
        msgDto: {},
      }
    },
    methods: {
      closeForm() {
        this.show = false;
        this.msgDto = {};
        this.$refs.form.resetFields();
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .el-input, .el-select {
    width: 200px;
  }
</style>
