class SpacefolioSearch {
  def searchService
  static mapping = { version false }

//////////////space//////////////////////
  Integer id
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
  String description
  Integer asort
  Integer arendatype_id
  Integer paystatus
  Integer modstatus
  Integer is_nosubrenting
  Integer is_nopayment
  Integer is_adrsame
  String comment
  Integer project_id
  Double area
  Integer is_territory
  Integer payterm
  Integer paycondition
  Integer paytermcondition
  Integer contcol
  String bank_id
  Double ratemeter
  Integer is_addpayment
  Integer is_noexpense
  BigDecimal rate
  BigDecimal actrate
  BigDecimal ratedop
  BigDecimal debt
  BigDecimal addpayment_debt
  Date debtdate
  Long responsible
//////////////Dopagr/////////////////////
  String ds_anumber
  Date ds_adate
  Date ds_startdate
  Date ds_enddate
  Integer ds_payterm
  Double ds_ratemeter
  BigDecimal ds_rate
  Integer ds_is_addpayment
  BigDecimal ds_ratedop
  Integer is_changeprice
//////////////Others/////////////////////
  String arendator_name
  String arendodatel_name

  def csiSelectSpacefolio(dSpacefolioDate,dDateStart,dDateEnd,iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, spacedopagr.anumber as ds_anumber, spacedopagr.adate as ds_adate, spacedopagr.startdate as ds_startdate, spacedopagr.enddate as ds_enddate, spacedopagr.payterm as ds_payterm, spacedopagr.ratemeter as ds_ratemeter, spacedopagr.rate as ds_rate, spacedopagr.is_addpayment as ds_is_addpayment, spacedopagr.ratedop as ds_ratedop"
    hsSql.from='space join company c1 on (space.arendator=c1.id) join company c2 on (space.arendodatel=c2.id) left join spacedopagr on(spacedopagr.space_id=space.id and spacedopagr.startdate<=:maindate and spacedopagr.enddate>=:maindate)'
    hsSql.where="space.modstatus>=0"+
                ((iCompanyId>0)?' and space.arendator=:cId':'')+
                ((!dDateStart&&!dDateEnd)?' and ifnull(spacedopagr.startdate,space.adate)<=:maindate and ifnull(spacedopagr.enddate,space.enddate)>=:maindate':'')+
                (dDateStart?' and ifnull(spacedopagr.enddate,space.enddate)>=:startdate'+(!dDateEnd?' and ifnull(spacedopagr.startdate,space.adate)<=:startdate':''):'')+
                (dDateEnd?' and ifnull(spacedopagr.startdate,space.adate)<=:enddate'+(!dDateStart?' and ifnull(spacedopagr.enddate,space.enddate)>=:enddate':''):'')
    hsSql.order="space.id asc"
    hsSql.group="space.id"

    if(iCompanyId>0)
      hsLong['cId'] = iCompanyId

    hsString['maindate'] = String.format('%tF',dSpacefolioDate)
    if(dDateStart)
      hsString['startdate'] = String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['enddate'] = String.format('%tF',dDateEnd)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,-1,0,'*',true,SpacefolioSearch.class)
  }

}