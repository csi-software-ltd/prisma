class UserActivitySearch {
  def searchService
  static mapping = { version false }
/////User////////////
  Long id
/////Pers////////////
  String shortname
/////Department//////
  String depname
/////Others//////////
  Integer suc_count
  Integer unsuc_count

/////////////////////////////////////////////////////////////////////////////////////////////////////
  def csiFindUserActivity(_request,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="user.id, pers.shortname, department.name as depname, sum(if(userlog.success=1,1,0)) as suc_count, sum(if(userlog.success=0,1,0)) as unsuc_count"
    hsSql.from='user join pers on(user.pers_id=pers.id) join department on (user.department_id=department.id) left join userlog on(userlog.user_id=user.id)'
    hsSql.where="user.usergroup_id!=1"+
                (_request.pers_id>0?' and pers.id=:pers_id':'')+
                (_request.department_id>0?' and department.id=:dep_id':'')+
                (_request.is_active?' and userlog.logtime<=:reportend'+(_request.reportstart?' and userlog.logtime>=:reportstart':''):' and ifnull(userlog.logtime,:reportend)<=:reportend'+(_request.reportstart?' and ifnull(userlog.logtime,:reportstart)>=:reportstart':''))
    hsSql.group="user.id"
    hsSql.order="department.name asc, pers.shortname asc"

    hsString['reportend'] = String.format('%tF',_request.reportend+1)
    if(_request.pers_id>0)
      hsLong['pers_id'] = _request.pers_id
    if(_request.department_id>0)
      hsLong['dep_id'] = _request.department_id
    if(_request.reportstart)
      hsString['reportstart'] = String.format('%tF',_request.reportstart)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'*',true,UserActivitySearch.class)
  }
}