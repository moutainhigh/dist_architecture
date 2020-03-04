<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">
      <div class="search-item">
        <label>任务类型</label>
        <el-select v-model="searchParam.jobType" clearable>
          <el-option v-for="{code,desc} in $dict('ScheduleJobTypeEnum')" :label="desc" :key="parseInt(code)" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="search-item">
        <label>任务分组</label>
        <el-input v-model="searchParam.jobGroup"/>
      </div>

      <div class="search-item">
        <label>任务名称</label>
        <el-input v-model="searchParam.jobName"/>
      </div>

      <div class="search-item">
        <label>任务状态</label>
        <el-select v-model="searchParam.jobStatus" clearable>
          <el-option v-for="{code,desc} in $dict('ScheduleJobStatusEnum')" :label="desc" :key="code" :value="code"/>
        </el-select>
      </div>

      <div class="search-item">
        <label>任务描述</label>
        <el-input v-model="searchParam.jobDescription"/>
      </div>

      <div class="function-bar">
        <el-button class="el-button--primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
        <el-button plain @click="addScheduleJob" v-permission="'timer:schedule:add'"><i class="el-icon-plus"/>新增任务</el-button>
        <el-button plain @click="viewInstanceList" v-permission="'timer:instance:view'"><i class="el-icon-edit"/>实例管理</el-button>
        <el-button plain @click="viewOpLogList('','')" v-permission="'timer:opLog:view'"><i class="el-icon-view"/>操作日志</el-button>
      </div>
    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" max-height="300" border row-key="id">
        <el-table-column>
          <template v-slot:header="{}"> 任务分组<br/>任务名称</template>
          <template v-slot="{row}"> {{row.jobGroup}}<br/>{{row.jobName}}</template>
        </el-table-column>

        <el-table-column>
          <template v-slot:header="{}">上次执行时间<br/>下次执行时间</template>
          <template v-slot:default="{row}">{{row.lastExecuteTime}}<br/>{{row.nextExecuteTime}}</template>
        </el-table-column>

        <el-table-column label="任务类型" :formatter="row=>$dictCode('ScheduleJobTypeEnum',row.jobType).desc"/>
        <el-table-column label="任务状态" :formatter="row=>$dictCode('ScheduleJobStatusEnum',row.jobStatus).desc" :cell-style="jobStatusColumnStyle"/>

        <el-table-column :formatter="row=>
                         row.jobType===parseInt($dictFlag('ScheduleJobTypeEnum','SIMPLE_JOB').code)?
                         row.intervals+(row.intervalUnit===1?'(毫秒)':row.intervalUnit===2?'(秒)':row.intervalUnit===3?'(分)':row.intervalUnit===4?'(小时)':'')
                         :row.cronExpression">
          <template v-slot:header>间隔时间/<br/>corn表达式</template>
        </el-table-column>
        <el-table-column label="消息目的地" prop="destination"/>
        <el-table-column label="任务描述" prop="jobDescription"/>
        <el-table-column label="已执行次数" prop="executedTimes"/>

        <el-table-column label="操作" width="280px" v-slot="{row}">
          <el-button type="text" size="small" @click="viewScheduleJob(row)" v-permission="'timer:schedule:view'">查看</el-button>
          <el-button type="text" size="small" @click="changeScheduleJobStatus(row)" v-permission="'timer:schedule:edit'">
            {{row.jobStatus === 'PAUSED' ? '恢复' : '暂停'}}
          </el-button>
          <el-button v-if="row.jobStatus !== 'PAUSED'" type="text" size="small" @click="triggerScheduleJob(row)" v-permission="'timer:schedule:trigger'">触发</el-button>
          <el-button v-if="row.jobStatus !== 'PAUSED'" type="text" size="small" @click="notifyScheduleJob(row)" v-permission="'timer:schedule:trigger'">通知</el-button>
          <el-button type="text" size="small" @click="editScheduleJob(row)" v-permission="'timer:schedule:edit'">编辑</el-button>
          <el-button type="text danger" size="small" @click="deleteScheduleJob(row)" v-permission="'timer:schedule:delete'">删除</el-button>
          <el-button type="text" size="small" @click="viewOpLogList(row.jobGroup, row.jobName)" v-permission="'timer:opLog:view'">日志</el-button>
        </el-table-column>
      </el-table>
    </div>

    <div class="main-page-footer">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[15,30,50,100]"
        :total="pageResult.totalRecord"
        :page-size.sync="pageParam.pageSize"
        :current-page.sync="pageParam.pageCurrent"
        @size-change="refreshList"
        @current-change="refreshList"/>
    </div>

    <schedule-job-form ref="form" v-on:success="refreshList"/>
    <instance-list ref="instanceList"/>
    <op-log-list ref="opLogList"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import ScheduleJobForm from "./ScheduleJobForm";
  import InstanceList from "./InstanceList";
  import OpLogList from "./OpLogList";

  export default {
    name: "ScheduleJobList",
    components: {
      OpLogList,
      InstanceList,
      ScheduleJobForm,
    },
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 30},
        pageResult: {},
        loading: false,
      };
    },
    methods: {
      refreshList() {
        this.loading = true
        request
          .post(`/timer/listScheduleJob`, this.searchParam, {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      addScheduleJob() {
        this.$refs.form.form = {};
        this.$refs.form.actionType = "ADD";
        this.$refs.form.show = true;
      },
      viewInstanceList() {
        this.$refs.instanceList.show = true;
        this.$refs.instanceList.registerTimer();
      },
      viewScheduleJob(row) {
        request.post('/timer/getScheduleJobInfo', `jobGroup=${row.jobGroup}&jobName=${row.jobName}`)
          .then(({data}) => {
            this.$refs.form.actionType = 'VIEW'
            this.$refs.form.form = data;
            this.$refs.form.show = true;
          })
      },
      editScheduleJob(row) {
        request.post('/timer/getScheduleJobInfo', `jobGroup=${row.jobGroup}&jobName=${row.jobName}`)
          .then(({data}) => {
            this.$refs.form.actionType = 'EDIT'
            this.$refs.form.form = data;
            this.$refs.form.show = true;
          })
      },
      deleteScheduleJob(row) {
        this.$confirm("确定删除吗?")
          .then(() => request.post("/timer/deleteScheduleJob", `jobGroup=${row.jobGroup}&jobName=${row.jobName}`))
          .then(
            ({data}) => {
              this.$message.success(data)
              this.refreshList();
            }
          );
      },
      triggerScheduleJob(row) {
        this.$confirm('确定立即执行一次此定时任务吗?')
          .then(() => request.post("/timer/triggerScheduleJob", `jobGroup=${row.jobGroup}&jobName=${row.jobName}`))
          .then(({data}) => {
            this.$message.success(data)
            this.refreshList();
          })
      },
      notifyScheduleJob(row) {
        this.$confirm('确定直接发送MQ通知吗?')
          .then(() => request.post("/timer/notifyScheduleJob", `jobGroup=${row.jobGroup}&jobName=${row.jobName}`))
          .then(({data}) => {
            this.$message.success(data)
            this.refreshList();
          })
      },
      changeScheduleJobStatus(row) {
        let newStatus = row.jobStatus === "NORMAL" ? "PAUSED" : "NORMAL"
        this.$confirm(`确定${newStatus === 'NORMAL' ? '恢复' : '暂停'}定时任务吗?`)
          .then(() => request.post("/timer/changeScheduleJobStatus", `jobGroup=${row.jobGroup}&jobName=${row.jobName}&newStatus=${newStatus}`))
          .then(
            ({data}) => {
              this.$message.success(data)
              this.refreshList();
            }
          );
      },
      jobStatusColumnStyle(row) {
        console.log(row);
        if (row.jobStatus === 'NORMAL') {
          return '{color: green;}';
        } else if (row.jobStatus === 'PAUSED') {
          return '{color: red;}';
        } else {
          return '';
        }
      },
      viewOpLogList(jobGroup, jobName) {
        this.$refs.opLogList.show = true;
        this.$refs.opLogList.genObjKeyForJob(jobGroup, jobName);
        this.$refs.opLogList.refreshList();
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>

