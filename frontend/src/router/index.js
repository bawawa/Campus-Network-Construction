import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/children',
      name: 'Children',
      component: () => import('@/views/children/ChildrenList.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/children/add',
      name: 'AddChild',
      component: () => import('@/views/children/AddChild.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/children/:id',
      name: 'ChildDetail',
      component: () => import('@/views/children/ChildDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/children/:id/asd-profile',
      name: 'ASDProfile',
      component: () => import('@/views/children/ASDProfile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/children/:id/dietary-records',
      name: 'DietaryRecords',
      component: () => import('@/views/dietary/DietaryRecords.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/nutritionist',
      name: 'Nutritionist',
      component: () => import('@/views/nutritionist/NutritionistPanel.vue'),
      meta: { requiresAuth: true, role: 'NUTRITIONIST' }
    },
    {
      path: '/profile',
      name: 'UserProfile',
      component: () => import('@/views/user/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/',
      component: () => import('@/views/layout/MainLayout.vue'),
      children: [
        {
          path: '/reports',
          name: 'Reports',
          component: () => import('@/views/reports/NutritionReports.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/recipes',
          name: 'Recipes',
          component: () => import('@/views/recipes/RecipeRecommendations.vue'),
          meta: { requiresAuth: true }
        }
      ]
    },
    {
      path: '*',
      redirect: '/dashboard'
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters['user/isAuthenticated']
  const userRole = store.getters['user/userRole']

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.role && to.meta.role !== userRole) {
      // 角色权限检查
      next({ path: '/dashboard' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

