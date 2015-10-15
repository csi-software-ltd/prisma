class SmrhistSearch {
  def searchService
  static mapping = { version false }

//////////////smrhist/////////////////////
  Integer id
  Integer smr_id
  Date inputdate
  Date enddate
  Integer smrcat_id
  Long summa
  Long responsible
  String comment
  Long admin_id
//////////////Admin///////////////////////
  String admin_name
  String res_name

  def csiFindSmrHistory(iSmrId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, u1.name as admin_name, u2.name as res_name"
    hsSql.from='smrhist left join user u1 on (u1.id=smrhist.admin_id) left join user u2 on (u2.id=smrhist.responsible)'
    hsSql.where="1=1"+
                ((iSmrId>0)?' and smrhist.smr_id=:smr_id':'')
    hsSql.order="smrhist.inputdate desc"

    if(iSmrId>0)
      hsLong['smr_id'] = iSmrId

    searchService.fetchData(hsSql,hsLong,null,null,null,SmrhistSearch.class)
  }

}