class Agent {
  def searchService
  static mapping = { version false }

  Integer id
  String name
  Date inputdate = new Date()
  Integer modstatus = 1
  Integer client_id = 0

  def csiSelectAgent(sName,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='agent'
    hsSql.where="1=1"+
              ((sName!='')?' AND name=:name':'')+
              ((iModstatus!=-1)?' AND modstatus=:modstatus':'')

    hsSql.order="name asc"

    if(sName!='')
      hsString['name']=sName
    if(iModstatus!=-1)
      hsInt['modstatus']=iModstatus

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'id',true,Agent.class)
  }

  Agent csiSetAgent(hsInrequest){
    name = hsInrequest.aname
    client_id = hsInrequest.client_id?:0

    this
  }

  Agent csiSetModstatus(iModstatus){
    modstatus=iModstatus

    this
  }
}