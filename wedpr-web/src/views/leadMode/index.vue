<template>
  <div class="lead-mode">
    <ul>
      <li><span>项目名称：</span>{{ dataInfo.name }}</li>
      <li><span>项目简介：</span>{{ dataInfo.projectDesc }}</li>
    </ul>
    <div class="step-container">
      <el-steps :active="active" finish-status="success">
        <el-step title="选择模型"></el-step>
        <el-step title="选择数据资源"></el-step>
        <el-step title="配置并运行"></el-step>
        <el-step title="查看结果"></el-step>
      </el-steps>
    </div>

    <formCard key="1" title="请选择模型" v-show="active === 0">
      <div class="alg-container">
        <div :class="selectedAlg.value === item.value ? 'alg active' : 'alg'" v-for="item in algListFull" @click="selectAlg(item)" :key="item.value">
          <img :src="item.jobSrc" alt="" />
          <span class="title">{{ item.label }}</span>
        </div>
      </div>
    </formCard>
    <div v-show="active === 1">
      <div class="tags data-container" v-if="selectedAlg.needTagsProvider">
        <p>
          选择标签数据
          <span class="btn" @click="removeTag" v-if="tagSelectList.length"> <img src="~Assets/images/icon_delete.png" alt="" /> 移除 </span>
        </p>
        <div class="area" @click="addTag" v-if="!tagSelectList.length">
          <img src="~Assets/images/add_dataset.png" alt="" />
          <div>点击选择数据</div>
        </div>
        <div class="area table-area" v-else>
          <el-table size="small" :data="tagSelectList" :border="true" class="table-wrap">
            <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
            <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
            <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
            <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
            <el-table-column label="包含字段" prop="datasetFields" show-overflow-tooltip />
            <el-table-column label="已选标签字段" prop="selectedTagFields" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
      <div class="participates data-container" v-for="(item, index) in paticipateSelectList" :key="item">
        <p>
          选择参与方数据 {{ selectedAlg.value === jobEnum.XGB_TRAINING ? '' : index + 1 }}
          <span class="btn" @click="removeParticipate(index)" v-if="item.datasetId"><img src="~Assets/images/icon_delete.png" alt="" /> 移除 </span>
        </p>
        <div class="area" @click="showAddParticipate(index)" v-if="!item.datasetId">
          <img src="~Assets/images/add_dataset.png" alt="" />
          <div>点击选择数据</div>
        </div>
        <div class="area table-area" v-else>
          <el-table size="small" :data="[item]" :border="true" class="table-wrap">
            <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
            <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
            <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
            <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
            <el-table-column label="包含字段" prop="datasetFields" show-overflow-tooltip />
          </el-table>
        </div>
      </div>
      <div class="add" @click="showAddParticipate">
        <span><img src="~Assets/images/add_participate.png" alt="" />增加参与方</span>
      </div>
    </div>
    <div v-show="active === 2">
      <el-form v-if="selectedAlg.value === jobEnum.XGB_TRAINING" label-width="200px" :model="jobSettingForm" ref="jobSettingForm" :rules="jobSettingFormRules">
        <div class="participates data-container">
          <p>已选数据</p>
          <div class="area table-area">
            <el-table size="small" :data="jobSettingForm.selectedData" :border="true" class="table-wrap">
              <el-table-column label="角色" prop="ownerAgencyName" show-overflow-tooltip>
                <template v-slot="scope">
                  <el-tag color="#4384ff" style="color: white" v-if="scope.row.selectedTagFields" size="small">标签方</el-tag>
                  <el-tag color="#4CA9EC" style="color: white" v-if="!scope.row.selectedTagFields" size="small">参与方</el-tag>
                </template>
              </el-table-column>

              <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
              <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
              <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
              <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
              <el-table-column label="已选标签字段" prop="selectedTagFields" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <formCard key="set" title="请设置参数">
          <div class="alg-container">
            <el-form-item label="选择历史参数：" prop="setting">
              <el-select size="small" value-key="id" @change="handleSelectSetting" style="width: 360px" v-model="model_setting" placeholder="请选择">
                <el-option :key="item" v-for="item in modelSettingList" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item v-for="item in modelModule" :key="item.label" :label="item.label">
              <el-input-number size="small" v-if="item.type === 'float'" v-model="item.value" :step="0.1" style="width: 140px" :min="item.min_value" :max="item.max_value" />
              <el-input size="small" v-if="item.type === 'string'" v-model="item.value" style="width: 140px" />
              <el-input-number
                size="small"
                v-if="item.type === 'int'"
                v-model="item.value"
                :step="1"
                :min="item.min_value"
                :max="item.max_value"
                step-strictly
                style="width: 140px"
              />
              <el-radio-group v-if="item.type === 'bool'" v-model="item.value">
                <el-radio :label="1"> true </el-radio>
                <el-radio :label="0"> false </el-radio>
              </el-radio-group>
              <span v-if="item.type !== 'bool'" class="tips">{{ item.description }}</span>
            </el-form-item>
          </div>
        </formCard>
        <el-form-item label="结果接收方：" prop="receiver" label-width="120px">
          <el-select size="small" style="width: 360px" v-model="jobSettingForm.receiver" multiple placeholder="请选择">
            <el-option :key="item" v-for="item in agencyList" multiple :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form v-if="selectedAlg.value === jobEnum.SQL" key="3" label-width="200px" :model="jobSettingForm" ref="jobSettingForm" :rules="jobSettingFormRules">
        <div class="participates data-container">
          <p>已选数据</p>
          <div class="area table-area">
            <el-table size="small" :data="jobSettingForm.selectedData" :border="true" class="table-wrap">
              <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
              <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
              <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
              <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
              <el-table-column label="包含字段" prop="ownerUserName" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <formCard key="SQL" class="sql-card" title="编写SQL语句">
          <el-form-item label="" prop="sql" label-width="0">
            <div class="sql-container">
              <div class="modify-container">
                <editorCom v-model="jobSettingForm.sql" />
              </div>
              <div class="lead"><img src="~Assets/images/icon_guide.png" /> 语法指引及示例下载</div>
            </div>
          </el-form-item>
        </formCard>
        <el-form-item label="结果接收方：" prop="receiver" label-width="120px">
          <el-select size="small" style="width: 360px" v-model="jobSettingForm.receiver" multiple placeholder="请选择">
            <el-option :key="item" v-for="item in agencyList" multiple :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form v-if="selectedAlg.value === jobEnum.PIR" key="3" label-width="200px" :model="jobSettingForm" ref="jobSettingForm" :rules="jobSettingFormRules">
        <div class="participates data-container">
          <p>已选数据</p>
          <div class="area table-area">
            <el-table size="small" :data="jobSettingForm.selectedData" :border="true" class="table-wrap">
              <el-table-column label="角色" prop="ownerAgencyName" show-overflow-tooltip>
                <template>
                  <el-tag color="#4CA9EC" style="color: white" size="small">标签方</el-tag>
                </template>
              </el-table-column>

              <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
              <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
              <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
              <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
            </el-table>
          </div>
        </div>
        <formCard key="PIR" title="请设置查询规则">
          <el-form-item label="查询类型：" prop="queryType" label-width="120px">
            <el-radio-group v-model="jobSettingForm.queryType">
              <el-radio :label="1">查询存在性</el-radio>
              <el-radio :label="2">查询字段值</el-radio>
              （默认查询主键为id）
            </el-radio-group>
          </el-form-item>
          <el-form-item label="选择字段：" prop="dataFields" label-width="120px">
            <el-cascader
              style="width: 480px"
              :show-all-levels="false"
              :options="pirOptions"
              :emitPath="false"
              v-model="jobSettingForm.dataFields"
              :props="{ multiple: true }"
              @change="handleFieldsChange(data)"
              :popper-class="hide"
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item
            v-if="jobSettingForm.queryType === 2 && jobSettingForm.fieldsValueList && jobSettingForm.fieldsValueList.length"
            label="字段值："
            prop="fieldsValueList"
            label-width="120px"
          >
            <el-form-item style="margin-bottom: 10px" :prop="`fieldsValueList.${index}.label`" :key="index" v-for="(fields, index) in jobSettingForm.fieldsValueList">
              <el-input size="small" v-model="fields.value" placeholder="请输入字段值" style="width: 480px">
                <template slot="prepend">{{ fields.label }} </template>
              </el-input>
            </el-form-item>
          </el-form-item>
        </formCard>
      </el-form>
      <div v-if="selectedAlg.value === jobEnum.PSI">
        <el-form label-width="200px" :model="jobSettingForm" ref="jobSettingForm" :rules="jobSettingFormRules">
          <div class="participates data-container">
            <p>选择数据字段</p>
            <div class="area table-area">
              <el-table size="small" :data="jobSettingForm.selectedData" :border="true" class="table-wrap">
                <el-table-column label="机构ID" prop="ownerAgencyName" show-overflow-tooltip />
                <el-table-column label="数据资源名称" prop="datasetTitle" show-overflow-tooltip />
                <el-table-column label="已选资源ID" prop="datasetId" show-overflow-tooltip />
                <el-table-column label="所属用户" prop="ownerUserName" show-overflow-tooltip />
                <el-table-column label="包含字段" prop="datasetFields" show-overflow-tooltip>
                  <template v-slot="scope">
                    <el-select size="small" v-model="scope.row.datasetFieldsSelected" placeholder="请选择" multiple>
                      <el-option :key="item" v-for="item in scope.row.datasetFields.trim().split(',')" :label="item" :value="item"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <el-form-item label="结果接收方：" prop="receiver" label-width="120px">
            <el-select size="small" style="width: 360px" v-model="jobSettingForm.receiver" multiple placeholder="请选择">
              <el-option :key="item" v-for="item in agencyList" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div>
      <el-button size="medium" v-if="active > 0" @click="pre"> 上一步 </el-button>
      <el-button size="medium" v-if="active < 2" type="primary" @click="next" :disabled="nextDisabaled"> 下一步 </el-button>
      <el-button size="medium" v-if="active === 2" type="primary" @click="runJob" :disabled="runDisabaled"> 运行 </el-button>
    </div>
    <tagSelect :showTagsModal="showTagsModal" @closeModal="closeModal" @tagSelected="tagSelected"></tagSelect>
    <participateSelect :showParticipateModal="showParticipateModal" @closeModal="closeModal" @participateSelected="participateSelected"></participateSelect>
  </div>
