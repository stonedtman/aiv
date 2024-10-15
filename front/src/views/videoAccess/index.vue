<template>
    <div class="container">
        <div class="main-wrapper flex-between">
            <div class="menu-list" v-loading="loadingMenu">
                <div class="menu-header flex-between">
                    <div class="header-left">设备列表</div>
                    <div class="header-right"><el-button type="text" @click="createDeviceGroup"><i
                                class="el-icon-plus icon-left"></i>添加分组</el-button></div>
                </div>
                <div class="menu-body">
                    <el-input placeholder="输入分组或点位进行过滤" v-model="filterText" size="small"></el-input>
                    <div class="menu-wrap">
                        <el-tree ref="tree" :data="treeData" :props="defaultProps" node-key="id" default-expand-all
                            :expand-on-click-node="false" :filter-node-method="filterNode" :highlight-current="true"
                            @node-click="nodeClick" empty-text="">
                            <span class="custom-tree-node father" slot-scope="{ node, data }">
                                <span class="node-label">
                                    <i class="iconfont icon-list font-size-14" v-if="data.children"></i>
                                    <i class="el-icon-video-camera" v-else></i>
                                    <span :title="node.label">{{ node.label }}</span>
                                    <span class="dot"
                                        :class="{ 'normal': data.cameraStatus == 1, 'abnormal': data.cameraStatus == 0 }"></span>
                                </span>
                                <span class="child" @click.stop>
                                    <el-dropdown trigger="click" size="small" @command="clickMore">
                                        <span class="el-dropdown-link">
                                            <el-button icon="el-icon-more" type="text" size="small" />
                                        </span>
                                        <el-dropdown-menu slot="dropdown">
                                            <el-dropdown-item icon="el-icon-edit-outline" v-if="data.children"
                                                :command="beforeClickMore('updateGroup', data, node)">
                                                编辑分组
                                            </el-dropdown-item>
                                            <el-dropdown-item icon="el-icon-delete" v-if="data.children"
                                                :command="beforeClickMore('deleteGroup', data, node)">
                                                删除分组
                                            </el-dropdown-item>
                                            <el-dropdown-item icon="el-icon-plus"
                                                :command="beforeClickMore('createDevice', data, node)"
                                                v-if="data.children">
                                                添加设备
                                            </el-dropdown-item>
                                            <el-dropdown-item icon="el-icon-edit-outline" v-if="!data.children"
                                                :command="beforeClickMore('updateDevice', data, node)">
                                                编辑设备
                                            </el-dropdown-item>
                                            <el-dropdown-item icon="el-icon-delete" v-if="!data.children"
                                                :command="beforeClickMore('deleteDevice', data, node)">
                                                删除设备
                                            </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </el-dropdown>
                                </span>
                            </span>
                        </el-tree>
                        <el-empty description="暂无数据" v-if="treeData.length == 0"></el-empty>
                    </div>
                </div>
            </div>
            <div class="main-content" v-loading="loadingTable">
                <div class="top-action flex-between">
                    <div class="left-action">
                        <div class="split-screen">
                            分屏：
                            <el-dropdown trigger="click" @command="handleCommand">
                                <span class="el-dropdown-link">
                                    {{ screenNumber }}<i class="el-icon-arrow-down el-icon--right"></i>
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item command="16分屏">16分屏</el-dropdown-item>
                                    <el-dropdown-item command="9分屏">9分屏</el-dropdown-item>
                                    <el-dropdown-item command="4分屏">4分屏</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </div>
                    <div class="right-action">
                        <el-button type="primary" icon="el-icon-plus" size="mini" @click="createDevice">添加设备</el-button>
                    </div>
                </div>
                <div class="device-list" v-loading="fullScreenLoading" element-loading-text="画面加载中">
                    <div class=" device-item"
                        :class="{ 'col-4': screenNumber == '16分屏', 'col-3': screenNumber == '9分屏', 'col-2': screenNumber == '4分屏' }"
                        v-for="(item, index) in deviceList" :key="index">
                        <div class="device-wrap" :class="{ 'active': activeDeviceId == item.cameraId }">
                            <div class="device-video">
                                <div class="device-info flex-between">
                                    <div class="left-box">
                                        <i class="el-icon-video-camera icon-left"></i>
                                        <span class="device-location">{{ item.cameraLocation }}</span>
                                        <span class="dot"
                                            :class="{ 'normal': item.cameraStatus == 1, 'abnormal': item.cameraStatus == 0 }"></span>
                                    </div>
                                    <div class="right-box">
                                        <span class="device-group">{{ item.groupName }}</span>
                                    </div>
                                </div>
                                <div class="video-action" v-if="item.cameraStatus == 1 && item.cameraImg">
                                    <i class="iconfont icon-appshipinquanping" title="放大预览"
                                        @click="toggleFullScreen(item)"></i>
                                </div>
                                <div class="image" v-if="item.cameraStatus == 1 && item.cameraImg">
                                    <img :src="item.cameraImg" alt="">
                                </div>
                                <div class="screen-abnormal" v-if="item.cameraStatus == 0 || item.cameraImg == ''">
                                    <el-empty
                                        :description="item.cameraStatus == 0 ? '监控设备失效，画面无法显示' : '暂无监控画面'"></el-empty>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="device-create"
                        :class="{ 'col-4': screenNumber == '16分屏', 'col-3': screenNumber == '9分屏', 'col-2': screenNumber == '4分屏' }"
                        v-if="screenNumber == '16分屏' ? totalCount < 16 : screenNumber == '9分屏' ? totalCount < 9 : totalCount < 4">
                        <div class="device-create-wrap">
                            <div class="create-icon">
                                <i class="el-icon-plus" title="添加监控设备" @click="createDevice"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="params.pageNum" :page-sizes="[10, 20, 30, 40, 50]" :page-size="params.pageSize"
                    layout="total, prev, pager, next" :total="totalCount" v-if="deviceList.length > 0">
                </el-pagination>
            </div>
        </div>
        <el-dialog :title="groupTitle" :visible.sync="groupDialogVisible" width="35%" :close-on-click-modal="false"
            v-loading="dialogLoading">
            <div class="dialog-wrap">
                <div class="groupForm">
                    <el-form :model="groupForm" ref="groupForm" label-position="left" label-width="100px"
                        class="demo-ruleForm">
                        <div class="form-group">
                            <el-form-item label="分组名称" prop="groupName"
                                :rules="[{ required: true, message: '分组名称不能为空', trigger: 'blur' }, { max: 8, message: '长度不能超过8个字符', trigger: 'blur' }]">
                                <el-input v-model="groupForm.groupName" size="small" placeholder="请输入分组名称"></el-input>
                            </el-form-item>
                        </div>
                    </el-form>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="groupDialogVisible = false" size="small">取 消</el-button>
                <el-button type="primary" @click="submitCreateGroup" size="small">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog class="form-dialog" :title="deviceTitle" :visible.sync="deviceDialogVisible" width="70%"
            :close-on-click-modal="false" :close-on-press-escape="false" v-loading="dialogLoading"
            element-loading-background="rgba(255, 255, 255, 0.4)" :before-close="handleCloseDialog">
            <div class="dialog-wrap">
                <div class="deviceForm">
                    <el-form :model="deviceForm" :rules="rules" ref="deviceForm" label-position="left"
                        label-width="120px" class="demo-ruleForm">
                        <div class="form-title">基本信息</div>
                        <div class="form-group">
                            <el-form-item label="摄像头点位" prop="location">
                                <el-input v-model="deviceForm.location" size="small"
                                    placeholder="请输入摄像头点位，例：北区加油站"></el-input>
                            </el-form-item>
                        </div>
                        <div class="form-group">
                            <el-form-item label="摄像头分组" prop="group">
                                <el-select v-model="deviceForm.group" size="small" placeholder="请选择摄像头分组"
                                    style="width: 100%;">
                                    <el-option v-for="(item, index) in cameraGroupList" :key="index" :label="item.label"
                                        :value="item.value"></el-option>
                                </el-select>
                            </el-form-item>
                        </div>

                        <!-- <el-divider></el-divider> -->

                        <div class="form-title">技术参数</div>
                        <div class="form-group">
                            <el-form-item label="视频流地址" prop="videoStreaming">
                                <el-input type="textarea" v-model="deviceForm.videoStreaming" rows="4"
                                    placeholder="请输入视频流地址" resize="none"></el-input>
                            </el-form-item>
                            <div class="video-streaming-prompt">例：rtsp://用户名:密码@ip地址:端口号/路径</div>
                        </div>
                    </el-form>
                </div>
                <div class="camera-wrap" v-loading="screenLoading" :element-loading-text="screenLoadingText">
                    <img :src="screenInputUrl" alt="" style="width: 528px;height: 396px;" v-if="screenInputUrl">
                    <el-empty :description="deviceAbnormal ? '无法获取监控画面' : '点击 测试连接 按钮获取监控画面'" v-else></el-empty>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="success" plain @click="testConnect" size="small">测试连接</el-button>
                <el-button type="primary" @click="submitCreateDevice" size="small">提 交</el-button>
            </span>
        </el-dialog>
        <el-dialog :visible.sync="fullScreenDialogVisible" :fullscreen="true" :close-on-click-modal="false"
            :close-on-press-escape="false" :before-close="disconnectVideoStreaming">
            <div class="fullScreen-container">
                <div class="image" style="display: inline-block;" v-loading="exitScreenLoading"
                    element-loading-text="画面关闭中" element-loading-background="rgba(255, 255, 255, 0.4)">
                    <img :src="fullScreenStream" alt="" style="">
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { createVideoDeviceGroup, deleteVideoDeviceGroup, updateVideoDeviceGroup, getVideoDevice, getVideoList, getVideoDeviceDetail, createVideoDevice, updateVideoDevice, getAllVideoDeviceGroup, deleteVideoDevice, getDeviceProtocol, enabledStreamTransmit, disabledStreamTransmit } from "@/api/videoAccess";
import baseURL from "@/utils/request";
import staticURL from "@/utils/staticRequest";
export default {
    components: {},
    props: {},
    data() {
        return {
            filterText: '',
            treeData: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            totalCount: 0,
            params: {
                gId: "",
                pageNum: 1,
                pageSize: 9
            },
            screenNumber: "9分屏",
            deviceList: [],
            activeDeviceId: null,
            loadingMenu: false,
            loadingTable: false,
            dialogLoading: false,
            groupTitle: "添加设备分组",
            groupDialogVisible: false,
            groupForm: {
                groupName: ""
            },
            checkedGroupId: null,
            deviceTitle: "添加监控设备",
            deviceDialogVisible: false,
            deviceForm: {
                location: "",
                group: "",
                videoStreaming: ""
            },
            rules: {
                location: [
                    { required: true, message: '摄像头点位不能为空', trigger: 'blur' },
                ],
                group: [
                    { required: true, message: '摄像头分组不能为空', trigger: 'change' },
                ],
                // ipAddress: [
                //     { required: true, message: 'IP地址不能为空', trigger: 'blur' },
                //     {
                //         pattern: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
                //         message: 'IP地址格式不正确', trigger: 'blur'
                //     },
                // ],
                // accessMode: [
                //     { required: true, message: '请选择设备接入方式', trigger: 'change' },
                // ],
                // protocol: [
                //     { required: true, message: '协议类型不能为空', trigger: 'change' },
                // ],
                // port: [
                //     { required: true, message: '端口号不能为空', trigger: 'blur' },
                //     {
                //         pattern: /^(?:[0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$/,
                //         message: '端口号格式不正确', trigger: 'blur'
                //     }
                // ],
                // username: [
                //     { required: true, message: '用户名不能为空', trigger: 'blur' },
                // ],
                // password: [
                //     { required: true, message: '密码不能为空', trigger: 'blur' },
                // ],
                videoStreaming: [
                    { required: true, message: '视频流地址不能为空', trigger: 'blur' },
                ],
            },
            cameraGroupList: [],
            webSocket: null,
            sessionID: "",//启动/关闭视频流传输的session-id
            screenInputUrl: "",//画面接入的url
            screenLoading: false,
            screenLoadingText: "画面加载中",
            deviceAbnormal: false,
            fullScreenStream: "",
            fullScreenLoading: false,
            fullScreenDialogVisible: false,
            exitScreenLoading: false
        };
    },
    created() {
        this.initWebSocket();
        this.getVideoDevice();
    },
    mounted() {

    },
    beforeDestroy() {
        //断开websocket连接
        this.closeWebSocket();

    },
    watch: {
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    },
    computed: {

    },
    methods: {
        filterNode(value, data) {
            if (!value) return true;
            return data.label.indexOf(value) !== -1;
        },
        beforeClickMore(type, data, node) {
            // console.log(type, data, node)
            return {
                type: type,
                data: data,
                node: node,
            }
        },
        clickMore(params) {
            console.log(params);
            switch (params.type) {
                case 'updateGroup':
                    this.confirmEditGroup(params.data);
                    break
                case 'deleteGroup':
                    this.confirmDeleteGroup(params.data);
                    break
                case 'createDevice':
                    this.createDevice(params.data);
                    break
                case 'updateDevice':
                    this.confirmEditDevice(params.data);
                    break
                case 'deleteDevice':
                    this.confirmDeleteDevice(params.data);
                    break

            }
        },
        //点击树形节点触发事件
        nodeClick(data, node) {
            console.log(data, node)
            if (data.children) {
                this.activeDeviceId = "";
                this.params.gId = data.id;
                this.params.pageNum = 1;
                this.getVideoList();
            } else {
                this.activeDeviceId = data.cameraId;
            }
        },
        handleCommand(command) {
            if (command == "16分屏") {
                this.params.pageNum = 1;
                this.params.pageSize = 16;
            } else if (command == "9分屏") {
                this.params.pageNum = 1;
                this.params.pageSize = 9;

            } else if (command == "4分屏") {
                this.params.pageNum = 1;
                this.params.pageSize = 4;

            }
            this.screenNumber = command;
            this.$nextTick(() => {
                this.autoFitScreenRatio();
            })
        },
        autoFitScreenRatio() {
            //切换分屏后保证画面的比例始终是 4:3
            var screenElements = document.querySelectorAll(".device-video");
            if (screenElements.length > 0) {
                for (let i = 0; i < screenElements.length; i++) {
                    screenElements[i].style.height = JSON.stringify(screenElements[i].offsetWidth / 4 * 3) + "px";
                }
            }

            var createElement = document.querySelector(".device-create-wrap");
            if (createElement) {
                createElement.style.height = JSON.stringify(createElement.offsetWidth / 4 * 3) + "px";
            }
        },
        getVideoDevice() {
            this.loadingMenu = true;
            this.treeData = [];
            getVideoDevice().then(res => {
                setTimeout(() => {
                    this.loadingMenu = false;
                    if (res.code == 200) {
                        var deviceList = res.data;
                        if (deviceList.length > 0) {
                            deviceList.forEach(item => {
                                var obj = {};
                                obj.id = item.id;
                                obj.label = item.groupName;
                                obj.groupId = item.groupId;
                                var children = [];
                                if (item.cameras != undefined) {
                                    item.cameras.forEach(child => {
                                        children.push({ id: child.id, label: child.cameraLocation, cameraId: child.cameraId, videoStreaming: child.videoStreaming, cameraStatus: child.cameraStatus });
                                    })
                                }
                                obj.children = children;
                                this.treeData.push(obj);
                            })
                            this.getVideoList();
                            // this.$nextTick(() => {
                            //     // 手动触发一次节点的高亮
                            //     this.$refs.tree.setCurrentKey(this.treeData[0].id);
                            // })
                        }
                    }
                    this.$nextTick(() => {
                        this.autoFitScreenRatio();
                    })
                }, 500);
            }).catch(() => {
                this.loadingMenu = false;
            })
        },
        getVideoList() {
            this.loadingTable = true;
            this.deviceList = [];
            getVideoList(this.params).then(res => {
                setTimeout(() => {
                    this.loadingTable = false;
                    if (res.code == 200) {
                        this.totalCount = res.count;
                        this.deviceList = res.data;
                        this.deviceList.forEach(item => {
                            if (item.cameraImg) {
                                item.cameraImg = baseURL.split("/api")[0] + item.cameraImg;
                            }
                        })
                    }
                }, 500);
            }).catch(() => {
                this.loadingTable = false;
            })
        },
        createDeviceGroup() {
            this.groupForm = {
                groupName: ""
            };
            if (this.$refs.groupForm !== undefined) {
                this.$refs.groupForm.resetFields();
            }
            this.groupTitle = "添加设备分组";
            this.groupDialogVisible = true;
        },
        confirmEditGroup(row) {
            this.groupForm = {
                groupName: ""
            };
            if (this.$refs.groupForm !== undefined) {
                this.$refs.groupForm.resetFields();
            }
            this.checkedGroupId = row.groupId;
            this.groupTitle = "编辑设备分组";

            this.groupForm.groupName = row.label;
            this.groupDialogVisible = true;
        },
        submitCreateGroup() {
            this.$refs["groupForm"].validate((valid) => {
                if (valid) {
                    this.dialogLoading = true;
                    if (this.groupTitle == "添加设备分组") {
                        createVideoDeviceGroup(this.groupForm).then(res => {
                            if (res.code == 200) {
                                this.$message({
                                    message: '添加成功',
                                    type: 'success'
                                });
                                this.groupDialogVisible = false;
                                this.getVideoDevice();
                            }
                        }).finally(() => {
                            setTimeout(() => {
                                this.dialogLoading = false;
                            }, 1000);
                        })
                    } else {
                        var form = { groupId: this.checkedGroupId, groupName: this.groupForm.groupName };
                        updateVideoDeviceGroup(form).then(res => {
                            if (res.code == 200) {
                                this.$message({
                                    message: '修改成功',
                                    type: 'success'
                                });
                                this.groupDialogVisible = false;
                                this.getVideoDevice();
                            }
                        }).finally(() => {
                            setTimeout(() => {
                                this.dialogLoading = false;
                            }, 1000);
                        })
                    }

                }
            });
        },
        confirmDeleteGroup(row) {
            this.$confirm('确定要删除该分组吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.loadingMenu = true;
                deleteVideoDeviceGroup({ groupId: row.groupId }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getVideoDevice();
                    }
                }).finally(() => {
                    this.loadingMenu = false;
                })
            })
        },
        createDevice() {
            this.deviceForm = {
                location: "",
                group: "",
                videoStreaming: ""
            };
            if (this.$refs.deviceForm !== undefined) {
                this.$refs.deviceForm.resetFields();
            }
            this.deviceTitle = "添加监控设备";
            this.screenLoadingText = "画面加载中";
            this.screenInputUrl = ""; //清空画面流地址
            this.deviceAbnormal = false; //重置画面异常状态
            this.loadingTable = true;
            this.cameraGroupList = [];

            getAllVideoDeviceGroup().then(res => {
                this.deviceDialogVisible = true;
                setTimeout(() => {
                    var dialogWrapElement = document.querySelector(".form-dialog .dialog-wrap");
                    dialogWrapElement.scrollTo({
                        top: 0,
                    });
                }, 100);
                if (res.code == 200) {
                    if (res.data.length > 0) {
                        res.data.forEach(item => {
                            this.cameraGroupList.push({ label: item.groupName, value: item.id })
                        })
                    }
                }
            }).finally(() => {
                this.loadingTable = false;
            })
        },
        confirmEditDevice(row) {
            this.deviceForm = {
                location: "",
                group: "",
                videoStreaming: ""
            };
            if (this.$refs.deviceForm !== undefined) {
                this.$refs.deviceForm.resetFields();
            }
            this.checkedDeviceId = row.cameraId;
            this.deviceTitle = "编辑监控设备";
            this.screenLoadingText = "画面加载中";
            this.screenInputUrl = ""; //清空画面流地址
            this.deviceAbnormal = false; //重置画面异常状态
            this.loadingTable = true;
            this.cameraGroupList = [];
            var requests = [getAllVideoDeviceGroup(), getVideoDeviceDetail({ cameraId: row.cameraId })];
            Promise.all(requests).then(results => {
                this.deviceDialogVisible = true;
                setTimeout(() => {
                    var dialogWrapElement = document.querySelector(".form-dialog .dialog-wrap");
                    dialogWrapElement.scrollTo({
                        top: 0,
                    });
                }, 100);
                if (results[0].code == 200) {
                    if (results[0].data.length > 0) {
                        results[0].data.forEach(item => {
                            this.cameraGroupList.push({ label: item.groupName, value: item.id })
                        })
                    }
                }

                if (results[1].code == 200) {
                    if (Object.keys(results[1].data).length > 0) {
                        this.deviceForm.location = results[1].data.cameraLocation;
                        this.cameraGroupList.forEach(item => {
                            if (results[1].data.groupName == item.label) {
                                this.deviceForm.group = item.value;
                            }
                        })
                        this.deviceForm.videoStreaming = results[1].data.videoStreaming;
                    }
                }
            }).finally(() => {
                this.loadingTable = false;
            })
        },
        //测试填写的参数是否可以连接摄像头
        testConnect() {
            this.$refs["deviceForm"].validate((valid) => {
                if (valid) {
                    this.screenLoading = true;
                    if (this.webSocket) {
                        //如果画面在连接中 需要先断开连接才能关闭弹框
                        if (this.screenInputUrl) {
                            this.$message('请先等待当前画面断开后再重新连接！');
                            this.screenLoading = true;
                            this.screenLoadingText = "画面断开中";
                            //断开视频流传输
                            disabledStreamTransmit({ sessionId: this.sessionID }).then(res => {
                                if (res.code == 200) {
                                    this.screenLoading = false;
                                    setTimeout(() => {
                                        this.screenLoading = true;
                                        //开启视频流传输
                                        this.enabledStreamTransmit();
                                    }, 500);

                                }
                            }).catch(() => {
                                this.screenLoading = false;
                            })
                        } else {
                            //开启视频流传输
                            this.enabledStreamTransmit();
                        }
                    } else {
                        //连接websocket
                        this.initWebSocket();
                    }

                }
            });
        },
        handleCloseDialog() {
            if (!this.screenInputUrl && !this.screenLoading) {
                this.deviceDialogVisible = false;
            }
            //如果画面在连接中 需要先断开连接才能关闭弹框
            else {
                this.screenLoading = true;
                this.screenLoadingText = "画面断开中";
                //断开视频流传输
                disabledStreamTransmit({ sessionId: this.sessionID }).then(res => {
                    if (res.code == 200) {
                        this.screenLoading = false;
                        this.deviceDialogVisible = false;
                    }
                }).catch(() => {
                    this.screenLoading = false;
                    this.deviceDialogVisible = false;
                })
            }
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

                    const blob = new Blob([event.data], { type: 'image/jpeg' });
                    const url = URL.createObjectURL(blob);

                    if (this.deviceDialogVisible) {
                        this.screenLoading = false;

                        if (!this.screenLoading) {
                            this.screenInputUrl = url;
                        }
                    } else {
                        this.fullScreenStream = url;
                        if (this.fullScreenLoading) {
                            this.fullScreenLoading = false;
                            this.fullScreenDialogVisible = true;
                        }

                    }

                } else {
                    try {
                        var message = JSON.parse(event.data);
                        if (message.type == "sessionId") {
                            //websocket连接成功后才会传sessionId
                            this.sessionID = message.sessionId;

                            // if (this.deviceDialogVisible) {
                            //     //开启视频流传输
                            //     this.enabledStreamTransmit();
                            // }
                        }
                    } catch {
                        if (typeof event.data == "string") {
                            if (event.data == "视频流无法打开") {
                                if (this.deviceDialogVisible) {
                                    this.screenInputUrl = "";
                                    this.deviceAbnormal = true;
                                    this.screenLoading = false;
                                } else {
                                    this.fullScreenLoading = false;
                                    this.$message.error("视频流无法播放");
                                }
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
        enabledStreamTransmit(streaming) {
            //当设备表单打开时
            if (this.deviceDialogVisible) {
                //调用视频流传输api
                enabledStreamTransmit({ videoStreaming: this.deviceForm.videoStreaming, sessionId: this.sessionID });
            }
            //全屏查看监控画面
            else {
                enabledStreamTransmit({ videoStreaming: streaming, sessionId: this.sessionID });
            }
        },
        toggleFullScreen(row) {
            if (this.webSocket) {
                this.fullScreenLoading = true;
                this.enabledStreamTransmit(row.videoStreaming);
            } else {
                this.$message.error("websocket连接失败");
            }
        },
        disconnectVideoStreaming() {
            this.exitScreenLoading = true;
            disabledStreamTransmit({ sessionId: this.sessionID }).then(res => {
                setTimeout(() => {
                    if (res.code == 200) {
                        this.exitScreenLoading = false;
                        this.fullScreenStream = "";
                        this.fullScreenDialogVisible = false;
                    }
                }, 500);

            }).catch(() => {
                this.exitScreenLoading = false;
                this.fullScreenStream = "";
            })
        },
        submitCreateDevice() {
            this.$refs["deviceForm"].validate((valid) => {
                if (valid) {
                    this.dialogLoading = true;
                    var form = { cameraLocation: this.deviceForm.location, cameraGroup: this.deviceForm.group, typeInput: 1, videoStreaming: this.deviceForm.videoStreaming };

                    if (this.deviceTitle == "添加监控设备") {
                        createVideoDevice(form).then(res => {
                            if (res.code == 200) {
                                this.$message({
                                    message: '添加成功',
                                    type: 'success'
                                });
                                this.getVideoDevice();
                                //调用关闭弹窗判断函数
                                this.handleCloseDialog();
                            }

                        }).finally(() => {
                            setTimeout(() => {
                                this.dialogLoading = false;
                            }, 1000);
                        })
                    } else {
                        form.cameraId = this.checkedDeviceId;
                        updateVideoDevice(form).then(res => {
                            if (res.code == 200) {
                                this.$message({
                                    message: '修改成功',
                                    type: 'success'
                                });
                                this.getVideoDevice();
                                //调用关闭弹窗判断函数
                                this.handleCloseDialog();
                            }

                        }).finally(() => {
                            setTimeout(() => {
                                this.dialogLoading = false;
                            }, 1000);
                        })
                    }
                }
            });
        },
        confirmDeleteDevice(row) {
            this.$confirm('确定要删除该设备吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.loadingTable = true;
                deleteVideoDevice({ cameraId: row.cameraId }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        if (this.deviceList.length == 1 && this.params.pageNum > 1) {
                            this.params.pageNum--;
                        }
                        this.getVideoDevice();
                    }
                }).finally(() => {
                    this.loadingTable = false;
                })
            })
        },
        handleSizeChange(val) {
            // console.log(`每页 ${val} 条`);
            this.params.pageNum = 1;
            this.params.pageSize = val;
            // this.getMonitorTask();
        },
        handleCurrentChange(val) {
            // console.log(`当前页: ${val}`);
            this.params.pageNum = val;
            // this.getMonitorTask();
        },
    }
};
</script>
<style lang="scss" scoped>
.menu-list {
    width: 255px;
    height: calc(100vh - 160px);
    background-color: #fff;
    border-radius: 4px;
    position: fixed;
    left: 20px;
    top: 150px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .menu-header {
        height: 40px;
        line-height: 40px;
        padding: 0 12px;
        font-size: 16px;
        font-weight: 600;
        color: #606266;
        border-bottom: 1px solid #eff2f7;
    }

    .menu-body {
        padding: 15px 20px;
        flex: 1;

        .menu-wrap {
            height: calc(100% - 50px);
            margin-top: 10px;
            overflow: auto;

            ::v-deep .el-tree-node__content {
                height: 33px;
            }

            .father {
                padding: 0 8px;
                flex: 1;
                display: flex;
                align-items: center;
                -webkit-box-pack: justify;
                -ms-flex-pack: justify;
                justify-content: space-between;
                font-size: 14px;

                &:hover .child {
                    visibility: visible;
                }

                .node-label {
                    display: flex;
                    align-items: center;
                    flex: 1 1 0%;
                    width: 0px;
                    margin-right: 5px;

                    span {
                        width: calc(100% - 15px);
                        margin-left: 6px;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                    }

                    .dot {
                        display: inline-block;
                        width: 5px;
                        height: 5px;
                        border-radius: 50%;
                        margin-left: 5px;


                        &.normal {
                            background-color: #1cbb8c;
                        }

                        &.abnormal {
                            background-color: #ff3d60;
                        }
                    }
                }

                .child {
                    width: 35px;
                    visibility: hidden;

                }
            }


        }
    }
}

