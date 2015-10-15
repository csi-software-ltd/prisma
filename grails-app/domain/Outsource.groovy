class Outsource {
  def searchService
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  String name
  
  Outsource setData(hsInrequest){
    name=hsInrequest?.name?:''
    modstatus=hsInrequest?.modstatus?:1
    this
  }  
  
  def csiSelectOutsource(sName,iModstatus){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]
    
    hsSql.select="*"
    hsSql.from='outsource'
    hsSql.where="1=1"+
              ((sName!='')?' AND name=:name':'')+
              ((iModstatus!=-1)?' AND modstatus=:modstatus':'')
    hsSql.order="name asc"
    
    if(sName!='')
      hsString['name']=sName
    if(iModstatus!=-1)
      hsInt['modstatus']=iModstatus
      
    searchService.fetchData(hsSql,null,hsInt,hsString,null,Outsource.class)
  }
  
}

