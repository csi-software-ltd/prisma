class CompokvedSearch {
  def searchService
  static mapping = {
    version false
  }

/////Compokved/////////
  Integer id
  String okved_id
  Integer company_id
  Integer is_main
  Integer modstatus
  Date moddate
  String comments
/////Okved////////////
  Integer ok_modstatus
  String okvedname
  String okvedrazdel

  def csiFindCompokved(iCompanyId,iStatus,iModstatus=-1){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, okved.name as okvedname, okved.razdel as okvedrazdel, okved.modstatus as ok_modstatus"
    hsSql.from='compokved,okved'
    hsSql.where="okved.id=compokved.okved_id"+
                ((iCompanyId>0)?' and compokved.company_id=:company_id':'')+
                ((iStatus>-100)?' and compokved.modstatus=:modstatus':'')+
                ((iModstatus>-1)?' and okved.modstatus=:iModstatus':'')
    hsSql.order="compokved.is_main desc, compokved.id asc"

    if(iCompanyId>0)
      hsLong['company_id'] = iCompanyId
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus
    if(iModstatus>-1)
      hsLong['iModstatus'] = iModstatus

    searchService.fetchData(hsSql,hsLong,null,null,null,CompokvedSearch.class)
  }

  def csiFindCompokvedByOkvedId(iCompanyId,sOkved){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, okved.name as okvedname, okved.razdel as okvedrazdel, okved.modstatus as ok_modstatus"
    hsSql.from='compokved,okved'
    hsSql.where="okved.id=compokved.okved_id"+
                ((iCompanyId>0)?' and compokved.company_id=:company_id':'')+
                (sOkved?' and compokved.okved_id=:okved_id':'')
    hsSql.order="compokved.is_main desc, compokved.id asc"

    if(iCompanyId>0)
      hsLong['company_id'] = iCompanyId
    if(sOkved)
      hsString['okved_id'] = sOkved

    searchService.fetchData(hsSql,hsLong,null,hsString,null,CompokvedSearch.class)
  }

}