<template>
  <div class="screen">
    <div class="top">
      <div class="back"><img @click="backHome" src="~Assets/images/back.png" alt="" /></div>
      <div class="title">管理监控平台</div>
      <div class="time">2024 年 06 月 04 日 星期二 12:34:05</div>
    </div>
    <div class="bottom">
      <div class="left slide-con">
        <div class="title">
          <div class="img-container">
            <img src="~Assets/images/lead-data.png" alt="" />
          </div>
          <el-progress style="width: 160px" :stroke-width="8" :percentage="datasetUseInfo.usedProportion" :show-text="false"></el-progress
          ><span class="ell rate" :title="`已使用${datasetUseInfo.usedCount}/${datasetUseInfo.totalCount}`">
            已使用{{ datasetUseInfo.usedCount }}/{{ datasetUseInfo.totalCount }}</span
          >
        </div>
        <div class="chart-container">
          <div class="chart-con circle">
            <div id="circle-chart" class="chart"></div>
            <div class="data-table">
              <ul class="head">
                <li>分类</li>
                <li>个数</li>
                <li>使用率</li>
              </ul>
              <ul v-for="item in datasetTypeStatisticTableData" :key="item.datasetType">
                <li>{{ item.datasetType }}</li>
                <li>{{ item.count }}</li>
                <li>{{ item.usedProportion }}</li>
              </ul>
            </div>
          </div>
          <div class="chart-con">
            <div id="bar-chart" class="chart"></div>
          </div>
          <div class="chart-con line-chart">
            <p>数据资源总数走势</p>
            <div id="line-chart" class="chart"></div>
          </div>
        </div>
      </div>
      <div class="center">
        <div id="graph-chart" class="chart"></div>
        <div class="notice-con">
          <div class="msg-table">
            <ul class="head">
              <li>网络实时动态</li>
            </ul>
            <ul v-for="item in logInfoList" :key="item.des">
              <li class="msg">{{ item.des }}</li>
              <li class="time">{{ item.createTime }}</li>
            </ul>
          </div>
          <div class="msg-table">
            <ul class="head">
              <li>告警通知</li>
            </ul>
            <ul>
              <li class="msg">A上传你数据集成功</li>
              <li class="time">2024-09-08 09:34:23</li>
            </ul>
            <ul>
              <li>A上传你数据集成功</li>
              <li>2024-09-08 09:34:23</li>
            </ul>
            <ul>
              <li>A上传你数据集成功</li>
              <li>2024-09-08 09:34:23</li>
            </ul>
          </div>
        </div>
      </div>
      <div class="right slide-con">
        <div class="title">
          <div class="img-container">
            <img style="width: 108px" src="~Assets/images/job_sat.png" alt="" />
          </div>
          <el-progress style="width: 160px" :stroke-width="8" :percentage="jobOverview.successProportion" :show-text="false"></el-progress
          ><span class="ell rate" :title="`成功执行任务${jobOverview.successCount}/${jobOverview.totalCount}`">
            成功执行任务{{ jobOverview.successCount }}/{{ jobOverview.totalCount }}</span
          >
        </div>
        <div class="chart-container">
          <div class="chart-con circle">
            <div id="job-circle-chart" class="chart"></div>
          </div>
          <div class="chart-con">
            <div id="job-bar-chart" class="chart"></div>
          </div>
          <div class="chart-con line-chart">
            <p>任务数量走势</p>
            <div id="job-line-chart" class="chart"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="copyRight">
      <img src="~Assets/images/copyRight.png" />
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { mapGetters } from 'vuex'
import { dashboardManageServer, logManageServer } from 'Api'
import { jobStatusMap, actionMap, actionScreenStatus } from 'Utils/constant.js'
import dayjs from 'dayjs'
export default {
  name: 'HomePage',
  props: {},
  data() {
    return {
      jobOverviewList: [],
      loadingFlag: false,
      option: {},
      dataList: [],
      dataTotal: 0,
      userCount: 0,
      groupCount: 0,
      projectTotal: 0,
      searchDateVal: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      searchTabIndex: 0,
      tableData: [],
      jobStatusMap,
      showModifyModal: false,
      myDataCircleChart: null,
      myDataLineChart: null,
      myDataBarChart: null,

      myJobCircleChart: null,
      myJobineChart: null,
      myJobBarChart: null,
      graphChart: null,
      resourceTypeMap: {},
      circleOption: {
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            name: '来源',
            type: 'pie',
            radius: ['60%', '90%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              //   { value: 1048, name: 'CSV文件' },
              //   { value: 735, name: 'EXCEL文件' },
              //   { value: 580, name: '数据库' },
              //   { value: 484, name: 'HIVE' },
              //   { value: 300, name: 'HDFS' }
            ]
          }
        ]
      },
      barOption: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // Use axis to trigger tooltip
            type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
          }
        },
        legend: {
          left: 'center',
          bottom: -6,
          textStyle: {
            color: 'white'
          },
          icon: 'circle'
        },
        grid: {
          left: 0, // 图表距离容器左侧多少距离
          right: 10, // 图表距离容器右侧侧多少距离
          bottom: 30, // 图表距离容器上面多少距离
          top: 10, // 图表距离容器下面多少距离
          containLabel: true // 防止标签溢出
        },
        xAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#EFF4F9'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        yAxis: {
          type: 'category',
          axisLine: {
            lineStyle: {
              color: '#EFF4F9'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          },
          textStyle: {
            color: 'white'
          },
          data: []
        },
        series: []
      },
      lineOption: {
        xAxis: {
          type: 'category',
          data: ['星期1', '星期2', '星期3', '星期4', '星期5', '星期6', '星期7'],
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          show: true, // 是否显示图表背景网格
          left: 0, // 图表距离容器左侧多少距离
          right: 10, // 图表距离容器右侧侧多少距离
          bottom: 30, // 图表距离容器上面多少距离
          top: 10, // 图表距离容器下面多少距离
          containLabel: true // 防止标签溢出
        },
        smooth: true,
        legend: {
          data: ['机构1', '机构2', '机构3'],
          textStyle: {
            color: 'white'
          },
          left: 'center',
          bottom: -6,
          icon: 'circle',
          type: 'scroll',
          orient: 'horizontal' // vertical
        },
        yAxis: {
          type: 'value',
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        series: [
          {
            data: [1, 2, 20, 4, 5, 6, 7], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构1', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          },
          {
            data: [1, 6, 3, 4, 15, 6, 9], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构2', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          },
          {
            data: [1, 4, 3, 4, 9, 6, 17], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构3', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          }
        ]
      },
      circleJobOption: {
        legend: {
          orient: 'vertical',
          right: '40px',
          top: 'center',
          textStyle: {
            color: 'white'
          },
          icon: 'circle'
        },
        series: [
          {
            name: '来源',
            type: 'pie',
            radius: ['60%', '90%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            center: ['30%', '50%'],
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              //   { value: 1048, name: 'CSV文件' },
              //   { value: 735, name: 'EXCEL文件' },
              //   { value: 580, name: '数据库' },
              //   { value: 484, name: 'HIVE' },
              //   { value: 300, name: 'HDFS' }
            ]
          }
        ],
        tooltip: {
          // trigger 设置触发类型，默认数据触发，可选值：'item' ¦ 'axis'
          trigger: 'item'
        }
      },
      barJobOption: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // Use axis to trigger tooltip
            type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
          }
        },
        legend: {
          left: 'center',
          bottom: -6,
          textStyle: {
            color: 'white'
          },
          icon: 'circle'
        },
        grid: {
          left: 0, // 图表距离容器左侧多少距离
          right: 10, // 图表距离容器右侧侧多少距离
          bottom: 30, // 图表距离容器上面多少距离
          top: 10, // 图表距离容器下面多少距离
          containLabel: true // 防止标签溢出
        },
        xAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#EFF4F9'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        yAxis: {
          type: 'category',
          axisLine: {
            lineStyle: {
              color: '#EFF4F9'
            }
          },
          textStyle: {
            color: 'white'
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          },
          data: []
        },
        series: []
      },
      lineJobOption: {
        xAxis: {
          type: 'category',
          data: ['星期1', '星期2', '星期3', '星期4', '星期5', '星期6', '星期7'],
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          show: true, // 是否显示图表背景网格
          left: 0, // 图表距离容器左侧多少距离
          right: 10, // 图表距离容器右侧侧多少距离
          bottom: 30, // 图表距离容器上面多少距离
          top: 10, // 图表距离容器下面多少距离
          containLabel: true // 防止标签溢出
        },
        smooth: true,
        legend: {
          data: ['机构1', '机构2', '机构3'],
          textStyle: {
            color: 'white'
          },
          left: 'center',
          bottom: -6,
          icon: 'circle',
          type: 'scroll',
          orient: 'horizontal' // vertical
        },
        yAxis: {
          type: 'value',
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#262A32'],
              width: 1,
              type: 'solid'
            }
          }
        },
        series: [
          {
            data: [1, 2, 20, 4, 5, 6, 7], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构1', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          },
          {
            data: [1, 6, 3, 4, 15, 6, 9], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构2', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          },
          {
            data: [1, 4, 3, 4, 9, 6, 17], // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: '机构3', // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          }
        ]
      },
      graphChartOption: {
        title: {
          text: 'Basic Graph'
        },
        tooltip: {},
        animationDurationUpdate: 1500,
        animationEasingUpdate: 'quinticInOut',
        series: [
          {
            type: 'graph',
            layout: 'none',
            symbolSize: 50,
            roam: true,
            label: {
              show: true
            },
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            edgeLabel: {
              fontSize: 20
            },
            data: [
              {
                name: 'Node 1',
                x: 300,
                y: 300
              },
              {
                name: 'Node 2',
                x: 800,
                y: 300
              },
              {
                name: 'Node 3',
                x: 550,
                y: 100
              },
              {
                name: 'Node 4',
                x: 550,
                y: 500
              }
            ],
            // links: [],
            links: [
              {
                source: 0,
                target: 1,
                symbolSize: [5, 20],
                label: {
                  show: true
                },
                lineStyle: {
                  width: 5,
                  curveness: 0.2
                }
              },
              {
                source: 'Node 2',
                target: 'Node 1',
                label: {
                  show: true
                },
                lineStyle: {
                  curveness: 0.2
                }
              },
              {
                source: 'Node 1',
                target: 'Node 3'
              },
              {
                source: 'Node 2',
                target: 'Node 3'
              },
              {
                source: 'Node 2',
                target: 'Node 4'
              },
              {
                source: 'Node 1',
                target: 'Node 4'
              }
            ],
            lineStyle: {
              opacity: 0.9,
              width: 2,
              curveness: 0
            }
          }
        ]
      },
      dataReferOverviewList: [
        {
          type: 'CSV文件',
          count: 200,
          useRate: '40%'
        },
        {
          type: 'EXCEL文件',
          count: 200,
          useRate: '40%'
        },
        {
          type: '数据库',
          count: 200,
          useRate: '40%'
        },
        {
          type: 'HIVE',
          count: 200,
          useRate: '40%'
        },
        {
          type: 'HDFS',
          count: 200,
          useRate: '40%'
        }
      ],
      datasetUseInfo: {
        usedCount: 0,
        totalCount: 0,
        usedProportion: 0
      },
      jobOverview: {
        successCount: 0,
        totalCount: 0,
        successProportion: 0
      },
      datasetTypeStatisticTableData: [],
      logInfoList: []
    }
  },
  computed: {
    ...mapGetters(['userId', 'agencyName', 'userinfo', 'algList', 'agencyAdmin', 'permission']),
    algListMap() {
      const data = {}
      this.algList.forEach((v) => {
        data[v.value] = v.label
      })
      console.log(data, 'this.algListMap(v.jobType)')
      return data
    }
  },
  created() {
    this.queryRecordSyncStatus()
  },

  mounted() {
    this.initData()
    this.initGraphChart()
    this.fullScreen()
    const that = this
    window.onresize = function () {
      that.myDataCircleChart && that.myDataCircleChart.resize()
      that.myDataBarChart && that.myDataBarChart.resize()
      that.myDataLineChart && that.myDataLineChart.resize()
      that.myJobCircleChart && that.myJobCircleChart.resize()
      that.myJobineChart && that.myJobineChart.resize()
      that.myJobBarChart && that.myJobBarChart.resize()
      that.graphChart && that.graphChart.resize()
    }
  },
  methods: {
    initData() {
      this.getDatasetInfo()
      this.getDatasetLineData()
      this.getJobInfo()
      this.getJobLineData()
    },
    fullScreen() {
      const full = document.getElementById('app')
      console.log(full, '=================')
      if (full.RequestFullScreen) {
        full.RequestFullScreen()
      } else if (full.mozRequestFullScreen) {
        full.mozRequestFullScreen()
      } else if (full.webkitRequestFullScreen) {
        full.webkitRequestFullScreen()
      } else if (full.msRequestFullscreen) {
        full.msRequestFullscreen()
      }
    },
    exitFullScreen() {
      if (document.exitFullScreen) {
        document.exitFullScreen()
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen()
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen()
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen()
      }
    },
    handlOK() {
      this.showModifyModal = false
    },
    closeModal() {
      this.showModifyModal = false
    },
    modifyShow() {
      this.showModifyModal = true
    },
    handleData(key) {
      const data = this.algList.filter((v) => v.value === key)
      return data[0] || {}
    },
    initCircleData() {
      const chartDom = document.getElementById('circle-chart')
      if (chartDom) {
        this.myDataCircleChart = echarts.init(chartDom)
        this.myDataCircleChart && this.myDataCircleChart.setOption(this.circleOption)
      }
    },
    initBarData() {
      const chartDom = document.getElementById('bar-chart')
      if (chartDom) {
        this.myDataBarChart = echarts.init(chartDom)
        this.myDataBarChart && this.myDataBarChart.setOption(this.barOption)
      }
    },
    initLineData() {
      const chartDom = document.getElementById('line-chart')
      if (chartDom) {
        this.myDataLineChart = echarts.init(chartDom)
        this.myDataLineChart && this.myDataLineChart.setOption(this.lineOption)
      }
    },
    initJobCircleData() {
      const chartDom = document.getElementById('job-circle-chart')
      if (chartDom) {
        this.myJobCircleChart = echarts.init(chartDom)
        console.log(this.circleJobOption, ' this.circleJobOption')
        this.myJobCircleChart && this.myJobCircleChart.setOption(this.circleJobOption)
      }
    },
    initJobBarData() {
      const chartDom = document.getElementById('job-bar-chart')
      if (chartDom) {
        this.myDataBarChart = echarts.init(chartDom)
        this.myDataBarChart && this.myDataBarChart.setOption(this.barJobOption)
      }
    },
    initJobLineData() {
      const chartDom = document.getElementById('job-line-chart')
      if (chartDom) {
        this.myDataLineChart = echarts.init(chartDom)
        this.myDataLineChart && this.myDataLineChart.setOption(this.lineJobOption)
      }
    },
    initGraphChart() {
      const chartDom = document.getElementById('graph-chart')
      if (chartDom) {
        this.graphChart = echarts.init(chartDom)
        this.graphChart && this.graphChart.setOption(this.graphChartOption)
      }
    },
    backHome() {
      this.exitFullScreen()
      history.go(-1)
    },
    moreJob() {
      this.$router.push({ path: 'messageManage' })
    },
    goTaskDetail(item) {
      this.$router.push({ path: 'jobDetail', query: { id: item.id } })
    },
    goPage(path) {
      this.$router.push({ path })
    },
    // 获取数据集使用信息
    async getDatasetInfo() {
      const res = await dashboardManageServer.getDatasetInfo()
      console.log(res)
      if (res.code === 0 && res.data) {
        const { datasetOverview = {}, datasetTypeStatistic = [], agencyDatasetTypeStatistic = [] } = res.data
        this.datasetUseInfo = { ...datasetOverview } // progeress
        this.datasetTypeStatisticTableData = datasetTypeStatistic // 饼图
        this.circleOption.series[0].data = datasetTypeStatistic.map((v) => {
          return {
            name: v.datasetType,
            value: v.count
          }
        })
        this.initCircleData()
        const agencyNameList = agencyDatasetTypeStatistic.map((v) => v.agencyName)
        this.barOption.yAxis.data = agencyNameList
        const datasetTypeList = datasetTypeStatistic.map((v) => v.datasetType)
        console.log(datasetTypeList, 'datasetTypeList')
        this.barOption.series = datasetTypeList.map((dataType) => {
          const countList = []
          agencyDatasetTypeStatistic.forEach((agencyData) => {
            const { datasetTypeStatistic } = agencyData
            const data = datasetTypeStatistic.filter((data) => data.datasetType === dataType)
            if (data.length) {
              countList.push(data[0].count)
            } else {
              countList.push(0)
            }
          })
          console.log(countList, 'countList')
          return {
            name: dataType,
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              barWidth: 6 // 设置柱子粗细
            },
            data: countList
          }
        })
        this.initBarData()
        console.log(agencyDatasetTypeStatistic)
        // console.log(datasetTypeList)
      }
    },
    // 数据集趋势图
    async getDatasetLineData() {
      const start = new Date()
      const end = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 6)
      const res = await dashboardManageServer.getDatasetLineData({ startTime: dayjs(start).format('YYYY-MM-DD'), endTime: dayjs(end).format('YYYY-MM-DD') })
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        console.log(res)
        const { agencyDatasetStat = [] } = res.data
        const dateList = agencyDatasetStat[0].dateList
        const agencylist = agencyDatasetStat.map((v) => v.agencyName)
        const series = agencyDatasetStat.map((v) => {
          return {
            data: v.countList, // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: v.agencyName, // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          }
        })
        this.lineOption.xAxis.data = dateList
        this.lineOption.legend.data = agencylist
        this.lineOption.legend.left = agencylist.length > 5 ? '20px' : 'center'
        this.lineOption.series = series
        this.initLineData()
      }
    },
    // 获取任务信息
    async getJobInfo() {
      const res = await dashboardManageServer.getJobInfo()
      console.log(res)
      if (res.code === 0 && res.data) {
        const { jobOverview = {}, jobTypeStatistic = [], agencyJobTypeStatistic = [] } = res.data
        this.jobOverview = { ...jobOverview } // progeress
        this.circleJobOption.series[0].data = jobTypeStatistic.map((v) => {
          return {
            name: v.jobType,
            value: v.count
          }
        })
        const seriesData = this.circleJobOption.series[0].data
        this.circleJobOption.legend.formatter = function (name) {
          let tarValue
          for (let i = 0; i < seriesData.length; i++) {
            if (seriesData[i].name === name) {
              tarValue = seriesData[i].value
            }
          }
          return `${name}  ${tarValue}个`
        }
        this.initJobCircleData()
        const agencyNameList = agencyJobTypeStatistic.map((v) => v.agencyName)
        this.barJobOption.yAxis.data = agencyNameList
        const jobTypeList = jobTypeStatistic.map((v) => v.jobType)
        console.log(jobTypeList, 'jobTypeList')
        this.barJobOption.series = jobTypeList.map((jobType) => {
          const countList = []
          agencyJobTypeStatistic.forEach((agencyData) => {
            const { jobTypeStatistic } = agencyData
            const data = jobTypeStatistic.filter((data) => data.jobType === jobType)
            if (data.length) {
              countList.push(data[0].count)
            } else {
              countList.push(0)
            }
          })
          return {
            name: jobType,
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              barWidth: 6 // 设置柱子粗细
            },
            data: countList
          }
        })
        this.initJobBarData()
        console.log(agencyJobTypeStatistic)
      }
    },
    // 任务趋势图
    async getJobLineData(params) {
      const start = new Date()
      const end = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 6)
      console.log(start, end)
      const res = await dashboardManageServer.getJobLineData({ startTime: '2024-09-11', endTime: '2024-09-17' })
      this.loadingFlag = false

      if (res.code === 0 && res.data) {
        console.log(res)
        const { jobTypeStat = [] } = res.data
        const dateList = jobTypeStat[0].dateList
        // FIXME:
        const jobTypeList = jobTypeStat.map((v) => this.algListMap[v.jobType] || v.jobType)
        const series = jobTypeStat.map((v) => {
          return {
            data: v.countList, // 具体数据
            type: 'line', // 设置图表类型为折线图
            name: this.algListMap[v.jobType] || v.jobType, // 图表名称
            smooth: true // 是否将折线设置为平滑曲线
          }
        })
        this.lineJobOption.xAxis.data = dateList
        this.lineJobOption.legend.data = jobTypeList
        this.lineJobOption.legend.left = jobTypeList.length > 5 ? '20px' : 'center'
        this.lineJobOption.series = series
        console.log(this.lineJobOption, 'this.lineJobOption')
        this.initJobLineData()
      }
    },
    async queryRecordSyncStatus() {
      const params = { pageNum: 1, pageSize: 5 }
      const res = await logManageServer.queryRecordSyncStatus(params)
      console.log(res)
      if (res.code === 0 && res.data) {
        const { dataList = [] } = res.data
        this.logInfoList = dataList
          .map((v) => {
            const { resourceAction, createTime, status } = v
            const des = actionMap[resourceAction] + actionScreenStatus[status]
            return {
              des,
              createTime
            }
          })
          .splice(0, 3)
      }
    },
    goDataDetail(data) {
      const { datasetId } = data
      this.$router.push({ path: 'dataDetail', query: { datasetId } })
    },
    goDataListPage() {
      this.$router.push({ path: 'dataManage' })
    }
  }
}
</script>

