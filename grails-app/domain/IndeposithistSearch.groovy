class IndeposithistSearch {
  def searchService
  static mapping = { version false }

//////////////indeposithist///////////////
  Integer id
  Integer indeposit_id
  Integer atype
  Integer modstatus
  String anumber
  Date enddate
  BigDecimal summa
  BigDecimal rate
  BigDecimal comrate
  BigDecimal startsaldo
  String comment
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name

  def csiFindDepositHistory(iDepositId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name"
    hsSql.from='indeposithist left join user u1 on (u1.id=indeposithist.admin_id)'
    hsSql.where="1=1"+
                ((iDepositId>0)?' and indeposithist.indeposit_id=:indeposit_id':'')
    hsSql.order="indeposithist.inputdate desc"

    if(iDepositId>0)
      hsLong['indeposit_id'] = iDepositId

    searchService.fetchData(hsSql,hsLong,null,null,null,IndeposithistSearch.class)
  }

}