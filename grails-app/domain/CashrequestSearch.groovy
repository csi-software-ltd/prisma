class CashrequestSearch {
  def searchService

  static mapping = {
    version false
  }

  Integer id
  Date inputdate
  Date reqdate
  Integer modstatus
  Integer summa
  String comment
  Long initiator
  Float margin

  def csiSelectRequest(iId,iStatus,dDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='cashrequest'
    hsSql.where="cashrequest.modstatus!=8"+
                ((iId>0)?' AND cashrequest.id =:request_id':'')+
                ((iStatus>-100)?' AND cashrequest.modstatus =:status':'')+
                ((dDate)?' AND cashrequest.reqdate=:reqdate':'')
    hsSql.order="cashrequest.reqdate desc, cashrequest.id desc"

    if(iId>0)
      hsLong['request_id']=iId
    if(iStatus>-100)
      hsLong['status']=iStatus
    if(dDate)
      hsString['reqdate']=String.format('%tF',dDate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'cashrequest.id',true,CashrequestSearch.class)
  }

}