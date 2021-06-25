import request from '@/utils/request'
import { makePageParams } from '@/utils'

export function add(data) {
  return request({ url: '/user/add', method: 'post', data })
}

export function setProfile(data) {
  return request({ url: '/user/setProfile', method: 'post', data })
}

export function setPassword(data) {
  return request({ url: '/user/setPassword', method: 'post', data })
}

export function setLocked(data) {
  return request({ url: '/user/setLocked', method: 'post', data })
}

export function remove(id) {
  return request({ url: `/user/delete/${id}`, method: 'delete' })
}

export function page(params, query) {
  return request({ url: '/user/page', method: 'post', params: makePageParams(params), data: query })
}
