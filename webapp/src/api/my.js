import request from '@/utils/request'

export function profile() {
  return request({ url: '/my/profile', method: 'get' })
}

export function setProfile(data) {
  return request({ url: '/my/setProfile', method: 'post', data })
}

export function setPassword(data) {
  return request({ url: '/my/setPassword', method: 'post', data })
}
