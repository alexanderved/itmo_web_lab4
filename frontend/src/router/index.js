import { createRouter, createWebHistory } from 'vue-router'
import Main from '../pages/Main.vue'
import Login from '../pages/Login.vue'
import Registration from '../pages/Registration.vue'
import { isAuthenticated } from '@/utils/auth'


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', component: Main, meta: { requiresAuth: true } },
        { path: '/login', component: Login },
        { path: '/registration', component: Registration },
    ],
})

router.beforeEach((to, _from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isAuthenticated()) {
            next({ path: '/login' });
        } else {
            next(); 
        }
    } else {
        next();
    }
});

export default router
