<template>
  <div class="create-data">
    <el-form :inline="false" @submit="queryHandle" :rules="rules" :model="dataForm" ref="dataForm" size="small">
      <formCard title="基础信息">
        <el-form-item label-width="98px" label="项目名称：" prop="name">
          <el-input style="width: 480px" placeholder="请输入" v-model="dataForm.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label-width="98px" label="项目简介：" prop="projectDesc">
          <el-input style="width: 480px" placeholder="请输入" v-model="dataForm.projectDesc" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label-width="98px" label="选择模式：" prop="type">
          <el-radio-group v-model="dataForm.type">
            <el-radio label="Expert">向导模式</el-radio>
            <el-radio label="Wizard">专家模式</el-radio>
          </el-radio-group>
        </el-form-item>
      </formCard>
    </el-form>
    <div>
      <el-button size="medium" icon="el-icon-plus" type="primary" @click="submit"> 确认创建 </el-button>
    </div>
  </div>
</template>
<script>
import { projectManageServer, jupyterManageServer } from 'Api'
import { mapGetters } from 'vuex'
export default {
  name: 'projectCreate',
  data() {
    return {
      dataForm: {
        name: '',
        projectDesc: '',
        type: ''
      },
      rules: {
        name: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
        projectDesc: [{ required: true, message: '数据集不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '模式不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['userId', 'authorization'])
  },
  methods: {
    // 获取项目详情
    async queryProject() {
      this.loadingFlag = true
      const { projectId } = this
      const res = await projectManageServer.queryProject({ project: { id: projectId } })
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        const { dataList = [] } = res.data
        this.dataInfo = dataList[0] || {}
      } else {
        this.dataInfo = []
      }
    },
    async createProject(params) {
      const res = await projectManageServer.createProject(params)
      console.log(res)
      if (res.code === 0) {
        this.$router.push({ path: '/projectManage' })
      }
    },

    async getJupterLink(params) {
      const res = await jupyterManageServer.getJupterLink(params)
      console.log(res)
      if (res.code === 0 && res.data) {
        const { jupyters = [] } = res.data
        if (jupyters.length) {
          const { url = 'http://139.159.202.235:9401/lab' } = jupyters[0]
          window.open(url + '?Authorization=' + this.authorization)
        } else {
          window.open('http://139.159.202.235:9401/lab?Authorization=' + this.authorization)
        }
      }
    },
    submit() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          const { name, projectDesc, type } = this.dataForm
          if (type === 'Wizard') {
            this.getJupterLink({ id: this.userId })
          } else {
            this.createProject({ project: { name, projectDesc, type } })
          }
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
div.create-data {
  .el-checkbox {
    display: block;
    margin-bottom: 16px;
  }
}
</style>
