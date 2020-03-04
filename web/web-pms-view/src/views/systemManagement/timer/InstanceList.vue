<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="800px"
             :title="'实例列表'">
    <div v-loading="loading" element-loading-text="刷新中" element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)">
      <div class="tag-group">
        <span class="tag-group__title">分区操作：</span>
        <el-tag
          v-for="namespace in namespaceList"
          :key="namespace.code"
          :type="''"
          effect="plain">
          {{ namespace.name }}

          <el-button v-if="namespace.status === 1" type="danger" size="small" @click="changeNamespaceStatus(namespace.code, namespace.status)" v-permission="'timer:instance:edit'">
            <span>挂起</span>
          </el-button>
          <el-button v-if="namespace.status === 2" type="primary" size="small" @click="changeNamespaceStatus(namespace.code, namespace.status)"
                     v-permission="'timer:instance:edit'">
            <span>恢复</span>
          </el-button>
        </el-tag>
      </div>

      <br>

      <el-table :data="entityList" max-height="500" border row-key="id">
        <el-table-column label="实例ID" prop="instanceId" min-width="120px" align="left"/>
        <el-table-column label="实例状态" align="center">
          <template v-slot:default="{row}">
            <span v-if="row.status === 1" style="color: green">正常</span>
            <span v-else-if="row.status === 2" style="color:red">挂起中</span>
            <span v-else>{{row.status}}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后检入时间" align="center" min-width="110px">
          <template v-slot:default="{row}">
            <span v-if="isNotCheckInLongTime(row.updateTime)" style="color: red">{{row.updateTime}}</span>
            <span v-else>{{row.updateTime}}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" align="center" min-width="110px"/>
        <el-table-column label="分区名称" prop="namespaceName" align="center"/>
        <el-table-column label="分区状态" align="center">
          <template v-slot:default="{row}">
            <span v-if="row.namespaceStatus === 1" style="color: green">正常</span>
            <span v-else-if="row.namespaceStatus === 2" style="color:red">挂起中</span>
            <span v-else>{{row.namespaceStatus}}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="closeForm">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import request from '@/utils/request'

  export default {
    name: "instanceList",
    data() {
      return {
        loading: false,
        show: false,
        entityList: [],
        namespaceList: [],
        timer: {},
      }
    },
    beforeDestroy() {//页面关闭前关闭定时器
      this.clearTimer();
    },
    methods: {
      refreshList() {
        this.loading = true
        request.post(`/timer/listInstance`, null, null)
          .then(({data}) => {
            this.entityList = data;
            if (!this.entityList) {
              return;
            }

            //构建namespace对象
            let namespaceSet = new Set();
            this.namespaceList = [];
            this.entityList.forEach(entity => {
              if (!namespaceSet.has(entity.namespaceName)) {
                namespaceSet.add(entity.namespaceName);
                let obj = {};
                obj.code = entity.namespace;
                obj.name = entity.namespaceName;
                obj.status = entity.namespaceStatus;
                this.namespaceList.push(obj);
              }
            });
          }).finally(() => {
          this.loading = false
        });
      },
      registerTimer() {
        this.refreshList();//先立即刷新一次，之后就交由定时任务来刷新
        this.timer = setInterval(() => this.refreshList(), 3000);
      },
      clearTimer() {
        clearInterval(this.timer)
      },
      changeNamespaceStatus(namespace, namespaceStatus) {
        let msg = '';
        if (namespaceStatus === 1) {
          msg = '挂起当前分区';
        } else if (namespaceStatus === 2) {
          msg = '恢复当前分区';
        } else {
          return false;
        }

        this.$confirm(`确定${msg}吗?`)
          .then(() => {
            request.post(
              `/timer/changeNamespaceStatus`,
              null,
              {
                params: {namespace: namespace, namespaceStatus: namespaceStatus}
              }).then(({data}) => {
              if (data === true) {
                this.$message({
                  showClose: true,
                  message: '操作成功，分区状态同步到实例可能需几秒时间',
                  type: 'success'
                });
                this.refreshList();
              } else {
                this.$message({
                  showClose: true,
                  message: '操作失败!',
                  type: 'error'
                });
              }
            });
          });
      },
      isNotCheckInLongTime(checkInTime) {
        checkInTime = checkInTime.substring(0, 19);
        checkInTime = checkInTime.replace(/-/g, '/');
        let timeCheckIn = parseInt("" + (new Date(checkInTime).getTime() / 1000));
        let timeNow = parseInt("" + (new Date().getTime() / 1000));
        if (timeNow - timeCheckIn > (2 * 60)) { //最后检入时间超过2分钟，则认为是长时间无检入
          return true;
        } else {
          return false;
        }
      },
      closeForm() {
        this.show = false
        this.loading = false
        this.clearTimer()//清除定时器
      }
    }
  }
</script>
