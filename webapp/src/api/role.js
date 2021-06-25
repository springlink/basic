import request from '@/utils/request'
import { makePageParams } from '@/utils'

export function add(data) {
  return request({ url: '/role/add', method: 'post', data })
}

export function setDetail(data) {
  return request({ url: '/role/setDetail', method: 'post', data })
}

export function setDisabled(data) {
  return request({ url: '/role/setDisabled', method: 'post', data })
}

export function remove(id) {
  return request({ url: `/role/delete/${id}`, method: 'delete' })
}

export function page(params, query) {
  return request({ url: '/role/page', method: 'post', params: makePageParams(params), data: query })
}
