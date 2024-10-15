<template>
    <div class="page">
        <div class="page-top">
            <div class="system-title">
                <div class="logo"><img src="@/assets/images/logo.png" alt=""></div>
                <div class="name">思通数科 AI视频卫士</div>
            </div>
            <div class="register-btn" v-if="loginType == 'account'">
                <el-button type="primary" size="medium">注册</el-button>
            </div>
        </div>
        <div class="page-main">
            <div class="left-box">
                <div class="center-box">
                    <div class="heading">洞察未来，智守平安——AI视界，守护每刻</div>
                    <div class="subheading">基于深度学习的AI大模型，实现多场景实时监控与预警，精准分析用户行为，自学习优化，云边协同，高效生成自动化报告，确保安全无遗漏</div>
                    <img class="center-images" src="@/assets/images/login_background.webp" alt="">
                </div>
            </div>
            <div class="right-box" :class="{ 'bg-gray': loginType == 'qrcode' }" v-loading="loading"
                element-loading-text="登录中">
                <div class="account-login" v-if="loginType == 'account'">
                    <div class="system-name">智感安防：AI大模型视频分析预警系统</div>
                    <div class="login-form">
                        <el-form :model="form" :rules="rules" ref="loginForm">
                            <el-form-item label="账号" prop="username">
                                <el-input v-model="form.username" placeholder="请输入账号" size="medium"></el-input>
                            </el-form-item>
                            <el-form-item label="密码" prop="password">
                                <el-input v-model="form.password" placeholder="请输入密码" show-password
                                    auto-complete="new-password" size="medium"></el-input>
                            </el-form-item>
                        </el-form>
                        <el-button type="primary" class="login-btn" size="medium" @click="login">登录</el-button>
                        <div class="bottom-btns flex-between">
                            <el-button type="text" class="forgot-password"
                                style="margin-left: 0 !important;">忘记密码？</el-button>
                            <div class="weixin-login pointer" @click="changeLoginType(1)">
                                <i class="iconfont icon-weixin1"></i>
                                <span>微信登录</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="qrcode-login" v-if="loginType == 'qrcode'">
                    <div class="login-title"><i class="iconfont icon-weixin1"></i>微信登录</div>
                    <div class="login-sub-title">请打开微信扫码并关注公众号</div>
                    <div class="qrcode-image-wrap">
                        <div class="qrcode-image">
                            <img src="@/assets/images/qrcode.png" alt="">
                        </div>
                    </div>
                    <div class="password-login pointer" @click="changeLoginType(2)">
                        <i class="el-icon-lock"></i>
                        <span>密码登录</span>
                    </div>
                </div>
                <div class="footer">{{ copyright }}</div>
            </div>
        </div>

    </div>
</template>

<script>
import { login } from "@/api/login.js";
export default {
    data() {
        return {
            copyright: "",
            loginType: "account",
            form: {
                username: "",
                password: ""
            },
            rules: {
                username: [
                    { required: true, message: '账号不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' }
                ],
            },
            loading: false
        }
    },
    created() {
        var date = new Date();
        var year = date.getFullYear();
        this.copyright = "@2014-" + year + " 思通数科(南京)信息技术有限公司 苏ICP备17066984号-1";
    },
    methods: {
        changeLoginType(type) {
            if (type == 1) {
                this.loginType = "qrcode";
            } else {
                this.loginType = "account";
            }
        },
        login() {
            this.$refs["loginForm"].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    var form = { userName: this.form.username, userPwd: this.form.password };
                    login(form).then(res => {
                        // console.log(res)
                        this.loading = false;
                        if (res.code == 200) {
                            this.$message({
                                message: '登录成功',
                                type: 'success'
                            });
                            localStorage.setItem("Authorization", res.data.token);
                            localStorage.setItem("permissions", res.data.permissions);
                            this.$router.replace({ path: "/billboards" });
                        }
                    }).catch(() => {
                        this.loading = false;
                        this.$message.error(res.message);
                    })
                }
            });
        }
    }
}
</script>

