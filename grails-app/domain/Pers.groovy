class Pers {  
  def searchService  
  
  static constraints = {  
    inputdate(nullable:true)
    birthdate(nullable:true)
  }  
  static mapping = {
    version false    
  }

  public static final PERSTYPE_SOTRUDNIK=1
  public static final PERSTYPE_DIRECTOR=2
  public static final PERSTYPE_SPECIALIST=3

  Long id  
  String fullname  
	String shortname
  String inn
  String snils = ''
	String snilsdpf = ''
	String passport
  String passdate
  String passorg
  String propiska = ''
  String citizen = ''
  String education = ''
  String birthcity = ''
  String kodpodr = ''
  Long actsalary = 0l
  Date inputdate
  Date birthdate  
  Integer perstype
  Long cassadebt = 0
  Integer is_fixactsalary = 0
  Integer is_haveagr = 0
  Integer is_salarydebt = 0
  Long emplpers_id = 0

  def afterInsert(){
    snils = Tools.generateSnils(id,perstype)
    save()
    if (perstype==2) new User().csiSetUser([pers_id:id,login:id,name:shortname,password:Tools.generatePassword(),department_id:Department.findByIs_tehdir(1)?.id],true).save()
  }

  def csiSetPers(hsInrequest,bNew){
    if(bNew){
      inputdate=new Date()
      perstype=hsInrequest.perstype?:0
      if(perstype!=2)
        is_fixactsalary=1
    }

    if(perstype==2)
      is_fixactsalary=hsInrequest.is_fixactsalary?:0

    fullname=hsInrequest.fullname?:''
    shortname=hsInrequest.shortname?:''
    inn=hsInrequest.inn?:''
    passport=hsInrequest.passport?:''
    passdate=hsInrequest.passdate?:''
    passorg=hsInrequest.passorg?:''
    birthdate=Tools.getDate(hsInrequest.birthdate)
    propiska=hsInrequest.propiska?:''
    citizen=hsInrequest.citizen?:''
    education=hsInrequest.education?:''
    birthcity=hsInrequest.birthcity?:''
    kodpodr=hsInrequest.kodpodr?:''
    snilsdpf = hsInrequest.snilsdpf?:''

    this
  }

  Pers csiSetIsSalarydebt(iStatus){
    is_salarydebt = iStatus?:0
    this
  }

  Pers updateActsalary(_salary){
    actsalary = _salary?:0
    this
  }

  Pers csiSetIsHaveAgr(iStatus){
    is_haveagr = iStatus?:0
    this
  }

  def csiGetPersList(lUser_id,sShortname,sSnils,bIs_sys_user,iPerstype,iMax,iOffset){  
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:] 
    def hsInt=[:]    
    def hsString=[:]

    hsSql.select="*, pers.id as id"
    hsSql.from="pers"+
      ((bIs_sys_user)?" , user":"")      
    hsSql.where="1=1"+
      ((lUser_id>0)?' AND pers.id=:lUser_id':'')+  
      ((sShortname!='')?' AND shortname like CONCAT(:sShortname,"%")':'')+                
      ((sSnils!='')?' AND snils like CONCAT(:sSnils,"%")':'')+
      ((bIs_sys_user>0)?' AND pers.id = user.pers_id':'')+
      ((iPerstype>0)?" AND pers.perstype=:iPerstype":"")      
                
    hsSql.order="shortname asc"

    if(bIs_sys_user>0)
      hsSql.group="pers.id"    
    
    if(lUser_id>0)
      hsLong['lUser_id']=lUser_id
    if(iPerstype>0)
      hsInt['iPerstype']=iPerstype
    if(sSnils!='')
      hsString['sSnils']=sSnils
    if(sShortname!='')  
      hsString['sShortname']=sShortname    

    return searchService.fetchDataByPages(hsSql,null,hsLong,hsInt,hsString,
      null,null,iMax,iOffset,(bIs_sys_user>0)?'*':'pers.id',true,Pers.class)
  }
  
  def csiGetPersNoUser(sShortname){  
    def hsSql=[select:'',from:'',where:'',order:''] 
    def hsString=[:]    

    hsSql.select="*, pers.id as id"
    hsSql.from="pers LEFT OUTER JOIN user on pers.id=user.pers_id"
    hsSql.where="user.id IS NULL AND (perstype=1 OR perstype=2)"+
      (sShortname?" AND pers.shortname LIKE concat(:sShortname,'%')":"")
    
    hsSql.order="shortname asc"
    
    if(sShortname!='')  
      hsString['sShortname']=sShortname    

    return searchService.fetchData(hsSql,null,null,hsString,null,Pers.class,10)       
  }

  def csiFindBankdirnames(sDirname){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='pers join bankaccount on (pers.id=bankaccount.pers_id)'
    hsSql.where="1=1"+
                ((sDirname!='')?' and pers.fullname like concat("%",:pname,"%")':'')
    hsSql.group="pers.id"
    hsSql.order="pers.fullname asc"

    if(sDirname!='')
      hsString['pname']=sDirname

    searchService.fetchData(hsSql,null,null,hsString,null,Pers.class,10)
  }
}