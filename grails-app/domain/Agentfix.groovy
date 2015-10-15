class Agentfix {
  static mapping = {
    version false
  }

  Integer id
  Integer agentagr_id
  Integer agent_id
  Date inputdate = new Date()
  Date paydate
  BigDecimal summa

  Agentfix setData(_request){
    agent_id = _request.agent_id
    paydate = _request.paydate
    summa = _request.summa
    this
  }

}