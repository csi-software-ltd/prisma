class Bankhistory {
  static mapping = {    
    version false
  }

  static constraints = {  
    stopdate(nullable:true)
    is_license(nullable:true)    
  }

  Integer id
  String bank_id = ''
  Date moddate
  String name = '' 
  String coraccount = ''  
  Integer is_license
  Date stopdate
  String tel = ''
  Long admin_id 

  String toString(){
    "$id - $name"
  } 

  Bankhistory csiSetBankhistory(hsInrequest,lId){    
    bank_id = hsInrequest?.bank_id?:0
    moddate=new Date()
    name = hsInrequest?.name?:'' 
    coraccount = hsInrequest?.coraccount?:''
    is_license = hsInrequest?.is_license?:0
    stopdate = Tools.getDate(hsInrequest?.stopdate)
    tel = hsInrequest?.tel?:''
    admin_id = lId
    
    this
  }  
}