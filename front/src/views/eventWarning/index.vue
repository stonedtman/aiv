<template>
    <div class="container">
        <div class="main-wrapper flex-between">
            <div class="filter-wrap card" v-loading="filterLoading">
                <div class="filter-header border-bottom">
                    <i class="iconfont icon-list"></i>
                    筛选条件
                </div>
                <div class="filter-body">
                    <div class="title">预警类型</div>
                    <el-checkbox-group v-model="params.alertTypes" @change="handleAlarmTypeChange">
                        <el-checkbox v-for="(item, index) in alarmTypeList" :key="index"
                            :label="item.label"></el-checkbox>
                    </el-checkbox-group>
                    <el-empty description="暂无数据" v-if="alarmTypeList.length == 0"></el-empty>
                </div>
            </div>
            <div class="main-content card" v-loading="tableLoading">
                <div class="top-action">
                    <div class="search-box">
                        <el-input placeholder="请输入多个关键词，用空格隔开" v-model="params.keyword"
                            @keyup.enter.native="filterList">
                            <el-button slot="append" icon="el-icon-search" class="search-btn"
                                @click="filterList">搜索</el-button>
                        </el-input>
                    </div>
                </div>

                <div class="statistics">
                    <div class="search-total">共有<span class="total-count">{{ totalCount }}</span>条告警事件</div>
                    <div class="senior-search">
                        <div class="search-control">
                            <el-dropdown :hide-on-click="false">
                                <span class="el-dropdown-link">
                                    {{ params.selectedLoaction.length > 0 ? params.selectedLoaction.join("，") : "摄像头点位"
                                    }}<i class="el-icon-arrow-down el-icon--right"></i>
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item v-for="item in loactionOptions" :key="item.value"
                                        :command="item.value">
                                        <el-checkbox :key="item.value" v-model="params.selectedLoaction"
                                            :label="item.value" @change="handleChangeLocation(item, $event)">{{
                                                item.label }}</el-checkbox>
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                        <div class="search-control">
                            <el-dropdown :hide-on-click="false">
                                <span class="el-dropdown-link">
                                    {{ selectedTime ? selectedTime : "时间范围" }}<i
                                        class="el-icon-arrow-down el-icon--right"></i>
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item v-for="item in timeOptions" :key="item.value"
                                        :command="item.value">
                                        <el-radio ref="radio" v-model="selectedTime" :label="item.value"
                                            @input="handleTimeChange">{{
                                                item.label }}</el-radio>
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </div>
                </div>
                <div class="search-result">
                    <div class="image-list">
                        <div class="image-item" v-for="(item, index) in dataList" :key="index">
                            <div class="image">
                                <img :src="item.capturedImage" alt="">
                                <div class="play-icon">
                                    <i class="el-icon-caret-right" @click="viewVideo(item)"></i>
                                </div>
                            </div>
                            <div class="position"><span class="text-gray label">摄像头点位：</span><span class="value">{{
                                item.cameraPosition
                                    }}</span></div>
                            <div class="model"><span class="text-gray label">预警类型：</span><span class="value">{{
                                item.alertType }}</span>
                            </div>
                            <div class="date"><span class="text-gray label">预警时间：</span><span class="value">{{
                                item.alertTime.slice(0, 16)
                                    }}</span></div>
                        </div>
                    </div>
                    <el-empty description="暂无数据" v-if="dataList.length == 0"></el-empty>
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="params.pageNum" :page-sizes="[10, 20, 30, 40, 50]" :page-size="params.pageSize"
                        layout="total, prev, pager, next" :total="totalCount" v-if="dataList.length > 0">
                    </el-pagination>
                </div>
            </div>
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
                        <div class="result-item-value">{{ alarmInfo.alertTime.slice(0, 16) }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">告警级别</div>
                        <div class="result-item-value">{{ alarmInfo.alertLevel }}</div>
                    </div>
                </div>
                <div class="detail-video">
                    <video :src="alarmInfo.capturedVideo" :poster="alarmInfo.capturedImage" controls muted
                        preload="auto" autoplay="autoplay" loop="loop" style="width: 528px;height: 396px;"></video>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getWarningEvent, getAllAlgorithm, getAllLocations, getWarningEventDetail } from "@/api/eventWarning";
