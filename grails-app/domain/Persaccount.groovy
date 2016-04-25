class Persaccount {
  static mapping = { version false }

  Long id
  Long pers_id
  String bank_id
	String nomer
	String paccount
	String validmonth
  String validyear
  String pin
  Integer is_main = 0
  Integer modstatus
  Date inputdate
  Date moddate
  Long admin_id

  def csiSetPersaccount(hsInrequest,bNew,lUser){
    moddate=new Date()
    admin_id=lUser

    pers_id=hsInrequest.pers_id?:0
    bank_id=hsInrequest.bank_id?:''
    nomer=hsInrequest.nomer?:''
    paccount=hsInrequest.paccount?.replace('.','')
    validmonth=hsInrequest.validmonth?:''
    validyear=hsInrequest.validyear?:''
    modstatus=hsInrequest.modstatus?:0

    if(bNew){
      inputdate=new Date()

      if(Pers.get(pers_id).perstype==Pers.PERSTYPE_SOTRUDNIK)
        is_main=1
      else if(Pers.get(pers_id).perstype==Pers.PERSTYPE_DIRECTOR && !Persaccount.findByPers_idAndIs_mainAndModstatus(pers_id,1,1))
        is_main=1
    }
    this
  }

  Persaccount csiSetPIN(sPin){
		pin = sPin?:''
		this
  }

  Persaccount csiSetMainVal(iMain){
		is_main = iMain?:0
		this
  }

  Persaccount csiSetMain(){
    try {
      if(Persaccount.findByPers_idAndIs_mainAndModstatus(pers_id,1,1))
        Persaccount.findByPers_idAndIs_mainAndModstatus(pers_id,1,1)?.csiSetMainVal(0).save(flush:true,failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Persaccount csiSetMain\n"+e.toString())
    }
		csiSetMainVal(1)
    this
  }

  Persaccount csiSetModstatus(iModstatus){
		modstatus = iModstatus?:0
		this
  }

}