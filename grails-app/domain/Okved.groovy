class Okved {
  def searchService
  
  static mapping = {
    id column: 'id', generator: 'assigned'
    version false
  }

  String id
  String name
  String razdel
  String podrazdel
  Integer modstatus

  String toString(){
  	"$id - $name"
  }
  
  def csiSelectOkved(sId,sName,sRazdel,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='okved'
    hsSql.where="1=1"+
                ((sId!='')?' AND id like concat(:sId,"%")':'')+
                ((sName!='')?' AND name like concat("%",:sName,"%")':'')+
                ((sRazdel!='')?' AND razdel like concat("%",:sRazdel,"%")':'')                
    hsSql.order="id asc"

    if(sId!='')
      hsString['sId']=sId
    if(sName!='')
      hsString['sName']=sName
    if(sRazdel!='')
      hsString['sRazdel']=sRazdel

    return searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Okved.class)
  }
  
  def csiSetCSVOkved(lsData){
    id=lsData[0]
    name=lsData[1]
    razdel=lsData[2]
    podrazdel=lsData[3]
    modstatus=lsData[4].toInteger()
    this
  }

}