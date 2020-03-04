<template>
  <div class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper">
      <router-link
        v-for="route in visitedRoutes"
        ref="linkTags"
        :key="route.path"
        :class="isActive(route)?'active':''"
        :to="{ path: route.path, query: route.query, fullPath: route.fullPath }"
        tag="span"
        class="tags-view-item"
        @click.middle.native="closeTag(route)"
        @contextmenu.prevent.native="openContextMenu(route,$event)">
        {{route.title}}
        <span v-if="!route.meta.affix" class="el-icon-close" @click.prevent.stop="closeTag(route)"/>
      </router-link>
    </scroll-pane>

    <ul v-show="contextMenuRoute" :style="{left:left+'px',top:top+'px'}" class="contextmenu">
      <li @click="refreshSelectedTag(contextMenuRoute)">刷新</li>
      <li v-if="!(contextMenuRoute&&contextMenuRoute.meta&&contextMenuRoute.meta.affix)" @click="closeTag(contextMenuRoute)">关闭</li>
      <li @click="closeOthersTags">关闭其它</li>
      <li @click="closeAllTags">关闭所有</li>
    </ul>
  </div>
</template>

<script>
  import ScrollPane from './ScrollPane'

  export default {
    components: {ScrollPane},
    data() {
      return {
        visible: false,
        top: 0,
        left: 0,
        affixTags: [],
        contextMenuRoute: undefined,
      }
    },
    computed: {
      visitedRoutes() {
        return this.$store.state.tagsView.visitedRoutes
      }
    },
    watch: {
      $route() {
        this.addTag()
        this.moveToCurrentTag()
      },
      contextMenuRoute(value) {
        if (value) {
          document.body.addEventListener('click', this.closeContextMenu)
        } else {
          document.body.removeEventListener('click', this.closeContextMenu)
        }
      }
    },
    mounted() {
      this.initAffixTags()
      this.addTag()
    },
    methods: {
      isActive(route) {
        return route.path === this.$route.path;
      },
      addTag() {
        this.$store.dispatch('addVisitedRoute', this.$route)
      },
      moveToCurrentTag() {
        this.$nextTick(() => {
          let tag = this.$refs.linkTags.find(linkTag => linkTag.to.path === this.$route.path)
          tag && this.$refs.scrollPane.moveToTarget(tag)
        })
      },
      closeTag(route) {
        this.$store.dispatch('delVisitedRoute', route)
          .then(visitedRoutes => {
            this.isActive(route) && this.toLastRoute(visitedRoutes)
            this.contextMenuRoute = undefined;
          })
      },
      toLastRoute(routes) {
        if (routes.length > 0) {
          this.$router.push(routes.slice(-1)[0])
        } else {
          this.$router.push('/')
        }
      },
      initAffixTags() {
        this.$store.state.user.userData.routes
          .filter(route => route.children)
          .reduce((results, route) => {
            results.push(...route.children.filter(child => child.meta && child.meta.affix))
            return results;
          }, [])
          .forEach(route => {
            this.$store.dispatch('addVisitedRoute', route)
          })
      },

      closeOthersTags() {
        this.$router.push(this.contextMenuRoute)
        this.$store.dispatch('delOthersVisitedRoutes', this.contextMenuRoute).then(() => {
          this.moveToCurrentTag()
          this.contextMenuRoute = undefined;
        })
      },

      closeAllTags() {
        this.$store.dispatch('delAllVisitedRoutes').then(visitedRoutes => {
          if (visitedRoutes.some(route => route.path === this.$route.path)) {
            return
          }
          this.toLastRoute(visitedRoutes)
          this.contextMenuRoute = undefined;
        })
      },

      //todo
      refreshSelectedTag(route) {
        this.$store.dispatch('delVisitedRouteCache', route).then(() => {
          const {path} = route
          this.$nextTick(() => this.$router.replace({path: '/redirect' + path}))
          this.contextMenuRoute = undefined;
        })
      },

      openContextMenu(tag, e) {
        const menuMinWidth = 105
        const offsetLeft = this.$el.getBoundingClientRect().left // container margin left
        const offsetWidth = this.$el.offsetWidth // container width
        const maxLeft = offsetWidth - menuMinWidth // left boundary
        const left = e.clientX - offsetLeft + 15 // 15: margin right

        if (left > maxLeft) {
          this.left = maxLeft
        } else {
          this.left = left
        }

        this.top = e.clientY
        this.visible = true
        this.contextMenuRoute = tag;
      }
      ,
      closeContextMenu() {
        this.contextMenuRoute = undefined;
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .tags-view-container {
    height: 34px;
    width: 100%;
    background: #fff;
    border-bottom: 1px solid #d8dce5;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .12), 0 0 3px 0 rgba(0, 0, 0, .04);

    .tags-view-wrapper {
      .tags-view-item {
        display: inline-block;
        position: relative;
        cursor: pointer;
        height: 26px;
        line-height: 26px;
        border: 1px solid #d8dce5;
        color: #495060;
        background: #fff;
        padding: 0 8px;
        font-size: 12px;
        margin-left: 5px;
        margin-top: 4px;

        &:first-of-type {
          margin-left: 15px;
        }

        &:last-of-type {
          margin-right: 15px;
        }

        &.active {
          background-color: #42b983;
          color: #fff;
          border-color: #42b983;

          &::before {
            content: '';
            background: #fff;
            display: inline-block;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            position: relative;
            margin-right: 2px;
          }
        }
      }
    }

    .contextmenu {
      margin: 0;
      background: #fff;
      z-index: 100;
      position: absolute;
      list-style-type: none;
      padding: 5px 0;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 400;
      color: #333;
      box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);

      li {
        margin: 0;
        padding: 7px 16px;
        cursor: pointer;

        &:hover {
          background: #eee;
        }
      }
    }
  }
</style>

<style rel="stylesheet/scss" lang="scss">
  //reset element css of el-icon-close
  .tags-view-wrapper {
    .tags-view-item {
      .el-icon-close {
        width: 16px;
        height: 16px;
        vertical-align: 2px;
        border-radius: 50%;
        text-align: center;
        transition: all .3s cubic-bezier(.645, .045, .355, 1);
        transform-origin: 100% 50%;

        &:before {
          transform: scale(.6);
          display: inline-block;
          vertical-align: -3px;
        }

        &:hover {
          background-color: #b4bccc;
          color: #fff;
        }
      }
    }
  }
</style>
