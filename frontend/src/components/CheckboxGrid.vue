<template>
    <div class="checkbox-grid-container">
        <div class="checkbox-grid">
            <label :for="`${idPrefix}-checkbox-${value}`"
                   class="checkbox-item"
                   v-for="value in values"
                   :key="value"
                   :class="{ 'selected': selectedValues.has(value) }"
            >
                <input
                    type="checkbox"
                    :id="`${idPrefix}-checkbox-${value}`"
                    :value="value"
                    :checked="selectedValues.has(value)"
                    class="checkbox-input"
                    @change="handleCheckboxChange(value)"
                />

                <span class="checkbox-text">{{ value }}</span>
            </label>
        </div>
    </div>
</template>

<script>
import { useId } from 'vue'

export default {
    name: 'CheckboxGrid',
    emits: ['update:selectedValues'],
    setup() {
        const idPrefix = useId();

        return {
            idPrefix
        };
    },
    data() {
        const values = [];
        for (let i = -2; i <= 2; i += 0.5) {
            values.push(i);
        }

        return {
            selectedValues: new Set(),
            values,
        };
    },
    methods: {
        handleCheckboxChange(value) {
            if (this.selectedValues.has(value)) {
                this.selectedValues.delete(value);
            } else {
                this.selectedValues.add(value);
            }

            this.$emit('update:selectedValues', this.selectedValues);
        }
    }
};
</script>

<style scoped>
.checkbox-grid-container {
    color: white;
    width: 240px;
    margin: 0;
    padding: 20px;
    background-color: rgb(60, 60, 60);
    border-radius: 12px;
    border: 2px solid rgb(100, 100, 100);
    box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.1);
}

.checkbox-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
}

.checkbox-item {
    background-color: rgb(80, 80, 80);
    border-radius: 8px;
    padding: 6px;
    border: 2px solid rgb(100, 100, 100);

    display: flex;
    justify-content: space-between;

    user-select: none;
    cursor: pointer;

    box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.1);
}

.checkbox-item:hover {
    transform: translate(-1px, -1px);
}

.checkbox-item.selected {
    border-color: #3498db;
}

.checkbox-input {
    width: 17px;
    height: 17px;
    cursor: pointer;
}

.checkbox-text {
    font-size: 17px;
    font-weight: bold;
}

</style>