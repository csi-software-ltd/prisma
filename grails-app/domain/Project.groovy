class Project {
  def searchService
  def agentKreditService
  static mapping = { version false }

  static constraints = {
    startdate(nullable:true)
    enddate(nullable:true)
  }

  Integer id
  Integer modstatus
  String name
  String description
  Date startdate
  Date enddate
  Integer is_main = 0
  Long loansaldo = 0

  def csiSelectProject(sName,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='project'
    hsSql.where="1=1"+
              ((sName!='')?' AND name like concat("%",:name,"%")':'')+
              ((iModstatus!=-1)?' AND modstatus=:modstatus':'')
    hsSql.order="id asc"

    if(sName!='')
      hsString['name'] = sName
    if(iModstatus!=-1)
      hsInt['modstatus'] = iModstatus

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'id',true,Project.class)
  }

  Project csiSetProject(hsInrequest){
    name = hsInrequest?.name?:''
    description = hsInrequest?.description?:''
    startdate = hsInrequest?.startdate
    enddate = hsInrequest?.enddate
    modstatus = enddate?0:1

    this
  }

  Project changeLoansaldo(lSaldo){
    loansaldo += lSaldo
    this
  }

  def csiSearchUserProject(lUserId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]   

    hsSql.select="*, project.id as id"
    hsSql.from="project, user2project"     
    hsSql.where="user2project.project_id=project.id"+
                ((lUserId>0)?' AND user2project.user_id=:lUserId':'')                
    hsSql.order="project.name asc"

    if(lUserId>0)
      hsLong['lUserId']=lUserId

    searchService.fetchData(hsSql,hsLong,null,null,null,Project.class)
  }

  def csiSearchIndepositProjects(iDepositId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong = [:]

    hsSql.select="*"
    hsSql.from="project, indepositproject"
    hsSql.where="indepositproject.project_id=project.id"+
                (iDepositId>0?' AND indepositproject.indeposit_id=:dep_id':'')
    hsSql.group="project.id"
    hsSql.order="project.name asc"

    if(iDepositId>0)
      hsLong['dep_id'] = iDepositId

    searchService.fetchData(hsSql,hsLong,null,null,null,Project.class)
  }

  BigDecimal computeIndepositSaldo(iIndepositId){
    (!is_main?0.0g:Indeposit.get(iIndepositId)?.startsaldo?:0.0g) + (Indepositproject.findAllByIndeposit_idAndProject_idAndIs_percent(iIndepositId,id,0).sum{ it.summa }?:0.0g)
  }

  BigDecimal computeIndepositProjectPercent(iIndepositId, Date _date=null){
    agentKreditService.computeIndepositProjectPercent(this,Indeposit.get(iIndepositId),_date)
  }
}