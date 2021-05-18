import request from '@/utils/request'

export function page(page, size) {
  return request({
    url: '/user/page',
    method: 'get',
    params: { page, size }
  })
}
