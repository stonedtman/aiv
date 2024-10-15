<template>
    <div class="container">
        <div class="page-breadcrumb">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/algorithmManage' }">算法列表</el-breadcrumb-item>
                <el-breadcrumb-item>模型预测</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="main-wrapper card" v-loading="loading">
            <div class="center-container">
                <div class="model-detail">
                    <div class="image"><img :src="modelInfo.image" alt=""></div>
                    <div class="info">
                        <div class="title">{{ modelInfo.title }}</div>
                        <div class="scenes" v-html="modelInfo.scenes"></div>
                        <div class="desc">{{ modelInfo.desc }}</div>
                    </div>
                </div>

                <div class="tryout-model">
                    <div class="left-box">
                        <div class="title">识别图片</div>
                        <div class="image-container request-image" :style="{ height: imageContainHeight + 'px' }">
                            <img :src="requestImageUrl" alt="">
                        </div>
                        <div class="action">
                            <div class="upload-btn">
                                <input type="file" class="fileUpload" accept=".jpg,.png,.jpeg"
                                    @change="handleFileChange">
                                <el-button type="primary" size="small">上传图片</el-button>
                            </div>
                        </div>
                    </div>
                    <div class="right-box">
                        <div class="mark">
                            <el-tabs v-model="activeName">
                                <el-tab-pane label="识别结果" name="first">
                                    <div class="image-container return-image"
                                        :style="{ height: imageContainHeight + 'px' }" v-loading="callLoading"
                                        element-loading-text="识别中">
                                        <div class="mark-image">
                                            <img :src="returnImageUrl" alt="">
                                            <div class="inside-iframe" v-for="(item, index) in coordinate" :key="index"
                                                :style="{ width: item.width + 'px', height: item.height + 'px', left: item.left + 'px', top: item.top + 'px' }">
                                                <span>{{ item.label }} {{ item.score }}</span>
                                            </div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                                <el-tab-pane label="JSON结果" name="second">
                                    <div class="return-result" :style="{ height: imageContainHeight + 'px' }"
                                        v-loading="callLoading" element-loading-text="识别中">
                                        <pre v-html="returnResult"></pre>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                            <div class="confidence-filter" v-if="activeName == 'first'">
                                置信度
                                <el-tooltip placement="top" effect="light">
                                    <div slot="content">置信度说明：图中方框的百分比置信度是衡量算法识别准确性的量化指标，<br />置信度越高表示当前算法对检测结果的准确度越有信心。
                                    </div>
                                    <span class="question-icon">
                                        <svg viewBox="64 64 896 896" focusable="false" data-icon="question-circle"
                                            width="1em" height="1em" fill="currentColor" aria-hidden="true"
                                            class="el-tooltip" aria-describedby="el-tooltip-3086" tabindex="0">
                                            <path
                                                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z">
                                            </path>
                                            <path
                                                d="M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7a48.3 48.3 0 0130.9-44.8c59-22.7 97.1-74.7 97.1-132.5.1-39.3-17.1-76-48.3-103.3zM472 732a40 40 0 1080 0 40 40 0 10-80 0z">
                                            </path>
                                        </svg>
                                    </span>
                                </el-tooltip>
                                <el-input v-model="confidence" placeholder="请输入置信度(10~99)" size="small"
                                    @input="checkInputValue" @blur="filterConfidence"></el-input>
                                <span class="unit">%</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { getAlgorithDetail, modelToPredict } from "@/api/algorithmManage";
