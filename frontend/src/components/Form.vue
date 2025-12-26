<template>
    <div class="form-container">
        <div class="input-element">
            <h3 class="input-label">X:</h3>
            <CheckboxGrid @update:selectedValues="setXValues" />
        </div>

        <div id="x-input-error" class="input-error-message"></div>

        <div class="input-element">
            <h3 class="input-label">Y:</h3>
            <input class="y-input" v-model="yValue" placeholder="-3...5" />
        </div>

        <div id="y-input-error" class="input-error-message"></div>

        <div class="input-element">
            <h3 class="input-label">R:</h3>
            <CheckboxGrid @update:selectedValues="setRValues" />
        </div>

        <div id="r-input-error" class="input-error-message"></div>

        <button class="submit-button" @click="submit">Отправить</button>
    </div>
</template>

<script>
import { drawPoints, setRValues } from '@/utils/canvas';
import CheckboxGrid from './CheckboxGrid.vue'
import { storePoints } from '@/utils/points';
import { logOut } from '@/utils/auth';

export default {
    name: 'Form',
    emits: ['update:rValues', 'addPointsToTable'],
    components: {
        CheckboxGrid
    },
    data() {
        return {
            xValues: new Set(),
            yValue: "",
            rValues: new Set()
        }
    },
    methods: {
        setXValues(values) {
            this.xValues = new Set([...values]);
        },
        setRValues(values) {
            this.rValues = new Set([...values]);
        },
        async submit() {
            const y = parseFloat(this.yValue.replace(",", "."));

            const errX = document.getElementById("x-input-error");
            if (this.xValues.size === 0) {
                errX.innerText = "Не установлено значение X";
            } else {
                errX.innerText = "";
            }

            const errR = document.getElementById("r-input-error");
            if (this.rValues.size === 0) {
                errR.innerText = "Не установлено значение R";
            } else {
                errR.innerText = "";
            }

            const errY = document.getElementById("y-input-error");
            if ((isNaN(y) || y < -3 || y > 5 || y.toString() !== this.yValue.replace(",", "."))) {
                errY.innerText = "Неверный формат Y";

                return;
            } else {
                errY.innerText = "";

                let upoints = [];
                for (const x of [...this.xValues]) {
                    upoints.push({
                        x,
                        y: y,
                        rs: [...this.rValues]
                    });
                }

                try {
                    const points = await storePoints(upoints);
                    this.$emit('addPointsToTable', points);
                    drawPoints(points);
                } catch (e) {
                    logOut();
                }
            }
        }
    },
    watch: {
        rValues(newValue) {
            this.$emit('update:rValues', newValue);
        }
    }
}
</script>

<style>
.form-container {
    width: 300px;
    color: white;
    background-color: rgb(45, 45, 45);
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
    border: 2px solid rgb(100, 100, 100);
    box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.1);

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.input-element {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
}

.input-label {
    margin: 0px 10px 0px 0px;
}

.y-input {
    width: 260px;
    height: 30px;
    background-color: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 3px;
    color: white;
    padding: 3px 10px;
}

.submit-button {
    border: 2px solid rgb(21, 53, 200);
    border-radius: 7px;
    padding: 10px;
    margin-top: 5px;
    font-weight: bold;
}

.input-error-message {
    color: red;
    margin-bottom: 25px;
}

</style>