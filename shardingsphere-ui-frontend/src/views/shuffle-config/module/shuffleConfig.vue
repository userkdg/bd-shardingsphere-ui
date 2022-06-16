<!--
  - Licensed to the Apache Software Foundation (ASF) under one or more
  - contributor license agreements.  See the NOTICE file distributed with
  - this work for additional information regarding copyright ownership.
  - The ASF licenses this file to You under the Apache License, Version 2.0
  - (the "License"); you may not use this file except in compliance with
  - the License.  You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
  <el-row class="box-card">
    <div class="btn-group">
      <el-button
        class="btn-plus"
        type="primary"
        icon="el-icon-plus"
        @click="add"
      >{{ $t("shuffleConfig.btnTxt") }}</el-button>
    </div>
    <div class="table-wrap">
      <el-input v-model="searchKeyword" placeholder="模糊搜索数据库名|表名|增量字段名|更新变化字段关键字" style="width: 500px"></el-input>
      <el-button icon="el-icon-search" @click="getShuffleConfig()" type="primary"></el-button>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column
          v-for="(item, index) in column"
          :key="index"
          :prop="item.prop"
          :label="item.label"
          :width="item.width"
        />
        <el-table-column :label="$t('shuffleConfig.table.operate')" fixed="right" width="200">
          <template slot-scope="scope">
            <el-tooltip
              :content="$t('shuffleConfig.table.operateEdit')"
              class="item"
              effect="dark"
              placement="top"
            >
              <el-button
                :disabled="scope.row.activated"
                size="small"
                type="primary"
                icon="el-icon-edit"
                @click="handleEdit(scope.row)"
              />
            </el-tooltip>
            <el-tooltip
              :content="$t('shuffleConfig.table.operateDel')"
              class="item"
              effect="dark"
              placement="top"
            >
              <el-button
                size="small"
                type="danger"
                icon="el-icon-delete"
                @click="handlerDel(scope.row)"
              />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          :total="total"
          :current-page="currentPage"
          background
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-dialog
      :title="$t('shuffleConfig.configDialog.title')"
      :visible.sync="regustDialogVisible"
      width="1010px"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="170px">
        <el-form-item :label="$t('shuffleConfig.configDialog.schemaName')" prop="schemaName">
          <el-input :placeholder="$t('shuffleConfig.rules.schemaName')" v-model="form.schemaName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.tableName')" prop="tableName">
          <el-input :placeholder="$t('shuffleConfig.rules.tableName')" v-model="form.tableName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.incrFieldName')" prop="incrFieldName">
          <el-input :placeholder="$t('shuffleConfig.rules.incrFieldName')" v-model="form.incrFieldName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.onUpdateTimestampFields')" prop="onUpdateTimestampFields">
          <el-input :placeholder="$t('shuffleConfig.rules.onUpdateTimestampFields')" v-model="form.onUpdateTimestampFields" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.dataVolumeLevel')" prop="dataVolumeLevel">
          <el-input :placeholder="$t('shuffleConfig.rules.dataVolumeLevel')" v-model="form.dataVolumeLevel" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="regustDialogVisible = false">{{ $t("shuffleConfig.configDialog.btnCancelTxt") }}</el-button>
        <el-button
          type="primary"
          @click="onConfirm('form')"
        >{{ $t("shuffleConfig.configDialog.btnConfirmTxt") }}</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('shuffleConfig.configDialog.editTitle')"
      :visible.sync="editDialogVisible"
      width="1010px"
    >
      <el-form ref="editForm" :model="editForm" :rules="rules" label-width="170px">
        <el-form-item hidden prop="sensitiveId">
          <el-input hidden v-model="editForm.sensitiveId" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.schemaName')" prop="schemaName">
          <el-input :placeholder="$t('shuffleConfig.rules.schemaName')" v-model="editForm.schemaName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.tableName')" prop="tableName">
          <el-input :placeholder="$t('shuffleConfig.rules.tableName')" v-model="editForm.tableName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.incrFieldName')" prop="incrFieldName">
          <el-input :placeholder="$t('shuffleConfig.rules.incrFieldName')" v-model="editForm.incrFieldName" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.onUpdateTimestampFields')" prop="onUpdateTimestampFields">
          <el-input :placeholder="$t('shuffleConfig.rules.onUpdateTimestampFields')" v-model="editForm.onUpdateTimestampFields" autocomplete="off" />
        </el-form-item>
        <el-form-item :label="$t('shuffleConfig.configDialog.dataVolumeLevel')" prop="dataVolumeLevel">
          <el-input :placeholder="$t('shuffleConfig.rules.dataVolumeLevel')" v-model="editForm.dataVolumeLevel" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelEdit">{{ $t("shuffleConfig.configDialog.btnCancelTxt") }}</el-button>
        <el-button
          type="primary"
          @click="confirmEdit('editForm')"
        >{{ $t("shuffleConfig.configDialog.btnConfirmTxt") }}</el-button>
      </div>
    </el-dialog>
  </el-row>
