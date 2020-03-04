<template>
  <div>
    <div class="logo-container">
      <img src="@/assets/logo.png" alt="" class="logo">
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper" style="height: calc(100% - 90px);"><!-- 需要减去上面logo的高度 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical">
        <sidebar-item v-for="menu in userData&&userData.menus" :key="menu.path" :item="menu" :base-path="menu.path"/>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
  import {mapGetters} from 'vuex'
  import SidebarItem from './SidebarItem'
  import variables from '@/styles/variables.scss'

  export default {
    components: {SidebarItem},
    computed: {
      ...mapGetters([
        'sidebarCollapse',
        'userData'
      ]),
      activeMenu() {
        const route = this.$route
        const {path} = route
        return path
      },
      variables() {
        return variables
      },
    }
  }
</script>

<style lang="scss" scoped>
  .logo-container {
    width: 100%;

    .logo {
      width: 100%;
      padding: 20px 20px 10px;
      box-sizing: border-box;
    }
  }
</style>
