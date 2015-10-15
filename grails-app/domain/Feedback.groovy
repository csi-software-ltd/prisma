class Feedback {
  static mapping = { version false }
  static constraints = {
    adate(nullable:true)
  }

  Integer id
  Long user_id
  Date qdate = new Date()
  String qtext
  Integer feedbacktype_id
  Date adate = new Date()
  String atext = ''
  String fullstory = ''
  Long file_id = 0
  Integer modstatus = 0
  Integer is_readanswer = 0

  def beforeInsert(){
    fullstory = "<i>${String.format('%td.%<tm.%<tY %<tT',qdate)} ${Pers.get(User.get(user_id)?.pers_id?:0).shortname}:</i>\r<br/><br/>"+qtext
  }

  def beforeUpdate(){
    if (isDirty('atext')) is_readanswer = 0
  }

  Feedback setData(_request,_isSuper){
    if(_isSuper){
      atext = _request.atext.replace('\n','<br/>')
      adate = new Date()
      fullstory = "<i>${String.format('%td.%<tm.%<tY %<tT',adate)} Администратор:</i>\r<br/><br/>"+atext+'\r<br/><br/>'+fullstory
    } else {
      atext = ''
      adate = new Date()
      fullstory = "<i>${String.format('%td.%<tm.%<tY %<tT',new Date())} ${Pers.get(User.get(user_id)?.pers_id?:0).shortname}:</i>\r<br/><br/>"+_request.qtext.replace('\n','<br/>')+'\r<br/><br/>'+fullstory
    }
    this
  }

  Feedback csiSetFileId(iFileId){
    file_id = iFileId?:file_id
    this
  }

  Feedback computeStatus(){
  	modstatus = modstatus==2?2:atext?1:0
  	this
  }

  Feedback readAnswer(){
  	is_readanswer = 1
  	this
  }

  Feedback movetofaq(Integer _status){
    modstatus = _status?:modstatus
    this
  }

}