import baseURL from "@/utils/request";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            modelInfo: {
                modelId: null,
                image: "",
                title: "",
                scenes: "",
                desc: ""
            },
            activeName: "first",
            imageContainHeight: "400",
            requestImageUrl: "",
            returnImageUrl: "",
            returnResult: "",
            confidence: "",
            callLoading: false,
            originalCoordinate: [],
            coordinate: [],
        };
    },
    created() {
        this.modelInfo.modelId = this.$route.query.id;
        this.getAlgorithDetail(this.modelInfo.modelId);
    },
    mounted() {

    },
    watch: {},
    computed: {},
    methods: {
        getAlgorithDetail(modelId) {
            this.loading = true;
            getAlgorithDetail({ Id: modelId }).then(res => {
                if (res.code == 200) {
                    this.modelInfo.image = baseURL.split("/api")[0] + res.data.imgs;
                    this.modelInfo.title = res.data.modelName;
                    this.modelInfo.scenes = /,/.test(res.data.scene) ? res.data.scene.replace(/,/g, "&nbsp;&nbsp;") : res.data.scene;
                    this.modelInfo.desc = res.data.modelExplain;
                    if (res.data.testResult) {
                        this.requestImageUrl = baseURL.split("/api")[0] + res.data.imgTest;
                        var testResult = JSON.parse(res.data.testResult);
                        // console.log(testResult)

                        this.$nextTick(() => {
                            this.callLoading = true;
                            var imageElement = document.querySelector(".request-image img");
                            imageElement.onload = () => {
                                //获取页面上图片展示的宽高
                                var imageWidth = document.querySelector(".request-image img").offsetWidth;
                                var imageHeight = document.querySelector(".request-image img").offsetHeight;
                                // console.log(imageWidth, imageHeight)
                                this.imageContainHeight = imageHeight;

                                setTimeout(() => {
                                    let image = new Image();
                                    image.onload = () => {
                                        this.returnImageUrl = baseURL.split("/api")[0] + res.data.imgTest;
                                        var primaryWidth = image.width;
                                        var primaryHeight = image.height;
                                        var widthRatio = (imageWidth / primaryWidth).toFixed(2);
                                        var heightRatio = (imageHeight / primaryHeight).toFixed(2);
                                        var coordinate = testResult.data.coordinate;
                                        var markList = [];
                                        coordinate.forEach((item) => {
                                            var obj = {
                                                width: (item.xyxy[2] - item.xyxy[0]) * widthRatio,
                                                height: (item.xyxy[3] - item.xyxy[1]) * heightRatio,
                                                left: item.xyxy[0] * widthRatio,
                                                top: item.xyxy[1] * heightRatio,
                                                label: item.label,
                                                score: item.score,
                                            };
                                            markList.push(obj);
                                        });
                                        this.coordinate = markList;
                                        this.originalCoordinate = [...this.coordinate];
                                        this.returnResult = this.syntaxHighlight(testResult);
                                        this.callLoading = false;
                                    };
                                    image.src = this.requestImageUrl;
                                }, 3000);
                            }
                        })
                    }
                }

            }).finally(() => {
                this.loading = false;
            })
        },
        handleFileChange(event) {
            // console.log(event.target.files[0])
            //清空上一个文件的内容
            this.returnImageUrl = "";
            this.coordinate = [];
            this.returnResult = "";
            this.confidence = "";

            var file = event.target.files[0];
            var reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = (e) => {
                this.$message({
                    type: 'success',
                    message: '上传成功!'
                });
                //图片路径设置为读取的图片
                this.requestImageUrl = e.target.result;
                this.$nextTick(() => {
                    var imageElement = document.querySelector(".request-image img");
                    imageElement.onload = () => {
                        //获取页面上图片展示的宽高
                        var imageWidth = document.querySelector(".request-image img").offsetWidth;
                        var imageHeight = document.querySelector(".request-image img").offsetHeight;
                        // console.log(imageWidth, imageHeight)
                        this.imageContainHeight = imageHeight;
                    }
                })

                this.callLoading = true;
                var formData = new FormData();
                formData.append("file", file);
                formData.append("Id", this.modelInfo.modelId);
                modelToPredict(formData).then(res => {
                    if (res.code == 200) {
                        if (res.data.labels) {
                            var tempList = [];
                            var coordinate = res.data.labels;
                            if (this.confidence) {
                                coordinate.forEach((item) => {
                                    if (item.score * 100 >= parseInt(this.confidence)) {
                                        tempList.push(item);
                                    }
                                });
                            } else {
                                tempList = coordinate;
                            }
                            if (tempList.length == 0) {
                                this.$message({
                                    type: 'warning',
                                    message: '图片中无相关目标，请更换图片再试试吧！'
                                });

                            }
                            this.returnImageUrl = e.target.result;

                            this.$nextTick(() => {
                                var imageElement = document.querySelector(".return-image img");
                                imageElement.onload = () => {
                                    //获取页面上图片展示的宽高
                                    var imageWidth = document.querySelector(".return-image img").offsetWidth;
                                    var imageHeight = document.querySelector(".return-image img").offsetHeight;
                                    var markList = [];
                                    tempList.forEach((item) => {
                                        var obj = {
                                            width: item.width * imageWidth,
                                            height: item.height * imageHeight,
                                            left: item.x * imageWidth,
                                            top: item.y * imageHeight,
                                            label: item.zhLabel,
                                            score: item.confidence,
                                        };
                                        markList.push(obj);
                                    });
                                    this.coordinate = [...markList];
                                    this.originalCoordinate = [... this.coordinate];
                                }
                            })


                        } else {
                            this.returnImageUrl = e.target.result;
                        }
                        this.returnResult = this.syntaxHighlight(res.data);
                    }
                }).finally(() => {
                    this.callLoading = false;
                })


            };

        },
        filterConfidence() {
            if (JSON.parse(this.confidence) < 10) {
                this.confidence = 10;
            }
            if (JSON.parse(this.confidence) > 99) {
                this.confidence = 99;
            }
            var tempList = [];
            this.originalCoordinate.forEach((item) => {
                if (item.score * 100 >= parseInt(this.confidence)) {
                    tempList.push(item);
                }
            });
            this.coordinate = [...tempList];
            if (this.coordinate.length == 0) {
                this.$message({
                    type: 'warning',
                    message: '图片中无相关目标，请更换图片再试试吧！'
                });

            }
        },
        checkInputValue() {
            this.confidence = this.confidence.replace(/[^0-9]/g, "");
        },
        syntaxHighlight(json) {
            if (typeof json != "string") {
                json = JSON.stringify(json, undefined, 2);
            }
            json = json.replace(/&/g, "&").replace(/</g, "<").replace(/>/g, ">");
            return json.replace(
                /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
                function (match) {
                    var cls = "number";
                    if (/^"/.test(match)) {
                        if (/:$/.test(match)) {
                            cls = "key";
                        } else {
                            cls = "string";
                        }
                    } else if (/true|false/.test(match)) {
                        cls = "boolean";
                    } else if (/null/.test(match)) {
                        cls = "null";
                    }
                    return '<span class="' + cls + '">' + match + "</span>";
                }
            );
        }
    }
};
</script>
<style lang="scss" scoped>
.center-container {
    padding: 0 100px;

    .model-detail {
        display: flex;
        font-size: 16px;
        padding: 23px 0;

        .image {
            flex-shrink: 0;
            height: 154px;
            width: 211px;

            img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }
        }

        .info {
            padding: 0 32px 0 40px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .title {
                color: #3d3d3d;
                font-size: 36px;
            }

            .scenes {
                color: #999;
                font-size: 16px;
            }

            .desc {
                color: #3d3d3d;
                font-size: 16px;
            }
        }
    }

    .tryout-model {
        margin-top: 20px;
        display: flex;

        .left-box {
            width: calc((100% - 20px)/2);

            .image-container {
                margin-top: 15px;
            }

            .action {
                margin-top: 12px;

                .upload-btn {

                    .fileUpload {
                        position: absolute;
                        width: 80px;
                        height: 32px;
                        opacity: 0;
                        cursor: pointer;
                    }
                }


            }
        }

        .right-box {
            width: calc((100% - 20px)/2);
            margin-left: 20px;
            position: relative;

            .confidence-filter {
                position: absolute;
                right: 0;
                top: 0;
                display: flex;
                align-items: center;
                white-space: nowrap;

                .question-icon {
                    margin: 0 10px 0 5px;

                    svg {
                        vertical-align: -0.125em;
                    }
                }

                .el-input {
                    width: 160px;
                }

                .unit {
                    margin-left: 5px;
                }
            }
        }

        .title {
            font-size: 15px;
            font-weight: 600;
            color: #303133;
            height: 40px;
            line-height: 50px;
        }

        .image-container {
            border: 1px solid #ccc;
            padding: 24px;
            box-sizing: content-box;
            position: relative;

            .mark-image {
                position: relative;

                .inside-iframe {
                    position: absolute;
                    border: 3px solid red;
                    box-sizing: border-box;

                    span {
                        display: inline-block;
                        white-space: nowrap;
                        background-color: red;
                        color: #fff;
                        margin-top: -24px;
                        font-size: 15px;
                        position: absolute;
                    }
                }
            }

            img {
                width: 100%;
                object-fit: contain;
            }


        }

        .return-result {
            border: 1px solid #ccc;
            padding: 24px;
            box-sizing: content-box;

            pre {
                padding: 5px;
                overflow: auto;
                white-space: pre-wrap;
                word-wrap: break-word;
            }

            ::v-deep .key {
                color: red;
            }

            ::v-deep .string {
                color: green;
            }


        }
    }
}

::v-deep .el-tabs__item {
    font-weight: 600;
}

::v-deep .el-tabs__nav-wrap::after {
    height: 1px;
}
</style>