</template>
<script>
import {mapActions} from 'vuex'
import clone from 'lodash/clone'
import API from '../api'

export default {
  name: 'ShuffleConfig',
  data() {
    return {
      regustDialogVisible: false,
      editDialogVisible: false,
      column: [
        {
          label: this.$t('shuffleConfig').configDialog.schemaName,
          prop: 'schemaName'
        },
        {
          label: this.$t('shuffleConfig').configDialog.tableName,
          prop: 'tableName'
        },
        {
          label: this.$t('shuffleConfig').configDialog.incrFieldName,
          prop: 'incrFieldName'
        },
        {
          label: this.$t('shuffleConfig').configDialog.onUpdateTimestampFields,
          prop: 'onUpdateTimestampFields'
        },
        {
          label: this.$t('shuffleConfig').configDialog.dataVolumeLevel,
          prop: 'dataVolumeLevel'
        }
      ],
      form: {
        name: '',
        serverLists: '',
        namespace: '',
        instanceType: 'Zookeeper',
        orchestrationName: '',
        orchestrationType: 'config_center',
        digest: ''
      },
      editForm: {
        id: '',
        sensitiveId: '',
        schemaName: '',
        tableName: '',
        incrFieldName: '',
        onUpdateTimestampFields: '',
        dataVolumeLevel: 2,
      },
      rules: {
        schemaName: [
          {
            required: true,
            message: this.$t('shuffleConfig').rules.schemaName,
            trigger: 'change'
          }
        ],
        tableName: [
          {
            required: true,
            message: this.$t('shuffleConfig').rules.tableName,
            trigger: 'change'
          }
        ],
        incrFieldName: [
          {
            required: true,
            message: this.$t('shuffleConfig').rules.incrFieldName,
            trigger: 'change'
          }
        ],
        onUpdateTimestampFields: [
          {
            required: false,
            message: this.$t('shuffleConfig').rules.onUpdateTimestampFields,
            trigger: 'change'
          }
        ],
        dataVolumeLevel: [
          {
            required: true,
            message: this.$t('shuffleConfig').rules.dataVolumeLevel,
            trigger: 'change'
          }
        ]
      },
      dataVolumeLevelMap: {
        1: '万级别',
        2: '十万级别',
        3: '百万级别',
        4: '千万级别',
        5: '亿级别'
      },
      searchKeyword: '',
      tableData: [],
      cloneTableData: [],
      currentPage: 1,
      pageSize: 10,
      total: null
    }
  },
  created() {
    this.getShuffleConfig()
  },
  methods: {
    ...mapActions(['setRegCenterActivated']),
    handleCurrentChange(val) {
      const data = clone(this.cloneTableData)
      this.tableData = data.splice(val - 1, this.pageSize)
    },
    getShuffleConfig() {
      let params = {
        keyword: this.searchKeyword
      }
      API.getShuffleConfig(params).then(res => {
        let result = res.model
        for (const row of result) {
          row.dataVolumeLevel = this.dataVolumeLevelMap[row.dataVolumeLevel | 2]
        }
        const data = result
        this.total = data.length
        this.cloneTableData = clone(res.model)
        this.tableData = data.splice(0, this.pageSize)
      })
    },
    handlerDel(row) {
      this.$confirm('确定删除该辅助信息？',
        '删除表刷数辅助信息',
        {confirmButtonText: '确定', cancelButtonText: '取消'})
        .then(() => {
          API.deleteShuffleConfig(row.id).then(res => {
            this.$notify({
              title: this.$t('common').notify.title,
              message: this.$t('common').notify.delSucMessage,
              type: 'success'
            })
            this.getShuffleConfig()
          })
        })
    },
    onConfirm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          API.postShuffleConfig(this.form).then(res => {
            this.regustDialogVisible = false
            this.$notify({
              title: this.$t('common').notify.title,
              message: this.$t('common').notify.addSucMessage,
              type: 'success'
            })
            this.getShuffleConfig()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    add() {
      this.regustDialogVisible = true
    },
    handleEdit(row) {
      this.editDialogVisible = true
      this.editForm = Object.assign({}, row)
      this.editForm.id = row.id
    },
    confirmEdit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          API.updateShuffleConfig(this.editForm).then(res => {
            this.editDialogVisible = false
            this.$notify({
              title: this.$t('common').notify.title,
              message: this.$t('common').notify.editSucMessage,
              type: 'success'
            })
            this.getShuffleConfig()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    cancelEdit() {
      this.editDialogVisible = false
    }
  }
}
</script>
<style lang='scss' scoped>
.btn-group {
  margin-bottom: 20px;
}
.pagination {
  float: right;
  margin: 10px -10px 10px 0;
}
</style>
