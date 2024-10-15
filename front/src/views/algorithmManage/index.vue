<template>
    <div class="container">
        <div class="main-wrapper card" v-loading="loading">
            <div class="filter-container" v-if="!isfirst">
                <!-- <div class="filter-label">应用场景</div> -->
                <div class="filter-options">
                    <div class="option" :class="{ 'atv-option': activeScene == '全部' }" @click="filterScene('全部')">全部
                    </div>
                    <div class="option" v-for="(item, index) in sceneList" :key="index"
                        :class="{ 'atv-option': activeScene == item.modelType }" @click="filterScene(item.modelType)">{{
                            item.modelType
                        }}</div>
                </div>
            </div>
            <div class="list-action flex-between">
                <div class="list-total">为您找到相关算法<span class="total-count">{{ totalCount }}</span>个</div>
                <div class="right-button">
                    <el-button type="primary" icon="el-icon-upload2" size="small"
                        @click="createAlgorithm">导入模型</el-button>
                </div>
            </div>
            <div class="algorithm-list">
                <div class="algorithm-card" v-for="(item, index) in algorithmList" :key="index"
                    @click="viewDetail(item)">
                    <div class="thumbnail">
                        <img :src="item.imgs" alt="">
                    </div>
                    <div class="name">{{ item.modelName }}</div>
                    <div class="scene">{{ /,/.test(item.scene) ? item.scene.replace(/,/g, "&nbsp;&nbsp;") : item.scene
                        }}
                    </div>
                    <div class="desciription" :title="item.modelExplain">{{ item.modelExplain }}</div>
                    <div class="source">
                        <el-tag type="info" size="mini" v-if="item.aiModels.modelSource == 0">系统默认</el-tag>
                        <el-tag size="mini" v-else>用户上传</el-tag>
                    </div>
                    <div class="action" v-if="item.aiModels.modelSource == 1">
                        <div class="status"></div>
                        <div class="more" @click.stop>
                            <el-dropdown size="small">
                                <span class="el-dropdown-link">
                                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item><i class="el-icon-delete"></i>卸载</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </div>
                </div>
                <el-empty description="暂无数据" style="margin: 0 auto 60px;" v-if="algorithmList.length == 0"></el-empty>
                <div class="finally-tip" v-if="!loading">
                    <div class="finally-tip-text">已经到最后啦！没有找到想要的算法？您提需求，我来补充！</div>
                    <el-button type="primary" size="small" @click="toMoreAlgorithm">我想要新算法</el-button>
                </div>
            </div>

            <div class="rnav-menu">
                <a class="item should-popover">
                    <span class="circle-box"><i class="iconfont icon-weixin"></i></span>
                    <div class="rnav-name">联系我们</div>
                    <div class="popover-img"><img src="@/assets/images/wxcode.jpg" alt=""></div>
                </a>
                <!-- <a class="item">
                    <span class="circle-box"><i class="iconfont icon-fankui"></i></span>
                    <div class="rnav-name">反馈建议</div>
                </a>
                <a class="item" href="" target="_blank">
                    <span class="circle-box"><i class="iconfont icon-anzhuangshouce"></i></span>
                    <div class="rnav-name">用户手册</div>
                </a> -->
            </div>
        </div>
        <el-dialog title="导入模型" :visible.sync="uploadDialogVisible" width="40%" :close-on-click-modal="false"
            v-loading="dialogLoading" element-loading-background="rgba(255, 255, 255, 0.4)">
            <div class="dialog-wrap">
                <el-upload class="upload-demo" drag action="" accept=".zip" :auto-upload="false" :file-list="fileList"
                    :limit="1" :on-change="handleFileChange" :on-remove="handleRemoveFile">
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip" slot="tip">只能上传zip文件，一次性只能上传一个且不超过100M</div>
                </el-upload>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="uploadDialogVisible = false" size="small">取 消</el-button>
                <el-button type="primary" @click="submitCreate" size="small">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog title="我想要新算法" :visible.sync="demandDialogVisible" width="38%" :close-on-click-modal="false">
            <div class="demand-wrap">
                <div class="title">专注视觉 AI 算法定制</div>
                <div class="sub-title">1000+种 AI 场景落地经验</div>
                <div class="sub-title">最快 8 小时生产一个全新算法</div>
                <div class="qrcode">
                    <img src="@/assets/images/wxcode.jpg" alt="">
                </div>
            </div>
        </el-dialog>
    </div>

</template>

