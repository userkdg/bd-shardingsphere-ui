<template>
  <div>
    <el-dialog
      title="规则上传窗口"
      :visible.sync="visible"
      width="30%"
      :before-close="handleClose"
    >
      <el-button @click="downloadFile()" style="margin-bottom: 10px">
        下载模板
      </el-button>
      <el-select v-model="id" @change="handleSelect" filterable placeholder="请选择schema">
        <el-option
          v-for="item in schemaList"
          :key="item.id"
          :label="item.schemaName"
          :value="item.id">
        </el-option>
      </el-select>
      <el-upload
        class="upload-demo"
        :action="uploadUrl"
        :before-upload="beforeUpload"
        :on-exceed="handleExceed"
        :headers="myHeaders"
        drag
        :limit="1"
        :file-list="fileList"
        :on-success="upSuccess"
      >
        <div>
          <i class="el-icon-upload" ></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只能上传.xls.xlsx.xlsm后缀文件，且不超过10M</div>
        </div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import Api from '../api'
var token = window.localStorage.getItem('Access-Token')
export default {
  name: "uploadPanel",
  data() {
    return {
      uploadUrl: '',
      id:'',
      myHeaders:{"Access-Token": token},
      visible: false,
      fileList: [],
      openFileDialogOnClick: true,
      serviceBaseUrl: "",
      excelSuffix: ['xls','xlsx','xlsm'],
      schemaList:[],
    };
  },
  mounted() {
    this.schemas()
    // this.initUpload()
  },
  methods: {
    handleSelect(){
      this.uploadUrl = "http://localhost:8080/api/config-center/import?id="+this.id
    },
    upSuccess(res, f, fl){
      if(res.success){
        this.$message.success("上传成功");
        this.visible = false
      }else {
        this.$message.error(res.errorMsg);
        this.fileList = []
      }
    },
    visibleModel(visible) {
      this.visible = visible;
      this.errorList = [];
      this.id = ''
    },
    schemas(){
      Api.getSchemaList().then(response => {
        if(response.success){
         this.schemaList = response.model
        }else {
          this.$message.error(response.errorMsg)
        }
      })
    },
    handleClose(){
      this.visible = false
    },
    handleExceed() {
      this.$message.warning(`只能上传1个文件`);
    },
    beforeUpload(file) {
      let fileName=file.name.substring(file.name.lastIndexOf('.')+1)
      if(!this.excelSuffix.includes(fileName)){
        this.$message.error("请上传excel格式文件")
        return false;
      }
      if(this.id == null || this.id.length ==0){
        this.$message.error("请选择schema")
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 <= 10;
      if (!isLt2M) {
        this.$message.error("文件大小不大于10MB!");
        return false;
      }
      return isLt2M;
    },
    /*initUpload() {
     this.uploadUrl =
        "http://localhost:8080/api/config-center/import?id="
    },*/
    downloadFile() {
      axios({
        method: 'get',
        url: 'http://localhost:8080/api/config-center/download',
        headers: {
          'Access-Token': token
        },
        responseType: "blob"
      }).then(res => {
          if (res.data.type) {
            // 文件下载
            this.visible = false;
            const blob = new Blob([res.data], {
              type: "application/vnd.ms-excel"
            });
            let link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.setAttribute('download', '系统敏感信息采集表模板.xlsx');
            link.click();
            link = null;
            this.$message.success('下载成功');
          } else {
            // 返回json
            this.$message.warning(res.data.msg);
          }
        })
        .catch(err => {
          this.visible = false;
          this.$message.error("下载失败");
        });
    },
  },
};
</script>

<style scoped>
</style>
