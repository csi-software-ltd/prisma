class Agentrate {
  static mapping = {
    version false
  }

  Integer id
  Integer agentkredit_id
  Integer agent_id
  Float rate
  Integer is_sub = 0
  Integer subtype = 0
  Integer is_display = 0

	Agentrate setData(_request){
		rate = _request."rate_$id"?_request."rate_$id".toFloat():0f
    subtype = _request."subtype_$id"?:0
    is_display = is_sub?0:_request."is_display_$id"?:0
		this
	}

}