.main-content {
    flex: 1;
    margin-left: 270px;
    background-color: #fff;
    // min-height: calc(100vh - 160px);
    min-height: 420px;
    padding-bottom: 20px;
    box-sizing: border-box;
    overflow-x: hidden;

    .top-action {
        height: 40px;
        line-height: 40px;
        padding: 0 12px;
        font-size: 14px;
        border-bottom: 1px solid #eff2f7;

        .el-dropdown-link {
            cursor: pointer;
            color: #5664d2;
        }

        .el-icon-arrow-down {
            font-size: 12px;
        }

        .right-action {

            .el-button {
                padding: 7px;
            }
        }
    }

    .device-list {
        display: flex;
        flex-wrap: wrap;
        padding: 7px;

        .device-item,
        .device-create {
            padding: 5px;
            box-sizing: border-box;

            &.col-3 {
                width: 33.33%;
            }

            &.col-4 {
                width: 25%;
            }

            &.col-2 {
                width: 50%;
            }

            .device-wrap {
                height: 100%;
                // border: 1px solid #ebebeb;
                // padding: 5px;
                box-sizing: border-box;

                &.active {
                    outline: 4px solid #5664d2;
                }

                .device-video {
                    position: relative;
                    height: 100%;

                    .device-info {
                        width: 100%;
                        height: 26px;
                        line-height: 26px;
                        position: absolute;
                        left: 0;
                        top: 0;
                        font-size: 14px;
                        padding: 0 5px;
                        box-sizing: border-box;
                        color: #fff;
                        background-color: rgba(0, 0, 0, .6);

                        .left-box {
                            display: flex;
                            align-items: center;

                            i {
                                font-size: 16px;
                            }

                            .dot {
                                display: inline-block;
                                width: 5px;
                                height: 5px;
                                border-radius: 50%;
                                margin-left: 5px;


                                &.normal {
                                    background-color: #1cbb8c;
                                }

                                &.abnormal {
                                    background-color: #ff3d60;
                                }
                            }

                        }


                    }

                    .video-action {
                        width: 20px;
                        height: 20px;
                        line-height: 20px;
                        text-align: center;
                        background-color: rgba(0, 0, 0, .6);
                        position: absolute;
                        right: 5px;
                        bottom: 5px;
                        color: #fff;

                        i {
                            cursor: pointer;
                        }
                    }

                    .image {
                        width: 100%;
                        height: 100%;

                        img {
                            width: 100%;
                            height: 100%;
                        }

                    }

                    .screen-abnormal {
                        height: 100%;
                        background-color: rgba(0, 0, 0, .2);
                        display: flex;
                        justify-content: center;
                        align-items: center;

                        ::v-deep .el-empty__image {
                            width: 140px;
                        }

                        ::v-deep .el-empty__description p {
                            color: #f4f5f7;
                            letter-spacing: 1px;
                        }
                    }
                }
            }

            .device-create-wrap {
                height: 290px;
                box-sizing: border-box;
                background-color: #f5f6fa;
                display: flex;
                justify-content: center;
                align-items: center;

                i {
                    font-size: 50px;
                    color: #5664d2;
                    cursor: pointer;
                }
            }
        }
    }

    .el-pagination {
        margin-top: 15px;
    }
}

