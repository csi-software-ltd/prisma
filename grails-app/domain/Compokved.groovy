class Compokved {
  static mapping = {
    version false
  }
  static constraints = {
    comments(nullable:true)
  }
  Integer id
  String okved_id
  Integer company_id
  Integer is_main
  Integer modstatus = 1
  Date moddate = new Date()
  String comments = ''

  Compokved csiRawSetMain(iMain){
		is_main = iMain?:0
    modstatus = is_main?1:modstatus
		this
  }

  Compokved csiSetMain(){
		Compokved.findByCompany_idAndIs_main(company_id,1)?.csiRawSetMain(0).save(failOnError:true)
		csiRawSetMain(1)
  }

  Compokved csiSetData(_request){
    if (!is_main) modstatus = _request.modstatus?:0
    moddate = _request.moddate
    comments = _request.comments?:''
    this
  }

}
