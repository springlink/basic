<template>
  <div class="app-container">
    <el-tabs type="border-card" value="profile">

      <el-tab-pane label="账号信息" name="profile">
        <el-form ref="profileForm" :disabled="profileForm.processing" :rules="profileForm.rules" :model="profileForm.data" label-width="100px" style="width:480px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="profileForm.data.username" readonly />
          </el-form-item>
          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="profileForm.data.phoneNumber" />
          </el-form-item>
          <el-form-item label="Email" prop="email">
            <el-input v-model="profileForm.data.email" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="profileForm.processing" @click="onProfileFormSave()">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="密码修改" name="password">
        <el-form ref="passForm" :disabled="passForm.processing" :rules="passForm.rules" :model="passForm.data" label-width="100px" style="width:480px">
          <el-form-item label="账号密码" prop="password">
            <el-input v-model="passForm.data.password" type="password" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passForm.data.newPassword" type="password" />
          </el-form-item>
          <el-form-item label="确认新密码" prop="newPasswordConfirm">
            <el-input v-model="passForm.data.newPasswordConfirm" type="password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="passForm.processing" @click="onPassFormSave()">确认修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { profile, setProfile, setPassword } from '@/api/my'

export default {
  name: 'Profile',
  data() {
    return {
      profileForm: {
        processing: false,
        rules: {},
        data: {
          username: '',
          phoneNumber: '',
          email: ''
        }
      },
      passForm: {
        processing: false,
        rules: {
          password: [
            { required: true, message: '请输入账号密码', trigger: 'blur' }
          ],
          newPassword: [
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { min: 6, max: 20, message: '密码长度在6到20个字符内', trigger: 'blur' }
          ],
          newPasswordConfirm: [
            { required: true, message: '请确认新密码', trigger: 'blur' },
            { validator: (rule, value, callback) => {
              if (value !== this.passForm.data.newPassword) {
                callback(new Error('两次输入密码不一致'))
              } else {
                callback()
              }
            }, trigger: 'blur' }
          ]
        },
        data: {
          password: '',
          newPassword: '',
          newPasswordConfirm: ''
        }
      }
    }
  },
  mounted() {
    profile().then(res => {
      Object.assign(this.profileForm.data, res)
    }).finally(() => {
      this.profileForm.processing = false
    })
  },
  methods: {
    onProfileFormSave() {
      this.$refs.profileForm.validate().then(pass => {
        if (pass) {
          this.profileForm.processing = true
          setProfile(this.profileForm.data).then(res => {
            Message({ message: '保存成功', type: 'success', duration: 5 * 1000 })
          }).finally(() => {
            this.profileForm.processing = false
          })
        }
      })
    },
    onPassFormSave() {
      this.$refs.passForm.validate().then(pass => {
        if (pass) {
          this.passForm.processing = true
          setPassword(this.passForm.data).then(res => {
            Message({ message: '修改成功', type: 'success', duration: 5 * 1000 })
            this.$refs.passForm.resetFields()
          }).finally(() => {
            this.passForm.processing = false
          })
        }
      })
    }
  }
}
</script>
