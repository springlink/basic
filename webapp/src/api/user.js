import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/account/auth/login',
    method: 'post',
    data
  })
}

export function info() {
  return request({
    url: '/account/auth/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/account/auth/logout',
    method: 'post'
  })
}
