class CashSearch {
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
  Integer agentagr_id
  Integer agent_id
  Integer type
  Integer cashclass
  Long receipt
  Date inputdate
  Date operationdate
  Date platperiod
  Long admin_id
  String comment
  Integer project_id
  Integer expensetype_id
  Long tagadmin_id
///////////////admin//////////////////
  String admin_name
///////////////pers///////////////////
  String pers_name
  String pers_fio
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectCash(iId,iDepartmentId,iType,iClass,dDate,dOpdatestart,dOpdateend,iExprazdelId,iExppodrazdelId,iExptypeId,sComment,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as pers_name, pers.shortname as pers_fio"
    hsSql.from='cash join user u1 on (cash.admin_id=u1.id) left join user u2 on (cash.pers_id=u2.id) left join pers on (u2.pers_id=pers.id) left join expensetype on (cash.expensetype_id=expensetype.id)'
    hsSql.where="1=1"+
                ((iId>0)?' AND cash.id =:id':'')+
                ((iDepartmentId>0)?' AND cash.department_id =:department_id':'')+
                ((iType>-100)?' AND cash.type =:type':'')+
                ((iClass>0)?' AND cash.cashclass =:cashclass':'')+
                ((dDate)?' AND cash.inputdate<=:inputdate':'')+
                ((dOpdatestart)?' AND cash.operationdate>=:opdatestart':'')+
                ((dOpdateend)?' AND cash.operationdate<=:opdateend':'')+
                ((iExprazdelId>0)?' AND expensetype.expensetype1_id =:exprazdel_id':'')+
                ((iExppodrazdelId>0)?' AND expensetype.expensetype2_id =:exppodrazdel_id':'')+
                ((iExptypeId>0)?' AND cash.expensetype_id =:expensetype_id':'')+
                ((sComment!='')?' AND cash.comment like concat("%",:comment,"%")':'')
    hsSql.order="cash.id desc"

    if(iId>0)
      hsLong['id']=iId
    if(iDepartmentId>0)
      hsLong['department_id']=iDepartmentId
    if(iType>-100)
      hsLong['type']=iType
    if(iClass>0)
      hsLong['cashclass']=iClass
    if(dDate)
      hsString['inputdate']=String.format('%tF',dDate)
    if(dOpdatestart)
      hsString['opdatestart']=String.format('%tF',dOpdatestart)
    if(dOpdateend)
      hsString['opdateend']=String.format('%tF',dOpdateend)
    if(iExprazdelId>0)
      hsLong['exprazdel_id']=iExprazdelId
    if(iExppodrazdelId>0)
      hsLong['exppodrazdel_id']=iExppodrazdelId
    if(iExptypeId>0)
      hsLong['expensetype_id']=iExptypeId
    if(sComment!='')
      hsString['comment']=sComment

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'cash.id',true,CashSearch.class)
  }

  def csiSelectDCReturn(dDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, '' as admin_name, '' as pers_name, '' as pers_fio"
    hsSql.from='cash'
    hsSql.where="cash.type=3 and cash.cashclass=8"+
                ((dDate)?' AND year(cash.platperiod) =:year AND month(cash.platperiod) =:month':'')
    hsSql.order="cash.operationdate desc"

    if(dDate){
      hsLong['month'] = dDate.getMonth()+1
      hsLong['year'] = dDate.getYear()+1900
    }

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'cash.id',true,CashSearch.class)
  }

}