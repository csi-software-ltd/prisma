class TradehistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////tradehist////////////////////
  Integer id
  Integer trade_id
  Integer tradecat_id
  String anumber
  Date adate
  Date enddate
  String comment
  Integer summa
  Integer paytype
  Long responsible
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
  String responsible_name

  def csiFindTradeHistory(iTradeId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as responsible_name"
    hsSql.from='tradehist left join user u1 on (u1.id=tradehist.admin_id) left join user u2 on (u2.id=tradehist.responsible)'
    hsSql.where="1=1"+
                ((iTradeId>0)?' and tradehist.trade_id=:trade_id':'')
    hsSql.order="tradehist.inputdate desc"

    if(iTradeId>0)
      hsLong['trade_id']=iTradeId

    searchService.fetchData(hsSql,hsLong,null,null,null,TradehistSearch.class)
  }

}