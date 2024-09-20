import nodeUsable from '../../assets/images/node-usable.png'
import nodeDisable from '../../assets/images/node-disable.png'
export const colorList = ['#2F89F3', '#69CB92', '#FFA927', '#739AFC', '#EC744C']
export const circleOption = {
  tooltip: {
    trigger: 'item'
  },
  color: colorList,
  series: [
    {
      name: '来源',
      type: 'pie',
      radius: ['60%', '90%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        formatter: '{d}%',
        position: 'inner',
        color: 'white',
        fontSize: '8px'
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
}
export const barOption = {
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      // Use axis to trigger tooltip
      type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
    }
  },
  color: colorList,
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
}
export const lineOption = {
  color: colorList,
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
}
export const circleJobOption = {
  color: colorList,
  legend: {
    orient: 'vertical',
    right: '40px',
    top: 'center',
    textStyle: {
      color: 'white'
    },
    icon: 'circle',
    type: 'scroll',
    itemGap: 16
  },
  series: [
    {
      name: '来源',
      type: 'pie',
      radius: ['60%', '90%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        formatter: '{d}%',
        position: 'inner',
        color: 'white',
        fontSize: '8px'
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
}
export const barJobOption = {
  color: colorList,
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
    icon: 'circle',
    type: 'scroll',
    orient: 'horizontal' // vertical
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
}
export const lineJobOption = {
  color: colorList,
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
}
export const graphChartOption = {
  title: {
    text: ''
  },
  tooltip: {},
  animationDurationUpdate: 1500,
  animationEasingUpdate: 'quinticInOut',
  series: [
    {
      type: 'graph',
      layout: 'none',
      symbolSize: 50,
      roam: false,
      label: {
        show: true,
        position: 'top'
      },
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [4, 10],
      edgeLabel: {
        fontSize: 20
      },
      data: [
        {
          name: 'WeBank',
          symbol: 'image://' + nodeUsable,
          symbolSize: [100, 80],
          x: 300,
          y: 300
        },
        {
          name: 'SGD',
          symbol: 'image://' + nodeDisable,
          symbolSize: [100, 80],
          x: 800,
          y: 300
        },
        {
          name: 'WeBank1',
          symbol: 'image://' + nodeUsable,
          symbolSize: [100, 80],
          x: 550,
          y: 100
        },
        {
          name: 'WeBank2',
          symbol: 'image://' + nodeUsable,
          symbolSize: [100, 80],
          x: 550,
          y: 500
        }
      ],
      // links: [],
      links: [
        {
          source: 'WeBank',
          target: 'WeBank2',
          label: {
            show: false
          },
          lineStyle: {
            curveness: 0.2,
            color: '#60a9ff',
            type: 'dotted'
          }
        },
        {
          source: 'WeBank',
          target: 'SGD',
          label: {
            show: false
          },
          lineStyle: {
            curveness: 0.2,
            color: '#60a9ff',
            type: 'dotted'
          }
        },
        {
          source: 'WeBank',
          target: 'WeBank1',
          label: {
            show: false
          },
          lineStyle: {
            curveness: 0.2,
            color: '#60a9ff',
            type: 'dotted'
          }
        },
        {
          source: 'WeBank2',
          target: 'WeBank1',
          label: {
            show: false
          },
          lineStyle: {
            curveness: 0.2,
            color: '#60a9ff',
            type: 'dotted'
          }
        },
        {
          source: 'WeBank1',
          target: 'SGD',
          label: {
            show: false
          },
          lineStyle: {
            curveness: 0.2,
            color: '#ff4d4f',
            type: 'dotted'
          }
        },
        {
          source: 'SGD',
          target: 'WeBank1',
          label: {
            show: false
          },
          lineStyle: {
            curveness: -0.2,
            color: '#ff4d4f',
            type: 'dotted'
          }
        }
      ],
      lineStyle: {
        opacity: 0.9,
        width: 2,
        curveness: 0
      }
    }
  ]
}
