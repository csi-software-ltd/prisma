class IndepositSearch {
  def searchService
  static mapping = { version false }

//////////////indeposit///////////////////
  Integer id
  Integer client_id
  Integer atype
  Integer aclass
  Integer modstatus
  Date inputdate
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  BigDecimal startsaldo
  Integer valuta_id
  String comment
//////////////Client//////////////////////
  String client_name

  def csiSelectDeposits(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as client_name"
    hsSql.from='indeposit, client as c1'
    hsSql.where="indeposit.client_id=c1.id"+
                (hsRequest?.indid>0?' and indeposit.id=:indid':'')+
                (hsRequest?.client_id>0?' and indeposit.client_id=:client_id':'')+
                (hsRequest?.aclass>0?' and indeposit.aclass=:aclass':'')+
                (hsRequest?.modstatus>-100?' and indeposit.modstatus=:status':'')
    hsSql.order="indeposit.id desc"

    if(hsRequest?.indid>0)
      hsLong['indid'] = hsRequest.indid
    if(hsRequest?.client_id>0)
      hsLong['client_id'] = hsRequest.client_id
    if(hsRequest?.aclass>0)
      hsLong['aclass'] = hsRequest.aclass
    if(hsRequest?.modstatus>-100)
      hsLong['status'] = hsRequest.modstatus

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'indeposit.id',true,IndepositSearch.class)
  }

}