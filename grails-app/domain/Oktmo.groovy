class Oktmo {
  def searchService
  
  static mapping = {
    id column: 'id', generator: 'assigned'
    version false
  }

  String id
  String okato
  String mesto  

  String toString(){
  	"$id - $okato"
  }
  
  def csiSelectOktmo(sOktmo,sOkato,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='oktmo'
    hsSql.where="1=1"+
                ((sOktmo!='')?' AND id like concat(:sOktmo,"%")':'')+
                ((sOkato!='')?' AND okato like concat(:sOkato,"%")':'')
    hsSql.order="id asc"

    if(sOktmo!='')
      hsString['sOktmo']=sOktmo
    if(sOkato!='')
      hsString['sOkato']=sOkato

    return searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Oktmo.class)
  }
  def csiSetCSVOktmo(lsData){
    id=lsData[0]
    okato=lsData[1]
    mesto=lsData[2] 
    this
  }
}