<style lang="less" scoped>
div.screen {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: rgba(17, 17, 17);
  color: white;
  padding: 0 24px;
  div.top {
    display: flex;
    width: 100%;
    height: 64px;
    text-align: center;
    line-height: 64px;
    div.back {
      width: 30%;
      text-align: left;
      display: flex;
      align-items: center;
      img {
        cursor: pointer;
        width: 84px;
        height: auto;
      }
    }
    div.time {
      width: 30%;
      text-align: right;
      color: #95989d;
      font-size: 16px;
    }
    div.title {
      width: 40%;
      font-size: 26px;
      font-weight: 500;
      background: linear-gradient(90deg, rgba(35, 40, 52, 0) 12.37%, rgba(103, 118, 154, 0.3) 49.53%, rgba(35, 40, 52, 0) 86.69%);
    }
  }
  div.bottom {
    height: calc(100% - 128px);
    width: 100%;
    display: flex;
    background: url('~Assets/images/bg_screen.jpg');
    background-size: cover;
    div.slide-con {
      width: 30%;
      .title {
        height: 68px;
        display: flex;
        align-items: center;
        padding: 20px 0;
        margin-bottom: 28px;
        .img-container {
          flex: 1;
          img {
            width: 148px;
            height: auto;
          }
        }

        .el-progress {
          width: 160px;
          display: inline-block;
        }
        .el-progress__text {
          display: none;
        }
        span.ell.rate {
          margin-left: 12px;
          max-width: 160px;
        }
      }
      div.chart-container {
        height: calc(100% - 96px);
        width: 100%;
      }
      .data-table {
        ul {
          display: flex;
          justify-content: space-between;
          li {
            height: 34px;
            font-size: 14px;
            line-height: 34px;
            text-align: center;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 33%;
          }
        }
        .head {
          background-color: rgba(38, 43, 55);
        }
      }
      .chart-con {
        width: 100%;
        height: calc(33%);
        padding-bottom: 45px;
      }
      .line-chart {
        padding-bottom: 0;
        p {
          text-align: center;
          color: #edf0f6;
          font-size: 14px;
          margin-bottom: 14px;
        }
        div {
          height: calc(100% - 28px);
        }
      }
      .circle {
        display: flex;
        div.data-table {
          width: 60%;
          background-color: rgba(17, 17, 17);
        }
        #circle-chart {
          width: 40%;
          height: 100%;
        }
        #job-circle-chart {
          width: 100%;
          height: 100%;
        }
      }
    }
    .chart {
      height: 100%;
      width: 100%;
    }
    div.center {
      width: 40%;
      height: 100%;
      padding: 0 24px;
      #graph-chart {
        height: calc(100% - 134px);
      }
      .notice-con {
        height: 133px;
        display: flex;
        justify-content: space-between;
        div.msg-table {
          width: calc(50% - 13px);
          ul {
            display: flex;
            justify-content: space-between;
            padding: 0 10px;
            background: rgb(14, 14, 14);
            li {
              height: 34px;
              font-size: 14px;
              line-height: 34px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            li.msg {
              flex: 1;
              padding-right: 10px;
              text-align: left;
            }
            li.time {
              width: auto;
            }
          }
          .head {
            background-color: rgba(38, 43, 55);
          }
        }
      }
    }
  }
  div.copyRight {
    height: 64px;
    width: 100%;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    img {
      width: 124px;
      height: auto;
    }
  }
}
</style>