import baseURL from "@/utils/request";
export default {
    components: {},
    props: {},
    data() {
        return {
            filterLoading: false,
            tableLoading: false,
            params: {
                pageNum: 1,
                pageSize: 12,
                keyword: "",
                alertTypes: [],
                selectedLoaction: [],
                startTime: "",
                endTime: "",
            },
            alarmTypeList: [],
            totalCount: 0,
            dataList: [],
            dialogVisible: false,
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
            loactionOptions: [
                { label: '点位不限', value: '点位不限' },
            ],
            selectedTime: "",
            timeOptions: [
                { label: '时间不限', value: '时间不限' },
                { label: '过去 1 小时', value: '过去 1 小时' },
                { label: '过去 24 小时', value: '过去 24 小时' },
                { label: '过去 1 周', value: '过去 1 周' },
                { label: '过去 1 个月', value: '过去 1 个月' },
                { label: '过去 3 个月', value: '过去 3 个月' },
                { label: '过去 1 年', value: '过去 1 年' },
            ],

        };
    },
    created() {
        this.getAllAlgorithm();
        this.initLoading();
    },
    mounted() {
        //去除radio组件的aria-hidden属性，防止报错
        this.$refs.radio.forEach(item => {
            item.$refs.radio.removeAttribute("aria-hidden");
        })
    },
    watch: {},
    computed: {},
    methods: {
        getAllAlgorithm() {
            this.filterLoading = true;
            this.alarmTypeList = [];
            getAllAlgorithm().then(res => {
                setTimeout(() => {
                    this.filterLoading = false;
                    if (res.code == 200) {
                        if (res.data.length > 0) {
                            res.data.forEach(item => {
                                this.alarmTypeList.push({ label: item.modelName, value: item.id });
                            })
                        }
                    }
                }, 500);
            }).catch(() => {
                this.filterLoading = false;
            })
        },
        initLoading() {
            this.tableLoading = true;
            var params = { pageNum: this.params.pageNum, pageSize: this.params.pageSize, searchText: this.params.keyword, alertTypes: this.params.alertTypes, cameraPosition: this.params.selectedLoaction.includes("点位不限") ? [] : this.params.selectedLoaction, startTime: this.params.startTime, endTime: this.params.endTime };
            var requests = [getAllLocations(), getWarningEvent(params)];
            Promise.all(requests).then(results => {
                setTimeout(() => {
                    this.tableLoading = false;
                    if (results[0].code == 200) {
                        if (results[0].data.length > 0) {
                            results[0].data.forEach(item => {
                                this.loactionOptions.push({ label: item, value: item });
                            })
                        }
                    }

                    if (results[1].code == 200) {
                        if (results[1].data.length > 0) {
                            this.dataList = results[1].data;
                            this.dataList.forEach(item => {
                                item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                            })
                            this.totalCount = results[1].count;
                        }
                    }
                }, 500);
            }).catch(() => {
                this.tableLoading = false;
            })
        },
        getWarningEvent() {
            this.tableLoading = true;
            this.dataList = [];
            var params = { pageNum: this.params.pageNum, pageSize: this.params.pageSize, searchText: this.params.keyword, alertTypes: this.params.alertTypes, cameraPosition: this.params.selectedLoaction.includes("点位不限") ? [] : this.params.selectedLoaction, startTime: this.params.startTime, endTime: this.params.endTime };
            getWarningEvent(params).then(res => {
                setTimeout(() => {
                    this.tableLoading = false;
                    if (res.code == 200) {
                        this.dataList = res.data;
                        this.dataList.forEach(item => {
                            item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                        })
                        this.totalCount = res.count;
                    }
                }, 500);
            }).catch(() => {
                this.tableLoading = false;
            })
        },
        filterList() {
            this.params.pageNum = 1;
            this.getWarningEvent();
        },
        handleAlarmTypeChange() {
            this.params.pageNum = 1;
            this.getWarningEvent();
        },
        viewVideo(row) {
            this.tableLoading = true;
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
                this.tableLoading = false;
            })
        },
        handleChangeLocation(row, val) {
            if (row.value == "点位不限" && val) {
                this.params.selectedLoaction = ["点位不限"];
            } else {
                this.params.selectedLoaction.forEach((item, index) => {
                    if (item == "点位不限") {
                        this.params.selectedLoaction.splice(index, 1);
                    }
                })
            }
            this.filterList();
        },
        handleTimeChange() {
            console.log(this.selectedTime)
            if (this.selectedTime == "时间不限") {
                this.params.startTime = "";
                this.params.endTime = "";
            } else if (this.selectedTime == "过去 1 小时") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            } else if (this.selectedTime == "过去 24 小时") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000 * 24);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            } else if (this.selectedTime == "过去 1 周") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000 * 24 * 7);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            } else if (this.selectedTime == "过去 1 个月") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000 * 24 * 30);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            } else if (this.selectedTime == "过去 3 个月") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000 * 24 * 90);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            } else if (this.selectedTime == "过去 3 个月") {
                this.params.startTime = this.timestampToDate(new Date().getTime() - 3600 * 1000 * 24 * 365);
                this.params.endTime = this.timestampToDate(new Date().getTime());
            }
            this.filterList();
        },
        timestampToDate(timestamp) {
            let now = new Date(timestamp);
            let y = now.getFullYear();
            let m = now.getMonth() + 1;
            let d = now.getDate();
            return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + now.toTimeString().substring(0, 8);
        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`);
            this.params.pageNum = 1;
            this.params.pageSize = val;
            this.getWarningEvent();
        },
        handleCurrentChange(val) {
            // console.log(`当前页: ${val}`);
            this.params.pageNum = val;
            this.getWarningEvent();
        },
    }
};
</script>
<style lang="scss" scoped>
.filter-wrap {
    width: 215px;
    position: fixed;
    left: 20px;
    top: 150px;
    box-sizing: border-box;
    height: calc(100vh - 160px);
    // overflow: hidden;
    padding: 0;

    .filter-header {
        padding: 0 12px;
        color: #343a40;
        font-family: Inter, sans-serif;
        font-size: 16px;
        font-weight: 600;
        height: 40px;
        line-height: 40px;

        i {
            margin-right: 5px;
        }
    }

    .filter-body {
        padding: 15px 20px 20px;

        .title {
            color: #101010;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .el-checkbox-group {
            height: calc(100vh - 260px);
            overflow: auto;

            .el-checkbox {
                display: block;
                margin-left: 8px;

                &:not(:last-child) {
                    margin-bottom: 12px;

                }
            }
        }


    }
}






.main-content {
    flex: 1;
    margin-left: 230px;

    .top-action {
        width: 60%;
        margin: 15px 0 30px;


        .search-box {
            position: relative;

            .search-btn {
                background-color: #5664d2;
                color: #fff;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }
        }

    }

    .statistics {
        display: flex;
        align-items: center;

        .search-total {
            font-size: 14px;
            color: #a6a6a6;

            .total-count {
                margin: 0px 3px;
            }
        }

        .senior-search {
            display: flex;
            margin-left: 50px;

            .search-control {
                border: none;

                &:not(:last-child) {
                    margin-right: 30px;
                }
            }
        }
    }

    .search-result {
        margin-top: 15px;

        .image-list {
            display: flex;
            flex-wrap: wrap;

            .image-item {
                width: 24.1%;
                height: 270px;
                margin-right: 1.2%;
                margin-bottom: 20px;
                border: 1px solid #ebebeb;
                border-radius: 6px;
                box-sizing: border-box;
                font-size: 14px;
                overflow: hidden;
                // color: #a2a8b2;

                &:nth-child(4n) {
                    margin-right: 0;
                }

                .image {
                    width: 100%;
                    height: 169px;
                    margin-bottom: 12px;
                    position: relative;

                    img {
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                    }

                    .play-icon {
                        position: absolute;
                        bottom: 50%;
                        transform: translateY(50%);
                        right: 0;
                        left: 0;
                        text-align: center;


                        i {
                            display: inline-block;
                            background-color: rgba(0, 0, 0, 0.5);
                            width: 40px;
                            height: 40px;
                            line-height: 40px;
                            color: #fff;
                            font-size: 26px;
                            text-align: center;
                            box-shadow: 0 .5rem 1rem rgba(0, 0, 0, .15);
                            border-radius: 50%;
                            cursor: pointer;

                            &:hover {
                                -webkit-text-stroke: 2px #5664d2;
                                -webkit-text-fill-color: #5664d2;
                            }
                        }
                    }
                }

                .position,
                .model {
                    // font-weight: 600;
                    padding: 0 12px;
                    margin-bottom: 6px;

                    .value {
                        color: #030a1a;
                    }
                }


                .date {
                    padding: 0 12px;

                    .value {
                        color: #030a1a;
                    }
                }
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
        width: calc(100% - 558px);
        padding: 12px 20px;
        overflow-y: auto;
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

.upload-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    max-height: 60vh;
    overflow: auto;

    .title {
        font-size: 24px;
        color: #333;
        padding: 0 0 16px;
        font-weight: 600;
    }

    .sub-title {
        color: rgba(0, 0, 0, 0.65);
        font-size: 18px;
        padding: 0 0 24px;
    }

    .upload-imageUrl {
        width: 90%;
        margin-bottom: 24px;

        .search-btn {
            background-color: #5664d2;
            color: #fff;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
            height: 38px;
            box-sizing: border-box;
        }
    }

    .upload-imageFile {
        width: 90%;

        ::v-deep .el-upload {
            width: 100%;

            .el-upload-dragger {
                width: 100%;
            }
        }
    }

}
</style>