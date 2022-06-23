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
  <div class="schema">
    <el-row :gutter="10">
      <el-col
        v-for="(item, index) in schemaData"
        :key="index"
        :xs="24"
        :sm="12"
        :md="6"
        :lg="4"
        :xl="3"
      >
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{ item.title }}</span>
          </div>
          <div v-for="(itm, idex) in item.children" :key="idex" class="coll-item">
            <div :class="'itm icon-' + idex"/>
            <div class="txt">{{ itm }}</div>
            <i v-if="itm === '下载脚本'" class="icon-download" @click="handlerClick(item.title, itm)"/>
            <i v-else-if="itm === '一键刷数'" class="icon-submit" @click="handlerClick(item.title, itm)"/>
            <i v-else-if="itm === '选表刷数'" class="icon-submit" @click="handlerClick(item.title, itm)"/>
            <i v-else class="icon-edit" @click="handlerClick(item.title, itm)"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-button type="primary" icon="el-icon-plus" @click="add"/>
    </el-row>
    <el-dialog :visible.sync="centerDialogVisible" :title="type" width="80%" top="3vh">
      <el-row :gutter="20">
        <el-col :span="12">
          <span style="font-size: 18px; font-weight: bold;">Edit source here:</span>
          <el-input
            :rows="20"
            :placeholder="$t('ruleConfig.form.inputPlaceholder')"
            v-model="textarea"
            type="textarea"
            class="edit-text"
          />
        </el-col>
        <el-col :span="12">
          <span style="font-size: 18px; font-weight: bold;">Result (JS object dump):</span>
          <el-input
            :rows="20"
            :placeholder="$t('ruleConfig.form.inputPlaceholder')"
            v-model="textarea2"
            type="textarea"
            readonly
            class="show-text"
          />
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">{{ $t('btn.cancel') }}</el-button>
        <el-button type="primary" @click="onConfirm">{{ $t('btn.submit') }}</el-button>
      </span>
    </el-dialog>
    <el-dialog :visible.sync="addSchemaDialogVisible" title="Add Schema" width="80%" top="3vh">
      <el-form ref="form" :model="form" :rules="rules" label-width="170px">
        <el-form-item :label="$t('ruleConfig.schema.name')" prop="name">
          <el-input
            :placeholder="$t('ruleConfig.schemaRules.name')"
            v-model="form.name"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item :label="$t('ruleConfig.schema.ruleConfig')" prop="ruleConfig">
          <el-input
            :placeholder="$t('ruleConfig.schemaRules.ruleConfig')"
            :rows="8"
            v-model="form.ruleConfig"
            autocomplete="off"
            type="textarea"
            class="edit-text"
          />
        </el-form-item>
        <el-form-item :label="$t('ruleConfig.schema.dataSourceConfig')" prop="dataSourceConfig">
          <el-input
            :placeholder="$t('ruleConfig.schemaRules.dataSourceConfig')"
            :rows="8"
            v-model="form.dataSourceConfig"
            autocomplete="off"
            type="textarea"
            class="edit-text"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addSchemaDialogVisible = false">{{ $t('btn.cancel') }}</el-button>
        <el-button type="primary" @click="addSchema('form')">{{ $t('btn.submit') }}</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="showSelectTable2Shuffle" title="自定义刷数请求体" width="60%" top="3vh">
      <el-row :gutter="20">
        <el-col :span="12">
          <span style="font-size: 18px; font-weight: bold;">请求体:</span>
          <el-input
            :rows="30"
            placeholder="请填写自定义JSON字符串格式请求体"
            v-model="selectTableForm.customJsonRequestBody"
            type="textarea"
            class="edit-text"
          />
        </el-col>
        <el-col :span="12">
          <span style="font-size: 18px; font-weight: bold;">样例:</span>
          <el-input
            :rows="30"
            v-model="selectTableForm.customJsonRequestBodyExample"
            type="textarea"
            readonly
            class="show-text"
          />
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showSelectTable2Shuffle = false">{{ $t('btn.cancel') }}</el-button>
        <el-button type="primary" @click="doEncryptShuffle()">{{ $t('btn.submit') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  AuthYamlType,
  EncYamlType,
  jsYaml,
  ReadYamlType,
  SharYamlType,
  TranYamlType
} from '../../../utils/yamlTranstoJsonGlobal'
import API from '../api'
import C from '../../../utils/conf'

export default {
  name: 'Schema',
  data() {
    return {
      treeData: [],
      textarea: ``,
      schemaData: [],
      centerDialogVisible: false,
      type: null,
      sname: '',
      scname: '',
      addSchemaDialogVisible: false,
      schemaName: ``,
      rueleConfigTextArea: ``,
      dataSourceConfigTextArea: ``,
      showSelectTable2Shuffle: false,
      selectTableForm: {
        customJsonRequestBody: `{
  "shuffleTableNames": [
    "所属schema的表名，全匹配，否则返回成功，但忽略刷数"
  ]
}`,
        customJsonRequestBodyExample: `{
   // 必填，填写该schema下需要刷数的表
  "shuffleTableNames": [
    "string"
  ],
  // 可空，如需要对所选表自定义刷数模式，则可配置，一般为空即可
  "tableExtractDefines": [
    {
      // 表名
      "tableName":"string",
      // 抽取模式：All-全量，customExtractWhereSql属性失效、WithPersistStateCustomWhere-支持自定义WithPersistStateCustomWhere和多次增量刷数（前提是有设置该表的增量字段）、 OtherCustom-只支持自定义WithPersistStateCustomWhere
      "extractMode": "All",
      // 表增量字段：extractMode=WithPersistStateCustomWhere生效，用于增量刷数
      "incrFieldName": "last_update_time",
      // 自定义刷数表条件，where之后（不可加where)：extractMode=WithPersistStateCustomWhere或OtherCustom生效
      "customExtractWhereSql": "",
      // 是否重置表多次抽取状态，用于重置extractMode=WithPersistStateCustomWhere上一次刷数状态记录
      "resetExtractState": false,
      // 建议该表的刷数分片数
      "adviceNumberPartition": 50
    }
  ]
  // 注：填写时，不可带上// 注释内容！
}`,
      },
      selectTableRules: {
        tableNames: [
          {
            required: true,
            message: this.$t('ruleConfig').selectTableRules.tableNames,
            trigger: 'change'
          }
        ],
      },
      form: {
        name: '',
        ruleConfig: '',
        dataSourceConfig: ''
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t('ruleConfig').schemaRules.name,
            trigger: 'change'
          }
        ],
        ruleConfig: [
          {
            required: true,
            message: this.$t('ruleConfig').schemaRules.ruleConfig,
            trigger: 'change'
          }
        ],
        dataSourceConfig: [
          {
            required: true,
            message: this.$t('ruleConfig').schemaRules.dataSourceConfig,
            trigger: 'change'
          }
        ]
      }
    }
  },
  computed: {
    textarea2() {
      const dsYamlType = new jsYaml.Type(
        'tag:yaml.org,2002:org.apache.shardingsphere.orchestration.core.configuration.YamlDataSourceConfiguration',
        {
          kind: 'mapping',
          construct(data) {
            return data !== null ? data : {}
          }
        }
      )
      const DS_SCHEMA = jsYaml.Schema.create([AuthYamlType, TranYamlType, SharYamlType, ReadYamlType, EncYamlType])
      return JSON.stringify(
        jsYaml.load(this.textarea, {schema: DS_SCHEMA}),
        null,
        '\t'
      )
    }
  },
  created() {
    this.getSchema()
  },
  methods: {
    add() {
      this.addSchemaDialogVisible = true
    },
    handlerClick(parent, child) {
      this.schemaName = parent
      if (child === 'rule') {
        API.getSchemaRule(parent).then(res => {
          this.renderYaml(parent, child, res)
        })
      } else if (child === '下载脚本') {
        window.location.href = `${C.HOST}/api/config-center/encrypt/download/?schema=${parent}`
      } else if (child === '一键刷数') {
        this.$confirm('确定一键刷数，可一次或多次刷数？注：刷数方式，若敏感信息没设置增量字段，则每次全量，若有，则多次增量',
          '一键刷数',
          {confirmButtonText: '确定', cancelButtonText: '取消'})
          .then(() => {
            let params = {
              schema: parent,
              shuffleTableNames: [],
            }
            API.submitSchemaEncryptShuffle(params).then(res => {
              this.$message.info(`提交刷数异步作业成功`)
            })
          })
      } else if (child === '选表刷数') {
        this.showSelectTable2Shuffle = true
      } else {
        API.getSchemaDataSource(parent).then(res => {
          this.renderYaml(parent, child, res)
        })
      }
    },
    renderYaml(parent, child, res) {
      if (!res.success) return
      const model = res.model
      if (Object.prototype.toString.call(model) === '[object String]') {
        this.textarea = model
      } else {
        this.textarea = JSON.stringify(model, null, '\t')
      }
      this.sname = parent
      this.scname = child
      this.type = `${parent}-${child}`
      this.centerDialogVisible = true
    },
    getSchema() {
      API.getSchema().then(res => {
        const data = res.model
        const base = ['rule', 'datasource', '下载脚本', '一键刷数', '选表刷数']
        const newData = []
        for (const v of data) {
          newData.push({
            title: v,
            children: base
          })
        }
        this.schemaData = newData
      })
    },
    onConfirm() {
      if (this.scname === 'rule') {
        API.putSchemaRule(this.sname, {ruleConfig: this.textarea}).then(
          res => {
            this._onConfirm(res)
          }
        )
      } else {
        API.putSchemaDataSource(this.sname, {
          dataSourceConfig: this.textarea
        }).then(res => {
          this._onConfirm(res)
        })
      }
    },
    _onConfirm(res, type) {
      if (res.success) {
        this.$notify({
          title: this.$t('common').notify.title,
          message: this.$t('common').notify.updateCompletedMessage,
          type: 'success'
        })
        this.centerDialogVisible = false
        if (type === 'ADD_SCHEMA') {
          this.addSchemaDialogVisible = false
          this.getSchema()
        }
      } else {
        this.$notify({
          title: this.$t('common').notify.title,
          message: this.$t('common').notify.updateFaildMessage,
          type: 'error'
        })
      }
    },
    addSchema(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          API.addSchema({
            name: this.form.name,
            ruleConfiguration: this.form.ruleConfig,
            dataSourceConfiguration: this.form.dataSourceConfig
          }).then(res => {
            this._onConfirm(res, 'ADD_SCHEMA')
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    doEncryptShuffle(selectTableForm) {
      if (!this.selectTableForm.customJsonRequestBody) {
        this.$message.warning("请求体不可为空！")
        return;
      }
      try {
        let jsonObj = JSON.parse(this.selectTableForm.customJsonRequestBody)
        jsonObj.dbType = 'mysql'
        jsonObj.schema = this.schemaName
        API.submitSchemaEncryptShuffleCustom(jsonObj).then(res => {
          console.log(res)
          this.$message.success(`${res.model}`)
          this.showSelectTable2Shuffle = false
        })
      } catch (e) {
        console.log(e)
        this.$message.error("请求体内容非JSON字符串格式！")
      }
    },
    selectChange(item) {
      console.log(item)
    }
  }
}
</script>
<style lang='scss'>
.schema {
  margin-top: 20px;

  .coll-item {
    height: 16px;
    line-height: 16px;
    width: 100%;
    float: left;
    margin-bottom: 22px;

    .txt {
      color: rgb(51, 51, 51);
      font-size: 14px;
      padding-left: 10px;
      float: left;
      margin-right: 10px;
    }

    .itm {
      float: left;
      width: 16px;
      height: 16px;
    }

    .icon-0 {
      background: url('../../../assets/img/rules.png') no-repeat left center;
    }

    .icon-1 {
      background: url('../../../assets/img/data-source.png') no-repeat left center;
    }

    .icon-2 {
      background: url('../../../assets/img/list.png') no-repeat left center;
    }

    .icon-3 {
      background: url('../../../assets/img/list.png') no-repeat left center;
    }

    .icon-4 {
      background: url('../../../assets/img/list.png') no-repeat left center;
    }

    .icon-5 {
      background: url('../../../assets/img/list.png') no-repeat left center;
    }

    .edit-btn {
      float: right;
    }
  }

  .el-row {
    margin-bottom: 20px;
  }

  .el-collapse-item__header {
    font-size: 16px;
  }

  .edit-text {
    margin-top: 5px;

    textarea {
      background: #fffffb;
    }
  }

  .show-text {
    margin-top: 5px;

    textarea {
      background: rgb(246, 246, 246);
    }
  }

  .icon-edit {
    background: url('../../../assets/img/edit.png') no-repeat left center;
    width: 16px;
    height: 16px;
    display: inline-block;
    float: right;
    cursor: pointer;
  }

  .icon-download {
    background: url('../../../assets/img/download.png') no-repeat left center;
    width: 16px;
    height: 16px;
    display: inline-block;
    float: right;
    cursor: pointer;
  }

  .icon-submit {
    background: url('../../../assets/img/submit.png') no-repeat left center;
    width: 16px;
    height: 16px;
    display: inline-block;
    float: right;
    cursor: pointer;
  }

  .el-dialog__body {
    padding: 10px 20px;
  }

  .el-input {
    width: 30%;
  }

  .el-input__inner {
    height: 35px;
    line-height: 35px;
  }
}
</style>
