class IndepositprojectSearch {
  def searchService
  static mapping = { version false }

//////////////indepositproject////////////
  Integer id
  Integer indeposit_id
  Integer project_id
  BigDecimal summa
  Integer is_percent
  Integer is_transfer
  Integer related_id
  Date operationdate
  Date inputdate
//////////////project/////////////////////
  String project_name
  Integer is_main
//////////////general/////////////////////
  BigDecimal saldo

  def csiSelectOperations(hsRequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, prj.name as project_name, ifnull((select sum(summa) from indepositproject where indepositproject.indeposit_id=ind.indeposit_id and indepositproject.project_id=ind.project_id and indepositproject.is_percent=0 and (indepositproject.operationdate<ind.operationdate or (indepositproject.operationdate=ind.operationdate and indepositproject.id<=ind.id))),0) as saldo"
    hsSql.from='indepositproject as ind, project as prj'
    hsSql.where="ind.project_id=prj.id"+
                (hsRequest?.indeposit_id>0?' and ind.indeposit_id=:indeposit_id':'')+
                (hsRequest?.project_id>0?' and ind.project_id=:project_id':'')
    hsSql.order="ind.operationdate desc, ind.id desc"

    if(hsRequest?.indeposit_id>0)
      hsLong['indeposit_id'] = hsRequest.indeposit_id
    if(hsRequest?.project_id>0)
      hsLong['project_id'] = hsRequest.project_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'ind.id',true,IndepositprojectSearch.class)
  }

}