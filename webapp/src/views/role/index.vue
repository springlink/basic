<template>
  <div class="app-container">
    <el-card style="margin-bottom: 10px">
      <el-form size="mini" :inline="true" :model="list.query">
        <el-form-item label="角色名">
          <el-input v-model="list.query.name" placeholder="角色名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" :loading="list.loading" @click="onQuery()">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-plus" @click="onAdd()">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table
        v-loading="list.loading"
        :data="list.data"
        :default-sort="list.params"
        size="mini"
        border
        fit
        highlight-current-row
        @sort-change="onSortChange"
      >
        <el-table-column type="index" align="right" :index="1" />
        <el-table-column prop="name" label="角色名" sortable />
        <el-table-column prop="createdDate" label="创建时间" width="200" align="center" sortable />
        <el-table-column prop="disabled" label="已禁用" width="80" align="center">
          <template slot-scope="{row}">
            <el-popconfirm :title="`确定要${row.disabled ? '启用' : '禁用'}角色吗？`" @confirm="onToggleDisabled(row)">
              <el-switch slot="reference" :value="row.disabled" />
            </el-popconfirm>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
          <template slot-scope="{row}">
            <el-button size="mini" @click="onSetDetail(row)">编辑信息</el-button>
            <el-popconfirm title="确定要删除角色吗？" style="margin-left:10px" @confirm="onRemove(row)">
              <el-button slot="reference" size="mini" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <pagination :total="list.total" :page.sync="list.params.page" :limit.sync="list.params.limit" @pagination="loadList" />

    <el-dialog title="编辑角色" width="480px" :close-on-click-modal="false" :visible.sync="form.visible">
      <el-form ref="form" :disabled="form.processing" :rules="form.rules" :model="form.data" label-width="100px">
        <el-form-item label="角色名" prop="name">
          <el-input v-model="form.data.name" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="form.visible = false">取消</el-button>
        <el-button type="primary" :loading="form.processing" @click="onFormSave()">保存</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { Message } from 'element-ui'
import { add, setDetail, setDisabled, remove, page } from '@/api/role'
import Pagination from '@/components/Pagination'

export default {
  components: { Pagination },
  data() {
    return {
      list: {
        loading: true,
        data: [],
        total: 0,
        params: {
          page: 1,
          limit: 50,
          prop: 'createdDate',
          order: 'descending'
        },
        query: {
          name: null
        }
      },
      form: {
        visible: false,
        processing: false,
        data: {
          id: null,
          name: ''
        },
        rules: {
          name: [
            { required: true, message: '请输入角色名', trigger: 'blur' }
          ]
        }
      }
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.list.loading = true
      page(this.list.params, this.list.query).then(res => {
        this.list.data = res.elements
        this.list.total = res.total
        this.list.loading = false
      }).finally(() => {
        this.list.loading = false
      })
    },
    onSortChange({ prop, order }) {
      Object.assign(this.list.params, { sort: prop, order })
      this.loadList()
    },
    onQuery() {
      this.list.query.page = 1
      this.loadList()
    },
    onAdd() {
      this.form.visible = true
      this.$nextTick(() => {
        this.form.data.id = null
        this.$refs.form.resetFields()
        this.$refs.form.clearValidate()
      })
    },
    onRemove(row) {
      remove(row.id).then(res => {
        Message({ message: '删除成功', type: 'success', duration: 5 * 1000 })
        this.loadList()
      })
    },
    onToggleDisabled(row) {
      const disabled = !row.disabled
      setDisabled({ id: row.id, disabled }).then(res => {
        row.disabled = disabled
        Message({ message: `${disabled ? '禁用' : '启用'}成功`, type: 'success', duration: 5 * 1000 })
      })
    },
    onSetDetail(row) {
      this.form.visible = true
      this.$nextTick(() => {
        this.$refs.form.resetFields()
        this.$refs.form.clearValidate()
        Object.assign(this.form.data, row)
      })
    },
    onFormSave() {
      this.form.processing = true
      this.$refs.form.validate().then(pass => {
        if (pass) {
          return (this.form.data.id ? setDetail(this.form.data) : add(this.form.data)).then(res => {
            Message({
              message: '保存成功',
              type: 'success',
              duration: 5 * 1000
            })
            this.form.visible = false
            this.loadList()
          })
        }
      }).finally(() => {
        this.form.processing = false
      })
    }
  }
}
</script>
