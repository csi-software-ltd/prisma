class Agentagr {
  static mapping = {
    version false
  }
  Integer id
  String name
  Integer client_id
  String bank_id
  Date inputdate = new Date()
  Integer modstatus = 1

  def beforeDelete(){
    Agentagr.withNewSession{
      Agentagrbank.findAllByAgentagr_id(id).each{
        it.delete(flush:true)
      }
    }
  }

  String toString(){
    "$name${!modstatus?' (архивный)':''}"
  }

  Agentagr setData(_request){
    name = _request.name
    if(!Agentkredit.findByAgentagr_id(id?:0)){
      client_id = _request.client_id
      bank_id = _request.bank_id
    }
    this
  }

}