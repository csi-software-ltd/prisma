class Taxinspection {
  def searchService
  static mapping = {
    id column: 'id', generator: 'assigned'
    version false
  }

  String id
  String name
  String address
  String tel
  String district

  String toString(){
  	"$id - $name"
  }

  def csiSelectInspections(sId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='taxinspection'
    hsSql.where="1=1"+
                ((sId!='')?' AND id like concat(:inspection_id,"%")':'')
    hsSql.order="id desc"

    if(sId!='')
      hsString['inspection_id']=sId

    def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,'id',true,Taxinspection.class)
  }
  
  def csiSetTaxinspection(hsInrequest){
    id=hsInrequest?.id
    name=hsInrequest?.name
    address=hsInrequest?.address
    tel=hsInrequest?.tel?:''
    district=hsInrequest?.district?:''
    this
  }
  
  def csiSetCSVTaxinspection(lsData){
    id=lsData[0]
    name=lsData[1]
    address=lsData[2]
    tel=lsData[3]
    district=lsData[4]
    this
  }
}
