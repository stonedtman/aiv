<template>
    <div id="header">
        <div class="header-left">
            <div class="logo-img"><img src="@/assets/images/logo.png" alt=""></div>
            <div class="system-name">思通数科 AI视频卫士</div>
        </div>
        <div class="header-right">
            <div class="open-community" @click="gotoGitee">
                <i class="iconfont icon-gitee-fill-round"></i>
                <span>开源社区</span>
            </div>
            <div class="novice-start" @click="viewGuideDoc">
                <i class="iconfont icon-xingzhuang"></i>
                <span>新手入门</span>
            </div>
            <div class="settings" @click="settings">
                <i class="el-icon-setting"></i>
                <span>系统设置</span>
            </div>
            <div class="logout" @click="logout">
                <i class="el-icon-switch-button"></i>
                <span>退出</span>
            </div>
        </div>
    </div>
</template>

<script>
import { logout } from "@/api/login"
export default {
    data() {
        return {
            username: ""
        }
    },
    created() {
        this.username = localStorage.getItem("username");
    },
    methods: {
        logout() {
            logout().then(res => {
                if (res.code == 200) {
                    this.$message({
                        message: '退出登录',
                        type: 'success'
                    });
                    localStorage.removeItem("Authorization");
                    localStorage.removeItem("permissions");
                    this.$router.replace({ path: "/login" });
                }
            })

        },
        gotoGitee() {
            window.open("https://gitee.com/stonedtx/stonedtaiv");
        },
        viewGuideDoc() {
            window.open("https://docs.qq.com/doc/DQXZXcFFFU3lrcENY");
        },
        settings() {
            this.$router.push("/systemSettings");
        }
    }
}
</script>

<style lang="scss" scoped>
#header {
    height: 70px;
    background: #252b3b;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    // box-shadow: 0 0.05rem 0.01rem rgba(75, 75, 90, 0.075);
    position: fixed;
    width: 100%;
    z-index: 1999;
    color: #fff;

    .header-left {
        display: flex;
        // align-items: center;

        .logo-img {
            height: 30px;

            img {
                height: 100%;
            }
        }

        .system-name {
            font-size: 20px;
            // font-weight: 600;
            line-height: 31px;
        }
    }

    .header-right {
        display: flex;
        align-items: center;

        div {
            display: flex;
            align-items: center;
            cursor: pointer;

            &:not(:last-child) {
                margin-right: 20px;
            }

            &:hover {
                color: #5664d2;
            }

            i {
                font-size: 20px;
                margin-right: 5px;
            }

            span {
                font-size: 14px;
            }
        }
    }
}
</style>