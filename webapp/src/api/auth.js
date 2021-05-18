import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/auth/login',
    method: 'post',
    data
  })
}

export function info() {
  return request({
    url: '/user/auth/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/user/auth/logout',
    method: 'post'
  })
}
