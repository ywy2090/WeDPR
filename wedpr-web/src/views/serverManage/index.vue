<template>
  <div class="group-manage">
    <div class="form-search">
      <el-form :inline="true" @submit="queryHandle" :model="searchForm" ref="searchForm" size="small">
        <el-form-item prop="agency" label="发布机构：">
          <el-select clearable size="small" style="width: 160px" v-model="searchForm.agency" placeholder="请选择">
            <el-option :key="item" v-for="item in agencyList" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="owner" label="发布用户：">
          <el-input clearable style="width: 160px" v-model="searchForm.owner" placeholder="请输入"> </el-input>
        </el-form-item>
        <el-form-item prop="serviceName" label="服务名称：">
          <el-input clearable style="width: 160pxcredential / query" v-model="searchForm.serviceName" placeholder="请输入"> </el-input>
        </el-form-item>
        <el-form-item prop="createDate" label="发布时间：">
          <el-date-picker clearable value-format="yyyy-MM-dd" style="width: 160px" v-model="searchForm.createDate" type="date" placeholder="请选择日期"> </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="queryFlag" @click="queryHandle">
            {{ queryFlag ? '查询中...' : '查询' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="default" :loading="queryFlag" @click="reset"> 重置 </el-button>
        </el-form-item>
        <div class="op-container">
          <div class="btn" @click="creatPsi"><img class="icon-btn" src="~Assets/images/lead icon_service1.png" alt="" /> 发布匿踪查询服务</div>
          <div class="btn model" @click="creatModel"><img class="icon-btn" src="~Assets/images/lead icon_service2.png" alt="" /> 发布模型预测服务</div>
        </div>
      </el-form>
    </div>
    <div class="card-container">
      <div @click="getDetail(item)" class="server-con" v-for="item in tableData" :key="item">
        <img src="~Assets/images/icon_service1.png" alt="" />
        <dl>
          <dt>{{ item.serviceName }}</dt>
          <dd>
            发布人：<span class="count">{{ item.owner }}</span>
          </dd>
          <dd>
            发布机构：<span class="count">{{ item.agency }}</span>
          </dd>
          <dd>
            服务类型：<span class="count">{{ item.serviceType }}</span>
          </dd>
          <dd>
            创建时间：<span>{{ item.createTime }}</span>
          </dd>
        </dl>
        <div class="edit">
          <div class="op-con" v-if="item.owner === userId && item.agency === agencyId">
            <img src="~Assets/images/icon_edit.png" alt="" @click.stop="modifyData(item)" />
            <img @click.stop="showDeleteModal(item)" src="~Assets/images/icon_delete.png" alt="" />
          </div>
          <div class="apply" @click.stop="applyData(item)" v-else>
            <span>申请使用</span>
          </div>
        </div>
      </div>
    </div>
    <we-pagination :total="total" :page_offset="pageData.page_offset" :page_size="pageData.page_size" @paginationChange="paginationHandle"></we-pagination>
  </div>
</template>
<script>
import wePagination from '@/components/wePagination.vue'
import { serviceManageServer } from 'Api'
import { handleParamsValid } from 'Utils/index.js'
import { mapGetters } from 'vuex'
export default {
  name: 'serverManage',
  components: {
    wePagination
  },
  data() {
    return {
      searchForm: {
        agency: '',
        owner: '',
        serviceName: '',
        createDate: ''
      },
      searchQuery: {
        agency: '',
        owner: '',
        serviceName: '',
        createDate: ''
      },
      pageData: {
        page_offset: 1,
        page_size: 8
      },
      total: 10,
      queryFlag: false,
      tableData: [],
      loadingFlag: false,
      showAddModal: false
    }
  },
  computed: {
    ...mapGetters(['agencyList', 'userId', 'agencyId'])
  },
  created() {
    this.getPublishList()
  },
  methods: {
    reset() {
      this.$refs.searchForm.resetFields()
    },
    // 查询
    queryHandle() {
      this.$refs.searchForm.validate((valid) => {
        if (valid) {
          this.searchQuery = { ...this.searchForm }
          this.pageData.page_offset = 1
          this.getPublishList()
        } else {
          return false
        }
      })
    },
    // 删除服务
    showDeleteModal(serverData) {
      const { serviceId } = serverData
      this.$confirm('确认删除服务吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.deleteServer({ serviceId })
        })
        .catch(() => {})
    },
    async deleteServer(params) {
      const res = await serviceManageServer.revokeService(params)
      console.log(res)
      if (res.code === 0) {
        this.$message.success('服务删除成功')
        this.getPublishList()
      }
    },
    // 获取数据集列表
    async getPublishList() {
      const { page_offset, page_size } = this.pageData
      const { agency = '', owner = '', serviceName = '', createDate = '' } = this.searchQuery
      let params = handleParamsValid({ agency, owner, serviceName, createDate })
      params = { ...params, pageNum: page_offset, pageSize: page_size }
      this.loadingFlag = true
      const res = await serviceManageServer.getPublishList(params)
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { wedprPublishedServiceList = [], totalCount } = res.data
        this.tableData = wedprPublishedServiceList
        this.total = totalCount
      } else {
        this.tableData = []
        this.total = 0
      }
    },
    // 分页切换
    paginationHandle(pageData) {
      console.log(pageData, 'pagData')
      this.pageData = { ...pageData }
      this.getPublishList()
    },
    creatPsi() {
      this.$router.push({ path: '/pirServerCreate' })
    },
    creatModel() {
      this.$router.push({ path: '/modelServerCreate' })
    },
    getDetail(data) {
      const { serviceId, type } = data
      this.$router.push({ path: '/serverDetail', query: { serviceId, serverType: type } })
    },
    applyData(item) {
      const { serviceId } = item
      this.$router.push({ path: '/serverDetail', query: { serviceId, type: 'apply' } })
    },
    modifyData(item) {
      const { serviceId } = item
      this.$router.push({ path: '/pirServerCreate', query: { type: 'edit', serviceId } })
    }
  }
}
</script>
<style lang="less" scoped>
div.op-container {
  margin: 16px 0;
  div.btn {
    padding: 5px 16px;
    border-radius: 4px;
    cursor: pointer;
    background: #51a14e;
    color: white;
    display: inline-block;
    font-size: 14px;
    line-height: 22px;
    margin-right: 12px;
  }
  div.btn:hover {
    opacity: 0.8;
  }
  div.model {
    background: #0ca8ff;
  }
  img.icon-btn {
    width: 16px;
    height: 16px;
    transform: translateY(2px);
    margin-right: 9px;
  }
}
div.card-container {
  margin-left: -16px;
  margin-right: -16px;
  overflow: hidden;
}
div.server-con {
  border: 1px solid #e0e4ed;
  padding: 20px;
  box-sizing: border-box;
  border-radius: 12px;
  float: left;
  width: calc(25% - 32px);
  background-color: #f6f8fc;
  margin: 20px 16px;
  cursor: pointer;
  min-width: 240px;
  img {
    width: 48px;
    height: 48px;
    margin-bottom: 16px;
  }
  dl {
    dt {
      font-size: 16px;
      line-height: 24px;
      color: #262a32;
      font-weight: bolder;
      margin-bottom: 16px;
    }
    dd {
      font-size: 12px;
      line-height: 24px;
      margin-bottom: 4px;
      color: #787b84;
      span {
        float: right;
      }
    }
  }
  div.edit {
    margin-top: 28px;
  }
  .op-con {
    display: flex;
    justify-content: space-around;
    height: auto;
    img {
      width: 24px;
      height: 24px;
      cursor: pointer;
      margin-bottom: 0;
    }
  }
  div.apply {
    border: 1px solid #b3b5b9;
    margin-bottom: -4px;
    margin-top: -4px;
    height: 32px;
    padding: 5px 12px;
    text-align: center;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    span {
      display: flex;
      align-items: center;
    }
  }
}
div.server-con:hover {
  box-shadow: 0px 2px 10px 2px #00000014;
  cursor: pointer;
}
</style>
