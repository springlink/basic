<template>
  <div class="app-container">
    <el-table
      v-loading="loading"
      :data="list"
      element-loading-text="正在加载..."
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="序号" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="用户名">
        <template slot-scope="scope">
          {{ scope.row.username }}
        </template>
      </el-table-column>
      <el-table-column label="手机号码" width="200" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.phoneNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Email" width="200" align="center">
        <template slot-scope="scope">
          {{ scope.row.email }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="200" align="center">
        <template slot-scope="scope">
          {{ scope.row.createdDate }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { page } from '@/api/user'

export default {
  data() {
    return {
      list: null,
      loading: true
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      page(1, 50).then(res => {
        this.list = res.elements
        this.loading = false
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
