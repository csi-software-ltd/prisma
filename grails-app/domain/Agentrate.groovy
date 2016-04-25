class Agentrate {
  static mapping = { version false }

  Integer id
  Integer agentkredit_id
  Integer agent_id
  Float rate
  Integer is_sub = 0
  Integer subtype = 0
  Integer is_display = 0

  def beforeDelete(){
    Agentrateforperiods.withNewSession{ Agentrateforperiods.findAllByAgentkredit_idAndAgent_id(agentkredit_id,agent_id).each{ it.delete(flush:true) } }
  }

	Agentrate setData(_request){
		rate = _request."rate_$id"?_request."rate_$id".toFloat():0f
    is_display = is_sub?0:_request."is_display_$id"?:0
		this
	}

}