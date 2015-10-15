class CashrequesthistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////cashrequesthist//////////////
  Integer id
  Integer cashrequest_id
  Date reqdate
  Integer modstatus
  Integer summa
  Float margin
  String comment
  Long admin_id
  Date inputdate
//////////////Admin///////////////////////
  String admin_name

  def csiFindHistory(iCashrequestId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='cashrequesthist left join user u1 on (u1.id=cashrequesthist.admin_id)'
    hsSql.where="1=1"+
                ((iCashrequestId>0)?' and cashrequesthist.cashrequest_id=:cashrequest_id':'')
    hsSql.order="cashrequesthist.inputdate desc"

    if(iCashrequestId>0)
      hsLong['cashrequest_id']=iCashrequestId

    searchService.fetchData(hsSql,hsLong,null,null,null,CashrequesthistSearch.class)
  }

}