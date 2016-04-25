class SmrSearch {
  def searchService
  static mapping = { version false }

//////////////smr/////////////////////////
  Integer id
  Integer client
  Integer supplier
  Integer smrsort
  Integer smrcat_id
  Date inputdate
  Date adate
  Date enddate
  String anumber
  String cbank_id
  String sbank_id
  String description
  Integer modstatus
  String comment
  Long summa
  Integer paytype
  Long avans
  Double avanspercent
  Long responsible
  Integer project_id
//////////////Company/////////////////////
  String client_name
  String supplier_name

  def csiSelectSmrs(sClientName,sSupplierName,iCatId,iSort,iStatus,iVision,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as client_name, c2.name as supplier_name"
    hsSql.from='smr, company as c1, company as c2'
    hsSql.where="smr.client=c1.id and smr.supplier=c2.id"+
                ((sClientName!='')?' and c1.name like concat("%",:client_name,"%")':'')+
                ((sSupplierName!='')?' and c2.name like concat("%",:supplier_name,"%")':'')+
                ((iCatId>0)?' and smr.smrcat_id=:smrcat_id':'')+
                ((iSort>0)?' and smr.smrsort=:smrsort':'')+
                ((iStatus>-100)?' and smr.modstatus=:modstatus':'')+
                (iVision>0?' and (if(c1.is_holding=0,0,c1.visualgroup_id)=:visualgroup_id or if(c2.is_holding=0,0,c2.visualgroup_id)=:visualgroup_id)':'')
    hsSql.order="smr.id desc"

    if(sClientName!='')
      hsString['client_name'] = sClientName
    if(sSupplierName!='')
      hsString['supplier_name'] = sSupplierName
    if(iCatId>0)
      hsLong['smrcat_id'] = iCatId
    if(iSort>0)
      hsLong['smrsort'] = iSort
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus
    if(iVision>0)
      hsLong['visualgroup_id'] = iVision

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'smr.id',true,SmrSearch.class)
  }

  def csiSelectSmrSummary(Date dateStart, Date dateEnd, iClientId, iSupplierId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, c1.name as client_name, c2.name as supplier_name"
    hsSql.from='smr, company c1, company c2'
    hsSql.where="smr.client=c1.id and smr.supplier=c2.id and smr.modstatus>=0"+
                (iSupplierId>0?' and smr.supplier=:supplier':'')+
                (iClientId>0?' and smr.client=:client':'')+
                (dateStart?' and smr.adate>=:datestart':'')+
                (dateEnd?' and smr.adate<=:dateend and smr.enddate>=:dateend':'')
    hsSql.order="smr.id desc"

    if(iSupplierId>0)
      hsLong['supplier'] = iSupplierId
    if(iClientId>0)
      hsLong['client'] = iClientId
    if (dateStart)
      hsString['datestart'] = String.format('%tF',dateStart)
    if (dateEnd)
      hsString['dateend'] = String.format('%tF',dateEnd)

    searchService.fetchData(hsSql,hsLong,null,hsString,null,SmrSearch.class)
  }
}