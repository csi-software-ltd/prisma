class SpaceSummarySearch {
  def searchService
  static mapping = { version false }
//////////////space////////////////////////
  String id
  Integer sid
  Integer mainagr_id
  Integer spacetype_id
  Integer arendator
  Integer arendodatel
  String city
  String fulladdress
  String shortaddress
  String anumber
  Date adate
  Date inputdate
  Date enddate
  Integer prolongcondition
  Integer prolongterm
  Integer monthnotification
  String description
  Integer asort
  Integer arendatype_id
  Integer paystatus
  Integer modstatus
  Integer is_nosubrenting
  Integer subspaceqty
  Integer is_nopayment
  Integer is_adrsame
  String comment
  Integer project_id
  Double area
  Integer payterm
  String bank_id
  Double ratemeter
  Integer is_addpayment
  BigDecimal rate
  BigDecimal actrate
  BigDecimal ratedop
  BigDecimal debt
  Date debtdate
  Long responsible
  Integer permitstatus
  Integer workstatus
  String prolongcomment
  Date permitdate
  Date workdate
  Long workuser
//////////////Company/////////////////////
  String arendator_name
  String arendator_gd
  String arendodatel_name
//////////////Bank////////////////////////
  String bank_name
//////////////Bankchek////////////////////
  Date checkdate
  Integer checktype_id

  def csiSelectSpaces(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="concat(space.id,' - ',ifnull(bankcheck.id,0)) as id, space.id as sid, c1.name as arendator_name, c2.name as arendodatel_name, c1.gd as arendator_gd, bank.name as bank_name, space.*, bank.*, bankcheck.*"
    hsSql.from='space left join bankcheck on (space.id=bankcheck.agr_id and bankcheck.agrtype_id=2) left join bank on (bankcheck.bank_id=bank.id), company as c1, company as c2'
    hsSql.where="space.modstatus=1 and space.arendator=c1.id and space.arendodatel=c2.id"+
                (hsRequest?.address?' and space.fulladdress like concat("%",:address,"%")':'')+
                (hsRequest?.arendator_name?' and c1.name like concat("%",:arendator_name,"%")':'')+
                (hsRequest?.arendodatel_name?' and c2.name like concat("%",:arendodatel_name,"%")':'')+
                (hsRequest?.is_nds==1?' and c2.taxoption_id=1':hsRequest?.is_nds==2?' and c2.taxoption_id>1':'')+
                (hsRequest?.anumber?' and space.anumber like concat("%",:anumber,"%")':'')+
                (hsRequest?.bank_id?' and bankcheck.bank_id=:bank_id':'')+
                (hsRequest?.bankchecktype_id>0?' and bankcheck.checktype_id=:checktype_id':'')+
                (hsRequest?.spacetype_id>0?' and space.spacetype_id=:spacetype_id':'')+
                (hsRequest?.arendatype_id>0?' and space.arendatype_id=:arendatype_id':'')+
                (hsRequest?.subrenting==1?' and space.subspaceqty>0 and space.is_subwritten=0':hsRequest?.subrenting==2?' and space.subspaceqty>0 and space.is_subwritten=1':hsRequest?.subrenting==3?' and space.is_nosubrenting=1':'')+
                (hsRequest?.is_adrsame>0?' and space.is_adrsame>0':'')+
                (hsRequest?.payterm_from>0?' and space.payterm>=:payterm_from':'')+
                (hsRequest?.payterm_to>0?' and space.payterm<=:payterm_to':'')+
                (hsRequest?.enddate_from?' and space.enddate>=:enddate_from':'')+
                (hsRequest?.enddate_to?' and space.enddate<=:enddate_to':'')
    hsSql.order="c1.name asc, c2.name asc"

    if(hsRequest?.address)
      hsString['address']=hsRequest.address
    if(hsRequest?.arendator_name)
      hsString['arendator_name']=hsRequest.arendator_name
    if(hsRequest?.arendodatel_name)
      hsString['arendodatel_name']=hsRequest.arendodatel_name
    if(hsRequest?.anumber)
      hsString['anumber']=hsRequest.anumber
    if(hsRequest?.bank_id)
      hsString['bank_id']=hsRequest.bank_id
    if(hsRequest?.bankchecktype_id>0)
      hsLong['checktype_id']=hsRequest.bankchecktype_id
    if(hsRequest?.spacetype_id>0)
      hsLong['spacetype_id']=hsRequest.spacetype_id
    if(hsRequest?.arendatype_id>0)
      hsLong['arendatype_id']=hsRequest.arendatype_id
    if(hsRequest?.payterm_from>0)
      hsLong['payterm_from']=hsRequest.payterm_from
    if(hsRequest?.payterm_to>0)
      hsLong['payterm_to']=hsRequest.payterm_to
    if(hsRequest?.enddate_from)
      hsString['enddate_from']=String.format('%tF',hsRequest.enddate_from)
    if(hsRequest?.enddate_to)
      hsString['enddate_to']=String.format('%tF',hsRequest.enddate_to)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'space.id',true,SpaceSummarySearch.class)
  }
}