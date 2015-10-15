class CompanyhistSearch {
  def searchService
  static mapping = {
    version false
  }

  Integer id
  Integer company_id
  String legalname
  Date namedate
  String oktmo
  String kpp
  String okato
  String legaladr
  Date adrdate
  Long capital
  Date capitaldate
  Integer capitalsecure
  Integer capitalpaid
  Long cost
  String tel
  String taxinspection_id
  String pfrfreg
  String fssreg
  Integer taxoption_id
  Integer activitystatus_id
  Long admin_id
  Date inputdate

  String admin_name

  def csiFindHistory(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, user.name as admin_name"
    hsSql.from='companyhist left join user on (user.id=companyhist.admin_id)'
    hsSql.where="1=1"+
                ((iCompanyId>0)?' and companyhist.company_id=:company_id':'')
    hsSql.order="companyhist.inputdate desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,CompanyhistSearch.class)
  }

}