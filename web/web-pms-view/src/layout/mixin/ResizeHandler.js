import store from '@/store'

const {body} = document
const WIDTH = 992 // refer to Bootstrap's responsive design

export default {
  watch: {
    $route(route) {
      if (this.device === 'mobile' && !this.sidebarCollapse) {
        store.dispatch('toggleSideBar', true)
      }
      this.$_adaptMainPage()
    },
  },
  data() {
    return {$_adaptMainPageTimer: undefined};
  },
  beforeMount() {
    window.addEventListener('resize', this.$_resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.$_resizeHandler)
  },
  mounted() {
    const isMobile = this.$_isMobile()
    if (isMobile) {
      store.dispatch('toggleDevice', 'mobile')
      store.dispatch('toggleSideBar', true)
    }
    this.$_adaptMainPage()
  },
  methods: {
    // use $_ for mixins properties
    // https://vuejs.org/v2/style-guide/index.html#Private-property-names-essential
    $_isMobile() {
      const rect = body.getBoundingClientRect()
      return rect.width - 1 < WIDTH
    },
    $_resizeHandler() {
      if (!document.hidden) {
        const isMobile = this.$_isMobile()
        store.dispatch('toggleDevice', isMobile ? 'mobile' : 'desktop')

        if (isMobile) {
          store.dispatch('toggleSideBar', true)
        }
      }
      this.$_adaptMainPage();
    },
    $_adaptMainPage() {
      //加缓冲，防止快速重复调用
      if (this.$_adaptMainPageTimer) {
        clearTimeout(this.$_adaptMainPageTimer)
      }
      this.$_adaptMainPageTimer = setTimeout(() => {
        this.$nextTick(() => {
          let appMain = document.getElementsByClassName("app-main")[0]
          let mainPageHeader = document.getElementsByClassName("main-page-header")[0]
          let mainPageFooter = document.getElementsByClassName("main-page-footer")[0]
          let mainPageContent = document.getElementsByClassName("main-page-content")[0]
          if (!appMain || !mainPageContent) {
            return;
          }
          let mainPageHeight = appMain.offsetHeight
          let headerHeight = mainPageHeader ? mainPageHeader.offsetHeight : 0;
          let footerHeight = mainPageFooter ? mainPageFooter.offsetHeight : 0;
          mainPageContent.style.height = (mainPageHeight - headerHeight - footerHeight - 50) + 'px';
        })
      }, 100)
    }
  }
}
