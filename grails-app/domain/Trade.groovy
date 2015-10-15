class Trade {
  static mapping = {
    version false
  }
  static constraints = {
    debtdate(nullable:true)
    enddate(nullable:true)
  }
  private enum Historyfields {
    TRADECAT_ID, ANUMBER, ADATE, ENDDATE, COMMENT, SUMMA, PAYTYPE, RESPONSIBLE
  }

  Integer id
  Integer tradetype
  Integer tradesort
  Integer tradecat_id
  Integer client
  Integer supplier
  String anumber
  Date adate
  Date inputdate = new Date()
  Date enddate
  String description
  Integer modstatus
  String comment
  Integer summa
  Integer paytype
  Integer debt = 0
  Date debtdate
  Integer space_id = 0
  Long responsible

  def transient admin_id

  def afterInsert(){
    new Tradehist(trade_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Tradehist(trade_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  static def getResponsibles(){
    Trade.list().collect{it.responsible}.unique().inject([:]){map, id -> map[id]=User.get(id)?.name;map}
  }

  Trade setData(_request){
    client = Company.findByNameOrInn(_request.client,_request.client)?.id
    supplier = Company.findByNameOrInn(_request.supplier,_request.supplier)?.id
    tradetype = _request.tradetype?:0
    tradesort = _request.tradesort?:0
    tradecat_id = _request.tradecat_id
    anumber = _request.anumber
    adate = _request.adate
    enddate = _request.enddate
    description = _request.description?:''
    comment = _request.comment?:''
    summa = _request.summa
    paytype = _request.paytype?:0
    space_id = tradecat_id!=7?0:_request.space_id?:0
    responsible = _request.responsible?:0l
    this
  }

  Trade updateModstatus(){
    modstatus = ((enddate?:new Date())<new Date()-1)?0:1
    this
  }

  Trade csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Boolean isHaveDirty (){ return Trade.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

}