<template>
  <div class="record">
    <div class="card-container" v-if="modelTableData.length">
      <modelCard
        v-for="item in modelTableData"
        :selected="value === item.setting"
        @selected="(data) => hanleSelectedModel(data, item)"
        :modelInfo="item"
        :key="item.id"
      ></modelCard>
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
</template>
<script>
import { settingManageServer } from 'Api'
import wePagination from '@/components/wePagination.vue'
import { mapGetters } from 'vuex'
import modelCard from '@/components/modelCard.vue'

export default {
  name: 'modelSelect',
  model: {
    prop: 'value'
  },
  props: {
    value: {
      type: String,
      default: ''
    }
  },
  components: {
    modelCard,
    wePagination
  },
  data() {
    return {
      loadingFlag: false,
      pageData: { page_offset: 1, page_size: 8 },
      modelTableData: []
    }
  },
  created() {
    this.getModelData()
  },
  computed: {
    ...mapGetters(['userId', 'agencyId'])
  },
  methods: {
    hanleSelectedModel(selected, item) {
      if (selected) {
        this.$emit('input', item.setting)
      } else {
        this.$emit('input', '')
      }
    },
    async getModelData() {
      const { page_offset, page_size } = this.pageData
      const params = { pageNum: page_offset, pageSize: page_size }
      this.loadingFlag = true
      const res = await settingManageServer.querySettings({
        onlyMeta: false,
        ...params,
        condition: {
          id: '',
          name: '',
          type: 'MODEL_SETTING',
          owner: ''
        }
      })
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { dataList, total } = res.data
        this.modelTableData = dataList.map((v) => {
          const setting = JSON.parse(v.setting)
          const { label_provider, label_column, participant_agency_list } = setting
          const participant_agency_list_filtered = participant_agency_list.map((v) => {
            return {
              ...v,
              isLabelProvider: v.agency === label_provider
            }
          })
          return {
            ...v,
            label_column,
            participant_agency_list: participant_agency_list_filtered
          }
        })
        this.total = total
      } else {
        this.modelTableData = []
        this.total = 0
      }
    },
    paginationHandle(pageData) {
      this.pageData = { ...pageData }
      this.getModelData()
    }
  }
}
</script>
<style lang="less" scoped>
.select-data {
  border: 1px solid #e0e4ed;
  border-radius: 4px;
  padding: 20px;
  height: auto;
  .el-empty {
    margin-top: 0;
  }
}
.card-container {
  margin: -10px -10px;
  max-height: 410px;
  overflow: auto;
}

::v-deep div.data-card {
  margin: 10px;
  ul li:first-child {
    line-height: 26px;
    margin-bottom: 8px;
  }
  ul li span.data-size {
    i {
      font-size: 18px;
      line-height: 26px;
    }
  }
}
</style>
