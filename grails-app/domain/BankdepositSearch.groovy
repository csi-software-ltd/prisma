class BankdepositSearch {
  def searchService
  static mapping = { version false }

//////////////deposit/////////////////////
  Integer id
  Integer bank
  Integer dtype
  Integer modstatus
  Date inputdate
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer valuta_id
  Integer term
  BigDecimal startsumma
  BigDecimal startprocent
  Date startsaldodate
  String comment
//////////////Company/////////////////////
  String bankcompany_name

  def csiSelectDeposits(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as bankcompany_name"
    hsSql.from='bankdeposit, company as c1'
    hsSql.where="bankdeposit.bank=c1.id"+
                (hsRequest?.did>0?' and bankdeposit.id=:did':'')+
                (hsRequest?.bankcompany_id>0?' and bankdeposit.bank=:bcompany_id':'')
    hsSql.order="bankdeposit.id desc"

    if(hsRequest?.did>0)
      hsLong['did'] = hsRequest.did
    if(hsRequest?.bankcompany_id>0)
      hsLong['bcompany_id'] = hsRequest.bankcompany_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'bankdeposit.id',true,BankdepositSearch.class)
  }

}