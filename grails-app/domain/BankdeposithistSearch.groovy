class BankdeposithistSearch {
  def searchService
  static mapping = { version false }

//////////////bankdeposithist/////////////
  Integer id
  Integer bankdeposit_id
  Integer dtype
  Integer modstatus
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer term
  String comment
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name

  def csiFindDepositHistory(iDepositId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='bankdeposithist left join user u1 on (u1.id=bankdeposithist.admin_id)'
    hsSql.where="1=1"+
                ((iDepositId>0)?' and bankdeposithist.bankdeposit_id=:bankdeposit_id':'')
    hsSql.order="bankdeposithist.inputdate desc"

    if(iDepositId>0)
      hsLong['bankdeposit_id'] = iDepositId

    searchService.fetchData(hsSql,hsLong,null,null,null,BankdeposithistSearch.class)
  }

}