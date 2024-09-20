<template>
  <div class="create-data">
    <el-form :inline="false" @submit="checkService" :model="serverForm" :rules="serverRules" ref="serverForm" size="small">
      <formCard title="基础信息">
        <el-form-item label-width="96px" label="服务名称：" prop="serviceName">
          <el-input style="width: 480px" placeholder="请输入" v-model="serverForm.serviceName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label-width="96px" label="服务简介：" prop="serviceDesc">
          <el-input type="textarea" style="width: 480px" placeholder="请输入" v-model="serverForm.serviceDesc" autocomplete="off"></el-input>
        </el-form-item>
      </formCard>
      <formCard title="选择发布模型" v-if="type !== 'edit'">
        <div class="record">
          <div class="card-container" v-if="tableData.length">
            <div class="card" v-for="item in tableData" :key="item.id" @click="goDetail(item)">
              <div class="info">
                <div class="project-title">
                  <span :title="item.name">{{ item.name }}</span>
                  <el-checkbox @change="handleSelect" :value="item.selected"></el-checkbox>
                </div>
                <div class="count-detail">
                  <dl>
                    <dt>机构数量</dt>
                    <dd>6</dd>
                  </dl>
                  <dl>
                    <dt>数据资源数量</dt>
                    <dd>6</dd>
                  </dl>
                  <dl>
                    <dt>模型数量</dt>
                    <dd>6</dd>
                  </dl>
                </div>
                <ul>
                  <li class="ell">
                    发起人： <span>{{ item.owner }}</span>
                  </li>
                  <li>
                    创建时间： <span>{{ item.createTime }}</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <el-empty v-else :image-size="120" description="暂无数据">
            <img slot="image" src="~Assets/images/pic_empty_news.png" alt="" />
          </el-empty>
          <we-pagination
            :pageSizesOption="[8, 12, 16, 24, 32]"
            :total="total"
            :page_offset="pageData.page_offset"
            :page_size="pageData.page_size"
            @paginationChange="paginationHandle"
          ></we-pagination>
        </div>
      </formCard>
    </el-form>
    <div>
      <el-button size="medium" type="primary" @click="checkService" v-if="type === 'edit'"> 编辑服务 </el-button>
      <el-button size="medium" type="primary" @click="checkService" v-else> 发布服务 </el-button>
    </div>
  </div>
