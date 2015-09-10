namespace java com.sankuai.meituan.waimai.service.order.query.service

include 'wmorder.thrift'
include 'fb303.thrift'

service UserThriftService extends fb303.FacebookService {
    string findUsernameById(1: wmorder.long id);
}
