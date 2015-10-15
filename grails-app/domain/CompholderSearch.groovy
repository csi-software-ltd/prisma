class CompholderSearch {
  def searchService
  static mapping = { version false }
////////Compholder//////
  Integer id
  Integer company_id
  Long pers_id
  Integer holdcompany_id
  Integer share
  Long summa
  Date startdate
  Date enddate
  Integer modstatus
  String comment
  Integer admin_id
/////////Admin/////////
  String admin_name
/////////Pers//////////
  String shortname
  String fullname
  String passport
  String passdate
  String passorg
  String propiska
  String citizen
/////////Pers//////////
  String company_name
  String legaladr

  def csiFindCompholdersByCompanyIdAndModstatus(iCompanyId,iModstatus,iShare=-1){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, pers.shortname as shortname, user.name as admin_name, company.name as company_name"
    hsSql.from='compholder left join pers on (pers.id=compholder.pers_id) left join company on (company.id=compholder.holdcompany_id),user'
    hsSql.where="compholder.admin_id=user.id"+
                ((iCompanyId>0)?' and compholder.company_id=:company_id':'')+
                ((iModstatus>-100)?' and compholder.modstatus=:modstatus':'')+
                ((iShare>-1)?' and compholder.share>=:share':'')
    hsSql.order="ifnull(pers.shortname,company.name) asc, enddate asc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId
    if(iModstatus>-100)
      hsLong['modstatus']=iModstatus
    if(iShare>-1)
      hsLong['share']=iShare

    searchService.fetchData(hsSql,hsLong,null,null,null,CompholderSearch.class)
  }

  def compholderAutocomplete(sName){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*, pers.shortname as shortname, '' as admin_name, '' as company_name"
    hsSql.from='compholder,pers'
    hsSql.where="pers.id=compholder.pers_id and compholder.modstatus=1"+
                ((sName!='')?' and pers.shortname like concat(:name,"%")':'')
    hsSql.group="compholder.pers_id"
    hsSql.order="pers.shortname asc"

    if(sName!='')
      hsString['name'] = sName

    searchService.fetchData(hsSql,null,null,hsString,null,CompholderSearch.class,10)
  }

}