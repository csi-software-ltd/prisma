class CashdepartmentSearch {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

///////////////cash///////////////////
  Integer id
  Integer department_id
  Long summa
  Long saldo
  Integer valuta_id
  Long pers_id
  Integer type
  Integer cashclass
  Integer is_dep
  Long receipt
  Date inputdate
  Date operationdate
  Integer expensetype_id
  Long admin_id
  String comment
///////////////admin//////////////////
  String admin_name
///////////////pers///////////////////
  String pers_name
  String pers_fio
  Long depusersaldo
  Integer cashaccess
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectCash(iId,iDepartmentId,lPersId,iType,iClass,dDate,dOpdatestart,dOpdateend,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as pers_name, pers.shortname as pers_fio, 0 as depusersaldo, 0 as cashaccess"
    hsSql.from='cashdepartment join user u1 on (cashdepartment.admin_id=u1.id) left join user u2 on (cashdepartment.pers_id=u2.id) left join pers on (u2.pers_id=pers.id)'
    hsSql.where="1=1"+
                ((iId>0)?' AND cashdepartment.id =:id':'')+
                ((iDepartmentId>0)?' AND cashdepartment.department_id =:department_id':'')+
                ((lPersId>0)?' AND cashdepartment.pers_id =:pers_id AND is_dep=0':' AND is_dep=1')+
                ((iType>-100)?' AND cashdepartment.type =:type':'')+
                ((iClass>-100)?' AND cashdepartment.cashclass =:cashclass':'')+
                ((dDate)?' AND cashdepartment.inputdate<=:inputdate':'')+
                ((dOpdatestart)?' AND cashdepartment.operationdate>=:opdatestart':'')+
                ((dOpdateend)?' AND cashdepartment.operationdate<=:opdateend':'')
    hsSql.order="cashdepartment.id desc"

    if(iId>0)
      hsLong['id']=iId
    if(iDepartmentId>0)
      hsLong['department_id']=iDepartmentId
    if(lPersId>0)
      hsLong['pers_id']=lPersId
    if(iType>-100)
      hsLong['type']=iType
    if(iClass>-100)
      hsLong['cashclass']=iClass
    if(dDate)
      hsString['inputdate']=String.format('%tF',dDate)
    if(dOpdatestart)
      hsString['opdatestart']=String.format('%tF',dOpdatestart)
    if(dOpdateend)
      hsString['opdateend']=String.format('%tF',dOpdateend)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'cashdepartment.id',true,CashdepartmentSearch.class)
  }

  def csiFindDepsaldoByDate(dDate){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, (select sum(IF(cashdepartment.saldo>0,cashdepartment.saldo,0)) from user left join cashdepartment on (cashdepartment.id=(select max(id) from cashdepartment where user.department_id=cashdepartment.department_id and cashdepartment.is_dep=0 and user.id=cashdepartment.pers_id and date(cashdepartment.inputdate)<=:repdate)) where user.department_id=department.id and cashaccess not in (1,3) and usergroup_id!=1) as depusersaldo, '' as pers_name, '' as pers_fio, '' as admin_name, 0 as cashaccess"
    hsSql.from='department left join cashdepartment on (cashdepartment.id=(select max(id) from cashdepartment where department.id=cashdepartment.department_id and cashdepartment.is_dep=1 and date(cashdepartment.inputdate)<=:repdate))'
    hsSql.where="1=1"
    hsSql.order="department.name asc"

    hsString['repdate']=String.format('%tF',dDate)

    searchService.fetchData(hsSql,null,null,hsString,null,CashdepartmentSearch.class)
  }

  def csiFindUsersaldoByCashaccess(liCashacess,dDate){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]
    def hsList=[:]

    hsSql.select="*, pers.shortname as pers_name, '' as pers_fio, '' as admin_name, cashdepartment.saldo as depusersaldo"
    hsSql.from='user left join pers on (user.pers_id=pers.id) left join cashdepartment on (cashdepartment.id=(select max(id) from cashdepartment where cashdepartment.is_dep=0 and user.id=cashdepartment.pers_id and date(cashdepartment.inputdate)<=:repdate))'
    hsSql.where="user.usergroup_id!=1"+
                (liCashacess?' AND user.cashaccess in (:cashaccess)':'')
    hsSql.order="cashaccess desc, pers.shortname asc"

    if(liCashacess)
      hsList['cashaccess']=liCashacess
    hsString['repdate']=String.format('%tF',dDate)

    searchService.fetchData(hsSql,null,null,hsString,hsList,CashdepartmentSearch.class)
  }
}