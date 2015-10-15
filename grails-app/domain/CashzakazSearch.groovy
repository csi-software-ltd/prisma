class CashzakazSearch {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

///////////////cash///////////////////
  Integer id
  Long initiator
  Long summa
  String purpose
  Integer valuta_id
  Date inputdate
  Integer department_id
  Integer modstatus
  Date moddate
  String comment
  Integer cashrequest_id
  Date todate
///////////////users//////////////////
  String initiator_name
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectZakaz(iId,lUserId,iDepartmentId,sUsername,iStatus,iArchive,iCashrequestId,dDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, u1.name as initiator_name"
    hsSql.from='cashzakaz join user u1 on (cashzakaz.initiator=u1.id)'
    hsSql.where="1=1"+
                ((iId>0)?' AND cashzakaz.id =:zakaz_id':'')+
                ((lUserId>0)?' AND cashzakaz.initiator =:user_id':'')+
                ((iDepartmentId>0)?' AND cashzakaz.department_id =:department_id':'')+
                ((sUsername!='')?' AND u1.name like concat("%",:user_name,"%")':'')+
                ((iStatus>-100)?' AND cashzakaz.modstatus =:status':'')+
                ((iArchive)?' AND cashzakaz.modstatus=6':' AND cashzakaz.modstatus>0 AND cashzakaz.modstatus<6')+
                ((iCashrequestId>0)?' AND cashzakaz.cashrequest_id =:cashrequest_id':'')+
                ((iCashrequestId==-1)?' AND cashzakaz.cashrequest_id = 0':'')+
                ((dDate)?' AND cashzakaz.todate=:todate':'')
    hsSql.order="cashzakaz.id desc"

    if(iId>0)
      hsLong['zakaz_id']=iId
    if(lUserId>0)
      hsLong['user_id']=lUserId
    if(iDepartmentId>0)
      hsLong['department_id']=iDepartmentId
    if(sUsername!='')
      hsString['user_name']=sUsername
    if(iStatus>-100)
      hsLong['status']=iStatus
    if(iCashrequestId>0)
      hsLong['cashrequest_id']=iCashrequestId
    if(dDate)
      hsString['todate']=String.format('%tF',dDate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'cashzakaz.id',true,CashzakazSearch.class)
  }

  def csiFindApprovedNonCopletedZakaz(iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, u1.name as initiator_name"
    hsSql.from='cashzakaz join user u1 on (cashzakaz.initiator=u1.id)'
    hsSql.where="cashzakaz.modstatus < 4 and cashzakaz.is_managerapprove=1"
    hsSql.order="cashzakaz.id desc"

    searchService.fetchDataByPages(hsSql,null,null,null,null,
      null,null,iMax,iOffset,'cashzakaz.id',true,CashzakazSearch.class)
  }

}