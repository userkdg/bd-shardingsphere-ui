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
  <div class="s-layout-header">
    <div class="s-pro-components-header">
<!--      <div class="s-pro-components-header-right">
        <div class="avatar">
          <el-dropdown @command="handlerClick">
            <el-tag type="success">
              <span class="el-dropdown-link">
                {{ username || 'Not logged in' }}
                <i class="el-icon-arrow-down el-icon&#45;&#45;right" />
              </span>
            </el-tag>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>{{ $t("common.loginOut") }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div class="lang-more">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ dropdownTitle }}<i class="el-icon-arrow-down el-icon&#45;&#45;right" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="(item, index) in dropdownList" :key="index" :command="item.command">{{ item.title }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>-->
      <div style="width: 50%">
        <el-menu
          mode = 'horizontal'
          :default-active="defActive"
          :defaultSelectedKeys = '[$route.path]'
        >
          <template v-for="(item, index) in menuData">
            <el-menu-item v-if="!item.child" :index="String(index)" :key="String(index)">
              <a :href="'#' + item.href" :key="String(index)">{{item.title}}</a>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
<!--      <el-breadcrumb separator="/" class="bread-nav">
        <el-breadcrumb-item>
          <a
            style="font-weight: bold; color: #1890ff;"
          >{{ $store.state.global.regCenterActivated || '' }}</a>
        </el-breadcrumb-item>
      </el-breadcrumb>-->
    </div>
  </div>
</template>
<script>
export default {
  name: 'Head',
  data() {
    return {
      menuData: this.$t('common').menuData[0].child,
      defActive: '',
      isCollapse: false,
      username: '',
      breadcrumbTxt: '',
      dropdownList: this.$t('common').dropdownList,
      dropdownTitle:  this.$t('common').dropdownList[0].title
      //dropdownTitle: localStorage.getItem('language') === 'zh-CN' ? this.$t('common').dropdownList[0].title : this.$t('common').dropdownList[1].title
    }
  },
  watch:{
    $route: {
      handler(route) {
        for (const v of this.menuData) {
          if (!v.child) {
            if (v.href === route.path) {
              this.defActive = v.href
              break
            }
          } else {
            for (const vv of v.child) {
              if (route.path === vv.href) {
                this.defActive = vv.href
                break
              }
            }
          }
        }
      },
      immediate: true
    }
  },
  computed: {
    classes() {
      return [
        `icon-item`,
        {
          [`icon-shrink`]: !this.isCollapse,
          [`icon-expand`]: this.isCollapse
        }
      ]
    }
  },
  created() {
    const store = window.localStorage
    this.username = store.getItem('username')
  },
  methods: {
    handleCommand(command) {
      this.$i18n.locale = command
      const ls = this.$t('common').dropdownList
      this.dropdownTitle = command === 'zh-CN' ? ls[0].title : ls[1].title
      localStorage.setItem('language', command)
      location.reload()
    },
    handleClick({ item, key, keyPath }){
      console.log(key)
    },
    togger() {
      this.isCollapse = !this.isCollapse
      this.$emit('on-togger', this.isCollapse)
    },
    handlerClick() {
      const store = window.localStorage
      store.removeItem('username')
      store.removeItem('Access-Token')
      location.href = '#/login'
    }
  }
}
</script>
<style lang="scss" scoped>
.s-layout-header {
  background: #001529;
  padding: 0;
  height: 64px;
  line-height: 64px;
  width: 100%;
  .bread-nav {
    float: right;
    height: 64px;
    line-height: 64px;
    padding-right: 20px;
  }
  .s-pro-components-header {
    height: 64px;
    padding: 0;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    position: relative;
    i.icon-item {
      width: 16px;
      height: 16px;
      float: left;
      cursor: pointer;
      margin: 24px;
    }
    i.icon-shrink {
      background: url('../../assets/img/shrink.png') no-repeat left center;
    }
    .icon-expand {
      background: url('../../assets/img/expand.png') no-repeat left center;
    }
    .s-pro-components-header-right {
      float: right;
      height: 100%;
      overflow: hidden;
    }
  }
  .avatar {
    cursor: pointer;
    padding: 0 12px;
    display: inline-block;
    transition: all 0.3s;
    height: 100%;
  }
  .lang-more {
    cursor: pointer;
    padding: 0 20px;
    display: inline-block;
    transition: all 0.3s;
    height: 100%;
    // .lang-icon {
    //   background: url('../../assets/img/lang.png') no-repeat center center;
    //   width: 32px;
    //   height: 60px;
    // }
  }
  .a-span{
    margin-left: 20px;
  }
  .b-span{
    margin-left: 10px;
  }
}
</style>
