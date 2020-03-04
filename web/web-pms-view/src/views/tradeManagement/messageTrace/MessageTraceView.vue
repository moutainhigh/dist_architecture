<template>
  <el-dialog :visible.sync="show" :close-on-click-modal="false" :before-close="closeForm" append-to-body width="700px"
             :title="'通知响应日志'">
    <el-table border :data="notifyLogs" @expand-change="expandChange" :span-method="spanMethod" row-key="id" :tree-props="{children: 'children'}">
      <el-table-column label="响应方" prop="groupName" width="250px"/>
      <el-table-column label="类别" v-slot="{row}" width="100px">
        <span v-if="!row.parent">最新响应</span>
        <span v-else>历史响应</span>
      </el-table-column>
      <el-table-column label="响应时间" prop="consumeTime" width="150px"/>
      <el-table-column label="响应状态" v-slot="{row}" width="150px">
        <span v-if="row.consumeStatus===true" style="color: green">响应成功</span>
        <span v-else-if="row.consumeStatus===false" style="color:red">响应失败</span>
      </el-table-column>
    </el-table>
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
        traceEntity: {},
      }
    },
    computed: {
      notifyLogs() {
        let groupConsumeList = [];
        let id = 1;
        for (let groupName in this.traceEntity.groupConsumeInfos) {
          let traceConsumeList = this.traceEntity.groupConsumeInfos[groupName];
          let item = {
            id: id++,
            groupName: groupName,
            consumeTime: traceConsumeList[0].consumeTime,
            consumeStatus: traceConsumeList[0].consumeStatus,
            isExpand: false
          };
          groupConsumeList.push(item);
          if (traceConsumeList.length > 1) {
            item.children = [];
            traceConsumeList.slice(1).forEach(p => {
              item.children.push({
                id: id++,
                groupName: groupName,
                consumeTime: p.consumeTime,
                consumeStatus: p.consumeStatus,
                parent: item
              });
            });
          }
        }
        ;
        return groupConsumeList;
      }
    },
    methods: {
      spanMethod({row, columnIndex}) {
        if (columnIndex === 0 && row.children && row.isExpand) {
          return [row.children.length + 1, 1];
        } else if (columnIndex === 0 && row.parent) {
          return [0, 0];
        } else if (columnIndex === 1 && row.parent) {
          if (row === row.parent.children[0]) {
            return [row.parent.children.length, 1];
          } else {
            return [0, 0];
          }
        } else {
          return [1, 1];
        }
      },
      expandChange(expandRows, expanded) {
        expandRows.isExpand = expanded;
      },
      closeForm() {
        this.show = false;
        this.traceEntity = {};
      }
    }
  }
</script>

