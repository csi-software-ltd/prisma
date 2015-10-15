class Holding {
  static mapping = {
    version false
  }

  Integer id
  String name
  Long cashsaldo
////////////////////////////////////

	Holding changeSaldo(lSaldo){
		cashsaldo += lSaldo
		this
	}

}