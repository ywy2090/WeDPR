import http from '../utils/http'

// get link
const getJupterLink = (params) => http.post('/jupyter/query', params)

// jupyter open
const openJupter = (params) => http.get('/jupyter/open', params)

export default {
  getJupterLink,
  openJupter
}
