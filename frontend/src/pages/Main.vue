<template>
    <div class="top-container">
        <Header>
            <template v-slot:right-content>
                <ExitButton />
            </template>
        </Header>

        <div class="container">
            <Form @update:rValues="setRValues" @addPointsToTable="addPointsToTable" />

            <canvas id="graph" width="512" height="512" hidden />
            <canvas id="webgl-graph" width="512" height="512" />
        </div>

        <div class="container">
            <Table :data="tableData" @clear-points="onClearPoints" />
        </div>
    </div>
</template>

<script>
import Header from '@/components/Header.vue'
import ExitButton from '@/components/ExitButton.vue'
import Form from '@/components/Form.vue'
import Table from '@/components/Table.vue'

import { setRValues, initCanvas, drawCanvas, drawPoints, setAddPointsFunc } from '@/utils/canvas.js'
import { getPoints } from '@/utils/points.js'
import { waitUpdate } from '@/utils/update'
import { logOut } from '@/utils/auth'
import { drawWebGLCanvas, initWebGLCanvas } from '@/utils/canvas3d';

export default {
    name: 'Main',
    components: {
        Header,
        ExitButton,
        Form,
        Table
    },
    data() {
        return {
            rValues: new Set(),
            tableData: []
        }
    },
    methods: {
        setRValues(values) {
            this.rValues = new Set([...values]);
        },
        async onClearPoints() {
            try {
                this.tableData = await getPoints();
                drawCanvas();
                drawPoints(this.tableData);
                drawWebGLCanvas();
            } catch (e) {
                logOut();
            }
        },
        addPointsToTable(points) {
            this.tableData = points.concat(this.tableData);
        }
    },
    mounted() {
        initCanvas();
        drawCanvas();
        initWebGLCanvas();

        getPoints()
            .then((p) => {
                this.tableData = p;
                drawCanvas();
                drawPoints(this.tableData);
                drawWebGLCanvas();
            })
            .catch(logOut);

        waitUpdate(async () => {
            try {
                this.tableData = await getPoints();
                drawCanvas();
                drawPoints(this.tableData);
                drawWebGLCanvas();
            } catch (e) {
                logOut();
            }
        });

        setAddPointsFunc((p) => this.addPointsToTable(p));
    },
    watch: {
        rValues(newValue) {
            setRValues(newValue);
            drawCanvas();
            drawPoints(this.tableData);
            drawWebGLCanvas();
        }
    }
}
</script>

<style>
body {
    background-color: rgb(40, 40, 40);
}

.top-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 20px;
}

.container {
    width: 60%;
    background-color: rgb(50, 50, 50);
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
    display: flex;
    justify-content: space-evenly;
    align-items: center;
}

#graph {
    width: 512px;
    height: 512px;
}

.disabled-graph {
    opacity: 0.25;
    cursor: not-allowed;
}

@media (max-width: 1054px) {
    .container {
        width: 90%;
        border-radius: 6px;
        padding: 10px;
    }
}

@media (max-width: 872px) {
    .container {
        width: 90%;
        border-radius: 6px;
        padding: 10px;
        display: flex;
        flex-direction: column;
        gap: 20px;
    }
}

</style>