class LizinghistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////lizinghist////////////////////
  Integer id
  Integer lizing_id
  String anumber
  Date adate
  Date enddate
  String comment
  BigDecimal summa
  BigDecimal initialfee
  Integer rate
  Long responsible
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
  String responsible_name

  def csiFindLizingHistory(iLizingId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as responsible_name"
    hsSql.from='lizinghist left join user u1 on (u1.id=lizinghist.admin_id) left join user u2 on (u2.id=lizinghist.responsible)'
    hsSql.where="1=1"+
                ((iLizingId>0)?' and lizinghist.lizing_id=:lizing_id':'')
    hsSql.order="lizinghist.inputdate desc"

    if(iLizingId>0)
      hsLong['lizing_id']=iLizingId

    searchService.fetchData(hsSql,hsLong,null,null,null,LizinghistSearch.class)
  }

}