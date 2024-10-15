<template>
    <div id="page">
        <top-bar></top-bar>
        <nav-bar></nav-bar>
        <div id="main">
            <router-view></router-view>
            <div class="copyright">{{ copyright }}</div>
        </div>
    </div>
</template>

<script>
import topBar from "@/components/topbar.vue";
import navBar from "@/components/navbar.vue";
export default {
    components: { topBar, navBar },
    data() {
        return {
            copyright: ""
        }
    },
    created() {
        var date = new Date();
        var year = date.getFullYear();
        this.copyright = "@2014-" + year + " 思通数科(南京)信息技术有限公司 苏ICP备17066984号-1";
    },
    methods: {

    }
}
</script>
<style lang="scss" scoped>
#main {
    margin-top: 130px;
}

.copyright {
    text-align: center;
    color: #6c757d;
    font-size: 15px;
    padding: 20px 15px;
}
</style>