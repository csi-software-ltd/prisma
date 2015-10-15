class LicplanpaymentSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////licplanpayment//////////////
  Integer id
  Integer license_id
  Date paydate
  Integer summa
  Integer modstatus
  Long admin_id
//////////////Company/////////////////////
  String company_name
  String sro_name
//////////////License/////////////////////
  String anumber
  Date adate

  def csiSelectLicensePayments(sCompanyName,iStatus,dStartDate,dEndDate,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as company_name, c2.name as sro_name"
    hsSql.from='licplanpayment, license, company c1, company c2'
    hsSql.where="licplanpayment.license_id=license.id and license.company_id=c1.id and license.sro_id=c2.id and licplanpayment.modstatus>=0"+
                ((sCompanyName!='')?' and c1.name like concat("%",:company_name,"%")':'')+
                ((iStatus>-100)?' and licplanpayment.modstatus=:modstatus':'')+
                ((dStartDate)?' and licplanpayment.paydate>=:startdate':'')+
                ((dEndDate)?' and licplanpayment.paydate<:enddate':'')
    hsSql.order="licplanpayment.paydate asc"

    if(sCompanyName!='')
      hsString['company_name']=sCompanyName
    if(iStatus>-100)
      hsLong['modstatus']=iStatus
    if(dStartDate)
      hsString['startdate']=String.format('%tF',dStartDate)
    if(dEndDate)
      hsString['enddate']=String.format('%tF',dEndDate)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'licplanpayment.id',true,LicplanpaymentSearch.class)
  }

}