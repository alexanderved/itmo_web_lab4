<template>
    <header class="custom-header">
        <div class="left-section">
            <p class="header-text">{{ name }}</p>
            <p class="header-text">Группа {{ group }}</p>
            <p class="header-text">Вариант {{ variant }}</p>
        </div>

        <div class="right-section">
            <slot name="right-content">
                <div class="dummy-content">

                </div>
            </slot>
        </div>
    </header>
</template>

<script>
export default {
    name: 'Header',
    props: {
        name: {
            type: String,
            default: 'Ведехин Александр Вадимович'
        },
        group: {
            type: String,
            default: 'P3218'
        },
        variant: {
            type: Number,
            default: 18045
        }
    }
}
</script>

<style scoped>
.custom-header {
    height: 100px;
    width: 100%;

    color: white;
    font-style: italic;
    font-size: 14pt;
    background-color: rgb(50, 50, 50);

    display: flex;
    justify-content: space-between;
    align-items: center;

    padding: 10px;
}

.left-section {
    margin: 10px;
    display: flex;
    flex-direction: column;
}

.header-text {
    margin-top: 5px;
    margin-bottom: 5px;
}

@media (max-width: 872px) {
    .custom-header {
        height: 200px;
        width: 100%;

        font-size: 14pt;
        display: flex;
        flex-direction: column;
        justify-content: center;

        padding: 10px;
    }
}

</style>