<script>
import { getSceneList, getAlgorithmList, getAlgorithDetail, exportAlgorithModel } from "@/api/algorithmManage";
import baseURL from "@/utils/request";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            isfirst: true,
            params: {
                scene: ""
            },
            sceneList: [],
            activeScene: "全部",
            totalCount: 0,
            algorithmList: [],
            uploadDialogVisible: false,
            demandDialogVisible: false,
            fileList: [],
            dialogLoading: false
        };
    },
    created() {
        this.inintLoading();
    },
    mounted() {

    },
    watch: {},
    computed: {},
    methods: {
        inintLoading() {
            this.loading = true;
            var requests = [getSceneList(), getAlgorithmList(this.params)];
            Promise.all(requests).then(results => {
                setTimeout(() => {
                    this.loading = false;
                    if (results[0].code == 200) {
                        this.sceneList = results[0].data;
                        if (this.sceneList.length > 0) {
                            this.isfirst = false;
                        }
                    }
                    if (results[1].code == 200) {
                        this.totalCount = results[1].count;
                        if (results[1].data.length > 0) {
                            this.algorithmList = results[1].data;
                            this.algorithmList.forEach(item => {
                                if (item.imgs) {
                                    item.imgs = baseURL.split("/api")[0] + item.imgs;
                                }
                            })
                        }

                    }
                }, 500);
            }).catch(() => {
                this.loading = false;
            })

        },
        getAlgorithmList() {
            this.loading = true;
            this.algorithmList = [];
            getAlgorithmList(this.params).then(res => {
                setTimeout(() => {
                    this.loading = false;
                    if (res.code == 200) {
                        this.totalCount = res.count;
                        if (res.data.length > 0) {
                            this.algorithmList = res.data;
                            this.algorithmList.forEach(item => {
                                if (item.imgs) {
                                    item.imgs = baseURL.split("/api")[0] + item.imgs;
                                }
                            })
                        } else {
                            this.algorithmList = [];
                        }
                    }
                }, 500);

            }).catch(() => {
                this.loading = false;
            })
        },
        filterScene(scene) {
            this.activeScene = scene;
            if (scene == "全部") {
                this.params.scene = "";
            } else {
                this.params.scene = scene;
            }

            this.getAlgorithmList();
        },
        viewDetail(row) {
            this.$router.push({ path: "/algorithmManage/tryout", query: { id: row.id } });
        },
        createAlgorithm() {
            this.fileList = [];
            this.uploadDialogVisible = true;
        },
        submitCreate() {
            if (this.fileList.length > 0) {
                this.dialogLoading = true;
                let formData = new FormData();
                let modelFile = this.fileList[0].raw;
                formData.append("modelFile", modelFile);
                exportAlgorithModel(formData).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '上传成功!'
                        });
                        this.uploadDialogVisible = false;
                        this.getAlgorithmList();
                    }
                }).finally(() => {
                    setTimeout(() => {
                        this.dialogLoading = false;
                    }, 1000);
                })
            } else {
                this.$message({
                    message: '请上传模型文件',
                    type: 'warning'
                });
            }
        },
        handleFileChange(file, fileList) {
            console.log(file, fileList);
            var size = file.size / 1024 / 1024;
            if (size <= 100) {
                this.fileList = fileList;
            } else {
                this.$message({
                    message: '上传文件的大小不能超过100M',
                    type: 'warning'
                });
            }
        },
        handleRemoveFile(file, fileList) {
            console.log(file, fileList);
            this.fileList = fileList;
        },
        toMoreAlgorithm() {
            this.demandDialogVisible = true;
        }
    }
};
</script>
<style lang="scss" scoped>
.filter-container {
    margin-top: 10px;
    padding: 0 12px;

    .filter-label {
        color: #1b1e26;
        font-size: 16px;
        font-weight: 600;
    }

    .filter-options {
        display: flex;
        flex-wrap: wrap;
        margin-top: 15px;

        .option {
            border-radius: 3px;
            cursor: pointer;
            margin-bottom: 20px;
            margin-right: 5px;
            padding: 4px 16px;
            font-size: 14px;

            &.atv-option {
                color: #3852dd;
                background-color: rgba(101, 118, 200, 0.18);
            }
        }
    }
}

.list-action {

    .list-total {
        font-size: 14px;
        color: #a6a6a6;

        .total-count {
            margin: 0 3px;
        }
    }
}

