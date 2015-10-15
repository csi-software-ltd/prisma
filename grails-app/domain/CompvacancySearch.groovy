class CompvacancySearch {
  def searchService
  static mapping = { version false }
////////Compvacancy//////
  Integer id
  Integer company_id
  Integer composition_id
  Integer salary
  Integer numbers
////////composition//////
  String position_name

  def csiFindCompvacancyByCompanyId(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, composition.name as position_name"
    hsSql.from='compvacancy, composition'
    hsSql.where="compvacancy.composition_id=composition.id"+
                ((iCompanyId>0)?' and compvacancy.company_id=:company_id':'')
    hsSql.order="composition.position_id asc, composition.name asc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,CompvacancySearch.class)
  }

}