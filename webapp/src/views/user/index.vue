<template>
  <div class="app-container">
    <el-card style="margin-bottom: 10px">
      <el-form size="mini" :inline="true" :model="list.query">
        <el-form-item label="用户名">
          <el-input v-model="list.query.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="list.query.createdDate"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
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
        <el-table-column prop="username" label="用户名" sortable />
        <el-table-column prop="phoneNumber" label="手机号码" width="200" align="center" />
        <el-table-column prop="email" label="Email" width="200" align="center" />
        <el-table-column prop="createdDate" label="创建时间" width="200" align="center" sortable />
        <el-table-column prop="locked" label="账号锁定" width="80" align="center">
          <template slot-scope="{row}">
            <el-popconfirm :title="`确定要${row.locked ? '解锁' : '锁定'}账号吗？`" @confirm="onToggleLocked(row)">
              <el-switch slot="reference" :value="row.locked" />
            </el-popconfirm>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
          <template slot-scope="{row}">
            <el-button size="mini" @click="onSetProfile(row)">修改资料</el-button>
            <el-button v-if="row.status!='published'" size="mini" @click="onSetPassword(row)">修改密码</el-button>
            <el-popconfirm title="确定要删除用户吗？" style="margin-left:10px" @confirm="onRemove(row)">
              <el-button slot="reference" size="mini" type="danger">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <pagination :total="list.total" :page.sync="list.params.page" :limit.sync="list.params.limit" @pagination="loadList" />

    <el-dialog title="编辑用户" width="480px" :close-on-click-modal="false" :visible.sync="form.visible">
      <el-form ref="form" :disabled="form.processing" :rules="form.rules" :model="form.data" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.data.username" :readonly="!!form.data.id" />
        </el-form-item>
        <el-form-item v-if="!form.data.id" label="密码" prop="password">
          <el-input v-model="form.data.password" type="password" />
        </el-form-item>
        <el-form-item v-if="!form.data.id" label="确认密码" prop="passwordConfirm">
          <el-input v-model="form.data.passwordConfirm" type="password" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="form.data.phoneNumber" />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.data.email" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="form.visible = false">取消</el-button>
        <el-button type="primary" :loading="form.processing" @click="onFormSave()">保存</el-button>
      </div>
    </el-dialog>

    <el-dialog title="修改密码" width="480px" :close-on-click-modal="false" :visible.sync="passForm.visible">
      <el-form ref="passForm" :disabled="passForm.processing" :rules="passForm.rules" :model="passForm.data" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="passForm.data.password" type="password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="passwordConfirm">
          <el-input v-model="passForm.data.passwordConfirm" type="password" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="passForm.visible = false">取消</el-button>
        <el-button type="primary" :loading="passForm.processing" @click="onPassFormSave()">确认</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { Message } from 'element-ui'
import { add, setProfile, setPassword, setLocked, remove, page } from '@/api/user'
import Pagination from '@/components/Pagination'

export default {
  components: { Pagination },
  data() {
    const usernameValidators = [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 4, max: 30, message: '用户名长度在4到30个字符内', trigger: 'blur' }
    ]
    const passwordValidators = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在6到20个字符内', trigger: 'blur' }
    ]
    const newPasswordConfirmValidators = (getPassword) => [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { validator: (rule, value, callback) => {
        if (value !== getPassword()) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
    ]

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
          username: null,
          createdDate: []
        }
      },
      form: {
        visible: false,
        processing: false,
        data: {
          id: null,
          username: '',
          password: '',
          passwordConfirm: '',
          phoneNumber: '',
          email: ''
        },
        rules: {
          username: usernameValidators,
          password: passwordValidators,
          passwordConfirm: newPasswordConfirmValidators(() => this.form.data.password)
        }
      },
      passForm: {
        visible: false,
        processing: false,
        data: {
          id: null,
          password: '',
          passwordConfirm: ''
        },
        rules: {
          password: passwordValidators,
          passwordConfirm: newPasswordConfirmValidators(() => this.passForm.data.password)
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
    onToggleLocked(row) {
      const locked = !row.locked
      setLocked({ id: row.id, locked }).then(res => {
        row.locked = locked
        Message({ message: `${locked ? '锁定' : '解锁'}成功`, type: 'success', duration: 5 * 1000 })
      })
    },
    onSetProfile(row) {
      this.form.visible = true
      this.$nextTick(() => {
        this.$refs.form.resetFields()
        this.$refs.form.clearValidate()
        Object.assign(this.form.data, row)
      })
    },
    onSetPassword(row) {
      this.passForm.visible = true
      this.$nextTick(() => {
        this.passForm.data.id = row.id
        this.$refs.passForm.resetFields()
        this.$refs.passForm.clearValidate()
      })
    },
    onFormSave() {
      this.form.processing = true
      this.$refs.form.validate().then(pass => {
        if (pass) {
          return (this.form.data.id ? setProfile(this.form.data) : add(this.form.data)).then(res => {
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
    },
    onPassFormSave() {
      this.passForm.processing = true
      this.$refs.passForm.validate().then(pass => {
        if (pass) {
          return setPassword(this.passForm.data).then(res => {
            Message({
              message: '修改成功',
              type: 'success',
              duration: 5 * 1000
            })
            this.passForm.visible = false
          })
        }
      }).finally(() => {
        this.passForm.processing = false
      })
    }
  }
}
</script>
