<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" @close="closeForm" :title="(actionType==='ADD'&&'新增角色')||(actionType==='EDIT'&&'编辑角色')||''">
    <el-form ref="form" label-position="left" :rules="rules" :model="form">
      <el-form-item prop="roleName" label="角色名称" label-width="100px">
        <el-input v-model="form.roleName" :disabled="actionType!=='ADD'" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="remark" label="描述" label-width="100px">
        <el-input v-model="form.remark" autocomplete="off"/>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="closeForm">取 消</el-button>
      <el-button :loading="loading" type="primary" @click="doSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import request from "@/utils/request";

  export default {
    name: "RoleForm",
    data() {
      return {
        show: false,
        loading: false,
        actionType: undefined,
        form: {},
        rules: {
          roleName: [{required: true, message: "角色名称不能为空"}]
        },
      };
    },
    methods: {
      doSubmit() {
        this.actionType === 'ADD' && this.doAdd();
        this.actionType === 'EDIT' && this.doEdit();
      },

      doAdd() {
        this.$refs.form.validate(valid => {
          if (valid) {
            this.loading = true;
            request
              .post("/pmsRole/addPmsRole", this.form)
              .then(response => {
                this.closeForm();
                this.$message.success(response.data);
                this.$emit("success");
              })
              .finally(() => {
                this.loading = false;
              });
          }
        });
      },
      doEdit() {
        this.$refs.form.validate(valid => {
          if (valid) {
            this.loading = true;
            request
              .post("/pmsRole/editPmsRole", this.form)
              .then(response => {
                this.closeForm();
                this.$message.success(response.data);
                this.$emit("success");
              })
              .finally(() => {
                this.loading = false;
              });
          }
        });
      },
      closeForm() {
        this.show = false;
        this.loading = false;
        this.form = {};
        this.$refs.form.resetFields();
      }
    }
  };
</script>

<style scoped>
  /deep/ .el-dialog {
    width: 450px;
  }

  .el-input {
    width: 300px;
  }
</style>
