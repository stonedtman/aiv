<template>
    <div class="container">
        <div class="main-wrapper" v-loading="loading">
            <el-row :gutter="20">
                <el-col :span="16" class="billboards-left">
                    <el-row :gutter="20" class="billboards-left-top">
                        <el-col :span="8">
                            <div class="card stats-digit">
                                <div class="card-body">
                                    <div class="stats-label">摄像头布控</div>
                                    <div class="stats-value">{{ statistics.deviceCount }}</div>
                                </div>
                                <div class="card-icon text-primary">
                                    <i class="iconfont icon-bukong"></i>
                                </div>
                            </div>
                            <div class="card stats-trend border-top">
                                <div class="card-body">
                                    <div class="stats-value-ratio badge"
                                        :class="{ 'badge-yellow': statistics.deviceCount == 0, 'badge-red': statistics.deviceCount > 0 && statistics.deviceCount != statistics.deviceWorkCount, 'badge-green': statistics.deviceCount > 0 && statistics.deviceCount == statistics.deviceWorkCount }">
                                        <i class="iconfont icon-yuandian"></i>
                                        <span>{{ statistics.deviceCount > 0 ? statistics.deviceRatio + "%" : "N/A"
                                            }}</span>
                                    </div>
                                    <div class="stats-value-desc text-gray">
                                        {{ statistics.deviceCount > 0 ? (statistics.deviceRatio == 100 ? "设备状态全部正常"
                                            : (statistics.deviceCount - statistics.deviceWorkCount) + "台设备状态异常") : "当前无设备"
                                        }}
                                    </div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="8">
                            <div class="card stats-digit">
                                <div class="card-body">
                                    <div class="stats-label">今日预警数量</div>
                                    <div class="stats-value">{{ statistics.todayCount }}</div>
                                </div>
                                <div class="card-icon text-primary">
                                    <i class="iconfont icon-gaojingshijian"></i>
                                </div>
                            </div>
                            <div class="card stats-trend border-top">
                                <div class="card-body">
                                    <div class="stats-value-ratio badge"
                                        :class="{ 'badge-yellow': statistics.todayStatus == 0, 'badge-red': statistics.todayStatus == 1, 'badge-green': statistics.todayStatus == 2 }">
                                        <i
                                            :class="{ 'el-icon-caret-bottom': statistics.deviceCount > 0 && statistics.todayStatus == 0, 'el-icon-caret-top': statistics.deviceCount > 0 && statistics.todayStatus == 1, 'iconfont icon-yuandian': statistics.deviceCount == 0 || statistics.todayStatus == 2 }"></i>
                                        <span>{{ statistics.todayCount > 0 ? statistics.todayRatio + "%" : "N/A"
                                            }}</span>
                                    </div>
                                    <div class="stats-value-desc text-gray">{{
                                        statistics.todayCount > 0 ? (statistics.todayStatus == 0 ? "比昨天预警数量降低"
                                            :
                                            statistics.todayStatus == 1 ? "比昨天预警数量升高" : "与昨天预警数量相同") : "当前无预警信息" }}</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="8">
                            <div class="card stats-digit">
                                <div class="card-body">
                                    <div class="stats-label">昨日预警数量</div>
                                    <div class="stats-value">{{ statistics.yesterdayCount }}</div>
                                </div>
                                <div class="card-icon text-primary">
                                    <i class="iconfont icon-alarm"></i>
                                </div>
                            </div>
                            <div class="card stats-trend border-top">
                                <div class="card-body">
                                    <div class="stats-value-ratio badge"
                                        :class="{ 'badge-yellow': statistics.yesterdayStatus == 0, 'badge-red': statistics.yesterdayStatus == 1, 'badge-green': statistics.yesterdayStatus == 2 }">
                                        <i
                                            :class="{ 'el-icon-caret-bottom': statistics.deviceCount > 0 && statistics.yesterdayStatus == 0, 'el-icon-caret-top': statistics.deviceCount > 0 && statistics.yesterdayStatus == 1, 'iconfont icon-yuandian': statistics.deviceCount == 0 || statistics.yesterdayStatus == 2 }"></i>
                                        <span>{{ statistics.yesterdayCount > 0 ? statistics.yesterdayRatio + "%" : "N/A"
                                            }}</span>
                                    </div>
                                    <div class="stats-value-desc text-gray">{{ statistics.yesterdayCount > 0 ?
                                        (statistics.yesterdayStatus == 0 ?
                                            "比前天预警数量降低" :
                                            statistics.yesterdayStatus == 1 ? "比前天预警数量升高" : "与前天预警数量相同") : "无预警信息" }}</div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20" style="margin-top: 24px;">
                        <el-col :span="24">
                            <div class="layout card">
                                <div class="layout-top flex-between">
                                    <div class="title"><i class="el-icon-data-line"
                                            style="vertical-align: text-bottom;"></i>预警排名走势分析</div>
                                    <div class="action">
                                        <div class="filter-btn-group">
                                            <el-button-group>
                                                <el-button size="small" class="chart-filter"
                                                    :class="{ 'active': activeIndex == 1 }"
                                                    @click="filterWarningRank(1)">今天</el-button>
                                                <el-button size="small" class="chart-filter"
                                                    :class="{ 'active': activeIndex == 2 }"
                                                    @click="filterWarningRank(2)">7天</el-button>
                                                <el-button size="small" class="chart-filter"
                                                    :class="{ 'active': activeIndex == 3 }"
                                                    @click="filterWarningRank(3)">30天</el-button>
                                            </el-button-group>
                                        </div>
                                    </div>
                                </div>
                                <div class="layout-content" v-loading="chartLoading">
                                    <div class="line-chart" v-if="splineAreaChart.series.length > 0">
                                        <apexchart class="apex-charts" :height="lineChartHeight" type="area" dir="ltr"
                                            :series="splineAreaChart.series" :options="splineAreaChart.chartOptions">
                                        </apexchart>
                                    </div>
                                    <div class="chart-empty" v-else>
                                        <el-empty description="暂无预警排名走势信息"></el-empty>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </el-col>
                <el-col :span="8" class="billboards-right">
                    <el-row :gutter="20" class="realtime-monitor">
                        <el-col :span="24">
                            <div class="layout card">
                                <div class="layout-top flex-between">
                                    <div class="title"><i class="el-icon-video-camera"
                                            style="vertical-align: text-bottom;"></i>摄像机实时监控画面</div>
                                    <div class="action">
                                        <div class="device-options" v-if="monitorList.length > 0">
                                            <el-cascader v-model="selectedMonitor" :options="monitorList" size="small"
                                                @change="handleMonitorChange"></el-cascader>
                                        </div>
                                        <div class="create-button" v-if="monitorList.length == 0">
                                            <el-button type="text" @click="createDevice">添加摄像头</el-button>
                                        </div>
                                    </div>
                                </div>
                                <div class="layout-content" v-loading="screenLoading" element-loading-text="画面加载中">
                                    <div class="realtime-video" ref="fullscreenElement" v-if="screenInputUrl">
                                        <img :src="screenInputUrl" draggable="false" style="width: 100%;height: 100%;">
                                        <div class="action">
                                            <i class="iconfont icon-quanping1 fullscreen-icon pointer" title="进入全屏"
                                                @click="toggleFullScreen" v-if="!isFullScreen"></i>
                                            <i class="iconfont icon-tuichuquanping fullscreen-icon pointer" title="退出全屏"
                                                @click="toggleFullScreen" v-else></i>
                                        </div>
                                    </div>
                                    <div class="footage-empty" v-else>
                                        <el-empty
                                            :description="monitorList.length == 0 ? '点击 添加摄像头 添加监控设备' : deviceAbnormal ? '监控设备失效，画面无法显示' : '暂无监控画面'"></el-empty>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20" style="margin-top: 24px;">
                        <el-col :span="24">
                            <div class="layout card">
                                <div class="layout-top flex-between">
                                    <div class="title"><i class="el-icon-bell"
                                            style="vertical-align: middle;"></i>最近预警列表清单</div>
                                    <div class="action">
                                        <div class="tomore-button" v-if="alarmList.length > 0">
                                            <el-button type="text" @click="toMoreWarning">更多</el-button>
                                        </div>
                                        <div class="create-button" v-if="monitorList.length == 0">
                                            <el-button type="text" @click="createTask">添加监测任务</el-button>
                                        </div>
                                    </div>
                                </div>
                                <div class="layout-content" v-loading="alarmLoading">
                                    <div class="simple-list">
                                        <div class="simple-wrap" v-if="alarmList.length > 0">
                                            <vue-seamless-scroll ref="scrollBar" :data="alarmList" :loop="true"
                                                :class-option="classOption">
                                                <ul class="list-unstyled"
                                                    :class="{ 'activity-wid': alarmList.length > 1 }">
                                                    <li class="activity-list" v-for="(item, index) in alarmList"
                                                        :key="index">
                                                        <div class="activity-icon avatar-xs">
                                                            <span
                                                                class="avatar-title bg-primary text-primary rounded-circle">
                                                                <i class="iconfont icon-laba"></i>
                                                            </span>
                                                        </div>
                                                        <div class="event-list">
                                                            <div class="event-list-item">
                                                                <div class="event-list-item-left">
                                                                    <div class="event-date font-size-13">
                                                                        <span>{{ item.alertTime.slice(0, 16) }}</span>
                                                                        <!-- <small class="text-gray">12:07 中午</small> -->
                                                                    </div>
                                                                    <div class="event-name">
                                                                        <p>
                                                                            <span class="text-gray label">监控区域：</span>
                                                                            <span class="value">{{ item.cameraPosition
                                                                                }}</span>
                                                                        </p>
                                                                        <p><span class="text-gray label">预警类型：</span>
                                                                            <span class="value">{{
                                                                                item.alertType }}</span>
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                                <div class="event-list-item-right">
                                                                    <div class="event-image pointer"
                                                                        @click="viewDetail(item)">
                                                                        <img :src="item.capturedImage" alt="">
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </li>
                                                </ul>
                                            </vue-seamless-scroll>
                                        </div>

                                        <div class="alarm-empty" v-else>
                                            <el-empty description="暂无预警信息"></el-empty>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </div>
        <el-dialog title="告警详情" :visible.sync="dialogVisible" width="65%">
            <div class="dialog-wrap">
                <div class="detail-info">
                    <div class="result-item">
                        <div class="result-item-key">预警编号</div>
                        <div class="result-item-value">{{ alarmInfo.alertId }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">监控区域</div>
                        <div class="result-item-value">{{ alarmInfo.cameraPosition }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">监测任务</div>
                        <div class="result-item-value">{{ alarmInfo.monitoringTask }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">预警类型</div>
                        <div class="result-item-value">{{ alarmInfo.alertType }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">预警时间</div>
                        <div class="result-item-value">{{ alarmInfo.alertTime }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">告警级别</div>
                        <div class="result-item-value">{{ alarmInfo.alertLevel }}</div>
                    </div>
                </div>
                <div class="detail-video">
                    <video :src="alarmInfo.capturedVideo" :poster="alarmInfo.capturedImage" controls preload="auto"
                        autoplay="autoplay" loop="loop" style="width: 528px;height: 396px;"></video>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getDeviceStatus, getStatistics, getTodayAlarmTrend, getLastWeekAlarmTrend, getLastMonthAlarmTrend, getMonitorDevice, enabledStreamTransmit, disabledStreamTransmit, getLatestWarning, getWarningEventDetail } from "@/api/billboards";
import baseURL from "@/utils/request";
import staticURL from "@/utils/staticRequest";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            countDuration: 2000,
            statistics: {
                deviceCount: 0,
                deviceWorkCount: 0,
                deviceRatio: 0,
                todayCount: 0,
                todayRatio: 0,
                todayStatus: 0,
                yesterdayCount: 0,
                yesterdayRatio: 0,
                yesterdayStatus: 0
            },
            monitorList: [],
            selectedMonitor: [],
            splineAreaChart: {
                series: [
                    // {
                    //     name: '吸烟检测',
                    //     data: [34, 40, 28, 52, 42, 109, 100]
                    // },
                    // {
                    //     name: '打电话检测',
                    //     data: [32, 60, 34, 46, 34, 52, 41]
                    // },
                    // {
                    //     name: '交通事故检测',
                    //     data: [36, 50, 44, 62, 54, 12, 31]
                    // }
                ],
                chartOptions: {
                    chart: {
                        toolbar: {
                            show: false // 隐藏顶部工具栏
                        },
                        animations: {
                            enabled: false // 设置动画效果为禁用
                        },
                    },
                    dataLabels: {
                        enabled: false
                    },
                    stroke: {
                        curve: 'smooth',
                        width: 3,
                    },
                    colors: ['#556ee6', '#34c38f', "#4aa3ff"],
                    xaxis: {
                        // type: 'datetime',
                        categories: [],
                    },
                    grid: {
                        borderColor: '#f1f1f1',
                    },
                    tooltip: {
                        // x: {
                        //     format: 'yyyy/MM/dd HH:mm'
                        // },
                    },

                }
            },
            activeIndex: 1,
            webSocket: null,
            dialogVisible: false,
            alarmList: [],
            screenInputUrl: "",//画面接入的url
            screenLoading: false,
            sessionID: "",//启动/关闭视频流传输的session-id
            isfirst: true,
            classOption: {
                step: 0.8
            },
            isFullScreen: false,
            timer: null,
            deviceAbnormal: false,//监控画面是否异常
            changeVideoSuccess: false,//切换画面是否成功
            alarmLoading: false,
            alarmInfo: {
                alertId: "",
                alertLevel: "",
                alertTime: "",
                alertType: "",
                cameraPosition: "",
                capturedImage: "",
                capturedVideo: "",
                monitoringTask: ""
            },
            chartLoading: false,
            lineChartHeight: ""
        }
    },
    created() {
        this.initLoading();
    },
    mounted() {
        var footageEmptyDom = document.querySelector(".footage-empty");
        footageEmptyDom.style.height = JSON.stringify(footageEmptyDom.offsetWidth * 0.75) + "px";

        var billboardsRightDom = document.querySelector(".billboards-right");
        var billboardsLeftTopDom = document.querySelector(".billboards-left-top");
        this.lineChartHeight = JSON.stringify(billboardsRightDom.offsetHeight - billboardsLeftTopDom.offsetHeight - 122);
        document.querySelector(".chart-empty").style.height = JSON.stringify(JSON.parse(this.lineChartHeight) + 15) + "px";

        //监听监控画面全屏事件
        document.addEventListener('fullscreenchange', this.onFullScreenChange);
    },
    beforeDestroy() {
        // 组件销毁前关闭 WebSocket 连接
        this.closeWebSocket();
        //断开视频流传输
        disabledStreamTransmit({ sessionId: this.sessionID });

        //清除定时器
        clearInterval(this.timer);

        //移除监听监控画面全屏事件
        document.removeEventListener('fullscreenchange', this.onFullScreenChange);
    },
    watch: {

    },
    computed: {},
    methods: {
        initLoading() {
            this.loading = true;
            this.monitorList = [];
            var requests = [getMonitorDevice(), getLatestWarning(), getDeviceStatus(), getStatistics(), getTodayAlarmTrend()];
            Promise.all(requests).then(results => {
                // console.log(results);
                if (results[0].code == 200) {
                    if (results[0].data.length > 0) {
                        results[0].data.forEach(item => {
                            var obj = { label: item.groupName, value: item.groupName };
                            var children = [];
                            item.cameras.forEach(child => {
                                if (child.cameraStatus != undefined) {
                                    children.push({ label: child.cameraLocation, value: child.videoStreaming, status: child.cameraStatus })
                                } else {
                                    children.push({ label: child.cameraLocation, value: child.videoStreaming })
                                }
                            });
                            obj.children = children;
                            this.monitorList.push(obj);
                        });

                        this.initWebSocket();
                    }
                }

                if (results[1].code == 200) {
                    if (results[1].data.length > 0) {
                        this.alarmList = results[1].data;
                        this.alarmList.forEach(item => {
                            item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                            item.capturedVideo = baseURL.split("/api")[0] + item.capturedVideo;
                        })
                    }
                }
                //每隔俩分钟自动调接口获取一次预警信息
                this.timer = setInterval(() => {
                    getLatestWarning().then(res => {
                        if (res.code == 200) {
                            if (res.data.length > 0) {
                                this.alarmList = res.data;
                                this.alarmList.forEach(item => {
                                    item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                                    item.capturedVideo = baseURL.split("/api")[0] + item.capturedVideo;
                                })
                            }
                        }
                    })
                }, 1000 * 120);

                if (results[2].code == 200) {
                    if (Object.keys(results[2].data).length > 0) {
                        var deviceStatistics = results[2].data;
                        this.statistics.deviceCount = deviceStatistics.Camerasum;
                        this.statistics.deviceWorkCount = deviceStatistics.working;
                        if (deviceStatistics.rate) {
                            this.statistics.deviceRatio = Number(deviceStatistics.rate.split("%")[0]);
                        } else {
                            this.statistics.deviceRatio = 0;
                        }
                    }
                }

                if (results[3].code == 200) {
                    if (Object.keys(results[3].data).length > 0) {
                        var alarmStatistics = results[3].data;
                        this.statistics.todayCount = alarmStatistics.today;
                        this.statistics.todayRatio = Math.abs(Number(alarmStatistics["day-yesterday"]));
                        this.statistics.todayStatus = Number(alarmStatistics["day-yesterday"]) > 0 ? 1 : Number(alarmStatistics["day-yesterday"]) < 0 ? 0 : 2;
                        this.statistics.yesterdayCount = alarmStatistics.yesterday;
                        this.statistics.yesterdayRatio = Math.abs(Number(alarmStatistics["yesterday-before"]));
                        this.statistics.yesterdayStatus = Number(alarmStatistics["yesterday-before"]) > 0 ? 1 : Number(alarmStatistics["yesterday-before"]) < 0 ? 0 : 2;;
                    }
                }

                if (results[4].code == 200) {
                    var result = results[4].data;
                    if (Object.keys(result).length > 0) {
                        var dataSets = [];
                        var categories = [];
                        var isfirst = true;
                        for (const key in result) {
                            var modelObject = {};
                            modelObject.name = key;
                            var dataArray = [];
                            for (const sonkey in result[key]) {
                                if (isfirst) {
                                    categories.push(sonkey);
                                }
                                dataArray.push(result[key][sonkey]);
                            }
                            isfirst = false;
                            modelObject.data = dataArray;
                            dataSets.push(modelObject);
                        }
                        this.splineAreaChart.series = dataSets;
                        this.splineAreaChart.chartOptions.xaxis.categories = categories;
                    }
                }

            }).finally(() => {
                this.loading = false;
            })
        },
        filterWarningRank(index) {
            this.activeIndex = index;
            if (index == 1) {
                this.getTodayAlarmTrend();
            } else if (index == 2) {
                this.getLastWeekAlarmTrend();
            } else {
                this.getLastMonthAlarmTrend();
            }
        },
        getTodayAlarmTrend() {
            this.chartLoading = true;
            this.splineAreaChart.series = [];
            this.splineAreaChart.chartOptions.xaxis.categories = [];
            getTodayAlarmTrend().then(res => {
                setTimeout(() => {
                    this.chartLoading = false;
                    if (res.code == 200) {
                        var result = res.data;
                        if (Object.keys(result).length > 0) {
                            var dataSets = [];
                            var categories = [];
                            var isfirst = true;
                            for (const key in result) {
                                var modelObject = {};
                                modelObject.name = key;
                                var dataArray = [];
                                for (const sonkey in result[key]) {
                                    if (isfirst) {
                                        categories.push(sonkey);
                                    }
                                    dataArray.push(result[key][sonkey]);
                                }
                                isfirst = false;
                                modelObject.data = dataArray;
                                dataSets.push(modelObject);
                            }
                            this.splineAreaChart.series = dataSets;
                            this.splineAreaChart.chartOptions.xaxis.categories = categories;
                        }
                    }
                }, 500);
            }).catch(() => {
                this.chartLoading = false;
            })
        },
        getLastWeekAlarmTrend() {
            this.chartLoading = true;
            this.splineAreaChart.series = [];
            this.splineAreaChart.chartOptions.xaxis.categories = [];
            getLastWeekAlarmTrend().then(res => {
                setTimeout(() => {
                    this.chartLoading = false;
                    if (res.code == 200) {
                        var result = res.data;
                        if (Object.keys(result).length > 0) {
                            var dataSets = [];
                            var categories = [];
                            var isfirst = true;
                            for (const key in result) {
                                var modelObject = {};
                                modelObject.name = key;
                                var dataArray = [];
                                for (const sonkey in result[key]) {
                                    if (isfirst) {
                                        categories.push(sonkey);
                                    }
                                    dataArray.push(result[key][sonkey]);
                                }
                                isfirst = false;
                                modelObject.data = dataArray;
                                dataSets.push(modelObject);
                            }
                            this.splineAreaChart.series = dataSets;
                            this.splineAreaChart.chartOptions.xaxis.categories = categories;
                        }
                    }
                }, 500);

            }).catch(() => {
                this.chartLoading = false;
            })
        },
        getLastMonthAlarmTrend() {
            this.chartLoading = true;
            this.splineAreaChart.series = [];
            this.splineAreaChart.chartOptions.xaxis.categories = [];
            getLastMonthAlarmTrend().then(res => {
                setTimeout(() => {
                    this.chartLoading = false;
                    if (res.code == 200) {
                        var result = res.data;
                        if (Object.keys(result).length > 0) {
                            var dataSets = [];
                            var categories = [];
                            var isfirst = true;
                            for (const key in result) {
                                var modelObject = {};
                                modelObject.name = key;
                                var dataArray = [];
                                for (const sonkey in result[key]) {
                                    if (isfirst) {
                                        categories.push(sonkey);
                                    }
                                    dataArray.push(result[key][sonkey]);
                                }
                                isfirst = false;
                                modelObject.data = dataArray;
                                dataSets.push(modelObject);
                            }
                            this.splineAreaChart.series = dataSets;
                            this.splineAreaChart.chartOptions.xaxis.categories = categories;
                        }
                    }
                }, 500);

            }).catch(() => {
                this.chartLoading = false;
            })
        },
        initWebSocket() {
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
                    if (this.isfirst) {
                        this.screenLoading = false;
                        this.isfirst = false;
                    } else {
                        //切换监控画面时下一个视频流开始传输时清除加载状态
                        if (this.changeVideoSuccess) {
                            this.deviceAbnormal = false;
                            this.screenLoading = false;
                            this.changeVideoSuccess = false;
                        }
                    }

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
                            //连接成功后获取默认的监控画面
                            for (let i = 0; i < this.monitorList.length; i++) {
                                var cameraList = this.monitorList[i].children;
                                for (let j = 0; j < cameraList.length; j++) {
                                    if (cameraList[j].status != undefined) {
                                        if (cameraList[j].status == 1) {
                                            this.selectedMonitor = [this.monitorList[i].value, cameraList[j].value];
                                            this.enabledStreamTransmit();
                                            break;
                                        }
                                    }
                                }
                                if (this.selectedMonitor.length > 0) {
                                    break;
                                }
                            }
                        }
                    } catch {
                        if (typeof event.data == "string") {
                            //停掉上一个监控时才会出现 准备播放
                            if (event.data == "准备播放") {
                                this.changeVideoSuccess = true;
                            }

                            if (event.data == "视频流无法打开") {
                                this.changeVideoSuccess = true;
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
        enabledStreamTransmit() {
            this.screenLoading = true;
            //开启视频流传输
            enabledStreamTransmit({ videoStreaming: this.selectedMonitor[1], sessionId: this.sessionID });
        },
        handleMonitorChange(value) {
            this.screenInputUrl = "";
            this.deviceAbnormal = false;
            this.enabledStreamTransmit();
        },
        toggleFullScreen() {
            //全屏显示
            this.isFullScreen = !this.isFullScreen;
            this.$fullscreen.toggle(this.$refs.fullscreenElement);
        },
        onFullScreenChange() {
            //关闭全屏监控画面
            if (!document.fullscreenElement) {
                this.isFullScreen = false;
                this.$fullscreen.toggle(this.$refs.fullscreenElement);
            }
        },
        toMoreWarning() {
            this.$router.push("/eventWarning");
        },
        viewDetail(row) {
            this.alarmLoading = true;
            getWarningEventDetail({ alertId: row.alertId }).then(res => {
                if (res.code == 200) {
                    this.dialogVisible = true;
                    this.alarmInfo = res.data;
                    if (Object.keys(this.alarmInfo).length > 0) {
                        this.alarmInfo.capturedImage = baseURL.split("/api")[0] + this.alarmInfo.capturedImage;
                        this.alarmInfo.capturedVideo = baseURL.split("/api")[0] + this.alarmInfo.capturedVideo;
                    }
                }
            }).finally(() => {
                this.alarmLoading = false;
            })

        },
        createDevice() {
            this.$router.push("/videoAccess");
        },
        createTask() {
            this.$router.push("/taskMonitor");
        }
    }
};
</script>
<style lang="scss" scoped>
.stats-label {
    font-size: 14px;
}

.stats-value {
    color: #343a40;
    font-family: Inter, sans-serif;
    font-size: 22px;
    margin-top: 8px;
}


.stats-digit {
    display: flex;
    align-items: flex-start;
    border-bottom-left-radius: 0;
    border-bottom-right-radius: 0;

    .card-body {
        flex: 1;
    }

    .card-icon i {
        font-size: 28px;
    }
}

.stats-trend {
    border-top-left-radius: 0;
    border-top-right-radius: 0;

    .card-body {
        display: flex;
        align-items: center;

        .stats-value-ratio {
            font-size: 11px;
            margin-right: 8px;

            i {
                margin-right: 3px;
            }

            .icon-yuandian {
                font-size: 11px;
            }
        }

        .stats-value-desc {
            font-size: 14px;
        }
    }
}

.layout-top {
    height: 32px;
    margin-bottom: 10px;

    .title {
        color: #343a40;
        font-size: 15px;
        font-weight: 500;
        font-family: Inter, sans-serif;
        // display: flex;
        // align-items: center;

        i {
            margin-right: 5px;
            font-size: 18px;
        }
    }

    .action {
        padding: 0 6px;

        .chart-filter {

            &.active {
                color: #FFFFFF;
                background-color: #5664d2;
                border-color: #5664d2;
            }
        }
    }
}

.layout-content {

    .realtime-video {
        position: relative;

        .action {
            position: absolute;
            left: 0;
            bottom: 5px;
            width: 100%;
            height: 30px;
            // background-color: rgba(0, 0, 0, 0.4);
            z-index: 10;

            .fullscreen-icon {
                position: absolute;
                right: 10px;
                bottom: 50%;
                transform: translateY(50%);
                color: #fff;
                font-size: 18px;
                // font-weight: 600;
            }
        }
    }

    .chart-empty,
    .footage-empty,
    .alarm-empty {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .simple-list {
        height: 45vh;
        padding-right: 15px;
        overflow: hidden;

        &:after {
            content: " ";
            display: table;
        }

        &:before {
            content: " ";
            display: table;
        }

        &::-webkit-scrollbar {
            width: 0;
        }
    }


    .list-unstyled {
        padding-left: 0;
        list-style: none;
        animation-timing-function: linear;
        margin-top: 8px;
        margin-left: 16px;
    }

    .activity-wid {

        .activity-list {

            &:before {
                content: "";
                border-left: 2px dashed rgba(86, 100, 210, .25);
                position: absolute;
                left: 0;
                bottom: 0;
                top: 32px;
            }
        }
    }

    .activity-list {
        position: relative;
        padding: 0 0 40px 30px;

        // &:last-child {
        //     padding-bottom: 0;
        // }

        .activity-icon {
            position: absolute;
            left: -15px;
            top: 0;
            z-index: 9;

            .avatar-title {
                align-items: center;
                display: flex;
                font-weight: 500;
                height: 100%;
                justify-content: center;
                width: 100%;
            }
        }

        .avatar-xs {
            width: 32px;
            height: 32px;
        }

        .event-list {

            .event-list-item {
                display: flex;
                justify-content: space-between;
                align-items: center;

                .event-list-item-left {
                    width: 65%;

                    .event-date {
                        color: #030a1a;
                        font-family: Inter, sans-serif;
                        font-weight: 500;
                        margin-bottom: 8px;

                        small {
                            font-size: 80%;
                            font-weight: 400;
                        }
                    }

                    .event-name {
                        font-size: 14px;
                        line-height: 24px;

                        .value {
                            color: #030a1a;
                        }
                    }
                }

                .event-list-item-right {
                    width: 30%;

                    .event-image {
                        height: 70px;

                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                            border-radius: 4px;
                        }
                    }
                }
            }
        }
    }
}

.dialog-wrap {
    padding: 0 10px;
    max-height: 60vh;
    overflow: auto;
    display: flex;
    justify-content: space-between;

    .detail-info {
        width: calc(100% - 630px);
        padding: 12px 20px;
        box-sizing: border-box;

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
}
</style>