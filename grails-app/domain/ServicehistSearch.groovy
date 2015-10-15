class ServicehistSearch {
  def searchService
  static mapping = { version false }

//////////////servicehist/////////////////
  Integer id
  Integer service_id
  Date inputdate
  Date enddate
  Long summa
  String zbank_id
  String ebank_id
  String description
  String comment
  Long responsible
  Long admin_id
//////////////Admin///////////////////////
  String admin_name

  def csiFindServiceHistory(iServiceId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='servicehist left join user u1 on (u1.id=servicehist.admin_id)'
    hsSql.where="1=1"+
                ((iServiceId>0)?' and servicehist.service_id=:service_id':'')
    hsSql.order="servicehist.inputdate desc"

    if(iServiceId>0)
      hsLong['service_id'] = iServiceId

    searchService.fetchData(hsSql,hsLong,null,null,null,ServicehistSearch.class)
  }

}