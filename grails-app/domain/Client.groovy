class Client {
  def searchService
  static mapping = {
    version false
    sort "name"
  }

  Integer id
  String name
  Integer modstatus = 1
  Integer parent = 0
  Date inputdate = new Date()
  BigDecimal saldo = 0.0g
  BigDecimal addsaldo = 0.0g
  BigDecimal midsaldo = 0.0g
  Date saldodate = new Date()
  BigDecimal fee = 0.0g
  Integer is_t = 0
  Integer is_super = 0
  Integer is_clientcomm = 0
  Integer is_middleman = 0

  def csiSelectClient(sName,iParentId,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='client'
    hsSql.where="1=1"+
              ((sName!='')?' AND name like concat("%",:name,"%")':'')+
              ((iModstatus!=-1)?' AND modstatus=:modstatus':'')+
              (iParentId>0?' AND parent=:parent':'')
    hsSql.order="name asc"

    if(sName!='')
      hsString['name'] = sName
    if(iModstatus!=-1)
      hsInt['modstatus'] = iModstatus
    if(iParentId>0)
      hsInt['parent'] = iParentId

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'id',true,Client.class)
  }

  Client csiSetClient(hsInrequest){
    name = hsInrequest.name
    parent = hsInrequest.parent?:0
    is_clientcomm = !parent?0:hsInrequest.is_clientcomm?:0
    is_middleman = !parent?0:hsInrequest.is_middleman?:0
    if (is_t) fee = hsInrequest.fee?:0
    this
  }

  Client csiSetBaseSaldo(_request){
    saldo = _request.saldo?:0.0g
    addsaldo = _request.addsaldo?:0.0g
    midsaldo = _request.midsaldo?:0.0g
    this
  }

  Client csiSetModstatus(iModstatus){
    modstatus = iModstatus?:0
    this
  }

  Client updateSaldo(_summa){
    saldo += _summa
    saldodate = new Date()
    this
  }

  Client updateAddSaldo(_summa){
    addsaldo += _summa
    this
  }

  BigDecimal computeCurSaldo(){
    saldo + addsaldo
  }
}