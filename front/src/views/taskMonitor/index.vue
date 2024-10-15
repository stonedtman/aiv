<template>
    <div class="container">
        <div class="main-wrapper card" v-loading="loading">
            <div class="page-top-action">
                <div class="table-filter">
                    <div class="filter-params">
                        <div class="filter-control">
                            <el-input v-model="params.keyword" prefix-icon="el-icon-search" size="small"
                                class="search-input" placeholder="请输入搜索内容" clearable
                                @keyup.enter.native="filterList"></el-input>
                        </div>
                    </div>
                    <div class="else-params">
                        <div class="filter-params">
                            <div class="filter-control">
                                <el-select v-model="params.alertLevel" placeholder="请选择告警级别" size="small"
                                    @change="filterList">
                                    <el-option v-for="(item, index) in alarmLevelList" :label="item.label"
                                        :value="item.value" :key="index"></el-option>
                                </el-select>
                            </div>
                        </div>
                        <div class="filter-params">
                            <div class="filter-control">
                                <el-date-picker v-model="params.dateRange" type="datetimerange" range-separator="至"
                                    start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd HH:mm:ss"
                                    size="small" :picker-options="pickerOptions" @change="filterList">
                                </el-date-picker>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right-search">
                    <el-button type="primary" icon="el-icon-plus" size="small" @click="createTask">添加任务</el-button>
                    <!-- <div class="filter-icon" @click="controlFilter"><i class="iconfont icon-icon-test"></i></div> -->
                </div>
            </div>
            <div class="table">
                <el-table :data="tableData" stripe style="width: 100%" empty-text="">
                    <!-- <el-table-column type="selection" width="55">
                    </el-table-column> -->
                    <el-table-column prop="taskId" label="任务ID">
                    </el-table-column>
                    <el-table-column prop="taskName" label="任务名称">
                        <template slot-scope="scope">
                            <div class="text-primary pointer" @click="viewDetail(scope.row)">
                                {{ scope.row.taskName }}
                                <i class="iconfont icon-chakanxiangqing table-icon-right icon-eye"></i>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cameraPosition" label="摄像头点位">
                    </el-table-column>
                    <el-table-column prop="aiModels" label="应用场景">
                        <template slot-scope="scope">
                            <div>
                                {{ scope.row.aiModels.join("，") }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="任务状态" align="center">
                        <template slot-scope="scope">
                            <div class="badge badge-green font-size-12" v-if="scope.row.status == 1">
                                已启动
                            </div>
                            <div class="badge badge-purple font-size-12" v-else>
                                未启动
                            </div>
                        </template>
                    </el-table-column>
                    <!-- <el-table-column prop="alertMethod" label="告警方式" align="center">
                        <template slot-scope="scope">
                            <div class="badge badge-blue font-size-12" v-if="scope.row.alertMethod == '邮件'">
                                {{ scope.row.alertMethod }}
                            </div>
                            <div class="badge badge-green font-size-12" v-else-if="scope.row.alertMethod == '电话'">
                                {{ scope.row.alertMethod }}
                            </div>
                            <div class="font-size-12" v-else>
                                {{ scope.row.alertMethod }}
                            </div>
                        </template>
                    </el-table-column> -->
                    <el-table-column prop="alertLevel" label="告警级别" align="center">
                        <template slot-scope="scope">
                            <div class="badge badge-red" v-if="scope.row.alertLevel == '高'">高</div>
                            <div class="badge badge-orange" v-else-if="scope.row.alertLevel == '中'">中</div>
                            <div class="badge badge-purple" v-else>低</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" align="center">
                        <template slot-scope="scope">
                            <div>{{ scope.row.createTime.slice(0, 16) }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-tooltip class="item" effect="light" content="编辑" placement="top">
                                <el-button type="text" icon="el-icon-edit" class="font-size-16"
                                    @click="confirmEdit(scope.row)"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="light" content="删除" placement="top">
                                <el-button type="text" icon="el-icon-delete" class="text-red font-size-16"
                                    @click="confirmDelete(scope.row)"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="light" content="启动" placement="top"
                                v-if="scope.row.status == 0">
                                <el-button type="text" icon="el-icon-video-play" class="text-green font-size-16"
                                    @click="confirmPlay(scope.row)"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="light" content="停用" placement="top"
                                v-if="scope.row.status == 1">
                                <el-button type="text" icon="el-icon-video-pause" class="text-orange font-size-16"
                                    @click="confirmPause(scope.row)"></el-button>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <template v-slot:empty>
                        <el-empty description="暂无数据"></el-empty>
                    </template>
                </el-table>
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="params.pageNum" :page-sizes="[10, 20, 30, 40, 50]" :page-size="params.pageSize"
                    layout="total, prev, pager, next" :total="totalCount" v-if="tableData.length > 0">
                </el-pagination>
            </div>
        </div>
        <el-drawer :title="warnTaskTitle" :visible.sync="drawerVisible" direction="rtl" size="40%"
            v-loading="drawerLoading" element-loading-background="rgba(255, 255, 255, 0.4)">
            <div class="drawer-wrap">
                <el-form ref="ruleForm" :model="form" :rules="rules" label-width="130px">
                    <div class="form">
                        <div class="left-form">
                            <el-form-item label="任务名称" prop="taskName">
                                <el-input v-model="form.taskName" size="small" class="long-input"
                                    placeholder="请输入任务名称"></el-input>
                            </el-form-item>
                            <el-form-item label="任务描述" prop="taskDesc">
                                <el-input type="textarea" v-model="form.taskDesc" class="long-input"
                                    placeholder="请输入任务描述" rows="3"></el-input>
                            </el-form-item>
                            <el-form-item label="任务优先级" prop="taskLevel">
                                <el-select v-model="form.taskLevel" placeholder="请选择任务优先级" size="small"
                                    class="long-input">
                                    <el-option label="高" value="高"></el-option>
                                    <el-option label="中" value="中"></el-option>
                                    <el-option label="低" value="低"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="监控点位" prop="location">
                                <el-cascader v-model="form.location" size="small" :options="pointsList" clearable
                                    placeholder="请选择摄像头点位" class="long-input"></el-cascader>
                            </el-form-item>
                            <el-form-item label="应用场景" prop="model">
                                <el-select v-model="form.model" placeholder="请选择应用场景" multiple :multiple-limit="3"
                                    size="small" class="long-input">
                                    <el-option v-for="(item, index) in modelList" :label="item.label"
                                        :value="item.value" :key="index"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="检测日期" prop="date">
                                <el-radio-group v-model="form.date" class="flex-center">
                                    <el-radio label="每天"></el-radio>
                                    <el-radio label="自定义" class="flex-center">
                                        <el-form-item prop="selfDate"
                                            :rules="form.date == '自定义' ? [{ required: true, message: '请选择检测日期', trigger: 'change' }] : []">
                                            <el-date-picker v-model="form.selfDate" type="daterange" range-separator="至"
                                                start-placeholder="开始日期" end-placeholder="结束日期" size="small"
                                                :picker-options="detectPickerOptions" style="width: 245px;">
                                            </el-date-picker>
                                        </el-form-item>
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <!-- <el-form-item label="检测时间" prop="timeRange">
                                <el-time-picker is-range v-model="form.timeRange" range-separator="至"
                                    start-placeholder="开始时间" end-placeholder="结束时间" placeholder="选择时间范围" size="small">
                                </el-time-picker>
                            </el-form-item> -->
                            <el-form-item label="检测轮询时长" prop="duration">
                                <el-radio-group v-model="form.duration" class="flex-center">
                                    <el-radio label="实时"></el-radio>
                                    <el-radio label="自定义" class="flex-center">
                                        <div class="form-control-merge">
                                            <el-form-item prop="selfDuration"
                                                :rules="form.duration == '自定义' ? [{ required: true, message: '请输入轮询时长', trigger: 'blur' }] : []">
                                                <el-input v-model="form.selfDuration" size="small" placeholder="请输入轮询时长"
                                                    style="width: 145px;"></el-input>
                                            </el-form-item>
                                            <el-form-item prop="selfDurationUnit"
                                                :rules="form.duration == '自定义' ? [{ required: true, message: '请选择时长单位', trigger: 'change' }] : []">
                                                <el-select v-model="form.selfDurationUnit" placeholder="时长单位"
                                                    size="small" style="width:100px;">
                                                    <el-option label="秒钟" value="秒钟"></el-option>
                                                    <el-option label="分钟" value="分钟"></el-option>
                                                    <el-option label="小时" value="小时"></el-option>
                                                </el-select>
                                            </el-form-item>
                                        </div>
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <!-- <el-form-item label="推送邮箱" prop="email">
                                <el-input v-model="form.email" size="small" placeholder="请输入推送邮箱"
                                    class="long-input"></el-input>
                            </el-form-item> -->
                            <el-form-item label="任务状态" prop="taskStatus">
                                <el-radio-group v-model="form.taskStatus">
                                    <el-radio label="启动"></el-radio>
                                    <el-radio label="停用"></el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </div>
                        <!-- <div class="right-form"> -->
                        <!-- <div class="form-control">
                                <el-form-item label="框选电子围栏">
                                    <div class="screen-footage long-input">
                                        <div class="image" v-if="footageUrl"><img :src="footageUrl" alt=""></div>
                                        <div class="image-empty">
                                            <i class="el-icon-picture"></i>
                                        </div>
                                        <div class="screen-controls flex-between">
                                            <div class="refresh-btn">
                                                <i class="el-icon-video-camera"></i>
                                                <span>刷新预览</span>
                                            </div>
                                            <div class="enlarge-btn">
                                                <i class="el-icon-zoom-in"></i>
                                                <span>放大画面</span>
                                            </div>
                                        </div>
                                    </div>
                                </el-form-item>
                            </div>

                            <el-form-item label="目标数量" prop="targetNumber">
                                <el-input v-model="form.targetNumber" size="small" placeholder="例：>=5"
                                    class="long-input"></el-input>
                            </el-form-item>
                            <div class="form-control-merge">
                                <el-form-item label="目标停留时长" prop="timing">
                                    <el-input v-model="form.timing" size="small" placeholder="例：>=60"
                                        style="width: 230px;"></el-input>
                                </el-form-item>
                                <el-form-item prop="timeUnit">
                                    <el-select v-model="form.timeUnit" placeholder="时长单位" class="short-input"
                                        size="small">
                                        <el-option label="秒钟" value="秒钟"></el-option>
                                        <el-option label="分钟" value="分钟"></el-option>
                                        <el-option label="小时" value="小时"></el-option>
                                    </el-select>
                                </el-form-item>
                            </div> -->
                        <!-- </div> -->
                    </div>

                    <div class="bottom-btns">
                        <el-button size="small">重置</el-button>
                        <el-button type="primary" @click="submitForm" size="small">提交</el-button>
                    </div>
                </el-form>
            </div>
        </el-drawer>
        <el-dialog title="监测任务详情" :visible.sync="dialogVisible" width="65%" :close-on-click-modal="false"
            :close-on-press-escape="false" :before-close="handleCloseDialog">
            <div class="dialog-wrap">
                <div class="detail-info">
                    <div class="result-item">
                        <div class="result-item-key">任务名称</div>
                        <div class="result-item-value">{{ taskInfo.taskName }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">任务描述</div>
                        <div class="result-item-value">{{ taskInfo.taskDescription == "默认任务描述" ? "无" :
                            taskInfo.taskDescription }}
                        </div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">监控点位</div>
                        <div class="result-item-value">{{ taskInfo.cameraPosition }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">监控分组</div>
                        <div class="result-item-value">{{ taskInfo.groupName }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">算法模型</div>
                        <div class="result-item-value">{{ taskInfo.aiModels }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">任务启动时间</div>
                        <div class="result-item-value">{{ taskInfo.startTime }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">任务停用时间</div>
                        <div class="result-item-value">{{ taskInfo.endTime }}</div>
                    </div>
                </div>
                <div class="camera-wrap" v-loading="screenLoading" :element-loading-text="screenLoadingText">
                    <div class="camera-title">当前监控摄像头画面</div>
                    <img :src="screenInputUrl" alt="" style="width: 480px;height: 360px;" v-if="screenInputUrl">
                    <el-empty :description="deviceAbnormal ? '监控设备失效，画面无法显示' : '暂无监控画面'" v-else></el-empty>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getMonitorTask, getMonitorTaskDetail, getAllAlgorithm, getMonitorDevice, createAlarmTask, updateAlarmTask, playAlarmTask, pauseAlarmTask, deleteAlarmTask, enabledStreamTransmit, disabledStreamTransmit } from "@/api/taskMonitor";
import baseURL from "@/utils/request";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            tableData: [],
            totalCount: 0,
            params: {
                keyword: "",
                pageNum: 1,
                pageSize: 10,
                alertLevel: "",
                dateRange: []
            },
            alarmLevelList: [
                {
                    label: "全部",
                    value: "全部"
                },
                {
                    label: "高",
                    value: "高"
                },
                {
                    label: "中",
                    value: "中"
                },
                {
                    label: "低",
                    value: "低"
                },
            ],
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now() - 8.64e6
                },
                shortcuts: [
                    {
                        text: '过去 1 小时内',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '过去 24 小时内',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '过去 1 周内',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '过去 1 个月内',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '过去 3 个月内',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }
                ]
            },
            warnTaskTitle: "添加预警任务",
            drawerVisible: false,
            form: {
                taskName: '',
                taskLevel: '',
                model: [],
                taskDesc: '',
                date: "",
                selfDate: "",
                timeRange: "",
                duration: "",
                selfDuration: "",
                selfDurationUnit: "",
                location: '',
                targetNumber: '',
                timing: '',
                timeUnit: '',
                taskStatus: '',
            },
            rules: {
                taskName: [
                    { required: true, message: '请输入任务名称', trigger: 'blur' },
                    { max: 8, message: '长度不能超过8个字符', trigger: 'blur' }
                ],
                taskLevel: [
                    { required: true, message: '请选择任务优先级', trigger: 'change' }
                ],
                // email: [
                //     { required: true, message: '请输入推送邮箱', trigger: 'blur' },
                //     {
                //         pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/,
                //         message: '邮箱格式不正确', trigger: 'blur'
                //     }
                // ],
                model: [
                    { required: true, message: '请选择算法模型', trigger: 'change' }
                ],
                date: [
                    // { required: true, message: '请选择检测日期', trigger: 'change' }
                ],
                timeRange: [
                    // { required: true, message: '请选择检测时间范围', trigger: 'change' }
                ],
                duration: [
                    // { required: true, message: '请选择检测轮询时长', trigger: 'change' }
                ],
                location: [
                    { required: true, message: '请选择监控点位', trigger: 'change' }
                ],
                timeUnit: [
                    // { required: true, message: '请选择时长单位', trigger: 'change' }
                ],
                taskStatus: [
                    { required: true, message: '请选择任务状态', trigger: 'change' }
                ]
            },
            detectPickerOptions: {
                disabledDate(time) {
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);

                    // 将选择器中的日期时间设置为0点
                    time = new Date(time.getFullYear(), time.getMonth(), time.getDate()).getTime();

                    // 如果选择的日期小于今天的日期，则禁用
                    return time < today.getTime();
                },
            },
            modelList: [],
            timeList: [
                {
                    label: "5秒",
                    value: "5秒",
                },
                {
                    label: "10秒",
                    value: "10秒",
                },
                {
                    label: "30秒",
                    value: "30秒",
                },
                {
                    label: "自定义秒钟",
                    value: "自定义秒钟",
                },
            ],
            pointsList: [],
            footageUrl: "",
            dialogVisible: false,
            taskInfo: {

            },
            drawerLoading: false,
            checkedTaskId: null,
            webSocket: null,
            sessionID: "",//启动/关闭视频流传输的session-id
            screenInputUrl: "",//画面接入的url
            screenLoading: false,
            screenLoadingText: "画面加载中",
            deviceAbnormal: false
        };
    },
    created() {
        this.getMonitorTask();
    },
    mounted() {

    },
    beforeDestroy() {
        //断开websocket连接
        this.closeWebSocket();
    },
    watch: {},
    computed: {},
    methods: {
        getMonitorTask() {
            this.loading = true;
            this.tableData = [];
            var params = { taskName: this.params.keyword, pageNum: this.params.pageNum, pageSize: this.params.pageSize, alertLevel: this.params.alertLevel, startTime: this.params.dateRange.length > 0 ? this.params.dateRange[0] : "", endTime: this.params.dateRange.length > 0 ? this.params.dateRange[1] : "" };
            getMonitorTask(params).then(res => {
                setTimeout(() => {
                    this.loading = false;
                    if (res.code == 200) {
                        this.tableData = res.data;
                        this.tableData.forEach(item => {
                            var models = [];
                            item.aiModels.forEach(model => {
                                models.push(model.modelName);
                            })
                            item.aiModels = models;
                        })
                        this.totalCount = res.count;
                    }
                }, 500);
            }).catch(() => {
                this.loading = false;
            })
        },
        filterList() {
            this.params.pageNum = 1;
            this.getMonitorTask();
        },
        createTask() {
            this.form = {
                taskName: '',
                taskLevel: '',
                model: [],
                taskDesc: '',
                date: "",
                selfDate: "",
                timeRange: "",
                duration: "",
                selfDuration: "",
                selfDurationUnit: "",
                location: '',
                targetNumber: '',
                timing: '',
                timeUnit: '',
                taskStatus: '',
            };
            if (this.$refs.ruleForm !== undefined) {
                this.$refs.ruleForm.resetFields();
            }
            this.warnTaskTitle = "添加预警任务";
            this.loading = true;
            this.modelList = [];
            this.pointsList = [];
            var request = [getAllAlgorithm(), getMonitorDevice()];
            Promise.all(request).then(results => {
                this.drawerVisible = true;
                if (results[0].code == 200) {
                    if (results[1].data.length > 0) {
                        results[0].data.forEach(item => {
                            this.modelList.push({ label: item.modelName, value: item.id });
                        })
                    }
                }

                if (results[1].code == 200) {
                    if (results[1].data.length > 0) {
                        results[1].data.forEach(item => {
                            var obj = { label: item.groupName, value: item.groupName };
                            var children = [];
                            item.cameras.forEach(child => {
                                if (child.cameraStatus != undefined) {
                                    children.push({ label: child.cameraLocation, value: child.cameraLocation, status: child.cameraStatus })
                                } else {
                                    children.push({ label: child.cameraLocation, value: child.cameraLocation })
                                }
                            });
                            obj.children = children;
                            this.pointsList.push(obj);
                        });
                    }
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        viewDetail(row) {
            this.screenLoadingText = "画面加载中";
            this.screenInputUrl = ""; //每次打开新的详情前清空画面流地址
            this.deviceAbnormal = false; //重置画面异常状态
            this.loading = true;
            getMonitorTaskDetail({ Id: row.id }).then(res => {
                if (res.code == 200) {
                    this.dialogVisible = true;
                    this.taskInfo = res.data;
                    if (this.taskInfo.aiModels.length > 0) {
                        var models = [];
                        this.taskInfo.aiModels.forEach(item => {
                            models.push(item.modelName);
                        });
                        this.taskInfo.aiModels = models.join("，");
                    }

                    this.screenLoading = true;
                    if (this.webSocket) {
                        //开启视频流传输
                        this.enabledStreamTransmit(this.taskInfo.videoStreaming);
                    } else {
                        //连接websocket
                        this.initWebSocket(this.taskInfo.videoStreaming);
                    }

                    setTimeout(() => {
                        var dialogWrapElement = document.querySelector(".dialog-wrap");
                        dialogWrapElement.scrollTo({
                            top: 0,
                        });
                    }, 100);
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        handleCloseDialog() {
            if (!this.screenInputUrl && !this.screenLoading) {
                this.dialogVisible = false;
            }
            //如果画面在连接中 需要先断开连接才能关闭弹框
            else {
                this.screenLoading = true;
                this.screenLoadingText = "画面断开中";
                //断开视频流传输
                disabledStreamTransmit({ sessionId: this.sessionID }).then(res => {
                    if (res.code == 200) {
                        this.screenLoading = false;
                        this.dialogVisible = false;
                    }
                }).catch(() => {
                    this.screenLoading = false;
                    this.dialogVisible = false;
                })
            }
        },
        initWebSocket(videoStreaming) {
            var wsUrl = "";
            if (window.location.origin.indexOf("https") > -1) {
                wsUrl = "wss" + baseURL.split("https")[1] + "/ws/video";
            } else {
                wsUrl = "ws" + baseURL.split("http")[1] + "/ws/video";
            }

            if (this.webSocket == null) {
                this.webSocket = new WebSocket(wsUrl);
            }

            this.webSocket.binaryType = 'arraybuffer';

            this.webSocket.onopen = () => {
                console.log('WebSocket已连接');
                // 可以在这里发送消息
                // this.webSocket.send();
            };
            this.webSocket.onmessage = (event) => {
                // console.log('收到消息：', event.data);
                if (typeof event.data == "object") {
                    this.screenLoading = false;

                    const blob = new Blob([event.data], { type: 'image/jpeg' });
                    const url = URL.createObjectURL(blob);
                    if (!this.screenLoading) {
                        this.screenInputUrl = url;
                    }
                } else {
                    try {
                        var message = JSON.parse(event.data);
                        if (message.type == "sessionId") {
                            //websocket连接成功后才会传sessionId
                            this.sessionID = message.sessionId;

                            //开启视频流传输
                            this.enabledStreamTransmit(videoStreaming);
                        }
                    } catch {
                        if (typeof event.data == "string") {
                            if (event.data == "视频流无法打开") {
                                this.screenInputUrl = "";
                                this.deviceAbnormal = true;
                                this.screenLoading = false;
                            }
                        }
                    }
                }

            };

            this.webSocket.onerror = (error) => {
                this.$message.error("websocket连接失败");
            };

            this.webSocket.onclose = (event) => {
                console.log("websocket连接断开");
            };
        },
        closeWebSocket() {
            if (this.webSocket) {
                this.webSocket.close();
            }
        },
        enabledStreamTransmit(videoStreaming) {
            //调用视频流传输api
            enabledStreamTransmit({ videoStreaming, sessionId: this.sessionID });
        },
        confirmEdit(row) {
            this.form = {
                taskName: '',
                taskLevel: '',
                model: [],
                taskDesc: '',
                date: "",
                selfDate: "",
                timeRange: "",
                duration: "",
                selfDuration: "",
                selfDurationUnit: "",
                location: '',
                targetNumber: '',
                timing: '',
                timeUnit: '',
                taskStatus: '',
            };
            if (this.$refs.ruleForm !== undefined) {
                this.$refs.ruleForm.resetFields();
            }
            this.checkedTaskId = row.id;
            this.warnTaskTitle = "编辑预警任务";
            this.loading = true;
            this.modelList = [];
            this.pointsList = [];
            var request = [getAllAlgorithm(), getMonitorDevice(), getMonitorTaskDetail({ Id: row.id })];
            Promise.all(request).then(results => {
                this.drawerVisible = true;
                if (results[0].code == 200) {
                    if (results[1].data.length > 0) {
                        results[0].data.forEach(item => {
                            this.modelList.push({ label: item.modelName, value: item.id });
                        })
                    }
                }

                if (results[1].code == 200) {
                    if (results[1].data.length > 0) {
                        results[1].data.forEach(item => {
                            var obj = { label: item.groupName, value: item.groupName };
                            var children = [];
                            item.cameras.forEach(child => {
                                if (child.cameraStatus != undefined) {
                                    children.push({ label: child.cameraLocation, value: child.cameraLocation, status: child.cameraStatus })
                                } else {
                                    children.push({ label: child.cameraLocation, value: child.cameraLocation })
                                }
                            });
                            obj.children = children;
                            this.pointsList.push(obj);
                        });
                    }
                }

                if (results[2].code == 200) {
                    var taskInfo = results[2].data;
                    if (Object.keys(taskInfo).length > 0) {
                        this.form.taskName = taskInfo.taskName;
                        this.form.taskDesc = taskInfo.taskDescription == "默认任务描述" ? "" : taskInfo.taskDescription;
                        this.form.taskLevel = taskInfo.alertLevel;
                        this.form.model = /,/.test(taskInfo.ids) ? taskInfo.ids.split(",") : [taskInfo.ids];
                        this.form.email = taskInfo.notificationEmail;
                        this.form.taskStatus = taskInfo.status == 1 ? "启动" : "停用";
                        this.form.location[0] = taskInfo.groupName;
                        this.form.location[1] = taskInfo.cameraPosition;
                    }
                }

            }).finally(() => {
                this.loading = false;
            })

        },
        submitForm() {
            this.$refs["ruleForm"].validate((valid) => {
                if (valid) {
                    this.drawerLoading = true;
                    var form = {};
                    form.taskName = this.form.taskName;
                    form.taskDescription = this.form.taskDesc;
                    form.cameraPosition = this.form.location[1];
                    form.alertLevel = this.form.taskLevel;
                    form.notificationEmail = this.form.email;
                    form.ids = this.form.model.join(",");
                    form.status = this.form.taskStatus == "启动" ? 1 : 0;

                    if (this.warnTaskTitle == "添加预警任务") {
                        createAlarmTask(form).then(res => {
                            if (res.code == 200) {
                                this.drawerVisible = false;
                                this.$message({
                                    type: 'success',
                                    message: '添加成功!'
                                });
                                this.getMonitorTask();
                            }
                        }).finally(() => {
                            setTimeout(() => {
                                this.drawerLoading = false;
                            }, 1000);
                        })
                    } else {
                        form.id = this.checkedTaskId;
                        updateAlarmTask(form).then(res => {
                            if (res.code == 200) {
                                this.drawerVisible = false;
                                this.$message({
                                    type: 'success',
                                    message: '修改成功!'
                                });
                                this.getMonitorTask();
                            }

                        }).finally(() => {
                            setTimeout(() => {
                                this.drawerLoading = false;
                            }, 1000);
                        })
                    }
                }
            });
        },
        confirmDelete(row) {
            this.$confirm('确定要删除该任务吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.loading = true;
                deleteAlarmTask({ Id: row.id }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '删除成功！'
                        });
                        if (this.tableData.length == 1 && this.params.pageNum > 1) {
                            this.params.pageNum--;
                        }
                        this.getMonitorTask();
                    }
                }).finally(() => {
                    this.loading = false;
                })
            })
        },
        confirmPlay(row) {
            this.$confirm('确定要启动该任务吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.loading = true;
                playAlarmTask({ Id: row.id }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '启动成功！'
                        });
                        this.getMonitorTask();
                    }
                }).finally(() => {
                    this.loading = false;
                })

            })
        },
        confirmPause(row) {
            this.$confirm('确定要停用该任务吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.loading = true;
                pauseAlarmTask({ Id: row.id }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '关闭成功！'
                        });
                        this.getMonitorTask();
                    }
                }).finally(() => {
                    this.loading = false;
                })

            })
        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`);
            this.params.pageNum = 1;
            this.params.pageSize = val;
            this.getMonitorTask();
        },
        handleCurrentChange(val) {
            // console.log(`当前页: ${val}`);
            this.params.pageNum = val;
            this.getMonitorTask();
        },
        disabledDate(date) {
            // 获取今天的日期
            const today = new Date();
            // 设置时间为今天的23:59:59，即当天的结束时间
            today.setHours(23, 59, 59, 999);

            // 如果选择的日期在今天之前，则禁用选择
            return date && date < today;
        }
    }
};
</script>
<style lang="scss" scoped>
.flex-center {
    display: flex;
    align-items: center;
}



.el-form {
    padding: 0 20px;

    .form {
        display: flex;
        margin-bottom: 65px;
        overflow: auto;

        .right-form {
            margin-left: 5vw;
        }

        .el-input,
        .el-select {
            width: 240px;

        }

        .long-input {
            width: 350px;
        }

        .short-input {
            width: 120px;
        }

        .screen-footage {
            position: relative;

            .image-empty {
                height: 260px;
                background-color: #D9D9D9;
                display: flex;
                justify-content: center;
                align-items: center;

                i {
                    font-size: 24px;
                }
            }

            .screen-controls {
                position: absolute;
                left: 0;
                bottom: 5px;
                width: 100%;
                padding: 0 10px;
                box-sizing: border-box;
                color: #505050;
                z-index: 1;

                div {
                    cursor: pointer;

                    &:hover {
                        color: #000;
                    }

                    i {
                        margin-right: 6px;
                    }
                }


            }
        }

        .form-control-merge {
            display: flex;

            ::v-deep .el-form-item:nth-child(1) .el-input__inner {
                border-top-right-radius: 0;
                border-bottom-right-radius: 0;
            }

            ::v-deep .el-form-item:nth-child(2) .el-form-item__content {
                margin-left: 0 !important;
            }

            ::v-deep .el-form-item:nth-child(2) .el-input__inner {
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }
        }
    }

    .bottom-btns {
        width: 100%;
        position: absolute;
        left: 0;
        bottom: 0;
        background: #fff;
        padding-left: 70px;
        padding-bottom: 30px;

        .el-button {
            min-width: 72px;

            &:not(:last-child) {
                margin-right: 20px;
            }

        }


    }
}

.dialog-wrap {
    padding: 0 10px;
    max-height: 53vh;
    display: flex;
    justify-content: space-between;

    .detail-info {
        width: calc(100% - 500px);
        padding: 12px 20px;
        box-sizing: border-box;
        overflow-y: auto;
        // background-color: #e2e2e2;

        .result-item {
            display: flex;
            margin-bottom: 24px;

            .result-item-key {
                width: 100px;
                color: #a6a6a6;
                margin-right: 20px;
            }



            .result-item-value {
                flex: 1;

                .image-wrap {
                    position: relative;

                    .image-empty {
                        width: 100%;
                        height: 220px;
                        background-color: #D9D9D9;
                        display: flex;
                        justify-content: center;
                        align-items: center;

                        i {
                            font-size: 24px;
                        }
                    }
                }


            }
        }
    }

    .camera-wrap {
        width: 480px;
        margin-left: 20px;
        box-sizing: border-box;

        .camera-title {
            margin-bottom: 10px;
        }
    }

}
</style>