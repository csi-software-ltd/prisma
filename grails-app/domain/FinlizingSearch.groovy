class FinlizingSearch {
  def searchService
  static mapping = { version false }

//////////////flizing/////////////////////
  Integer id
  Integer fldatel
  Integer flpoluchatel
  Integer flbank
  Date inputdate
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer modstatus
  String comment
//////////////Company/////////////////////
  String fldatel_name
  String flpoluchatel_name
  String bankcompany_name

  def csiSelectFLizings(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as fldatel_name, c2.name as flpoluchatel_name, c3.name as bankcompany_name"
    hsSql.from='finlizing, company as c1, company as c2, company as c3'
    hsSql.where="finlizing.fldatel=c1.id and finlizing.flpoluchatel=c2.id and finlizing.flbank=c3.id"+
                (hsRequest?.flid>0?' and finlizing.id=:flid':'')+
                (hsRequest?.modstatus>-100?' and finlizing.modstatus=:status':'')+
                (hsRequest?.company_name?' and c2.name like concat("%",:company_name,"%")':'')
    hsSql.order="finlizing.id desc"

    if(hsRequest?.flid>0)
      hsLong['flid'] = hsRequest.flid
    if(hsRequest?.modstatus>-100)
      hsLong['status'] = hsRequest.modstatus
    if(hsRequest?.company_name)
      hsString['company_name'] = hsRequest.company_name

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'finlizing.id',true,FinlizingSearch.class)
  }

}