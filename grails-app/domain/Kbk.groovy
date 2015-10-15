class Kbk {
  def searchService
  
  static mapping = {
    version false
  }

  String id
  String name
  String razdel
  String kbk
  String kbkpeni
  String kbkshtraf
  String kbksearch
  Integer kbkrazdel_id  
 
  def csiSelectKbk(sKbksearch,iKbkRazdel_id,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='kbk'
    hsSql.where="1=1"+
                ((sKbksearch!='')?' AND kbksearch like concat("%",:sKbksearch,"%")':'')+
                ((iKbkRazdel_id>0)?' AND kbkrazdel_id =:iKbkRazdel_id':'')
    hsSql.order="id asc"

    if(sKbksearch!='')
      hsString['sKbksearch']=sKbksearch
    if(iKbkRazdel_id>0)
      hsInt['iKbkRazdel_id']=iKbkRazdel_id

    return searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,
      null,null,iMax,iOffset,'id',true,Kbk.class)
  }
  
  def csiSetCSVKbk(lsData){
    name=lsData[0]
    razdel=lsData[1]
    kbk=lsData[2]
    kbkpeni=lsData[3]
    kbkshtraf=lsData[4]
    kbksearch=lsData[5]
    kbkrazdel_id=lsData[6]
    this    
  }
}