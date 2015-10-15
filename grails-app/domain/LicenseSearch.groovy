class LicenseSearch {
  def searchService
  static mapping = {
    version false
  }

  Integer id
  Integer company_id
  Integer sro_id
  Integer industry_id
  Date inputdate
  String anumber
  Date adate
  Date enddate
  String license
  Integer paytype
  Integer entryfee
  Integer paidfee
  Integer alimit
  Integer regfee
  Integer regfeeterm

  String sro_name
  String company_name

  def csiFindCompanyLicenses(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, company.name as sro_name, null as company_name"
    hsSql.from='license, company'
    hsSql.where="license.sro_id=company.id and license.modstatus>0"+
                ((iCompanyId>0)?' and license.company_id=:company_id':'')
    hsSql.order="license.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,LicenseSearch.class)
  }

  def csiSelectLicenses(iCompanyId,iIndustryId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as sro_name, c2.name as company_name"
    hsSql.from='license, company c1, company c2'
    hsSql.where="license.sro_id=c1.id and license.company_id=c2.id and license.modstatus>0"+
                ((iCompanyId>0)?' and license.company_id=:company_id':'')+
                ((iIndustryId>0)?' and license.industry_id=:industry_id':'')
    hsSql.order="license.id desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iIndustryId>0)
      hsLong['industry_id']=iIndustryId

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'license.id',true,LicenseSearch.class)
  }

  def csiSelectLicenseSummary(Date dateStart, Date dateEnd, iCompanyId, iSroId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as sro_name, c2.name as company_name"
    hsSql.from='license, company c1, company c2'
    hsSql.where="license.sro_id=c1.id and license.company_id=c2.id and license.modstatus>=0"+
                (iCompanyId>0?' and license.company_id=:company_id':'')+
                (iSroId>0?' and license.sro_id=:sro_id':'')+
                (dateStart?' and license.adate>=:datestart':'')+
                (dateEnd?' and license.adate<=:dateend and license.enddate>=:dateend':'')
    hsSql.order="license.id desc"

    if(iCompanyId>0)
      hsLong['company_id'] = iCompanyId
    if(iSroId>0)
      hsLong['sro_id'] = iSroId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,LicenseSearch.class)
  }
}