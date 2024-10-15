<template>
    <div id="nav">
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
            <el-menu-item index="1">
                <template slot="title">
                    <i class="el-icon-s-data"></i>
                    <span>数据看板</span>
                </template>
            </el-menu-item>
            <el-menu-item index="2">
                <template slot="title">
                    <i class="el-icon-map-location"></i>
                    <span>监测任务</span>
                </template>
            </el-menu-item>
            <el-menu-item index="3">
                <template slot="title">
                    <i class="el-icon-bell"></i>
                    <span>事件告警</span>
                </template>
            </el-menu-item>
            <el-menu-item index="4">
                <template slot="title">
                    <i class="el-icon-coin"></i>
                    <span>视频接入</span>
                </template>
            </el-menu-item>
            <el-menu-item index="5">
                <template slot="title">
                    <i class="el-icon-menu"></i>
                    <span>算法管理</span>
                </template>
            </el-menu-item>
            <!-- <el-menu-item index="6">
                <template slot="title">
                    <i class="el-icon-aim"></i>
                    <span>视频检索</span>
                </template>
            </el-menu-item> -->
            <el-menu-item index="7">
                <template slot="title">
                    <i class="el-icon-cpu"></i>
                    <span>模型训练</span>
                </template>
            </el-menu-item>
            <el-menu-item index="8">
                <template slot="title">
                    <i class="el-icon-monitor"></i>
                    <span>监控大屏</span>
                </template>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<script>
import baseURL from "@/utils/request";
export default {
    data() {
        return {
            activeIndex: "1",
        }
    },
    watch: {
        '$route.path': function (newPath) {
            this.keepActive();
        },
    },
    created() {
        this.keepActive();
        window.addEventListener("popstate", this.keepActive, false);
    },
    mounted() {

    },
    methods: {
        keepActive() {
            let path = this.$route.path;
            if (path.indexOf("/billboards") > -1) {
                this.activeIndex = "1";
            } else if (path.indexOf("/taskMonitor") > -1) {
                this.activeIndex = "2";
            } else if (path.indexOf("/eventWarning") > -1) {
                this.activeIndex = "3";
            } else if (path.indexOf("/videoAccess") > -1) {
                this.activeIndex = "4";
            } else if (path.indexOf("/algorithmManage") > -1) {
                this.activeIndex = "5";
            }
            // else if (path.indexOf("/videoRetrieve") > -1) {
            //     this.activeIndex = "6";
            // } 
            else if (path.indexOf("/contact") > -1) {
                this.activeIndex = "7";
            } else {
                this.activeIndex = "";
            }
        },
        handleSelect(key, keyPath) {
            // console.log(key, keyPath);
            if (key == "1") {
                this.$router.push("/billboards");
            } else if (key == "2") {
                this.$router.push("/taskMonitor");
            } else if (key == "3") {
                this.$router.push("/eventWarning");
            } else if (key == "4") {
                this.$router.push("/videoAccess");
            } else if (key == "5") {
                this.$router.push("/algorithmManage");
            }
            // else if (key == "6") {
            //     this.$router.push("/videoRetrieve");
            // } 
            else if (key == "7") {
                this.$router.push("/contact");
            } else if (key == "8") {
                this.activeIndex = "";
                this.$nextTick(() => {
                    this.keepActive();
                })
                window.open(baseURL.split("/api")[0] + "/screen/dataView");
            }
        },

    }
}
</script>

<style lang="scss" scoped>
#nav {
    position: fixed;
    left: 0;
    top: 70px;
    width: 100%;
    height: 60px;
    background: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, .08);
    z-index: 1999;

    .el-menu {
        // display: inline-block;
        // margin: 0 auto;
        padding: 0 20px;

        &.el-menu--horizontal {
            border: none;
        }

        .el-menu-item {
            height: 55px;
            line-height: 55px;
            padding: 0 5px 0 2px;

            &:not(:last-child) {
                margin-right: 40px;
            }

            &.is-active {
                color: #5664d2 !important;
                border-color: #5664d2;
                font-weight: 600;
            }

            &:hover i {
                color: #5664d2;
            }

            &:hover span {
                color: #5664d2;
            }

        }

        .el-submenu {
            margin-right: 40px;

            &.is-active {

                ::v-deep .el-submenu__title {
                    color: #5664d2 !important;
                    border-color: #5664d2;
                    font-weight: 600;

                    i {
                        color: #5664d2 !important;
                        font-weight: 600;
                    }
                }

            }

            ::v-deep .el-submenu__title {
                height: 55px;
                line-height: 55px;
                padding: 0 5px 0 2px;
            }
        }


    }
}

.el-menu--horizontal .el-menu .el-menu-item {
    padding-left: 20px !important;
}
</style>