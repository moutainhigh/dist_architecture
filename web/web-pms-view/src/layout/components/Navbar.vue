<template>
  <div class="navbar">
    <hamburger :is-active="!sidebarCollapse" class="hamburger-container" @toggleClick="toggleSideBar"/>

    <breadcrumb class="breadcrumb-container"/>

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="el-dropdown-link" style="cursor: pointer;"> {{userData.name}}<i class="el-icon-arrow-down el-icon--right"/></div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/">
            <el-dropdown-item> Home</el-dropdown-item>
          </router-link>
          <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
            <el-dropdown-item>Docs</el-dropdown-item>
          </a>
          <el-dropdown-item divided>
            <span style="display:block;" @click="logout">Log Out</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
  import {mapGetters} from 'vuex'
  import Breadcrumb from '@/components/Breadcrumb'
  import Hamburger from '@/components/Hamburger'
  import {removeToken} from "@/utils/loginToken"

  export default {
    components: {
      Breadcrumb,
      Hamburger
    },
    computed: {
      ...mapGetters([
        'sidebarCollapse',
        'userData'
      ])
    },
    methods: {
      toggleSideBar() {
        this.$store.dispatch('toggleSideBar', !this.sidebarCollapse)
      },
      async logout() {
        await this.$store.dispatch('setUserData', undefined)
          .then(() => removeToken())
        this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

    .hamburger-container {
      line-height: 46px;
      height: 100%;
      float: left;
      cursor: pointer;
      transition: background .3s;
      -webkit-tap-highlight-color: transparent;

      &:hover {
        background: rgba(0, 0, 0, .025)
      }
    }

    .breadcrumb-container {
      float: left;
    }

    .right-menu {
      float: right;
      height: 100%;
      line-height: 50px;
      margin-right: 20px;
    }
  }
</style>
