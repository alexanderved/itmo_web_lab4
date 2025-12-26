<template>
    <button class="exit-button" @click="handleExit()">
        <img class="exit-icon"
             src="@/assets/logout.png"
             alt="Exit" />
    </button>
</template>

<script>
import { removeTokens } from '@/utils/auth';

export default {
    name: 'ExitButton',
    methods: {
        handleExit() {
            removeTokens();

            this.$router.push('/login');
        }
    }
}
</script>

<style scoped>
.exit-button {
    background-color: transparent;
    border: none;
    border-radius: 10px;
    margin: 5px;
    padding: 10px 10px 5px 10px;
}

.exit-button:hover {
    background-color: rgba(70, 70, 70, 0.7);
}

.exit-button:active {
    background-color: rgba(60, 60, 60, 0.7);
}

.exit-icon {
    background-color: transparent;
    width: 35px;
}
</style>