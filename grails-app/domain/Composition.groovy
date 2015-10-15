class Composition {
  def searchService
  static mapping = {
    version false
  }
  
  Integer id
  String name
  Integer position_id  
  
  Composition csiSetData(hsInrequest){
    name=hsInrequest?.name?:''
    position_id=hsInrequest?.position_id?:0
    this
  }
  
  def csiFindComposition(iPositionId,sName){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*,c.id as id"
    hsSql.from='composition as c, position as p'
    hsSql.where='(c.position_id=p.id)'+
                 ((iPositionId>0)?' AND c.position_id=:position_id':'')+
                 ((sName!='')?' AND c.name like concat("%",:name,"%")':'')
    hsSql.order="p.name asc, c.name asc"

    if(iPositionId>0)
      hsInt['position_id']=iPositionId
    if(sName!='')
      hsString['name']=sName
      
    searchService.fetchData(hsSql,null,hsInt,hsString,null,Composition.class)
  }
}
