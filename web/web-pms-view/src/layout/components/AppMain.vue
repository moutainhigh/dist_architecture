<template>
  <section class="app-main">
    <keep-alive :include="cachedRoutes">
      <router-view :key="key"/>
    </keep-alive>
  </section>
</template>

<script>
  export default {
    name: 'AppMain',
    computed: {
      key() {
        return this.$route.path
      },
      cachedRoutes() {
        return this.$store.state.tagsView.visitedRoutes
          .filter(route => route.cachedComponentName)
          .map(route => route.cachedComponentName);
      }
    }
  }
</script>

<style scoped>
  .app-main {
    /*50 = navbar  */
    min-height: calc(100vh - 84px);
    width: 100%;
    position: relative;
    overflow: hidden;
  }

  .fixed-header + .app-main {
    padding-top: 50px;
  }
</style>

<style lang="scss">
  // fix css style bug in open el-dialog
  .el-popup-parent--hidden {
    .fixed-header {
      padding-right: 15px;
    }
  }
</style>