.form-dialog .el-form-item {
    margin-bottom: 18px;
}

.dialog-wrap {
    padding: 0 10px;
    max-height: 53vh;
    display: flex;
    justify-content: space-between;

    .groupForm {
        width: 80%;
        margin: 0 auto;
    }

    .deviceForm {
        width: calc(100% - 558px);
        padding: 0 20px;
        overflow-y: auto;

        .form-title {
            font-size: 15px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .el-cascader {
            width: 100%;
        }

        .el-divider--horizontal {
            margin-left: -25px;
            width: calc(100% + 35px);
        }
    }

    .camera-wrap {
        width: 528px;
        height: 396px;
        margin-left: 20px;
        box-sizing: border-box;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .video-streaming-prompt {
        font-size: 14px;
        color: #999;
        margin-left: 120px;
        white-space: nowrap;
    }

    .detail-info {
        width: calc(100% - 528px);
        padding: 12px 20px;
        box-sizing: border-box;

        .result-item {
            display: flex;
            margin-bottom: 20px;

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


::v-deep .el-dialog.is-fullscreen {
    background: transparent;


    .el-dialog__close {
        color: #fff;
        font-size: 24px;
    }
}

.fullScreen-container {
    text-align: center;

    img {
        width: auto;
        height: 85vh;
    }
}
</style>