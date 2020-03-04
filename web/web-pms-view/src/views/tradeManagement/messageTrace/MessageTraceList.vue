<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">

      <div class="search-item">
        <label>业务流水号</label>
        <el-input v-model="searchParam.trxNo"/>
      </div>

      <div class="search-item">
        <label>消息发送时间</label>
        <el-date-picker
          v-model="pubTimeRange"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :clearable="false">
        </el-date-picker>
      </div>

      <div class="function-bar">
        <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
      </div>
    </div>

    <div class="main-page-content">
      <el-table :data="pageResult" border row-key="id">

        <el-table-column type="index" label="序号"/>

        <el-table-column width="140" label="发送时间" property="pub.pubTime"/>

        <el-table-column label="流水号" property="key"/>

        <el-table-column label="发送方" property="pub.pubGroupName"/>

        <el-table-column label="通知状态" width="70">成功</el-table-column>

        <el-table-column>
          <template v-slot:header="{}">响应方</template>
          <template v-slot="{row}">
            <span v-for="(consumeList,groupName) in row.groupConsumeInfos" :key="groupName">{{groupName}}<br/></span>
          </template>
        </el-table-column>

        <el-table-column>
          <template v-slot:header="{}">响应状态</template>
          <template v-slot="{row}">
            <span v-for="(consumeList,groupName) in row.groupConsumeInfos" :key="groupName">
                <span v-if="consumeList[0].consumeStatus===true" style="color: green">响应成功</span>
                <span v-else-if="consumeList[0].consumeStatus===false" style="color:red">响应失败</span>
              <br/>
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200px;">
          <template v-slot:default="{row}">
            <el-button type="text" size="small" @click="viewTraceInfoPage(row)" v-permission="'message:trace:view'">
              查看响应日志
            </el-button>
            <el-button type="text" size="small" @click="viewMessageContentPage(row)" v-permission="'message:trace:view'">
              查看报文
            </el-button>
            <el-button type="text" size="small" @click="resendNotify(row)" v-permission="'message:trace:resend'">补发
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <message-content-view ref="messageContentView"/>
    <message-trace-view ref="messageTraceView"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import MessageContentView from "./MessageContentView";
  import MessageTraceView from "./MessageTraceView";
  import dayjs from "dayjs";

  export default {
    name: "MessageTraceList",
    components: {
      MessageContentView,
      MessageTraceView
    },
    data() {
      return {
        searchParam: {},
        pubTimeRange: [dayjs(new Date().getTime() - 1000 * 60 * 60 * 24).format("YYYY-MM-DD HH:mm:ss"), dayjs(new Date(new Date().getTime() + 1000 * 60)).format("YYYY-MM-DD HH:mm:ss")],
        pageResult: {},
        loading: false,
      };
    },
    methods: {
      refreshList() {
        this.loading = true
        request
          .post(`messageTrace/listMessageTrace`,
            Object.assign(this.searchParam, {
              pubTimeBegin: this.pubTimeRange[0],
              pubTimeEnd: this.pubTimeRange[1]
            }),
            {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      viewTraceInfoPage(row) {
        request.post("messageTrace/getTraceDetail", `msgId=${row.msgId}`)
          .then(({data}) => {
            this.$refs.messageTraceView.traceEntity = data;
            this.$refs.messageTraceView.show = true;
          })
      },
      viewMessageContentPage(row) {
        request.post("messageTrace/getMessageContent", `topic=${row.pub.topic}&msgId=${row.msgId}`)
          .then(({data}) => {
            this.$refs.messageContentView.msgDto = data;
            this.$refs.messageContentView.show = true;
          });
      },
      resendNotify(row) {
        this.$confirm('确定补发通知吗?')
          .then(() => request.post("messageTrace/resendNotify", `notifyId=${row.id}`))
          .then(({data}) => {
            this.$message.success(data)
            this.refreshList();
          })
      },
    }
  };
</script>

