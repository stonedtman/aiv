<template>
    <div class="container" style="padding-top: 12px;">
        <div class="main-wrapper card" style="padding-top: 10px;">
            <div class="main-content">
                <el-tabs>
                    <el-tab-pane label="密码修改">
                        <div class="tab-content">
                            <div class="form" v-loading="loading">
                                <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="90px"
                                    class="demo-ruleForm">
                                    <el-form-item label="旧的密码" prop="oldPass">
                                        <el-input type="password" v-model="ruleForm.oldPass" autocomplete="off"
                                            placeholder="请输入旧的密码" size="small"></el-input>
                                    </el-form-item>
                                    <el-form-item label="新的密码" prop="pass">
                                        <el-input type="password" v-model="ruleForm.pass" autocomplete="off"
                                            placeholder="请输入新的的密码" size="small"></el-input>
                                    </el-form-item>
                                    <el-form-item label="确认密码" prop="checkPass">
                                        <el-input type="password" v-model="ruleForm.checkPass" autocomplete="off"
                                            placeholder="请再次输入新的密码" size="small"></el-input>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button @click="resetForm" size="small">重置</el-button>
                                        <el-button type="primary" @click="submitForm" size="small">提交</el-button>
                                    </el-form-item>
                                </el-form>
                            </div>
                        </div>

                    </el-tab-pane>
                    <!-- <el-tab-pane label="接口调用"></el-tab-pane>
                    <el-tab-pane label="推送设置"></el-tab-pane> -->
                </el-tabs>
            </div>

        </div>
    </div>
</template>

<script>
import { changePassword } from "@/api/login";
export default {
    components: {},
    props: {},
    data() {
        return {
            loading: false,
            ruleForm: {
                oldPass: "",
                pass: "",
                checkPass: ""
            },
            rules: {
                oldPass: [
                    { required: true, message: '请输入旧的密码', trigger: 'blur' },
                ],
                pass: [
                    { required: true, message: '请输入新的密码', trigger: 'blur' },
                    { min: 6, message: '密码不能少于六位数', trigger: 'blur' },
                    { validator: this.validatePass, trigger: 'blur' }
                ],
                checkPass: [
                    { required: true, message: '请再次输入新的密码', trigger: 'blur' },
                    { validator: this.validatePass2, trigger: 'blur' }
                ],
            }
        };
    },
    created() {

    },
    mounted() {

    },
    watch: {},
    computed: {},
    methods: {
        submitForm() {
            this.$refs["ruleForm"].validate((valid) => {
                if (valid) {
                    this.loading = true;
                    var form = { oldPassword: this.ruleForm.oldPass, newPassword: this.ruleForm.pass };
                    changePassword(form).then(res => {
                        this.loading = false;
                        if (res.code == 200) {
                            this.$message({
                                message: '密码修改成功，请重新登录',
                                type: 'success'
                            });
                            setTimeout(() => {
                                localStorage.removeItem("Authorization");
                                localStorage.removeItem("permissions");
                                this.$router.replace({ path: "/login" });
                            }, 2000);
                        }
                    }).catch(() => {
                        this.loading = false;
                    })
                }
            });
        },
        resetForm() {
            this.$refs["ruleForm"].resetFields();
        },
        validatePass: (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入新的密码'));
            } else {
                if (this.ruleForm.checkPass !== '') {
                    this.$refs.ruleForm.validateField('checkPass');
                }
                callback();
            }
        },
        validatePass2: (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入新的密码'));
            } else if (value !== this.ruleForm.pass) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        }
    }
};
</script>
<style lang="scss" scoped>
.main-content {

    ::v-deep .el-tabs__item {
        color: #999;

        &.is-active {
            color: #5664d2;
            font-weight: 600;
        }
    }

    ::v-deep .el-tabs__nav-wrap::after {
        background-color: transparent;
    }

    .tab-content {


        .form {

            .el-input {
                width: 300px;
            }

            .el-button+.el-button {
                margin-left: 20px;
            }
        }
    }
}
</style>