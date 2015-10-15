class LicensehistSearch {
  def searchService
  static mapping = {
    version false
  }

  Integer id
  Integer license_id
  Date inputdate
  String anumber
  Date adate
  Date enddate
  Integer paytype
  Integer entryfee
  Integer alimit
  Integer regfee
  Long admin_id

  String admin_name

  def csiFindHistory(iLicenseId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, user.name as admin_name"
    hsSql.from='licensehist left join user on (user.id=licensehist.admin_id)'
    hsSql.where="1=1"+
                ((iLicenseId>0)?' and licensehist.license_id=:license_id':'')
    hsSql.order="licensehist.inputdate desc"

    if(iLicenseId>0)
      hsLong['license_id']=iLicenseId

    searchService.fetchData(hsSql,hsLong,null,null,null,LicensehistSearch.class)
  }

}