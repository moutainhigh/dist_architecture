<template>
  <div class="main-page" v-loading="loading">
    <div class="main-page-header">

      <div class="search-item">
        <label>创建时间</label>
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"/>
      </div>

      <div class="search-item">
        <label>账务处理流水号</label>
        <el-input v-model="searchParam.accountProcessNo"/>
      </div>

      <div class="search-item">
        <label>商户号</label>
        <el-input v-model="searchParam.merchantNo" placeholder="须与创建时间一起使用"/>
      </div>

      <div class="search-item">
        <label>平台流水号</label>
        <el-input v-model="searchParam.trxNo" placeholder="须与创建时间一起使用"/>
      </div>

      <div class="search-item">
        <label>发送阶段</label>
        <el-select v-model="searchParam.callbackStage" clearable>
          <el-option v-for="{code,desc} in $dict('AccountProcessResultCallbackStageEnum')" :label="desc"
                     :key="parseInt(code)" :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="search-item">
        <label>审核阶段</label>
        <el-select v-model="searchParam.auditStage" clearable>
          <el-option v-for="{code,desc} in $dict('AccountProcessResultAuditStageEnum')" :label="desc" :key="parseInt(code)"
                     :value="parseInt(code)"/>
        </el-select>
      </div>

      <div class="search-item">
        <label>处理结果</label>
        <el-select v-model="searchParam.processResult" clearable>
          <el-option key="1" :value="1" label="成功"/>
          <el-option key="2" :value="-1" label="失败"/>
        </el-select>
      </div>

      <div class="search-item">
        <label>是否异步</label>
        <el-select v-model="searchParam.isFromAsync" clearable>
          <el-option key="1" :value="1" label="是"/>
          <el-option key="2" :value="-1" label="否"/>
        </el-select>
      </div>

      <div class="function-bar">
        <el-switch @change="onHistoryChange" v-model="inHistory" active-text="查询历史表" inactive-text="查询实时表"/>
        <el-button type="primary" @click="refreshList"><i class="el-icon-search"/>查询</el-button>
        <el-button @click="auditProcessResult({rows:selectedRow,auditType:parseInt($dictFlag('AccountResultAuditTypeEnum','AUDIT_TO_REPROCESS').code)})">
          批量审核为重新处理
        </el-button>
      </div>


    </div>

    <div class="main-page-content">
      <el-table :data="pageResult.data" border row-key="id" @selection-change="val=>selectedRow=val">
        <el-table-column type="selection"/>
        <el-table-column type="index" label="序号"/>

        <el-table-column label="创建时间" prop="createTime" width="130px"/>

        <el-table-column label="账务处理流水号" prop="accountProcessNo" width="200px"/>

        <el-table-column label="账务处理结果" prop="processResult" v-slot="{row}" width="100px">
          <span v-if="row.processResult===1" style="color: green;">成功</span>
          <span v-else-if="row.processResult===-1" style="color: red;">失败</span>
        </el-table-column>

        <el-table-column label="错误编码" prop="errorCode" width="80px"/>

        <el-table-column label="需要回调" width="80px"
                         :formatter="row=>row.requestDto&&JSON.parse(row.requestDto).callbackDestination?'是':'否'"/>

        <el-table-column label="异步账务" width="80px"
                         :formatter="row=>row.isFromAsync===1?'是':'否'"/>

        <el-table-column label="审核阶段" width="80px" v-slot="{row}">
          <span v-if="row.auditStage===parseInt($dictFlag('AccountProcessResultAuditStageEnum','AUDIT_NONE').code)" style="color: green;">不审核</span>
          <span v-else-if="row.auditStage===parseInt($dictFlag('AccountProcessResultAuditStageEnum','AUDIT_WAITING').code)" style="color: red;">待审核</span>
          <span v-else-if="row.auditStage===parseInt($dictFlag('AccountProcessResultAuditStageEnum','AUDIT_FINISHED').code)" style="color: gray">已审核</span>
        </el-table-column>


        <el-table-column label="发送阶段" width="80px" v-slot="{row}">
          <span v-if="row.callbackStage===parseInt($dictFlag('AccountProcessResultCallbackStageEnum','PENDING').code)" style="color: red;">待发送</span>
          <span v-else-if="row.callbackStage===parseInt($dictFlag('AccountProcessResultCallbackStageEnum','SENT').code)" style="color: green;">已发送</span>
          <span v-else-if="row.callbackStage===parseInt($dictFlag('AccountProcessResultCallbackStageEnum','NONE_SEND').code)" style="color: gray">不发送</span>
        </el-table-column>


        <el-table-column prop="remark" label="备注" show-overflow-tooltip/>

        <el-table-column label="操作" v-slot="{row}" width="190px">
          <el-button type="text" size="small" @click="viewAccountProcessResultPage(row)"
                     v-permission="'account:mch:result:view'">查看详情
          </el-button>

          <el-button type="text" size="small" @click="resendProcessResultCallback(row)"
                     v-if="(row.callbackStage===parseInt($dictFlag('AccountProcessResultCallbackStageEnum','PENDING').code)
                     ||row.callbackStage===parseInt($dictFlag('AccountProcessResultCallbackStageEnum','SENT').code))
                      && row.auditStage!==parseInt($dictFlag('AccountProcessResultAuditStage','AUDIT_WAITING').code)"
                     v-permission="'account:mch:result:callback'">发送通知
          </el-button>

          <el-dropdown size="small" placement="right" trigger="click" @command="auditProcessResult" v-permission="'account:mch:result:audit'"
                       v-if="row.auditStage===parseInt($dictFlag('AccountProcessResultAuditStageEnum','AUDIT_WAITING').code)"
                       style="margin-left: 10px;color: red;">
            <span class="el-dropdown-link" style="cursor: pointer;">
              审核<i class="el-icon-arrow-down el-icon--right"/>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="{code,desc} in $dict('AccountResultAuditTypeEnum')" :command="{rows:[row],auditType:parseInt(code)}">{{desc}}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

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

    <account-mch-process-result-view ref="processResultView"/>
  </div>
