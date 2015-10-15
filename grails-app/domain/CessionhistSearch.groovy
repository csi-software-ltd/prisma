class CessionhistSearch {
  def searchService
  static mapping = {
    version false
  }

//////////////cessionhist/////////////////
  Integer id
  Integer cession_id
  Date enddate
  BigDecimal summa
  String dopagrcomment
  Long responsible
  Date inputdate
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
  String responsible_name

  def csiFindCessionHistory(iCessionId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as responsible_name"
    hsSql.from='cessionhist left join user u1 on (u1.id=cessionhist.admin_id) left join user u2 on (u2.id=cessionhist.responsible)'
    hsSql.where="1=1"+
                ((iCessionId>0)?' and cessionhist.cession_id=:cession_id':'')
    hsSql.order="cessionhist.inputdate desc"

    if(iCessionId>0)
      hsLong['cession_id']=iCessionId

    searchService.fetchData(hsSql,hsLong,null,null,null,CessionhistSearch.class)
  }

}