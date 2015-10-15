class KredithistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////kredithist//////////////////
  Integer id
  Integer kredit_id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  BigDecimal summa
  Double rate
  Date enddate
  Integer kreditterm
  Integer is_agr
  String comment
  String dopagrcomment
  Long responsible
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
  String responsible_name

  def csiFindKreditHistory(iKreditId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as responsible_name"
    hsSql.from='kredithist left join user u1 on (u1.id=kredithist.admin_id) left join user u2 on (u2.id=kredithist.responsible)'
    hsSql.where="1=1"+
                ((iKreditId>0)?' and kredithist.kredit_id=:kredit_id':'')
    hsSql.order="kredithist.inputdate desc"

    if(iKreditId>0)
      hsLong['kredit_id']=iKreditId

    searchService.fetchData(hsSql,hsLong,null,null,null,KredithistSearch.class)
  }

}