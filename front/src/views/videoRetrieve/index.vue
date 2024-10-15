<template>
    <div class="container">
        <div class="main-wrapper card" v-loading="loading">
            <div class="top-action">
                <div class="image-search-box" v-if="imageUrl">
                    <div class="upload-image">
                        <img :src="imageUrl" alt="">
                        <span class="delete-icon" @click="deleteImage"><i class="el-icon-close"></i></span>
                    </div>
                    <el-input placeholder="请输入多个关键词，用空格隔开" v-model="params.keyword" @keyup.enter.native="searchKeyword">
                        <el-button slot="append" icon="el-icon-search" class="search-btn"
                            @click="searchKeyword">搜索</el-button>
                    </el-input>
                    <el-button type="text" icon="el-icon-camera" class="picToVid-btn" title="图搜视频"
                        @click="uploadImage"></el-button>
                </div>
                <div class="search-box" v-else>
                    <el-input placeholder="请输入多个关键词，用空格隔开" v-model="params.keyword" @keyup.enter.native="searchKeyword">
                        <el-button slot="append" icon="el-icon-search" class="search-btn"
                            @click="searchKeyword">搜索</el-button>
                    </el-input>
                    <el-button type="text" icon="el-icon-camera" class="picToVid-btn" title="图搜视频"
                        @click="uploadImage"></el-button>
                </div>
                <div class="hot-keywords" v-if="hotKeywords.length > 0">
                    <span class="hot-label">热门搜索：</span>
                    <div class="keyword-list">
                        <span class="keyword" v-for="(keyword, index) in hotKeywords" :key="index"
                            @click="chooseHotKeyword(keyword)">{{ keyword }}</span>
                    </div>
                </div>
            </div>

            <div class="main-content">
                <div class="statistics">
                    <div class="search-total">为您找到相关视频<span class="total-count">{{ totalCount }}</span>个</div>
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
                                        <el-radio v-model="selectedTime" :label="item.value"
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
                            <div class="model"><span class="text-gray label">算法模型：</span><span class="value">{{
                                item.alertType }}</span>
                            </div>
                            <div class="date"><span class="text-gray label">抓拍时间：</span><span class="value">{{
                                item.alertTime
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
        <el-dialog title="视频详情" :visible.sync="dialogVisible" width="65%">
            <div class="dialog-wrap">
                <div class="detail-info">
                    <div class="result-item">
                        <div class="result-item-key">摄像头点位</div>
                        <div class="result-item-value">{{ videoInfo.cameraPosition }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">监测任务</div>
                        <div class="result-item-value">{{ videoInfo.monitoringTask }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">算法模型</div>
                        <div class="result-item-value">{{ videoInfo.alertType }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">抓拍时间</div>
                        <div class="result-item-value">{{ videoInfo.alertTime.slice(0, 16) }}</div>
                    </div>
                    <div class="result-item">
                        <div class="result-item-key">视频标签</div>
                        <div class="result-item-value">{{ videoInfo.videoTags }}</div>
                    </div>
                </div>
                <div class="detail-video">
                    <video :src="videoInfo.capturedVideo" :poster="videoInfo.capturedImage" controls muted
                        preload="auto" autoplay="autoplay" loop="loop" style="width: 528px;height: 396px;"></video>
                </div>
            </div>
        </el-dialog>
        <el-dialog :visible.sync="uploadVisible" width="40%" :close-on-click-modal="false">
            <div class="upload-wrap">
                <div class="title">按图片搜索</div>
                <div class="sub-title">上传一张图片开始您的搜索。</div>
                <!-- <div class="sub-title">选择以下任何一种方式开始您的搜索。</div> -->
                <!-- <div class="upload-imageUrl">
                    <el-input placeholder="请在此处粘贴图片网址" v-model="pasteImageUrl" size="medium"
                        @keyup.enter.native="searchImage">
                        <el-button slot="append" icon="el-icon-search" class="search-btn"
                            @click="searchImage"></el-button>
                    </el-input>
                </div> -->
                <div class="upload-imageFile">
                    <el-upload class="upload-demo" drag action="" accept=".jpg,.png,.jpeg" :auto-upload="false"
                        :limit="1" :file-list="fileList" :on-change="handleFileChange">
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">只能上传jpg/png/jpeg文件，一次性只能上传一个且不超过10MB</div>
                    </el-upload>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getVideoList, getLocationList, getAllAlgorithm, getVideoListByImage, getVideoDetail } from "@/api/videoRetrieve";
import baseURL from "@/utils/request";
import axios from "axios";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            params: {
                pageNum: 1,
                pageSize: 12,
                keyword: "",
                selectedLoaction: [],
                startTime: "",
                endTime: "",
                imageFile: ""
            },
            imageUrl: "",
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
            hotKeywords: [],
            totalCount: 0,
            dataList: [],
            dialogVisible: false,
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
            uploadVisible: false,
            pasteImageUrl: "",
            fileList: [],
            videoInfo: {
                alertTime: "",
                alertType: "",
                cameraPosition: "",
                capturedImage: "",
                capturedVideo: "",
                monitoringTask: "",
                videoInfo: ""
            }
        };
    },
    created() {
        this.initLoading();
    },
    mounted() {

    },
    watch: {},
    computed: {},
    methods: {
        initLoading() {
            this.loading = true;
            var params = { pageNum: this.params.pageNum, pageSize: this.params.pageSize, alertTypes: this.params.keyword ? this.params.keyword.split(" ") : [], cameraPosition: this.params.selectedLoaction.includes("点位不限") ? [] : this.params.selectedLoaction, startTime: this.params.startTime, endTime: this.params.endTime };
            var requests = [getLocationList(), getVideoList(params), getAllAlgorithm()];
            Promise.all(requests).then(results => {
                setTimeout(() => {
                    this.loading = false;
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

                    if (results[2].code == 200) {
                        if (results[2].data.length > 0) {
                            results[2].data.forEach(item => {
                                this.hotKeywords.push(item.modelName);
                            })
                            if (this.hotKeywords.length > 8) {
                                this.hotKeywords = this.hotKeywords.slice(0, 9);
                            }
                        }
                    }
                }, 500);
            }).catch(() => {
                this.loading = false;
            })

        },
        getVideoList() {
            this.loading = true;
            this.dataList = [];
            var params = { pageNum: this.params.pageNum, pageSize: this.params.pageSize, alertTypes: this.params.keyword ? this.params.keyword.split(" ") : [], cameraPosition: this.params.selectedLoaction.includes("点位不限") ? [] : this.params.selectedLoaction, startTime: this.params.startTime, endTime: this.params.endTime };
            getVideoList(params).then(res => {
                setTimeout(() => {
                    this.loading = false;
                    if (res.code == 200) {
                        this.totalCount = res.count;
                        if (res.data.length > 0) {
                            this.dataList = res.data;
                            this.dataList.forEach(item => {
                                item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                            })
                        }
                    }
                }, 500);

            }).catch(() => {
                this.loading = false;
            })
        },
        searchKeyword() {
            this.imageUrl = "";
            this.params.pageNum = 1;
            this.getVideoList();
        },
        chooseHotKeyword(keyword) {
            this.params.keyword = keyword;
            this.searchKeyword();
        },
        searchImage() {
            // this.urlToAxiosFile(this.pasteImageUrl, "image.png").then(file => {
            //     console.log(file)
            // })
        },
        // urlToAxiosFile(url, filename) {
        //     return axios({
        //         url: url,
        //         method: 'GET',
        //         responseType: 'blob', // 告诉Axios返回的数据类型是blob
        //     }).then(response => {
        //         const blob = new Blob([response.data], { type: response.headers['content-type'] });
        //         return new File([blob], filename, { type: blob.type });
        //     });
        // },
        deleteImage() {
            this.searchKeyword();
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
            this.params.pageNum = 1;
            if (this.imageUrl) {
                this.getVideoListByImage();
            } else {
                this.getVideoList();
            }
        },
        viewVideo(row) {
            this.loading = true;
            getVideoDetail({ alertId: row.alertId }).then(res => {
                if (res.code == 200) {
                    this.dialogVisible = true;
                    this.videoInfo = res.data;
                    if (Object.keys(this.videoInfo).length > 0) {
                        this.videoInfo.videoTags = /,/.test(this.videoInfo.videoTags) ? this.videoInfo.videoTags.replace(/,/g, "，") : this.videoInfo.videoTags;
                        this.videoInfo.capturedImage = baseURL.split("/api")[0] + this.videoInfo.capturedImage;
                        this.videoInfo.capturedVideo = baseURL.split("/api")[0] + this.videoInfo.capturedVideo;
                    }
                }
            }).finally(() => {
                this.loading = false;
            })
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
            this.params.pageNum = 1;
            if (this.imageUrl) {
                this.getVideoListByImage();
            } else {
                this.getVideoList();
            }
        },
        timestampToDate(timestamp) {
            let now = new Date(timestamp);
            let y = now.getFullYear();
            let m = now.getMonth() + 1;
            let d = now.getDate();
            return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + now.toTimeString().substring(0, 8);
        },
        uploadImage() {
            this.pasteImageUrl = "";
            this.fileList = [];
            this.uploadVisible = true;
        },
        getVideoListByImage() {
            this.loading = true;
            this.dataList = [];
            let formData = new FormData();
            formData.append("image", this.params.imageFile.raw);
            formData.append("pageNum", this.params.pageNum);
            formData.append("pageSize", this.params.pageSize);
            formData.append("alertTypes", this.params.keyword ? this.params.keyword.split(" ") : []);
            formData.append("cameraPosition", this.params.selectedLoaction.includes("点位不限") ? [] : this.params.selectedLoaction);
            formData.append("startTime", this.params.startTime);
            formData.append("endTime", this.params.endTime);
            getVideoListByImage(formData).then(res => {
                setTimeout(() => {
                    this.loading = false;
                    if (res.code == 200) {
                        this.totalCount = res.count;
                        if (res.data.length > 0) {
                            this.dataList = res.data;
                            this.dataList.forEach(item => {
                                item.capturedImage = baseURL.split("/api")[0] + item.capturedImage;
                            })
                        }
                    }
                }, 500);
            }).catch(() => {
                this.loading = false;
            })
        },
        handleFileChange(file, fileList) {
            console.log(file, fileList)
            var size = file.size / 1024 / 1024;
            if (size <= 10) {
                this.imageUrl = URL.createObjectURL(file.raw);
                this.params.imageFile = file;
                this.params.pageNum = 1;
                this.uploadVisible = false;
                this.getVideoListByImage();
            } else {
                this.$message({
                    message: '上传图片的大小不能超过10M',
                    type: 'warning'
                });
            }

        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`);
            this.params.pageNum = 1;
            this.params.pageSize = val;
            if (this.imageUrl) {
                this.getVideoListByImage();
            } else {
                this.getVideoList();
            }

        },
        handleCurrentChange(val) {
            // console.log(`当前页: ${val}`);
            this.params.pageNum = val;
            if (this.imageUrl) {
                this.getVideoListByImage();
            } else {
                this.getVideoList();
            }
        },
    }
};
</script>
<style lang="scss" scoped>
.top-action {
    width: 70%;
    text-align: center;
    margin: 15px auto 30px;


    .search-box,
    .image-search-box {
        position: relative;

        .upload-image {
            height: 32px;
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            z-index: 10;


            img {
                width: 100%;
                height: 100%;
            }

            .delete-icon {
                position: absolute;
                right: 1px;
                top: 1px;
                opacity: .8;
                width: 12px;
                height: 12px;
                line-height: 12px;
                text-align: center;
                background-color: rgba(0, 0, 0, .8);
                cursor: pointer;

                i {
                    font-size: 12px;
                    color: #fff;
                }
            }
        }

        .search-btn {
            background-color: #5664d2;
            color: #fff;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
        }

        .picToVid-btn {
            position: absolute;
            top: 50%;
            right: 110px;
            transform: translateY(-50%);
            font-size: 18px;
            color: #aeb1bc;

            &:hover {
                color: #5664d2;
            }
        }

        ::v-deep .el-input__inner {
            padding-right: 55px;
        }
    }

    .image-search-box {

        ::v-deep .el-input__inner {
            padding-left: 65px;
        }
    }

    .hot-keywords {
        display: flex;
        margin-top: 16px;
        font-size: 14px;

        .keyword-list {

            .keyword {
                margin-right: 22px;
                cursor: pointer;

                &:hover {
                    color: #5664d2;
                }
            }
        }
    }

}


.main-content {


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
                width: 23.5%;
                height: 270px;
                margin-right: 2%;
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