<style lang="scss" scoped>
.page {
    background-color: #fff;
    position: relative;

    .page-top {
        position: absolute;
        left: 0;
        top: 15px;
        width: 100%;
        height: 36px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 40px;
        box-sizing: border-box;
        z-index: 1;

        .system-title {
            display: flex;
            // align-items: center;

            .logo {
                height: 30px;

                img {
                    height: 100%;
                }
            }

            .name {
                font-size: 20px;
                font-weight: 600;
                line-height: 32px;
            }
        }
    }

    .page-main {
        display: flex;

        .left-box {
            width: 50%;
            height: 100vh;
            background-color: #f3f8ff;

            .center-box {
                width: 68vh;
                display: flex;
                flex-direction: column;
                justify-content: center;
                height: 100%;
                margin: 0 auto;

                .heading {
                    font-size: 24px;
                    margin-bottom: 24px;
                    text-align: left;
                    font-weight: 600;
                }

                .subheading {
                    font-size: 14px;
                    line-height: 24px;
                    margin-bottom: 12px;
                    text-align: left;
                    color: #656769;
                }

                .center-images {
                    width: 100%;
                }
            }
        }

        .right-box {
            width: 50%;
            height: 100vh;
            display: flex;
            justify-content: center;
            position: relative;

            .account-login {
                width: 65vh;
                display: flex;
                flex-direction: column;
                justify-content: center;
                height: 100%;
                margin: 0 auto;

                .system-name {
                    font-size: 20px;
                    font-weight: 500;
                    margin-bottom: 20px;
                    text-align: center;
                    letter-spacing: 1px;
                }

                .login-form {
                    padding: 0 5.5vw;

                    .el-form-item {
                        margin-bottom: 16px;
                    }

                    .weixin-login {
                        display: flex;
                        align-items: center;
                        color: #1ece29;
                        font-size: 14px;

                        i {
                            font-size: 20px;
                            margin-right: 3px;
                        }

                    }
                }


                .login-btn {
                    width: 100%;
                    margin-top: 20px;
                    margin-bottom: 5px;
                }

                .el-divider--horizontal {
                    margin: 12px 0;
                }

                .other-login {
                    padding: 0 5.5vw;

                    .change-login-text {
                        font-size: 14px;
                        color: #a7adc3;
                    }

                    .login-weixin {
                        width: 100%;
                        margin-top: 10px;
                    }
                }
            }

            .qrcode-login {
                width: 496px;
                height: 604px;
                border-radius: 12px;
                background: #fff;
                box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.16);
                margin: auto;
                text-align: center;

                .login-title {
                    margin-top: 80px;
                    font-size: 24px;
                    line-height: 20px;
                    letter-spacing: 0.88px;
                    color: #3f3f40;

                    i {
                        color: #1ece29;
                        margin-right: 5px;
                        font-size: 24px;
                    }
                }

                .login-sub-title {
                    margin-top: 20px;
                    font-size: 16px;
                    line-height: 17px;
                    letter-spacing: 0.75px;
                    color: #828282;
                }

                .qrcode-image-wrap {
                    position: relative;
                    width: 228px;
                    height: 228px;
                    margin: 40px auto 10px;
                    color: #fff;
                    // background: #f4f4f4;
                    // padding: 10px;
                    border: 1px solid #eee;
                    box-sizing: border-box;

                    .qrcode-image {

                        img {
                            width: 100%;
                            height: 100%;
                        }
                    }

                }

                .password-login {
                    width: 228px;
                    margin: 50px auto 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    color: #128bed;
                    font-size: 15px;

                    i {
                        margin-right: 3px;
                    }
                }

            }



            .footer {
                width: 100%;
                bottom: 0;
                padding: 19px 15px 20px;
                position: absolute;
                right: 0;
                color: #6c757d;
                font-size: 16px;
                text-align: center;
            }
        }

        .bg-gray {
            background-color: #f2f3f5;
        }
    }
}
</style>