</template>
<script>
import formCard from '@/components/formCard.vue'
// import dataCard from '@/components/dataCard.vue'
import { settingManageServer, projectManageServer } from 'Api'
import tagSelect from './tagSelect/index.vue'
import participateSelect from './participateSelect/index.vue'
import { mapGetters } from 'vuex'
import { algListFull, jobEnum } from 'Utils/constant.js'
import editorCom from '@/components/editorCom.vue'

export default {
  name: 'leadMode',
  components: {
    formCard,
    tagSelect,
    participateSelect,
    editorCom
    // dataCard
  },
  data() {
    return {
      active: 0,
      activeName: 'first',
      dataForm: {
        setting: []
      },
      jobSettingForm: {
        receiver: [],
        selectedData: [],
        sql: '',
        queryType: 1,
        dataFields: [],
        fieldsValueList: []
      },
      jobSettingFormRules: {
        receiver: [{ required: true, message: '结果接收方不能为空', trigger: 'blur' }],
        selectedData: [{ required: true, message: '参与方不能为空', trigger: 'blur' }],
        sql: [{ required: true, message: 'sql内容不能为空', trigger: 'blur' }],
        queryType: [{ required: true, message: '请选择查询类型', trigger: 'blur' }]
      },
      modelModule: [
        {
          type: 'float',
          min_value: 0,
          max_value: 1,
          value: 0.5,
          label: '测试值：',
          description: '我是测试值'
        },
        {
          type: 'float',
          min_value: 0,
          max_value: 1,
          value: 0.5,
          label: '测试值1：',
          description: '我是测试值'
        }
      ],
      selectedAlg: {},
      algListFull,
      showTagsModal: false,
      showParticipateModal: false,
      pageData: {},
      tagSelectList: [],
      paticipateSelectList: [{}],
      dataInfo: {},
      jobEnum,
      modelSettingList: [],
      model_setting: '',
      addContainerIndex: 0
    }
  },
  created() {
    const { projectId } = this.$route.query
    this.projectId = projectId
    projectId && this.queryProject()
  },
  watch: {
    selectedAlg(selectedAlg) {
      console.log(selectedAlg)
      const { participateNumber } = selectedAlg
      this.paticipateSelectList = []
      for (let i = 0; i < participateNumber; i++) {
        this.paticipateSelectList.push({})
      }
      console.log(participateNumber, this.paticipateSelectList)
      switch (selectedAlg.value) {
        case jobEnum.XGB_TRAINING:
          this.queryModelSettingList()
          break
        default:
          break
      }
    },
    selectedData(v) {
      console.log(v, 'selectedData=================')
      if (this.selectedAlg.value === jobEnum.PSI) {
        this.jobSettingForm.selectedData = v.map((v) => {
          return { ...v, datasetFieldsSelected: [] }
        })
      } else {
        this.jobSettingForm.selectedData = v.map((v) => {
          return { ...v }
        })
      }
    }
  },
  computed: {
    ...mapGetters(['agencyList', 'algList']),
    nextDisabaled() {
      if (this.active === 0) {
        return !this.selectedAlg.value
      }
      return false
    },
    // 组合处理选中的dataset
    selectedData() {
      const paticipateData = this.paticipateSelectList.map((v) => {
        // v.datasetFields 兼容后台服务
        return { ...v, datasetFields: v.datasetFields || '', datasetFieldsSelected: [] }
      })
      return [...this.tagSelectList, ...paticipateData]
    },
    runDisabaled() {
      const type = this.selectedAlg.value
      let disabled = true
      let selectedFields = false
      switch (type) {
        case jobEnum.PSI:
          selectedFields = this.jobSettingForm.selectedData.every((v) => v.datasetFieldsSelected.length)
          disabled = !(this.jobSettingForm.receiver.length && selectedFields)
          break
        default:
          disabled = !this.jobSettingForm.receiver.length
          break
      }
      return disabled
    },
    pirOptions() {
      if (this.jobSettingForm.selectedData.length) {
        const { datasetFields = '' } = this.jobSettingForm.selectedData[0]
        const fields = datasetFields.trim().split(',')
        const children = fields.map((v) => {
          return {
            label: v,
            value: v
          }
        })
        return [
          {
            value: 0,
            label: '所有字段',
            children
          }
        ]
      } else {
        return []
      }
    }
  },
  methods: {
    handleFieldsChange() {
      console.log(this.jobSettingForm.dataFields, 'handleChange')
      this.jobSettingForm.fieldsValueList = this.jobSettingForm.dataFields.map((v) => {
        return {
          value: '',
          label: v[1]
        }
      })
      console.log(this.jobSettingForm.fieldsValueList, 'this.jobSettingForm.fieldsValueList')
    },
    checkJobData() {
      this.$refs.jobSettingForm.validate((valid) => {
        if (valid) {
          const { value } = this.selectedAlg
          switch (value) {
            case jobEnum.PSI:
              this.handlePsiJobData()
              break
            case jobEnum.XGB_TRAINING:
              this.handleXGBdata()
              break
            default:
              break
          }
        }
      })
    },
    runJob() {
      console.log('run start')
      this.checkJobData()
    },
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

    handlePsiJobData() {
      const { selectedAlg } = this
      const { selectedData, receiver } = this.jobSettingForm
      const { name } = this.dataInfo
      const dataSetList = selectedData.map((v) => {
        console.log(v, v.datasetStoragePath, JSON.parse(v.datasetStoragePath))
        const dataset = {
          owner: v.ownerUserName,
          ownerAgency: v.ownerAgencyName,
          path: JSON.parse(v.datasetStoragePath).filePath,
          storageTypeStr: v.datasetStorageType,
          datasetID: v.datasetId
        }
        return {
          idFields: v.datasetFieldsSelected,
          dataset,
          receiveResult: receiver.includes(v.ownerAgencyName)
        }
      })
      const param = { dataSetList }
      const params = { jobType: selectedAlg.value, projectName: name, param: JSON.stringify(param) }
      const taskParties = selectedData.map((v) => {
        return {
          userName: v.ownerUserName,
          agency: v.ownerAgencyName
        }
      })
      const datasetList = selectedData.map((v) => v.datasetId)
      this.submitJob({ job: params, taskParties, datasetList })
    },
    handleXGBdata() {
      const { selectedAlg, modelModule } = this
      const { selectedData, receiver } = this.jobSettingForm
      const { name } = this.dataInfo
      console.log(selectedData, 'selectedData')
      const dataSetList = selectedData.map((v) => {
        const dataset = {
          owner: v.ownerUserName,
          ownerAgency: v.ownerAgencyName,
          path: JSON.parse(v.datasetStoragePath).filePath,
          storageTypeStr: v.datasetStorageType,
          datasetID: v.datasetId
        }
        return {
          idFields: v.datasetFieldsSelected,
          dataset,
          labelProvider: !!v.selectedTagFields,
          receiveResult: receiver.includes(v.ownerAgencyName)
        }
      })
      const modelSetting = {}
      modelModule.forEach((v) => {
        const key = v.label
        modelSetting[key] = v.value
      })
      const param = { dataSetList, modelSetting }
      console.log(param, 'modelSettingmodelSettingmodelSettingmodelSettingmodelSettingmodelSettingmodelSetting')
      const params = { jobType: selectedAlg.value, projectName: name, param: JSON.stringify(param) }
      const taskParties = selectedData.map((v) => {
        return {
          userName: v.ownerUserName,
          agency: v.ownerAgencyName
        }
      })
      const datasetList = selectedData.map((v) => v.datasetId)
      console.log({ job: params, taskParties }, receiver)
      this.submitJob({ job: params, taskParties, datasetList })
    },
    handleSelectSetting(data) {
      const setting = JSON.parse(data.setting)
      this.modelModule.forEach((v) => {
        const key = v.label
        v.value = setting[key]
      })
      console.log(this.modelModule, setting, 'this.modelModule')
    },
    // 创建JOB
    async submitJob(params) {
      this.loadingFlag = true
      const res = await projectManageServer.submitJob(params)
      this.loadingFlag = false
      console.log(res)
      if (res.code === 0 && res.data) {
        this.$message.success('任务创建成功')
        this.$router.push({ path: '/jobDetail', query: { id: res.data } })
      } else {
        this.dataInfo = []
      }
    },
    async querySettings(params) {
      const res = await settingManageServer.querySettings(params)
      console.log(res)
      if (res.code === 0 && res.data) {
        const { setting = '' } = res.data[0]
        console.log(setting, 'JSON.parse(setting)')
        this.modelModule = JSON.parse(setting)
      }
    },
    async queryModelSettingList() {
      const res = await settingManageServer.querySettings({
        onlyMeta: false,
        condition: {
          id: '',
          name: '',
          type: 'MODEL_SETTING',
          owner: ''
        }
      })
      console.log(res)
      if (res.code === 0 && res.data) {
        this.modelSettingList = res.data.map((v) => {
          return {
            label: v.name,
            value: v
          }
        })
      }
    },
    showAddParticipate(addContainerIndex) {
      this.showParticipateModal = true
      this.addContainerIndex = addContainerIndex // 记录点击添加的是哪个container
    },
    closeModal() {
      this.showTagsModal = false
      this.showParticipateModal = false
    },
    tagSelected(data) {
      this.showTagsModal = false
      this.tagSelectList = [...data]
    },
    removeTag() {
      this.tagSelectList = []
    },
    setArea() {
      if (this.paticipateSelectList.some((v) => v.datasetId)) {
        this.paticipateSelectList = this.paticipateSelectList.filter((v) => v.datasetId)
      } else {
        this.paticipateSelectList = [{}]
      }
    },
    removeParticipate(index) {
      this.$set(this.paticipateSelectList, index, {})
    },
    participateSelected(data) {
      this.showParticipateModal = false
      this.$set(this.paticipateSelectList, this.addContainerIndex, data)
    },
    handleClick() {},
    addTag() {
      this.showTagsModal = true
    },

    next() {
      if (this.active === 1) {
        const { participateNumber } = this.selectedAlg
        const validpaticipateSelect = this.paticipateSelectList.filter((v) => v.datasetId)
        const validDataLength = validpaticipateSelect.length // 有效数据集数量
        const participateAgencyList = validpaticipateSelect.map((v) => v.ownerAgencyName)
        // 参与机构数量
        const uniqueAgencyLength = Array.from(new Set(participateAgencyList)).length
        if (this.selectedAlg.value === jobEnum.XGB_TRAINING) {
          const tag = this.tagSelectList[0] || {}
          const xgbValidParticipateLength = validpaticipateSelect.filter((v) => v.ownerAgencyName !== tag.ownerAgencyName).length
          // xgb需要选择标签提供方数据集
          const tagSelectListNum = this.tagSelectList.length
          if (!(tagSelectListNum === 1 && participateNumber === xgbValidParticipateLength)) {
            this.$message.error(`请添加标签提供方，并添加${participateNumber}个不同机构的参与方`)
            return
          }
        } else if (this.selectedAlg.value === jobEnum.PSI) {
          // psi 可同一机构下多个数据集
          if (validDataLength < participateNumber) {
            this.$message.error(`请添加至少${participateNumber}个参与方`)
            return
          }
        } else {
          // 其他任务只要参与方数量满足
          if (uniqueAgencyLength < participateNumber) {
            this.$message.error(`请添加至少${participateNumber}个不同机构的参与方`)
            return
          }
        }
        this.active++
      } else {
        this.active++
      }
    },
    pre() {
      this.active--
    },
    selectAlg(selectedAlg) {
      console.log(selectedAlg, 'selectedAlg')
      this.selectedAlg = { ...selectedAlg }
    }
  }
}
</script>
<style lang="less" scoped>
div.lead-mode {
  ul {
    li {
      color: #525660;
      font-size: 14px;
      line-height: 22px;
      margin-bottom: 12px;
      span {
        color: #787b84;
      }
    }
  }
  .step-container {
    margin-top: 42px;
    margin-bottom: 42px;
    width: 732px;
  }
  .alg-container {
    overflow: hidden;
    div.alg {
      float: left;
      text-align: center;
      height: 54px;
      background: #eff3fa;
      margin: 16px;
      width: calc(16% - 32px);
      line-height: 74px;
      color: #262a32;
      display: flex;
      align-items: center;
      min-width: 220px;
      border-radius: 16px;
      cursor: pointer;
      border: 2px solid white;
      box-sizing: content-box;
      img {
        height: 54px;
        width: auto;
        border-top-left-radius: 16px;
        border-bottom-left-radius: 16px;
      }
      .title {
        flex: 1;
        text-align: center;
        font-size: 16px;
        color: #262a32;
      }
    }
    .alg.active {
      border-color: #3071f2;
    }
  }
  .data-container {
    width: 872px;
    height: auto;
    border: 1px solid #e0e4ed;
    border-radius: 12px;
    margin-bottom: 42px;
    overflow: hidden;
    p {
      background: #d6e3fc;
      color: #262a32;
      font-size: 16px;
      font-weight: 500;
      line-height: 24px;
      text-align: left;
      padding: 13px 24px;
      span {
        float: right;
        font-size: 14px;
        font-weight: 400;
        line-height: 22px;
        color: #262a32;
        padding: 3px 12px;
        background-color: white;
        border-radius: 4px;
        transform: translateY(-4px);
        cursor: pointer;
        img {
          width: 16px;
          height: 16px;
          transform: translateY(2px);
        }
      }
    }
    div.area {
      text-align: center;
      height: 126px;
      display: flex;
      align-items: center;
      flex-direction: column;
      justify-content: center;
      cursor: pointer;
      img {
        display: inline-block;
        width: 16px;
        height: 16px;
        margin-bottom: 8px;
      }
    }
    div.area.table-area {
      height: auto;
    }
  }
  div.add {
    border: 1px solid #e0e4ed;
    width: 872px;
    height: 36px;
    padding: 7px 12px;
    text-align: center;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    color: #3071f2;
    font-size: 14px;
    line-height: 22px;
    margin-bottom: 44px;
    span {
      display: flex;
      align-items: center;
    }
    img {
      width: 18px;
      height: 18px;
      margin-right: 8px;
    }
  }
  span.tips {
    font-size: 14px;
    color: #787b84;
    margin-left: 16px;
    font-weight: 500;
  }
  div.sql-card {
    padding-bottom: 0;
    div.sql-container {
      display: flex;
      div.modify-container {
        flex: 1;
        height: 500px;
      }
      div.lead {
        color: #3071f2;
        padding-left: 16px;
        cursor: pointer;
        img {
          width: 16px;
          height: 16px;
          vertical-align: middle;
        }
      }
    }
  }

  ::v-deep .el-step__head.is-success {
    color: #3071f2;
    border-color: #3071f2;
  }
  ::v-deep .el-step__title.is-success {
    color: #3071f2;
  }
  ::v-deep .el-step__head.is-process {
    color: white;
    .el-step__icon {
      background-color: #3071f2;
      border-color: #3071f2;
    }
  }
  ::v-deep .el-step__title.is-process {
    color: #3071f2;
  }
  ::v-deep .el-step.is-horizontal .el-step__line {
    top: 18px;
  }
  ::v-deep .el-step__icon {
    width: 36px;
    height: 36px;
  }
  ::v-deep .el-input-group__prepend {
    width: 84px;
    text-align: center;
  }
}
</style>
