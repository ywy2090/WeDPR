<template>
  <div class="card" :key="modelInfo.id">
    <div class="info">
      <div class="project-title">
        <span :title="modelInfo.name">{{ modelInfo.name }}</span>
        <el-checkbox @change="handleSelect" :value="selected"></el-checkbox>
      </div>
      <div class="count-detail" v-if="false">
        <dl>
          <dt>标签方</dt>
          <dd>6</dd>
        </dl>
        <dl>
          <dt>参与方</dt>
          <dd>6</dd>
        </dl>
        <dl>
          <dt>标签字段</dt>
          <dd>6</dd>
        </dl>
      </div>
      <ul>
        <li class="ell" v-for="item in modelInfo.participant_agency_list" :key="item.agency" :title="item.agency + ',字段' + item.fields">
          {{ item.isLabelProvider ? '标签方：' : '机构方：' }} <span>{{ item.agency }},字段{{ item.fields }}</span>
        </li>
        <li class="ell">
          标签字段： <span>{{ modelInfo.label_column }}</span>
        </li>
        <li class="ell">
          发起人： <span>{{ modelInfo.owner }}</span>
        </li>
        <li>
          创建时间： <span :title="modelInfo.createTime">{{ modelInfo.createTime }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'dataCard',
  props: {
    modelInfo: {
      type: Object,
      default: () => {}
    },
    selected: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  },
  computed: {},
  methods: {
    handleSelect(checked) {
      this.$emit('selected', checked)
    }
  }
}
</script>

<style scoped lang="less">
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
</style>