.algorithm-list {
    display: flex;
    flex-wrap: wrap;
    margin-top: 10px;

    .algorithm-card {
        position: relative;
        border: 1px solid #ebebeb;
        border-radius: 6px;
        width: 23.5%;
        height: 318px;
        // height: 305px;
        margin-bottom: 20px;
        margin-right: 2%;
        overflow: hidden;
        box-sizing: border-box;
        cursor: pointer;

        &:nth-child(4n) {
            margin-right: 0;
        }

        .thumbnail {
            height: 169px;
            margin-bottom: 12px;
            overflow: hidden;
            width: 100%;

            &:hover img {
                transform: scale(1.04);
                transition: all 0.3s;
            }

            img {
                height: 100%;
                object-fit: cover;
                width: 100%;
            }
        }

        .name {
            color: #333;
            font-size: 15px;
            font-weight: 600;
            margin-bottom: 6px;
            padding: 0 90px 0 12px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .scene {
            color: #4d5869;
            font-size: 13px;
            font-weight: 400;
            margin-bottom: 6px;
            padding: 0 12px;
        }

        .desciription {
            color: #4d5869;
            font-size: 12px;
            font-weight: 400;
            height: 40px;
            line-height: 20px;
            padding: 0 12px;
            overflow: hidden;
            text-overflow: ellipsis;
            -webkit-line-clamp: 2;
            display: -webkit-box;
            -webkit-box-orient: vertical;
        }

        .source {
            position: absolute;
            top: 178px;
            right: 12px;
        }

        .action {
            display: flex;
            justify-content: flex-end;
            font-size: 13px;
            width: calc(100% - 24px);
            position: absolute;
            left: 12px;
            bottom: 10px;

            .status {
                color: #f27045;
                margin-right: 15px;
            }

            .more {

                .el-dropdown {
                    font-size: 13px;
                    color: #909399;
                }

            }
        }
    }

    .finally-tip {
        width: 100%;
        text-align: center;
        margin-top: 20px;

        .finally-tip-text {
            color: #b6b6b6;
            font-size: 14px;
            margin-bottom: 15px;
        }
    }

}

.upload-demo {
    width: 100%;

    ::v-deep .el-upload {
        width: 100%;

        .el-upload-dragger {
            width: 100%;
        }
    }
}



.rnav-menu {
    position: fixed;
    z-index: 999;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    background: #fff;
    border: 1px solid #eee;
    border-radius: 4px;
    transition: right .5s ease;
}

.rnav-menu .item {
    display: block;
    width: 55px;
    text-align: center;
    margin-top: 0;
    padding-top: 14px;
    padding-bottom: 12px;
    position: relative;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}

.rnav-menu .item:last-of-type {
    border-bottom: none;
}

.rnav-menu .item .rnav_icon {
    width: 32px;
    height: 32px;
}

.rnav-menu .item .rnav-name {
    display: block;
    color: #333;
    font-size: 12px;
    text-align: center;
    transform: scale(.9);
    font-weight: 400;
    margin-bottom: 0;
    margin-top: 5px;
    cursor: pointer;
}

.rnav-menu .item .popover-img {
    position: absolute;
    top: -63px;
    left: -250px;
    z-index: 1099;
    width: 195px;
    opacity: 0;
    visibility: hidden;
    border-radius: 3px;
    transition: left .5s ease, opacity .5s ease-in;
}

.rnav-menu .item .popover-img img {
    width: 195px;
    border-radius: 3px;
    box-shadow: 0 0 8px 2px rgba(0, 0, 0, .1);
}

.circle-box {
    display: inline-block;
    width: 32px;
    height: 32px;
    line-height: 32px;
    border-radius: 50%;
    background-color: #e9f5ff;
}

.circle-box .iconfont {
    font-size: 20px;
    color: #1b6cff;
}

.rnav-menu .item:hover {
    background-color: #128bed;
}

.rnav-menu .item:first-of-type:hover {
    border-radius: 4px 4px 0 0;
}

.rnav-menu .item:last-of-type:hover {
    border-radius: 0 0 4px 4px;
}


.rnav-menu .item:hover .rnav-name {
    color: #fff;
}

.rnav-menu .item:hover .circle-box {
    background-color: #fff;
}

.should-popover:hover .popover-img {
    opacity: 1;
    left: -210px;
    visibility: visible;
}

.feedback p {
    margin-bottom: 1rem;
}

.weixin-code img {
    width: 150px;
}

.form {
    width: 85%;
    margin: 0 auto;
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

                    img {
                        width: 100%;
                        height: 260px;
                        object-fit: cover;
                    }

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


.scene-list {

    ::v-deep .el-form-item__content {
        line-height: 30px;
    }
}


.demand-wrap {
    text-align: center;

    .title {
        font-size: 24px;
        color: #3f3f40;
        letter-spacing: 0.88px;
        margin-bottom: 12px;
    }

    .sub-title {
        font-size: 16px;
        line-height: 28px;
        letter-spacing: 0.75px;
        color: #828282;
    }

    .qrcode {
        width: 340px;
        height: 340px;
        margin: 0 auto;

        img {
            width: 100%;
            height: 100%;
        }
    }
}
</style>