</template>
<script>
import formCard from '@/components/formCard.vue'
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import { serviceManageServer, dataManageServer } from 'Api'
import wePagination from '@/components/wePagination.vue'
// import { handleParamsValid } from 'Utils/index.js'
import { mapGetters } from 'vuex'
export default {
  name: 'pirServerCreate',
  mixins: [tableHeightHandle],
  components: {
    formCard,
    wePagination
  },
  data() {
    return {
      serverForm: {
        serviceName: '',
        serviceDesc: '',
        searchType: '',
        searchFieldsType: '',
        exists: [],
        values: []
      },
      pageData: {
        page_offset: 1,
        page_size: 8
      },
      total: -1,
      queryFlag: false,
      tableData: [
        {
          name: '国家电网电费预测模型',
          agencyCount: 6,
          datasetCount: 5,
          modelCount: 4,
          owner: 'flyhuang',
          createTime: '2024-6-26 11:23:45'
        }
      ],
      loadingFlag: false,
      showAddModal: false,
      dataForm: {
        setting: []
      },
      type: '',
      dataList: [],
      selectedData: {},
      serviceId: ''
    }
  },

  created() {
    const { type, serviceId } = this.$route.query
    this.type = type
    if (this.type === 'edit') {
      this.serviceId = serviceId
      this.queryService()
    } else {
      this.getListDataset()
    }
  },
  computed: {
    ...mapGetters(['userinfo', 'userId']),
    handleSelect() {
      console.log(9999)
      return true
    },
    serverRules() {
      return {
        serviceName: [
          {
            required: true,
            message: '请输入服务名称',
            trigger: 'blur'
          }
        ],
        serviceDesc: [
          {
            required: true,
            message: '请输入服务描述',
            trigger: 'blur'
          }
        ],
        searchType: [
          {
            required: true,
            message: '请选择查询存在性',
            trigger: 'blur'
          }
        ],
        searchFieldsType: [
          {
            required: true,
            message: '请选择查询字段值',
            trigger: 'blur'
          }
        ],
        values: [
          {
            required: this.serverForm.searchFieldsType === 'some',
            message: '请输入查询字段',
            trigger: 'blur'
          }
        ],
        exists: [
          {
            required: this.serverForm.searchType === 'some',
            message: '请输入查询字段',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    // 获取服务详情回显
    async queryService() {
      this.loadingFlag = true
      const { serviceId } = this
      const res = await serviceManageServer.getServerDetail({ serviceId })
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { wedprPublishedService = {} } = res.data
        const { serviceConfig = '', serviceName, serviceDesc } = wedprPublishedService
        const { exists = [], values = [], datasetId } = JSON.parse(serviceConfig)
        this.getDetail({ datasetId })
        const searchType = exists.includes('*') ? 'all' : 'some'
        const searchFieldsType = values.includes('*') ? 'all' : 'some'
        const existsList = searchType === 'all' ? [] : exists
        const valuesList = searchType === 'all' ? [] : values
        this.serverForm = { ...this.serverForm, serviceName, serviceDesc, searchType, searchFieldsType, exists: existsList, values: valuesList }
      }
    },
    // 获取数据集详情
    async getDetail(params) {
      this.loadingFlag = true
      const res = await dataManageServer.queryDataset({ ...params })
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { datasetFields } = res.data
        const fieldsList = datasetFields.split(', ')
        this.selectedData = { ...res.data, fieldsList }
      }
    },
    async createService(params) {
      const res = await serviceManageServer.publishService(params)
      if (res.code === 0 && res.data) {
        console.log(res)
        this.$message.success('服务发布成功')
        this.$router.push({ path: 'serverManage' })
      }
    },
    async updateService(params) {
      const res = await serviceManageServer.updateService(params)
      if (res.code === 0) {
        console.log(res)
        this.$message.success('服务编辑成功')
        history.go(-1)
      }
    },
    selected(checked, row) {
      this.serverForm.exists = []
      if (checked) {
        const fieldsList = row.datasetFields.split(', ')
        this.selectedData = { ...row, fieldsList }
      } else {
        this.selectedData = {}
      }
    },
    checkService() {
      this.$refs.serverForm.validate((valid) => {
        if (valid) {
          if (!this.selectedData.datasetId) {
            this.$message.error('请选择数据集')
            return
          }
          const { serviceName, serviceDesc, searchType, searchFieldsType, exists, values } = this.serverForm
          const { datasetId } = this.selectedData
          if (searchType === 'all') {
            exists.push('*')
          }
          if (searchFieldsType === 'all') {
            values.push('*')
          }
          const serviceConfig = { datasetId, exists, values }
          if (this.type === 'edit') {
            this.updateService({ serviceId: this.serviceId, serviceName, serviceDesc, serviceConfig: JSON.stringify(serviceConfig), serviceType: 'pir' })
          } else {
            this.createService({ serviceName, serviceDesc, serviceConfig: JSON.stringify(serviceConfig), serviceType: 'pir' })
          }
        } else {
          return false
        }
      })
    },
    async getListDataset() {
      const { page_offset, page_size } = this.pageData
      // 仅选择自己的数据
      const params = { pageNum: page_offset, pageSize: page_size, ownerUserName: this.userId }
      this.loadingFlag = true
      const res = await dataManageServer.listDataset(params)
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { content = [], totalCount } = res.data
        this.dataList = content.map((v) => {
          return {
            ...v,
            showSelect: true,
            isOwner: v.ownerAgencyName === this.agencyId && v.ownerUserName === this.userId
          }
        })
        console.log(content, 'content', totalCount)
        this.total = totalCount
      } else {
        this.dataList = []
        this.total = 0
      }
    },
    // 分页切换
    paginationHandle(pageData) {
      console.log(pageData, 'pagData')
      this.pageData = { ...pageData }
      this.getAccountList()
    }
  }
}
</script>
<style lang="less" scoped>
div.create-data {
  div.card-container {
    overflow: hidden;
    margin-left: -16px;
    margin-right: -16px;
    div.card {
      float: left;
      background: #f6fcf9;
      height: auto;
      border: 1px solid #e0e4ed;
      border-radius: 12px;
      margin: 16px;
      width: calc(25% - 32px);
      box-sizing: border-box;
      min-width: 220px;
      position: relative;
      div.info {
        padding: 20px;
      }
      div.project-title {
        font-size: 16px;
        line-height: 24px;
        font-family: PingFang SC;
        margin-bottom: 24px;
        color: #262a32;
        display: flex;
        align-items: center;
        span {
          display: inline-block;
          width: 100%;
          text-align: left;
          font-weight: bold;
          overflow: hidden;
          flex: 1;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
        ::v-deep .el-checkbox__inner {
          border-radius: 50%;
          width: 20px;
          height: 20px;
          line-height: 20px;
          font-size: 16px;
          border: 1px solid #3071f2;
          box-shadow: 0 0 3px #3071f2;
        }
        ::v-deep .el-checkbox__inner::after {
          left: 7px;
          width: 4px;
          height: 8px;
          top: 3px;
        }
      }
      div.count-detail {
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
        dl {
          color: #787b84;
          dt {
            font-size: 12px;
            line-height: 20px;
          }
          dd {
            color: #262a32;
            font-size: 16px;
            line-height: 24px;
            font-weight: 500;
          }
        }
      }
      ul {
        li {
          font-size: 12px;
          line-height: 20px;
          margin-bottom: 4px;
          color: #787b84;
          display: flex;
          align-items: center;
          span {
            text-align: right;
            color: #262a32;
            flex: 1;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
          }
          span.data-size {
            i {
              font-size: 28px;
              font-style: normal;
            }
          }
        }
        li:first-child {
          line-height: 28px;
        }
      }
    }
  }
}
</style>
