<template>
    <div class="registration-page">
        <Header />

        <div class="registration-container">
            <div class="registration-header">
                <h1>Регистрация</h1>
            </div>

            <div class="registration-form">
                <div class="form-group">
                    <label for="username">Имя пользователя</label>
                    <input
                        type="text"
                        v-model="username"
                        placeholder="Введите имя"
                        class="form-input"
                    />
                </div>

                <div class="form-group">
                    <div class="password-label-container">
                        <label for="password">Пароль</label>
                    </div>
                    <div class="password-input-container">
                        <input
                            :type="showPassword ? 'text' : 'password'"
                            v-model="password"
                            placeholder="Введите пароль"
                            class="form-input password-input"
                        />
                        <button
                            type="button"
                            class="password-toggle"
                            @click="togglePasswordVisibility"
                        >
                            <svg
                                v-if="!showPassword"
                                xmlns="http://www.w3.org/2000/svg"
                                width="20"
                                height="20"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                            >
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            <svg
                                v-else
                                xmlns="http://www.w3.org/2000/svg"
                                width="20"
                                height="20"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                            >
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                <line x1="1" y1="1" x2="23" y2="23"></line>
                            </svg>
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <div class="password-label-container">
                        <label for="password">Пароль (повтор)</label>
                    </div>
                    <div class="password-input-container">
                        <input
                            :type="showPassword ? 'text' : 'password'"
                            v-model="passwordRepeat"
                            placeholder="Повторите пароль"
                            class="form-input password-input"
                        />
                        <button
                            type="button"
                            class="password-toggle"
                            @click="togglePasswordVisibility"
                        >
                            <svg
                                v-if="!showPassword"
                                xmlns="http://www.w3.org/2000/svg"
                                width="20"
                                height="20"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                            >
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            <svg
                                v-else
                                xmlns="http://www.w3.org/2000/svg"
                                width="20"
                                height="20"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                            >
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                <line x1="1" y1="1" x2="23" y2="23"></line>
                            </svg>
                        </button>
                    </div>
                </div>

                <div id="registration-error" class="registration-error-message"></div>

                <div class="form-buttons">
                    <button type="submit" class="action-button button-registration" @click="handleRegistration">
                        Зарегистрироваться
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Header from '@/components/Header.vue'
import { registration } from '@/utils/auth';

export default {
    name: 'Registration',
    components: {
        Header
    },
    data() {
        return {
            username: '',
            password: '',
            passwordRepeat: '',
            showPassword: false,
        }
    },
    methods: {
        togglePasswordVisibility() {
            this.showPassword = !this.showPassword
        },
        
        async handleRegistration() {
            console.log('registration attempt with:', {
                username: this.username,
                password: this.password,
                passwordRepeat: this.passwordRepeat,
            });

            if (this.password !== this.passwordRepeat) {
                const err = document.getElementById("registration-error");
                err.innerHTML = "Пароль и повторный пароль не совпадают"

                return;
            }

            try {
                await registration({
                    username: this.username,
                    password: this.password,
                });

                this.$router.push('/login');
            } catch (e) {
                const err = document.getElementById("registration-error");
                err.innerHTML = "Не удалось зарегистрировать нового пользователя"
            }
        },
    }
}
</script>

<style scoped>
.registration-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: rgb(40, 40, 40);
    gap: 60px;
}

.registration-container {
    background-color: rgb(60, 60, 60);
    border-radius: 16px;
    padding: 40px;
    width: 100%;
    max-width: 420px;
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.05);
}

.registration-header {
    text-align: center;
    margin-bottom: 32px;
}

.registration-header h1 {
    color: #ffffff;
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 8px;
    letter-spacing: -0.5px;
}

.registration-form {
    margin-bottom: 32px;
}

.form-group {
    margin-bottom: 24px;
}

.form-group label {
    display: block;
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
}

.password-label-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.form-input {
    width: 100%;
    padding: 14px 16px;
    background-color: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    color: #ffffff;
    font-size: 15px;
    box-sizing: border-box;
}

.form-input::placeholder {
    color: rgba(255, 255, 255, 0.4);
}

.form-input:focus {
    outline: none;
    border-color: #4d90fe;
    background-color: rgba(255, 255, 255, 0.07);
}

.password-input-container {
    position: relative;
    display: flex;
    align-items: center;
}

.password-input {
    padding-right: 50px;
}

.password-toggle {
    position: absolute;
    right: 12px;
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.5);
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.password-toggle:hover {
    color: rgba(255, 255, 255, 0.8);
    background-color: rgba(255, 255, 255, 0.05);
}

.form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    font-size: 14px;
}

.form-buttons {
    display: flex;
    gap: 12px;
    margin-top: 32px;
}

.action-button {
    flex: 1;
    padding: 14px 20px;
    border: none;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    justify-content: center;
    align-items: center;
}

.button-registration {
    background-color: #4d90fe;
    color: white;
}

.button-registration:hover {
    background-color: #3a7de8;
}

.button-registration:active {
    background-color: #3773d3ff;
}

.button-register {
    background-color: transparent;
    color: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.button-register:hover {
    background-color: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.3);
}

.button-register:active {
    background-color: rgba(255, 255, 255, 0.01);
}

.registration-error-message {
    color: red;
    margin-bottom: 25px;
}

@media (max-width: 1054px) {
    .registration-container {
        padding: 20px;
    }
}

</style>