</template>

<script>
  import request from "@/utils/request";
  import AccountMchProcessResultView from './AccountMchProcessResultView'
  import {formatMoney} from "@/utils/commonUtil"
  import dayjs from "dayjs"

  export default {
    name: "AccountMchProcessResultList",
    components: {
      AccountMchProcessResultView
    },
    data() {
      return {
        searchParam: {},
        pageParam: {pageCurrent: 1, pageSize: 30},
        inHistory: false,
        createTimeRange: [],
        pageResult: {},
        selectedRow: [],
        loading: false,
      };
    },
    methods: {
      formatMoney,
      refreshList() {
        this.loading = true
        request
          .post(`accountMch/listProcessResult?inHistory=${this.inHistory}`,
            Object.assign(this.searchParam, this.createTimeRange ? {createTimeBegin: this.createTimeRange[0], createTimeEnd: this.createTimeRange[1]} : null),
            {params: this.pageParam})
          .then(({data}) => {
            this.pageResult = data;
          }).finally(() => {
          this.loading = false
        });
      },
      viewAccountProcessResultPage(row) {
        request.post(`accountMch/viewProcessResult?inHistory=${row.inHistory}`, `id=${row.id}`)
          .then(({data}) => {
            this.$refs.processResultView.processResult = data;
            this.$refs.processResultView.show = true;
          })
      },
      resendProcessResultCallback(row) {
        this.$confirm('确认发送账务处理结果通知吗?')
          .then(() => request.post(`accountMch/resendProcessResultCallback?inHistory=${row.inHistory}`, `id=${row.id}`))
          .then(({data}) => {
            this.$message.success(data);
          });
      },
      auditProcessResult({rows, auditType}) {
        if (!rows || rows.length === 0 || rows.find(row => row.auditStage !== parseInt(this.$dictFlag('AccountProcessResultAuditStageEnum', 'AUDIT_WAITING').code))) {
          this.$message.error("请选择需要审核的账务处理结果")
          return
        }
        this.$confirm(`确定将${rows.length}条账务处理结果审核为:" <span style="color: red;">${this.$dictCode('AccountResultAuditTypeEnum', auditType).desc}</span>" 吗?`, {dangerouslyUseHTMLString: true})
          .then(() => request.post(`/accountMch/auditProcessResult?auditType=${auditType}`, rows.map(row => row.id)))
          .then(({data}) => {
            this.$message.success(data)
            this.refreshList()
          })
      },
      onHistoryChange(history) {
        if (history) {
          this.createTimeRange = [dayjs(new Date()).add(-1, 'M').format("YYYY-MM-DD HH:mm:ss"), dayjs(new Date()).format("YYYY-MM-DD HH:mm:ss")];
        } else {
          this.createTimeRange = [];
        }
        this.pageResult = {}
      }
    },
    mounted() {
      this.refreshList();
    }
  };
</script>

