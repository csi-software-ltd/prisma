class CessionSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////cession//////////////////////
  Integer id
  Integer cessionvariant
  Integer cessiontype
  Integer changetype
  Integer paytype
  Integer zalogstatus
  String cedent
  Integer cedentcompany
  Integer cessionary
  Integer debtor
  String anumber
  Date adate
  Date inputdate
  Date enddate
  String description
  Integer modstatus
  String comment
  BigDecimal summa
  Integer valuta_id
  Integer agr_id
  Long responsible
//////////////Company/////////////////////
  String debtor_name
  String cessionary_name
  String bank_name
  String cedent_name

  def csiSelectCessions(sInn,sCompanyName,sBankId,iValutaId,iAgrId,iChangeType,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as cessionary_name, bank.name as bank_name, c2.name as debtor_name, c3.name as cedent_name"
    hsSql.from='cession left join bank on (cession.cedent=bank.id) left join company c3 on (cession.cedentcompany=c3.id), company c1, company c2'
    hsSql.where="cession.cessionary=c1.id and cession.debtor=c2.id"+
                ((sInn!='')?' and (c1.inn like concat("%",:inn,"%") or c2.inn like concat("%",:inn,"%"))':'')+
                ((sCompanyName!='')?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                ((sBankId!='')?' and cession.cedent=:bank_id':'')+
                ((iValutaId>0)?' and cession.valuta_id=:valuta_id':'')+
                ((iAgrId>0)?' and cession.agr_id=:agr_id':'')+
                ((iChangeType>0)?' and cession.changetype=:changetype':'')+
                ((iStatus>-100)?' and cession.modstatus=:modstatus':'')
    hsSql.order="cession.id desc"

    if(sInn!='')
      hsString['inn']=sInn
    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(sBankId!='')
      hsString['bank_id']=sBankId
    if(iValutaId>0)
      hsLong['valuta_id']=iValutaId
    if(iAgrId>0)
      hsLong['agr_id']=iAgrId
    if(iChangeType>0)
      hsLong['changetype']=iChangeType
    if(iStatus>-100)
      hsLong['modstatus']=iStatus

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'cession.id',true,CessionSearch.class)
  }

  def csiSelectCompanyCessions(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as cessionary_name, bank.name as bank_name, c2.name as debtor_name, c3.name as cedent_name"
    hsSql.from='cession left join bank on (cession.cedent=bank.id) left join company c3 on (cession.cedentcompany=c3.id), company c1, company c2'
    hsSql.where="cession.cessionary=c1.id and cession.debtor=c2.id"+
                ((iCompanyId>0)?' and cessionary=:company_id':'')
    hsSql.order="cession.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,CessionSearch.class)
  }
}