<template>
    <div class="table-container">
        <div class="table-controls">
            <button class="clear-button" @click="handleClear">
                Очистить
            </button>
            
            <div class="pagination-controls">
                <button 
                    class="pagination-button" 
                    @click="previousPage"
                    :disabled="currentPage === 1"
                >
                ‹
                </button>
                
                <span class="page-info">
                {{ currentPage }} / {{ totalPages }}
                </span>
                
                <button 
                    class="pagination-button" 
                    @click="nextPage"
                    :disabled="currentPage === totalPages"
                >
                ›
                </button>
            </div>
        </div>

        <table class="table">
            <thead>
                <tr>
                    <th v-for="column in columns" :key="column" class="table-header">
                        {{ column }}
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(row, index) in paginatedData" :key="index">
                    <td
                        v-for="column in columns"
                        :key="column"
                        class="table-cell"
                        :title="getCellValue(row, column)"
                    >
                        {{ getCellValue(row, column) }}
                    </td>
                </tr>
            </tbody>
        </table>
        
        <div v-if="data.length === 0" class="empty-message">
            Точки отсутсвуют
        </div>
    </div>
</template>

<script>
import { logOut } from '@/utils/auth';
import { clearPoints } from '@/utils/points';

export default {
    name: 'Table',
    props: {
        data: {
            type: Array,
            required: true,
            default: () => []
        },
        pageSize: {
            type: Number,
            default: 10
        },
    },
    emit: ['clear-points'],
    data() {
        return {
            columns: ['X', 'Y', 'R', 'Попадение'],
            currentPage: 1
        }
    },
    computed: {
        totalPages() {
            return Math.ceil(this.data.length / this.pageSize) || 1
        },
        paginatedData() {
            const startIndex = (this.currentPage - 1) * this.pageSize
            const endIndex = startIndex + this.pageSize
            return this.data.slice(startIndex, endIndex)
        }
    },
    methods: {
        getCellValue(row, column) {
            const columnMapping = {
                'X': 'x',
                'Y': 'y', 
                'R': 'rs',
                'Попадение': 'isHit'
            };
            
            const property = columnMapping[column];
            const value = row[property];
            
            if (property === 'isHit') {
                return value ? 'Успех' : 'Провал';
            } else if (property === "rs") {
                return value.join(", ");
            }
            
            return value;
        },
        
        previousPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
            }
        },
        
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
            }
        },
        
        async handleClear() {
            try {
                await clearPoints();
                this.$emit("clear-points");
            } catch (e) {
                logOut();
            }
        }
    }
}
</script>

<style scoped>
.table-container {
    width: 100%;
    max-width: 900px;
    margin: 0 auto;
    border: 1px solid rgb(80, 80, 80);
    border-radius: 8px;
    overflow: hidden;
}

.table-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background-color: rgb(120, 120, 120);
    border-bottom: 1px solid rgb(80, 80, 80);
}

.clear-button {
    padding: 6px 16px;
    background-color: rgb(255, 82, 82);
    color: black;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 2px 4px 8px rgba(0, 0, 0, 0.1);
}

.clear-button:hover {
    background-color: rgb(255, 62, 62);
    transform: translateY(-1px);
}

.clear-button:active {
    background-color: rgb(255, 42, 42);
    transform: translateY(0px);
}

.pagination-controls {
    display: flex;
    align-items: center;
    gap: 8px;
}

.pagination-button {
    padding: 4px 12px;
    background-color: rgb(220, 220, 220);
    border: 1px solid rgb(80, 80, 80);
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    min-width: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pagination-button:hover:not(:disabled) {
    background-color: rgb(180, 180, 180);
}

.pagination-button:disabled {
    opacity: 0.5;
    cursor: default;
}

.pagination-button:active:not(:disabled) {
    background-color: rgb(150, 150, 150);
}

.page-info {
    font-size: 14px;
    font-weight: 500;
    color: white;
    min-width: 60px;
    text-align: center;
    user-select: none;
}

.table {
    width: 100%;
    table-layout: fixed;
    border-collapse: collapse;
    border-spacing: 0;
    background-color: white;
}

.table-header {
    background-color: rgb(150, 150, 150);
    padding: 14px 12px;
    text-align: center;
    font-weight: 600;
    color: white;
    border-bottom: 2px solid rgb(80, 80, 80);
    border-right: 1px solid rgb(80, 80, 80);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-size: 14px;
}

.table-header:last-child {
    border-right: none;
}

.table-cell {
    padding: 12px;
    background-color: rgb(220, 220, 220);
    border-bottom: 1px solid rgb(80, 80, 80);
    border-right: 1px solid rgb(80, 80, 80);
    text-align: center;
    color: black;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-size: 14px;
}

.table-cell:last-child {
    border-right: none;
}

tr:hover .table-cell {
    background-color: rgb(210, 210, 210);
}

tbody tr:last-child .table-cell {
    border-bottom: none;
}

.empty-message {
    text-align: center;
    padding: 40px 20px;
    color: white;
    font-weight: bold;
    background-color: rgb(200, 200, 200);
    font-size: 18px;
}
</style>