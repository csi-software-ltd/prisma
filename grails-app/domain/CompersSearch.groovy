class CompersSearch {
  def searchService
  static mapping = {
    version false
  }
  static constraints = {
  }

////////Compers////////
  Integer id
  Long pers_id
  Integer position_id
  Integer composition_id
  Integer company_id
  Date jobstart
  Date jobend
  Date gd_valid
  Integer modstatus
  Integer salary
  String industrywork
  String prevwork
  String comment
/////////Composition///
  String position_name
/////////History///////
  Date inputdate
  String admin_name
/////////Pers//////////
  String shortname
  String fullname
  String passport
  String passdate
  String passorg
  String propiska
  String citizen
  String education

  String collectPassData(){
    "Паспорт №$passport выдан $passorg $passdate"
  }

  def csiFindCompers(iCompanyId,sSnils,iModstatus){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, null as inputdate, pers.shortname as shortname, '' as admin_name, composition.name as position_name"
    hsSql.from='compers,pers,composition'
    hsSql.where="pers.id=compers.pers_id and compers.composition_id=composition.id"+
                ((iCompanyId!=0)?' and compers.company_id=:company_id':'')+
                ((sSnils!='')?' and pers.snils=:snils':'')+
                ((iModstatus>-100)?' and modstatus=:modstatus':'')
    hsSql.order="compers.position_id asc, pers.shortname"

    if(iCompanyId!=0)
      hsLong['company_id']=iCompanyId
    if(sSnils!='')
      hsString['snils']=sSnils
    if(iModstatus>-100)
      hsLong['modstatus']=iModstatus

    def hsRes=searchService.fetchData(hsSql,hsLong,null,hsString,null,CompersSearch.class)
  }

  def csiFindNonLinkedCompers(iMonth,iYear){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, null as inputdate, pers.shortname as shortname, company.name as admin_name, '' as position_name"
    hsSql.from='compers left join salarycomp on (compers.pers_id=salarycomp.pers_id and compers.company_id=salarycomp.company_id and (salarycomp.month=:month and salarycomp.year=:year)), pers, company'
    hsSql.where="compers.modstatus=1 and salarycomp.id IS NULL and compers.pers_id=pers.id and compers.company_id=company.id"
    hsSql.order="compers.company_id asc, pers.shortname"

    hsLong['month']=iMonth
    hsLong['year']=iYear

    searchService.fetchData(hsSql,hsLong,null,null,null,CompersSearch.class)
  }

  def csiFindCompersHistory(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, 0 as modstatus, pers.shortname as shortname, user.name as admin_name, composition.name as position_name, '' as industrywork, '' as prevwork"
    hsSql.from='compershist,pers,user,composition'
    hsSql.where="pers.id=compershist.pers_id and compershist.admin_id=user.id and compershist.composition_id=composition.id"+
                ((iCompanyId>0)?' and compershist.company_id=:company_id':'')
    hsSql.order="compershist.inputdate desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,CompersSearch.class)
  }

  def csiFindCompersAll(iCompanyId,iModstatus){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, null as inputdate, pers.shortname as shortname, '' as admin_name, '' as position_name"
    hsSql.from='compers,pers'
    hsSql.where="pers.id=compers.pers_id"+
                ((iCompanyId>0)?' and compers.company_id=:company_id':'')+
                ((iModstatus>-100)?' and modstatus=:modstatus':'')
    hsSql.order="compers.position_id asc, pers.shortname"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iModstatus>-100)
      hsLong['modstatus']=iModstatus

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,CompersSearch.class)
  }
  def csiFindCompersByPosition(iCompanyId,iPosition_id,bActive=-1){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, null as inputdate, pers.shortname as shortname, '' as admin_name, '' as position_name"
    hsSql.from='compers,pers'
    hsSql.where="pers.id=compers.pers_id"+
                ((iCompanyId>0)?' and compers.company_id=:company_id':'')+
                ((iPosition_id>0)?' and position_id=:iPosition_id':'')+
                ((bActive==0)?' and jobend is not null':'')+
                ((bActive==1)?' and jobend is null':'')
    hsSql.order="compers.position_id asc, pers.shortname"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iPosition_id>0)
      hsLong['iPosition_id']=iPosition_id

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,CompersSearch.class)
  }

  def csiFindActiveCompers(lId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='compers,company'
    hsSql.where="company.id=compers.company_id and company.modstatus=1 and compers.modstatus=1"+
                ((lId>0)?' and compers.pers_id=:pers_id':'')
    hsSql.order="company.id asc"

    if(lId>0)
      hsLong['pers_id']=lId

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,Compers.class)
  }

  def csiFindGDsByDate(dDate){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, company.name as position_name, '' as admin_name"
    hsSql.from='compers,pers,company'
    hsSql.where="pers.id=compers.pers_id and compers.company_id=company.id and compers.position_id=1 and compers.jobstart<=:repdate and ifnull(compers.jobend,:repdate)>=:repdate"
    hsSql.order="pers.shortname asc"

    hsString['repdate'] = String.format('%tF',dDate)

    def hsRes=searchService.fetchData(hsSql,null,null,hsString,null,CompersSearch.class)
  }

  def csiFindGDsByCompany(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, null as inputdate, pers.shortname as shortname, '' as admin_name, '' as position_name"
    hsSql.from='compers,pers'
    hsSql.where="pers.id=compers.pers_id and compers.position_id=1"+
                ((iCompanyId>-100)?' and compers.company_id=:company_id':'')
    hsSql.order="compers.jobstart desc"

    if(iCompanyId>-100)
      hsLong['company_id']=iCompanyId

    def hsRes=searchService.fetchData(hsSql,hsLong,null,null,null,CompersSearch.class)
  }
}