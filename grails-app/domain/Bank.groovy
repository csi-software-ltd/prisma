class Bank {
  def searchService
  static mapping = {
    id column: 'id', generator: 'assigned'
    sort "id"
    version false
  }

  static constraints = {  
    stopdate(nullable:true)
  }

  String id
  String address = ''
  String name
  String shortname
  String coraccount = ''
  String city = ''
  Integer is_license
  Integer is_sanation = 0
  Integer is_local = 0
  Integer is_foreign = 0
  Date stopdate
  String tel = ''
  String contactinfo = ''
  String operinfo = ''
  String techinfo = ''
  String prevnameinfo = ''
  String comment = ''
  Integer is_request = 0
  Integer is_anketa = 0
  Integer rko_rate = 0
  Integer open_rate = 0
  Integer ibankopen_rate = 0
  Integer ibankserv_rate = 0
  Integer plat_rate = 0
  Integer platreturn_rate = 0
  Integer race_rate = 0
  Integer income_rate = 0
  Integer urgent_rate = 0
  Integer besp_rate = 0
  Integer spravka_rate = 0
  Integer addline_rate = 0
  Integer vypiska_rate = 0
  Integer ibankterm = 0

  String toString(){
    "$id - $name ${!is_license?'(Отозвана лицензия)':''}"
  }

  def csiSelectBanks(sId,sName,sCity,iIsLicense,isMy,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="* , bank.id AS id"
    hsSql.from='bank'+
                ((isMy)?', bankaccount':'')
    hsSql.where="1=1"+
                ((sId!='')?' AND bank.id like concat(:bank_id,"%")':'')+
                ((sName!='')?' AND bank.name like concat("%",:bank_name,"%")':'')+
                ((sCity!='')?' AND bank.city like concat("%",:city,"%")':'')+
                ((iIsLicense)?' AND is_license=0':'')+
                ((isMy)?' AND bankaccount.bank_id=bank.id':'')
    hsSql.order="bank.is_local desc, bank.name asc"
    hsSql.group=isMy?"bank.id":null

    if(sId!='')
      hsString['bank_id']=sId
    if(sName!='')
      hsString['bank_name']=sName
    if(sCity!='')
      hsString['city']=sCity

    def hsRes=searchService.fetchDataByPages(hsSql,null,null,null,hsString,
      null,null,iMax,iOffset,isMy?'*':'id',true,Bank.class)
  }
  
  def csiSetBank(hsInrequest){
    if(!hsInrequest.is_edit){
      id=hsInrequest?.id
      address=hsInrequest?.address?:''      
      city=hsInrequest?.city?:''     
      rko_rate = hsInrequest?.rko_rate?:0
      open_rate = hsInrequest?.open_rate?:0
      ibankopen_rate = hsInrequest?.ibankopen_rate?:0
      ibankserv_rate = hsInrequest?.ibankserv_rate?:0
      plat_rate = hsInrequest?.plat_rate?:0
      platreturn_rate = hsInrequest?.platreturn_rate?:0
      race_rate = hsInrequest?.race_rate?:0
      income_rate = hsInrequest?.income_rate?:0
      urgent_rate = hsInrequest?.urgent_rate?:0
      besp_rate = hsInrequest?.besp_rate?:0
      spravka_rate = hsInrequest?.spravka_rate?:0
      addline_rate = hsInrequest?.addline_rate?:0
      vypiska_rate = hsInrequest?.vypiska_rate?:0    
      is_local = hsInrequest?.is_local?:0
    }
    name=hsInrequest?.name?:''
    shortname=hsInrequest?.shortname?:''
    coraccount=hsInrequest?.coraccount?:''
    is_foreign = hsInrequest?.is_foreign?:0 
    is_license=hsInrequest?.is_license?:0
    stopdate=Tools.getDate(hsInrequest?.stopdate?:'')
    is_sanation=hsInrequest?.is_sanation?:0
    tel=hsInrequest?.tel?:''
    ibankterm = hsInrequest?.ibankterm?:0
    contactinfo=hsInrequest?.contactinfo?:''
    operinfo=hsInrequest?.operinfo?:''
    techinfo=hsInrequest?.techinfo?:''
    prevnameinfo=hsInrequest?.prevnameinfo?:''
    comment=hsInrequest?.comment?:''
    
    this
  } 
  
  def csiSetCSVBank(lsData){
    id=lsData[0]
    name=lsData[1]    
    if(lsData.size()>2)
      coraccount=lsData[2] 
    if(lsData.size()>3)  
      city=lsData[3]    
    
    if(lsData.size()>4)  
      stopdate=Tools.getDateShort(lsData[4])
      
    if(stopdate)
      is_license=0
    else
      is_license=1
      
    this
  }
  
  def csiSetCSVBankFromBNKSEEK(lsData){
    id=lsData[12]
    name=lsData[10]      
    coraccount=lsData[23]            
    is_license=1   
    city=lsData[7] 
    stopdate=null

    if(lsData[1].trim()=='ОТЗВ'){
      is_license=0
      stopdate=Tools.getDateShort(lsData[25])
    }        
      
    this
  }
  
  def csiSetCSVBankFromBNKDEL(lsData){
    id=lsData[12]
    name=lsData[10]      
    //stopdate=Tools.getDateShort(lsData[21]) //это дата удаления     
    coraccount=lsData[23]
    
    //if(stopdate)    
      is_license=0       
      
    this
  }      
}
