class CashreportSearch {
  def searchService

  static mapping = {
    version false
  }

///////////report////////////////
  Integer id
  Long summa
  Date repdate
  Date confirmdate
  Integer expensetype_id
  Integer project_id
  String description
  Integer modstatus
  Integer department_id
  Long file_id
  String comment_dep
  String comment
  Long executor
  Long initiator
  Integer type
///////////user//////////////////
  String executor_name

/////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectReports(iId,lExecutor,sExecutor,iDepId,iStatus,dDate,iExprazdelId,iExppodrazdelId,iExptypeId,iProjectId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, pers.shortname as executor_name"
    hsSql.from='cashreport left join expensetype on (cashreport.expensetype_id=expensetype.id), user, pers'
    hsSql.where="cashreport.executor=user.id and pers.id=user.pers_id"+
                ((iId>0)?' AND cashreport.id =:report_id':'')+
                ((lExecutor>0)?' AND cashreport.executor =:executor':'')+
                ((sExecutor!='')?' and pers.shortname like concat("%",:executor_name,"%")':'')+
                ((iDepId>0)?' AND cashreport.department_id =:department_id':'')+
                ((iStatus>-100)?' AND cashreport.modstatus =:status':'')+
                ((dDate)?' AND cashreport.repdate<=:repdate':'')+
                ((iExprazdelId>0)?' AND expensetype.expensetype1_id =:exprazdel_id':'')+
                ((iExppodrazdelId>0)?' AND expensetype.expensetype2_id =:exppodrazdel_id':'')+
                ((iExptypeId>0)?' AND cashreport.expensetype_id =:expensetype_id':'')+
                ((iProjectId>0)?' AND cashreport.project_id =:project_id':'')
    hsSql.order="cashreport.id desc"

    if(iId>0)
      hsLong['report_id']=iId
    if(lExecutor>0)
      hsLong['executor']=lExecutor
    if(sExecutor!='')
      hsString['executor_name']=sExecutor
    if(iDepId>0)
      hsLong['department_id']=iDepId
    if(iStatus>-100)
      hsLong['status']=iStatus
    if(dDate)
      hsString['repdate']=String.format('%tF',dDate)
    if(iExprazdelId>0)
      hsLong['exprazdel_id']=iExprazdelId
    if(iExppodrazdelId>0)
      hsLong['exppodrazdel_id']=iExppodrazdelId
    if(iExptypeId>0)
      hsLong['expensetype_id']=iExptypeId
    if(iProjectId>0)
      hsLong['project_id']=iProjectId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'cashreport.id',true,CashreportSearch.class)
  }

  def csiSelectReportsSummary(iDepId,dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, pers.shortname as executor_name"
    hsSql.from='cashreport left join expensetype on (cashreport.expensetype_id=expensetype.id), user, pers'
    hsSql.where="cashreport.executor=user.id and pers.id=user.pers_id"+
                ((iDepId>0)?' AND cashreport.department_id =:department_id':'')+
                ((dDateStart)?' AND cashreport.repdate>=:repdate_start':'')+
                ((dDateEnd)?' AND cashreport.repdate<=:repdate_end':'')
    hsSql.order="cashreport.id desc"

    if(iDepId>0)
      hsLong['department_id'] = iDepId
    if(dDateStart)
      hsString['repdate_start'] = String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['repdate_end'] = String.format('%tF',dDateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,CashreportSearch.class)
  }
}