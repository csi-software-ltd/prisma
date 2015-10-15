class SpacehistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////spacehist////////////////////////
  Integer id
  Integer space_id
  Integer mainagr_id
  Integer spacetype_id
  Date enddate
  Integer modstatus
  String comment
  Double area
  Integer payterm
  Double ratemeter
  Integer is_nosubrenting
  Integer is_nopayment
  Integer is_addpayment
  Integer project_id
  BigDecimal rate
  Long admin_id
  Long responsible
  Date inputdate
//////////////Company/////////////////////
  String arendator_name
  String arendodatel_name
//////////////Admin///////////////////////
  String admin_name
  String anumber
  String responsible_name

  def csiFindCompanySpaceHistory(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, c1.name as arendator_name, c2.name as arendodatel_name, user.name as admin_name, space.anumber as anumber, '' as responsible_name"
    hsSql.from='spacehist left join user on (user.id=spacehist.admin_id), space, company as c1, company as c2'
    hsSql.where="spacehist.space_id=space.id and space.arendator=c1.id and space.arendodatel=c2.id"+
                ((iCompanyId>0)?' and (space.arendator=:company_id or space.arendodatel=:company_id)':'')
    hsSql.order="spacehist.inputdate desc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,SpacehistSearch.class)
  }

  def csiFindSpaceHistory(iSpaceId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, '' as arendator_name, '' as arendodatel_name, u1.name as admin_name, '' as anumber, u2.name as responsible_name"
    hsSql.from='spacehist left join user u1 on (u1.id=spacehist.admin_id) left join user u2 on (u2.id=spacehist.responsible)'
    hsSql.where="1=1"+
                ((iSpaceId>0)?' and spacehist.space_id=:space_id':'')
    hsSql.order="spacehist.inputdate desc"

    if(iSpaceId>0)
      hsLong['space_id']=iSpaceId

    searchService.fetchData(hsSql,hsLong,null,null,null,SpacehistSearch.class)
  }

}