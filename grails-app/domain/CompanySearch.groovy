class CompanySearch {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

///////////////company///////////////////
  Integer id
  String name
  String legalname
  Integer is_holding
  Integer visualgroup_id
  String inn
  String kpp
  String okvedmain
  String gd
  String city
  String ogrn
  String oktmo
  String okato
  String okpo
  Date opendate
  Date inputdate
  String legaladr
  String postadr
  String tel
  String smstel
  String email
  String taxinspection_id
  Integer taxoption_id
  Integer form_id
  Integer is_subarenda
  Integer is_sublizing
  String contactinfo
  String comment
  Integer modstatus
  Integer activitystatus_id
  Integer is_dirchange
  String color
  Integer colorfill
  Date moddate

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectCompanies(iId,sName,sBankname,sOkved,iProject,iTaxoption,iBankaccount,iHolding,sGd,sTaxinspectionDistrict,lResponsibleId,iIsLicense,iIsWww,sColor,iFill,iVision,hsAddParams,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='company left join compokved on (company.id=compokved.company_id) left join (bankaccount join bank on(bankaccount.bank_id=bank.id and bankaccount.typeaccount_id = 1)) on (company.id=bankaccount.company_id) left join cproject on (cproject.company_id=company.id)'
    hsSql.where="1=1"+
                ((iId>0)?' AND company.id =:company_id':'')+
                ((sName!='')?' AND (company.name like concat("%",:company_name,"%") OR company.inn like concat(:company_name,"%"))':'')+
                ((sBankname!='')?' AND bank.name like concat("%",:bankname,"%")':'')+
                ((sGd!='')?' AND company.gd like concat("%",:gd,"%")':'')+
                ((sOkved!='')?' AND compokved.okved_id =:okved':'')+
                ((iProject>0)?' AND cproject.project_id =:project_id':'')+
                ((iTaxoption>0)?' AND company.taxoption_id =:taxoption_id':'')+
                ((iBankaccount==1)?' AND bankaccount.modstatus = 1':(iBankaccount==0)?' AND ifnull(bankaccount.modstatus,100) = 100':'')+
                (iHolding>0?' AND company.is_holding = 1'+(iVision>0?' AND company.visualgroup_id=:visualgroup_id':''):' AND company.is_holding = 0'+(iVision>0?' OR company.visualgroup_id!=:visualgroup_id':''))+
                ((sTaxinspectionDistrict!='')?' AND (select district from taxinspection where id=company.taxinspection_id) like concat("%",:district,"%")':'')+
                ((lResponsibleId>0)?' AND (company.responsible1 =:responsible OR company.responsible2 =:responsible)':'')+
                ((iIsLicense>0)?' AND (select count(*) from complicense where company_id=company.id and modstatus=1) > 0':'')+
                (iIsWww==1?" AND company.www!=''":iIsWww==0?" and company.www=''":'')+
                ((sColor!='')?' AND company.color like concat("%",:color,"%")':'')+
                (iFill==1?' AND company.colorfill = 1':iFill==0?' AND company.colorfill = 0':'')+
                (hsAddParams?.cgroup_id>-100?' AND company.cgroup_id =:cgroup_id':'')+
                (hsAddParams?.visualgroup_id>-100?' AND company.visualgroup_id =:visualgroup_id':'')+
                (hsAddParams?.is_inactive==1?' AND company.activitystatus_id in (select id from activitystatus where is_close=1)':'')
    hsSql.order="company.name asc"
    hsSql.group="company.id"

    if(iId>0)
      hsLong['company_id']=iId
    if(iProject>0)
      hsLong['project_id']=iProject
    if(iTaxoption>0)
      hsLong['taxoption_id']=iTaxoption
    if(lResponsibleId>0)
      hsLong['responsible']=lResponsibleId
    if(iVision>0)
      hsLong['visualgroup_id']=iVision
    if(sName!='')
      hsString['company_name']=sName
    if(sBankname!='')
      hsString['bankname']=sBankname
    if(sOkved!='')
      hsString['okved']=sOkved
    if(sGd!='')
      hsString['gd']=sGd
    if(sTaxinspectionDistrict!='')
      hsString['district']=sTaxinspectionDistrict
    if(sColor!='')
      hsString['color']=sColor
    if(hsAddParams?.cgroup_id>-100)
      hsLong['cgroup_id']=hsAddParams.cgroup_id
    if(hsAddParams?.visualgroup_id>-100)
      hsLong['visualgroup_id']=hsAddParams.visualgroup_id

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'*',true,CompanySearch.class)
  }

}
