class FinlizinghistSearch {
  def searchService
  static mapping = { version false }

//////////////finlizinghist///////////////
  Integer id
  Integer finlizing_id
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer modstatus
  String description
  String comment
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name

  def csiFindFLizingHistory(iFLizing){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='finlizinghist left join user u1 on (u1.id=finlizinghist.admin_id)'
    hsSql.where="1=1"+
                ((iFLizing>0)?' and finlizinghist.finlizing_id=:finlizing_id':'')
    hsSql.order="finlizinghist.inputdate desc"

    if(iFLizing>0)
      hsLong['finlizing_id'] = iFLizing

    searchService.fetchData(hsSql,hsLong,null,null,null,FinlizinghistSearch.class)
  }

}