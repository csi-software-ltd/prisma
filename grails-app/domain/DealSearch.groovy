class DealSearch {
  def searchService
  static mapping = { version false }
//////////////deal////////////////////////
  Integer id
  Integer client_id
  Integer dtype
  Date dstart
  Date dend
  Date moddate
  BigDecimal commission
  BigDecimal income
  BigDecimal outlay
  BigDecimal dealsaldo
  Integer modstatus
//////////////Client//////////////////////
  String client_name

  def csiSelectDeals(iClientId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="deal.*, client.name as client_name"
    hsSql.from='deal join client on (deal.client_id=client.id)'
    hsSql.where="1=1"+
                ((iClientId>0)?' and deal.client_id=:client_id':'')
    hsSql.order="deal.id desc"

    if(iClientId>0)
      hsLong['client_id'] = iClientId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'deal.id',true,DealSearch.class)
  }
}