import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import child from './modules/child'
import dietary from './modules/dietary'
import nutritionist from './modules/nutritionist'
import nutritionAnalysis from './modules/nutrition-analysis'
import recipe from './modules/recipe'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    child,
    dietary,
    nutritionist,
    nutritionAnalysis,
    recipe
  },
  strict: process.env.NODE_ENV !== 'production'
})

